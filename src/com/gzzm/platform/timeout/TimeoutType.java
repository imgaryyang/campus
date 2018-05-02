package com.gzzm.platform.timeout;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.Value;

import java.util.*;

/**
 * 超时类型，由各业务系统提供
 *
 * @author camel
 * @date 14-4-1
 */
public class TimeoutType implements Value<String>
{
    private static final long serialVersionUID = 4490353052180191194L;

    public static final TimeoutType ROOT = new TimeoutType("@root", "", "");

    /**
     * 类型ID，由业务系统定义，如果已@开头表示这是个虚拟类型，仅做分类使用，不是实际的类型
     */
    private String typeId;

    private String typeName;

    private String simpleName;

    @NotSerialized
    private List<TimeoutType> children;

    public TimeoutType()
    {
    }

    public TimeoutType(String typeId)
    {
        this.typeId = typeId;
    }

    public TimeoutType(String typeId, String typeName, String simpleName)
    {
        this.typeId = typeId;
        this.typeName = typeName;
        this.simpleName = simpleName;
    }

    public String getTypeId()
    {
        return typeId;
    }

    public void setTypeId(String typeId)
    {
        this.typeId = typeId;
    }

    public String getTypeName()
    {
        if (typeName == null)
            return Tools.getMessage("timeout." + typeId + ".name");

        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public String getSimpleName()
    {
        if (simpleName == null)
            return Tools.getMessage("timeout." + typeId + ".simpleName");

        return simpleName;
    }

    public void setSimpleName(String simpleName)
    {
        this.simpleName = simpleName;
    }

    public List<TimeoutType> getChildren()
    {
        return children;
    }

    public void setChildren(List<TimeoutType> children)
    {
        this.children = children;
    }

    public void addChild(TimeoutType type)
    {
        if (children == null)
            children = new ArrayList<TimeoutType>();

        children.add(type);
    }

    public String valueOf()
    {
        return typeId;
    }

    @Override
    public String toString()
    {
        return getTypeName();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof TimeoutType))
            return false;

        TimeoutType that = (TimeoutType) o;

        return typeId.equals(that.typeId);
    }

    @Override
    public int hashCode()
    {
        return typeId.hashCode();
    }
}
