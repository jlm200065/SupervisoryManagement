package org.example.web.controller.engine;

import com.github.pagehelper.PageInfo;
import org.example.common.response.ResponseResult;
import org.example.system.domin.Topic;
import org.example.system.domin.dto.TopicQueryDto;
import org.example.system.service.TopicService;
import org.example.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topic")
public class TopicController extends BaseController {

    @Autowired
    private TopicService topicService;

    @PostMapping("/all")
    public ResponseResult findTopicsByPage(@RequestBody TopicQueryDto queryDto) {
        PageInfo<Topic> data = topicService.findTopicsByPage(queryDto);
        return getResult(data);
    }

    @GetMapping("/{id}")
    public ResponseResult findTopicById(@PathVariable String id) {
        Topic topic = topicService.findTopicById(id);
        return getResult(topic);
    }

    @PostMapping("/add")
    public ResponseResult addOrUpdateTopic(@RequestBody Topic topic) {
        int result = topicService.addOrUpdateTopic(topic);
        return getResult(result);
    }

    @PutMapping("/update")
    public ResponseResult updateTopic(@RequestBody Topic topic) {
        int result = topicService.addOrUpdateTopic(topic);
        return getResult(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteTopic(@PathVariable String id) {
        int result = topicService.deleteTopic(id);
        return getResult(result);
    }
}
