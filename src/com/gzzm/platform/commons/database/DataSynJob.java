package com.gzzm.platform.commons.database;

import com.gzzm.platform.commons.Tools;

/**
 * @author camel
 * @date 2014/11/20
 */
public class DataSynJob implements Runnable
{
    private String source;

    private String target;

    public DataSynJob()
    {
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    @Override
    public void run()
    {
        try
        {
            new DataSynService(source, target).synAllTablesAndData();
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }
}
