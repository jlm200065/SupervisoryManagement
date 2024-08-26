package org.example.system.combiner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Shape;
import org.camunda.bpm.model.xml.ModelInstance;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Author:ljl
 * @Date:2023/10/25
 * @VERSION:1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BpmnPanel {

    private double initX = 200;

    private double initY = 80;

    private double environmentWidth;

    private double environmentHeight = 200;

    private double space = 50;

    private List<BpmnInfoHolder> bpmnInfoHolders = new ArrayList<>();

    private List<ParticipantHolder> participantHolders = new ArrayList<>();

    private List<ParticipantHolder> sortedParticipantHolders = new ArrayList<>();

    private Map<String, List<MessageFlowInfo>> messageFlowMap = new HashMap<>();

    private List<MessageFlowInfo> messageFlowInfoList = new ArrayList<>();

    private DocumentBuilder documentBuilder;

    private Document document;

    private boolean drawEnvironmentFlag = true;

    private ModelInstance finalModel;

    {
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void addBpmnInfo(String bpmnContent) throws UnsupportedEncodingException {
        BpmnInfoHolder bpmnInfoHolder = new BpmnInfoHolder();
        try {
            Document document = documentBuilder.parse(new InputSource(new ByteArrayInputStream(bpmnContent.getBytes("UTF-8"))));
            bpmnInfoHolder.setDocument(document);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        // 获取对应的modelInstance
        BpmnModelInstance modelInstance = Bpmn.readModelFromStream(new ByteArrayInputStream(bpmnContent.getBytes("UTF-8")));
        bpmnInfoHolder.setBpmnModelInstance(modelInstance);
        // 预处理，将messageFlow中转折的线改为直连
        bpmnInfoHolder.preHandle();
        bpmnInfoHolders.add(bpmnInfoHolder);
        // 获取该Bpmn下所有的非environment participant
        List<ParticipantHolder> participantHolderList = bpmnInfoHolder.obtainParticipantList();
        participantHolders.addAll(participantHolderList);
        List<MessageFlow> messageFlowList = bpmnInfoHolder.obtainMessageFlowList();
        messageFlowList.forEach(messageFlow -> {
            String name = messageFlow.getName();
            if (!StringUtils.hasText(name)) return ;
            List<MessageFlowInfo> list = messageFlowMap.get(name);
            if (CollectionUtils.isEmpty(list)) {
                list = new ArrayList<>();
                MessageFlowInfo messageFlowInfo = new MessageFlowInfo(name, messageFlow.getSource().getId(), messageFlow.getTarget().getId());
                list.add(messageFlowInfo);
                messageFlowMap.put(name, list);
                return ;
            }
            boolean isMatched = false;
            Iterator<MessageFlowInfo> iterator = list.iterator();
            if (Constant.environment.equals(messageFlow.getSource().getId())) {
                while(iterator.hasNext()) {
                    MessageFlowInfo next = iterator.next();
                    if (Constant.environment.equals(next.getTargetRef())) {
                        isMatched = true;
                        MessageFlowInfo messageFlowInfo = new MessageFlowInfo();
                        messageFlowInfo.setName(name);
                        messageFlowInfo.setSourceRef(next.getSourceRef());
                        messageFlowInfo.setTargetRef(messageFlow.getTarget().getId());
                        messageFlowInfoList.add(messageFlowInfo);
                        iterator.remove();
                    }
                }
            } else if (Constant.environment.equals(messageFlow.getTarget().getId())) {
                while(iterator.hasNext()) {
                    MessageFlowInfo next = iterator.next();
                    if (Constant.environment.equals(next.getSourceRef())) {
                        isMatched = true;
                        MessageFlowInfo messageFlowInfo = new MessageFlowInfo();
                        messageFlowInfo.setName(name);
                        messageFlowInfo.setSourceRef(messageFlow.getSource().getId());
                        messageFlowInfo.setTargetRef(next.getTargetRef());
                        messageFlowInfoList.add(messageFlowInfo);
                        iterator.remove();
                    }
                }
            }
            if (!isMatched) {
                MessageFlowInfo messageFlowInfo = new MessageFlowInfo(name, messageFlow.getSource().getId(), messageFlow.getTarget().getId());
                list.add(messageFlowInfo);
            }
            if (CollectionUtils.isEmpty(list)) {
                messageFlowMap.remove(name);
            }
        });
        bpmnInfoHolder.removeEnvironment();
    }

    /**
     * 根据width生序排序
     */
    public void sortParticipantHoldersByWidth() {
        Collections.sort(participantHolders, (o1, o2) -> (int)(o1.getWidth() - o2.getWidth()));
    }

    public void initEnvironmentWidth() {
        if (CollectionUtils.isEmpty(participantHolders)) {
            throw new RuntimeException("ParticipantHolders.Empty");
        }
        int length = participantHolders.size();
        int l = 0, r = length - 1;
        if ((length & 1) == 1) {
            environmentWidth = participantHolders.get(length - 1).getWidth();
            --r;
        }
        while(l < r) {
            environmentWidth = Math.max(environmentWidth, space + (participantHolders.get(l++).getWidth() +
                    participantHolders.get(r--).getWidth()));
        }
    }

    public void confirmParticipantPositionAndSize() {
        if (CollectionUtils.isEmpty(participantHolders)) return ;
        int len = this.participantHolders.size();
        int l = 0, r = len - 1;
        if ((len & 1) == 1) {
            r -= 1;
        }
        double startY = initY + environmentHeight + space;
        while (l < r) {
            ParticipantHolder pl = participantHolders.get(l);
            ParticipantHolder pr = participantHolders.get(r);

            pl.adjustSize(initX, startY);
            sortedParticipantHolders.add(pl);
            pr.adjustSize(initX + space + pl.getWidth(), startY);
            sortedParticipantHolders.add(pr);
            ++l;
            --r;
            startY += space + Math.max(pl.getHeight(), pr.getHeight());
        }
        if ((len & 1) == 1) {
            ParticipantHolder participantHolder = participantHolders.get(len - 1);
            participantHolder.adjustSize(initX, startY);
            sortedParticipantHolders.add(participantHolder);
        }
    }

    private void updateShapeIds(Document document) {
        NodeList shapes = document.getElementsByTagName("bpmndi:BPMNShape");
        for (int i = 0; i < shapes.getLength(); i++) {
            Element shape = (Element) shapes.item(i);
            String oldId = shape.getAttribute("id");
            if (!oldId.isEmpty()) {
                String newId = generateUniqueId("Shape");
                shape.setAttribute("id", newId);
                updateReferences(document, oldId, newId);
            }
        }
    }

    private void updateReferences(Document document, String oldId, String newId) {
        NodeList elements = document.getElementsByTagName("*");
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            if (oldId.equals(element.getAttribute("sourceRef"))) {
                element.setAttribute("sourceRef", newId);
            }
            if (oldId.equals(element.getAttribute("targetRef"))) {
                element.setAttribute("targetRef", newId);
            }
            if (oldId.equals(element.getAttribute("bpmnElement"))) {
                element.setAttribute("bpmnElement", newId);
            }
        }
    }

    // Generate unique ID
    private String generateUniqueId(String prefix) {
        return prefix + "_" + RandomStringUtils.randomAlphanumeric(8);
    }

    private void cleanUnusedElements(Document document) {
        NodeList shapes = document.getElementsByTagName("bpmndi:BPMNShape");
        NodeList edges = document.getElementsByTagName("bpmndi:BPMNEdge");

        Set<String> usedElements = new HashSet<>();
        NodeList elements = document.getElementsByTagName("*");
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            if (element.hasAttribute("bpmnElement")) {
                usedElements.add(element.getAttribute("bpmnElement"));
            }
        }

        for (int i = 0; i < shapes.getLength(); i++) {
            Element shape = (Element) shapes.item(i);
            if (!usedElements.contains(shape.getAttribute("bpmnElement"))) {
                shape.getParentNode().removeChild(shape);
            }
        }

        for (int i = 0; i < edges.getLength(); i++) {
            Element edge = (Element) edges.item(i);
            if (!usedElements.contains(edge.getAttribute("bpmnElement"))) {
                edge.getParentNode().removeChild(edge);
            }
        }
    }

    public void drawParticipant() {
        drawEnvironmentFlag = messageFlowMap.size() > 0;
        String targetTemplate = "template.bpmn";
        if (this.drawEnvironmentFlag) {
            targetTemplate = "environment_template.bpmn";
        }
        ClassPathResource classPathResource = new ClassPathResource(targetTemplate);
        try {
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(classPathResource.getInputStream());

            if (drawEnvironmentFlag) {
                Shape shape = modelInstance.getModelElementsByType(Shape.class)
                        .stream()
                        .filter(s -> Constant.environment.equals(s.getAttributeValue("bpmnElement")))
                        .findAny()
                        .get();
                Bounds bound = shape.getBounds();
                bound.setX(initX);
                bound.setY(initY);
                bound.setWidth(environmentWidth);
                bound.setHeight(environmentHeight);



                // Generate fake services based on unmatched message flows
                for (int i = 0; i < messageFlowMap.size(); i++) {
                    ParallelGateway gateway = modelInstance.getModelElementById("Gateway_0cyeibu");
                    AbstractFlowNodeBuilder<?, ?> builder = gateway.builder();
                    builder.serviceTask("fakeService" + (i + 1))
                            .name("Fake Service " + (i + 1))
                            .connectTo("Gateway_1boer0t");
                }
            }
            String bpmn = Bpmn.convertToString(modelInstance);
            document = documentBuilder.parse(new ByteArrayInputStream(bpmn.getBytes("UTF-8")));

            for (BpmnInfoHolder bpmnInfoHolder : bpmnInfoHolders) {
                BpmnModelInstance bpmnModelInstance = bpmnInfoHolder.getBpmnModelInstance();
                Document subDoc = documentBuilder.parse(new ByteArrayInputStream(Bpmn.convertToString(bpmnModelInstance).getBytes("UTF-8")));

                updateShapeIds(subDoc);

                Node definitionsTarget = document.getElementsByTagName("bpmn:definitions").item(0);
                Node planeTarget = document.getElementsByTagName("bpmndi:BPMNPlane").item(0);
                Node diagramTarget = document.getElementsByTagName("bpmndi:BPMNDiagram").item(0);
                Node collaborationTarget = document.getElementsByTagName("bpmn:collaboration").item(0);
                NodeList childNodes = subDoc.getElementsByTagName("bpmn:collaboration").item(0).getChildNodes();

                for (int i = 0; i < childNodes.getLength(); i++) {
                    if (childNodes.item(i).getNodeName().contains("documentation")) continue;
                    Node node = document.importNode(childNodes.item(i), true);
                    if (node instanceof Element) {
                        Element element = (Element) node;
                        String oldId = element.getAttribute("id");
                        if (!oldId.isEmpty()) {
                            String newId = generateUniqueId(oldId);
                            element.setAttribute("id", newId);
                            updateReferences(subDoc, oldId, newId);
                            cleanUnusedElements(document);
                        }
                    }
                    collaborationTarget.appendChild(node);
                }

                NodeList subProcessList = subDoc.getElementsByTagName("bpmn:process");

                Node subPlane = subDoc.getElementsByTagName("bpmndi:BPMNPlane").item(0);
                for (int i = 0; i < subPlane.getChildNodes().getLength(); i++) {
                    Node node = document.importNode(subPlane.getChildNodes().item(i), true);
                    planeTarget.appendChild(node);
                }

                for (int i = 0; i < subProcessList.getLength(); i++) {
                    Node node = document.importNode(subProcessList.item(i), true);
                    definitionsTarget.insertBefore(node, diagramTarget);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DrawParticipant.Error");
        }
        cleanUnusedShapes();
    }

    private void cleanUnusedShapes() {
        NodeList shapes = document.getElementsByTagName("bpmndi:BPMNShape");
        NodeList edges = document.getElementsByTagName("bpmndi:BPMNEdge");

        Set<String> usedElements = new HashSet<>();
        NodeList elements = document.getElementsByTagName("*");
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            if (element.hasAttribute("bpmnElement")) {
                usedElements.add(element.getAttribute("bpmnElement"));
            }
        }

        for (int i = 0; i < shapes.getLength(); i++) {
            Element shape = (Element) shapes.item(i);
            if (!usedElements.contains(shape.getAttribute("bpmnElement"))) {
                shape.getParentNode().removeChild(shape);
            }
        }

        for (int i = 0; i < edges.getLength(); i++) {
            Element edge = (Element) edges.item(i);
            if (!usedElements.contains(edge.getAttribute("bpmnElement"))) {
                edge.getParentNode().removeChild(edge);
            }
        }
    }

    public void drawMessageFlow() {
        if (document == null) throw new RuntimeException("document.Null");
        Node collaboration = document.getElementsByTagName("bpmn:collaboration").item(0);
        Node plane = document.getElementsByTagName("bpmndi:BPMNPlane").item(0);
        NodeList shapes = document.getElementsByTagName("bpmndi:BPMNShape");

        for (MessageFlowInfo messageFlowInfo : messageFlowInfoList) {
            Element messageFlow = document.createElement("bpmn:messageFlow");
            String id = generateUniqueId("Flow");
            messageFlow.setAttribute("id", id);
            messageFlow.setAttribute("name", messageFlowInfo.getName());
            messageFlow.setAttribute("sourceRef", messageFlowInfo.getSourceRef());
            messageFlow.setAttribute("targetRef", messageFlowInfo.getTargetRef());
            collaboration.appendChild(messageFlow);

            int flag = 2;
            Point pointSource = new Point();
            Point pointTarget = new Point();
            for (int i = 0; i < shapes.getLength() && flag > 0; i++) {
                Element item = (Element) shapes.item(i);
                String bpmnElement = item.getAttribute("bpmnElement");
                if (bpmnElement != null) {
                    if (messageFlowInfo.getSourceRef().equals(bpmnElement)) {
                        flag -= 1;
                        Element bound = (Element) item.getElementsByTagName("dc:Bounds").item(0);
                        double x = Double.parseDouble(bound.getAttribute("x"));
                        double y = Double.parseDouble(bound.getAttribute("y"));
                        double width = Double.parseDouble(bound.getAttribute("width"));
                        pointSource.setX(x + width / 2);
                        pointSource.setY(y);
                    } else if (messageFlowInfo.getTargetRef().equals(bpmnElement)) {
                        flag -= 1;
                        Element bound = (Element) item.getElementsByTagName("dc:Bounds").item(0);
                        double x = Double.parseDouble(bound.getAttribute("x"));
                        double y = Double.parseDouble(bound.getAttribute("y"));
                        double width = Double.parseDouble(bound.getAttribute("width"));
                        pointTarget.setX(x + width / 2);
                        pointTarget.setY(y);
                    }
                }
            }
            if (flag == 0) {
                Element flowElement = document.createElement("bpmndi:BPMNEdge");
                flowElement.setAttribute("id", id + "_di");
                flowElement.setAttribute("bpmnElement", id);
                Element startPoint = document.createElement("di:waypoint");
                startPoint.setAttribute("x", String.valueOf(pointSource.getX()));
                startPoint.setAttribute("y", String.valueOf(pointSource.getY()));
                flowElement.appendChild(startPoint);
                Element endPoint = document.createElement("di:waypoint");
                endPoint.setAttribute("x", String.valueOf(pointTarget.getX()));
                endPoint.setAttribute("y", String.valueOf(pointTarget.getY()));
                flowElement.appendChild(endPoint);
                plane.appendChild(flowElement);
            }
        }

        if (drawEnvironmentFlag) {
            int fakeServiceCount = 1;  // 增加fakeService计数器
            for (Map.Entry<String, List<MessageFlowInfo>> listEntry : messageFlowMap.entrySet()) {
                List<MessageFlowInfo> list = listEntry.getValue();
                for (MessageFlowInfo messageFlowInfo : list) {
                    Element messageFlow = document.createElement("bpmn:messageFlow");
                    String id = generateUniqueId("Flow");
                    messageFlow.setAttribute("id", id);
                    messageFlow.setAttribute("name", messageFlowInfo.getName());
                    // 连接到fakeService而不是environment
                    String fakeServiceId = "fakeService" + fakeServiceCount++;
                    String sourceRef = messageFlowInfo.getSourceRef().equals(Constant.environment) ? fakeServiceId : messageFlowInfo.getSourceRef();
                    String targetRef = messageFlowInfo.getTargetRef().equals(Constant.environment) ? fakeServiceId : messageFlowInfo.getTargetRef();
                    messageFlow.setAttribute("sourceRef", sourceRef);
                    messageFlow.setAttribute("targetRef", targetRef);
                    collaboration.appendChild(messageFlow);
                    Point pointSource = null;
                    Point pointTarget = null;

                    // 如果连接的是fakeService则重新计算坐标
                    if (fakeServiceId.equals(sourceRef)) {
                        for (int i = 0; i < shapes.getLength(); i++) {
                            Element item = (Element) shapes.item(i);
                            String bpmnElement = item.getAttribute("bpmnElement");
                            if (bpmnElement != null && fakeServiceId.equals(bpmnElement)) {
                                Element bound = (Element) item.getElementsByTagName("dc:Bounds").item(0);
                                double x = Double.parseDouble(bound.getAttribute("x"));
                                double y = Double.parseDouble(bound.getAttribute("y"));
                                double width = Double.parseDouble(bound.getAttribute("width"));
                                pointSource = new Point(x + width / 2, y);
                                break;
                            }
                        }
                    } else {
                        for (int i = 0; i < shapes.getLength(); i++) {
                            Element item = (Element) shapes.item(i);
                            String bpmnElement = item.getAttribute("bpmnElement");
                            if (bpmnElement != null && messageFlowInfo.getSourceRef().equals(bpmnElement)) {
                                Element bound = (Element) item.getElementsByTagName("dc:Bounds").item(0);
                                double x = Double.parseDouble(bound.getAttribute("x"));
                                double y = Double.parseDouble(bound.getAttribute("y"));
                                double width = Double.parseDouble(bound.getAttribute("width"));
                                pointSource = new Point(x + width / 2, y);
                                break;
                            }
                        }
                    }

                    if (fakeServiceId.equals(targetRef)) {
                        for (int i = 0; i < shapes.getLength(); i++) {
                            Element item = (Element) shapes.item(i);
                            String bpmnElement = item.getAttribute("bpmnElement");
                            if (bpmnElement != null && fakeServiceId.equals(bpmnElement)) {
                                Element bound = (Element) item.getElementsByTagName("dc:Bounds").item(0);
                                double x = Double.parseDouble(bound.getAttribute("x"));
                                double y = Double.parseDouble(bound.getAttribute("y"));
                                double width = Double.parseDouble(bound.getAttribute("width"));
                                pointTarget = new Point(x + width / 2, y);
                                break;
                            }
                        }
                    } else {
                        for (int i = 0; i < shapes.getLength(); i++) {
                            Element item = (Element) shapes.item(i);
                            String bpmnElement = item.getAttribute("bpmnElement");
                            if (bpmnElement != null && messageFlowInfo.getTargetRef().equals(bpmnElement)) {
                                Element bound = (Element) item.getElementsByTagName("dc:Bounds").item(0);
                                double x = Double.parseDouble(bound.getAttribute("x"));
                                double y = Double.parseDouble(bound.getAttribute("y"));
                                double width = Double.parseDouble(bound.getAttribute("width"));
                                pointTarget = new Point(x + width / 2, y);
                                break;
                            }
                        }
                    }

                    if (pointSource != null && pointTarget != null) {
                        Element flowElement = document.createElement("bpmndi:BPMNEdge");
                        flowElement.setAttribute("id", id + "_di");
                        flowElement.setAttribute("bpmnElement", id);
                        Element startPoint = document.createElement("di:waypoint");
                        startPoint.setAttribute("x", String.valueOf(pointSource.getX()));
                        startPoint.setAttribute("y", String.valueOf(pointSource.getY()));
                        flowElement.appendChild(startPoint);
                        Element endPoint = document.createElement("di:waypoint");
                        endPoint.setAttribute("x", String.valueOf(pointTarget.getX()));
                        endPoint.setAttribute("y", String.valueOf(pointTarget.getY()));
                        flowElement.appendChild(endPoint);
                        plane.appendChild(flowElement);
                    }
                }
            }
        }
    }

    public String testXml() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(byteArrayOutputStream));
            String xmlStr = byteArrayOutputStream.toString();
//            System.out.println(xmlStr);
            return xmlStr;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}