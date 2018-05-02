package com.gzzm.ods.receipt.meeting;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 12-11-5
 */
@Service(url = "/ods/receipt/meeting/items/query")
public class ReceiptMeetingItemQuery extends BaseQueryCrud<ReceiptMeetingItem, Long>
{
    @Inject
    private MeetingReceiptDao dao;

    private Long receiptId;

    @Like("dept.deptName")
    private String deptName;

    @Like
    private String userName;

    public ReceiptMeetingItemQuery()
    {
        addOrderBy("dept.leftValue");
        addOrderBy("orderId");
    }

    public Long getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "replied=1";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.setTitle("会议回执反馈信息");

        view.addComponent("部门名称", "deptName");
        view.addComponent("参会人员", "userName");

        view.addColumn("参会人员", "userName").setWidth("100");
        view.addColumn("部门名称", "dept.deptName").setWidth("150");
        view.addColumn("性别", "sex");
        view.addColumn("联系电话", "phone").setWidth("100");
        view.addColumn("职务", "station").setWidth("100");
        view.addColumn("参会时段", "joinTime").setWidth("150");
        view.addColumn("食宿安排", "accommodation");
        view.addColumn("备注", "remark").setAutoExpand(true);

        view.defaultInit();
        view.addButton(Buttons.export("xls"));

        ReceiptMeeting meeting = dao.getMeeting(receiptId);
        if (meeting != null && meeting.getAttachmentId() != null)
            view.addButton("查看附件", "showAttachments(" + meeting.getReceiptId() + ")");

        view.importJs("/ods/receipt/meeting/query.js");

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("参会人员列表");
    }
}
