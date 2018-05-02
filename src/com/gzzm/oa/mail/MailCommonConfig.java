package com.gzzm.oa.mail;

import com.gzzm.platform.annotation.ConfigValue;

/**
 * 邮件公共配置
 *
 * @author camel
 * @date 2010-4-23
 */
public class MailCommonConfig
{
    private static final long DEFAULTCAPACITY = 1024 * 1024 * 100;

    /**
     * 默认邮箱容量
     */
    @ConfigValue(name = "MAIL_CAPACITY", defaultValue = "" + DEFAULTCAPACITY)
    private Long capacity;

    /**
     * 是否支持smtp服务
     */
    @ConfigValue(name = "MAIL_SMTP", defaultValue = "true")
    private Boolean smtp;

    /**
     * 系统是否支持pop服务
     */
    @ConfigValue(name = "MAIL_POP", defaultValue = "false")
    private Boolean pop;

    public MailCommonConfig()
    {
    }

    public Long getCapacity()
    {
        return capacity;
    }

    public Boolean isSmtp()
    {
        return smtp;
    }

    public void setSmtp(Boolean smtp)
    {
        this.smtp = smtp;
    }

    public Boolean isPop()
    {
        return pop;
    }

    public void setPop(Boolean pop)
    {
        this.pop = pop;
    }
}
