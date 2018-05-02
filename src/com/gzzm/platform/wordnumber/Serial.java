package com.gzzm.platform.wordnumber;

import com.gzzm.platform.organ.Dept;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * 序列号
 *
 * @author camel
 * @date 2010-8-8
 */
@Entity(table = "PFSERIAL", keys = {"type", "deptId", "year", "serialName"})
public class Serial
{
    /**
     * 流水号的类型，用于标识流水号的用途，如od表示公文，ga表示审批，等
     *
     * @see WordNumber#type
     */
    @ColumnDescription(type = "varchar(50)")
    private String type;

    /**
     * 流水号所属部门
     */
    private Integer deptId;

    /**
     * 年份，以公历算，每年开始时重置流水号
     */
    @ColumnDescription(type = "number(4)")
    private Integer year;

    /**
     * 流水号的名称
     */
    private String serialName;

    /**
     * 流水号的当前值，即下次生成流水号时的值
     */
    @Require
    @ColumnDescription(type = "number(9)")
    private Integer serialValue;

    /**
     * 关联部门对象
     */
    private Dept dept;

    public Serial()
    {
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getSerialName()
    {
        return serialName;
    }

    public void setSerialName(String serialName)
    {
        this.serialName = serialName;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    public Integer getSerialValue()
    {
        return serialValue;
    }

    public void setSerialValue(Integer serialValue)
    {
        this.serialValue = serialValue;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Serial))
            return false;

        Serial serial = (Serial) o;

        return deptId.equals(serial.deptId) && serialName.equals(serial.serialName) && type.equals(serial.type) &&
                year.equals(serial.year);
    }

    @Override
    public int hashCode()
    {
        int result = type.hashCode();
        result = 31 * result + serialName.hashCode();
        result = 31 * result + deptId.hashCode();
        result = 31 * result + year.hashCode();
        return result;
    }
}
