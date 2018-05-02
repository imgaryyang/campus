package com.gzzm.ods.flow;

/**
 * @author camel
 * @date 12-11-19
 */
public class PrintInfo
{
    private Integer templateId;

    private String showName;

    private String formName;

    public PrintInfo()
    {
    }

    public PrintInfo(Integer templateId, String showName, String formName)
    {
        this.templateId = templateId;
        this.showName = showName;
        this.formName = formName;
    }

    public Integer getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Integer templateId)
    {
        this.templateId = templateId;
    }

    public String getShowName()
    {
        return showName;
    }

    public void setShowName(String showName)
    {
        this.showName = showName;
    }

    public String getFormName()
    {
        return formName;
    }

    public void setFormName(String formName)
    {
        this.formName = formName;
    }
}
