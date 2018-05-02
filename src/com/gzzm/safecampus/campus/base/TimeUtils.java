package com.gzzm.safecampus.campus.base;

import net.cyan.commons.util.DataConvert;

/**
 * 时间相关工具类
 *
 * @author Neo
 * @date 2018/4/10 13:53
 */
public class TimeUtils
{
    public TimeUtils()
    {
    }

    /**
     * 将小时和分钟格式化成 hh:mm的格式
     *
     * @param hour   小时
     * @param minute 分钟
     * @return 格式后结果
     */
    public static String hourMinuteFormat(Integer hour, Integer minute)
    {
        return DataConvert.format(hour, "00") + ":" + DataConvert.format(minute, "00");
    }
}
