package com.gzzm.oa.vote;

import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 投票类型
 *
 * @author camel
 * @date 12-3-26
 */
@Entity(table = "OAVOTETYPE", keys = "typeId")
public class VoteType
{
    @Generatable(length = 3)
    private Integer typeId;

    @Unique
    @Require
    @ColumnDescription(type = "varchar(50)")
    private String typeName;

    @Require
    @ColumnDescription(type = "varchar(50)")
    private String actionName;

    /**
     * 显示页面
     */
    @ColumnDescription(type = "varchar(50)")
    private String showPage;

    public VoteType()
    {
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public String getActionName()
    {
        return actionName;
    }

    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    public String getShowPage()
    {
        return showPage;
    }

    public void setShowPage(String showPage)
    {
        this.showPage = showPage;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof VoteType))
            return false;

        VoteType voteType = (VoteType) o;

        return typeId.equals(voteType.typeId);
    }

    @Override
    public int hashCode()
    {
        return typeId.hashCode();
    }

    @Override
    public String toString()
    {
        return typeName;
    }
}
