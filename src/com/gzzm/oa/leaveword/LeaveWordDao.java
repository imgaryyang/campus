package com.gzzm.oa.leaveword;

import net.cyan.thunwind.dao.GeneralDao;

/**
 * @author camel
 * @date 13-7-31
 */
public abstract class LeaveWordDao extends GeneralDao
{
    public LeaveWordDao()
    {
    }

    public void lockLeaveWord(Integer leaveWordId) throws Exception
    {
        lock(LeaveWord.class, leaveWordId);
    }

    public LeaveWord getLeaveWord(Integer leaveWordId) throws Exception
    {
        return load(LeaveWord.class, leaveWordId);
    }
}
