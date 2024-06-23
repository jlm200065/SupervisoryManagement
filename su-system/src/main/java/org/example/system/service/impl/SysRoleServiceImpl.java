package org.example.system.service.impl;

import org.example.system.domin.SysRole;
import org.example.system.mapper.SysRoleMapper;
import org.example.system.mapper.SysRoleResourceMapper;
import org.example.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;


@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleResourceMapper sysRoleResourceMapper;



    @Override
    public List<SysRole> findAllRoles() {
        return sysRoleMapper.findAllRoles();
    }

    @Override
    public SysRole findRoleById(Long id) {
        return sysRoleMapper.findRoleById(id);
    }

    @Override
    public int addRole(SysRole sysRole) {
        return sysRoleMapper.addRole(sysRole);
    }

    @Override
    public int updateRole(SysRole sysRole) {
        return sysRoleMapper.updateRole(sysRole);
    }

    @Override
    public int deleteRole(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += sysRoleMapper.deleteRole(id);
        }
        return rows;
    }

    @Override
    public int allotRight(Long roleId, Long[] keys) {
        int rows = 0;
        HashSet<Long> originResources = new HashSet<>(sysRoleMapper.findAllRights(roleId));

        for (Long id : keys) {
            if (originResources.contains(id)) {
                originResources.remove(id);
            } else {
                rows += sysRoleResourceMapper.addRight(roleId, id);
            }
        }
        for (Long id : originResources) {
            rows += sysRoleMapper.deleteRight(roleId, id);
        }
        return rows;
    }
}
