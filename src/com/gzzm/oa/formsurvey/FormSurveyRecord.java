package com.gzzm.oa.formsurvey;

import com.gzzm.platform.form.FormBody;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.util.Date;

/**
 * 调研记录，即每个部门填写的结果
 *
 * @author camel
 * @date 13-4-23
 */
@Entity(table = "OAFORMSURVEYRECORD", keys = "recordId")
public class FormSurveyRecord
{
    @Generatable(length = 9)
    private Integer recordId;

    /**
     * 关联的调研信息
     */
    private Integer surveyId;

    private FormSurvey survey;

    /**
     * 标题，从表单提取，提取规则由FormSurvey的title字段定义
     *
     * @see FormSurvey#title
     */
    @ColumnDescription(type = "varchar(250)")
    private String title;

    /**
     * 记录哪个部门上报的数据
     */
    private Integer deptId;

    @NotSerialized
    private Dept dept;

    /**
     * 用户ID，记录哪个用户上报的数据
     */
    private Integer userId;

    @NotSerialized
    private User user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 状态，审核未审核
     */
    private RecordState state;

    /**
     * 关联的表单数据对象
     */
    private Long bodyId;

    @NotSerialized
    private FormBody body;

    @ColumnDescription(type = "varchar(600)")
    private String remark;

    /**
     * 关联的数据的主键，有时候我们进行调研之前会把一些历史数据倒过来调研表，这时候我们希望这些调研数据和原来的数据能够匹配上
     */
    @ColumnDescription(type = "varchar(25)")
    private String linkId;

    @ColumnDescription(type = "number(1)")
    private Integer deleteTag;

    /**
     * 审核事件
     */
    private Date auditTime;

    @ColumnDescription(type = "number(1)", defaultValue = "0")
    private Integer exportState;

    public FormSurveyRecord()
    {
    }

    public Integer getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Integer recordId)
    {
        this.recordId = recordId;
    }

    public Integer getSurveyId()
    {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId)
    {
        this.surveyId = surveyId;
    }

    public FormSurvey getSurvey()
    {
        return survey;
    }

    public void setSurvey(FormSurvey survey)
    {
        this.survey = survey;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public RecordState getState()
    {
        return state;
    }

    public void setState(RecordState state)
    {
        this.state = state;
    }

    public Long getBodyId()
    {
        return bodyId;
    }

    public void setBodyId(Long bodyId)
    {
        this.bodyId = bodyId;
    }

    public FormBody getBody()
    {
        return body;
    }

    public void setBody(FormBody body)
    {
        this.body = body;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getLinkId()
    {
        return linkId;
    }

    public void setLinkId(String linkId)
    {
        this.linkId = linkId;
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    public Date getAuditTime()
    {
        return auditTime;
    }

    public void setAuditTime(Date auditTime)
    {
        this.auditTime = auditTime;
    }

    public Integer getExportState()
    {
        return exportState;
    }

    public void setExportState(Integer exportState)
    {
        this.exportState = exportState;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof FormSurveyRecord))
            return false;

        FormSurveyRecord that = (FormSurveyRecord) o;

        return recordId.equals(that.recordId);
    }

    @Override
    public int hashCode()
    {
        return recordId.hashCode();
    }

    @Override
    public String toString()
    {
        return title;
    }
}
