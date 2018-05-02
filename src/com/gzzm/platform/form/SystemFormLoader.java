package com.gzzm.platform.form;

import net.cyan.valmiki.form.xml.serialize.XmlFormLoader;

import java.io.*;
import java.util.Date;

/**
 * @author camel
 * @date 11-9-14
 */
public class SystemFormLoader extends XmlFormLoader
{
    public static final SystemFormLoader INSTANCE = new SystemFormLoader();

    private SystemFormLoader()
    {
        super(FormApi.XML_SERIALIZER_CONTAINER);
    }

    @Override
    protected InputStream getInputStream(String formId) throws Exception
    {
        char[] content = FormApi.getDao().getFormContent(Integer.valueOf(formId));
        if (content == null || content.length == 0)
            return null;

        return new ByteArrayInputStream(new String(content).getBytes("UTF-8"));
    }

    public boolean check(String formId, long loadTime) throws Exception
    {
        Date updateTime = FormApi.getDao().getFormUpdateTime(Integer.valueOf(formId));
        return updateTime == null || updateTime.getTime() > loadTime;
    }
}
