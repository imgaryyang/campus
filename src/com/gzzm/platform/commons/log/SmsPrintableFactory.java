package com.gzzm.platform.commons.log;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.log.*;
import net.cyan.commons.util.*;
import com.gzzm.platform.message.Message;

/**
 * @author camel
 * @date 2014/9/10
 */
public class SmsPrintableFactory implements PrintableProvider
{
    private String phone;

    private String message;

    private String cron = "0 0 8-20 * * ? *";

    private boolean error;

    private boolean inited;

    public SmsPrintableFactory()
    {
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getCron()
    {
        return cron;
    }

    public void setCron(String cron)
    {
        this.cron = cron;
    }

    @Override
    public Printable getPrintable()
    {
        return new SmsPrintable(this);
    }

    synchronized void setError()
    {
        if (!inited)
        {
            inited = true;
            try
            {
                final String[] phones = this.phone.split(",");
                Jobs.addJob(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        boolean error;
                        synchronized (SmsPrintableFactory.this)
                        {
                            error = SmsPrintableFactory.this.error;
                            SmsPrintableFactory.this.error = false;
                        }

                        if (error)
                        {
                            for (String phone : phones)
                            {
                                Message message = new Message();
                                message.setMessage(SmsPrintableFactory.this.message);
                                message.setPhone(phone);
                                message.setForce(true);
                                message.send();
                            }
                        }
                    }
                }, cron);
            }
            catch (Throwable ex)
            {
                Tools.wrapException(ex);
            }
        }

        error = true;
    }
}
