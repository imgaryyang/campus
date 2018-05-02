package com.gzzm.platform.message.sms;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.DateUtils;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.Align;

import java.sql.Date;
import java.util.Collection;

/**
 * 短信记录查询
 *
 * @author camel
 * @date 2010-5-24
 */
@Service(url = "/message/sms/log")
public class SmsLogQuery extends DeptOwnedQuery<SmsLog, String>
{
    /**
     * 时间范围作查询条件
     */
    @Lower(column = "sendTime")
    private java.sql.Date time_start;

    @Upper(column = "sendTime")
    private java.sql.Date time_end;

    /**
     * 短信内容做查询条件
     */
    @Like
    private String content;

    /**
     * 电话号码做查询条件
     */
    @Like
    private String phone;

    /**
     * 接收者做查询条件
     */
    @Like("user.userName")
    private String userName;

    @UserId
    private Integer userId;

    /**
     * 是否是个人短信查询，个人短信为true，所有短信为false
     */
    private boolean user;

    public SmsLogQuery()
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

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Integer getUserId()
    {
        return user ? userId : null;
    }

    public boolean isUser()
    {
        return user;
    }

    public void setUser(boolean user)
    {
        this.user = user;
    }

    @Override
    @In("fromDeptId")
    @NotSerialized
    public Collection<Integer> getQueryDeptIds()
    {
        return super.getQueryDeptIds();
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (time_start == null && time_end == null)
            time_start = new java.sql.Date(DateUtils.addMonth(DateUtils.truncate(new java.util.Date()), -2).getTime());
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("内容", "content");
        if (!user)
        {
            view.addComponent("电话", "phone");
            view.addComponent("发送部门", "deptIds");
            view.addComponent("接收人", "userName");
        }
        view.addComponent("时间", "time_start", "time_end");

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        view.addColumn("短信内容", "content").setAlign(Align.left).setWrap(true);
        view.addColumn("发送时间", "sendTime").setWidth("150");
        view.addColumn("接收电话", "phone").setWidth("100");
        view.addColumn("定时发送", "fixedTime").setWidth("150");
        view.addColumn("接收时间", "receiveTime").setWidth("140");
        if (!user)
            view.addColumn("接收人", "user.userName").setWidth("60");
        view.addColumn("发送部门", "fromDept.deptName").setWidth("125");
        view.addColumn("接收部门", "toDept.deptName").setWidth("125");
        view.addColumn("状态", "state").setWidth("60");

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("短信发送记录");
    }
}
