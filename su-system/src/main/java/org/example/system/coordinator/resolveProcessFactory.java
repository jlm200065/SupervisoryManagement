package org.example.system.coordinator;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaPropertiesImpl;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;

public class resolveProcessFactory {
    private static DocumentBuilder documentBuilder;
    static {
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static ProcessInCoordinator resolveProcess(String FILENAME_BASE, String FILE) {
        String FILENAME = FILENAME_BASE + FILE;
        BpmnModelInstance modelInst;
        File file = new File(FILENAME);
        modelInst = Bpmn.readModelFromFile(file);
        ProcessInCoordinator processInCoordinator = new ProcessInCoordinator(FILE);
        Collection<MessageFlow> messageFlowList = modelInst.getModelElementsByType(MessageFlow.class);
        Iterator<MessageFlow> messageFlowIterator = messageFlowList.iterator();
        while (messageFlowIterator.hasNext()) {
            MessageFlow messageFlow = messageFlowIterator.next();
            if (messageFlow.getExtensionElements() != null) {
//            System.out.println(messageFlow.getExtensionElements().getUniqueChildElementByType(CamundaPropertiesImpl.class));
                CamundaPropertiesImpl camundaProperties = (CamundaPropertiesImpl) messageFlow.getExtensionElements().getUniqueChildElementByType(CamundaPropertiesImpl.class);
//            System.out.println(camundaProperties.getCamundaProperties());
                Iterator<CamundaProperty> prosIterator = camundaProperties.getCamundaProperties().iterator();
                while (prosIterator.hasNext()) {
                    System.out.println(prosIterator.next().getCamundaValue());
                }
            }
            if (messageFlow.getSource().getId().contains("Activity")) {
                processInCoordinator.getMessageSent().add(messageFlow.getName());
            } else {
                processInCoordinator.getMessageReceived().add(messageFlow.getName());
            }
        }
        System.out.println(processInCoordinator.toString());
        return processInCoordinator;
    }


    public static ProcessInCoordinator resolveProcessByText(String bpmnContent, String participantId) throws UnsupportedEncodingException {
        try {
            Document document = documentBuilder.parse(new InputSource(new ByteArrayInputStream(bpmnContent.getBytes("UTF-8"))));
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        // 获取对应的modelInstance
        BpmnModelInstance modelInst = Bpmn.readModelFromStream(new ByteArrayInputStream(bpmnContent.getBytes("UTF-8")));
        Collection<MessageFlow> messageFlowList = modelInst.getModelElementsByType(MessageFlow.class);
        ProcessInCoordinator processInCoordinator = new ProcessInCoordinator(participantId);
        Iterator<MessageFlow> messageFlowIterator = messageFlowList.iterator();
        while (messageFlowIterator.hasNext()) {
            MessageFlow messageFlow = messageFlowIterator.next();
            if (messageFlow.getExtensionElements() != null) {
//            System.out.println(messageFlow.getExtensionElements().getUniqueChildElementByType(CamundaPropertiesImpl.class));
                CamundaPropertiesImpl camundaProperties = (CamundaPropertiesImpl) messageFlow.getExtensionElements().getUniqueChildElementByType(CamundaPropertiesImpl.class);
//            System.out.println(camundaProperties.getCamundaProperties());
                Iterator<CamundaProperty> prosIterator = camundaProperties.getCamundaProperties().iterator();
                while (prosIterator.hasNext()) {
                    System.out.println(prosIterator.next().getCamundaValue());
                }
            }
            if (messageFlow.getSource().getId().contains("Activity")) {
                processInCoordinator.getMessageSent().add(messageFlow.getName());
            } else {
                processInCoordinator.getMessageReceived().add(messageFlow.getName());
            }
        }
        System.out.println(processInCoordinator.toString());
        return processInCoordinator;
    }
}
