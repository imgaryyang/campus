package com.gzzm.oa.cloudstorage.usercloud;

import com.gzzm.oa.cloudstorage.CloudShare;
import com.gzzm.oa.userfile.UserFile;
import com.gzzm.oa.userfile.UserFileFolder;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.ManyToMany;
import net.cyan.thunwind.annotation.OneToMany;

import java.util.List;

/**
 * 云盘个人文件分享
 *
 * @author gyw
 * @date 2017/8/8 0008
 */
@Entity(table = "OAUSERCLOUDSHARE", keys = "shareId")
public class UserCloudShare extends CloudShare {


    @OneToMany
    @NotSerialized
    private List<ShareRecord> shareRecords;

    @NotSerialized
    @ManyToMany(table = "OASHAREFILE")
    private List<UserFile> userShareFiles;

    @NotSerialized
    @ManyToMany(table = "OASHAREFOLDER")
    private List<UserFileFolder> userShareFolders;

    public UserCloudShare() {
    }

    public List<ShareRecord> getShareRecords() {
        return shareRecords;
    }

    public void setShareRecords(List<ShareRecord> shareRecords) {
        this.shareRecords = shareRecords;
    }

    public List<UserFile> getUserShareFiles() {
        return userShareFiles;
    }

    public void setUserShareFiles(List<UserFile> userShareFiles) {
        this.userShareFiles = userShareFiles;
    }

    public List<UserFileFolder> getUserShareFolders() {
        return userShareFolders;
    }

    public void setUserShareFolders(List<UserFileFolder> userShareFolders) {
        this.userShareFolders = userShareFolders;
    }
}
