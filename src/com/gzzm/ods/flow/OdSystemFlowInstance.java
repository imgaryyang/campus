package com.gzzm.ods.flow;

import com.gzzm.platform.flow.SystemFlowInstance;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 2014/8/22
 */
@Entity(table = "PFFLOWINSTANCE", keys = "instanceId")
@Indexes({@Index(columns = {"DEPTID", "STARTTIME"})})
public class OdSystemFlowInstance extends SystemFlowInstance
{
    public OdSystemFlowInstance()
    {
    }
}
