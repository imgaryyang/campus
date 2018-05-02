package com.gzzm.ods.redhead;

import net.cyan.commons.util.Value;

/**
 * 保存在发文时选择的红头模版和红头标题的信息
 *
 * @author camel
 * @date 11-9-25
 */
public class RedHeadTitleInfo implements Value<Integer>
{
    private static final long serialVersionUID = 3661926707182192864L;

    private Integer redHeadId;

    private String redHeadName;

    private Integer[] titleIds;

    public RedHeadTitleInfo()
    {
    }

    public Integer getRedHeadId()
    {
        return redHeadId;
    }

    public void setRedHeadId(Integer redHeadId)
    {
        this.redHeadId = redHeadId;
    }

    public String getRedHeadName()
    {
        return redHeadName;
    }

    public void setRedHeadName(String redHeadName)
    {
        this.redHeadName = redHeadName;
    }

    public Integer[] getTitleIds()
    {
        return titleIds;
    }

    public void setTitleIds(Integer[] titleIds)
    {
        this.titleIds = titleIds;
    }

    public Integer valueOf()
    {
        return redHeadId;
    }

    @Override
    public String toString()
    {
        return redHeadName;
    }
}
