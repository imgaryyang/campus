package com.gzzm.ods.query;

import com.gzzm.ods.exchange.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.util.JoinType;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;

import java.sql.Date;

/**
 * @author fwj
 * @date 2011-7-6
 */
@Service(url = "/ods/query/receive2")
public class ReceiveQuery2 extends DeptOwnedQuery<ReceiveBase, Long>
{
    /**
     * 文件名称
     */
    @Like("document.title")
    private String title;

    /**
     * 原文号
     */
    @Like("document.sendNumber")
    private String sendNumber;

    /**
     * 来文单位
     */
    @Like("document.sourceDept")
    private String sourceDept;

    /**
     * 主题词
     */
    @Like("document.subject")
    private String subject;

    @Lower(column = "sendTime")
    private Date sendTime_start;

    @Upper(column = "sendTime")
    private Date sendTime_end;

    @Lower(column = "acceptTime")
    private Date acceptTime_start;

    @Upper(column = "acceptTime")
    private Date acceptTime_end;

    @Contains("document.textContent")
    private String text;

    private ReceiveState state;

    private ReceiveMethod method;

    public ReceiveQuery2()
    {
        addOrderBy("receive.sendTime", OrderType.desc);
        addOrderBy("receive.acceptTime", OrderType.desc);
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

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public Date getSendTime_start()
    {
        return sendTime_start;
    }

    public void setSendTime_start(Date sendTime_start)
    {
        this.sendTime_start = sendTime_start;
    }

    public Date getAcceptTime_start()
    {
        return acceptTime_start;
    }

    public void setAcceptTime_start(Date acceptTime_start)
    {
        this.acceptTime_start = acceptTime_start;
    }

    public Date getAcceptTime_end()
    {
        return acceptTime_end;
    }

    public void setAcceptTime_end(Date acceptTime_end)
    {
        this.acceptTime_end = acceptTime_end;
    }

    public Date getSendTime_end()
    {
        return sendTime_end;
    }

    public void setSendTime_end(Date sendTime_end)
    {
        this.sendTime_end = sendTime_end;
    }

    public ReceiveState getState()
    {
        return state;
    }

    public void setState(ReceiveState state)
    {
        this.state = state;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public ReceiveMethod getMethod()
    {
        return method;
    }

    public void setMethod(ReceiveMethod method)
    {
        this.method = method;
    }

    @Override
    @Forward(page = "/ods/query/receive2.ptl")
    public String show(Long key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    public String getAlias()
    {
        return "receive";
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        StringBuilder builder =
                new StringBuilder("receive2.documentId=receive.documentId and receive2.receiveId>receive.receiveId");

        if (method != null)
            builder.append(" and receive2.method=:method");

        if (getAuthDeptIds() != null)
            builder.append(" and receive2.deptId in :authDeptIds");

        join(ReceiveBase.class, "receive2", builder.toString(), JoinType.left);
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        StringBuilder builder = new StringBuilder("receive.type=0");
        if (state == null)
            builder.append(" and receive.state<4");

        builder.append(" and receive2.receiveId is null");

        return builder.toString();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("文件名", "title");
        view.addComponent("来文单位", "sourceDept");
        view.addComponent("发文时间", "sendTime_start", "sendTime_end");
        view.addComponent("文件内容", "text");

        if (state == null)
        {
            view.addComponent("状态", new CCombox("state", new ReceiveState[]{
                    ReceiveState.noAccepted,
                    ReceiveState.accepted,
                    ReceiveState.flowing,
                    ReceiveState.end
            }));
        }

        view.addMoreComponent("原文号", "sendNumber");
//        view.addMoreComponent("主题词", "subject");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                .setHref("/ods/document/${documentId}/attachment").setTarget("_blank")
                .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("文件名称", new HrefCell("document.titleText").setAction("show(${receiveId})"))
                .setOrderFiled("document.title").setWrap(true).setOrderFiled("receive.document.title");

        view.addColumn("来文单位", "document.sourceDept").setWidth("90").setWrap(true).setOrderFiled(
                "receive.document.sourceDept");
        view.addColumn("收文部门", "dept.deptName").setWidth("90").setWrap(true).setOrderFiled("receive.dept.deptName");

        view.addColumn("查看正文", new CHref("查看正文").setAction("showText(${documentId})")).setWidth("70");

        view.addColumn("原文号", "document.sendNumber").setWidth("95").setWrap(true)
                .setOrderFiled("receive.document.sendNumber");

        view.addColumn("发文时间", new FieldCell("sendTime").setFormat("yyyy-MM-dd\nHH:mm:ss")).setWidth(
                "80").setOrderFiled("receive.sendTime").setAlign(Align.center).setWrap(true);
        view.addColumn("接收时间", new FieldCell("acceptTime").setFormat("yyyy-MM-dd\nHH:mm:ss")).setWidth(
                "75").setOrderFiled("receive.acceptTime").setAlign(Align.center).setWrap(true);

        if (state == null)
            view.addColumn("状态", "state").setWidth("50").setOrderFiled("receive.state");

        view.addColumn("所有收文部门", new ConditionComponent().add("type.name()=='receive'&&method.name()!='manual'",
                new CHref("所有收文部门").setAction("allReceives(${documentId})"))).setWidth("85");

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        view.importJs("/ods/query/receive2.js");
        view.importCss("/ods/query/receive2.css");

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("收文列表");
    }
}
