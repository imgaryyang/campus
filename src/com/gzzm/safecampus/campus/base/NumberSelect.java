package com.gzzm.safecampus.campus.base;

import net.cyan.commons.util.DataConvert;
import net.cyan.commons.util.KeyValue;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 数字下拉选择控件生成工具类
 *
 * @author Neo
 * @date 2018/4/10 12:43
 */
public class NumberSelect
{
    public NumberSelect()
    {
    }

    public static List<KeyValue<String>> createHourSelect()
    {
        return createHourSelect(null);
    }

    public static List<KeyValue<String>> createHourSelect(String format)
    {
        return createNumberSelect(0, 23, format);
    }

    public static List<KeyValue<String>> createMinuteSelect()
    {
        return createMinuteSelect(null);
    }

    public static List<KeyValue<String>> createMinuteSelect(String format)
    {
        return createNumberSelect(0, 59, format);
    }

    public static List<KeyValue<String>> createNumberSelect(int start, int end)
    {
        return createNumberSelect(start, end, null);
    }

    public static List<KeyValue<String>> createNumberSelect(int start, int end, String format)
    {
        List<KeyValue<String>> numbers = new ArrayList<>(end - start);
        for (; start <= end; start++)
        {
            String value = StringUtils.isEmpty(format) ? (start + "") : DataConvert.format(start, format);
            numbers.add(new KeyValue<>(start + "", value));
        }
        return numbers;
    }
}
