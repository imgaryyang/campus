package com.gzzm.oa.mail;

import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 邮件签名
 *
 * @author camel
 * @date 2015/6/19
 */
@Entity(table = "OAMAILSIGN", keys = "signId")
public class MailSign
{
    @Generatable(length = 8)
    private Integer signId;

    private Integer userId;

    @NotSerialized
    private User user;

    private Integer deptId;

    @NotSerialized
    private Dept dept;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String title;

    @Require
    @ColumnDescription(type = "varchar(4000)")
    private String content;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public MailSign()
    {
    }

    public Integer getSignId()
    {
        return signId;
    }

    public void setSignId(Integer signId)
    {
        this.signId = signId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
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

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof MailSign))
            return false;

        MailSign sign = (MailSign) o;

        return signId.equals(sign.signId);

    }

    @Override
    public int hashCode()
    {
        return signId.hashCode();
    }

    @Override
    public String toString()
    {
        return title;
    }
}
