package org.example.system.mapper;

import org.example.system.domin.dto.PubSubQueryDto;
import org.example.system.domin.PubSub;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PubSubMapper extends Mapper<PubSub> {
    List<PubSub> selectByCondition(PubSubQueryDto queryDto);
}
