package com.gzzm.ods.archive;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.exchange.Send;
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
 * 发文归档
 *
 * @author camel
 * @date 2016/6/12
 */
@Service(url = "/ods/archive/send")
public class SendArchivePage extends ToArchivePage<Send>
{
    @Inject
    private OdFlowDao odFlowDao;

    public SendArchivePage()
    {
        addOrderBy("sendTime", OrderType.desc);
        addForceLoad("document");
        addForceLoad("createUser");
    }

    @Override
    @NotSerialized
    @Lower(column = "sendTime")
    public Date getStartTime()
    {
        return super.getStartTime();
    }

    @Override
    @NotSerialized
    @Upper(column = "sendTime")
    public Date getEndTime()
    {
        return super.getEndTime();
    }

    @Override
    protected OfficeDocument getDocument(Send send)
    {
        return send.getDocument();
    }

    @Override
    protected Long getInstanceId(Send entity) throws Exception
    {
        SendFlowInstance sendFlowInstance = odFlowDao.getSendFlowInstanceByDocumentId(entity.getDocumentId());
        if (sendFlowInstance != null)
            return sendFlowInstance.getInstanceId();

        return null;
    }

    @Override
    protected java.util.Date getDocTime(Send send)
    {
        return send.getSendTime();
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        join(Archive.class, "archive",
                "archive.deptId=:deptId and archive.year=:year and archive.documentId=send.documentId", JoinType.left);
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "send.state=0 and archive.archiveId is null";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("文件名称", "title");
        view.addComponent("文件内容", "text");
        view.addComponent("发文字号", "sendNumber");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                .setHref("/ods/document/${encodedDocumentId}/attachment").setTarget("_blank")
                .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("文件名称", new HrefCell("document.title").setAction("showSend(${sendId})")).setWrap(true);
        view.addColumn("发文字号", "document.sendNumber").setWidth("140").setWrap(true);
        view.addColumn("发文时间", new FieldCell("sendTime").setFormat("yyyy-MM-dd HH:mm")).setWidth("110")
                .setAlign(Align.center).setWrap(true);
        view.addColumn("紧急程度", "document.priority").setWidth("80").setAlign(Align.center);
        view.addColumn("拟稿人", "createUser.userName").setWidth("80").setWrap(true);
        view.addColumn("发文稿笺", new CHref("发文稿笺")
        {
            @Override
            public Object display(Object obj, String expression, UIManager uiManager) throws Exception
            {
                Send send = (Send) obj;
                SendFlowInstance sendFlowInstance =
                        odFlowDao.getSendFlowInstanceByDocumentId(send.getDocumentId());

                if (sendFlowInstance != null)
                    return super.display(obj, expression, uiManager);
                else
                    return null;
            }
        }.setAction("openFlow(${sendId})")).setWidth("65");

        view.importJs("/ods/archive/send.js");
        view.importJs("/ods/archive/to_archive.js");

        view.addButton(Buttons.query());
        view.addButton("将选择的文件归档", "catalogSelected()");
        view.addButton("将查询到的文件归档", "catalogQueryResult()");

        return view;
    }
}
