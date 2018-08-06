package com.cn.BBSAutoRelay.model.frame;

import javax.persistence.*;

@Table(name = "tb_menu")
public class Menu {
    @Id
    @Column(name = "menu_id")
    private Long menuId;

    /**
     * 菜单名
     */
    private String title;

    /**
     * 图标
     */
    private String icon;

    /**
     * 资源地址
     */
    private String href;

    /**
     * 权限
     */
    private String perms;

    /**
     * true：展开，false：不展开
     */
    private String spread;

    /**
     * 父节点
     */
    @Column(name = "parent_id")
    private Long parentId;

    private Long sorting;

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
     * 获取菜单名
     *
     * @return title - 菜单名
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置菜单名
     *
     * @param title 菜单名
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * 获取图标
     *
     * @return icon - 图标
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置图标
     *
     * @param icon 图标
     */
    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    /**
     * 获取资源地址
     *
     * @return href - 资源地址
     */
    public String getHref() {
        return href;
    }

    /**
     * 设置资源地址
     *
     * @param href 资源地址
     */
    public void setHref(String href) {
        this.href = href == null ? null : href.trim();
    }

    /**
     * 获取权限
     *
     * @return perms - 权限
     */
    public String getPerms() {
        return perms;
    }

    /**
     * 设置权限
     *
     * @param perms 权限
     */
    public void setPerms(String perms) {
        this.perms = perms == null ? null : perms.trim();
    }

    /**
     * 获取true：展开，false：不展开
     *
     * @return spread - true：展开，false：不展开
     */
    public String getSpread() {
        return spread;
    }

    /**
     * 设置true：展开，false：不展开
     *
     * @param spread true：展开，false：不展开
     */
    public void setSpread(String spread) {
        this.spread = spread == null ? null : spread.trim();
    }

    /**
     * 获取父节点
     *
     * @return parent_id - 父节点
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * 设置父节点
     *
     * @param parentId 父节点
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return sorting
     */
    public Long getSorting() {
        return sorting;
    }

    /**
     * @param sorting
     */
    public void setSorting(Long sorting) {
        this.sorting = sorting;
    }
}