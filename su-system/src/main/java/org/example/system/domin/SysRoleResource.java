package org.example.system.domin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SysRoleResource implements Serializable {

    private static final Long serialVersionUID = 1L;

    private Long roleId;
    private Long resourceId;
}
