package org.example.system.service;

import org.example.system.domin.dto.PubSubQueryDto;
import org.example.system.domin.PubSub;

import java.util.List;

public interface PubSubService {
    List<PubSub> findAll();

    PubSub findById(String id);

    int addPubSub(PubSub pubSub);

    int updatePubSub(PubSub pubSub);

    int deletePubSub(String id);

    List<PubSub> findPubSubsByPage(PubSubQueryDto queryDto);
}
