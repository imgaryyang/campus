package com.gzzm.ods.business;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.form.*;
import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 办公公文系统内部流程业务模型
 *
 * @author camel
 * @date 2010-9-14
 */
@Entity(table = "ODBUSINESSMODEL", keys = "businessId")
public class BusinessModel
{
    /**
     * 业务ID，主键
     */
    @Generatable(length = 7)
    private Integer businessId;

    /**
     * 业务名称
     */
    @Require
    @Unique(with = "deptId")
    @ColumnDescription(type = "varchar(250)")
    private String businessName;

    /**
     * 所属部门的部门ID
     */
    @Index
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 流程ID，关联IEFLOWID，当发布新版本的流程时保持稳定性
     */
    @ColumnDescription(type = "number(7)")
    private Integer flowId;

    /**
     * 表单ID，关联IEFORMID，当发布新版本的流程时保持稳定性
     */
    @ColumnDescription(type = "number(7)")
    private Integer formId;

    /**
     * 发文表单ID，仅对能转为发文的公文类型有效，包括receive,exreceive和inner
     */
    @ColumnDescription(type = "number(7)")
    private Integer sendFormId;

    /**
     * 业务类型，如send为发文流程,receive为收文流程，union为联合办文流程,collect为会签流程,inner为内部流程等等，可扩展
     *
     * @see com.gzzm.ods.flow.OdFlowInstance#type
     */
    @Require
    @ColumnDescription(type = "varchar(50)")
    private BusinessType type;

    /**
     * 此业务用于公文交换还是内部OA
     */
    @Require
    private BusinessTag tag;

    /**
     * 业务简称，如呈阅，呈批等，可以为空
     */
    @ColumnDescription(type = "varchar(50)")
    private String simpleName;

    /**
     * 业务状态
     */
    @Require
    private BusinessState state;

    /**
     * 排序ID
     */
    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    /**
     * 流程组件类型，为一个java类的全名，此java类实现OdFlowComponent，为流程增加扩展功能
     *
     * @see com.gzzm.ods.flow.OdFlowComponent
     * @see com.gzzm.ods.flow.OdFlowContext
     */
    @ColumnDescription(type = "varchar(250)")
    private String componentType;

    @ColumnDescription(type = "varchar(250)")
    private String jsFile;

    /**
     * 类型名称
     */
    @ColumnDescription(type = "varchar(50)")
    private String typeName;

    @Require
    @ColumnDescription(defaultValue = "1")
    private Boolean display;

    public BusinessModel()
    {
    }

    public Integer getBusinessId()
    {
        return businessId;
    }

    public void setBusinessId(Integer businessId)
    {
        this.businessId = businessId;
    }

    public String getBusinessName()
    {
        return businessName;
    }

    public void setBusinessName(String businessName)
    {
        this.businessName = businessName;
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

    public Integer getFlowId()
    {
        return flowId;
    }

    public void setFlowId(Integer flowId)
    {
        this.flowId = flowId;
    }

    public Integer getFormId()
    {
        return formId;
    }

    public void setFormId(Integer formId)
    {
        this.formId = formId;
    }

    public Integer getSendFormId()
    {
        return sendFormId;
    }

    public void setSendFormId(Integer sendFormId)
    {
        this.sendFormId = sendFormId;
    }

    public BusinessType getType()
    {
        return type;
    }

    public void setType(BusinessType type)
    {
        this.type = type;
    }

    public BusinessTag getTag()
    {
        return tag;
    }

    public void setTag(BusinessTag tag)
    {
        this.tag = tag;
    }

    public String getSimpleName()
    {
        return simpleName;
    }

    public void setSimpleName(String simpleName)
    {
        this.simpleName = simpleName;
    }

    public BusinessState getState()
    {
        return state;
    }

    public void setState(BusinessState state)
    {
        this.state = state;
    }

    public Boolean getDisplay()
    {
        return display;
    }

    public void setDisplay(Boolean display)
    {
        this.display = display;
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
    public FlowInfo getFlow() throws Exception
    {
        if (flowId != null)
            return FlowApi.getLastFlow(flowId);
        return null;
    }

    @NotSerialized
    public FormInfo getForm() throws Exception
    {
        if (formId != null)
            return FormApi.getLastForm(formId);
        return null;
    }

    @NotSerialized
    public FormInfo getSendForm() throws Exception
    {
        if (sendFormId != null)
            return FormApi.getLastForm(sendFormId);
        return null;
    }

    public String getComponentType()
    {
        return componentType;
    }

    public void setComponentType(String componentType)
    {
        this.componentType = componentType;
    }

    public String getJsFile()
    {
        return jsFile;
    }

    public void setJsFile(String jsFile)
    {
        this.jsFile = jsFile;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    @NotSerialized
    public String getComponentName()
    {
        return Tools.getMessage(componentType);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof BusinessModel))
            return false;

        BusinessModel that = (BusinessModel) o;

        return businessId.equals(that.businessId);
    }

    @Override
    public int hashCode()
    {
        return businessId.hashCode();
    }

    @Override
    public String toString()
    {
        return businessName;
    }
}
