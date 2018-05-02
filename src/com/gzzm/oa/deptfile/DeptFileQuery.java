package com.gzzm.oa.deptfile;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Null;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;

import java.sql.Date;
import java.util.Map;

/**
 * 部门文件查询 继承自DeptOwnedQuery，以实现权限控制，实现只能查看用户有权限查看的文件 ccs
 *
 * @author : ccs
 * @date : 2010-3-10
 */
@Service(url = "/oa/deptfile/query")
public class DeptFileQuery extends DeptOwnedQuery<DeptFile, Integer>
{
    @Like
    private String fileName;

    /**
     * 根目录ID
     */
    private Integer folderId;

    /**
     * 全文检索
     */
    @Contains
    private String text;

    /**
     * 以k为单位的文件大小查询条件
     */
    private Integer fileSizeK_min;

    private Integer fileSizeK_max;

    @Lower(column = "uploadTime")
    private Date time_start;

    @Upper(column = "uploadTime")
    private Date time_end;

    /**
     * 用文件类型做查询条件
     */
    private String fileType;

    /**
     * 是否查询所有的文件，而不逐个目录查询
     */
    private boolean all;

    public DeptFileQuery()
    {
        addOrderBy("uploadTime", OrderType.desc);
        addOrderBy("fileId", OrderType.desc);
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public Integer getFolderId()
    {
        return folderId;
    }

    public void setFolderId(Integer folderId)
    {
        this.folderId = folderId;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Integer getFileSizeK_min()
    {
        return fileSizeK_min;
    }

    public void setFileSizeK_min(Integer fileSizeK_min)
    {
        this.fileSizeK_min = fileSizeK_min;
    }

    @Lower(column = "fileSize")
    @NotSerialized
    public Integer getFileSize_min()
    {
        if (fileSizeK_min != null && !Null.isNull(fileSizeK_min))
            return fileSizeK_min * 1024;
        return null;
    }

    public Integer getFileSizeK_max()
    {
        return fileSizeK_max;
    }

    public void setFileSizeK_max(Integer fileSizeK_max)
    {
        this.fileSizeK_max = fileSizeK_max;
    }

    @Upper(column = "fileSize")
    @NotSerialized
    public Integer getFileSize_max()
    {
        if (fileSizeK_max != null && !Null.isNull(fileSizeK_max))
            return fileSizeK_max * 1024;
        return null;
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

    public String getFileType()
    {
        return fileType;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    public boolean isAll()
    {
        return all;
    }

    public void setAll(boolean all)
    {
        this.all = all;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;
        if (all)
        {
            view = new PageTableView();
        }
        else
        {
            DeptFileFolderDisplay folderTree = Tools.getBean(DeptFileFolderDisplay.class);
            folderTree.setDeptId(getDeptId());
            view = new ComplexTableView(folderTree, "folderId", true).enableDD();
        }

        view.addComponent("文件名称", "fileName").setWidth("150px");
        if (all)
            view.addComponent("文件内容", "text").setWidth("150px");

        view.addComponent("文件类型", "fileType").setWidth("45px");

        if (all)
        {
            view.addMoreComponent("    文件大小(KB) 大于", "fileSizeK_min").setWidth("80px");
            view.addMoreComponent("小于", "fileSizeK_max").setWidth("80px");

            view.addMoreComponent("    上传时间 从", "time_start");
            view.addMoreComponent("到", "time_end");
        }

        view.addButton(Buttons.query());

        view.addColumn("文件名称", new DeptFileNameCell());
        view.addColumn("类型", "fileType");

        if (all)
            view.addColumn("所属目录", "fileFolder.folderName");

        view.addColumn("创建人", "createUser.userName");

        if (getAuthDeptIds() == null || getAuthDeptIds().size() > 1)
        {
            view.addColumn("所属部门", "dept.deptName");
        }

        view.addColumn("创建时间", new FieldCell("uploadTime").setFormat("yyyy-MM-dd HH:mm")).setWidth("110")
                .setHidden(true);
        view.addColumn("更新时间", new FieldCell("updateTime").setFormat("yyyy-MM-dd HH:mm")).setWidth("110");
        view.addColumn("大小", new FieldCell("fileSize").setFormat("bytesize")).setWidth("65");
        view.addColumn("来源", "sourceName").setOrderFiled("source").setWidth("80").setAlign(Align.center);

        view.addColumn("查看/下载", new CUnion(
                new ConditionComponent().add("editFile||canShow()", new CHref("查看").setAction(
                        "display(${fileId},${editFile.toString()},'${fileType}')")),
                new ConditionComponent().add("!editFile", new CHref("下载",
                        "/oa/deptfile/${fileId}/down").setTarget("_blank"))))
                .setAlign(Align.center).setWidth("80").setLocked(true);

        view.importJs("/oa/deptfile/deptfile.js");
        view.importCss("/oa/deptfile/deptfile.css");
        return view;
    }


    @Override
    protected void beforeShowList() throws Exception
    {
        if (!all)
        {
            folderId = 0;
        }
    }

    @Service
    public Integer getNextImgFile(Integer fileId) throws Exception
    {
        DeptFile file = getEntity(fileId);

        Map<String, Object> parameters = toMap();
        parameters.put("uploadTime", file.getUploadTime());
        parameters.put("fileId", file.getFileId());

        return getCrudService().oqlQueryFirst("select fileId from DeptFile deptfile where (" + createCondition() +
                ") and (uploadTime<:uploadTime or uploadTime=:uploadTime and fileId<:fileId) " +
                " and fileType in ('jpg','jpeg','gif','bmp','png','JPG','JPEG','GIF','BMP','PNG') " +
                "order by uploadTime desc,fileId desc", Integer.class, parameters);
    }

    @Service
    public Integer getPreImgFile(Integer fileId) throws Exception
    {
        DeptFile file = getEntity(fileId);

        Map<String, Object> parameters = toMap();
        parameters.put("uploadTime", file.getUploadTime());
        parameters.put("fileId", file.getFileId());

        return getCrudService().oqlQueryFirst("select fileId from DeptFile deptfile where (" + createCondition() +
                ") and (uploadTime>:uploadTime or uploadTime=:uploadTime and fileId>:fileId) " +
                " and fileType in ('jpg','jpeg','gif','bmp','png','JPG','JPEG','GIF','BMP','PNG') " +
                "order by uploadTime,fileId", Integer.class, parameters);
    }
}
