package org.example.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.system.domin.SysRoleResource;

public interface SysRoleResourceMapper {

    int addRight(@Param("roleId") Long roleId, @Param("resourceId") Long resourceId);
}
