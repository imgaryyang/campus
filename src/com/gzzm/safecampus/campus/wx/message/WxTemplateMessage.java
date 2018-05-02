package com.gzzm.safecampus.campus.wx.message;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.safecampus.campus.wx.WxMpServiceProvider;
import com.gzzm.safecampus.wx.common.WxService;
import com.gzzm.safecampus.wx.user.WxUser;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 微信消息
 *
 * @author Neo
 * @date 2018/3/28 9:53
 */
public class WxTemplateMessage
{
    @Inject
    private static Provider<MessageConfig> messageConfigProvider;

    @Inject
    private static Provider<WxService> wxServiceProvider;

    @Inject
    private static Provider<UserOnlineInfo> userOnlineInfoProvider;

    public WxTemplateMessage()
    {
    }

    /**
     * 发送待缴账单消息
     *
     * @param billMsg 账单消息
     */
    public static void sendUnpaidBillMsg(BillMsg billMsg)
    {
        sendBillMsg(billMsg, messageConfigProvider.get().getUnpaidBillTemplate());
    }

    /**
     * 发送已缴账单消息
     *
     * @param billMsg 账单消息
     */
    public static void sendPaidBillMsg(BillMsg billMsg)
    {
        sendBillMsg(billMsg, messageConfigProvider.get().getPaidBillTemplate());
    }

    /**
     * 发送账单消息
     *
     * @param billMsg  账单消息
     * @param template 使用的模板
     */
    public static void sendBillMsg(BillMsg billMsg, MessageConfig.TemplateItem template)
    {
        Map<String, String> data = new HashMap<>();
        data.put("first", billMsg.getFirst());
        data.put("childName", billMsg.getStudentName());
        data.put("paymentType", billMsg.getPaymentType());
        data.put("payItemName", billMsg.getPayItemName());
        data.put("money", billMsg.getMoney());
        data.put("time", billMsg.getTime());
        //支付成功后，由平台发送账单
        if (userOnlineInfoProvider.get() == null)
            sendMessageByStudentId(1, template, data, billMsg);
        else
            sendMessageByStudentId(userOnlineInfoProvider.get().getUserId(), template, data, billMsg);
    }

    /**
     * 发送考勤模板消息
     *
     * @param attendanceMsg 考勤信息
     */
    public static void sendAttendanceMsg(Integer teacherId, AttendanceMsg attendanceMsg)
    {
        Map<String, String> data = new HashMap<>();
        data.put("first", attendanceMsg.getFirst());
        data.put("childName", attendanceMsg.getStudentName());
        data.put("attendanceType", attendanceMsg.getAttendanceType());
        data.put("attendanceTime", attendanceMsg.getAttendanceTime());
        data.put("attendanceStatus", attendanceMsg.getAttendanceStatus());
        sendMessageByStudentId(teacherId, messageConfigProvider.get().getAttendanceTemplate(), data, attendanceMsg);
    }

    /**
     * 发送成绩模板消息
     *
     * @param scoreMsg 成绩信息
     */
    public static void sendScoreMsg(ScoreMsg scoreMsg)
    {
        Map<String, String> data = new HashMap<>();
        data.put("first", scoreMsg.getFirst());
        data.put("childName", scoreMsg.getStudentName());
        data.put("courseName", scoreMsg.getCourseName());
        data.put("time", scoreMsg.getTime());
        data.put("score", scoreMsg.getScore());
        sendMessageByStudentId(userOnlineInfoProvider.get().getUserId(), messageConfigProvider.get().getScoreTemplate(), data, scoreMsg);
    }

    /**
     * 发送消息给学生家长
     *
     * @param template 使用的模板
     * @param data     模板数据
     */
    public static <T extends StudentMsg> void sendMessageByStudentId(Integer sender, MessageConfig.TemplateItem template, Map<String, String> data, T msg)
    {
        List<WxUser> wxUsers = null;
        try
        {
            wxUsers = wxServiceProvider.get().getWxUsersByStudentId(msg.getStudentId());

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if (wxUsers != null)
        {
            for (WxUser wxUser : wxUsers)
            {
                try
                {
                    Tools.debug("send to: " + wxUser.getUserName());
                    //调用微信接口发送模板消息
                    String messageId = sendMessage(wxUser, template.getTemplateId(), msg.getUrl(), data);
                    //记录消息
                    TemplateMessage templateMsg = new TemplateMessage();
                    templateMsg.setMessageId(messageId);
                    templateMsg.setSendTime(new Date());
                    templateMsg.setWxUserId(wxUser.getWxUserId());
                    templateMsg.setContent(Tools.getMessage(template.getTemplate().replaceAll(" ", ""), data));
                    templateMsg.setMessageType(msg.getMessageType());
                    templateMsg.setSender(sender);
                    wxServiceProvider.get().getDao().add(templateMsg);
                    Tools.debug("send over: ");
                } catch (Exception e)
                {
                    //一条记录发送失败不影响其他记录的发送
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送一条消息
     *
     * @param wxUser     微信用户
     * @param templateId 消息模板Id
     * @param url        点击消息进去的url
     * @param data       模板数据
     * @throws Exception 操作异常
     */
    public static String sendMessage(WxUser wxUser, String templateId, String url, Map<String, String> data) throws Exception
    {
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(wxUser.getOpenId())
                .templateId(templateId)
                .url(url)
                .build();
        for (Map.Entry<String, String> obj : data.entrySet())
        {
            templateMessage.getData().add(new WxMpTemplateData(obj.getKey(), obj.getValue()));
        }
        WxMpTemplateMsgService templateMsgService = WxMpServiceProvider.getTemplateMsgService();
        return templateMsgService.sendTemplateMsg(templateMessage);
    }
}
