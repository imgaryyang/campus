package com.gzzm.safecampus.campus.wx.menu;

import com.gzzm.safecampus.campus.wx.tag.Tag;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.List;

/**
 * 菜单分组
 *
 * @author Neo
 * @date 2018/4/23 11:59
 */
@Entity(table = "SCMENUGROUP", keys = "groupId")
public class MenuGroup
{
    @Generatable(length = 3)
    private Integer groupId;

    @ColumnDescription(type = "VARCHAR2(50)")
    private String groupName;

    /**
     * 菜单匹配条件：标签条件
     */
    @Unique
    private Integer tagId;

    @NotSerialized
    @ToOne
    private Tag tag;

    @ColumnDescription(type = "VARCHAR2(250)")
    private String remark;

    /**
     * 菜单列表
     */
    @NotSerialized
    @OneToMany
    private List<Menu> menus;

    public MenuGroup()
    {
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public Integer getTagId()
    {
        return tagId;
    }

    public void setTagId(Integer tagId)
    {
        this.tagId = tagId;
    }

    public Tag getTag()
    {
        return tag;
    }

    public void setTag(Tag tag)
    {
        this.tag = tag;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public List<Menu> getMenus()
    {
        return menus;
    }

    public void setMenus(List<Menu> menus)
    {
        this.menus = menus;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof MenuGroup)) return false;

        MenuGroup menuGroup = (MenuGroup) o;

        return groupId.equals(menuGroup.groupId);

    }

    @Override
    public int hashCode()
    {
        return groupId.hashCode();
    }
}
