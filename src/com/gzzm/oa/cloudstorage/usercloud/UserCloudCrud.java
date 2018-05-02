package com.gzzm.oa.cloudstorage.usercloud;

import com.gzzm.oa.cloudstorage.CommonCloudCrud;
import com.gzzm.oa.userfile.*;
import com.gzzm.platform.commons.FileUploadService;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.SystemCrudUtils;
import com.gzzm.platform.log.LogAction;
import com.gzzm.platform.organ.Dept;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.ObjectResult;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.BeanUtils;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 个人云盘
 *
 * @author gyw
 * @date 2017/7/25 0025
 */
@Service
public class UserCloudCrud extends CommonCloudCrud {

    /**
     * 导航栏
     */
    private static List<UserFileFolder> navList = new ArrayList<UserFileFolder>();

    /**
     * 文件相关的数据操作
     */
    @Inject
    private UserFileService service;

    /**
     * 文件夹
     */
    private List<UserFileFolder> userFileFolders;

    /**
     * 文件
     */
    private List<UserFile> userFiles;

    private List<ShareRecord> shareRecords;
    /**
     * 分享的文件
     */
    private List<UserCloudShare> userCloudShares;

    /**
     * 分享ID
     */
    private Integer shareId;

    /**
     * 分享名
     */
    private String shareName;

    private UserCloudShare userCloudShare;

    public UserCloudCrud() {
        tag = 0;
    }

    public Integer getShareId() {
        return shareId;
    }

    public void setShareId(Integer shareId) {
        this.shareId = shareId;
    }

    public List<UserFileFolder> getUserFileFolders() {
        return userFileFolders;
    }

    public void setUserFileFolders(List<UserFileFolder> userFileFolders) {
        this.userFileFolders = userFileFolders;
    }

    public List<UserFile> getUserFiles() {
        return userFiles;
    }

    public void setUserFiles(List<UserFile> userFiles) {
        this.userFiles = userFiles;
    }

    public List<UserCloudShare> getUserCloudShares() {
        return userCloudShares;
    }

    public void setUserCloudShares(List<UserCloudShare> userCloudShares) {
        this.userCloudShares = userCloudShares;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    public UserCloudShare getUserCloudShare() {
        return userCloudShare;
    }

    public void setUserCloudShare(UserCloudShare userCloudShare) {
        this.userCloudShare = userCloudShare;
    }

    public List<ShareRecord> getShareRecords() throws Exception {
        if (shareId == null || shareId == 0) {
            if (shareRecords == null) {
                shareRecords = new ArrayList<ShareRecord>();
            }
        } else {
            shareRecords = dao.get(UserCloudShare.class, shareId).getShareRecords();
        }
        return shareRecords;
    }

    public void setShareRecords(List<ShareRecord> shareRecords) {
        this.shareRecords = shareRecords;
    }

    @Service(url = "/oa/cloudstorage/usercloud/usercloudcrud")
    public String toHomePage() throws Exception {
        String condition = " where f.userId = " + userOnlineInfo.getUserId() + " and deleteTag =0";
        if (nowfolderId == null || nowfolderId == 0) {
            navList.removeAll(navList);
            nowfolderId = 0;
        } else {
            if (nowfolderId != 0) {
                UserFileFolder u = dao.get(UserFileFolder.class, nowfolderId);
                if (!navList.contains(u)) {
                    navList.add(u);
                } else {
                    navList = navList.subList(0, navList.indexOf(u) + 1);
                }
            }
        }
        if (type != null && type == 6 && shareId == null) {
            userCloudShares = dao.getShareFileByUserId(userOnlineInfo.getUserId());
        }
        if (shareId != null) {
            userCloudShare = dao.get(UserCloudShare.class, shareId);
        }
        if (type != null && type != 0) {
            navList.removeAll(navList);
        }
        RequestContext.getContext().getSession().setAttribute("navList", navList);
        userFileFolders = (type != null && type == 6 && shareId != null) ? dao.get(UserCloudShare.class, shareId).getUserShareFolders() : (dao.getObjectListByCondition(UserFileFolder.class, (type != null && type != 0) ? null : getFileFolderCondition(condition)));
        userFiles = (type != null && type == 6 && shareId != null) ? dao.get(UserCloudShare.class, shareId).getUserShareFiles() : ((type != null && type == 6) ? null : dao.getObjectListByCondition(UserFile.class, getFileCondition(condition)));
        fileCount = (userFileFolders != null ? userFileFolders.size() : 0) + (userFiles == null ? 0 : userFiles.size()) + (userCloudShares == null ? 0 : userCloudShares.size());
        return "/oa/cloudstorage/usercloud/usercloudhome.ptl";
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

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @return 文件输入流
     * @throws Exception 数据库操作抛出异常
     * @author
     */
    @Service(url = "/oa/cloudstorage/usercloud/{$0}/down")
    public InputFile downloadFile(Integer fileId, String contentType) throws Exception {
        UserFile userFile = dao.get(UserFile.class, fileId);
        UserFileContent content = service.getFileContent(fileId);

        // 考虑fileType为null的情况
        String fileName = userFile.getFileName();
        if (!StringUtils.isEmpty(userFile.getFileType()))
            fileName += "." + userFile.getFileType();

        if (contentType == null)
            contentType = userFile.getFileType();

        return new InputFile(content.getContent(), fileName, contentType);
    }

    /**
     * 创建文件夹
     *
     * @param parentId
     * @param folderName
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/cloudstorage/usercloud/usercloudcrud/createFolder", method = HttpMethod.all)
    public UserFileFolder createFolder(Integer parentId, String folderName) throws Exception {
        if (dao.checkNameRepeat(UserFileFolder.class, folderName, userOnlineInfo.getUserId(), parentId,0) > 0) {
            return null;
        }

        UserFileFolder userFileFolder = new UserFileFolder();
        userFileFolder.setParentFolder(dao.get(UserFileFolder.class, parentId));
        userFileFolder.setParentFolderId(parentId);
        userFileFolder.setFolderName(folderName);
        userFileFolder.setCreateTime(new Date());
        userFileFolder.setUserId(userOnlineInfo.getUserId());
        dao.save(userFileFolder);
        SystemCrudUtils.saveLog(userFileFolder, LogAction.add,null,null);
        return userFileFolder;
    }

    /**
     * 删除文件
     *
     * @return
     */
    @Service(url = "/oa/cloudstorage/usercloud/usercloudcrud/deleteFolderAndFiles")
    @Transactional
    public Boolean deleteFolderAndFiles() {
        try {
            List<Integer> folderIdsList = formatIdsByStr(folderIds);
            if (folderIdsList != null && folderIdsList.size() > 0) {
                for (Integer folderId : folderIdsList) {
                    UserFileFolder userFileFolder = dao.get(UserFileFolder.class, folderId);
                    deleteAllFilesByFolder(userFileFolder);
                }
            }

            List<Integer> fileIdsList = formatIdsByStr(fileIds);
            if (fileIdsList != null && fileIdsList.size() > 0) {
                for(Integer id:fileIdsList){
                    SystemCrudUtils.saveLog(dao.load(UserFile.class,id), LogAction.modify,null,null);
                }
                dao.changeFileDeleteTag(fileIdsList);
            }
            return true;
        } catch (Exception e) {
            Tools.log(e.getMessage());
            return false;
        }
    }

    private void deleteAllFilesByFolder(UserFileFolder userFileFolder) throws Exception {

        userFileFolder.setDeleteTag(true);
        SystemCrudUtils.saveLog(userFileFolder, LogAction.modify,null,null);
        dao.save(userFileFolder);

        List<UserFile> userFiles = dao.getUserFiles(userOnlineInfo.getUserId(), userFileFolder.getFolderId());
        if (userFiles != null && userFiles.size() > 0) {
            for (UserFile userFile : userFiles) {
                userFile.setDeleteTag(true);
                SystemCrudUtils.saveLog(userFile, LogAction.modify,null,null);
                dao.save(userFile);
            }
        }

        List<UserFileFolder> childeFileFolders = dao.getUserFileFolders(userOnlineInfo.getUserId(), userFileFolder.getFolderId());
        if (childeFileFolders != null && childeFileFolders.size() > 0) {
            for (UserFileFolder userFileFolder1 : childeFileFolders) {
                deleteAllFilesByFolder(userFileFolder1);
            }
        }
    }


    /**
     * 上传
     *
     * @param filePaths
     * @throws Exception
     */
    @Service(url = "/oa/cloudstorage/usercloud/upload", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void upload(String[] filePaths) throws Exception {
        FileUploadService service = uploadServiceProvider.get();

        for (String filePath : filePaths) {
            InputFile file = service.getFile(filePath);
            try {
                setFile(new InputFile(file.getInputable(), file.getName()));
                UserFile userFile = new UserFile();
                userFile.setFolderId(nowfolderId);
                String[] nameAndType = file.getName().split("\\.");
                String name = nameAndType[0];
                String type = nameAndType[nameAndType.length - 1];
                userFile.setFileName(name);
                userFile.setFileType(type);
                userFile.setFileSize(file.size());
                userFile.setUploadTime(new Date());
                userFile.setUpdateTime(userFile.getUploadTime());
                userFile.setSource("upload");
                userFile.setEditFile(true);
                userFile.setRemark("");
                userFile.setUser(userOnlineInfo.getUserEntity());
                userFile.setUserId(userOnlineInfo.getUserId());
                dao.save(userFile);
                SystemCrudUtils.saveLog(userFile, LogAction.add,null,null);

                UserFileContent userFileContent = new UserFileContent();
                userFileContent.setFileId(userFile.getFileId());
                userFileContent.setContent(file.getInputable());
                dao.save(userFileContent);
                SystemCrudUtils.saveLog(userFileContent, LogAction.add,null,null);
            } finally {
                try {
                    service.deleteFile(filePath);
                } catch (Throwable ex) {
                    //删除临时文件错误，跳过
                }
            }
        }
    }

    /**
     * 下载
     *
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/cloudstorage/usercloud/zip")
    public InputFile zipAll() throws Exception {
        List<Integer> folderIdsList = formatIdsByStr(folderIds);
        List<Integer> fileIdsList = formatIdsByStr(fileIds);
        List<Integer> shareIdsList = formatIdsByStr(shareIds);
        if (folderIdsList != null && folderIdsList.size() == 1 && fileIdsList == null) {
            return service.zip(folderIdsList.get(0));
        } else if (folderIdsList == null && fileIdsList != null && fileIdsList.size() == 1) {
            return downloadFile(fileIdsList.get(0), null);
        } else if (shareIdsList != null && shareIdsList.size() > 0) {
            return service.zipShareFolder(shareIdsList);
        } else {
            List<UserFileFolder> userFileFolders = folderIdsList==null?null:dao.getUserFileFoldersByIds(folderIdsList);
            List<UserFile> userFiles = fileIdsList==null?null:dao.getUserFilesByIds(fileIdsList);
            return service.zipFoldersAndFiles(userFileFolders, userFiles, "云盘资料");
       }
    }

    /**
     * 文件夹树生成
     *
     * @return
     */
    @Service(url = "/oa/cloudstorage/usercloud/createAllUserFolder")
    @ObjectResult
    public String createAllUserFolder() {
        String preStr = "<ul class=\"fzd\">\n" +
                "<li class=\"current\" id='root'><span class='seled'";
        String lastStr = " onclick='spanToggle(this)'><img src=\"/oa/cloudstorage/image/jjh_03.png\" width=\"26\" height=\"21\"/>";
        StringBuffer sb = new StringBuffer().append(preStr + "code='0'" + lastStr + "全部文件</span>");
        List<UserFileFolder> userFileFolders = dao.getUserFileFolders(userOnlineInfo.getUserId(), 0);

        for (UserFileFolder u : userFileFolders) {
            createElement(preStr, lastStr, u, "");
            sb.append(eleStr);
            eleStr = "";
        }
        sb.append("</li></ul>");
        return sb.toString();
    }

    private void createElement(String preStr, String lastStr, UserFileFolder userFileFolder, String superStr) {
        List<UserFileFolder> userFileFolders = dao.getUserFileFolders(userOnlineInfo.getUserId(), userFileFolder.getFolderId());
        if (userFileFolders != null && userFileFolders.size() > 0) {
            eleStr = eleStr + preStr + "code='" + userFileFolder.getFolderId() + "'" + lastStr + userFileFolder.getFolderName() + "</span>";
            for (UserFileFolder uf : userFileFolders) {
                createElement(preStr, lastStr, uf, eleStr);
            }
            eleStr += "</li></ul>";

        } else {
            eleStr = superStr + "<ul>\n" + "<li class='childli'><a class='seled' code='" + userFileFolder.getFolderId() + "' href=\"#\"><img src=\"/oa/cloudstorage/image/jjh_03.png\" width=\"26\" height=\"21\"/>" + userFileFolder.getFolderName() + "</a></li>\n" + "</ul>";
        }
    }


    /**
     * 将文件复制到目标文件夹
     *
     * @param seledFolderId
     * @return
     */
    @Service(url = "/oa/cloudstorage/usercloud/copyToTargetFolder")
    public Boolean copyToTargetFolder(Integer seledFolderId) throws Exception {
        try {
            List<Integer> shareIdsList = formatIdsByStr(shareIds);
            if (shareIdsList != null && shareIdsList.size() > 0) {
                for (Integer id : shareIdsList) {
                    UserCloudShare userCloudShare = dao.load(UserCloudShare.class, id);
                    UserFileFolder uff = new UserFileFolder();
                    uff.setParentFolderId(seledFolderId);
                    uff.setFolderName(userCloudShare.getShareName());
                    uff.setCreateTime(new Date());
                    uff.setUserId(userOnlineInfo.getUserId());
                    dao.save(uff);
                    SystemCrudUtils.saveLog(uff, LogAction.add,null,null);
                    List<UserFileFolder> uffs = userCloudShare.getUserShareFolders();
                    for (UserFileFolder u : uffs) {
                        copyTo(u, uff.getFolderId());
                    }
                    List<UserFile> ufs = userCloudShare.getUserShareFiles();
                    for (UserFile uf : ufs) {
                        UserFile userFile = new UserFile();
                        BeanUtils.copyProperties(uf, userFile, new BaseTypeFilter());
                        userFile.setFolderId(uff.getFolderId());
                        userFile.setUserId(userOnlineInfo.getUserId());
                        userFile.setFileId(null);
                        userFile.setSource("copy");
                        userFile.setRemark("");
                        userFile.setUpdateTime(new Date());
                        userFile.setUploadTime(new Date());
                        dao.save(userFile);
                        SystemCrudUtils.saveLog(userFile, LogAction.add,null,null);

                        UserFileContent ufc = new UserFileContent();
                        BeanUtils.copyProperties(uf.getFileContent(), ufc, new BaseTypeFilter());
                        ufc.setFileId(userFile.getFileId());
                        dao.save(ufc);
                        SystemCrudUtils.saveLog(ufc, LogAction.add,null,null);
                    }
                }
            }

            List<Integer> folderIdsList = formatIdsByStr(folderIds);
            if (folderIdsList != null && folderIdsList.size() > 0) {
                for (Integer id : folderIdsList) {
                    UserFileFolder userFileFolder = dao.get(UserFileFolder.class, id);
                    copyTo(userFileFolder, seledFolderId);
                }
            }
            List<Integer> fileIdsList = formatIdsByStr(fileIds);
            if (fileIdsList != null && fileIdsList.size() > 0) {
                for (Integer id : fileIdsList) {
                    UserFile userFileSource = dao.get(UserFile.class, id);
                    UserFile userFile = new UserFile();
                    BeanUtils.copyProperties(userFileSource, userFile, new BaseTypeFilter());
                    userFile.setFolderId(seledFolderId);
                    userFile.setFileId(null);
                    userFile.setSource("copy");
                    userFile.setRemark("");
                    userFile.setUpdateTime(new Date());
                    userFile.setUploadTime(new Date());
                    dao.save(userFile);
                    SystemCrudUtils.saveLog(userFile, LogAction.add,null,null);

                    UserFileContent ufc = new UserFileContent();
                    BeanUtils.copyProperties(userFileSource.getFileContent(), ufc, new BaseTypeFilter());
                    ufc.setFileId(userFile.getFileId());
                    dao.save(ufc);
                    SystemCrudUtils.saveLog(ufc, LogAction.add,null,null);
                }
            }
            return true;
        } catch (Exception e) {
            Tools.log(e);
            return false;
        }
    }

    private void copyTo(UserFileFolder userFileFolder, Integer folderId) throws Exception {
        UserFileFolder u = new UserFileFolder();
        BeanUtils.copyProperties(userFileFolder, u, new BaseTypeFilter());
        u.setCreateTime(new Date());
        u.setParentFolderId(folderId);
        u.setUserId(userOnlineInfo.getUserId());
        u.setFolderId(null);
        dao.save(u);
        SystemCrudUtils.saveLog(u, LogAction.add,null,null);

        List<UserFile> userFiles = dao.getUserFiles(userFileFolder.getUserId(), userFileFolder.getFolderId());
        if (userFiles != null && userFiles.size() > 0) {
            for (UserFile userFile : userFiles) {
                UserFile uf = new UserFile();
                BeanUtils.copyProperties(userFile, uf, new BaseTypeFilter());
                uf.setFileContent(null);
                uf.setFileId(null);
//                uf.setFileFolder(null);
                uf.setUser(null);
                uf.setShares(null);
                uf.setUserId(userOnlineInfo.getUserId());
                uf.setFolderId(u.getFolderId());
                uf.setUpdateTime(new Date());
                uf.setUploadTime(uf.getUpdateTime());
                uf.setSource("copy");
                dao.save(uf);
                SystemCrudUtils.saveLog(uf, LogAction.add,null,null);

                UserFileContent ufc = new UserFileContent();
                BeanUtils.copyProperties(userFile.getFileContent(), ufc, new BaseTypeFilter());
                ufc.setFileId(uf.getFileId());
                dao.save(ufc);
                SystemCrudUtils.saveLog(ufc, LogAction.add,null,null);
            }
        }

        List<UserFileFolder> childeFileFolders = dao.getUserFileFolders(userFileFolder.getUserId(), userFileFolder.getFolderId());
        if (childeFileFolders != null && childeFileFolders.size() > 0) {
            for (UserFileFolder userFileFolder1 : childeFileFolders) {
                copyTo(userFileFolder1, u.getFolderId());
            }
        }
    }


    /**
     * 分享文件
     *
     * @param encrypt
     * @param validay
     * @param foerverRadio
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/cloudstorage/usercloud/sharefiles", method = HttpMethod.all)
    public UserCloudShare shareFiles(Boolean encrypt, Integer validay, Boolean foerverRadio, String shareName) throws Exception {
        UserCloudShare userCloudShare = new UserCloudShare();
        List<Integer> folerIdList = formatIdsByStr(folderIds);
        if (folerIdList != null && folerIdList.size() > 0) {
            List<UserFileFolder> userFileFolders = new ArrayList<UserFileFolder>();
            for (Integer id : folerIdList) {
                UserFileFolder userFileFolder = dao.get(UserFileFolder.class, id);
                userFileFolders.add(userFileFolder);
            }
            userCloudShare.setUserShareFolders(userFileFolders);
        }

        List<Integer> fileIdsList = formatIdsByStr(fileIds);
        if (fileIdsList != null && fileIdsList.size() > 0) {
            List<UserFile> userFiles = new ArrayList<UserFile>();
            for (Integer id : fileIdsList) {
                UserFile userFile = dao.get(UserFile.class, id);
                userFiles.add(userFile);
            }
            userCloudShare.setUserShareFiles(userFiles);
        }
        userCloudShare.setEncrypt(encrypt);
        userCloudShare.setFoerver(foerverRadio);
        userCloudShare.setShareTime(new Date());
        userCloudShare.setShareName(shareName);
        if (!foerverRadio) {
            userCloudShare.setValidDay(validay);
            userCloudShare.setExpiredTime(new Date(userCloudShare.getShareTime().getTime() + userCloudShare.getValidDay() * 24 * 60 * 60 * 1000));
        }
        userCloudShare.setShareUserId(userOnlineInfo.getUserId());
        userCloudShare.setShareUser(userOnlineInfo.getUserEntity());
        if (encrypt) {
            userCloudShare.setPassword(generateWord());
        }
        dao.save(userCloudShare);
        SystemCrudUtils.saveLog(userCloudShare, LogAction.add,null,null);
        return userCloudShare;
    }


    /**
     * 取消分享
     *
     * @return
     */
    @Service(url = "/oa/cloudstorage/usercloud/usercloudcrud/channelShareFiles")
    @Transactional
    public Boolean channelShareFiles() {
        try {
            List<Integer> shareIdLists = formatIdsByStr(shareIds);
            if (shareIdLists != null && shareIdLists.size() > 0) {
                for(Integer id:shareIdLists){
                    SystemCrudUtils.saveLog(dao.load(UserCloudShare.class,id), LogAction.modify,null,null);
                }
                dao.channelShareFiles(shareIdLists);
            }
            return true;
        } catch (Exception e) {
            Tools.log(e.getMessage());
            return false;
        }
    }

    /**
     * 移动到目标文件夹
     *
     * @param seledFolderId
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/cloudstorage/usercloud/moveToTargetFolder")
    public Boolean moveToTargetFolder(Integer seledFolderId) throws Exception {
        try {
            List<Integer> folderIdsList = formatIdsByStr(folderIds);
            if (folderIdsList != null && folderIdsList.size() > 0) {
                for (Integer id : folderIdsList) {
                    UserFileFolder userFileFolder = dao.get(UserFileFolder.class, id);
                    userFileFolder.setParentFolderId(seledFolderId);
                    SystemCrudUtils.saveLog(userFileFolder, LogAction.modify,null,null);
                    dao.save(userFileFolder);
                }
            }
            List<Integer> fileIdsList = formatIdsByStr(fileIds);
            if (fileIdsList != null && fileIdsList.size() > 0) {
                for (Integer id : fileIdsList) {
                    UserFile userFileSource = dao.get(UserFile.class, id);
                    userFileSource.setFolderId(seledFolderId);
                    userFileSource.setSource("move");
                    userFileSource.setUpdateTime(new Date());
                    SystemCrudUtils.saveLog(userFileSource, LogAction.modify,null,null);
                    dao.save(userFileSource);
                }
            }
            return true;
        } catch (Exception e) {
            Tools.log(e);
            return false;
        }
    }

    @Service(url = "/oa/cloudstorage/usercloud/showSharePage")
    public String showSharePage(Integer id, String fileIdsStr, String folderIdsStr) throws Exception {
        fileIds = fileIdsStr;
        folderIds = folderIdsStr;
        if (id != null) {
            shareId = id;
            shareName = dao.get(UserCloudShare.class, id).getShareName();
        }
        return "/oa/cloudstorage/usercloud/share.ptl";
    }


    /**
     * 分享关系建立
     *
     * @param shareName
     * @return
     * @throws Exception
     */
    @Service(method = HttpMethod.all)
    public Integer saveShareRecord(String shareName, Integer shareId) throws Exception {
        UserCloudShare share;
        if (shareId == null || shareId == 0) {
            share = shareFiles(false, null, true, shareName);
        } else {
            share = dao.get(UserCloudShare.class, shareId);
        }
        List<ShareRecord> shareList=null;
        if(CollectionUtils.isNotEmpty(shareRecords)){
            shareList=new ArrayList<ShareRecord>();
            List<Integer> shareDpteList=new ArrayList<Integer>();
            for(ShareRecord shareRecord:shareRecords){
                if(shareRecord.getDeptId()!=0){
                    Dept dept=dao.load(Dept.class,shareRecord.getDeptId());
                    shareDpteList.add(shareRecord.getDeptId());
                    shareDpteList.addAll(dept.allSubDeptIds());
                }else{
                    shareList.add(shareRecord);
                }
            }
            if(CollectionUtils.isNotEmpty(shareDpteList)){
                for(Integer deptId:shareDpteList){
                    ShareRecord dpetShare= new ShareRecord();
                    dpetShare.setType(ShareType.DEPT);
                    dpetShare.setUserId(0);
                    dpetShare.setDeptId(deptId);
                    shareList.add(dpetShare);
                }
            }
        }else{
            shareList=shareRecords;
        }
        share.setShareRecords(shareList);
        SystemCrudUtils.saveLog(share, LogAction.modify,null,null);
        dao.save(share);
        return share.getShareId();
    }

    /**
     * 根据父文件的ID获取子文件
     *
     * @param parentId
     * @return
     */
    @ObjectResult
    @Service(url = "/oa/cloudstorage/usercloud/createFolderTreeChildren")
    public String createFolderTreeChildren(Integer parentId) {
        String treeStr = "";
        List<UserFileFolder> userFileFolders = dao.getUserFileFolders(userOnlineInfo.getUserId(), parentId);
        for (UserFileFolder userFileFolder : userFileFolders) {
            if (userFileFolder.getChildren() != null && userFileFolder.getChildren().size() > 0) {
                treeStr += "<ul class='fzd'><li class='current' id='root'><span  state='0' class='seled'code= '" + userFileFolder.getFolderId() + "'onclick='spanToggle(this)'><img src='/oa/cloudstorage/image/jjh_03.png' width='26' height='21'/>" + userFileFolder.getFolderName() + "</span></ul>";
            } else {
                treeStr += "<ul>" + "<li class='childli'><a state='1' class='seled' code='" + userFileFolder.getFolderId() + "' href='#'><img src='/oa/cloudstorage/image/jjh_03.png' width='26' height='21'/>" + userFileFolder.getFolderName() + "</a></li></ul>";
            }
        }
        return treeStr;
    }
}
