package org.example.system.service.impl;

import org.example.system.domin.SysResource;
import org.example.system.mapper.SysResourceMapper;
import org.example.system.service.SysResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class SysResourceServiceImpl implements SysResourceService {

    @Autowired
    private SysResourceMapper sysResourceMapper;

    @Override
    public List<SysResource> findAllResources() {
        return sysResourceMapper.findAllResources();
    }

    @Override
    public List<SysResource> findWithChildren() {
        return sysResourceMapper.findWithChildren();
    }

    @Override
    public List<SysResource> findAllWithAllChildren() {
        return sysResourceMapper.findAllWithAllChildren();
    }

    @Override
    public SysResource findResourceById(Long id) {
        return sysResourceMapper.findResourceById(id);
    }

    @Override
    public int addResource(SysResource sysResource) {
        if (sysResource.getParentId() == 0) {
            sysResource.setLevel(1);
        } else {
            SysResource parent = this.findResourceById(sysResource.getParentId());
            if (parent != null) {
                sysResource.setLevel(parent.getLevel() + 1);
            }
        }
        return sysResourceMapper.addResource(sysResource);
    }

    @Override
    public int updateResource(SysResource sysResource) {
        if (sysResource.getParentId() == 0) {
            sysResource.setLevel(1);
        } else {
            SysResource parent = this.findResourceById(sysResource.getParentId());
            if (parent != null) {
                sysResource.setLevel(parent.getLevel() + 1);
            }
        }
        log.debug(sysResource.toString());
        return sysResourceMapper.updateResource(sysResource);
    }

    @Override
    public int deleteResource(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += sysResourceMapper.deleteResource(id);
        }
        return rows;
    }
}
