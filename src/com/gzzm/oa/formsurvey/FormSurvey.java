package com.gzzm.oa.formsurvey;

import com.gzzm.platform.form.*;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.io.InputStream;
import java.util.Date;

/**
 * 表单调研，定义一个通过表单定制所定制出来的调研
 *
 * @author camel
 * @date 13-4-23
 */
@Entity(table = "OAFORMSURVEY", keys = "surveyId")
public class FormSurvey
{
    @Generatable(length = 6)
    private Integer surveyId;

    @Require
    @ColumnDescription(type = "varchar(250)")
    private String surveyName;

    private Integer formId;

    /**
     * 定义怎么从表单正提取标题，为一个el表达式，表达式上引用的变量为表单中的组件
     */
    @ColumnDescription(type = "varchar(50)")
    private String title;

    /**
     * 标题在界面上显示的名字，如事项名称等
     */
    @ColumnDescription(type = "varchar(25)")
    private String titleName;

    private Integer creator;

    @NotSerialized
    @ToOne("CREATOR")
    private User user;

    private Date createTime;

    /**
     * 打印模板
     */
    private InputStream printTemplate;

    /**
     * 打印模板的最后修改时间
     */
    private Date lastModified;

    /**
     * javascript路径
     */
    @ColumnDescription(type = "varchar(250)")
    private String jsPath;

    @Require
    private Boolean valid;

    @ColumnDescription(type = "number(6)")
    private Integer orderId;

    public FormSurvey()
    {
    }

    public Integer getSurveyId()
    {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId)
    {
        this.surveyId = surveyId;
    }

    public String getSurveyName()
    {
        return surveyName;
    }

    public void setSurveyName(String surveyName)
    {
        this.surveyName = surveyName;
    }

    public Integer getFormId()
    {
        return formId;
    }

    public void setFormId(Integer formId)
    {
        this.formId = formId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitleName()
    {
        return titleName;
    }

    public void setTitleName(String titleName)
    {
        this.titleName = titleName;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public InputStream getPrintTemplate()
    {
        return printTemplate;
    }

    public void setPrintTemplate(InputStream printTemplate)
    {
        this.printTemplate = printTemplate;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    public String getJsPath()
    {
        return jsPath;
    }

    public void setJsPath(String jsPath)
    {
        this.jsPath = jsPath;
    }

    public Boolean getValid()
    {
        return valid;
    }

    public void setValid(Boolean valid)
    {
        this.valid = valid;
    }

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @NotSerialized
    public FormInfo getForm() throws Exception
    {
        if (formId != null)
            return FormApi.getLastForm(formId);
        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof FormSurvey))
            return false;

        FormSurvey that = (FormSurvey) o;

        return surveyId.equals(that.surveyId);
    }

    @Override
    public int hashCode()
    {
        return surveyId.hashCode();
    }

    @Override
    public String toString()
    {
        return surveyName;
    }
}
