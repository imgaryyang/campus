package com.gzzm.ods.query;

import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.CLabel;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 公文导出文件
 *
 * @author LDP
 * @date 2015/11/27
 */
@Service(url = "/ods/docfilelist")
public class DocFileList extends UserOwnedQuery<OdDocFile, Integer>
{
    @Inject
    private AttachmentService attachmentService;

    @Inject
    private OdDocFileDao odDocFileDao;

    public DocFileList()
    {
        addOrderBy("actionTime", OrderType.desc);
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        //这里只查出发文簿和收文簿的导出记录
        return " docFileType in (0, 1) ";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addColumn("导出文件类型", new CLabel("${docFileType == 0 ? '发文' : '收文'}")).setAlign(Align.center)
                .setWidth("150");
        view.addColumn("导出操作时间", "actionTime").setWidth("200");
        view.addColumn("文件生成完成时间", "finishTime").setWidth("200");
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
        }).setWidth("150").setAlign(Align.center);

        return view;
    }

    @Service
    public InputFile downloadFile(Integer docFileId) throws Exception
    {
        OdDocFile docFile = getEntity(docFileId);

        if(docFile.getContent() != null)
            return new InputFile(docFile.getContent(), (docFile.getDocFileType() == 0 ? "发文数据" : "收文数据") + ".zip");

        if(docFile.getAttachmentId() == null)
            throw new NoErrorException("下载失败，文件可能被删除或尚未生成完成");

        return new InputFile(attachmentService.getAttachment(docFile.getAttachmentId(), 1).getBytes(),
                (docFile.getDocFileType() == 0 ? "发文数据" : "收文数据") + ".zip");
    }

    @Service(url = "/ods/query/docfile/dbtodiskpage")
    public String dbToDiskPage()
    {
        return "/ods/query/dbtodisk.ptl";
    }

    @Service
    public void toDisk() throws Exception
    {
        List<OdDocFile> docFiles = odDocFileDao.getDbFileList();
        if(CollectionUtils.isEmpty(docFiles)) return;

        DbToDiskProgressInfo progressInfo = RequestContext.getContext().getProgressInfo(DbToDiskProgressInfo.class);
        progressInfo.setTotal(docFiles.size());

        Tools.log("====开始释放单位公文数据库空间====");
        for (OdDocFile f : docFiles)
        {
            Attachment attachment = new Attachment();
            attachment.setInputable(new Inputable.StreamInput(f.getContent()));
            attachment.setUserId(f.getUserId());
            attachment.setDeptId(f.getDeptId());
            if(f.getDocFileType() == 0)
            {
                attachment.setAttachmentName("发文数据.zip");
                attachment.setFileName("发文数据.zip");
            }
            else if(f.getDocFileType() == 1)
            {
                attachment.setAttachmentName("收文数据.zip");
                attachment.setFileName("收文数据.zip");
            }

            List<Attachment> attachments = new ArrayList<Attachment>(1);
            attachments.add(attachment);
            f.setAttachmentId(attachmentService.save(attachments));
            f.setContent(Null.Stream);
            odDocFileDao.save(f);
            progressInfo.setFinished(progressInfo.getFinished() + 1);
            Tools.log("======共需要释放" + docFiles.size() + "条记录，已释放" + progressInfo.getFinished() + "条=======");
        }

        Tools.log("=====单位公文数据库空间已释放完毕======");
    }

    public static class DbToDiskProgressInfo implements ProgressInfo
    {
        private Integer total = 0;

        private Integer finished = 0;

        public DbToDiskProgressInfo()
        {
        }

        public Integer getTotal()
        {
            return total;
        }

        public void setTotal(Integer total)
        {
            this.total = total;
        }

        public Integer getFinished()
        {
            return finished;
        }

        public void setFinished(Integer finished)
        {
            this.finished = finished;
        }

        @Override
        public String getProgressName()
        {
            return "正在从数据库中将文件转换到硬盘";
        }

        @Override
        public float getPercentage()
        {
            return (finished * 1.0f)/ total;
        }

        @Override
        public String getDescription()
        {
            return "共需要处理" + total + "个文件，已完成" + finished + "个";
        }
    }
}
