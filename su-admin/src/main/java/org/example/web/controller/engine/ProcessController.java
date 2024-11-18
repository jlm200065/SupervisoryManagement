package org.example.web.controller.engine;

import org.example.common.response.ResponseResult;
import org.example.system.combiner.BpmnPanel;
import org.example.system.converter.service.BpmnService;
import org.example.system.domain.dto.ProcessQueryDto;
import org.example.system.domain.Process;
import org.example.system.service.ProcessService;
import org.example.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.system.coordinator.service.coordinatorService;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/process")
public class ProcessController extends BaseController {

    @Autowired
    private ProcessService processService;

    @Autowired
    private coordinatorService coordinatorService;


    @Autowired
    private BpmnService bpmnService;



    // 根据 ID 获取特定的 Process 实体
    @GetMapping("/getAllCombination")
    public ResponseResult getAllCombination() throws UnsupportedEncodingException {
        return getResult(coordinatorService.selectAndCombineProcess());
    }

    @PostMapping("/all")
    public ResponseResult findProcessesByPage(@RequestBody ProcessQueryDto queryDto) {
        System.out.println("QueryDTO: " + queryDto.toString());
        List<Process> data = processService.findProcessesByPage(queryDto);
        return getResult(data);
    }

    @PostMapping("/alternate")
    public ResponseResult findProcessByNameExpectSelf(@RequestBody HashMap<String, String> queryDto) {
        System.out.println("QueryDTO: " + queryDto.toString());
        List<Process> data = processService.findProcessByNameExpectSelf(queryDto.get("name"), queryDto.get("id"));
        return getResult(data);
    }



    @PostMapping("/getCombineProcess")
    public ResponseResult getCombineProcess(@RequestBody List<String> processIds) {
        List<Process> data = processService.getCombineProcess(processIds);
        return getResult(data);
    }

    // 根据 ID 获取特定的 Process 实体
    @GetMapping("/{id}")
    public ResponseResult findProcessById(@PathVariable String id) {
        Process process = processService.findById(id);
        return getResult(process);
    }

    // 创建新的 Process 实体
    @PostMapping("/add")
    public ResponseResult addProcess(@RequestBody Process process) {
        int result = processService.addProcess(process);
        return getResult(result);
    }

    // 更新现有的 Process 实体
    @PutMapping("/update")
    public ResponseResult updateProcess(@RequestBody Process process) {
        int result = processService.updateProcess(process);
        return getResult(result);
    }

    // 删除特定的 Process 实体
    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteProcess(@PathVariable String id) {
        int result = processService.deleteProcess(id);
        return getResult(result);
    }

    @PostMapping("/getCombine")
    public String getCombine(@RequestBody List<String> xmlContents) throws UnsupportedEncodingException {
        BpmnPanel bpmnPanel = new BpmnPanel();
        for (String xmlContent : xmlContents) {
            bpmnPanel.addBpmnInfo(xmlContent);
        }

        bpmnPanel.sortParticipantHoldersByWidth();
        bpmnPanel.initEnvironmentWidth();
        bpmnPanel.confirmParticipantPositionAndSize();
        bpmnPanel.drawParticipant();
        bpmnPanel.drawMessageFlow();
        String resultXml = bpmnPanel.testXml();

        return resultXml;
    }


    @PostMapping("/adapterProcess")
    public ResponseEntity<String> processBpmn(@RequestBody Map<String, String> request) {
        String bpmnXml = request.get("bpmnXml");
        String result = bpmnService.convertBpmnToCollaboration(bpmnXml);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(500).body("Error processing BPMN XML");
        }
    }

}
