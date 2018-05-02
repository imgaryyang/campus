package com.gzzm.oa.cloudstorage.dao;

import com.gzzm.oa.cloudstorage.usercloud.ShareRecord;
import com.gzzm.oa.cloudstorage.usercloud.UserCloudShare;
import com.gzzm.oa.deptfile.DeptFile;
import com.gzzm.oa.deptfile.DeptFileFolder;
import com.gzzm.oa.userfile.UserFile;
import com.gzzm.oa.userfile.UserFileFolder;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.annotation.OQLUpdate;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author gyw
 * @date 2017/7/26 0026
 */
public abstract class FileDao extends GeneralDao {
    public FileDao() {
    }

    @OQL("select f from UserFile f where f.userId = ?1 and f.folderId = ?2 and f.deleteTag = 0 order by f.updateTime")
    public abstract List<UserFile> getUserFiles(Integer userId, Integer folderId);

    @OQL("select f from UserFileFolder f where f.userId = ?1 and f.parentFolderId = ?2 and f.deleteTag=0 order by f.createTime desc")
    public abstract List<UserFileFolder> getUserFileFolders(Integer userId, Integer folderId);

    /**
     * 取出数据
     * @param cls
     * @param oqlCondition
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> getObjectListByCondition(Class<T> cls, String oqlCondition) throws Exception {
        if (oqlCondition == null) {
            return null;
        }
        String oql = "select f from " + cls.getSimpleName() + " f ";
        oql = oql + oqlCondition;
        return (List<T>) oqlQuery(oql, cls);
    }

    @OQLUpdate("update UserFileFolder set deleteTag = 1 where folderId in :1")
    public abstract void changeFolderDeleteTag(List<Integer> folderIds);

    @OQLUpdate("update UserFile set deleteTag = 1 where fileId in :1")
    public abstract void changeFileDeleteTag(List<Integer> fiIeIds);

    public Integer checkNameRepeat(Class cls, String nameStr, Integer id,Integer parentId,Integer currId) throws Exception {
        Entity at = (Entity) cls.getAnnotation(Entity.class);
        String sqlCondition = "";
        if (cls == UserFile.class) {
            sqlCondition = " e.fileId <>"+currId+" and  e.userId = " + id + " and e.folderId="+parentId+" and e.deleteTag = 0  and e.filename = '" + nameStr + "'";
        }
        if (cls == UserFileFolder.class) {
            sqlCondition = " e.folderId <>"+currId+" and e.userId = " + id + " and e.parentFolderId="+parentId+" and e.deleteTag = 0  and e.folderName = '" + nameStr + "'";
        }

        if (cls == DeptFile.class) {
            sqlCondition = " e.fileId <>"+currId+" and  e.deptId = " + id + " and e.folderId="+parentId+" and e.filename = '" + nameStr + "'";
        }

        if (cls == DeptFileFolder.class) {
            sqlCondition =" e.folderId <>"+currId+ " and  e.deptId = " + id +" and e.parentFolderId="+parentId+ " and e.folderName = '" + nameStr + "'";
        }
        return sqlQuery("select * from " + at.table() + "  e where " + sqlCondition, cls).size();
    }


    @OQL("select s from UserCloudShare s where s.shareUserId = ?1 and deleteTag = 0")
    public abstract List<UserCloudShare> getShareFileByUserId(Integer userId);

    @OQLUpdate("update UserCloudShare u set deleteTag =1 where shareId in ?1")
    public abstract void channelShareFiles(List<Integer> shareIdLists);

    @OQL("select f from DeptFile f where f.deptId = ?1 and f.folderId = ?2 order by f.updateTime")
    public abstract List<DeptFile> getDeptFiles(Integer deptId, Integer folderId);

    @OQL("select f from DeptFileFolder f where f.deptId = ?1 and f.parentFolderId = ?2 order by f.createTime desc")
    public abstract List<DeptFileFolder> getDeptFileFolders(Integer deptId, Integer folderId);

    @OQLUpdate("delete from DeptFile d where d.fileId in ?1")
    public abstract void deleteByIds(List<Integer> fileIdsList);

    @OQL("select s from ShareRecord s where s.shareId = ?1")
    public abstract List<ShareRecord> getShareRecordsByShareId(Integer shareId);


    @OQL("select r from UserCloudShare r where r.shareId in(select s.shareId from ShareRecord s where s.userId =?1 or s.deptId =?2) and r.deleteTag = 0")
    public abstract List<UserCloudShare> getUserCloudSharesByUserIdAndDeptId(Integer userId, Integer deptId);

    @OQL("select u from UserFileFolder u where u.parentFolderId = ?1 and u.deleteTag=0")
    public abstract List<UserFileFolder> getUserFileFoldersByFolderId(Integer nowFolderId);

    @OQL("select u from UserFile u where u.folderId = ?1 and u.deleteTag=0")
    public abstract List<UserFile> getUserFilesByFolderId(Integer nowFolderId);

    @OQL("select u from UserFileFolder u where u.folderId in ?1")
    public abstract List<UserFileFolder> getUserFileFoldersByIds(List<Integer> folderIdsList);

    @OQL("select u from UserFile u where u.fileId in ?1")
    public abstract List<UserFile> getUserFilesByIds(List<Integer> fileIdsList);
}
