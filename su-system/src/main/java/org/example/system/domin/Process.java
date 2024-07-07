package org.example.system.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "process")
public class Process implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String delegatedMessageIn;
    private String delegatedMessageOut;
    private String bpmn;
    private String name;
    private String origin;
    private String engineCategory;
}
