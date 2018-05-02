package com.gzzm.ods.archive;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.exchange.ReceiveBase;
import com.gzzm.ods.flow.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.util.JoinType;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 收文归档
 *
 * @author camel
 * @date 2016/5/11
 */
@Service(url = "/ods/archive/receive")
public class ReceiveArchivePage extends ToArchivePage<ReceiveBase>
{
    @Inject
    private OdFlowDao dao;

    /**
     * 来文单位
     */
    @Like("document.sourceDept")
    private String sourceDept;

    public ReceiveArchivePage()
    {
        addOrderBy("sendTime", OrderType.desc);
    }

    public String getSourceDept()
    {
        return sourceDept;
    }

    public void setSourceDept(String sourceDept)
    {
        this.sourceDept = sourceDept;
    }

    @Override
    @NotSerialized
    @Lower(column = "acceptTime")
    public Date getStartTime()
    {
        return super.getStartTime();
    }

    @Override
    @NotSerialized
    @Upper(column = "acceptTime")
    public Date getEndTime()
    {
        return super.getEndTime();
    }

    @Override
    protected OfficeDocument getDocument(ReceiveBase receiveBase) throws Exception
    {
        return receiveBase.getDocument();
    }

    @Override
    protected Long getInstanceId(ReceiveBase receiveBase) throws Exception
    {
        OdFlowInstance odFlowInstance = dao.getOdFlowInstanceByReceiveId(receiveBase.getReceiveId());
        if (odFlowInstance != null)
            return odFlowInstance.getInstanceId();

        return null;
    }

    @Override
    protected java.util.Date getDocTime(ReceiveBase receiveBase) throws Exception
    {
        return receiveBase.getAcceptTime();
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

        join(Archive.class, "archive",
                "archive.deptId=:deptId and archive.year=:year and archive.documentId=receive.documentId",
                JoinType.left);
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "receive.state in (1,2,3) and archive.archiveId is null and type=0";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("文件名称", "title");
        view.addComponent("文件内容", "text");
        view.addComponent("来文单位", "sourceDept");
        view.addComponent("发文字号", "sendNumber");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                .setHref("/ods/document/${encodedDocumentId}/attachment").setTarget("_blank")
                .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("文件名称", new HrefCell("document.title").setAction("showReceive(${receiveId})")).setWrap(true);
        view.addColumn("来文单位", "document.sourceDept").setWidth("90").setWrap(true);
        view.addColumn("发文字号", "document.sendNumber").setWidth("140").setWrap(true);
        view.addColumn("发文时间", new FieldCell("sendTime").setFormat("yyyy-MM-dd HH:mm")).setWidth("110")
                .setAlign(Align.center).setWrap(true);
        view.addColumn("收文时间", new FieldCell("acceptTime").setFormat("yyyy-MM-dd HH:mm")).setWidth("110")
                .setAlign(Align.center).setWrap(true);
        view.addColumn("紧急程度", "document.priority").setWidth("80").setAlign(Align.center);
        view.addColumn("文件处理笺", new CHref("文件处理笺").setAction("openFlow(${receiveId})")).setWidth("80");

        view.importJs("/ods/archive/receive.js");
        view.importJs("/ods/archive/to_archive.js");

        view.addButton(Buttons.query());
        view.addButton("将选择的文件归档", "catalogSelected()");
        view.addButton("将查询到的文件归档", "catalogQueryResult()");

        return view;
    }
}
