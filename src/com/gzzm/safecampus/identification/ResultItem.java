package com.gzzm.safecampus.identification;

/**
 * @author zy
 * @date 2018/3/29 15:08
 */
public class ResultItem
{
    /**
     * 行为标识
     */
    private Integer actionId;

    /**
     * 置信度0-100
     */
    private Integer confidence;

    /**
     * 用户主键 陌生人stranger  没有人脸noface
     */
    private String personId;

    public ResultItem()
    {
    }

    public Integer getActionId()
    {
        return actionId;
    }

    public void setActionId(Integer actionId)
    {
        this.actionId = actionId;
    }

    public Integer getConfidence()
    {
        return confidence;
    }

    public void setConfidence(Integer confidence)
    {
        this.confidence = confidence;
    }

    public String getPersonId()
    {
        return personId;
    }

    public void setPersonId(String personId)
    {
        this.personId = personId;
    }
}
