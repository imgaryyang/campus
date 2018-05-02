package com.gzzm.in;

import com.gzzm.platform.commons.NoErrorException;
import net.cyan.arachne.RequestContext;

import java.util.Collection;

/**
 * @author camel
 * @date 2016/12/3
 */
public class InterfaceDeptCheck
{
    public InterfaceDeptCheck()
    {
    }

    public static void checkDeptId(Integer deptId, Collection<Integer> deptIds) throws Exception
    {
        String s = "interface.noauth";

        if (deptId == null)
            throw new NoErrorException(s);

        if (deptIds != null)
        {
            if (!deptIds.contains(deptId))
            {
                throw new NoErrorException(s);
            }
        }
    }

    public static void checkDeptId(Integer deptId) throws Exception
    {
        checkDeptId(deptId, InterfaceDeptIdsFactory.getDeptIds(RequestContext.getContext().getRequest()));
    }
}
