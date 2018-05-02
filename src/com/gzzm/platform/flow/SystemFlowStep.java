package com.gzzm.platform.flow;

import net.cyan.thunwind.annotation.*;
import net.cyan.valmiki.flow.dao.FlowStepEntity;

/**
 * 流程步骤对象
 *
 * @author camel
 * @date 2010-9-14
 */
@Entity(table = "PFFLOWSTEP", keys = {"stepId", "preStepId"})
@Indexes({
        @Index(columns = {"RECEIVER", "STATE", "LASTSTEP", "RECEIVETIME"}),
        @Index(columns = {"DEPTID", "DEPTLAST", "RECEIVETIME"}),
        @Index(columns = {"INSTANCEID", "PRESTEPID"})
})
public class SystemFlowStep extends FlowStepEntity
{
    /**
     * 部门ID
     */
    @ColumnDescription(type = "number(7)")
    private Integer deptId;

    @ColumnDescription(type = "number(9)")
    private Integer dealer;

    /**
     * 委托ID，当一个事项是某个人委托给另外一个人时，设置对应的委托ID
     *
     * @see com.gzzm.platform.consignation.Consignation#consignationId
     */
    @ColumnDescription(type = "number(9)")
    @Index
    private Integer consignationId;

    @ColumnDescription(defaultValue = "0")
    private Boolean hidden;

    /**
     * 科室的最后一份公文
     */
    @ColumnDescription(type = "number(1)")
    private Integer deptLast;

    private Boolean read;

    /**
     * 数据所属的年份，用于对数据进行分区
     */
    @ColumnDescription(type = "number(4)")
    private Integer year;

    public SystemFlowStep()
    {
    }

    @Override
    @Generatable(length = 13, name = "PFFLOWSTEPID")
    public Long getStepId()
    {
        return super.getStepId();
    }

    @Override
    @Index
    @ColumnDescription(type = "number(13)")
    public Long getGroupId()
    {
        return super.getGroupId();
    }

    @Override
    @ColumnDescription(type = "number(12)")
    public Long getInstanceId()
    {
        return super.getInstanceId();
    }

    @Override
    @Index
    @ColumnDescription(type = "number(13)")
    public Long getPreStepId()
    {
        return super.getPreStepId();
    }

    @Override
    @ColumnDescription(type = "number(13)")
    public Long getTopStepId()
    {
        return super.getTopStepId();
    }

    @Override
    @ColumnDescription(type = "varchar(50)")
    public String getNodeId()
    {
        return super.getNodeId();
    }

    @Override
    @ColumnDescription(type = "varchar(50)")
    public String getNodeName()
    {
        return super.getNodeName();
    }

    /**
     * 接收者，支持三种类型的接收者，用户、岗位、部门
     * 用户用userId表示，岗位用stationId@deptId或者stationName#deptId表示，部门用@deptId表示
     * 用$开头表示这是一个虚拟的接收者，没有实际接收者只保存一个名称，$后面部分为接收者名称
     *
     * @return 接收者
     */
    @Override
    @ColumnDescription(type = "varchar(50)")
    public String getReceiver()
    {
        return super.getReceiver();
    }


    @Override
    @ColumnDescription(type = "varchar(250)")
    public String getSourceName()
    {
        return super.getSourceName();
    }

    @Override
    public Integer getState()
    {
        return super.getState();
    }

    @Override
    @ColumnDescription(type = "varchar(800)")
    public String getWaitForGroup()
    {
        return super.getWaitForGroup();
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getDealer()
    {
        return dealer;
    }

    public void setDealer(Integer dealer)
    {
        this.dealer = dealer;
    }

    public Integer getConsignationId()
    {
        return consignationId;
    }

    public void setConsignationId(Integer consignationId)
    {
        this.consignationId = consignationId;
    }

    public Boolean getHidden()
    {
        return hidden;
    }

    public void setHidden(Boolean hidden)
    {
        this.hidden = hidden;
    }

    public Integer getDeptLast()
    {
        return deptLast;
    }

    public void setDeptLast(Integer deptLast)
    {
        this.deptLast = deptLast;
    }

    public Boolean getRead()
    {
        return read;
    }

    public void setRead(Boolean read)
    {
        this.read = read;
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
