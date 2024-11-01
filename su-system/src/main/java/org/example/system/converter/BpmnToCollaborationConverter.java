package org.example.system.converter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;

public class BpmnToCollaborationConverter {
    public static void main(String[] args) {
        try {
            File inputFile = new File("超市.bpmn");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);

            doc.getDocumentElement().normalize();

            // 检查是否已经有Collaboration和Participant_Environment
            NodeList collaborationList = doc.getElementsByTagName("bpmn:collaboration");
            Element collaboration = null;
            String environmentParticipantId = "Participant_Environment";

            if (collaborationList.getLength() == 0) {
                // 如果没有Collaboration元素，创建新的Collaboration
                collaboration = doc.createElement("bpmn:collaboration");
                collaboration.setAttribute("id", "Collaboration_1");
                doc.getDocumentElement().appendChild(collaboration);

                // 创建Environment参与者
                Element environmentParticipant = doc.createElement("bpmn:participant");
                environmentParticipant.setAttribute("id", environmentParticipantId);
                environmentParticipant.setAttribute("name", "Environment");
                collaboration.appendChild(environmentParticipant);
            } else {
                // 如果已存在Collaboration元素，获取它
                collaboration = (Element) collaborationList.item(0);

                // 检查是否已存在Participant_Environment
                NodeList participants = collaboration.getElementsByTagName("bpmn:participant");
                boolean environmentExists = false;
                for (int i = 0; i < participants.getLength(); i++) {
                    Element participant = (Element) participants.item(i);
                    if (environmentParticipantId.equals(participant.getAttribute("id"))) {
                        environmentExists = true;
                        break;
                    }
                }

                // 如果不存在Participant_Environment，创建它
                if (!environmentExists) {
                    Element environmentParticipant = doc.createElement("bpmn:participant");
                    environmentParticipant.setAttribute("id", environmentParticipantId);
                    environmentParticipant.setAttribute("name", "Environment");
                    collaboration.appendChild(environmentParticipant);
                }
            }

            // 处理BPMN中的任务和事件，找到带有input/output的元素
            NodeList tasks = doc.getElementsByTagName("bpmn:task");
            for (int i = 0; i < tasks.getLength(); i++) {
                Element task = (Element) tasks.item(i);
                NodeList inputOutputElements = task.getElementsByTagName("camunda:inputOutput");

                if (inputOutputElements.getLength() > 0) {
                    // 添加MessageFlow连接到Environment
                    String taskId = task.getAttribute("id");
                    Element messageFlow = doc.createElement("bpmn:messageFlow");
                    messageFlow.setAttribute("id", "Flow_" + taskId);
                    messageFlow.setAttribute("sourceRef", taskId);
                    messageFlow.setAttribute("targetRef", environmentParticipantId);
                    collaboration.appendChild(messageFlow);
                }
            }

            // 处理中间事件
            NodeList intermediateEvents = doc.getElementsByTagName("bpmn:intermediateCatchEvent");
            for (int i = 0; i < intermediateEvents.getLength(); i++) {
                Element event = (Element) intermediateEvents.item(i);
                NodeList inputElements = event.getElementsByTagName("camunda:inputParameter");

                if (inputElements.getLength() > 0) {
                    // 添加MessageFlow从Environment到事件
                    String eventId = event.getAttribute("id");
                    Element messageFlow = doc.createElement("bpmn:messageFlow");
                    messageFlow.setAttribute("id", "Flow_" + eventId);
                    messageFlow.setAttribute("sourceRef", environmentParticipantId);
                    messageFlow.setAttribute("targetRef", eventId);
                    collaboration.appendChild(messageFlow);
                }
            }

            // 输出新的BPMN文件
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("output_collaboration.bpmn"));
            transformer.transform(source, result);

            System.out.println("BPMN 文件已成功转换为协作图!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
