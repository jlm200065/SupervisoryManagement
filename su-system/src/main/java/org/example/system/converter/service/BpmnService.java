package org.example.system.converter.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

@Service
public class BpmnService {

    public String convertBpmnToCollaboration(String bpmnXml) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(bpmnXml)));

            // Create collaboration element
            Element definitionsElement = doc.getDocumentElement();
            Element collaborationElement = doc.createElementNS(definitionsElement.getNamespaceURI(), "bpmn:collaboration");
            collaborationElement.setAttribute("id", "Collaboration_0b3fl5e");
            definitionsElement.appendChild(collaborationElement);

            // Add Environment participant
            Element participantEnvironment = doc.createElementNS(definitionsElement.getNamespaceURI(), "bpmn:participant");
            participantEnvironment.setAttribute("id", "Participant_Environment");
            participantEnvironment.setAttribute("name", "Environment");
            participantEnvironment.setAttribute("processRef", "Process_Environment");
            collaborationElement.appendChild(participantEnvironment);

            // Add process participant
            NodeList processList = doc.getElementsByTagName("bpmn:process");
            if (processList.getLength() > 0) {
                Element processElement = (Element) processList.item(0);
                String processId = processElement.getAttribute("id");
                Element participantElement = doc.createElementNS(definitionsElement.getNamespaceURI(), "bpmn:participant");
                participantElement.setAttribute("id", "Participant_037smfk");
                participantElement.setAttribute("name", "测试参与者");
                participantElement.setAttribute("processRef", processId);
                collaborationElement.appendChild(participantElement);

                // Iterate through all elements to add message flows
                NodeList documentationList = doc.getElementsByTagName("bpmn:documentation");
                for (int i = 0; i < documentationList.getLength(); i++) {
                    Element documentationElement = (Element) documentationList.item(i);
                    String documentationText = documentationElement.getTextContent();
                    if (documentationText.startsWith("input:")) {
                        Element parentElement = (Element) documentationElement.getParentNode();
                        String sourceRef = "Participant_Environment";
                        String targetRef = parentElement.getAttribute("id");

                        Element messageFlow = doc.createElementNS(definitionsElement.getNamespaceURI(), "bpmn:messageFlow");
                        messageFlow.setAttribute("id", "Flow_" + i + "_input");
                        messageFlow.setAttribute("name", "输入");
                        messageFlow.setAttribute("sourceRef", sourceRef);
                        messageFlow.setAttribute("targetRef", targetRef);
                        collaborationElement.appendChild(messageFlow);
                    } else if (documentationText.startsWith("output:")) {
                        Element parentElement = (Element) documentationElement.getParentNode();
                        String sourceRef = parentElement.getAttribute("id");
                        String targetRef = "Participant_Environment";

                        Element messageFlow = doc.createElementNS(definitionsElement.getNamespaceURI(), "bpmn:messageFlow");
                        messageFlow.setAttribute("id", "Flow_" + i + "_output");
                        messageFlow.setAttribute("name", "输出");
                        messageFlow.setAttribute("sourceRef", sourceRef);
                        messageFlow.setAttribute("targetRef", targetRef);
                        collaborationElement.appendChild(messageFlow);
                    }
                }
            }

            // Add Process_Environment
            Element processEnvironment = doc.createElementNS(definitionsElement.getNamespaceURI(), "bpmn:process");
            processEnvironment.setAttribute("id", "Process_Environment");
            processEnvironment.setAttribute("isExecutable", "true");
            definitionsElement.appendChild(processEnvironment);

            // Add BPMN shapes for participants
            Element bpmnPlane = doc.createElementNS(definitionsElement.getNamespaceURI(), "bpmndi:BPMNPlane");
            bpmnPlane.setAttribute("id", "BPMNPlane_1");
            bpmnPlane.setAttribute("bpmnElement", collaborationElement.getAttribute("id"));
            definitionsElement.appendChild(bpmnPlane);

            // Add BPMN shapes for the Environment participant
            Element participantEnvironmentShape = doc.createElementNS(definitionsElement.getNamespaceURI(), "bpmndi:BPMNShape");
            participantEnvironmentShape.setAttribute("id", "Participant_Environment_di");
            participantEnvironmentShape.setAttribute("bpmnElement", "Participant_Environment");
            participantEnvironmentShape.setAttribute("isHorizontal", "true");
            Element boundsEnvironment = doc.createElementNS(definitionsElement.getNamespaceURI(), "dc:Bounds");
            boundsEnvironment.setAttribute("x", "142");
            boundsEnvironment.setAttribute("y", "80");
            boundsEnvironment.setAttribute("width", "468");
            boundsEnvironment.setAttribute("height", "150");
            participantEnvironmentShape.appendChild(boundsEnvironment);
            bpmnPlane.appendChild(participantEnvironmentShape);

            // Add BPMN shapes for the process participant
            Element participantShape = doc.createElementNS(definitionsElement.getNamespaceURI(), "bpmndi:BPMNShape");
            participantShape.setAttribute("id", "Participant_037smfk_di");
            participantShape.setAttribute("bpmnElement", "Participant_037smfk");
            participantShape.setAttribute("isHorizontal", "true");
            Element boundsParticipant = doc.createElementNS(definitionsElement.getNamespaceURI(), "dc:Bounds");
            boundsParticipant.setAttribute("x", "142");
            boundsParticipant.setAttribute("y", "270");
            boundsParticipant.setAttribute("width", "468");
            boundsParticipant.setAttribute("height", "140");
            participantShape.appendChild(boundsParticipant);
            bpmnPlane.appendChild(participantShape);

            // Transform document to string
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);

            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
