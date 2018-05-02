package com.gzzm.oa.mail;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.FieldCell;

import java.sql.Date;

/**
 * 邮箱统计
 *
 * @author camel
 * @date 11-8-30
 */
@Service(url = "/oa/mail/userstat")
public class UserMailStat extends UserStat
{
    @Lower
    private Date time_start;

    @Upper
    private Date time_end;

    public UserMailStat()
    {
        setLoadTotal(true);
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
    protected void initStats() throws Exception
    {
        super.initStats();

        join(Mail.class, "m", "m.userId=u.userId");

        addStat("sendCount", "count(m.type=1 and sendTime>=?time_start and sendTime<?time_end)");
        addStat("receiveCount", "count(m.type=2 and acceptTime>=?time_start and acceptTime<?time_end)");
        addStat("wastage", "nvl(sum(mailSize),0)");
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("用户名", "name");
        view.addComponent("所属部门", "topDeptIds");
        view.addComponent("统计时间", "time_start", "time_end");

        view.addColumn("用户名", "u==null?'合计':u.userName").setWidth("140");
        view.addColumn("所属部门", "u.allSimpleDeptName()");

        view.addColumn("发件数", "sendCount");
        view.addColumn("收件数", "receiveCount");
        view.addColumn("占用空间", new FieldCell("wastage", "bytesize"));

        view.defaultInit();

        return view;
    }
}
