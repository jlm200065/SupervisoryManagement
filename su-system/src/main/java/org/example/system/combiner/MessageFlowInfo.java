package org.example.system.combiner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:ljl
 * @Date:2023/10/28
 * @VERSION:1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageFlowInfo {
    private String name;
    private String sourceRef;
    private String targetRef;
}
