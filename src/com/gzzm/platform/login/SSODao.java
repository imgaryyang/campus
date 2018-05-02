package com.gzzm.platform.login;

import com.gzzm.platform.organ.*;
import net.cyan.thunwind.annotation.GetByField;

/**
 * 单点登录的数据访问接口
 *
 * @author camel
 * @date 11-11-25
 */
public abstract class SSODao extends OrganDao
{
    public SSODao()
    {
    }

    @GetByField("sourceId")
    public abstract User getUserBySourceId(String sourceId) throws Exception;
}
