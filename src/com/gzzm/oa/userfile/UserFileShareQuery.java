package com.gzzm.oa.userfile;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 共享文件查询
 *
 * @author : wmy
 * @date : 2010-3-17
 */
@Service(url = "/oa/userfile/share")
public class UserFileShareQuery extends BaseQueryCrud<UserFile, Integer>
{
    @Like
    private String fileName;

    /**
     * 用文件类型做查询条件
     */
    private String fileType;

    /**
     * 全文检索
     */
    @Contains
    private String text;

    /**
     * 用于匹配左边树文件夹ID
     */
    @Equals("folderId")
    private Integer folderId;

    /**
     * 用于匹配左边树共享人ID
     */
    @Equals("userId")
    private Integer shareUserId;

    /**
     * 当前用户登录信息
     */
    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 左边树节点ID
     */
    private String nodeId;

    private String page;

    public UserFileShareQuery()
    {
        // 原来莫名奇妙用用户ID排序，改之ccs
        addOrderBy("updateTime", OrderType.desc);
        addOrderBy("fileId", OrderType.desc);
    }

    @Service
    public Integer getNextImgFile(Integer fileId) throws Exception
    {
        UserFile file = getEntity(fileId);

        Map<String, Object> parameters = toMap();
        parameters.put("updateTime", file.getUpdateTime());
        parameters.put("fileId", file.getFileId());

        return getCrudService().oqlQueryFirst("select fileId from UserFile where " +
                "(updateTime<:updateTime or updateTime=:updateTime and fileId<:fileId) " +
                " and fileType in ('jpg','jpeg','gif','bmp','png','JPG','JPEG','GIF','BMP','PNG') " +
                "and (" + createCondition() + ")" +
                "order by updateTime desc,fileId desc", Integer.class, parameters);
    }

    @Service
    public Integer getPreImgFile(Integer fileId) throws Exception
    {
        UserFile file = getEntity(fileId);

        Map<String, Object> parameters = toMap();
        parameters.put("updateTime", file.getUpdateTime());
        parameters.put("fileId", file.getFileId());

        return getCrudService().oqlQueryFirst("select fileId from UserFile where " +
                "(updateTime>:updateTime or updateTime=:updateTime and fileId>:fileId) " +
                " and fileType in ('jpg','jpeg','gif','bmp','png','JPG','JPEG','GIF','BMP','PNG') " +
                " and (" + createCondition() + ")" +
                "order by updateTime,fileId", Integer.class, parameters);
    }
    // 删除从其他地方复制过来的垃圾代码

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view ;

        if ("list".equals(getPage()))
        {
            view = new PageTableView(true);
            view.setPage("table");
            view.importJs("/oa/userfile/list.js");
            view.addColumn("文件名称", new UserFileNameCell()).setAlign(Align.left);
        }else{
            view = new ComplexTableView(new UserFileShareNodeDisplay(),
                            "nodeId", true);
            view.importJs("/oa/userfile/userfile.js");
            view.importCss("/oa/userfile/userfile.css");
            view.addComponent("文件名称", "fileName").setWidth("150px");
            view.addComponent("文件类型", "fileType").setWidth("70px");
            view.addComponent("文件内容", "text").setWidth("150px");

            view.addButton(Buttons.query());
            view.addColumn("文件名称", new UserFileNameCell());
        }

        view.addColumn("共享人", "user.userName");
        if (!"list".equals(getPage()))
        {
            view.addColumn("文件大小", new FieldCell("fileSize").setFormat("bytesize"));
        }
        view.addColumn("上传时间", "uploadTime");
        if (!"list".equals(getPage()))
        {
            view.addColumn("查看/下载", new CUnion(
                    new CHref("${(editFile||canShow())?'查看':''}").setAction(
                            "display(${fileId},${editFile.toString()},'${fileType}')"),
                    new CHref("${editFile?'':'下载'}", "/oa/userfile/${fileId}/down").setTarget("_blank")))
                    .setAlign(Align.center).setWidth("80").setLocked(true);
        }

        return view;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        if ("list".equals(page)){
            setPageSize(6);
        }
        super.beforeShowList();
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        //判断节点,u开始为用户层级,f开始为目录层级
        if (getNodeId() != null && getNodeId().startsWith("u"))
        {
            setShareUserId(Integer.valueOf(getNodeId().substring(1)));
        }
        else if (getNodeId() != null && getNodeId().startsWith("f"))
        {
            setFolderId(Integer.valueOf(getNodeId().substring(1)));
        }
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "exists s in shares : (s.userId=:userId or s.deptId in :deptIds) " +
                "or exists m in fileFolder.shares : (m.userId=:userId or m.deptId in :deptIds)";
    }

    @NotCondition
    @NotSerialized
    public Integer getUserId()
    {
        return userOnlineInfo.getUserId();
    }

    @NotSerialized
    public List<Integer> getDeptIds()
    {
        return userOnlineInfo.getDeptIds();
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileType()
    {
        return fileType;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(String nodeId)
    {
        this.nodeId = nodeId;
    }

    public Integer getFolderId()
    {
        return folderId;
    }

    public void setFolderId(Integer folderId)
    {
        this.folderId = folderId;
    }

    public Integer getShareUserId()
    {
        return shareUserId;
    }

    public void setShareUserId(Integer shareUserId)
    {
        this.shareUserId = shareUserId;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }
}
