package org.example.system.combiner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Collaboration;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.di.Edge;
import org.camunda.bpm.model.bpmn.instance.di.Label;
import org.camunda.bpm.model.bpmn.instance.di.Shape;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
public class BpmnInfoHolder {
    private BpmnModelInstance bpmnModelInstance;
    private Document document;

    public List<ParticipantHolder> obtainParticipantList() {
        Collaboration collaboration = getCollaboration();
        Collection<Participant> participants = collaboration.getParticipants();
        List<Participant> participantList = participants.stream().filter(participant -> !"Participant_Environment".equals(participant.getId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(participantList)) {
            throw new RuntimeException("Bpmn.Participant.Empty");
        }
        List<ParticipantHolder> participantHolders = participantList.stream().map(participant -> {
            ParticipantHolder participantHolder = new ParticipantHolder(participant, this);
            participantHolder.calculateSize();
            return participantHolder;
        }).collect(Collectors.toList());
        return participantHolders;
    }

    public List<MessageFlow> obtainMessageFlowList() {
        if (bpmnModelInstance == null) {
            throw new RuntimeException("ModelInstance.Null");
        }
        List<MessageFlow> messageFlowList = bpmnModelInstance.getModelElementsByType(MessageFlow.class)
                .stream()
                .filter(messageFlow -> Constant.environment.equals(messageFlow.getSource().getId())
                        || Constant.environment.equals(messageFlow.getTarget().getId()))
                .collect(Collectors.toList());
        messageFlowList.forEach(messageFlow -> {
            String id = messageFlow.getId();
            bpmnModelInstance.getModelElementsByType(Edge.class)
                    .stream()
                    .filter(edge -> id.equals(edge.getAttributeValue("bpmnElement")))
                    .forEach(edge -> edge.getParentElement().removeChildElement(edge));
            messageFlow.getParentElement().removeChildElement(messageFlow);
        });

        Set<String> ids = messageFlowList
                .stream()
                .map(messageFlow -> messageFlow.getId())
                .collect(Collectors.toSet());
        bpmnModelInstance.getModelElementsByType(Edge.class)
                .stream()
                .filter(edge -> ids.contains(edge.getAttributeValue("bpmnElement")))
                .forEach(edge -> edge.getParentElement().removeChildElement(edge));
        return messageFlowList;
    }

    /**
     * 将messageFlow中有拐点的全部转换为直连
     */
    public void preHandle() {
        if (bpmnModelInstance == null) {
            throw new RuntimeException("ModelInstance.Null");
        }
        Set<String> ids = bpmnModelInstance.getModelElementsByType(MessageFlow.class)
                .stream()
                .map(messageFlow -> messageFlow.getId())
                .collect(Collectors.toSet());
        List<Edge> edges = bpmnModelInstance.getModelElementsByType(Edge.class).stream().collect(Collectors.toList());
        edges.forEach(edge -> {
            edge.getChildElementsByType(Label.class).stream().forEach(label -> {
                label.getParentElement().removeChildElement(label);
            });
        });
        edges.stream()
            .filter(edge -> ids.contains(edge.getAttributeValue("bpmnElement")))
            .forEach(edge -> {
                List<Waypoint> waypoints = edge.getWaypoints().stream().collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(waypoints) && waypoints.size() > 2) {
                    for (int i = 0; i < waypoints.size(); i++) {
                        if (i != 0 && i != waypoints.size() - 1) {
                            edge.removeChildElement(waypoints.get(i));
                        }
                    }
                }
            });
        bpmnModelInstance.getModelElementsByType(Shape.class)
                .stream().forEach(shape -> {
                    shape.getChildElementsByType(Label.class)
                            .stream()
                            .forEach(label -> label.getParentElement().removeChildElement(label));
                });


    }

    private Collaboration getCollaboration() {
        if (bpmnModelInstance == null) {
            throw new RuntimeException("ModelInstance.Null");
        }
        Collection<Collaboration> collaborations = bpmnModelInstance.getModelElementsByType(Collaboration.class);
        Collaboration collaboration = null;
        Iterator<Collaboration> collaborationIterator = collaborations.iterator();
        while(collaborationIterator.hasNext()) {
            collaboration = collaborationIterator.next();
        }
        if (collaboration == null) {
            throw new RuntimeException("No.Collaboration");
        }
        return collaboration;
    }


    public void removeEnvironment() {
        if (bpmnModelInstance == null) {
            throw new RuntimeException("ModelInstance.Null");
        }
        bpmnModelInstance.getModelElementsByType(Participant.class)
                .stream()
                .filter(participant -> Constant.environment.equals(participant.getId()))
                .forEach(participant -> {
                    Process process = participant.getProcess();
                    process.getParentElement().removeChildElement(process);
                    participant.getParentElement().removeChildElement(participant);
                });
        bpmnModelInstance.getModelElementsByType(Shape.class)
                .stream()
                .filter(shape -> Constant.environment.equals(shape.getAttributeValue("bpmnElement")))
                .forEach(shape -> shape.getParentElement().removeChildElement(shape));
    }
}
