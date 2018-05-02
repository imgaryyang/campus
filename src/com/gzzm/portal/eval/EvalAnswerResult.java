package com.gzzm.portal.eval;

import net.cyan.thunwind.annotation.*;

/**
 * @author sjy
 * @date 2018/2/27
 */
@Entity(table = "PLEVALANSWERRESULT", keys = {"recordId","answerId"})
public class EvalAnswerResult
{
    private Long recordId;

    @ToOne("RECORDID")
    private EvalRecord record;

    private Integer answerId;

    @ToOne("ANSWERID")
    private EvalAnswer answer;

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Integer getAnswerId()
    {
        return answerId;
    }

    public void setAnswerId(Integer answerId)
    {
        this.answerId = answerId;
    }

    public EvalAnswer getAnswer()
    {
        return answer;
    }

    public void setAnswer(EvalAnswer answer)
    {
        this.answer = answer;
    }

    public EvalRecord getRecord()
    {
        return record;
    }

    public void setRecord(EvalRecord record)
    {
        this.record = record;
    }
}
