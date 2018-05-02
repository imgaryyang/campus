package com.gzzm.portal.cms.information;

import net.cyan.thunwind.annotation.*;

/**
 * 信息采编之间的关系
 * @author sjy
 * @date 2018/1/29
 */
@Entity(table = "PLINFORMATIONEDITRELATED",keys = {"informationId","otherInformationId"})
public class InformationEditRelated
{
    private Long informationId;

    private Long otherInformationId;

    public Long getInformationId()
    {
        return informationId;
    }

    public void setInformationId(Long informationId)
    {
        this.informationId = informationId;
    }

    public Long getOtherInformationId()
    {
        return otherInformationId;
    }

    public void setOtherInformationId(Long otherInformationId)
    {
        this.otherInformationId = otherInformationId;
    }
}
