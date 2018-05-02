package com.gzzm.oa.activite;

import com.gzzm.oa.schedule.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.*;
import com.gzzm.platform.organ.User;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.DateUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 11-12-21
 */
public class ActiviteService
{
    @Inject
    private ActiviteDao dao;

    @Inject
    private ScheduleService scheduleService;

    public ActiviteService()
    {
    }

    public ActiviteDao getDao()
    {
        return dao;
    }

    public Activite getActivite(Integer activiteId) throws Exception
    {
        return dao.getActivite(activiteId);
    }

    @Transactional
    public void issue(Integer activiteId) throws Exception
    {
        Activite activite = dao.getActivite(activiteId);
        activite.setState(ActiviteState.issued);

        dao.update(activite);

        updateSchedule(activite);

        for (ActiviteMember member : activite.getMemberLists())
        {
            sendMessage(member);
        }
    }

    @Transactional
    public void end(Integer activiteId) throws Exception
    {
        Activite activite = dao.getActivite(activiteId);
        activite.setState(ActiviteState.end);

        dao.update(activite);

        updateSchedule(activite);
    }

    @Transactional
    public void memberCheck(Integer[] memberIds, MemberState state) throws Exception
    {
        for (Integer memberId : memberIds)
        {
            ActiviteMember member = dao.getActiviteMember(memberId);
            member.setState(state);
            dao.update(member);

            updateSchedule(member.getActivite());
            sendMessage(member);
        }
    }

    @Transactional
    public void addMembers(Integer activiteId, Integer[] userIds) throws Exception
    {
        int orderId = -1;
        for (Integer userId : userIds)
        {
            ActiviteMember member = dao.getActiviteMember(activiteId, userId);
            if (member == null)
            {
                if (orderId < 0)
                {
                    Integer orderId1 = dao.getMaxMemberOrder(activiteId);
                    if (orderId1 != null)
                        orderId = orderId1;
                }

                member = new ActiviteMember();
                member.setActiviteId(activiteId);
                member.setUserId(userId);
                member.setState(MemberState.invite);
                member.setApplyTime(new Date());
                member.setOrderId(++orderId);
                dao.add(member);

                sendMessage(member);
            }
            else if (member.getState() == MemberState.fail)
            {
                member.setState(MemberState.invite);
                member.setApplyTime(new Date());
                dao.update(member);

                sendMessage(member);
            }
        }
    }

    public void apply(Integer activiteId, Integer userId) throws Exception
    {
        ActiviteMember member = dao.getActiviteMember(activiteId, userId);

        if (member == null)
        {
            member = new ActiviteMember();
            member.setActiviteId(activiteId);
            member.setUserId(userId);
            member.setApplyTime(new java.util.Date());
            member.setState(MemberState.applying);
            Integer orderId = dao.getMaxMemberOrder(activiteId);
            member.setOrderId(orderId == null ? 0 : orderId + 1);

            dao.add(member);

            sendMessage(member);
        }
    }

    public void updateSchedule(Activite activite) throws Exception
    {
        if (activite.getState() == ActiviteState.canceled)
        {
            removeSchedule(activite.getActiviteId());
            return;
        }

        Schedule schedule = new Schedule();
        schedule.setLinkId("activite_" + activite.getActiviteId());
        schedule.setTag(ScheduleTag.dept);
        schedule.setType(scheduleService.getType("活动"));
        schedule.setDeptId(activite.getDeptId());
        schedule.setCreator(activite.getCreator());

        schedule.setTitle(activite.getTitle());
        schedule.setContent(activite.getActiviteContent());
        schedule.setAddress(activite.getActivitePlace());
        schedule.setStartTime(activite.getStartTime());
        schedule.setEndTime(activite.getEndTime());
        if (activite.getState() == ActiviteState.end)
            schedule.setState(ScheduleState.closed);
        else
            schedule.setState(ScheduleState.notStarted);

        List<ActiviteMember> members = activite.getMemberLists();
        List<User> participants = new ArrayList<User>(members.size());
        for (ActiviteMember member : members)
        {
            if (member.getState() == MemberState.participation)
                participants.add(member.getUser());
        }
        schedule.setParticipants(participants);


        //提醒方式，默认为短信和即时消息提醒，并提前半小时和一小时提醒，可到日程管理中修改提醒方式
        schedule.setRemindType(new ScheduleRemindType[]{ScheduleRemindType.im, ScheduleRemindType.sms});
        schedule.setRemindTime(DateUtils.addMinute(schedule.getStartTime(), -60));
//        schedule.setRemindTime1(DateUtils.addMinute(schedule.getStartTime(), -30));

        scheduleService.updateSchedule(schedule);
    }

    public void removeSchedule(Integer activiteId) throws Exception
    {
        scheduleService.removeSchedule("activite_" + activiteId);
    }

    /**
     * 发送邀请参与活动的信息
     *
     * @param member 活动参与信息
     * @throws Exception 数据库错误
     */
    public void sendMessage(ActiviteMember member) throws Exception
    {
        MemberState state = member.getState();
        Activite activite = member.getActivite();
        if (activite == null)
            member.setActivite(activite = dao.getActivite(member.getActiviteId()));

        if (activite.getUser() == null)
            activite.setUser(dao.getUser(activite.getCreator()));

        if (member.getUserId().equals(activite.getCreator()))
            return;

        if (member.getUser() == null)
            member.setUser(dao.getUser(member.getUserId()));

        Message message = null;

        if (state == MemberState.invite)
        {
            message = new Message();
            message.setMessage(Tools.getMessage("oa.activite.inviteNotify", member));
            message.setSender(activite.getCreator());
            message.setUserId(member.getUserId());
            message.setFromDeptId(activite.getDeptId());
            message.setUrl("/oa/activite/join");
        }
        else if (state == MemberState.applying)
        {
            message = new Message();
            message.setMessage(Tools.getMessage("oa.activite.applyNotify", member));
            message.setSender(member.getUserId());
            message.setUserId(activite.getCreator());
            message.setFromDeptId(activite.getDeptId());
            message.setUrl("/oa/activite/member?activiteId=" + activite.getActiviteId());
        }
        else if (state == MemberState.nonparticipation)
        {
            message = new Message();
            message.setMessage(Tools.getMessage("oa.activite.rejectNotify", member));
            message.setSender(member.getUserId());
            message.setUserId(activite.getCreator());
            message.setFromDeptId(activite.getDeptId());
            message.setUrl("/oa/activite/member?activiteId=" + activite.getActiviteId());
        }
        else if (state == MemberState.fail)
        {
            message = new Message();
            message.setMessage(Tools.getMessage("oa.activite.failNotify", member));
            message.setSender(activite.getCreator());
            message.setUserId(member.getUserId());
            message.setFromDeptId(activite.getDeptId());
        }

        if (message != null)
        {
            if (activite.isNotify() == null || !activite.isNotify())
            {
                //只使用即时消息通知
                message.setMethods(ImMessageSender.IM);
            }

            message.send();
        }
    }
}
