package com.gzzm.safecampus.campus.base;

import net.cyan.commons.util.DataConvert;
import net.cyan.crud.CrudConfig;
import net.cyan.crud.util.CrudBeanUtils;
import net.cyan.crud.view.components.AbstractComponent;

/**
 * @author Neo
 * @date 2018/4/10 11:21
 */
public class CHourMinute extends AbstractComponent<CHourMinute>
{
    private String hour;

    private String minute;

    private String hourFormat;

    private String minuteFormat;

    private String hourWidth;

    private String minuteWidth;

    private boolean nullable;

    public CHourMinute(String hour, String minute)
    {
        this.hour = hour;
        this.minute = minute;
    }

    public String getMinute()
    {
        return minute;
    }

    public String getHour()
    {
        return hour;
    }

    public String getHourFormat()
    {
        return hourFormat;
    }

    public String getMinuteFormat()
    {
        return minuteFormat;
    }

    public String getHourWidth()
    {
        return hourWidth;
    }

    public String getMinuteWidth()
    {
        return minuteWidth;
    }

    public boolean isNullable()
    {
        return nullable;
    }

    public CHourMinute setFormat(String hourFormat, String minuteFormat)
    {
        this.hourFormat = hourFormat;
        this.minuteFormat = minuteFormat;
        return this;
    }

    public CHourMinute setWidth(String hourWidth, String minuteWidth)
    {
        this.hourWidth = hourWidth;
        this.minuteWidth = minuteWidth;
        return this;
    }

    public CHourMinute setNullable(boolean nullable)
    {
        this.nullable = nullable;
        return this;
    }

    public String getHourExpression(String expression)
    {
        if (this.hour == null)
            return null;

        if (this.hour.startsWith("this."))
            return this.hour.substring(5);

        return expression == null ? this.hour : expression + "." + this.hour;
    }

    public String getMinuteExpression(String expression)
    {
        if (this.minute == null)
            return null;

        if (this.minute.startsWith("this."))
            return this.minute.substring(5);

        return expression == null ? this.minute : expression + "." + this.minute;
    }

    public Integer getHourValue(Object obj) throws Exception
    {
        return getValue(obj, hour);
    }

    public Integer getMinuteValue(Object obj) throws Exception
    {
        return getValue(obj, minute);
    }

    protected Integer getValue(Object obj, String expression) throws Exception
    {
        if (expression != null)
        {
            try
            {
                if (expression.startsWith("this."))
                    return DataConvert.convertType(Integer.class,
                            CrudBeanUtils.eval(expression.substring(5), CrudConfig.getContext().getCrud()));
                else
                    return DataConvert.convertType(Integer.class, CrudBeanUtils.eval(expression, obj));
            } catch (Exception ex)
            {
                //表达式不合法
            }
        }
        return null;
    }
}
