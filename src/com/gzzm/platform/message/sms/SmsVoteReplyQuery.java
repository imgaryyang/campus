package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * @author camel
 * @date 11-9-28
 */
@Service(url = "/message/sms/vote/reply/list")
public class SmsVoteReplyQuery extends BaseQueryCrud<SmsVoteReply, Integer>
{
    @Inject
    private SmsDao dao;

    private Integer voteId;

    @Like
    private String phone;

    @Lower(column = "replyTime")
    private Date time_start;

    @Upper(column = "replyTime")
    private Date time_end;

    public SmsVoteReplyQuery()
    {
        addOrderBy("replyTime", OrderType.desc);
    }

    public Integer getVoteId()
    {
        return voteId;
    }

    public void setVoteId(Integer voteId)
    {
        this.voteId = voteId;
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

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.setTitle("短信投票回复列表");

        SmsVote vote = dao.getVote(voteId);
        boolean anonymous = vote.isAnonymous();

        if (!anonymous)
            view.addComponent("手机号码", "phone");
        view.addComponent("投票时间", "time_start", "time_end");

        if (!anonymous)
        {
            view.addColumn("手机号码", "phone");
            view.addColumn("姓名", "userName");
        }

        view.addColumn("投票时间", "replyTime");
        view.addColumn("选择选项", "voteText").setOrderFiled("voteValue");

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("短信投票记录");
    }
}
