package org.example.system.service.impl;

import com.github.pagehelper.PageHelper;
import org.example.system.domain.Process;
import org.example.system.domain.dto.ProcessQueryDto;
import org.example.system.mapper.ProcessMapper;
import org.example.system.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProcessServiceImpl implements ProcessService {

    @Autowired
    private ProcessMapper processMapper;

    @Override
    public List<Process> findAll() {
        return processMapper.selectAll();
    }

    @Override
    public Process findById(String id) {
        return processMapper.selectByPrimaryKey(id);
    }

    @Override
    public int addProcess(Process process) {
        process.setId(UUID.randomUUID().toString());  // 设置 UUID
        return processMapper.insert(process);
    }

    @Override
    public int updateProcess(Process process) {
        return processMapper.updateByPrimaryKey(process);
    }

    @Override
    public int deleteProcess(String id) {
        return processMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Process> findProcessesByPage(ProcessQueryDto queryDto) {
        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        return processMapper.selectByCondition(queryDto);
    }
}
