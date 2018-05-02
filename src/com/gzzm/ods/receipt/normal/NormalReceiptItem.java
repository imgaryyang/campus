package com.gzzm.ods.receipt.normal;

import com.gzzm.platform.commons.Sex;
import com.gzzm.platform.organ.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 回执的参与人员，由收文部门填写
 *
 * @author camel
 * @date 12-4-8
 */
@Entity(table = "ODNORMALRECEIPTITEM", keys = "itemId")
@Indexes(@Index(
        columns = {"RECEIPTID", "DEPTID", "USERID"}
))
public class NormalReceiptItem
{
    @Generatable(length = 12)
    private Long itemId;

    private Long receiptId;

    private NormalReceipt normalReceipt;

    /**
     * 参与人员名称
     */
    @Require
    private String userName;

    /**
     * 参与人员ID，如果不是系统中的用户，为空
     */
    private Integer userId;

    private User user;

    /**
     * 性别
     */
    @Require
    private Sex sex;

    /**
     * 联系电话
     */
    @ColumnDescription(type = "varchar(50)")
    private String phone;

    /**
     * 职务
     */
    private String station;

    /**
     * 备注
     */
    @ColumnDescription(type = "varchar(800)")
    private String remark;

    /**
     * 填报的部门
     */
    private Integer deptId;

    private Dept dept;

    /**
     * 填报的用户
     */
    private Integer creator;

    @ToOne("CREATOR")
    private User createUser;

    /**
     * 填报的时间
     */
    private Date createTime;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    @ColumnDescription(defaultValue = "1")
    private Boolean replied;

    public NormalReceiptItem()
    {
    }

    public Long getItemId()
    {
        return itemId;
    }

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
    }

    public NormalReceipt getNormalReceipt()
    {
        return normalReceipt;
    }

    public void setNormalReceipt(NormalReceipt normalReceipt)
    {
        this.normalReceipt = normalReceipt;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
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

    public Sex getSex()
    {
        return sex;
    }

    public void setSex(Sex sex)
    {
        this.sex = sex;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getStation()
    {
        return station;
    }

    public void setStation(String station)
    {
        this.station = station;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
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

    public Boolean getReplied()
    {
        return replied;
    }

    public void setReplied(Boolean replied)
    {
        this.replied = replied;
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
        return userName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof NormalReceiptItem))
            return false;

        NormalReceiptItem that = (NormalReceiptItem) o;

        return itemId.equals(that.itemId);
    }

    @Override
    public int hashCode()
    {
        return itemId.hashCode();
    }
}
