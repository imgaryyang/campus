package com.gzzm.platform.form;

import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.valmiki.form.FormComponent;

/**
 * @author camel
 * @date 2015/1/14
 */
public class FormComponentInfo
{
    private String page;

    @NotSerialized
    private FormComponent component;

    public FormComponentInfo()
    {
    }

    public FormComponentInfo(String page, FormComponent component)
    {
        this.page = page;
        this.component = component;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public FormComponent getComponent()
    {
        return component;
    }

    public void setComponent(FormComponent component)
    {
        this.component = component;
    }
}
