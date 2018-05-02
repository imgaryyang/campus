package com.gzzm.platform.desktop;

/**
 * @author camel
 * @date 2010-6-14
 */
public class PendingCount
{
    private PendingItem item;

    private int count;

    public PendingCount(PendingItem item, int count)
    {
        this.item = item;
        this.count = count;
    }

    public PendingItem getItem()
    {
        return item;
    }

    public int getCount()
    {
        return count;
    }
}
