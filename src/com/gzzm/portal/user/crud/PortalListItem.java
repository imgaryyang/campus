package com.gzzm.portal.user.crud;

import com.gzzm.platform.commons.crud.Action;

import java.util.*;

/**
 * 门户列表项，包括每个列表要显示的内容
 *
 * @author camel
 * @date 2011-8-9
 */
public class PortalListItem
{
    /**
     * 对象的主键
     */
    private Object key;

    /**
     * 实体
     */
    private Object record;

    /**
     * 动作列表
     */
    private List<Action> actions;

    public PortalListItem()
    {
    }

    public Object getKey()
    {
        return key;
    }

    public void setKey(Object key)
    {
        this.key = key;
    }

    public Object getRecord()
    {
        return record;
    }

    public void setRecord(Object record)
    {
        this.record = record;
    }

    public List<Action> getActions()
    {
        return actions;
    }

    public void addAction(Action action)
    {
        if (actions == null)
            actions = new ArrayList<Action>();

        actions.add(action);
    }
}
