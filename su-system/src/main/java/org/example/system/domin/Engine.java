package org.example.system.domin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Engine implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;

    @NotBlank(message = "Engine type cannot be empty")
    private String engineType;

    @NotBlank(message = "URL cannot be empty")
    private String url;

    private String pubSubId;

    private Integer status;

    private String name;
}
