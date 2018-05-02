package com.gzzm.platform.flow;

import net.cyan.thunwind.annotation.*;
import net.cyan.valmiki.flow.dao.FlowContextEntity;

/**
 * 流程上下文对象，保存流程流转过程中的变量
 *
 * @author camel
 * @date 2010-9-14
 */
@Entity(table = "PFFLOWCONTEXT", keys = "instanceId")
public class SystemFlowContext extends FlowContextEntity
{
    public SystemFlowContext()
    {
    }

    @Override
    @ColumnDescription(type = "number(12)")
    public Long getInstanceId()
    {
        return super.getInstanceId();
    }
}
