package com.gzzm.safecampus.campus.pay;

import java.util.*;

/**
 * @author yuanfang
 * @date 18-04-25 11:05
 */
public class paymentTypeConfig
{
    private Set<String> types;

    public paymentTypeConfig()
    {
    }

    public Set<String> getTypes()
    {
        return types;
    }

    public void setTypes(Set<String> types)
    {
        this.types = types;
    }
}
