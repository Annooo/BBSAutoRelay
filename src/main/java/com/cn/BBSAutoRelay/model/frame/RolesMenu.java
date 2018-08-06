package com.cn.BBSAutoRelay.model.frame;

import javax.persistence.*;

@Table(name = "tb_role_menu")
public class RolesMenu {
    @Id
    @Column(name = "menu_id")
    private Long menuId;

    @Id
    @Column(name = "role_id")
    private Long roleId;

    /**
     * @return menu_id
     */
    public Long getMenuId() {
        return menuId;
    }

    /**
     * @param menuId
     */
    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    /**
     * @return role_id
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * @param roleId
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}