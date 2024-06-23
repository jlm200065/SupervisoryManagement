package org.example.system.domin.dto;

import lombok.Data;

@Data
public class TopicQueryDto {
    private String topic;
    private int pageNum;
    private int pageSize;
}
