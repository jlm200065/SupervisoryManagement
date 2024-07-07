package org.example.system.combiner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Edge;
import org.camunda.bpm.model.bpmn.instance.di.Shape;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * @Author:ljl
 * @Date:2023/10/25
 * @VERSION:1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantHolder {
    private double width;
    private double height;
    private Participant participant;
    private BpmnInfoHolder bpmnInfoHolder;
    private double initX;
    private double initY;
    private double finalX;
    private double finalY;

    private int positionIndex;


    public ParticipantHolder(Participant participant, BpmnInfoHolder bpmnInfoHolder) {
        this.participant = participant;
        this.bpmnInfoHolder = bpmnInfoHolder;
    }

    public void calculateSize() {
        if (participant == null || bpmnInfoHolder == null) {
            throw new RuntimeException("Not.initialize");
        }
        BpmnModelInstance bpmnModelInstance = bpmnInfoHolder.getBpmnModelInstance();
        String id = participant.getId();
        Collection<Shape> bpmnShapes = bpmnModelInstance.getModelElementsByType(Shape.class);
        Optional<Shape> target = bpmnShapes.stream().filter(shape -> id.equals(shape.getAttributeValue("bpmnElement"))).findAny();
        Shape shape = target.orElseThrow(() -> new RuntimeException("Shape.Not.Found"));
        width = shape.getBounds().getWidth();
        height = shape.getBounds().getHeight();
        initX = shape.getBounds().getX();
        initY = shape.getBounds().getY();
    }

    public void adjustSize(double finalX, double finalY) {
        this.finalX = finalX;
        this.finalY = finalY;
        double diffX = finalX - initX, diffY = finalY - initY;
        String id = participant.getProcess().getId();
        Set<String> ids = getProcessIds(id);
        ids.add(participant.getId());
        BpmnModelInstance bpmnModelInstance = bpmnInfoHolder.getBpmnModelInstance();
        bpmnModelInstance.getModelElementsByType(Edge.class)
                .stream()
                .filter(edge -> ids.contains(edge.getAttributeValue("bpmnElement")))
                .forEach(edge -> {
                    edge.getWaypoints().stream().forEach(waypoint -> {
                        waypoint.setX(waypoint.getX() + diffX);
                        waypoint.setY(waypoint.getY() + diffY);
                    });
                });
        bpmnModelInstance.getModelElementsByType(Shape.class)
                .stream()
                .filter(shape -> ids.contains(shape.getAttributeValue("bpmnElement")))
                .forEach(shape -> {
                    Bounds bounds = shape.getBounds();
                    bounds.setX(bounds.getX() + diffX);
                    bounds.setY(bounds.getY() + diffY);
                });
        bpmnModelInstance.getModelElementsByType(MessageFlow.class)
                .stream()
                .filter(messageFlow -> !Constant.environment.equals(messageFlow.getTarget().getId())
                        && !Constant.environment.equals(messageFlow.getTarget().getId()))
                .forEach(messageFlow -> {
                    Edge messageFlowEdge = bpmnModelInstance.getModelElementsByType(Edge.class)
                            .stream()
                            .filter(edge -> messageFlow.getId().equals(edge.getAttributeValue("bpmnElement")))
                            .findAny().get();
                    List<Waypoint> waypoints = messageFlowEdge.getWaypoints().stream().collect(Collectors.toList());
                    if (waypoints == null || waypoints.size() != 2) throw new RuntimeException("WayPoints.Size.Error");
                    Waypoint waypoint = null;
                    if (ids.contains(messageFlow.getSource().getId())) {
                        waypoint = waypoints.get(0);
                    } else if (ids.contains(messageFlow.getTarget().getId())) {
                        waypoint = waypoints.get(1);
                    }
                    if (waypoint != null) {
                        waypoint.setX(waypoint.getX() + diffX);
                        waypoint.setY(waypoint.getY() + diffY);
                    }
                });
    }

    /**
     * 根据participant id获取process下所有元素的id
     * @param id
     * @return
     */
    private Set<String> getProcessIds(String id) {
        BpmnModelInstance bpmnModelInstance = bpmnInfoHolder.getBpmnModelInstance();
        Process p = bpmnModelInstance.getModelElementsByType(Process.class)
                .stream()
                .filter(process -> id.equals(process.getId()))
                .findAny()
                .get();
        Set<String> ids = p.getChildElementsByType(BaseElement.class)
                .stream()
                .map(baseElement -> baseElement.getId())
                .collect(Collectors.toSet());
        return ids;
    }
}
