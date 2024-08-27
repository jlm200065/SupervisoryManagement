package org.example.system.service;

import org.example.system.domain.Process;
import org.example.system.domain.dto.ProcessQueryDto;

import java.util.List;

public interface ProcessService {
    List<Process> findAll();

    Process findById(String id);

    int addProcess(Process process);

    int updateProcess(Process process);

    int deleteProcess(String id);

    List<Process> findProcessesByPage(ProcessQueryDto queryDto);

    List<Process> getCombineProcess(List<String> processIds);
}
