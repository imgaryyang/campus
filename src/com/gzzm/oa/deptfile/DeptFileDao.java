package com.gzzm.oa.deptfile;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 部门文件相关的操作
 *
 * @author ccs
 */
public abstract class DeptFileDao extends GeneralDao
{
    public DeptFileDao()
    {
    }

    public DeptFileFolder getRootFolder() throws Exception
    {
        //这里当根目录不存在时，添加一个自动增加根目录
        DeptFileFolder folder = load(DeptFileFolder.class, 0);

        if (folder == null)
        {
            folder = new DeptFileFolder();
            folder.setFolderId(0);
            folder.setFolderName("根目录");
            add(folder);
        }

        return folder;
    }

    public DeptFile getDeptFile(Integer fileId) throws Exception
    {
        return load(DeptFile.class, fileId);
    }

    public DeptFileContent getFileContent(Integer fileId) throws Exception
    {
        return load(DeptFileContent.class, fileId);
    }

    public DeptFileFolder getFolder(Integer folderId) throws Exception
    {
        return load(DeptFileFolder.class, folderId);
    }

    public DeptFileBak getBak(Long bakId) throws Exception
    {
        return load(DeptFileBak.class, bakId);
    }

    @OQL("select f from DeptFileFolder f where f.deptId in ?1 and f.parentFolderId=0 order by dept.leftValue,orderId")
    public abstract List<DeptFileFolder> getTopFolders(Collection<Integer> deptIds)
            throws Exception;

    @OQL("select f from DeptFileFolder f where f.parentFolderId=:1 order by orderId")
    public abstract List<DeptFileFolder> getChildFolders(Integer parentFolderId) throws Exception;


    /**
     * 删除文件内容
     *
     * @param fileIds 要删除的文件id列表
     * @throws Exception 数据库查询异常
     */
    @OQLUpdate("delete DeptFileContent where fileId in :1")
    public abstract void deleteFileContent(Integer... fileIds) throws Exception;

    @OQL("select b from DeptFileBak b where b.fileId=:1 order by uploadTime")
    public abstract List<DeptFileBak> getBaks(Integer fileId) throws Exception;

    @OQL("select f from DeptFile f where f.deptId=:1 and f.folderId=:2 order by uploadTime")
    public abstract List<DeptFile> getFiles(Integer deptId, Integer folderId) throws Exception;

}