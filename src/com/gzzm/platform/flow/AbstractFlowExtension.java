package com.gzzm.platform.flow;

import com.gzzm.platform.commons.BusinessContext;
import com.gzzm.platform.form.SystemFormContext;
import com.gzzm.platform.organ.UserInfo;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.FormContext;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author camel
 * @date 2014/8/27
 */
public abstract class AbstractFlowExtension implements FlowExtension
{
    protected FlowComponentContext context;

    public AbstractFlowExtension()
    {
    }

    protected FlowComponentContext getContext()
    {
        return context;
    }

    @NotSerialized
    public FlowContext getFlowContext() throws Exception
    {
        return context.getFlowContext();
    }

    @NotSerialized
    public SystemFormContext getFormContext() throws Exception
    {
        return context.getFormContext();
    }

    protected SystemFormContext getFormContext(String name) throws Exception
    {
        return context.getFormContext(name);
    }

    protected FlowPage getFlowPage() throws Exception
    {
        return context.getFlowPage();
    }

    @NotSerialized
    public Long getInstanceId() throws Exception
    {
        return getContext().getInstanceId();
    }

    @NotSerialized
    public Long getStepId() throws Exception
    {
        return getContext().getStepId();
    }

    @NotSerialized
    public String getNodeId() throws Exception
    {
        return getFlowContext().getStep().getNodeId();
    }

    @NotSerialized
    public boolean isEditable() throws Exception
    {
        return getFlowContext().isEditable();
    }

    @NotSerialized
    public UserInfo getUserInfo() throws Exception
    {
        return getContext().getUserInfo();
    }

    @NotSerialized
    public Integer getUserId() throws Exception
    {
        return getUserInfo().getUserId();
    }

    @NotSerialized
    public List<Action> getActions() throws Exception
    {
        return getFlowPage().getActions();
    }

    @NotSerialized
    public int getState() throws Exception
    {
        return getFlowContext().getStep().getState();
    }

    @NotSerialized
    public BusinessContext getBusinessContext() throws Exception
    {
        return getContext().getBusinessContext();
    }

    @NotSerialized
    public Integer getBusinessDeptId() throws Exception
    {
        return getBusinessContext().getBusinessDeptId();
    }

    @Override

    public void initActions(List<Action> actions, FlowComponentContext context) throws Exception
    {
        this.context = context;
    }

    @Override
    public void init(FlowComponentContext context) throws Exception
    {
        this.context = context;
    }

    @Override
    public void initForm(FormContext formContext, FlowComponentContext context) throws Exception
    {
        this.context = context;
    }

    @Override
    public void beforeShow(FlowComponentContext context) throws Exception
    {
    }

    @Override
    public void initExtension(FlowComponentContext context) throws Exception
    {
        this.context = context;
        fill();
    }

    @Override
    public void extractData(FlowComponentContext context) throws Exception
    {
        this.context = context;
    }

    @Override
    public void saveData(FlowComponentContext context) throws Exception
    {
        this.context = context;
    }

    @Override
    public void beforeSend(FlowComponentContext context, Map<String, List<List<NodeReceiver>>> receiverMap)
            throws Exception
    {
        this.context = context;
    }

    @Override
    public void afterSend(FlowComponentContext context, Map<String, List<List<NodeReceiver>>> receiverMap)
            throws Exception
    {
        this.context = context;
    }

    @Override
    public void beforeEndFlow(FlowComponentContext context) throws Exception
    {
        this.context = context;
    }

    @Override
    public void endFlow(FlowComponentContext context) throws Exception
    {
        this.context = context;
    }

    @Override
    public void beforeStopFlow(FlowComponentContext context) throws Exception
    {
        this.context = context;
    }

    @Override
    public void stopFlow(FlowComponentContext context) throws Exception
    {
        this.context = context;
    }

    @Override
    public boolean accept(Action action, FlowComponentContext context) throws Exception
    {
        return true;
    }

    @Override
    public Object execute(String actionId, Object[] args, FlowComponentContext context) throws Exception
    {
        this.context = context;

        return execute(actionId, args);
    }

    protected Object execute(String actionId, Object[] args) throws Exception
    {
        Method actionMethod = getMethod(actionId);

        if (actionMethod != null)
        {
            Type[] parameterTypes = actionMethod.getGenericParameterTypes();

            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameters.length; i++)
            {
                if (args.length > i)
                {
                    parameters[i] = DataConvert.convertToType(parameterTypes[i], args[i]);
                }
            }

            return actionMethod.invoke(this, parameters);
        }

        return null;
    }

    @Override
    public String forward(String forward, FlowComponentContext context) throws Exception
    {
        this.context = context;

        return forward(forward);
    }

    protected String forward(String forward) throws Exception
    {
        Method method = null;
        try
        {
            method = getClass().getMethod(forward);
        }
        catch (NoSuchMethodException ex)
        {
            //没有此forward方法，跳过
        }

        if (method != null && method.getReturnType() == String.class)
            return (String) method.invoke(this);

        return null;
    }

    @Override
    public InputFile down(String file, FlowComponentContext context) throws Exception
    {
        this.context = context;

        return down(file);
    }

    protected InputFile down(String file) throws Exception
    {
        Method method = null;
        try
        {
            method = getClass().getMethod(file);
        }
        catch (NoSuchMethodException ex)
        {
            //没有此forward方法，跳过
        }

        if (method != null)
        {
            Class<?> type = method.getReturnType();

            if (type == InputFile.class || type == byte[].class || type == InputStream.class || type == File.class ||
                    type == Inputable.class)
            {
                Object ret = method.invoke(this);

                if (ret instanceof File)
                {
                    ret = new InputFile((File) ret);
                }
                else if (!(ret instanceof InputFile))
                {
                    ret = new InputFile(InputFile.toInputable(ret), file);
                }

                return (InputFile) ret;
            }
        }

        return null;
    }

    protected Method getMethod(String name) throws Exception
    {
        try
        {
            return BeanUtils.searchPublicMethod(getClass(), name);
        }
        catch (Throwable ex)
        {
            //寻找方法没成功，跳过
        }

        return null;
    }

    protected void fill() throws Exception
    {
        RequestContext requestContext = RequestContext.getContext();
        if (requestContext != null)
        {
            requestContext.fillForm(this, null);
        }
    }

    @Override
    @NotSerialized
    public String getJsFile()
    {
        return null;
    }
}
