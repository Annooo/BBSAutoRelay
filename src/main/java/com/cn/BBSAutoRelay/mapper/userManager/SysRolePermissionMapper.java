package com.cn.BBSAutoRelay.mapper.userManager;

import com.cn.BBSAutoRelay.model.userManager.SysRolePermissionKey;

public interface SysRolePermissionMapper {
    int deleteByPrimaryKey(SysRolePermissionKey key);

    int insert(SysRolePermissionKey record);

    int insertSelective(SysRolePermissionKey record);
}