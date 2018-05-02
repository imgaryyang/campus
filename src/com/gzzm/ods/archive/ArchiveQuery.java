package com.gzzm.ods.archive;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;

/**
 * 归档文件查询
 *
 * @author camel
 * @date 2016/6/13
 */
@Service(url = "/ods/archive/archive")
public class ArchiveQuery extends ArchiveBasePage<Archive>
{
    private Integer serial;

    @NotCondition
    private Integer catalogId;

    private String timeLimit;

    private boolean readOnly;

    @NotSerialized
    private ArchiveConfig config;

    public ArchiveQuery()
    {
        addOrderBy("catalog.orderId");
        addOrderBy("serial");
    }

    @Override
    public boolean isReadOnly()
    {
        return readOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public Integer getCatalogId() throws Exception
    {
        if (getEntity() != null && getCatalogName() != null)
            return super.getCatalogId();
        else
            return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public String getTimeLimit()
    {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit)
    {
        this.timeLimit = timeLimit;
    }

    @Override
    @Like("title")
    public String getTitle()
    {
        return super.getTitle();
    }

    @Override
    @Like("sendNumber")
    public String getSendNumber()
    {
        return super.getSendNumber();
    }

    @Override
    @Like("author")
    public String getAuthor()
    {
        return super.getAuthor();
    }

    @Override
    @Like("remark")
    public String getRemark()
    {
        return super.getRemark();
    }

    public Integer getSerial()
    {
        return serial;
    }

    public void setSerial(Integer serial)
    {
        this.serial = serial;
    }

    public ArchiveConfig getConfig() throws Exception
    {
        if (config == null)
            config = service.getConfig(getDeptId());

        return config;
    }

    public String getArchiveNo(Archive archive) throws Exception
    {
        ArchiveConfig config = getConfig();

        return config.getGeneralNo() + "-" + getYear() + "-" + archive.getTimeLimit() + "-" +
                StringUtils.leftPad(Integer.toString(rank("").by(archive.getArchiveId())), 4, '0');
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (catalogId != null && catalogId > 0)
            return "catalogId=:catalogId";

        return null;
    }

    @Override
    public String getMetaCondition() throws Exception
    {
        if ("exp".equals(getAction()))
            return "deptId=:deptId and year=:year";
        else
            return super.getMetaCondition();
    }

    @Service(method = HttpMethod.post)
    public void up(Long archiveId) throws Exception
    {
        service.up(archiveId);
    }

    @Service(method = HttpMethod.post)
    public void down(Long archiveId) throws Exception
    {
        service.down(archiveId);
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("案卷目录", new CCombox("this.catalogName").setEditable(true).setNullable(true));
        view.addComponent("保管期限", "timeLimit");
        view.addComponent("页数", "pagesCount");
        view.addComponent("责任人", "author");
        view.addComponent("备注", new CTextArea("remark"));

        view.addDefaultButtons();

        return view;
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        setCatalogName(getEntity().getCatalog().getCatalogName());
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        Integer catalogId = getCatalogId();

        if (!catalogId.equals(getEntity().getCatalogId()))
        {
            service.lockCatalog(catalogId);
            service.lockCatalog(getEntity().getCatalogId());
            service.getDao().upSerials(getEntity().getCatalogId(), getEntity().getSerial());

            getEntity().setCatalogId(catalogId);
            getEntity().setSerial(service.getDao().getMaxSerial(catalogId) + 1);
        }

        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        ArchiveCatalogDisplay catalogDisplay = Tools.getBean(ArchiveCatalogDisplay.class);
        catalogDisplay.setYear(getYear());
        catalogDisplay.setDeptId(getDeptId());

        ComplexTableView view = new ComplexTableView(catalogDisplay, "catalogId", false);

        view.addComponent("文件名称", "title");
        view.addComponent("文件内容", "text");
        view.addComponent("发文字号", "sendNumber");

        if ("exp".equals(getAction()))
        {
            view.addColumn("全宗名称", "crud$.config.generalName");
            view.addColumn("全宗号", "crud$.config.generalNo");
            view.addColumn("档号", "crud$.getArchiveNo(this)");
            view.addColumn("归档年度", "year");
            view.addColumn("保管期限", "timeLimit");
            view.addColumn("件号", "crud$.index$");
            view.addColumn("附注", "remark");
            view.addColumn("文件标题", "title");
            view.addColumn("文件编号", "sendNumber");
            view.addColumn("责任者", "author");
            view.addColumn("文件日期", "docDate");
        }
        else
        {
            view.addColumn("附件", new CImage(Tools.getCommonIcon("attachment"))
                    .setHref("/ods/document/${documentId}/attachment").setTarget("_blank")
                    .setProperty("style", "cursor:pointer").setPrompt("点击下载附件"))
                    .setDisplay("${document.attachment}").setLocked(true).setWidth("27").setHeader(
                    new CImage(Tools.getCommonIcon("attachment")).setPrompt("附件"));

            view.addColumn("文件名称", "title");
            view.addColumn("原文号", "sendNumber").setWidth("90").setWrap(true);
            view.addColumn("责任者", "author");
            view.addColumn("文件日期", new FieldCell("docTime").setFormat("yyyy-MM-dd")).setWidth("85");
            view.addColumn("保管期限", "timeLimit").setWidth("65").setAlign(Align.center);
            view.addColumn("页数", "pagesCount").setWidth("50").setAlign(Align.center);
            view.addColumn("备注", "remark").setWidth("120").setWrap(true);
            view.addColumn("正文", new CHref("正文", "/ods/document/${documentId}/text/down").
                    setPrompt("点击下载正文").setTarget("_blank")).setWidth("40");
            view.addColumn("文件处理笺", new CHref("${instanceId!=null?'文件处理笺':''}").setAction("openFlow(${instanceId})"))
                    .setWidth("80");


            if (!readOnly)
            {
                if (catalogId != null && catalogId > 0)
                {
                    view.addColumn("上移", new CButton("上移", "up(${archiveId})")).setWidth("50");
                    view.addColumn("下移", new CButton("下移", "down(${archiveId})")).setWidth("50");
                }
                else
                {
                    view.addColumn("上移", new CLabel("")).setWidth("45");
                    view.addColumn("下移", new CLabel("")).setWidth("45");
                }

                view.addColumn("修改归档信息", new CButton("修改归档信息", Actions.show(null))).setWidth("90");
            }
        }

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("导出word", new String[]{"doc"}));
        view.addButton(Buttons.export("导出excel", new String[]{"xls"}));

        view.importJs("/ods/archive/archive.js");

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        ExportParameters parameters = super.getExportParameters();

        XlsDataExporter.setTable(parameters, "数据");

        return parameters;
    }
}
