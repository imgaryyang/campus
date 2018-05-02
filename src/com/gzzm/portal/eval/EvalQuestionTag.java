package com.gzzm.portal.eval;

import com.gzzm.portal.annotation.*;
import com.gzzm.portal.tag.*;

/**
 * @author sjy
 * @date 2018/2/28
 */
@Tag(name = "evalQuestionTag")
public class EvalQuestionTag extends EntityQueryTag<EvalQuestion,Integer>
{
    private Integer stationId;

    private TargetType type;

    @Override
    protected String getQueryString() throws Exception
    {
        return "select t from com.gzzm.portal.eval.EvalQuestion t where t.type.type=?type and t.type.stationId=?stationId order by t.orderId";
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    public TargetType getType()
    {
        return type;
    }

    public void setType(TargetType type)
    {
        this.type = type;
    }
}
