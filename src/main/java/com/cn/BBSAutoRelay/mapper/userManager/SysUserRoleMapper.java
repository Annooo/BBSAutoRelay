package com.cn.BBSAutoRelay.mapper.userManager;

import com.cn.BBSAutoRelay.model.userManager.SysUserRoleKey;

public interface SysUserRoleMapper {
    int deleteByPrimaryKey(SysUserRoleKey key);

    int insert(SysUserRoleKey record);

    int insertSelective(SysUserRoleKey record);
}