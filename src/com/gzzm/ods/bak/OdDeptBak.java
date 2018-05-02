package com.gzzm.ods.bak;

import com.gzzm.ods.receivetype.ReceiveType;
import com.gzzm.ods.sendnumber.SendNumber;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;

/**
 * @author camel
 * @date 12-10-15
 */
@Entity(table = "ODDEPTBAK", keys = "backId")
public class OdDeptBak
{
    @Generatable(length = 9)
    private Integer backId;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String backName;

    /**
     * 备份包所属的部门，即备份哪个部门的公文
     */
    @Index
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 要备份的公文时间段的开始时间
     */
    @Require
    private Date startTime;

    /**
     * 要备份的公文时间段的结束时间
     */
    @Require
    private Date endTime;

    /**
     * 备份包创建时间
     */
    private java.util.Date createTime;

    /**
     * 关联的收文类型，仅当type为receive时有效，通过#com.gzzm.ods.receivetype.ReceiveTypeTreeModel来选择
     */
    @Index
    private Integer receiveTypeId;

    @NotSerialized
    private ReceiveType receiveType;

    /**
     * 关联的发文字号，仅当type为SEND时有效，只能选择本部门的发文字号，可以为空
     */
    @Index
    private Integer sendNumberId;

    @NotSerialized
    private SendNumber sendNumber;

    /**
     * 备份发文还是收文，必填
     */
    @Require
    @ColumnDescription(nullable = false)
    private OdDeptBakType type;

    @ColumnDescription(type = "varchar(250)")
    private String path;

    private Integer creator;

    @ToOne("CREATOR")
    private User createUser;

    public OdDeptBak()
    {
    }

    public Integer getBackId()
    {
        return backId;
    }

    public void setBackId(Integer backId)
    {
        this.backId = backId;
    }

    public String getBackName()
    {
        return backName;
    }

    public void setBackName(String backName)
    {
        this.backName = backName;
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

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public java.util.Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime)
    {
        this.createTime = createTime;
    }

    public Integer getReceiveTypeId()
    {
        return receiveTypeId;
    }

    public void setReceiveTypeId(Integer receiveTypeId)
    {
        this.receiveTypeId = receiveTypeId;
    }

    public ReceiveType getReceiveType()
    {
        return receiveType;
    }

    public void setReceiveType(ReceiveType receiveType)
    {
        this.receiveType = receiveType;
    }

    public OdDeptBakType getType()
    {
        return type;
    }

    public void setType(OdDeptBakType type)
    {
        this.type = type;
    }

    public Integer getSendNumberId()
    {
        return sendNumberId;
    }

    public void setSendNumberId(Integer sendNumberId)
    {
        this.sendNumberId = sendNumberId;
    }

    public SendNumber getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(SendNumber sendNumber)
    {
        this.sendNumber = sendNumber;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OdDeptBak))
            return false;

        OdDeptBak odDeptBak = (OdDeptBak) o;

        return backId.equals(odDeptBak.backId);
    }

    @Override
    public int hashCode()
    {
        return backId.hashCode();
    }

    @Override
    public String toString()
    {
        return backName;
    }
}