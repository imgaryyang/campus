package com.gzzm.portal.eval;

import com.gzzm.portal.commons.PortalUtils;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.*;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * @author sjy
 * @date 2018/2/26
 */
@Service
public class EvalService
{
    @Inject
    private EvalDao dao;

    private Long objectId;

    private TargetType targetType;

    private Integer optionId;

    private String remark;

    private String userName;

    private String phone;

    private List<Integer> answerIds;

    @Service(url = "/web/portal/evalAnswer", method = HttpMethod.all)
    @Transactional
    @ObjectResult
    public String answer() throws Exception
    {
        if (objectId != null && targetType != null && answerIds != null && answerIds.size() > 0)
        {
            EvalRecord record = new EvalRecord();
            record.setEvalType(targetType);
            record.setObjectId(objectId);
            record.setRemark(remark);
            record.setIp(PortalUtils.getIp(RequestContext.getContext().getRequest()));
            record.setEvalTime(new Date());
            record.setUserName(userName);
            record.setPhone(phone);
            dao.save(record);
            for (Integer answerId : answerIds)
            {
                EvalAnswerResult answerResult = new EvalAnswerResult();
                answerResult.setRecordId(record.getRecordId());
                answerResult.setAnswerId(answerId);
                dao.save(answerResult);
            }
            return "ok";
        }
        return "error";
    }

    /**
     * 评价
     *
     * @return
     * @throws Exception
     */
    @Service(url = "/web/portal/eval", method = HttpMethod.all)
    @Transactional
    @ObjectResult
    public String eval() throws Exception
    {

        EvalRecord temp = dao.queryEvalRecordByIp(RequestContext.getContext().getRequest().getRemoteAddr(), objectId);
        if (temp != null)
        {
            return "completed";
        }


        if (objectId != null && targetType != null && optionId != null)
        {
            EvalRecord record = new EvalRecord();
            record.setEvalType(targetType);
            record.setObjectId(objectId);
            record.setOptionId(optionId);
            record.setRemark(remark);
            record.setIp(PortalUtils.getIp(RequestContext.getContext().getRequest()));
            record.setEvalTime(new Date());
            record.setUserName(userName);
            record.setPhone(phone);
            dao.save(record);
            return "ok";
        }
        return "error";
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    public TargetType getTargetType()
    {
        return targetType;
    }

    public void setTargetType(TargetType targetType)
    {
        this.targetType = targetType;
    }

    public Integer getOptionId()
    {
        return optionId;
    }

    public void setOptionId(Integer optionId)
    {
        this.optionId = optionId;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
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

    public List<Integer> getAnswerIds()
    {
        return answerIds;
    }

    public void setAnswerIds(List<Integer> answerIds)
    {
        this.answerIds = answerIds;
    }
}
