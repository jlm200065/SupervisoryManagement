package org.example.system.service;

import com.github.pagehelper.PageInfo;
import org.example.system.domin.Topic;
import org.example.system.domin.dto.TopicQueryDto;

public interface TopicService {
    PageInfo<Topic> findTopicsByPage(TopicQueryDto queryDto);

    Topic findTopicById(String id);

    int addOrUpdateTopic(Topic topic);

    int deleteTopic(String id);
}
