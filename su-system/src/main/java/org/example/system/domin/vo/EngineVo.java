package org.example.system.domin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.system.domin.Engine;
import org.example.system.domin.PubSub;
import org.example.system.domin.Topic;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EngineVo extends Engine {
    private static final long serialVersionUID = 1L;
    private String pubSubName;
    private String pubSubUrl;
    private List<Topic> topics;
}
