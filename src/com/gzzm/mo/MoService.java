package com.gzzm.mo;

import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2014/5/14
 */
public class MoService
{
    @Inject
    private MoDao dao;

    public MoService()
    {
    }

    public MoDao getDao()
    {
        return dao;
    }
}