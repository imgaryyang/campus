package com.gzzm.platform.message.sms;

import net.cyan.nest.annotation.Injectable;

/**
 * 短消息配置
 *
 * @author camel
 * @date 2010-6-3
 */
@Injectable(singleton = true)
public class SmsConfig
{
    /**
     * 开始发消息的时间，以小时为单位，默认早上8点钟之后才发消息
     */
    private int startHour = 8;

    /**
     * 结束发消息的时间，以小时为单位，默认晚上11点之后不发消息
     */
    private int endHour = 23;

    public SmsConfig()
    {
    }

    public int getStartHour()
    {
        return startHour;
    }

    public void setStartHour(int startHour)
    {
        this.startHour = startHour;
    }

    public int getEndHour()
    {
        return endHour;
    }

    public void setEndHour(int endHour)
    {
        this.endHour = endHour;
    }
}
