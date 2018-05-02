package com.gzzm.in;

import com.gzzm.platform.organ.Dept;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * @author camel
 * @date 13-12-17
 */
public abstract class InterfaceDao extends GeneralDao
{
    public InterfaceDao()
    {
    }

    @GetByKey
    public abstract Dept getDept(Integer deptId);

    @GetByField("loginName")
    public abstract InterfaceUser getUserByLoginName(String loginName) throws Exception;
}
