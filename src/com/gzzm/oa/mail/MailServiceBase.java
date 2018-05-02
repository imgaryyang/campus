package com.gzzm.oa.mail;

import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2010-6-18
 */
public class MailServiceBase
{
    @Inject
    private static Provider<MailCommonConfig> commonConfigProvider;

    @Inject
    protected MailDao dao;

    private MailCommonConfig commonConfig;

    public MailServiceBase()
    {
    }

    public MailDao getDao()
    {
        return dao;
    }

    private MailCommonConfig getCommonConfig()
    {
        if (commonConfig == null)
            commonConfig = commonConfigProvider.get();

        return commonConfig;
    }

    public MailConfig getConfig(Integer userId) throws Exception
    {
        MailConfig config = dao.getConfig(userId);
        if (config == null)
            config = new MailConfig();

        if (config.getCapacity() == null)
            config.setCapacity(getCommonConfig().getCapacity());

        if (config.isSmtp() == null)
            config.setSmtp(getCommonConfig().isSmtp());

        if (config.isPop() == null)
            config.setPop(getCommonConfig().isPop());

        return config;
    }
}
