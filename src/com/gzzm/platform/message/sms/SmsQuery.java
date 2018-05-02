package com.gzzm.platform.message.sms;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.Collection;

/**
 * 短信查询
 *
 * @author camel
 * @date 2010-5-23
 */
@Service(url = "/message/sms/query")
public class SmsQuery extends BaseNormalCrud<Sms, Long>
{
    /**
     * 时间范围作查询条件
     */
    @Lower(column = "sendTime")
    private java.sql.Date time_start;

    @Upper(column = "sendTime")
    private java.sql.Date time_end;

    @Like
    private String content;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @AuthDeptIds
    @NotSerialized
    @NotCondition
    private Collection<Integer> deptIds;

    public SmsQuery()
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

    @NotCondition
    @NotSerialized
    public Integer getUserId()
    {
        return userOnlineInfo.getUserId();
    }

    public Collection<Integer> getDeptIds()
    {
        return deptIds;
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (userOnlineInfo.isAdmin())
            return null;

        String s = "userId=:userId";

        if (!userOnlineInfo.isSelf(RequestContext.getContext().getRequest()) && deptIds != null && deptIds.size() > 0)
            s += " or deptId in :deptIds";

        return s;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.importJs("/platform/message/sms.js");

        view.addComponent("内容", "content");
        view.addComponent("时间", "time_start", "time_end");

        view.addColumn("短信内容", "content");
        view.addColumn("发送时间", "sendTime");
        view.addColumn("定时发送", "fixedTime");
        view.addColumn("发送人", "user.userName");
        view.addColumn("跟踪", new CButton("跟踪", "showReceipts(${smsId})"));

        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());
        view.addButton(Buttons.export("xls"));

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("短信记录");
    }
}
