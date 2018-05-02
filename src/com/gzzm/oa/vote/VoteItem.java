package com.gzzm.oa.vote;

import java.util.Date;

/**
 * @author camel
 * @date 12-4-12
 */
public class VoteItem
{
    private Integer recordId;

    private Date voteTime;

    private String userName;

    private String deptName;

    /**
     * 得分
     */
    private Integer score;

    public VoteItem()
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

    public Date getVoteTime()
    {
        return voteTime;
    }

    public void setVoteTime(Date voteTime)
    {
        this.voteTime = voteTime;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public Integer getScore()
    {
        return score;
    }

    public void setScore(Integer score)
    {
        this.score = score;
    }
}
