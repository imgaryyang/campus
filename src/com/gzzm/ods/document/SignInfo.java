package com.gzzm.ods.document;

import java.util.Date;

/**
 * @author camel
 * @date 2015/5/22
 */
public class SignInfo
{
    private String signer;

    private Date signTime;

    public SignInfo()
    {
    }

    public SignInfo(String signer, Date signTime)
    {
        this.signer = signer;
        this.signTime = signTime;
    }

    public String getSigner()
    {
        return signer;
    }

    public void setSigner(String signer)
    {
        this.signer = signer;
    }

    public Date getSignTime()
    {
        return signTime;
    }

    public void setSignTime(Date signTime)
    {
        this.signTime = signTime;
    }
}
