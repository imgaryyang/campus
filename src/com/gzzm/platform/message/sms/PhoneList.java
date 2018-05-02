package com.gzzm.platform.message.sms;

import com.gzzm.platform.organ.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 电话列表
 *
 * @author camel
 * @date 12-4-28
 */
@Entity(table = "PFPHONELIST", keys = "listId")
public class PhoneList
{
    @Generatable(length = 5)
    private Integer listId;

    private Integer deptId;

    private Dept dept;

    private Integer creator;

    @ToOne("CREATOR")
    private User createUser;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String listName;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public PhoneList()
    {
    }

    public Integer getListId()
    {
        return listId;
    }

    public void setListId(Integer listId)
    {
        this.listId = listId;
    }

    public String getListName()
    {
        return listName;
    }

    public void setListName(String listName)
    {
        this.listName = listName;
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

        if (!(o instanceof PhoneList))
            return false;

        PhoneList phoneList = (PhoneList) o;

        return listId.equals(phoneList.listId);
    }

    @Override
    public int hashCode()
    {
        return listId.hashCode();
    }

    @Override
    public String toString()
    {
        return listName;
    }
}
