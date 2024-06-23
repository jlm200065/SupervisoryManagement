package org.example.web.controller.engine;

import com.github.pagehelper.PageInfo;
import org.example.common.response.ResponseResult;
import org.example.system.domin.dto.EngineDto;
import org.example.system.domin.dto.EngineQueryDto;
import org.example.system.domin.vo.EngineVo;
import org.example.system.service.EngineService;
import org.example.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/engine")
public class EngineController extends BaseController {

    @Autowired
    private EngineService engineService;

    @PostMapping("/all")
    public ResponseResult findEnginesByPage(@RequestBody EngineQueryDto queryDto) {
        PageInfo<EngineVo> data = engineService.findEnginesByPage(queryDto);
        return getResult(data);
    }

    @GetMapping("/{id}")
    public ResponseResult findEngineById(@PathVariable String id) {
        EngineVo engine = engineService.findEngineById(id);
        return getResult(engine);
    }

    @PostMapping("/add")
    public ResponseResult addOrUpdateEngine(@RequestBody EngineDto engineDto) {
        int result = engineService.addOrUpdateEngine(engineDto);
        return getResult(result);
    }

    @PutMapping("/update")
    public ResponseResult updateEngine(@RequestBody EngineDto engineDto) {
        int result = engineService.addOrUpdateEngine(engineDto);
        return getResult(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseResult deleteEngine(@PathVariable String id) {
        int result = engineService.deleteEngine(id);
        return getResult(result);
    }
}
