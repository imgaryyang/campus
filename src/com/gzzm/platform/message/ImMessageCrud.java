package com.gzzm.platform.message;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;

import java.sql.Date;

/**
 * 系统即时消息的维护
 *
 * @author camel
 * @date 11-12-4
 */
@Service(url = "/message/im/crud")
public class ImMessageCrud extends UserOwnedNormalCrud<ImMessage, Long>
{
    @Lower(column = "sendTime")
    private Date time_start;

    @Upper(column = "sendTime")
    private Date time_end;

    @Like
    private String content;

    public ImMessageCrud()
    {
        addOrderBy("sendTime", OrderType.desc);
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
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.setTitle("系统消息");

        view.addComponent("时间", "time_start", "time_end");
        view.addComponent("内容", "content");

        view.addColumn("时间", "sendTime");
        view.addColumn("内容", "content").setAutoExpand(true).setWrap(true);

        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());

        return view;
    }
}
