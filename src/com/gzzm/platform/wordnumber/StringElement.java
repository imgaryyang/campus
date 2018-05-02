package com.gzzm.platform.wordnumber;

import net.cyan.commons.util.StringUtils;

/**
 * 静态字符串
 *
 * @author camel
 * @date 11-10-27
 */
public class StringElement implements WordNumberElement
{
    private String text;

    public StringElement(String text)
    {
        this.text = text;
    }

    public StringElement()
    {
    }

    public void parse(String s) throws Exception
    {
        text = s;
    }

    public String getResult(WordNumber wordNumber) throws Exception
    {
        return text;
    }

    public String getText()
    {
        return text;
    }

    public String getType()
    {
        return "string";
    }

    public String save()
    {
        return text;
    }

    public String getTypeName()
    {
        return "字符";
    }

    public String getPattern()
    {
        return StringUtils.pattern(text);
    }

    @Override
    public String toString()
    {
        return text;
    }
}
