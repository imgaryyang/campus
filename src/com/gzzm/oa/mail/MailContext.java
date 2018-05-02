package com.gzzm.oa.mail;

import net.cyan.commons.log.LogManager;
import net.cyan.commons.util.Provider;
import net.cyan.mail.MailDao;
import net.cyan.mail.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 邮件上下文
 *
 * @author camel
 * @date 2010-4-15
 */
public class MailContext
{
    @Inject
    private static Provider<SystemMailDao> daoProvider;

    @Inject
    private static Provider<MailContext> contextProvider;

    private static MailContext context;


    private MailServer server;

    /**
     * 邮件服务器域名列表
     */
    private List<String> domains;


    private String domain;

    private String exchanger;

    /**
     * 服务器的名称
     */
    private String serverName = "gzzm mail server";

    /**
     * 是否支持smtp服务
     */
    private boolean smtp;

    /**
     * 是否支持pop3服务
     */
    private boolean pop3;

    private Map<String, String> mxs;

    public MailContext()
    {
    }

    public static synchronized MailContext getContext()
    {
        if (context == null)
            context = contextProvider.get();

        return context;
    }

    public String getServerName()
    {
        return serverName;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public boolean isSmtp()
    {
        return smtp;
    }

    public void setSmtp(boolean smtp)
    {
        this.smtp = smtp;
    }

    public boolean isPop3()
    {
        return pop3;
    }

    public void setPop3(boolean pop3)
    {
        this.pop3 = pop3;
    }

    public List<String> getDomains()
    {
        return domains;
    }

    public void setDomains(List<String> domains)
    {
        this.domains = domains;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    /**
     * 服务器的主域名，即域名列表中第一个
     *
     * @return 主域名
     */
    public String getDomain()
    {
        if (domain == null)
            return domains == null || domains.size() == 0 ? null : domains.get(0);
        else
            return domain;
    }

    public String getExchanger()
    {
        return exchanger;
    }

    public void setExchanger(String exchanger)
    {
        this.exchanger = exchanger;
    }

    public Map<String, String> getMxs()
    {
        return mxs;
    }

    public void setMxs(Map<String, String> mxs)
    {
        this.mxs = mxs;
    }

    public synchronized MailServer getServer()
    {
        if (server == null)
        {
            server = new MailServer();
            server.setDaoFactory(new MailDaoFactory()
            {
                public MailDao getDao() throws Exception
                {
                    return daoProvider.get();
                }
            });

            server.setDomains(domains);
            server.setExchanger(exchanger);
            server.setServerName(serverName);
            server.setLog(LogManager.getLog("com.gzzm.mail"));

            if (mxs != null)
            {
                for (Map.Entry<String, String> entry : mxs.entrySet())
                {
                    Mxs.setServers(entry.getKey(), Arrays.asList(entry.getValue().split(",")));
                }
            }
        }

        return server;
    }
}
