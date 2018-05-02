package com.gzzm.ods.flow;

import com.gzzm.platform.flow.FlowComponent;

/**
 * 内部流程组件，为一个流程增加扩展逻辑
 *
 * @author camel
 * @date 11-12-6
 */
public interface OdFlowComponent extends FlowComponent<OdFlowContext>
{
    /**
     * 删除流程时的动作
     *
     * @param instance 要删除的流程实例
     * @throws Exception 允许实现类抛出错误
     */
    public void deleteFlow(OdFlowInstance instance) throws Exception;

    /**
     * 办结流程时的动作
     *
     * @param instance 要办结的流程实例
     * @throws Exception 允许实现类抛出错误
     */
    public void stopFlow(OdFlowInstance instance) throws Exception;
}