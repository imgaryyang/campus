package com.gzzm.platform.flow;

import com.gzzm.platform.organ.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;

/**
 * @author camel
 * @date 2010-9-21
 */
public final class SystemNodeReceiverGroup implements NodeReceiverGroup
{
    @Inject
    private static Provider<OrganDao> organDaoProvider;

    /**
     * 接收者并行处理方式
     */
    public final String PARALLEL = "parallel";

    /**
     * 不并行处理，所有接收者竞争上岗
     */
    public final String PARALLEL_NONE = "none";

    /**
     * 用户并行，所有用户并行处理
     */
    public final String PARALLEL_USER = "user";

    /**
     * 部门并行,不同部门的用户并行处理，同一个部门的用户竞争上岗
     */
    public final String PARALLEL_DEPT = "dept";

    public static final SystemNodeReceiverGroup INSTANCE = new SystemNodeReceiverGroup();

    private SystemNodeReceiverGroup()
    {
    }

    public boolean isInSomeGroup(NodeReceiver receiver1, NodeReceiver receiver2, FlowNode node,
                                 ScriptContext scriptContext) throws Exception
    {
        Object parallel = node.getProperty(PARALLEL);

        if (parallel instanceof Script)
        {
            scriptContext.setExtraVariable("receiver1", receiver1);
            scriptContext.setExtraVariable("receiver2", receiver2);

            Object result = ScriptExecutor
                    .execute((Script) parallel, scriptContext, scriptContext.getService().getFlow().getScripts());

            return DataConvert.toBoolean(result);
        }
        else
        {
            if (PARALLEL_NONE.equals(parallel))
            {
                return true;
            }
            else if (PARALLEL_DEPT.equals(parallel))
            {
                //部门相同为同一组，并行处理

                Integer deptId1 = getDeptId(receiver1);
                if (deptId1 == null)
                    return false;

                Integer deptId2 = getDeptId(receiver2);

                return deptId2 != null && deptId1.equals(deptId2);

            }
            else
            {
                return false;
            }
        }
    }

    public Integer getDeptId(NodeReceiver receiver) throws Exception
    {
        Integer deptId = DataConvert.convertType(Integer.class, receiver.getProperty("deptId"));
        if (deptId == null)
        {
            String receiverValue = receiver.getReceiver();
            if (receiverValue.indexOf('@') < 0 && receiverValue.indexOf('#') < 0 && receiverValue.indexOf('#') < 0)
            {
                User user = organDaoProvider.get().getUser(Integer.valueOf(receiverValue));

                for (Dept dept : user.getDepts())
                {
                    if (dept.getDeptId() != 1)
                    {
                        deptId = dept.getDeptId();
                        break;
                    }
                }

                if (deptId == null)
                    deptId = user.getDepts().get(0).getDeptId();

                receiver.setProperty("deptId", deptId);
            }
        }

        return deptId;
    }
}
