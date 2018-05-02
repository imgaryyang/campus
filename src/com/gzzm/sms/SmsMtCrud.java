package com.gzzm.sms;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.DateUtils;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.Align;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.List;

/**
 * @author camel
 * @date 2010-11-15
 */
@Service(url = "/sms/mt")
public class SmsMtCrud extends BaseNormalCrud<SmsMt, Long>
{
    @Inject
    private SmsService service;

    /**
     * 用序列号做查询条件，严格匹配
     */
    private String serial;

    /**
     * 用电话号码做查询条件，严格匹配
     */
    private String phone;

    @Lower(column = "sendTime")
    private Date time_start;

    @Upper(column = "sendTime")
    private Date time_end;

    @Contains
    private String content;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    private Integer userId;

    private Integer gatewayId;

    public SmsMtCrud()
    {
        addOrderBy("sendTime", OrderType.desc);
    }

    public String getSerial()
    {
        return serial;
    }

    public void setSerial(String serial)
    {
        this.serial = serial;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
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

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getGatewayId()
    {
        return gatewayId;
    }

    public void setGatewayId(Integer gatewayId)
    {
        this.gatewayId = gatewayId;
    }

    @NotSerialized
    public List<Integer> getUserIds() throws Exception
    {
        //管理员可以看到所有用户的数据，其他人只能看到自己拥有权限的用户的数据
        if (userOnlineInfo.isAdmin())
            return null;
        else
            return service.getDao().getSmsUserIds(userOnlineInfo.getUserId());
    }

    @NotSerialized
    @Select(field = "userId")
    public List<SmsUser> getUsers() throws Exception
    {
        //管理员可以看到所有用户，其他人只能看到自己拥有权限的用户
        if (userOnlineInfo.isAdmin())
            return service.getDao().getSmsUsers();
        else
            return service.getDao().getSmsUsers(userOnlineInfo.getUserId());
    }

    @NotSerialized
    @Select(field = "gatewayId")
    public List<Gateway> getGateways() throws Exception
    {
        return service.getDao().getAllGateways();
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
        PageTableView view = new PageTableView();

        view.addComponent("手机号码", "phone");
        view.addComponent("内容", "content");
        view.addComponent("时间", "time_start", "time_end");
        view.addComponent("网关", "gatewayId");
        view.addComponent("用户", "userId");
        view.addMoreComponent("序号", "serial");

        view.addColumn("手机号码", "phone").setWidth("90");
        view.addColumn("序号", "serial").setWidth("50");
        view.addColumn("发送时间", "sendTime").setWidth("140");
        view.addColumn("内容", "content").setAlign(Align.left).setAutoExpand(true).setWrap(true);
        view.addColumn("用户", "user.userName").setWidth("125");
        view.addColumn("网关", "gateway.gatewayName").setWidth("100");
        view.addColumn("接收时间", "receiveTime").setWidth("140");
        view.addColumn("错误信息", "error").setWidth("130");

        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());
        view.addButton(Buttons.export("xls"));
//        view.addButton("重新发送", "reSend();");

        view.importJs("/sms/mt.js");

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("已发短信列表");
    }

    @Service
    public void reSend() throws Exception
    {
        service.sendSmss(time_start, time_end);
    }
}