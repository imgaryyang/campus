package com.gzzm.platform.wordnumber;

import net.cyan.commons.util.DateUtils;

/**
 * 年号
 *
 * @author camel
 * @date 11-10-27
 */
public class YearElement implements WordNumberElement
{
    public YearElement()
    {
    }

    public void parse(String s) throws Exception
    {
    }

    public String getResult(WordNumber wordNumber) throws Exception
    {
        return Integer.toString(DateUtils.getYear(wordNumber.getTime()));
    }

    public String getType()
    {
        return "year";
    }

    public String save()
    {
        return "$" + getType() + "()";
    }

    public String getTypeName()
    {
        return "年号";
    }

    public String getPattern()
    {
        return "[1-2][0-9]{3}";
    }

    @Override
    public String toString()
    {
        return "年号";
    }
}
