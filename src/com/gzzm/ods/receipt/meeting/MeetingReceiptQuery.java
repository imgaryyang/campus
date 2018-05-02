package com.gzzm.ods.receipt.meeting;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.FieldCell;
import net.cyan.crud.view.components.*;

import java.sql.Date;
import java.util.Collection;


/**
 * @author camel
 * @date 12-11-5
 */
@Service(url = "/ods/receipt/meetingquery")
public class MeetingReceiptQuery extends DeptOwnedQuery<ReceiptMeeting, Long>
{
    @Like("receipt.document.title")
    private String title;

    @Like("receipt.document.sendNumber")
    private String sendNumber;

    @Upper(column = "receipt.sendTime")
    private Date time_start;

    @Lower(column = "receipt.sendTime")
    private Date time_end;

    @Contains("receipt.document.textContent")
    private String text;

    public MeetingReceiptQuery()
    {
        addOrderBy("receipt.sendTime", OrderType.desc);

        addForceLoad("receipt");
        addForceLoad("receipt.document");
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSendNumber()
    {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber)
    {
        this.sendNumber = sendNumber;
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

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    @Equals("receipt.deptId")
    @Override
    public Integer getDeptId()
    {
        return super.getDeptId();
    }

    @In("receipt.deptId")
    @Override
    public Collection<Integer> getQueryDeptIds()
    {
        return super.getQueryDeptIds();
    }

    @Override
    protected String getUserIdField() throws Exception
    {
        return "receipt.creator";
    }

    @Override
    protected Object createListView() throws Exception
    {
        Collection<Integer> authDeptIds = getAuthDeptIds();
        boolean showDeptTree = (authDeptIds == null || authDeptIds.size() > 3);

        PageTableView view = showDeptTree ? new ComplexTableView(new AuthDeptDisplay(), "deptId", false) :
                new PageTableView(false);

        view.addComponent("文件标题", "title");
        view.addComponent("发文字号", "sendNumber");
        view.addComponent("发送时间", "time_start", "time_end");
        view.addComponent("文件内容", "text");

        view.addColumn("文件标题", "receipt.document.title").setWrap(true);
        view.addColumn("会议名称", "meetingName").setWidth("150").setWrap(true);
        view.addColumn("发文字号", "receipt.document.sendNumber").setWrap(true);
        view.addColumn("发送时间", new FieldCell("receipt.sendTime", "yyy-MM-dd HH:mm")).setWidth("95");
        view.addColumn("会议开始时间", new FieldCell("startTime", "yyy-MM-dd HH:mm")).setWidth("95");
        view.addColumn("会议结束时间", new FieldCell("endTime", "yyy-MM-dd HH:mm")).setWidth("95");
        view.addColumn("会议地点", "location").setWrap(true);
        view.addColumn("反馈", new CHref("反馈").setAction("showItems(${receiptId})")).setWidth("40");
        view.addColumn("附件", new ConditionComponent().add("attachmentId!=null",
                new CHref("附件").setAction("showAttachments(${attachmentId})"))).setWidth("40");

        view.defaultInit();

        view.importJs("/ods/receipt/meeting/query.js");

        return view;
    }
}
