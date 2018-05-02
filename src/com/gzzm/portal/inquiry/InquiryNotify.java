package com.gzzm.portal.inquiry;

import com.gzzm.platform.annotation.Listener;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.Message;

/**
 * 咨询投诉通知
 *
 * @author camel
 * @date 2014/8/13
 */
@Listener
public class InquiryNotify
{
    /**
     * 接收短信提醒的应用
     */
    public static final String NOTIFY_APP = "inquiry_notify";

    public InquiryNotify()
    {
    }

    /**
     * 收到咨询投诉时的通知
     *
     * @param process 要通知的咨询投诉处理过程
     * @throws Exception 咨询投诉通知错误
     */
    @Listener("receive")
    public void notify(InquiryProcess process) throws Exception
    {
        Message message = new Message();

        message.setApp("inquiry");
        message.setToDeptId(process.getDeptId());

        if (process.getPreviousProcessId() != null)
            message.setFromDeptId(process.getPreviousProcess().getDeptId());

        if (process.getPreviousProcessId() != null)
            message.setContent(Tools.getMessage("oa.inquiry.turnnotify", process));
        else
            message.setContent(Tools.getMessage("oa.inquiry.sendnotify", process));

        message.setUrls("/portal/inquiry/process?states=NOACCEPTED&states=PROCESSING",
                "/portal/inquiry/process?states=NOACCEPTED&states=PROCESSING&autoAccept=true");

        //NOTIFY_APP为接收短信的应用，是一个虚拟的菜单
        //考虑到管理员有权限查看各部门的咨询投诉，但是不接收短信，所以不以咨询投诉处理菜单来区分是否发送短信
        //而是根据此虚拟菜单来区分是否应该发送短信
        message.sendTo(NOTIFY_APP, process.getDeptId());

        message.send();
    }
}
