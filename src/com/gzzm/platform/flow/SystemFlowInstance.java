package com.gzzm.platform.flow;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.Dept;
import net.cyan.thunwind.annotation.*;
import net.cyan.valmiki.flow.dao.FlowInstanceEntity;

import java.util.List;

/**
 * 流程实例对象
 *
 * @author camel
 * @date 2010-9-14
 */
@Entity(table = "PFFLOWINSTANCE", keys = "instanceId")
@Indexes({@Index(columns = {"DEPTID", "STARTTIME"})})
public class SystemFlowInstance extends FlowInstanceEntity
{
    /**
     * 实例所属的部门ID
     */
    private Integer deptId;

    /**
     * 关联Dept对象
     */
    private Dept dept;

    /**
     * 表单数据ID
     *
     * @see com.gzzm.platform.form.FormBody#bodyId
     */
    @ColumnDescription(type = "number(12)")
    private Long bodyId;

    /**
     * 是否是测试流程
     */
    private Boolean test;

    @OneToMany
    private List<InstanceBody> bodies;

    /**
     * 数据所属的年份，用于对数据进行分区
     */
    @ColumnDescription(type = "number(4)")
    private Integer year;

    public SystemFlowInstance()
    {
    }

    /**
     * 重载实例ID的get方法，加上annotation
     *
     * @return 流程实例ID
     */
    @Override
    @Generatable(length = 12)
    public Long getInstanceId()
    {
        return super.getInstanceId();
    }

    /**
     * 流程ID
     *
     * @see FlowInfo#flowId
     */
    @Override
    @ColumnDescription(type = "number(7)")
    public Integer getFlowId()
    {
        return super.getFlowId();
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

    public Long getBodyId()
    {
        return bodyId;
    }

    public void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    public Boolean isTest()
    {
        return test;
    }

    public void setTest(Boolean test)
    {
        this.test = test;
    }

    public List<InstanceBody> getBodies()
    {
        return bodies;
    }

    public void setBodies(List<InstanceBody> bodies)
    {
        this.bodies = bodies;
    }

    @Override
    @ColumnDescription(type = "number(13)")
    public Long getMainStepId()
    {
        return super.getMainStepId();
    }

    @Override
    @ColumnDescription(type = "varchar(500)")
    public String getTitle()
    {
        return super.getTitle();
    }

    @Override
    @ColumnDescription(type = "varchar(50)")
    public String getFlowTag()
    {
        return super.getFlowTag();
    }

    public String getFlowTagName()
    {
        return Tools.getMessage("platform.flow." + getFlowTag());
    }

    @Override
    @ColumnDescription(type = "number(1)")
    public Integer getState()
    {
        return super.getState();
    }

    @Override
    @ColumnDescription(type = "varchar(50)")
    public String getCreator()
    {
        return super.getCreator();
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }
}
