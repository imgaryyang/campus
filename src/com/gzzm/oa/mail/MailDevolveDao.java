package com.gzzm.oa.mail;

import net.cyan.thunwind.annotation.OQLUpdate;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.Date;

/**
 * 邮件移交相关的dao
 *
 * @author camel
 * @date 2015/9/14
 */
public abstract class MailDevolveDao extends GeneralDao
{
    public MailDevolveDao()
    {
    }

    @OQLUpdate("update Mail set userId=:2 where userId=:1 and sendTime>=?3 and sendTime<?4")
    public abstract void devolve(Integer fromUserId, Integer toUserId, Date startTime, Date endTime) throws Exception;
}
