package com.gzzm.oa.deptfile;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.archive.ArchiveUtils;
import com.gzzm.platform.commons.crud.*;
import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.exts.ParameterCheck;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.DownloadFile;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 个人文件管理 继承自DeptOwnedEditableCrud，以实现权限控制，实现只能维护用户有权限修改的文件 ccs
 *
 * @author : ccs
 * @date : 2010-3-10
 */
@Service(url = "/oa/deptfile/file")
public class DeptFileCrud extends DeptOwnedEditableCrud<DeptFile, Integer>
        implements OwnedCrud<DeptFile, Integer, Integer>
{
    static
    {
        ParameterCheck.addNoCheckURL("/oa/deptfile/file");
    }

    @Inject
    private static Provider<FileUploadService> uploadServiceProvider;

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

    @Lower(column = "updateTime")
    private Date time_start;

    @Upper(column = "updateTime")
    private Date time_end;

    /**
     * 用文件类型做查询条件
     */
    private String fileType;

    /**
     * 撰写文件字符串，用于接收请求和显示文件
     */
    private String content;

    /**
     * content字段转化为bytes，写死以"UTF-8"编码保存，以适应不同的系统环境;
     */
    private byte[] contentBytes;

    /**
     * 上传的文件
     */
    private InputFile file;

    /**
     * 文件相关的数据操作
     */
    @Inject
    private DeptFileService service;

    /**
     * 是否查询所有的文件，而不逐个目录查询
     */
    private boolean all;

    public DeptFileCrud()
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

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
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

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public boolean isAll()
    {
        return all;
    }

    public void setAll(boolean all)
    {
        this.all = all;
    }

    private byte[] getContentBytes() throws Exception
    {
        if (contentBytes == null)
        {
            if (content != null)
                contentBytes = content.getBytes("UTF-8");
            else
                contentBytes = new byte[0];
        }

        return contentBytes;
    }

    @Override
    public String getOrderField()
    {
        return null;
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

        if (getAuthDeptIds() == null || getAuthDeptIds().size() > 1)
        {
            view.addComponent("选择部门", "deptId").setProperty("onchange", "selectDept()")
                    .setProperty("text", getDeptName());
        }

        view.addComponent("文件名称", "fileName").setWidth("150px");
        if (all)
            view.addComponent("文件内容", "text").setWidth("150px");

        view.addComponent("文件类型", "fileType").setWidth("45px");

        if (all)
        {
            view.addMoreComponent("    文件大小(KB) 大于", "fileSizeK_min").setWidth("80px");
            view.addMoreComponent("小于", "fileSizeK_max").setWidth("80px");

            view.addMoreComponent("    更新时间 从", "time_start");
            view.addMoreComponent("到", "time_end");
        }

        view.addButton(Buttons.query());
        if (!all)
        {
            view.addButton("上传文件", Actions.add(null)).setIcon(Buttons.getIcon("upload"));
            view.addButton("批量上传", "").setProperty("id", "batch");
            view.addButton("撰写文件", Actions.add("editfile")).setIcon(Buttons.getIcon("new"));
        }
        view.addButton(Buttons.delete());

        if (!all)
            view.addButton("下载目录", "zipFolder()");

        view.addColumn("文件名称", new DeptFileNameCell());
        view.addColumn("类型", "fileType").setHidden(true).setWidth("50");

        if (all)
            view.addColumn("所属目录", "fileFolder.folderName");

        view.addColumn("创建人", "createUser.userName");
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

        view.addColumn("修改记录", new CHref("修改记录").setAction("showBaks(${fileId})")).setWidth("70").setLocked(true);

        view.makeEditable();

        view.importJs("/oa/deptfile/deptfile.js");
        view.importCss("/oa/deptfile/deptfile.css");
        return view;
    }

    /**
     * 以html方式打开文件，仅当文件格式能转化为html时有效
     *
     * @param fileId 文件id
     * @return 显示文件的页面
     * @throws Exception 数据库操作异常或字节流转换异常
     */
    @Redirect
    @Service(url = "/oa/deptfile/{$0}/html")
    public String showHtml(Integer fileId) throws Exception
    {
        DeptFile deptFile = getEntity(fileId);


        DeptFileContent fileContent = service.getFileContent(fileId);

        String fileType = deptFile.getFileType();
        if (fileType == null)
            return null;

        if (OfficeUtils.canChangeToHtml(fileType))
        {
            return OfficeUtils.toHtml(fileContent.getContent(), fileType);
        }
        else if (ArchiveUtils.isArchive(fileType))
        {
            return ArchiveUtils.archive(
                    new InputFile(fileContent.getContent(), deptFile.getFileName() + "." + fileType));
        }

        return null;
    }

    /**
     * 打开文件查看界面，仅当文件为编辑文件是有效
     *
     * @param fileId 文件id
     * @return 显示文件的页面
     * @throws Exception 数据库操作异常或字节流转换异常
     */
    @Service(url = "/oa/deptfile/{$0}/show")
    public String showFile(Integer fileId) throws Exception
    {
        setEntity(getEntity(fileId));

        DeptFileContent fileContent = service.getFileContent(fileId);

        Inputable in = fileContent.getContent();
        if (in == null)
        {
            // 考虑字段可能为空
            setContent("");
        }
        else
        {
            setContent(new String(in.getBytes(), "UTF-8"));
        }

        return "showfile";
    }

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @return 文件输入流
     * @throws Exception 数据库操作抛出异常
     * @author wmy
     */
    @Service(url = "/oa/deptfile/{$0}/down")
    public InputFile downloadFile(Integer fileId, Boolean show) throws Exception
    {
        DeptFile deptFile = getEntity(fileId);
        DeptFileContent content = service.getFileContent(fileId);

        // 考虑fileType为null的情况
        String fileName = deptFile.getFileName();
        if (!StringUtils.isEmpty(deptFile.getFileType()))
            fileName += "." + deptFile.getFileType();

        DownloadFile inputFile = new DownloadFile(content.getContent(), fileName);

        if (show != null)
            inputFile.setAttachment(!show);

        return inputFile;
    }

    @Override
    @Forwards({@Forward(name = "upload", page = Pages.EDIT_BIG),
            @Forward(name = "editfile", page = "/oa/deptfile/editfile.ptl"),
            @Forward(name = "share", page = "/oa/deptfile/share.ptl")})
    public String show(Integer key, String forward) throws Exception
    {
        super.show(key, forward);

        if (StringUtils.isEmpty(forward))
        {
            if (getEntity().isEditFile())
            {
                // 原来这里没有判断文件为编辑文件,ccs
                DeptFileContent fileContent = service.getFileContent(key);

                Inputable content = fileContent.getContent();

                if (content == null)
                    setContent("");
                else
                    setContent(new String(content.getBytes(), "UTF-8"));

                return "editFile";
            }
            else
            {
                return "upload";
            }
        }

        return forward;
    }

    @Override
    public void initEntity(DeptFile entity) throws Exception
    {
        entity.setFolderId(getFolderId());
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        DeptFile file = getEntity();

        file.setEditFile(content != null);
        java.util.Date now = new java.util.Date();
        file.setUploadTime(now);
        file.setUpdateTime(now);
        file.setCreator(userOnlineInfo.getUserId());

        if (this.file != null)
        {
            file.setSource("upload");
            initFile();
        }
        else if (content != null)
        {
            // 编辑的文件设置文件类型为txt
            file.setFileType("txt");
            file.setFileSize((long) getContentBytes().length);
            file.setSource("edit");
        }
        else
        {
            throw new NoErrorException("oa.deptfile.noFileSelect");
        }

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        if (file != null)
        {
            saveBak();

            initFile();
            getEntity().setUpdateTime(new java.util.Date());
        }
        else if (content != null)
        {
            saveBak();

            getEntity().setFileSize((long) getContentBytes().length);
            getEntity().setUpdateTime(new java.util.Date());
        }

        return true;
    }

    private void saveBak() throws Exception
    {
        DeptFile deptFile = service.getDao().getDeptFile(getEntity().getFileId());

        DeptFileBak bak = new DeptFileBak();
        bak.setFileId(deptFile.getFileId());
        if (deptFile.getUpdateTime() == null)
            bak.setUploadTime(deptFile.getUploadTime());
        else
            bak.setUploadTime(deptFile.getUpdateTime());
        bak.setContent(deptFile.getFileContent().getContent());
        bak.setFileSize(deptFile.getFileSize());
        bak.setFileType(deptFile.getFileType());

        service.getDao().add(bak);
    }

    private void initFile() throws Exception
    {
        if (file != null)
            service.initFile(getEntity(), file);
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        if (!all)
        {
            folderId = 0;
            setDefaultDeptId();
        }
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();
    }

    @Override
    public void afterSave() throws Exception
    {
        super.afterSave();

        if (file != null)
        {
            DeptFileContent content = new DeptFileContent();
            content.setFileId(getEntity().getFileId());

            content.setContent(file.getInputable());

            service.getDao().save(content);

        }
        else if (content != null)
        {
            DeptFileContent content = new DeptFileContent();
            content.setFileId(getEntity().getFileId());
            content.setContent(new Inputable.ByteInput(getContentBytes()));

            service.getDao().save(content);
        }
    }

    @Service(url = "/oa/deptfile/upload", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void upload(String[] filePaths) throws Exception
    {
        FileUploadService service = uploadServiceProvider.get();

        for (String filePath : filePaths)
        {
            InputFile file = service.getFile(filePath);
            try
            {
                setFile(new InputFile(file.getInputable(), file.getName()));
                DeptFile deptFile = new DeptFile();
                deptFile.setFolderId(folderId);
                setEntity(deptFile);
                setNew$(true);

                save();
            }
            finally
            {
                try
                {
                    service.deleteFile(filePath);
                }
                catch (Throwable ex)
                {
                    //删除临时文件错误，跳过
                }
            }
        }
    }

    @Service(url = "/oa/deptfile/batch")
    @Forward(page = "/oa/deptfile/batch.ptl")
    public String batchUpload() throws Exception
    {
        return null;
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

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.importJs("/oa/deptfile/deptfile.js");
        view.addComponent("选择文件", new CFile(null).setProperty("name", "file"));
        view.addComponent("文件名称", "fileName");
        view.addComponent("摘要", new CTextArea("summary"));

        view.addButton(Buttons.save().setProperty("progress", "true"));
        view.addButton(Buttons.close());

        view.setPage("big");

        return view;
    }

    @Transactional
    public void moveAllTo(Integer[] keys, Integer newOwnerKey, Integer oldOwnerKey)
            throws Exception
    {
        OwnedCrudUtils.moveTo(Arrays.asList(keys), newOwnerKey, oldOwnerKey, this);
    }

    @Transactional
    public void moveTo(Integer key, Integer newOwnerKey, Integer oldOwnerKey)
            throws Exception
    {
        OwnedCrudUtils.moveTo(Collections.singleton(key), newOwnerKey, oldOwnerKey, this);
    }

    @Transactional
    public void copyAllTo(Integer[] keys, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        if (oldOwnerKey.intValue() != newOwnerKey.intValue())
        {
            for (Integer fileId : keys)
            {
                DeptFile file = getEntity(fileId);

                DeptFile cloneFile = clone(file);

                cloneFile.setFolderId(newOwnerKey);

                service.getDao().add(cloneFile);

                DeptFileContent fileContent = new DeptFileContent();
                fileContent.setFileId(cloneFile.getFileId());
                fileContent.setContent(file.getFileContent().getContent());

                service.getDao().add(fileContent);
            }
        }
    }

    @Transactional
    public void copyTo(Integer key, Integer newOwnerKey, Integer oldOwnerKey)
            throws Exception
    {
        copyAllTo(new Integer[]{key}, newOwnerKey, oldOwnerKey);
    }

    public String getOwnerField()
    {
        return "folderId";
    }

    public Integer getOwnerKey(DeptFile entity) throws Exception
    {
        return entity.getFolderId();
    }

    public void setOwnerKey(DeptFile entity, Integer ownerKey) throws Exception
    {
        entity.setFolderId(ownerKey);
    }

    @Service(url = "/oa/deptfile/folder/{folderId}/zip")
    public InputFile zipFolder(Integer folderId) throws Exception
    {
        return service.zip(folderId);
    }
}
