package com.gzzm.sms;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.view.components.CCombox;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 充值记录维护
 *
 * @author camel
 * @date 11-11-28
 */
@Service(url = "/sms/recharge")
public class ReChargeCrud extends BaseNormalCrud<ReCharge, Integer>
{
    @Inject
    private SmsDao dao;

    /**
     * 开始时间，查询条件
     */
    private Date time_start;

    /**
     * 结束时间，查询条件
     */
    private Date time_end;

    private Integer userId;

    public ReChargeCrud()
    {
        setLog(true);
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

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    @Select(field = {"userId", "entity.userId"})
    @NotSerialized
    public List<SmsUser> getUsers() throws Exception
    {
        return dao.getSmsUsers();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("充值时间", "time_start", "time_end");
        view.addComponent("充值用户", "userId");

        view.addColumn("充值时间", "reChargeTime");
        view.addColumn("充值用户", "user.userName");
        view.addColumn("充值网关", "type");
        view.addColumn("充值金额", "amount");

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("充值时间", "reChargeTime");
        view.addComponent("充值用户", "userId");
        view.addComponent("充值网关", new CCombox("type", new GatewayType[]{
                GatewayType.mobile, GatewayType.union, GatewayType.telecom
        }));
        view.addComponent("充值金额", "amount");

        view.addDefaultButtons();

        return view;
    }
}
