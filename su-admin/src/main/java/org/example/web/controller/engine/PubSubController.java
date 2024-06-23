package org.example.web.controller.engine;

import org.example.common.response.ResponseResult;
import org.example.system.domin.dto.PubSubQueryDto;
import org.example.system.domin.PubSub;
import org.example.system.service.PubSubService;
import org.example.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pubSub")
public class PubSubController extends BaseController {

    @Autowired
    private PubSubService pubSubService;

    // 获取所有 PubSub 实体
    @PostMapping("/all")
    public ResponseResult findPubSubsByPage(@RequestBody PubSubQueryDto queryDto) {

        System.out.println("queryDto: "+ queryDto.toString());
        List<PubSub> data = pubSubService.findPubSubsByPage(queryDto);
        return getResult(data);
    }

    // 根据 ID 获取特定的 PubSub 实体
    @GetMapping("/{id}")
    public ResponseResult findPubSubById(@PathVariable String id) {
        PubSub pubSub = pubSubService.findById(id);
        return getResult(pubSub);
    }

    // 创建新的 PubSub 实体
    @PostMapping("/add")
    public ResponseResult addPubSub(@RequestBody PubSub pubSub) {
        int result = pubSubService.addPubSub(pubSub);
        return getResult(result);
    }

    // 更新现有的 PubSub 实体
    @PutMapping("/update")
    public ResponseResult updatePubSub(@RequestBody PubSub pubSub) {
        int result = pubSubService.updatePubSub(pubSub);
        return getResult(result);
    }

    // 删除特定的 PubSub 实体
    @DeleteMapping("/delete/{id}")
    public ResponseResult deletePubSub(@PathVariable String id) {
        int result = pubSubService.deletePubSub(id);
        return getResult(result);
    }
}
