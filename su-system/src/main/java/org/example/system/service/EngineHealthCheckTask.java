package org.example.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.example.system.domin.Engine;
import org.example.system.service.EngineService;

import java.util.List;

@Component
public class EngineHealthCheckTask {

    @Autowired
    private EngineService engineService;

    private final RestTemplate restTemplate = new RestTemplate();

    // 定时任务，每分钟执行一次
    @Scheduled(fixedRate = 60000)
    public void checkEngineHealth() {
        List<Engine> engines = engineService.findAll();
        for (Engine engine : engines) {
            String url = engine.getUrl() + "/heartHealth";
            try {
                String response = restTemplate.getForObject(url, String.class);
                // 处理响应，如果需要的话
                System.out.println("Response from " + url + ": " + response);
            } catch (Exception e) {
                // 处理请求失败的情况
                engineService.updateEngineStatus(engine.getId(), -1);
                System.err.println("Failed to call " + url + ": " + e.getMessage());
            }
        }
    }
}
