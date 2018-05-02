package com.gzzm.ods.flow;

import com.gzzm.platform.flow.SystemFlowStep;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 2014/8/22
 */
@Entity(table = "PFFLOWSTEP", keys = {"stepId", "preStepId"})
@Indexes({
        @Index(columns = {"RECEIVER", "STATE", "LASTSTEP", "RECEIVETIME"}),
        @Index(columns = {"DEPTID", "RECEIVETIME"})
})
public class OdSystemFlowStep extends SystemFlowStep
{
    public OdSystemFlowStep()
    {
    }
}
