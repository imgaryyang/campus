package com.gzzm.platform.message.sms;

import com.gzzm.platform.organ.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

/**
 * @author camel
 * @date 13-8-15
 */
@Entity(table = "PFSMSTEMPLATE", keys = "templateId")
public class SmsTemplate
{
    @Generatable(length = 9)
    private Integer templateId;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String templateName;

    @Require
    @ColumnDescription(type = "varchar(4000)")
    private String content;

    private Integer userId;

    private User user;

    /**
     * 所属部门，当短信模板属于部门时为所属部门ID，否则为空
     */
    private Integer deptId;

    private Dept dept;

    public SmsTemplate()
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

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Dept getDept()
    {
        return dept;
    }

    public void setDept(Dept dept)
    {
        this.dept = dept;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SmsTemplate))
            return false;

        SmsTemplate that = (SmsTemplate) o;

        return templateId.equals(that.templateId);
    }

    @Override
    public int hashCode()
    {
        return templateId.hashCode();
    }
}
