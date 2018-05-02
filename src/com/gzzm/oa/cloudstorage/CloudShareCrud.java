package com.gzzm.oa.cloudstorage;

import com.gzzm.oa.cloudstorage.dao.FileDao;
import com.gzzm.oa.cloudstorage.usercloud.UserCloudShare;
import com.gzzm.oa.userfile.UserFile;
import com.gzzm.oa.userfile.UserFileFolder;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.ObjectResult;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * 云盘分享CRUD
 *
 * @author gyw
 * @date 2017/8/24 0024
 */
@Service
public class CloudShareCrud {

    /**
     * 导航栏
     */
    private static List<UserFileFolder> navList = new ArrayList<UserFileFolder>();

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private FileDao dao;

    private Integer nowFolderId;

    private Integer shareId;

    private UserCloudShare userCloudShare;

    private List<UserCloudShare> userCloudShares;

    private List<UserFile> userFiles;

    private List<UserFileFolder> userFileFolders;

    private List<UserFileFolder> userFileFolderList;

    private Integer fileCount;

    public CloudShareCrud() {
    }

    public Integer getNowFolderId() {
        return nowFolderId;
    }

    public void setNowFolderId(Integer nowFolderId) {
        this.nowFolderId = nowFolderId;
    }

    public List<UserCloudShare> getUserCloudShares() {
        return userCloudShares;
    }

    public void setUserCloudShares(List<UserCloudShare> userCloudShares) {
        this.userCloudShares = userCloudShares;
    }

    public UserCloudShare getUserCloudShare() {
        return userCloudShare;
    }

    public void setUserCloudShare(UserCloudShare userCloudShare) {
        this.userCloudShare = userCloudShare;
    }

    public Integer getShareId() {
        return shareId;
    }

    public void setShareId(Integer shareId) {
        this.shareId = shareId;
    }

    public List<UserFile> getUserFiles() {
        return userFiles;
    }

    public void setUserFiles(List<UserFile> userFiles) {
        this.userFiles = userFiles;
    }

    public List<UserFileFolder> getUserFileFolders() {
        return userFileFolders;
    }

    public void setUserFileFolders(List<UserFileFolder> userFileFolders) {
        this.userFileFolders = userFileFolders;
    }

    public List<UserFileFolder> getUserFileFolderList() {
        return userFileFolderList;
    }

    public void setUserFileFolderList(List<UserFileFolder> userFileFolderList) {
        this.userFileFolderList = userFileFolderList;
    }

    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    @Service(url = "/oa/cloudstorage/CloudShareCrud")
    public String toGetUrlPage() throws Exception {
        if (shareId != null) {
            userCloudShares = null;
            userCloudShare = dao.get(UserCloudShare.class, shareId);
            userFileFolders = nowFolderId == null ? userCloudShare.getUserShareFolders() : dao.getUserFileFoldersByFolderId(nowFolderId);
            userFiles = nowFolderId == null ? userCloudShare.getUserShareFiles() : dao.getUserFilesByFolderId(nowFolderId);
            if (nowFolderId != null) {
                if (nowFolderId != 0) {
                    UserFileFolder u = dao.get(UserFileFolder.class, nowFolderId);
                    if (!navList.contains(u)) {
                        navList.add(u);
                    } else {
                        navList = navList.subList(0, navList.indexOf(u) + 1);
                    }
                }else{
                    navList.removeAll(navList);
                }
            }else{
                navList.removeAll(navList);
            }
        } else {
            navList.removeAll(navList);
            userCloudShares = dao.getUserCloudSharesByUserIdAndDeptId(userOnlineInfo.getUserId(), userOnlineInfo.getDeptId());
        }
        RequestContext.getContext().getSession().setAttribute("navList", navList);
        fileCount = (userCloudShares == null ? 0 : userCloudShares.size()) + (userFileFolders == null ? 0 : userFileFolders.size()) + (userFiles == null ? 0 : userFiles.size());
        return "/oa/cloudstorage/usercloud/usersharelist.ptl";
    }

    /**
     * 导航显示文件夹路径
     *
     * @param userFileFolders
     * @param index
     * @return
     */
    @ObjectResult
    public String createPath(List<UserFileFolder> userFileFolders, Integer index) {
        String path = "/";
        if (index == 0) {
            path += userFileFolders.get(index).getFolderName();
        } else {
            for (int i = 0; i <= index; i++) {
                path += userFileFolders.get(i).getFolderName() + "/";
            }
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

}
