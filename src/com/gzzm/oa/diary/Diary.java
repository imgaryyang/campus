package com.gzzm.oa.diary;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

import java.sql.Date;

/**
 * @author wxj
 * @date 11-10-27
 */
@Entity(table = "OADIARY", keys = "diaryId")
public class Diary
{
    /**
     * 日志ID
     */
    @Generatable(length = 9)
    private Integer diaryId;

    /**
     * 日志时间
     */
    @Require
    private Date diaryTime;

    /**
     * 日志填写时间
     */
    @Require
    private java.util.Date createTime;

    /**
     * 用户，对应用户表中的userId
     */
    @Index
    private Integer userId;

    /**
     * 用户，关联User对象
     */
    @NotSerialized
    private User user;

    /**
     * 日志标题
     */
    @Require
    @ColumnDescription(type = "varchar(400)")
    private String title;

    /**
     * 日志内容
     */
    @ColumnDescription(type = "varchar(4000)")
    private String content;

    /**
     * 工作量
     */
    @Require
    @MaxVal("99")
    @MinVal("0")
    @ColumnDescription(type = "number(2)")
    private Integer amount;

    public Diary()
    {
    }

    public Integer getDiaryId()
    {
        return diaryId;
    }

    public void setDiaryId(Integer diaryId)
    {
        this.diaryId = diaryId;
    }

    public Date getDiaryTime()
    {
        return diaryTime;
    }

    public void setDiaryTime(Date diaryTime)
    {
        this.diaryTime = diaryTime;
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

    public String getContent()
    {

        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public java.util.Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime)
    {
        this.createTime = createTime;
    }

    public Integer getAmount()
    {
        return amount;
    }

    public void setAmount(Integer amount)
    {
        this.amount = amount;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Diary))
            return false;

        Diary diary = (Diary) o;

        return diaryId.equals(diary.diaryId);
    }

    @Override
    public int hashCode()
    {
        return diaryId.hashCode();
    }
}