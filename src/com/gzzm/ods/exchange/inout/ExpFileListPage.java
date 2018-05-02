package com.gzzm.ods.exchange.inout;

import com.gzzm.ods.query.OdDocFile;
import com.gzzm.platform.attachment.AttachmentService;
import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.InputFile;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.CLabel;
import net.cyan.nest.annotation.Inject;

/**
 * 公文导出文件列表
 *
 * @author LDP
 * @date 2017/4/25
 */
@Service(url = "/ods/exchange/inout/expfilelistpage")
public class ExpFileListPage extends DeptOwnedNormalCrud<OdDocFile, Integer>
{
    @Inject
    private AttachmentService attachmentService;

    private Integer docFileType;

    @Like
    private String remark;

    public ExpFileListPage()
    {
        addOrderBy("actionTime", OrderType.desc);
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getDocFileType()
    {
        return docFileType;
    }

    public void setDocFileType(Integer docFileType)
    {
        this.docFileType = docFileType;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        //这里只查出收发文json的导出记录
        return " docFileType in (2,3) ";
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("文件说明", "remark");

        view.addColumn("文件说明", "remark");

        if(docFileType == null) {
            view.addColumn("导出文件类型", new CLabel("${docFileType == 2 ? '发文' : '收文'}")).setAlign(Align.center).setWidth("100");
        }

        view.addColumn("所属部门", "dept.deptName").setWidth("200");
        view.addColumn("导出操作时间", "actionTime").setWidth("150");
        view.addColumn("文件生成完成时间", "finishTime").setWidth("150");
        view.addColumn("文件下载", new BaseSimpleCell()
        {
            @Override
            public Object getValue(Object entity) throws Exception
            {
                OdDocFile docFile = (OdDocFile) entity;

                if(docFile.getState() == 2)
                {
                    return "文件生成失败";
                }

                if(docFile.getState() == 1)
                {
                    return "<a href='#' onclick='downloadFile(" + docFile.getDocFileId() + ")'>文件下载</a>";
                }
                else
                {
                    return "文件生成中";
                }
            }
        }).setWidth("120").setAlign(Align.center);

        view.setCheckable("${state != null && state > 0}");

        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());

        return view;
    }

    @Service
    public InputFile downloadFile(Integer docFileId) throws Exception
    {
        OdDocFile docFile = getEntity(docFileId);

        if(docFile.getAttachmentId() == null)
            throw new NoErrorException("下载失败，文件可能被删除或尚未生成完成");

        return attachmentService.getAttachment(docFile.getAttachmentId(), 1).getInputFile();
    }

    @Service(url = "/ods/exchange/inout/remark")
    public String showRemarkPage()
    {
        return "/ods/exchange/inout/remark.ptl";
    }
}
