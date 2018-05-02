package com.gzzm.platform.commons.log;

import net.cyan.commons.log.Printable;

/**
 * @author camel
 * @date 2014/9/10
 */
public class SmsPrintable implements Printable
{
    private StringBuilder buffer = new StringBuilder();

    private SmsPrintableFactory factory;

    public SmsPrintable(SmsPrintableFactory factory)
    {
        this.factory = factory;
    }

    @Override
    public void println(String s)
    {
        buffer.append(s).append("\n");
    }

    @Override
    public void flush()
    {
        String s = buffer.toString();

        if (s.startsWith("[error]"))
        {
            factory.setError();
        }
    }

    @Override
    public String toString()
    {
        return buffer.toString();
    }
}
