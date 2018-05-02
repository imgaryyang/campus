package com.gzzm.platform.flow;

import net.cyan.commons.util.InputFile;
import net.cyan.valmiki.flow.Action;

import java.util.List;

/**
 * @author camel
 * @date 2014/8/26
 */
public interface FlowExtension extends FlowComponent<FlowComponentContext>
{
    public void initActions(List<Action> actions, FlowComponentContext context) throws Exception;

    public String forward(String forward, FlowComponentContext context) throws Exception;

    public Object execute(String actionId, Object[] args, FlowComponentContext context) throws Exception;

    public InputFile down(String file, FlowComponentContext context) throws Exception;

    public void initExtension(FlowComponentContext context) throws Exception;
}