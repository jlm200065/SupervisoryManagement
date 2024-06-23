package org.example.system.mapper;

import org.example.system.domin.Topic;
import org.example.system.domin.dto.TopicQueryDto;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TopicMapper extends Mapper<Topic> {
    List<Topic> selectByCondition(TopicQueryDto queryDto);
}
