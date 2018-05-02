package com.gzzm.portal.survey;

import com.gzzm.platform.organ.User;
import com.gzzm.portal.cms.station.Station;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 调查项目信息持久类，记录调查的标题，创建人、创建时间、调查开始时间、调查结束时间、状态、调查说明
 *
 * @author steq
 * @date 2011-6-16
 */
@Entity(table = "PLSURVEY", keys = "surveyId")
public class Survey
{
    /**
     * 主键
     */
    @Generatable(length = 7)
    private Integer surveyId;

    /**
     * 标题
     */
    @Require
    @ColumnDescription(type = "varchar(250)")
    private String surveyName;

    /**
     * 调查创建人
     */
    @Require
    private Integer creator;

    /**
     * 创建时间
     */
    @ColumnDescription(nullable = false)
    private Date createdTime;

    /**
     * 调查开始时间
     */
    @Require
    @ColumnDescription(nullable = false)
    private Date startTime;

    /**
     * 调查结束时间
     */
    @ColumnDescription(nullable = false)
    private Date endTime;

    /**
     * 状态
     * 0为未发布
     * 1为发布
     */
    @Require
    @ColumnDescription(defaultValue = "0")
    private SurveyState state;

    /**
     * 调查说明
     */
    @ColumnDescription(type = "varchar(4000)")
    private String remark;

    /**
     * 每个IP可连续参与某个调查的最多参与次数
     */
    @ColumnDescription(type = "number(6)")
    private Integer limitCount;

    /**
     * 每个IP可连续参与某个调查的最短时间间隔，单位分钟
     */
    @ColumnDescription(type = "number(6)")
    private Integer limitTime;

    private Integer typeId;

    private SurveyType type;

    @ToOne("CREATOR")
    @NotSerialized
    private User user;

    @Index
    @Require
    private Integer stationId;

    @NotSerialized
    private Station station;

    @NotSerialized
    @OneToMany
    @OrderBy(column = "ORDERID")
    private List<SurveyQuestion> questions;

    public Survey()
    {
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public Date getCreatedTime()
    {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime)
    {
        this.createdTime = createdTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public SurveyState getState()
    {
        return state;
    }

    public void setState(SurveyState state)
    {
        this.state = state;
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

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Integer getLimitCount()
    {
        return limitCount;
    }

    public void setLimitCount(Integer limitCount)
    {
        this.limitCount = limitCount;
    }

    public Integer getLimitTime()
    {
        return limitTime;
    }

    public void setLimitTime(Integer limitTime)
    {
        this.limitTime = limitTime;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public SurveyType getType()
    {
        return type;
    }

    public void setType(SurveyType type)
    {
        this.type = type;
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public Station getStation()
    {
        return station;
    }

    public void setStation(Station station)
    {
        this.station = station;
    }

    public List<SurveyQuestion> getQuestions()
    {
        return questions;
    }

    public void setQuestions(List<SurveyQuestion> questions)
    {
        this.questions = questions;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Survey))
            return false;

        Survey survey = (Survey) o;

        return surveyId.equals(survey.surveyId);
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

    public Survey cloneSurvey()
    {
        Survey survey = new Survey();

        survey.setSurveyName(surveyName);
        survey.setStartTime(startTime);
        survey.setEndTime(endTime);
        survey.setRemark(remark);
        survey.setLimitCount(limitCount);
        survey.setLimitTime(limitTime);

        List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();

        for (SurveyQuestion question : getQuestions())
        {
            questions.add(question.cloneQuestion());
        }

        survey.setQuestions(questions);

        return survey;
    }
}
