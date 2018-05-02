package com.gzzm.platform.flow;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.form.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.*;

import java.util.*;

/**
 * 对开始一个流程的业务逻辑的包装，提供一些属性，用于传入开始一个流程需要的一些信息，并返回流程上下文和表单上下文
 *
 * @author camel
 * @date 11-9-21
 */
public class FlowStartContext
{
    private SystemFlowDao systemFlowDao;

    /**
     * 最终要返回的流程上下文信息
     */
    protected FlowContext flowContext;

    /**
     * 最终要返回的表单上下文信息
     */
    protected SystemFormContext formContext;

    /**
     * 忽略版本号的流程Id，和flowId只需要设置一个
     *
     * @see FlowInfo#ieFlowId
     * @see #flowId
     */
    protected Integer ieFlowId;

    /**
     * 忽略版本号的表单Id，和formId之需要设置一个
     *
     * @see FormInfo#ieFormId
     * @see #formId
     */
    protected Integer ieFormId;

    /**
     * 流程ID，和ieFlowId只需要设置一个
     *
     * @see FlowInfo#flowId
     * @see #ieFlowId
     */
    protected Integer flowId;

    /**
     * 表单ID，和ieFormId只需要设置一个
     *
     * @see FormInfo#formId
     * @see #ieFormId
     */
    protected Integer formId;

    /**
     * 业务上下文，包括当前用户信息，业务部门ID和一些其他的扩展属性
     */
    protected BusinessContext businessContext;

    /**
     * 流程实例的初始标题，可以为空
     *
     * @see SystemFlowInstance#title
     */
    protected String title;

    /**
     * 流程类型标识，不能为空
     *
     * @see SystemFlowInstance#flowTag
     */
    protected String flowTag;

    protected boolean test;

    /**
     * 初始表单数据
     */
    protected Map<String, Object> formDatas;

    /**
     * 表示新建的表单的数据从此表单中copy
     */
    protected Long cloneBodyId;

    public static <T extends FlowStartContext> T create(Class<T> c) throws Exception
    {
        return Tools.getBean(c);
    }

    public FlowStartContext()
    {
    }

    protected SystemFlowDao getSystemFlowDao() throws Exception
    {
        if (systemFlowDao == null)
            systemFlowDao = createSystemFlowDao();

        return systemFlowDao;
    }

    protected SystemFlowDao createSystemFlowDao() throws Exception
    {
        return DefaultSystemFlowDao.getInstance();
    }

    /**
     * 获得忽略版本号的流程ID，子类可以重装此方法
     *
     * @return 忽略版本号的流程ID
     * @throws Exception 允许子类抛出异常
     */
    public Integer getIeFlowId() throws Exception
    {
        return ieFlowId;
    }

    public void setIeFlowId(Integer ieFlowId)
    {
        this.ieFlowId = ieFlowId;
    }

    /**
     * 获得忽略版本号的表单ID，子类可以重装此方法
     *
     * @return 忽略版本号的表单ID
     * @throws Exception 允许子类抛出异常
     */
    public Integer getIeFormId() throws Exception
    {
        return ieFormId;
    }

    public void setIeFormId(Integer ieFormId)
    {
        this.ieFormId = ieFormId;
    }

    public Integer getFlowId() throws Exception
    {
        if (flowId == null)
        {
            Integer ieFlowId = getIeFlowId();
            if (ieFlowId != null)
                flowId = FlowApi.getLastFlowId(ieFlowId);
        }

        return flowId;
    }

    public void setFlowId(Integer flowId)
    {
        this.flowId = flowId;
    }

    public Integer getFormId() throws Exception
    {
        if (formId == null)
        {
            Integer ieFormId = getIeFormId();
            if (ieFormId != null)
                formId = FormApi.getLastFormId(getIeFormId());
        }

        return formId;
    }

    public void setFormId(Integer formId)
    {
        this.formId = formId;
    }

    public BusinessContext getBusinessContext()
    {
        return businessContext;
    }

    public void setBusinessContext(BusinessContext businessContext)
    {
        this.businessContext = businessContext;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getFlowTag() throws Exception
    {
        return flowTag;
    }

    public void setFlowTag(String flowTag)
    {
        this.flowTag = flowTag;
    }

    public boolean isTest()
    {
        return test;
    }

    public void setTest(boolean test)
    {
        this.test = test;
    }

    public Long getCloneBodyId()
    {
        return cloneBodyId;
    }

    public void setCloneBodyId(Long cloneBodyId)
    {
        this.cloneBodyId = cloneBodyId;
    }

    public FlowContext getFlowContext()
    {
        return flowContext;
    }

    public SystemFormContext getFormContext()
    {
        return formContext;
    }

    public Long getInstanceId()
    {
        if (flowContext != null)
        {
            String s = flowContext.getInstance().getInstanceId();

            if (s != null)
                return Long.valueOf(s);
        }

        return null;
    }

    public Long getStepId()
    {
        if (flowContext != null)
        {
            FlowStep step = flowContext.getStep();
            if (step != null)
            {
                String s = step.getStepId();

                if (s != null)
                    return Long.valueOf(s);
            }
        }

        return null;
    }

    public Flow getFlow() throws Exception
    {
        if (flowContext != null)
            return flowContext.getFlow();

        Integer flowId = getFlowId();
        if (flowId != null)
            return FlowApi.getFlow(flowId);

        return null;
    }

    public FlowNode getNode() throws Exception
    {
        if (flowContext != null)
            return flowContext.getNode();

        return null;
    }

    public FlowInstance getFlowInstance()
    {
        if (flowContext != null)
            return flowContext.getInstance();

        return null;
    }

    public FlowStep getFlowStep()
    {
        if (flowContext != null)
            return flowContext.getStep();

        return null;
    }

    public Long getBodyId()
    {
        if (formContext != null)
            return formContext.getBodyId();

        return null;
    }

    public FormData getFormData()
    {
        if (formContext != null)
            return formContext.getFormData();

        return null;
    }

    public WebForm getForm() throws Exception
    {
        if (formContext != null)
            return formContext.getForm();

        Integer formId = getFormId();
        if (formId != null)
            return FormApi.getForm(formId);

        return null;
    }

    public void setFormData(String name, Object value)
    {
        if (formDatas == null)
            formDatas = new HashMap<String, Object>();

        formDatas.put(name, value);
    }

    protected void initForm(SystemFormContext formContext) throws Exception
    {
        if (formDatas != null)
            formContext.getFormData().setValues(formDatas);

        if (cloneBodyId != null)
        {
            SystemFormContext cloneFormContext = FormApi.getFormContext(cloneBodyId);

            FormApi.copyForm(cloneFormContext, formContext, false);
        }
    }

    protected FlowInstance createFlowInstance() throws Exception
    {
        return new FlowInstance();
    }

    protected void initFlowInstance(FlowInstance instance) throws Exception
    {
        instance.setCreator(businessContext.getUserId().toString());
        instance.setCreatorName(businessContext.getUserName());
        FlowApi.setTest(instance, isTest());
        FlowApi.setDeptId(instance, businessContext.getBusinessDeptId());
        instance.setTitle(title);
    }

    protected void initScriptContext(FlowContext flowContext) throws Exception
    {
        ScriptContext scriptContext = flowContext.getScriptContext();

        scriptContext.addVariableContainer(new MapVariableContainer(getBusinessContext()));
        if (formContext != null)
        {
            scriptContext.addVariableContainer(new MapVariableContainer(getFormContext().getContext()));
        }
    }

    protected void initFlowContext(FlowContext flowContext) throws Exception
    {
    }

    /**
     * 发起流程
     *
     * @throws Exception 发起流程错误
     */
    @Transactional
    public void start() throws Exception
    {
        Integer formId = getFormId();

        Long bodyId = null;
        if (formId != null)
        {
            //绑定了表单，需要创建表单数据
            formContext = FormApi.newFormContext(formId);
            formContext.setBusinessContext(businessContext);

            initForm(formContext);

            formContext.save();

            bodyId = formContext.getBodyId();
        }

        //创建流程实例
        FlowInstance instance = createFlowInstance();
        instance.setFlowId(getFlowId().toString());
        instance.setFlowTag(getFlowTag());

        initFlowInstance(instance);

        FlowApi.setBodyId(instance, bodyId);
        flowContext = FlowApi.newContext(instance, getSystemFlowDao());

        initScriptContext(flowContext);

        //标识流程实例和步骤的状态为-1，在手工保存之前不生效
        instance.setState(-1);
        flowContext.getStep().setState(-1);
        flowContext.getStep().setProperty("deptId", businessContext.getDeptId());
        FlowNode node = flowContext.getNode();
        if (formContext != null)
        {
            //初始化表单角色
            String roles = null;

            if (node != null)
                roles = node.getProperty("role", flowContext.getScriptContext());

            FormRole role;

            if (roles == null)
            {
                role = formContext.getForm().createDefaultRole();
            }
            else
            {
                role = formContext.getForm().getRole(roles.split(","));
            }

            formContext.setRole(role);
        }

        if ("&".equals(flowContext.getStep().getReceiver()))
        {
            flowContext.getStep().setReceiver(instance.getCreator());
            flowContext.getStep().setProperty("deptId", businessContext.getDeptId());
        }

        initFlowContext(flowContext);

        flowContext.saveData();

        if (formContext != null)
            formContext.save();
    }
}
