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
@Service(url = "/ods/archive/inner")
public class InnerArchivePage extends ToArchivePage<OdFlowInstance>
{
    @Inject
    private OdFlowDao odFlowDao;

    public InnerArchivePage()
    {
        addOrderBy("startTime", OrderType.desc);
        addForceLoad("document");
        addForceLoad("createUser");
    }

    @Override
    @NotSerialized
    @Lower(column = "startTime")
    public Date getStartTime()
    {
        return super.getStartTime();
    }

    @Override
    @NotSerialized
    @Upper(column = "startTime")
    public Date getEndTime()
    {
        return super.getEndTime();
    }

    @Override
    protected OfficeDocument getDocument(OdFlowInstance instance)
    {
        return instance.getDocument();
    }

    @Override
    protected Long getInstanceId(OdFlowInstance instance) throws Exception
    {
        return instance.getInstanceId();
    }

    @Override
    protected java.util.Date getDocTime(OdFlowInstance instance)
    {
        return instance.getStartTime();
    }

    @Override
    public String getAlias()
    {
        return "i";
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        join(Archive.class, "archive",
                "archive.deptId=:deptId and archive.year=:year and archive.documentId=i.documentId", JoinType.left);

        join(Send.class, "send", "send.deptId=:deptId and send.documentId=i.documentId", JoinType.left);
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "i.state=1 and i.type in ('send','inner') and i.document.textId is not null" +
                " and archive.archiveId is null and send.sendId is null";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("文件名称", "title");
        view.addComponent("文件内容", "text");

        //附件
        view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                .setHref("/ods/document/${encodedDocumentId}/attachment").setTarget("_blank")
                .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

        view.addColumn("文件名称", new HrefCell("document.title").setAction("openFlow(${instanceId})")).setWrap(true);
        view.addColumn("拟稿时间", new FieldCell("startTime").setFormat("yyyy-MM-dd HH:mm")).setWidth("120")
                .setAlign(Align.center).setWrap(true);
        view.addColumn("拟稿人", "createUser.userName").setWidth("100").setWrap(true);

        view.importJs("/ods/archive/inner.js");
        view.importJs("/ods/archive/to_archive.js");

        view.addButton(Buttons.query());
        view.addButton("将选择的文件归档", "catalogSelected()");
        view.addButton("将查询到的文件归档", "catalogQueryResult()");

        return view;
    }
}
