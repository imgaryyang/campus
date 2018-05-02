package com.gzzm.ods.urge;

import com.gzzm.ods.flow.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.flow.FlowApi;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * @author camel
 * @date 12-5-3
 */
public class UrgeJob implements Runnable
{
    //延后误差为一个小时，即如果一小时之内的提醒还未提醒，则继续提醒
    public static final long L = 1000 * 60 * 60;

    //提前误差为五分钟
    public static final long R = 1000 * 60 * 5;

    @Inject
    private static Provider<UrgeDao> daoProvider;

    private Long instanceId;

    public UrgeJob(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public void run()
    {
        try
        {
            UrgeDao dao = daoProvider.get();
            OdFlowInstance instance = dao.getInstance(instanceId);

            if (instance == null || instance.getState() != OdFlowInstanceState.unclosed ||
                    instance.isUrged() == null || !instance.isUrged())
            {
                //文件被删除或者文件已经结束，不提醒
                return;
            }

            OdFlowUrge urge = dao.getUrge(instanceId);
            if (urge == null || StringUtils.isBlank(urge.getContent()))
                return;

            Date remindTime = urge.getUrgeTime();
            Date nowTime = new Date();

            long remindTimeValue = remindTime.getTime();
            long nowTimeValue = nowTime.getTime();

            if (urge.getRemindType() == UrgeRemindType.day && nowTimeValue > remindTimeValue + L)
            {
                //每天提醒，将提醒日期设置为当前日期
                remindTimeValue += (nowTimeValue - remindTimeValue) / DateUtils.MILLIS_IN_DAY * DateUtils.MILLIS_IN_DAY;
            }

            if (remindTimeValue > nowTimeValue - L && remindTimeValue < nowTimeValue + R)
            {
                //提醒时间和当前时间在误差范围内
                Date lastRemindTime = urge.getLastRemindTime();

                //是否提醒
                boolean b = true;

                if (lastRemindTime != null)
                {
                    //已经提醒过，判断提醒时间是否在误差范围内
                    long lastRemindTimeValue = lastRemindTime.getTime();

                    if (remindTimeValue > lastRemindTimeValue - L && remindTimeValue < lastRemindTimeValue + R)
                    {
                        //提醒时间在误差范围内，不重复提醒
                        b = false;
                    }
                }

                if (b)
                {
                    //提醒

                    //更新最后提醒时间
                    urge.setLastRemindTime(new Date());
                    dao.update(urge);

                    //发送督办的消息
                    String title = instance.getDocument().getTitle();
                    FlowApi.sendMessageToNoDealSteps(
                            Tools.getMessage("com.gzzm.ods.urge.message", title, urge.getContent()), instanceId,
                            OdFlowService.getPageUrl(instance.getType()), OdSystemFlowDao.getInstance());
                }
            }

            if (urge.getRemindType() == UrgeRemindType.day)
            {
                //每天提醒，设置第二天继续提醒
                Jobs.addJob(this, new Date(remindTimeValue + DateUtils.MILLIS_IN_DAY), "od_urge_" + instanceId);
            }
        }
        catch (Throwable ex)
        {
            Tools.log("urge remind failed,instanceId:" + instanceId, ex);
        }
    }

    public static Date getRemindTime(OdFlowUrge urge)
    {
        Date remindTime = urge.getUrgeTime();
        Date nowTime = new Date();

        if (urge.getRemindType() == UrgeRemindType.day)
        {
            //每天提醒，将提醒日期设置为当前日期

            long remindTimeValue = remindTime.getTime();
            long nowTimeValue = nowTime.getTime();

            if (nowTimeValue > remindTimeValue)
            {
                remindTimeValue += (nowTimeValue - remindTimeValue) / DateUtils.MILLIS_IN_DAY * DateUtils.MILLIS_IN_DAY;

                if (remindTimeValue < nowTimeValue)
                    remindTimeValue += DateUtils.MILLIS_IN_DAY;

                return new Date(remindTimeValue);
            }
        }

        return remindTime;
    }

    public static void updateJob(OdFlowUrge urge) throws Exception
    {
        Jobs.addJob(new UrgeJob(urge.getInstanceId()), getRemindTime(urge), "od_urge_" + urge.getInstanceId());
    }
}
