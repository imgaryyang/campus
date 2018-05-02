package com.gzzm.oa.mail;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.StringUtils;

/**
 * pop3邮件接收的服务类
 *
 * @author camel
 * @date 2010-6-15
 */
public class PopReceiveService extends MailServiceBase
{
    public PopReceiveService()
    {
    }

    public void receive() throws Exception
    {
        for (Integer accountId : dao.getAllAccountIds())
        {
            try
            {
                receive(accountId);
            }
            catch (Throwable ex)
            {
                //接收一个邮箱失败不影响下一个邮箱的接收
                Tools.log("receive mail from pop3 server fail,accountId:" + accountId, ex);
            }
        }
    }

    public void receive(Integer accountId) throws Exception
    {
        receive(dao.getAccount(accountId));
    }

    public void receive(MailAccount account) throws Exception
    {
        if (!StringUtils.isEmpty(account.getPop3Server()) && getConfig(account.getUserId()).isPop())
        {
            MailContext.getContext().getServer()
                    .receiveMail(account.getUserId().toString(), account.getPop3Server(), -1, account.getUserName(),
                            account.getPassword(), account.getAddress(), false);
        }
    }
}
