package org.example.system.coordinator.service;

import org.example.system.coordinator.ProcessInCoordinator;
import org.example.system.coordinator.resolveProcessFactory;
import org.example.system.coordinator.combineProcessFactory;
import org.example.system.domain.Process;
import org.example.system.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class coordinatorService {
    @Autowired
    private ProcessService processService;

//    @Autowired
//    private combineProcessFactory combiner;

    public List<List<ProcessInCoordinator>>  selectAndCombineProcess() throws UnsupportedEncodingException {
        List<Process> allProcesses = processService.findAll();
        List<ProcessInCoordinator> processInCoordinatorList = new ArrayList<>();
        for (Process process : allProcesses) {
            ProcessInCoordinator processInCoordinator = resolveProcessFactory.resolveProcessByText(process.getBpmn(), process.getId());
            processInCoordinatorList.add(processInCoordinator);
        }
        combineProcessFactory combiner = new combineProcessFactory();
        return combiner.allCombineList(processInCoordinatorList);
    }


}
