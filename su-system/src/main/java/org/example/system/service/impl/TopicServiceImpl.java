package org.example.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.system.domin.Topic;
import org.example.system.domin.dto.TopicQueryDto;
import org.example.system.mapper.TopicMapper;
import org.example.system.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Override
    public PageInfo<Topic> findTopicsByPage(TopicQueryDto queryDto) {
        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        List<Topic> topics = topicMapper.selectByCondition(queryDto);
        return new PageInfo<>(topics);
    }

    @Override
    public Topic findTopicById(String id) {
        return topicMapper.selectByPrimaryKey(id);
    }

    @Override
    public int addOrUpdateTopic(Topic topic) {
        if (topic.getId() == null || topic.getId().isEmpty()) {
            topic.setId(UUID.randomUUID().toString());
            return topicMapper.insert(topic);
        } else {
            return topicMapper.updateByPrimaryKey(topic);
        }
    }

    @Override
    public int deleteTopic(String id) {
        return topicMapper.deleteByPrimaryKey(id);
    }
}
