package com.gzzm.im;

import net.cyan.commons.util.Chinese;

/**
 * 群信息
 *
 * @author camel
 * @date 2010-12-31
 */
public class GroupInfo
{
    private Integer groupId;

    private String groupName;

    /**
     * 全拼
     */
    private String spell;

    /**
     * 简拼,即每个汉子的第一个拼音
     */
    private String simpleSpell;

    public GroupInfo()
    {
    }

    public GroupInfo(Integer groupId, String groupName)
    {
        this.groupId = groupId;
        this.groupName = groupName;
        spell = Chinese.getLetters(groupName);
        simpleSpell = Chinese.getFirstLetters(groupName);
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

    public String getSpell()
    {
        return spell;
    }

    public String getSimpleSpell()
    {
        return simpleSpell;
    }
}
