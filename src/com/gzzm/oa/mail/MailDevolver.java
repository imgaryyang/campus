package com.gzzm.oa.mail;

import com.gzzm.platform.devolve.Devolver;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * @author camel
 * @date 2015/9/14
 */
public class MailDevolver implements Devolver
{
    @Inject
    private static Provider<MailDevolveDao> daoProvider;

    public MailDevolver()
    {
    }

    @Override
    public void devolve(Integer fromUserId, Integer toUserId, Date startTime, Date endTime) throws Exception
    {
        daoProvider.get().devolve(fromUserId, toUserId, startTime, endTime);
    }
}
