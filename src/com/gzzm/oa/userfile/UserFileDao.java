package com.gzzm.oa.userfile;

import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 用户文件相关的操作
 *
 * @author wmy
 */
public abstract class UserFileDao extends GeneralDao
{
    public UserFileDao()
    {
    }

    public UserFileFolder getRootFolder() throws Exception
    {
        //这里当根目录不存在时，添加一个自动增加根目录
        UserFileFolder folder = load(UserFileFolder.class, 0);

        if (folder == null)
        {
            folder = new UserFileFolder();
            folder.setFolderId(0);
            folder.setFolderName("根目录");
            add(folder);
        }

        return folder;
    }

    public UserFile getUserFile(Integer fileId) throws Exception
    {
        return load(UserFile.class, fileId);
    }

    public UserFileContent getFileContent(Integer fileId) throws Exception
    {
        return load(UserFileContent.class, fileId);
    }

    public UserFileFolder getFolder(Integer folderId) throws Exception
    {
        return load(UserFileFolder.class, folderId);
    }

    public UserFileBak getBak(Long bakId) throws Exception
    {
        return load(UserFileBak.class, bakId);
    }

    @OQL("select f from UserFileFolder f where f.userId=:1 and f.parentFolderId=:2 order by orderId")
    public abstract List<UserFileFolder> getFolders(Integer userId, Integer parentFolderId) throws Exception;

    @OQL("select f from UserFile f where f.userId=:1 and f.folderId=:2 order by uploadTime")
    public abstract List<UserFile> getFiles(Integer userId, Integer folderId) throws Exception;

    public UserFileConfig getUserFileConfig(Integer userId) throws Exception
    {
        return load(UserFileConfig.class, userId);
    }

    /**
     * 获得用户文件的总大小
     *
     * @param userId 用户ID
     * @return 文件总大小
     * @throws Exception 数据库查询异常
     */
    @OQL("select sum(t.fileSize) from UserFile t where t.userId=:1")
    public abstract Long sumFileSize(Integer userId) throws Exception;

    /**
     * 获得一组文件的总大小
     *
     * @param fileIds 文件ID列表
     * @return 文件总大小
     * @throws Exception 数据库查询异常
     */
    @OQL("select sum(t.fileSize) from UserFile t where t.fileId in :1")
    public abstract Long getFilesSize(Integer... fileIds) throws Exception;

    /**
     * 删除文件内容
     *
     * @param fileIds 要删除的文件id列表
     * @throws Exception 数据库查询异常
     */
    @OQLUpdate("delete UserFileContent where fileId in :1")
    public abstract void deleteFileContent(Integer... fileIds) throws Exception;

    /**
     * 根据被共享用户ID查找共享文件给他的用户列表
     *
     * @param userId 被共享人的用户id
     * @return 共享文件给共享人的用户列表
     * @throws Exception 数据库查询异常
     * @author wmy
     */
    @OQL("select u.u from (select u, min(select leftValue from u.depts d where d.state=0) as leftValue from User u where " +
            " state=0 and ((select 1 from UserFile f,f.shares s where f.userId=u.userId and (s.userId=:1 or s.deptId in :2))" +
            " is not empty or (select 1 from UserFileFolder f,f.shares s where f.userId=u.userId and (s.userId=:1 or " +
            "s.deptId in :2)) is not empty)) u order by u.leftValue,first(select d.orderId from " +
            "UserDept d where d.userId=u.u.userId and d.dept.leftValue=u.leftValue and d.dept.state=0)")
    public abstract List<User> getShareUserListByUserId(Integer userId, Collection<Integer> deptIds) throws Exception;

    /**
     * 根据被共享用户ID查找某用户共享给他的文件夹列表
     *
     * @param userId      被共享人的用户id
     * @param shareUserId 共享文件的用户ID
     * @return shareUserId共享给userId的文件价列表
     * @throws Exception 数据库查询异常
     * @author wmy
     */
    @OQL("select folder from UserFileFolder folder where " +
            "((select 1 from folder.files f,f.shares s where s.userId=:1 or s.deptId in :3) is not empty or " +
            "(exists s in folder.shares : (s.userId=:1 or s.deptId in :3))) and userId=:2 and " +
            "((select 1 from folder.parentFolder.files f,f.shares s where s.userId=:1 or s.deptId in :3) is empty" +
            " and (not exists s in folder.parentFolder.shares : (s.userId=:1 or s.deptId in :3)) or parentFolderId=0)" +
            "order by folder.orderId")
    public abstract List<UserFileFolder> getRootShareFolderListByUserId(Integer userId, Integer shareUserId,
                                                                        Collection<Integer> deptIds)
            throws Exception;

    /**
     * 根据被共享用户ID查找某用户共享给他的文件夹列表
     *
     * @param parentFolderId 父目录的ID
     * @param userId         被共享人的用户id
     * @return shareUserId共享给userId的文件价列表
     * @throws Exception 数据库查询异常
     * @author wmy
     */
    @OQL("select folder from UserFileFolder folder where " +
            "((select 1 from folder.files f,f.shares s where s.userId=:1 or s.deptId in :3) is not empty or " +
            "(exists s in folder.shares : (s.userId=:1 or s.deptId in :3))) and folder.parentFolderId=:2 " +
            "order by folder.orderId")
    public abstract List<UserFileFolder> getShareFolderListByUserId(Integer userId, Integer parentFolderId,
                                                                    Collection<Integer> deptIds) throws Exception;

    /**
     * 根据被共享用户ID查找某用户共享给他的文件夹数量
     *
     * @param parentFolderId 父目录的ID
     * @param userId         被共享人的用户id
     * @return shareUserId共享给userId的文件价列表
     * @throws Exception 数据库查询异常
     * @author wmy
     */
    @OQL("select count(*) from UserFileFolder folder where " +
            "((select 1 from folder.files f,f.shares s where (s.userId=:1 or s.deptId in :3)) is not empty or " +
            "(exists s in folder.shares : (s.userId=:1 or s.deptId in :3))) and folder.parentFolderId=:2 ")
    public abstract Integer getShareFolderCountByUserId(Integer userId, Integer parentFolderId,
                                                        Collection<Integer> deptIds) throws Exception;

    /**
     * 更新个人文件配置表中的已使用空间
     *
     * @param userId 用户ID
     * @throws Exception 数据库操作异常
     */
    public void updateUsed(Integer userId) throws Exception
    {
        UserFileConfig config = new UserFileConfig();
        config.setUserId(userId);
        config.setUsed(sumFileSize(userId));

        save(config);
    }

    @OQL("select b from UserFileBak b where b.fileId=:1 order by uploadTime")
    public abstract List<UserFileBak> getBaks(Integer fileId) throws Exception;
}