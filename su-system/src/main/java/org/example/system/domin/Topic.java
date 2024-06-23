package org.example.system.domin;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "topic")
public class Topic {
    @Id
    private String id;
    private String topic;
}
