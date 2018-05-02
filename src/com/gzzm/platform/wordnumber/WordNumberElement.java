package com.gzzm.platform.wordnumber;

/**
 * 序列号中的每个元素
 *
 * @author camel
 * @date 2011-6-22
 */
public interface WordNumberElement
{
    public void parse(String s) throws Exception;

    public String getResult(WordNumber wordNumber) throws Exception;

    public String toString();

    public String getType();

    public String getTypeName();

    public String getPattern();

    public String save();
}