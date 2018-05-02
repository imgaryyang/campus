package com.gzzm.platform.wordnumber;

import net.cyan.commons.util.*;

/**
 * @author camel
 * @date 12-8-7
 */
public class DateElement implements WordNumberElement
{
    private String format;

    public DateElement()
    {
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public void parse(String s) throws Exception
    {
        format = s.trim();
    }

    public String getResult(WordNumber wordNumber) throws Exception
    {
        return DateUtils.toString(wordNumber.getTime(), format);
    }

    public String getType()
    {
        return "date";
    }

    public String save()
    {
        return "$" + getType() + "(" + format + ")";
    }

    public String getTypeName()
    {
        return "日期";
    }

    public String getPattern()
    {
        return StringUtils.pattern(format).replaceAll("[y|M|d]", "[0-9]");
    }

    @Override
    public String toString()
    {
        return "日期";
    }
}
