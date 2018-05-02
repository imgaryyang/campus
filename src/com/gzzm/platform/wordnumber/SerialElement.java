package com.gzzm.platform.wordnumber;

import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 流水号
 *
 * @author camel
 * @date 11-10-27
 */
public class SerialElement implements WordNumberElement
{
    @Inject
    private static Provider<WordNumberDao> daoProvider;

    /**
     * 流水号的名称
     */
    private String name;

    /**
     * 流水号的长度，如果不够长度前面用0补齐，长度为0表示不补齐，长度小于0表示使用中文格式
     */
    private int length;

    private int year = -1;

    public SerialElement()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public void parse(String s) throws Exception
    {
        String[] ss = s.split(",");

        if (ss.length > 0)
            name = ss[0].trim();
        if (ss.length > 1)
            length = Integer.parseInt(ss[1].trim());
        if (ss.length > 2)
            year = Integer.parseInt(ss[2].trim());
    }

    public String save()
    {
        String s = "$" + getType() + "(" + name + "," + length;

        if (year >= 0)
            s += "," + year;

        s += ")";

        return s;
    }

    public String getName(WordNumber wordNumber) throws Exception
    {
        String name = this.name;
        Date time = wordNumber.getTime();

        if (StringUtils.isEmpty(name))
        {
            StringBuilder buffer = new StringBuilder();
            for (WordNumberElement element : wordNumber.getElements())
            {
                if (!(element instanceof SerialElement) && !(element instanceof YearElement))
                    buffer.append(element.getResult(wordNumber));
            }

            name = buffer.toString();
        }
        else
        {
            if (name.indexOf("{month}") > 0)
            {
                name = name.replace("{month}", DateUtils.toString(time, "MM"));
            }

            if (name.indexOf("{date}") > 0)
            {
                name = name.replace("{date}", DateUtils.toString(time, "MMdd"));
            }
        }

        return name;
    }

    public String getResult(WordNumber wordNumber) throws Exception
    {
        String name = getName(wordNumber);
        int year = this.year;
        if (year < 0)
            year = DateUtils.getYear(wordNumber.getTime());

        int value = daoProvider.get().getSerialValue(wordNumber.getType(), wordNumber.getDeptId(),
                year, name);

        //中文
        if (length < 0)
            return Chinese.numberToChinese(value, 0);

        String s = Integer.toString(value);
        s = StringUtils.leftPad(s, length, '0');

        return s;
    }

    public String getType()
    {
        return "serial";
    }

    public String getTypeName()
    {
        return "流水号";
    }

    public String getPattern()
    {
        if (length < 0)
        {
            return ".*";
        }
        else
        {
            StringBuilder buffer = new StringBuilder();

            for (int i = 0; i < length; i++)
            {
                buffer.append("[0-9]");
            }

            if (length == 0)
                buffer.append("[0-9]+");
            else
                buffer.append("[0-9]*");

            return buffer.toString();
        }
    }

    @Override
    public String toString()
    {
        return "流水号";
    }

    public SerialMatch match(String s, WordNumber wordNumber) throws Exception
    {
        String name = getName(wordNumber);
        String type = wordNumber.getType();
        Integer deptId = wordNumber.getDeptId();
        int year = this.year;
        if (year < 0)
            year = DateUtils.getYear(wordNumber.getTime());

        SerialMatch serialMatch = new SerialMatch();

        serialMatch.setDeptId(deptId);
        serialMatch.setName(name);
        serialMatch.setYear(year);
        serialMatch.setType(type);

        Serial serial = daoProvider.get().getSerial(type, deptId, year, name);

        if (serial == null)
            serialMatch.setValue1(0);
        else
            serialMatch.setValue1(serial.getSerialValue() - 1);

        if (length < 0)
            serialMatch.setValue2((int) Chinese.parseChineseToLong(s));
        else
            serialMatch.setValue2(Integer.valueOf(s));

        return serialMatch;
    }
}
