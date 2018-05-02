package com.gzzm.platform.message.sms;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.KeyValue;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

/**
 * 短信回执查询
 *
 * @author camel
 * @date 2010-5-24
 */
@Service(url = "/message/sms/receipt")
public class SmsReceiptQuery extends BaseQueryCrud<SmsReceipt, Long>
{
    @Inject
    private SmsDao dao;

    private Long smsId;

    @Like
    private String userName;

    @Like
    private String phone;

    private int replyed;

    @NotSerialized
    private Sms sms;

    public SmsReceiptQuery()
    {
        addOrderBy("receiptId");
    }

    public Long getSmsId()
    {
        return smsId;
    }

    public void setSmsId(Long smsId)
    {
        this.smsId = smsId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public int isReplyed()
    {
        return replyed;
    }

    public void setReplyed(int replyed)
    {
        this.replyed = replyed;
    }

    public Sms getSms() throws Exception
    {
        if (sms == null && smsId != null)
            sms = dao.getSms(smsId);

        return sms;
    }

    /**
     * 撤销短信
     *
     * @return 如果实际有撤销则返回true，否则返回false（当所有短信均已发送或已撤销时无法返回false）
     * @throws Exception 数据库操作错误
     */
    @Service(method = HttpMethod.post)
    public boolean cancel() throws Exception
    {
        return dao.cancelSms(getKeys()) > 0;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (replyed == 1)
            return "content is not null";
        else if (replyed == 2)
            return "content is null";

        return null;
    }

    @Override
    protected Object createListView() throws Exception
    {
        boolean requireReply = getSms().getRequireReply() == null || getSms().getRequireReply();

        PageTableView view = new PageTableView(true);

        view.setTitle("短信跟踪");
        view.setRemark("查看短信的所有接收者,并跟踪每个接收者的回复内容");

        view.importJs("/platform/message/sms.js");

        view.addComponent("接收人", "userName");
        view.addComponent("电话", "phone");

        if (requireReply)
        {
            view.addComponent("回复状态", new CCombox("replyed", new KeyValue[]{
                    new KeyValue<String>("0", "全部"),
                    new KeyValue<String>("1", "已回复"),
                    new KeyValue<String>("2", "未回复")
            }).setNullable(false));
        }

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));
        view.addButton(new CButton("重新发送", "resend();"));
        view.addButton(new CButton("撤消发送", "cancel();"));

        view.addColumn("接收人", "userName");
        view.addColumn("电话", "phone");
        view.addColumn("接收时间", "smsLog.state.name()=='canceled'?smsLog.state:receiveTime")
                .setAlign(Align.right).setOrderFiled("receiveTime").setWidth("140");

        if (requireReply)
        {
            view.addColumn("回复内容", "content").setAutoExpand(true).setWrap(true);
            view.addColumn("回复时间", "firstReplyTime");
        }

        view.addColumn("错误信息", "error").setAutoExpand(!requireReply);

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("短信回复记录");
    }
}
