package org.example.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.system.domin.Engine;
import org.example.system.domin.dto.EngineDto;
import org.example.system.domin.dto.EngineQueryDto;
import org.example.system.domin.vo.EngineVo;
import org.example.system.mapper.EngineMapper;
import org.example.system.mapper.EngineTopicMapper;
import org.example.system.service.EngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EngineServiceImpl implements EngineService {
    @Override
    public int updateEngineStatus(String id, int status) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("status", status);
        return engineMapper.updateEngineStatus(params);
    }
    @Override
    public List<Engine> findAll() {
        return engineMapper.selectAll();
    }
    @Autowired
    private EngineMapper engineMapper;

    @Autowired
    private EngineTopicMapper engineTopicMapper;

    @Override
    public PageInfo<EngineVo> findEnginesByPage(EngineQueryDto queryDto) {
        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        List<EngineVo> engines = engineMapper.selectByCondition(queryDto);
        return new PageInfo<>(engines);
    }
    @Transactional
    @Override
    public int addOrUpdateEngine(EngineDto engineDto) {
        Engine engine = engineMapper.findEngineByName(engineDto.getName());
        if (engine == null) {
            // 如果名称不存在，则创建新的 Engine
            engine = new Engine();
            engine.setId(UUID.randomUUID().toString());
            engine.setEngineType(engineDto.getEngineType());
            engine.setUrl(engineDto.getUrl());
            engine.setPubSubId(engineDto.getPubSubId());
            engine.setStatus(engineDto.getStatus());
            engine.setName(engineDto.getName());
            updateEngineTopics(engineDto, engine);
            return engineMapper.addEngine(engine);
        } else {
            // 如果名称已存在，则更新已有的 Engine
            engine.setEngineType(engineDto.getEngineType());
            engine.setUrl(engineDto.getUrl());
            engine.setPubSubId(engineDto.getPubSubId());
            engine.setStatus(engineDto.getStatus());

            updateEngineTopics(engineDto, engine);
            return engineMapper.updateEngine(engine);
        }
    }

    private void updateEngineTopics(EngineDto engineDto, Engine engine) {
        engineTopicMapper.deleteByEngineId(engine.getId());
        if (engineDto.getTopicIdList() != null) {
            for (String topicId : engineDto.getTopicIdList()) {
                engineTopicMapper.insert(UUID.randomUUID().toString(), engine.getId(), topicId);
            }
        }
    }

    @Override
    public EngineVo findEngineById(String id) {
        return (EngineVo) engineMapper.findEngineById(id);
    }



    @Override
    public int deleteEngine(String id) {
        engineTopicMapper.deleteByEngineId(id);
        return engineMapper.deleteEngine(id);
    }
}
