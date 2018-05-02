package com.gzzm.portal.olconsult;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;
import net.cyan.thunwind.annotation.ToOne;

import java.util.Date;

/**
 * 在线咨询
 *
 * @author camel
 * @date 13-5-30
 */
@Entity(table = "PLOLCONSULT", keys = "consultId")
public class OlConsult
{
    @Generatable(length = 9)
    private Integer consultId;

    private Integer typeId;

    @NotSerialized
    private OlConsultType type;

    private Integer satisfactionId;

    @ColumnDescription(type = "varchar2(200)")
    private String satisfactionRemark;

    @NotSerialized
    private OlConsultSatisfaction olConsultSatisfaction;

    /**
     * 发起咨询的时间
     */
    private Date applyTime;

    /**
     * 开始咨询的时间
     */
    private Date startTime;

    /**
     * 结束咨询的时间
     */
    private Date endTime;

    @NotSerialized
    private OlConsultState state;

    @ColumnDescription(type = "varchar(25)")
    private String userName;

    @ColumnDescription(type = "varchar(50)")
    private String browserIp;

    private Integer seatId;

    @NotSerialized
    private OlConsultSeat seat;

    private Integer answerUserId;

    @ToOne("ANSWERUSERID")
    private User anserUser;

    public OlConsult()
    {
    }

    public Integer getConsultId()
    {
        return consultId;
    }

    public void setConsultId(Integer consultId)
    {
        this.consultId = consultId;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public OlConsultType getType()
    {
        return type;
    }

    public void setType(OlConsultType type)
    {
        this.type = type;
    }

    public Date getApplyTime()
    {
        return applyTime;
    }

    public void setApplyTime(Date applyTime)
    {
        this.applyTime = applyTime;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public OlConsultState getState()
    {
        return state;
    }

    public void setState(OlConsultState state)
    {
        this.state = state;
    }

    public String getBrowserIp()
    {
        return browserIp;
    }

    public void setBrowserIp(String browserIp)
    {
        this.browserIp = browserIp;
    }

    public Integer getSeatId()
    {
        return seatId;
    }

    public void setSeatId(Integer seatId)
    {
        this.seatId = seatId;
    }

    public Integer getAnswerUserId()
    {
        return answerUserId;
    }

    public void setAnswerUserId(Integer answerUserId)
    {
        this.answerUserId = answerUserId;
    }

    public User getAnserUser()
    {
        return anserUser;
    }

    public void setAnserUser(User anserUser)
    {
        this.anserUser = anserUser;
    }

    public OlConsultSeat getSeat()
    {
        return seat;
    }

    public void setSeat(OlConsultSeat seat)
    {
        this.seat = seat;
    }

    public Integer getSatisfactionId() {
        return satisfactionId;
    }

    public void setSatisfactionId(Integer satisfactionId) {
        this.satisfactionId = satisfactionId;
    }

    public OlConsultSatisfaction getOlConsultSatisfaction() {
        return olConsultSatisfaction;
    }

    public void setOlConsultSatisfaction(OlConsultSatisfaction olConsultSatisfaction) {
        this.olConsultSatisfaction = olConsultSatisfaction;
    }

    public String getSatisfactionRemark() {
        return satisfactionRemark;
    }

    public void setSatisfactionRemark(String satisfactionRemark) {
        this.satisfactionRemark = satisfactionRemark;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof OlConsult))
            return false;

        OlConsult olConsult = (OlConsult) o;

        return consultId.equals(olConsult.consultId);
    }

    @Override
    public int hashCode()
    {
        return consultId.hashCode();
    }
}
