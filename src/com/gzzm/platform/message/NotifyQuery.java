package com.gzzm.platform.message;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.message.comet.CometService;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.Provider;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * @author camel
 * @date 12-12-27
 */
@Service(url = "/message/notify/query")
public class NotifyQuery extends BaseQueryCrud<Notify, Integer>
{
    @Inject
    private static Provider<CometService> cometServiceProvider;

    @UserId
    private Integer userId;

    @Lower(column = "sendTime")
    private Date time_start;

    @Upper(column = "sendTime")
    private Date time_end;

    @Like
    private String content;

    public NotifyQuery()
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
        PageTableView view = new PageTableView(false);

        view.addComponent("发送时间", "time_start", "time_end");
        view.addComponent("内容", "content");

        view.addColumn("内容", "content").setWrap(true);
        view.addColumn("发送人", "user.userName");
        view.addColumn("发送时间", "sendTime");

        view.defaultInit();

        return view;
    }

    @Service(url = "/message/notify/send")
    public String showNotify() throws Exception
    {
        return "notify";
    }

    @Service(url = "/message/notify/send", method = HttpMethod.post)
    public void sendNotify() throws Exception
    {
        java.util.Date sendTime = new java.util.Date();

        Notify notify = new Notify();
        notify.setContent(content);
        notify.setUserId(userId);
        notify.setSendTime(sendTime);
        getCrudService().add(notify);

        ImMessage message = new ImMessage();
        message.setContent(content);
        message.setSendTime(sendTime);

        cometServiceProvider.get().sendMessageToAllUser(message);
    }
}
