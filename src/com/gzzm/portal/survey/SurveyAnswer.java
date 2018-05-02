package com.gzzm.portal.survey;

import com.gzzm.platform.commons.Sex;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 调查回答表:每个回答为一个一条记录，包括回答的时间，ip还有一些其他的基本信息
 *
 * @author wxj
 * @date 2011-6-16
 */
@Entity(table = "PLSURVEYANSWER", keys = "answerId")
public class SurveyAnswer
{
    @Generatable(length = 12)
    private Long answerId;

    /**
     * 调查ID
     */
    @Index
    @ColumnDescription(nullable = false)
    private Integer surveyId;

    @NotSerialized
    private Survey survey;

    /**
     * 参与调查时间
     */
    @Require
    private Date answerTime;

    /**
     * 参与调查者的ip
     */
    @ColumnDescription(type = "varchar(20)", nullable = false)
    private String ip;

    /**
     * 参与调查者姓名
     */
    @ColumnDescription(type = "varchar(20)")
    private String userName;

    /**
     * 参与调查者年龄
     */
    @ColumnDescription(type = "number(3)")
    private Integer age;

    /**
     * 参与调查者性别
     */
    private Sex sex;

    /**
     * 参与调查者联系电话
     */
    @ColumnDescription(type = "varchar(50)")
    private String phone;

    /**
     * 参与调查者地址
     */
    @ColumnDescription(type = "varchar(100)")
    private String address;

    /**
     * 参与调查者职业
     */
    @ColumnDescription(type = "varchar(20)")
    private String occupation;

    /**
     * 参与调查者E-mail
     */
    @ColumnDescription(type = "varchar(50)")
    private String email;

    private Integer userId;

    @NotSerialized
    private User user;

    /**
     * 从调查回答表到调查问题可选答案表多对多关系
     */
    @NotSerialized
    @ManyToMany(table = "PLSURVEYANSWEROPTION")
    private List<SurveyOption> options;

    @OneToMany
    @NotSerialized
    private List<SurveyAnswerItem> items;

    public SurveyAnswer()
    {
    }

    public Long getAnswerId()
    {
        return answerId;
    }

    public void setAnswerId(Long answerId)
    {
        this.answerId = answerId;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public Date getAnswerTime()
    {
        return answerTime;
    }

    public void setAnswerTime(Date answerTime)
    {
        this.answerTime = answerTime;
    }

    public Integer getSurveyId()
    {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId)
    {
        this.surveyId = surveyId;
    }

    public Survey getSurvey()
    {
        return survey;
    }

    public void setSurvey(Survey survey)
    {
        this.survey = survey;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Integer getAge()
    {
        return age;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    public Sex getSex()
    {
        return sex;
    }

    public void setSex(Sex sex)
    {
        this.sex = sex;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getOccupation()
    {
        return occupation;
    }

    public void setOccupation(String occupation)
    {
        this.occupation = occupation;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
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

    public List<SurveyOption> getOptions()
    {
        return options;
    }

    public void setOptions(List<SurveyOption> options)
    {
        this.options = options;
    }

    public List<SurveyAnswerItem> getItems()
    {
        return items;
    }

    public void setItems(List<SurveyAnswerItem> items)
    {
        this.items = items;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof SurveyAnswer))
            return false;

        SurveyAnswer that = (SurveyAnswer) o;

        return answerId.equals(that.answerId);

    }

    @Override
    public int hashCode()
    {
        return answerId.hashCode();
    }
}
