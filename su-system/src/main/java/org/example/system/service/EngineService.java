package org.example.system.service;

import com.github.pagehelper.PageInfo;
import org.example.system.domin.Engine;
import org.example.system.domin.dto.EngineDto;
import org.example.system.domin.dto.EngineQueryDto;
import org.example.system.domin.vo.EngineVo;

import java.util.List;

public interface EngineService {
    PageInfo<EngineVo> findEnginesByPage(EngineQueryDto queryDto);

    EngineVo findEngineById(String id);

    int addOrUpdateEngine(EngineDto engineDto);
    List<Engine> findAll();
    int deleteEngine(String id);

    int updateEngineStatus(String id, int status);


}
