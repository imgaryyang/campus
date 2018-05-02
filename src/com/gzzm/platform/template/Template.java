package com.gzzm.platform.template;

import com.gzzm.platform.menu.MenuContainer;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.*;

import java.io.InputStream;
import java.util.Date;

/**
 * @author camel
 * @date 2011-4-21
 */
@Entity(table = "PFTEMPLATE", keys = "templateId")
public class Template
{
    @Inject
    private static Provider<MenuContainer> menuContainerProvider;

    /**
     * 模版ID
     */
    @Generatable(length = 6)
    private Integer templateId;

    /**
     * 模版名称，中文名
     */
    @Require
    @ColumnDescription(type = "varchar(250)", nullable = false)
    private String templateName;

    /**
     * 导出文件的文件名
     */
    @ColumnDescription(type = "varchar(250)")
    private String fileName;

    /**
     * 模版对应的应用
     */
    @Index
    @Require
    @ColumnDescription(type = "varchar(50)", nullable = false)
    private String appId;

    /**
     * 模版类型，即模版的用途
     */
    @Require
    private TemplateType type;

    /**
     * 模版的内容
     */
    private InputStream content;

    /**
     * 模版最后修改时间
     */
    private Date lastTime;

    /**
     * 条件，满足此条件时使用此模板，如果为空表示任何情况都满足
     */
    @ColumnDescription(type = "varchar(250)")
    private String condition;

    @ColumnDescription(type = "varchar(50)")
    private String format;

    @ColumnDescription(type = "varchar(250)")
    private String templateFileName;

    public Template()
    {
    }

    public Integer getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Integer templateId)
    {
        this.templateId = templateId;
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public TemplateType getType()
    {
        return type;
    }

    public void setType(TemplateType type)
    {
        this.type = type;
    }

    public InputStream getContent()
    {
        return content;
    }

    public void setContent(InputStream content)
    {
        this.content = content;
    }

    public Date getLastTime()
    {
        return lastTime;
    }

    public void setLastTime(Date lastTime)
    {
        this.lastTime = lastTime;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public String getTemplateFileName()
    {
        return templateFileName;
    }

    public void setTemplateFileName(String templateFileName)
    {
        this.templateFileName = templateFileName;
    }

    @NotSerialized
    public String getMenuTitle()
    {
        if (StringUtils.isEmpty(appId))
            return "";
        else
            return menuContainerProvider.get().getMenu(appId).getTitle();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Template))
            return false;

        Template that = (Template) o;

        return templateId.equals(that.templateId);
    }

    @Override
    public int hashCode()
    {
        return templateId.hashCode();
    }

    @BeforeAdd
    @BeforeUpdate
    public void beforeModify()
    {
        lastTime = new Date();
    }
}
