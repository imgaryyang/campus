package com.gzzm.platform.desktop;

import com.gzzm.platform.organ.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 工作桌面公告，在工作桌面顶部循环出现的公告
 *
 * @author camel
 * @date 2010-5-28
 */
@Entity(table = "PFPLACARD", keys = "placardId")
public class Placard
{
    /**
     * 公共ID
     */
    @Generatable(length = 6)
    private Integer placardId;

    /**
     * 内容
     */
    @Require
    @ColumnDescription(nullable = false, type = "varchar(4000)")
    private String content;

    /**
     * 公告所属的部门
     */
    @Index
    @ColumnDescription(nullable = false)
    private Integer deptId;

    /**
     * 关联部门对象
     */
    private Dept dept;

    /**
     * 创建公告的用户的ID
     */
    @ColumnDescription(nullable = false)
    private Integer creator;

    /**
     * 关联用户对象
     */
    @ToOne("CREATOR")
    private User createUser;

    /**
     * 创建时间
     */
    @ColumnDescription(nullable = false)
    private Date createTime;

    /**
     * 是否有效，默认为true
     */
    @ColumnDescription(nullable = false, defaultValue = "1")
    private Boolean valid;

    /**
     * 排序ID
     */
    private Integer orderId;

    public Placard()
    {
    }

    public Integer getPlacardId()
    {
        return placardId;
    }

    public void setPlacardId(Integer placardId)
    {
        this.placardId = placardId;
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

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(User createUser)
    {
        this.createUser = createUser;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Boolean isValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
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
        return content;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Placard))
            return false;

        Placard placard = (Placard) o;

        return placardId.equals(placard.placardId);
    }

    @Override
    public int hashCode()
    {
        return placardId.hashCode();
    }
}
