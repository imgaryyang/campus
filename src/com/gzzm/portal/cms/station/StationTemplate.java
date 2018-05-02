package com.gzzm.portal.cms.station;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.annotation.Unique;
import net.cyan.thunwind.annotation.*;

/**
 * 网站模板
 *
 * @author camel
 * @date 2011-5-3
 */
@Entity(table = "PLSTATIONTEMPLATE", keys = "templateId")
public class StationTemplate
{
    /**
     * ，模板ID
     */
    @Generatable(length = 6)
    private Integer templateId;

    /**
     * 模板名称
     */
    @Require
    @Unique
    @ColumnDescription(type = "varchar(250)")
    private String templateName;

    /**
     * 模板路径
     */
    @Require
    @Unique
    @ColumnDescription(type = "varchar(250)")
    private String path;

    /**
     * 排序ID，用于对模板进行手工排序，以友好的显示
     */
    private Integer orderId;

    /**
     * 模板创建人的ID
     */
    private Integer creator;

    /**
     * 关联模板创建人的User对象
     */
    @NotSerialized
    private User createUser;


    /**
     * 主页路径
     */
    @ColumnDescription(type = "varchar(250)")
    private String homePath;

    /**
     * 通用栏目模板路径
     */
    @ColumnDescription(type = "varchar(250)")
    private String channelPath;

    /**
     * 通用文章模板路径
     */
    @ColumnDescription(type = "varchar(250)")
    private String textPath;

    public StationTemplate()
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

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(User createUser)
    {
        this.createUser = createUser;
    }

    public String getHomePath()
    {
        return homePath;
    }

    public void setHomePath(String homePath)
    {
        this.homePath = homePath;
    }

    public String getChannelPath()
    {
        return channelPath;
    }

    public void setChannelPath(String channelPath)
    {
        this.channelPath = channelPath;
    }

    public String getTextPath()
    {
        return textPath;
    }

    public void setTextPath(String textPath)
    {
        this.textPath = textPath;
    }

    @Override
    public String toString()
    {
        return templateName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof StationTemplate))
            return false;

        StationTemplate that = (StationTemplate) o;

        return templateId.equals(that.templateId);
    }

    @Override
    public int hashCode()
    {
        return templateId.hashCode();
    }
}
