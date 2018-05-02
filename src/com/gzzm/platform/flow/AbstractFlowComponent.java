package com.gzzm.platform.flow;

import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.FormContext;

import java.util.*;

/**
 * @author camel
 * @date 2014/9/18
 */
public abstract class AbstractFlowComponent<C extends FlowComponentContext> implements FlowComponent<C>
{
    public AbstractFlowComponent()
    {
    }

    @Override
    public void init(C context) throws Exception
    {
    }

    @Override
    public void initForm(FormContext formContext, C context) throws Exception
    {
    }

    @Override
    public void beforeShow(C context) throws Exception
    {
    }

    @Override
    public void extractData(C context) throws Exception
    {
    }

    @Override
    public void saveData(C context) throws Exception
    {
    }

    @Override
    public void beforeSend(C context, Map<String, List<List<NodeReceiver>>> receiverMap) throws Exception
    {
    }

    @Override
    public void afterSend(C context, Map<String, List<List<NodeReceiver>>> receiverMap) throws Exception
    {
    }

    @Override
    public void beforeEndFlow(C context) throws Exception
    {
    }

    @Override
    public void endFlow(C context) throws Exception
    {
    }

    @Override
    public void beforeStopFlow(C context) throws Exception
    {
    }

    @Override
    public void stopFlow(C context) throws Exception
    {
    }

    @Override
    public String getJsFile()
    {
        return null;
    }

    @Override
    public boolean accept(Action action, FlowComponentContext context)
    {
        return true;
    }
}
