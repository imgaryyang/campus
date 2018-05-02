package com.gzzm.oa.vote;

import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 12-3-29
 */
@Entity(table = "OAVOTEPROJECT", keys = "projectId")
public class VoteProject
{
    @Generatable(length = 6)
    private Integer projectId;

    private Integer typeId;

    private Integer voteType;

    @Require
    @Unique(with = {"parentProject", "deptId", "typeId"})
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String projectName;

    private Integer deptId;

    @NotSerialized
    private Dept dept;

    private Integer parentProjectId;

    @NotSerialized
    private VoteProject parentProject;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public VoteProject()
    {
    }

    public Integer getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Integer projectId)
    {
        this.projectId = projectId;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public Integer getVoteType()
    {
        return voteType;
    }

    public void setVoteType(Integer voteType)
    {
        this.voteType = voteType;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
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

    public Integer getParentProjectId()
    {
        return parentProjectId;
    }

    public void setParentProjectId(Integer parentProjectId)
    {
        this.parentProjectId = parentProjectId;
    }

    public VoteProject getParentProject()
    {
        return parentProject;
    }

    public void setParentProject(VoteProject parentProject)
    {
        this.parentProject = parentProject;
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

        if (!(o instanceof VoteProject))
            return false;

        VoteProject that = (VoteProject) o;

        return projectId.equals(that.projectId);
    }

    @Override
    public int hashCode()
    {
        return projectId.hashCode();
    }

    @Override
    public String toString()
    {
        return projectName;
    }
}
