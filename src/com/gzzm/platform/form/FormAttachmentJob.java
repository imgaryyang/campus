package com.gzzm.platform.form;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2017/12/30
 */
public class FormAttachmentJob implements Runnable
{
    @Inject
    private Provider<FormAttachmentService> serviceProvider;

    public FormAttachmentJob()
    {
    }

    @Override
    public void run()
    {
        try
        {
            serviceProvider.get().makeFormAttachments();
        }
        catch (Exception ex)
        {
            Tools.log(ex);
        }
    }
}
