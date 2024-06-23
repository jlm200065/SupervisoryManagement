package org.example.system.domin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "pub_sub")
public class PubSub {

    // 可以在构造方法中生成 UUID
    public PubSub(String name, String url) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.url = url;
    }
    @Id
    private String id;
    private String name;
    private String url;
}
