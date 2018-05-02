package com.gzzm.im;

import com.gzzm.im.entitys.UserMessage;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.HtmlUtils;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.FieldCell;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 用户消息记录查询
 *
 * @author camel
 * @date 2011-2-7
 */
@Service(url = "/im/messages/user")
public class UserMessageCrud extends UserOwnedNormalCrud<UserMessage, Long>
{
    @Inject
    private ImDao dao;

    /**
     * 联系人，消息的发送者或者接收者
     */
    private Integer linkman;

    @Upper(column = "sendTime")
    private Date time_start;

    @Lower(column = "sendTime")
    private Date time_end;

    @Like
    private String content;

    public UserMessageCrud()
    {
        addOrderBy("sendTime", OrderType.desc);
    }

    public Integer getLinkman()
    {
        return linkman;
    }

    public void setLinkman(Integer linkman)
    {
        this.linkman = linkman;
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

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "sender=?linkman or receiver=?linkman";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        if (linkman == null)
            view.setTitle("聊天记录");
        else
            view.setTitle("聊天记录-" + dao.getUserName(linkman));

        view.addComponent("时间", "time_start", "time_end");
        view.addComponent("内容", "content");

        view.addColumn("发送人", new FieldCell("senderUser.userName")
        {
            @Override
            public String display(Object entity) throws Exception
            {
                UserMessage message = (UserMessage) entity;

                String s = message.getSenderUser().getUserName();

                if (message.getType() == MessageType.sms)
                    s += "(手机)";

                return s;
            }
        });
        view.addColumn("时间", "sendTime");
        view.addColumn("内容", new FieldCell("content")
        {
            @Override
            public String display(Object entity) throws Exception
            {
                UserMessage message = (UserMessage) entity;

                MessageType type = message.getType();
                if (type == null)
                    type = MessageType.text;

                switch (type)
                {
                    case text:
                    {
                        return ImUtils.decodeContent(HtmlUtils.escapeHtml(message.getContent()));
                    }
                    case sms:
                    {
                        return HtmlUtils.escapeHtml(message.getContent());
                    }
                    case html:
                    {
                        return message.getContent();
                    }
                    case file:
                    {
                        return "<a href='/attachment/" + dao.getAttachmentUUID(message.getAttachmentId()) +
                                "/1' target='_blank'>" + message.getContent() + "</a>";
                    }
                    case image:
                    {
                        String uuid = dao.getAttachmentUUID(message.getAttachmentId());
                        return "<img class='im_image' src='/attachment/" + uuid +
                                "/1/thumb' class='im_image' onclick=\"showImage('" + uuid + "')\">";
                    }
                }
                return null;
            }
        }.setOrderable(false)).setAutoExpand(true).setWrap(true);

        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());

        view.importJs("/im/message.js");
        view.importCss("/im/message.css");

        return view;
    }
}
