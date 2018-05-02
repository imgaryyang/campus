package com.gzzm.safecampus.device.card.entity;

import net.cyan.thunwind.annotation.*;

/**
 * @author liyabin
 * @date 2018/3/28
 */
@Entity(table = "SCGROUNPINFO", keys = "id")
public class GroupInfo
{
    @Generatable(length = 8)
    private Integer id;
    @ColumnDescription(type = "VARCHAR(8)")
    private String groupNo;
    @ColumnDescription(type = "VARCHAR(20)")
    private String groupName;
    @ColumnDescription(type = "VARCHAR(20)")
    private String groupInfo;

    public GroupInfo()
    {
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getGroupNo()
    {
        return groupNo;
    }

    public void setGroupNo(String groupNo)
    {
        this.groupNo = groupNo;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getGroupInfo()
    {
        return groupInfo;
    }

    public void setGroupInfo(String groupInfo)
    {
        this.groupInfo = groupInfo;
    }
}
