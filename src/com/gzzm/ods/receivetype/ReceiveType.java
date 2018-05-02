package com.gzzm.ods.receivetype;

import com.gzzm.platform.organ.Dept;
import com.gzzm.platform.wordnumber.WordNumber;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 收文编号
 *
 * @author camel
 * @date 11-11-5
 */
@Entity(table = "ODRECEIVETYPE", keys = "receiveTypeId")
public class ReceiveType
{
    /**
     * 收文类型ID,主键
     */
    @Generatable(length = 8)
    private Integer receiveTypeId;

    /**
     * 收文类型名称
     */
    @ColumnDescription(type = "varchar(250)")
    @Require
    @Unique(with = "deptId")
    private String receiveTypeName;

    /**
     * 收文类型的内容
     */
    @ColumnDescription(type = "varchar(250)")
    @Require
    private String serial;

    /**
     * 收文类型所属的部门
     */
    @Index
    private Integer deptId;

    /**
     * 关联部门对象
     */
    @NotSerialized
    private Dept dept;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 此收文类型关联的来文单位
     */
    @NotSerialized
    @ManyToMany(table = "ODRECEIVETYPESOURCE")
    private List<Dept> sourceDepts;

    @Index
    @ColumnDescription(nullable = false, defaultValue = "0")
    private Integer parentReceiveTypeId;

    @NotSerialized
    @ToOne("PARENTRECEIVETYPEID")
    private ReceiveType parentReceiveType;

    @NotSerialized
    @OrderBy(column = "ORDERID")
    @OneToMany("PARENTRECEIVETYPEID")
    private List<ReceiveType> subReceiveTypes;

    /**
     * 序列号所属部门
     */
    private Integer serialDeptId;

    @NotSerialized
    @ToOne("SERIALDEPTID")
    private Dept serialDept;

    /**
     * 0表示收文编号，1表示办文编号
     */
    @ColumnDescription(type = "number(1)", nullable = false, defaultValue = "0")
    private int type;

    public ReceiveType()
    {
    }

    public ReceiveType(Integer receiveTypeId, String receiveTypeName)
    {
        this.receiveTypeId = receiveTypeId;
        this.receiveTypeName = receiveTypeName;
    }

    public Integer getReceiveTypeId()
    {
        return receiveTypeId;
    }

    public void setReceiveTypeId(Integer receiveTypeId)
    {
        this.receiveTypeId = receiveTypeId;
    }

    public String getReceiveTypeName()
    {
        return receiveTypeName;
    }

    public void setReceiveTypeName(String receiveTypeName)
    {
        this.receiveTypeName = receiveTypeName;
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
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

    @NotSerialized
    public String getText() throws Exception
    {
        if (serial != null)
        {
            WordNumber wordNumber = new WordNumber(serial);

            return wordNumber.toString();
        }

        return null;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public List<Dept> getSourceDepts()
    {
        return sourceDepts;
    }

    public void setSourceDepts(List<Dept> sourceDepts)
    {
        this.sourceDepts = sourceDepts;
    }

    public Integer getParentReceiveTypeId()
    {
        return parentReceiveTypeId;
    }

    public void setParentReceiveTypeId(Integer parentReceiveTypeId)
    {
        this.parentReceiveTypeId = parentReceiveTypeId;
    }

    public ReceiveType getParentReceiveType()
    {
        return parentReceiveType;
    }

    public void setParentReceiveType(ReceiveType parentReceiveType)
    {
        this.parentReceiveType = parentReceiveType;
    }

    public List<ReceiveType> getSubReceiveTypes()
    {
        return subReceiveTypes;
    }

    public void setSubReceiveTypes(List<ReceiveType> subReceiveTypes)
    {
        this.subReceiveTypes = subReceiveTypes;
    }

    public Integer getSerialDeptId()
    {
        return serialDeptId;
    }

    public void setSerialDeptId(Integer serialDeptId)
    {
        this.serialDeptId = serialDeptId;
    }

    public Dept getSerialDept()
    {
        return serialDept;
    }

    public void setSerialDept(Dept serialDept)
    {
        this.serialDept = serialDept;
    }

    @NotSerialized
    public List<Integer> getAllReceiveTypeIds()
    {
        ArrayList<Integer> typeIds = new ArrayList<Integer>();
        getAllReceiveTypeIds(typeIds);

        return typeIds;
    }

    public void getAllReceiveTypeIds(List<Integer> typeIds)
    {
        typeIds.add(receiveTypeId);

        for (ReceiveType subReceiveType : getSubReceiveTypes())
        {
            subReceiveType.getAllReceiveTypeIds(typeIds);
        }
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof ReceiveType))
            return false;

        ReceiveType that = (ReceiveType) o;

        return receiveTypeId.equals(that.receiveTypeId);
    }

    @Override
    public int hashCode()
    {
        return receiveTypeId.hashCode();
    }

    @Override
    public String toString()
    {
        return receiveTypeName;
    }
}
