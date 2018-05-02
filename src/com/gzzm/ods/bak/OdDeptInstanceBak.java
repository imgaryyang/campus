package com.gzzm.ods.bak;

import com.gzzm.ods.flow.OdFlowInstance;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * @author camel
 * @date 2014/8/25
 */
@Entity(table = "ODDEPTINSTANCEBAK", keys = "backId")
public class OdDeptInstanceBak
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
     * 备份包创建时间
     */
    private java.util.Date createTime;

    @ColumnDescription(type = "varchar(250)")
    private String path;

    private Integer creator;

    @ToOne("CREATOR")
    private User createUser;

    @NotSerialized
    @ManyToMany(table = "ODDEPTINSTANCEBAKS")
    private List<OdFlowInstance> instances;

    public OdDeptInstanceBak()
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

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
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

    public List<OdFlowInstance> getInstances()
    {
        return instances;
    }

    public void setInstances(List<OdFlowInstance> instances)
    {
        this.instances = instances;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof OdDeptInstanceBak))
            return false;

        OdDeptInstanceBak that = (OdDeptInstanceBak) o;

        return backId.equals(that.backId);
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
