package org.example.system.service.impl;

import com.github.pagehelper.PageHelper;
import org.example.system.domin.dto.PubSubQueryDto;
import org.example.system.domin.PubSub;
import org.example.system.mapper.PubSubMapper;
import org.example.system.service.PubSubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PubSubServiceImpl implements PubSubService {

    @Autowired
    private PubSubMapper pubSubMapper;

    @Override
    public List<PubSub> findAll() {
        return pubSubMapper.selectAll();
    }

    @Override
    public PubSub findById(String id) {
        return pubSubMapper.selectByPrimaryKey(id);
    }

    @Override
    public int addPubSub(PubSub pubSub) {
        pubSub.setId(UUID.randomUUID().toString());  // 设置 UUID
        return pubSubMapper.insert(pubSub);
    }

    @Override
    public int updatePubSub(PubSub pubSub) {
        return pubSubMapper.updateByPrimaryKey(pubSub);
    }

    @Override
    public int deletePubSub(String id) {
        return pubSubMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<PubSub> findPubSubsByPage(PubSubQueryDto queryDto) {
        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        return pubSubMapper.selectByCondition(queryDto);
    }
}
