package com.gzzm.ods.exchange.back;

import com.gzzm.platform.organ.Dept;
import com.gzzm.platform.wordnumber.WordNumber;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 退文编号
 *
 * @author ldp
 * @date 2018/1/11
 */
@Entity(table = "ODBACKNUMBER", keys = "backNumberId")
public class BackNumber
{
    /**
     * 退文编号
     */
    @Generatable(length = 9)
    private Integer backNumberId;

    /**
     * 退文字号名称
     */
    @Require
    @Unique(with = "deptId")
    @ColumnDescription(type = "varchar(250)")
    private String backNumberName;

    /**
     * 退文字号的内容
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String backNumber;

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

    public BackNumber()
    {
    }

    public Integer getBackNumberId()
    {
        return backNumberId;
    }

    public void setBackNumberId(Integer backNumberId)
    {
        this.backNumberId = backNumberId;
    }

    public String getBackNumberName()
    {
        return backNumberName;
    }

    public void setBackNumberName(String backNumberName)
    {
        this.backNumberName = backNumberName;
    }

    public String getBackNumber()
    {
        return backNumber;
    }

    public void setBackNumber(String backNumber)
    {
        this.backNumber = backNumber;
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
        if (backNumber != null)
        {
            WordNumber wordNumber = new WordNumber(backNumber);

            return wordNumber.toString();
        }

        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof BackNumber))
            return false;

        BackNumber that = (BackNumber) o;

        return backNumberId.equals(that.backNumberId);
    }

    @Override
    public int hashCode()
    {
        return backNumberId.hashCode();
    }

    @Override
    public String toString()
    {
        return backNumberName;
    }
}
