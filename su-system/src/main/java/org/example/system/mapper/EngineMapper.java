package org.example.system.mapper;

import org.example.system.domin.Engine;
import org.example.system.domin.dto.EngineQueryDto;
import org.example.system.domin.vo.EngineVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface EngineMapper extends Mapper<Engine> {
    List<EngineVo> selectByCondition(EngineQueryDto queryDto);

    Engine findEngineById(String id);

    int addEngine(Engine engine);

    int updateEngine(Engine engine);

    int deleteEngine(String id);

    Engine findEngineByUrl(String url);
    int updateEngineStatus(Map<String, Object> params);
    Engine findEngineByName(String name);
}
