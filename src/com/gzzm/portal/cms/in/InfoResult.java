package com.gzzm.portal.cms.in;

import com.gzzm.in.BooleanResult;

/**
 * @author Xrd
 * @date 2017/7/4 16:35
 */
public class InfoResult extends BooleanResult
{
    private String error;

    private Long infoId;

    public Long getInfoId()
    {
        return infoId;
    }

    public void setInfoId(Long infoId)
    {
        this.infoId = infoId;
    }

    @Override
    public String getError()
    {
        return error;
    }

    @Override
    public void setError(String error)
    {
        this.error = error;
    }
}
