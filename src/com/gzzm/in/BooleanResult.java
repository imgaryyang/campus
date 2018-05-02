package com.gzzm.in;

import net.cyan.commons.annotation.ElementName;

/**
 * @author camel
 * @date 13-12-17
 */
@ElementName("result")
public class BooleanResult extends InterfaceResult
{
    private boolean success;

    public BooleanResult()
    {
    }

    public BooleanResult(boolean success)
    {
        this.success = success;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }
}
