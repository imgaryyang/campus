package com.gzzm.platform.wordnumber;

import net.cyan.commons.util.DataConvert;

/**
 * 提取变量
 *
 * @author camel
 * @date 13-11-6
 */
public class VarElement implements WordNumberElement
{
    private String name;

    public VarElement()
    {
    }

    public VarElement(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void parse(String s) throws Exception
    {
        name = s.trim();
    }

    public String getResult(WordNumber wordNumber) throws Exception
    {
        return DataConvert.toString(wordNumber.getProperty(name));
    }

    public String getType()
    {
        return "var";
    }

    public String getTypeName()
    {
        return "变量";
    }

    public String getPattern()
    {
        return ".+";
    }

    public String save()
    {
        return "$" + getType() + "(" + name + ")";
    }

    @Override
    public String toString()
    {
        return "{" + name + "}";
    }
}
