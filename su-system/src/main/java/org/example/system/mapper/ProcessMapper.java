package org.example.system.mapper;

import org.example.system.domain.Process;
import org.example.system.domain.dto.ProcessQueryDto;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProcessMapper extends Mapper<Process> {
    List<Process> selectByCondition(ProcessQueryDto queryDto);
}
