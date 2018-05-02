package com.gzzm.platform.opinion;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 全局共享的意见
 *
 * @author camel
 * @date 14-1-24
 */
@Entity(table = "PFCOMMONOPINION", keys = "opinionId")
public class CommonOpinion
{
    @Generatable(length = 9)
    private Integer opinionId;

    @Require
    @ColumnDescription(type = "varchar(1000)")
    private String title;

    @Require
    @ColumnDescription(type = "varchar(1000)")
    private String content;

    @Index
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public CommonOpinion()
    {
    }

    public Integer getOpinionId()
    {
        return opinionId;
    }

    public void setOpinionId(Integer opinionId)
    {
        this.opinionId = opinionId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public String toString()
    {
        return title;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof CommonOpinion))
            return false;

        CommonOpinion that = (CommonOpinion) o;

        return opinionId.equals(that.opinionId);
    }

    @Override
    public int hashCode()
    {
        return opinionId.hashCode();
    }
}
