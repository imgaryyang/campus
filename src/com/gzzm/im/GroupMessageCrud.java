package com.gzzm.im;

import com.gzzm.im.entitys.GroupMessage;
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
@Service(url = "/im/messages/group")
public class GroupMessageCrud extends UserOwnedNormalCrud<GroupMessage, Long>
{
    @Inject
    private ImDao dao;

    /**
     * 群id
     */
    private Integer groupId;

    @Upper(column = "sendTime")
    private Date time_end;

    @Lower(column = "sendTime")
    private Date time_start;

    @Like("senderUser.userName")
    private String userName;

    public GroupMessageCrud()
    {
        addOrderBy("sendTime", OrderType.desc);
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
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
    protected String getComplexCondition() throws Exception
    {
        return "groupId=?groupId ";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("聊天日期从", "time_start");
        view.addComponent("到", "time_end");
        view.addComponent("发送人", "userName");

        if (groupId == null)
            view.setTitle("聊天记录");
        else
            view.setTitle("聊天记录-" + dao.getGroupName(groupId));

        view.addColumn("发送人", new FieldCell("senderUser.userName")
        {
            @Override
            public String display(Object entity) throws Exception
            {
                GroupMessage message = (GroupMessage) entity;

                return message.getSenderUser().getUserName();

            }
        });
        view.addColumn("时间", "sendTime");
        view.addColumn("内容", new FieldCell("content")
        {
            @Override
            public String display(Object entity) throws Exception
            {
                GroupMessage message = (GroupMessage) entity;
                MessageType type = message.getType();
                if (type == null)
                    type = MessageType.text;

                switch (type)
                {
                    case text:
                    {
                        return ImUtils.decodeContent(HtmlUtils.escapeHtml(message.getContent()));
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

        view.importJs("/im/message.js");
        view.importCss("/im/message.css");

        return view;
    }
}
