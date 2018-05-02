package com.gzzm.ods.receipt.meeting;

import com.gzzm.oa.schedule.*;
import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.receipt.*;
import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.attachment.*;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.crud.CrudUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 12-4-8
 */
@Service
public class MeetingReceiptPage extends ReceiptBasePage
{
    @Inject
    private static Provider<ScheduleService> scheduleServiceProvider;

    @Inject
    private MeetingReceiptDao meetingDao;

    @Inject
    private ExchangeDao exchangeDao;

    private ReceiptMeeting meeting;

    private boolean readOnly;

    @UserId
    private Integer userId;

    /**
     * 收文单位能否编辑会议基本信息，如果会议信息由收文单位创建则允许编辑，如果会议信息由发文单位创建，则不允许编辑。
     * 仅当纸质收文或者从外系统接收公文时允许收文单位创建会议信息
     */
    private boolean editable;

    private PageAttachmentList attachments;

    public MeetingReceiptPage()
    {
    }

    public ReceiptMeeting getMeeting()
    {
        return meeting;
    }

    public void setMeeting(ReceiptMeeting meeting)
    {
        this.meeting = meeting;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public PageAttachmentList getAttachments()
    {
        return attachments;
    }

    public void setAttachments(PageAttachmentList attachments)
    {
        this.attachments = attachments;
    }

    @Service(url = "/ods/receipt/meeting/{receiptId}")
    public String show() throws Exception
    {
        initDeptId();

        loadReceipt();

        meeting = meetingDao.getMeeting(receiptId);

        if (meeting == null)
        {
            meeting = new ReceiptMeeting();
            meeting.setReceiptId(receiptId);
            meeting.setMeetingName(receipt.getDocument().getTitle());
            meeting.setLocation(" ");
            meetingDao.add(meeting);
        }

        return "meeting";
    }

    @Service
    @ObjectResult
    @Transactional
    public void save() throws Exception
    {
        super.save();

        meeting.setReceiptId(receiptId);
        meetingDao.update(meeting);
    }

    @Service(url = {"/ods/receipt/meeting/{receiptId}/reply", "/ods/receipt/meeting/reply"})
    public String showReply() throws Exception
    {
        loadReceipt();

        if (receipt != null)
        {
            meeting = meetingDao.getMeeting(receiptId);

            if (meeting == null)
            {
                meeting = new ReceiptMeeting();
                meeting.setReceiptId(receiptId);
                if (receipt.getDocumentId() != null)
                    meeting.setMeetingName(receipt.getDocument().getTitle());
                meeting.setLocation(" ");
                meetingDao.add(meeting);
            }
        }

        ReceiveBase receiveBase = exchangeDao.getReceiveByDocumentIdAndDeptId(documentId, deptId);
        editable = receiveBase.getMethod() == ReceiveMethod.manual || receiveBase.getMethod() == ReceiveMethod.outter;

        attachments = new PageAttachmentList();

        if (meeting != null)
            attachments.setAttachmentId(meeting.getAttachmentId());
        attachments.setAuthority(new DeptAttachmentAuthority(deptId, false));

        if (!editable)
        {
            ReceiptReply reply = getReply();
            if (reply != null && reply.getReplied() != null && reply.getReplied())
            {
                readOnly = true;
            }
        }

        attachments.setEditable(!readOnly);

        return "meetingreply";
    }

    @Transactional
    @Service(method = HttpMethod.post)
    public Long saveReply() throws Exception
    {
        if (receiptId == null)
        {
            Receipt receipt = new Receipt();
            receipt.setDocumentId(documentId);
            receipt.setType("meeting");
            receipt.setSendTime(new Date());
            receipt.setCreator(userId);

            meetingDao.add(receipt);

            receiptId = receipt.getReceiptId();

            OfficeDocument document = new OfficeDocument();
            document.setDocumentId(documentId);
            document.setReceiptId(receiptId);
            meetingDao.update(document);
        }

        meeting.setReceiptId(receiptId);

        if (attachments != null)
        {
            meeting.setAttachmentId(attachments.save(userId, deptId, "od_meeting"));
        }

        meetingDao.save(meeting);

        updateSchedules();

        return receiptId;
    }

    @Override
    @Transactional
    @Service(method = HttpMethod.post)
    public void reply() throws Exception
    {
        saveReply();

        meetingDao.makeItemReplied(receiptId, deptId);

        super.reply();
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public boolean addUsers(Integer[] userIds) throws Exception
    {
        boolean result = false;
        if (userIds != null)
        {
            for (Integer userId : userIds)
            {
                ReceiptMeetingItem item = meetingDao.getItemByUserId(receiptId, deptId, userId);
                if (item == null)
                {
                    User user = meetingDao.getUser(userId);

                    item = new ReceiptMeetingItem();
                    item.setReceiptId(receiptId);
                    item.setDeptId(deptId);
                    item.setUserId(userId);
                    item.setCreateTime(new Date());
                    item.setCreator(this.userId);
                    item.setUserName(user.getUserName());
                    item.setSex(user.getSex());
                    item.setPhone(user.getPhone());
                    item.setStation(StringUtils.concat(user.getStations(), ","));
                    item.setReplied(false);
                    item.setOrderId(CrudUtils.getOrderValue(6, true));

                    meetingDao.add(item);

                    result = true;
                }
            }
        }

        if (result)
        {
            updateSchedules();
        }

        return result;
    }

    private void updateSchedules() throws Exception
    {
        ScheduleService scheduleService = scheduleServiceProvider.get();

        if (meeting == null)
            meeting = meetingDao.getMeeting(receiptId);

        if (meeting.getStartTime() != null && meeting.getEndTime() != null)
        {
            Schedule schedule = new Schedule();
            schedule.setLinkId("odmeeting_" + meeting.getReceiptId());
            schedule.setTag(ScheduleTag.dept);
            schedule.setType(scheduleService.getType("会议"));
            schedule.setDeptId(deptId);
            schedule.setCreator(userId);

            schedule.setTitle(meeting.getMeetingName());
            schedule.setAddress(meeting.getLocation());
            schedule.setStartTime(meeting.getStartTime());
            schedule.setEndTime(meeting.getEndTime());

            List<ReceiptMeetingItem> items = meetingDao.getItems(receiptId, deptId);
            List<User> participants = new ArrayList<User>(items.size());
            for (ReceiptMeetingItem item : items)
            {
                if (item.getUserId() != null)
                    participants.add(item.getUser());
            }
            schedule.setParticipants(participants);

            //提醒方式，默认为短信和即时消息提醒，并提前半小时和一小时提醒，可到日程管理中修改提醒方式
            schedule.setRemindType(new ScheduleRemindType[]{ScheduleRemindType.im, ScheduleRemindType.sms});
            schedule.setRemindTime(DateUtils.addMinute(schedule.getStartTime(), -60));
//            schedule.setRemindTime1(DateUtils.addMinute(schedule.getStartTime(), -30));

            scheduleService.updateSchedule(schedule);
        }
    }
}
