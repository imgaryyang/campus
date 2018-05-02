package com.gzzm.ods.bak;

import java.util.Date;

/**
 * @author camel
 * @date 13-9-27
 */
public interface OdBakLisenter
{
    public void init(String bakName, int count);

    public void add(String title, Date time);
}
