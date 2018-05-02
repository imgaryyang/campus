package com.gzzm.ods.sendnumber;

import com.gzzm.platform.organ.Dept;
import com.gzzm.platform.wordnumber.WordNumber;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 发文字号
 *
 * @author camel
 * @date 2011-6-30
 */
@Entity(table = "ODSENDNUMBER", keys = "sendNumberId")
public class SendNumber
{
    /**
     * 发文字号ID,主键
     */
    @Generatable(length = 8)
    private Integer sendNumberId;

    /**
     * 发文字号名称
     */
    @ColumnDescription(type = "varchar(250)")
    @Require
    @Unique(with = "deptId")
    private String sendNumberName;

    /**
     * 发文字号的内容
     */
    @ColumnDescription(type = "varchar(250)")
    @Require
    private String sendNumber;

    /**
     * 发文字号所属的部门
     */
    @Index
    private Integer deptId;

    /**
     * 关联部门对象
     */
    private Dept dept;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public SendNumber()
    {
    }

    public SendNumber(Integer sendNumberId, String sendNumberName)
    {
        this.sendNumberId = sendNumberId;
        this.sendNumberName = sendNumberName;
    }

    public Integer getSendNumberId()
    {
        return sendNumberId;
    }

    public void setSendNumberId(Integer sendNumberId)
    {
        this.sendNumberId = sendNumberId;
    }

    public String getSendNumberName()
    {
        return sendNumberName;
    }

    public void setSendNumberName(String sendNumberName)
    {
        this.sendNumberName = sendNumberName;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
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

    @NotSerialized
    public String getText() throws Exception
    {
        if (sendNumber != null)
        {
            WordNumber wordNumber = new WordNumber(sendNumber);

            return wordNumber.toString();
        }

        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SendNumber))
            return false;

        SendNumber that = (SendNumber) o;

        return sendNumberId.equals(that.sendNumberId);
    }

    @Override
    public int hashCode()
    {
        return sendNumberId.hashCode();
    }

    @Override
    public String toString()
    {
        return sendNumberName;
    }
}
