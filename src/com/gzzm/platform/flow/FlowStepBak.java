package com.gzzm.platform.flow;

import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 2014/9/2
 */
@Entity(table = "PFFLOWSTEPBAK", keys = {"stepId", "preStepId"})
public class FlowStepBak extends SystemFlowStep
{
    public FlowStepBak()
    {
    }
}
