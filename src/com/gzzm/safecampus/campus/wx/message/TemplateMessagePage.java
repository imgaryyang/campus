package com.gzzm.safecampus.campus.wx.message;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.annotation.Lower;
import net.cyan.crud.annotation.Upper;

import java.sql.Date;

/**
 * 微信模板消息查询页面
 *
 * @author Neo
 * @date 2018/4/3 10:15
 */
@Service(url = "/campus/wx/message/templatemessage")
public class TemplateMessagePage extends BaseNormalCrud<TemplateMessage, Integer>
{
    /**
     * 消息类型
     */
    private TemplateMessageType messageType;

    /**
     * 消息Id
     */
    @Like
    private String messageId;

    @Lower(column = "sendTime")
    private Date time_start;

    @Upper(column = "sendTime")
    private Date time_end;

    public TemplateMessagePage()
    {
        addOrderBy("sendTime", OrderType.desc);
    }

    public TemplateMessageType getMessageType()
    {
        return messageType;
    }

    public void setMessageType(TemplateMessageType messageType)
    {
        this.messageType = messageType;
    }

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.addComponent("时间", "time_start", "time_end");
        view.addComponent("消息类型", "messageType");
        view.addComponent("消息Id", "messageId");
        view.addColumn("微信名称", "wxUser.userName").setWidth("100px");
        view.addColumn("发送人", "senderName").setWidth("200px");
        view.addColumn("发送时间", "sendTime").setWidth("150px");
        view.addColumn("消息类型", "messageType").setWidth("100px");
        view.addColumn("内容", "content");
        view.addColumn("消息Id", "messageId");
        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());
        return view;
    }
}
