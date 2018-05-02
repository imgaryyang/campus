package com.gzzm.ods.in;

import com.gzzm.in.InterfaceResult;

import java.util.List;

/**
 * @author camel
 * @date 2016/12/3
 */
public class ReceiveList extends InterfaceResult
{
    private List<ReceiveInfo> receives;

    public ReceiveList()
    {
    }

    public List<ReceiveInfo> getReceives()
    {
        return receives;
    }

    public void setReceives(List<ReceiveInfo> receives)
    {
        this.receives = receives;
    }
}
