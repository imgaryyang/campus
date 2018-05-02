package com.gzzm.safecampus.campus.device;

import com.gzzm.platform.organ.User;
import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.classes.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;

/**
 * 招行小白卡
 *
 * @author yiuman
 * @date 2018/3/14
 */
@Entity(table = "SCDEVICECARD", keys = "cardId")
public class DeviceCard extends BaseBean
{
    /**
     * id
     */
    @Generatable(length = 6)
    private Integer cardId;

    /**
     * 卡号
     */
    @ColumnDescription(type = "varchar(50)")
    private String cardNo;


    @NotSerialized
    @ToOne("TARGETID")
    private Student student;
    /**
     * 学生id或者教师id根据校园卡类型
     */
    private Integer targetId;

    @NotSerialized
    @ToOne("TEACHERID")
    private Teacher teacher;


    private Integer schoolId;

    @NotSerialized
    @ToOne("SCHOOLID")
    private School school;

    /**
     * 类型
     */
    @ColumnDescription(type = "number(1)")
    private CardType type;

    /**
     * 状态
     */
    @ColumnDescription(type = "number(1)")
    private CardStatus status;

    /**
     * 发卡时间
     */
    private Date releaseTime;

    /**
     * 过期时间
     */
    private Date expireTime;


    /**
     * 卡序列号
     */
    @ColumnDescription(type = "VARCHAR2(20)")
    private String cardSn;
    /**
     * 退卡人
     */
    private Integer returnOperation;

    @ToOne("RETURNOPERATION")
    private User returnUser;


    public Integer getCardId()
    {
        return cardId;
    }

    public void setCardId(Integer cardId)
    {
        this.cardId = cardId;
    }

    public String getCardNo()
    {
        return cardNo;
    }

    public void setCardNo(String cardNo)
    {
        this.cardNo = cardNo;
    }

    public CardType getType()
    {
        return type;
    }

    public void setType(CardType type)
    {
        this.type = type;
    }

    public CardStatus getStatus()
    {
        return status;
    }

    public void setStatus(CardStatus status)
    {
        this.status = status;
    }

    public Date getReleaseTime()
    {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime)
    {
        this.releaseTime = releaseTime;
    }

    public Date getExpireTime()
    {
        return expireTime;
    }

    public void setExpireTime(Date expireTime)
    {
        this.expireTime = expireTime;
    }


    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }


    public Teacher getTeacher()
    {
        return teacher;
    }

    public void setTeacher(Teacher teacher)
    {
        this.teacher = teacher;
    }

    public Integer getSchoolId()
    {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId)
    {
        this.schoolId = schoolId;
    }

    public School getSchool()
    {
        return school;
    }

    public void setSchool(School school)
    {
        this.school = school;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof DeviceCard)) return false;

        DeviceCard that = (DeviceCard) o;

        return cardId != null ? cardId.equals(that.cardId) : that.cardId == null;
    }

    @Override
    public int hashCode()
    {
        return cardId != null ? cardId.hashCode() : 0;
    }

    public Integer getTargetId()
    {
        return targetId;
    }

    public void setTargetId(Integer targetId)
    {
        this.targetId = targetId;
    }

    public String getCardSn()
    {
        return cardSn;
    }

    public void setCardSn(String cardSn)
    {
        this.cardSn = cardSn;
    }

    public Integer getReturnOperation()
    {
        return returnOperation;
    }

    public void setReturnOperation(Integer returnOperation)
    {
        this.returnOperation = returnOperation;
    }

    public User getReturnUser()
    {
        return returnUser;
    }

    public void setReturnUser(User returnUser)
    {
        this.returnUser = returnUser;
    }

}
