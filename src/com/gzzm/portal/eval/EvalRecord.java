package com.gzzm.portal.eval;

import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * @author sjy
 * @date 2018/2/26
 */
@Entity(table = "PLINQUIRYEVALRECORD", keys = "recordId")
public class EvalRecord
{
    @Generatable(length = 11)
    private Long recordId;

    /**
     * 评价类型
     */
    private TargetType evalType;

    private Long objectId;

    private Integer optionId;

    @ToOne("OPTIONID")
    private EvalOption option;

    /**
     * 评价时间
     */
    private Date evalTime;

    @ColumnDescription(type = "varchar(2000)")
    private String remark;

    @ColumnDescription(type = "varchar(25)")
    private String ip;

    @ColumnDescription(type = "varchar(50)")
    private String userName;

    @ColumnDescription(type = "varchar(12)")
    private String phone;

    @ManyToMany(table = "PLEVALANSWERRESULT")
    private List<EvalAnswer> answerResults;

    public List<EvalAnswer> getAnswerResults()
    {
        return answerResults;
    }

    public void setAnswerResults(List<EvalAnswer> answerResults)
    {
        this.answerResults = answerResults;
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public TargetType getEvalType()
    {
        return evalType;
    }

    public void setEvalType(TargetType evalType)
    {
        this.evalType = evalType;
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    public Integer getOptionId()
    {
        return optionId;
    }

    public void setOptionId(Integer optionId)
    {
        this.optionId = optionId;
    }

    public EvalOption getOption()
    {
        return option;
    }

    public void setOption(EvalOption option)
    {
        this.option = option;
    }

    public Date getEvalTime()
    {
        return evalTime;
    }

    public void setEvalTime(Date evalTime)
    {
        this.evalTime = evalTime;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof EvalRecord))
        {
            return false;
        }

        EvalRecord that = (EvalRecord) o;

        return recordId.equals(that.recordId);

    }

    @Override
    public int hashCode()
    {
        return recordId.hashCode();
    }
}
