package com.gzzm.oa.cloudstorage.deptcloud;

import com.gzzm.oa.cloudstorage.CommonCloudCrud;
import com.gzzm.oa.deptfile.DeptFile;
import com.gzzm.oa.deptfile.DeptFileContent;
import com.gzzm.oa.deptfile.DeptFileFolder;
import com.gzzm.oa.deptfile.DeptFileService;
import com.gzzm.platform.commons.FileUploadService;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.SystemCrudUtils;
import com.gzzm.platform.log.LogAction;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.ObjectResult;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.BeanUtils;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gyw
 * @date 2017/8/23 0023
 */
@Service
public class DeptCloudCrud extends CommonCloudCrud {

    /**
     * 导航栏
     */
    private static List<DeptFileFolder> navList = new ArrayList<DeptFileFolder>();

    @Inject
    private DeptFileService service;

    private List<DeptFile> deptFiles;

    private List<DeptFileFolder> deptFileFolders;

    public DeptCloudCrud() {
        tag = 0;
    }

    public List<DeptFile> getDeptFiles() {
        return deptFiles;
    }

    public void setDeptFiles(List<DeptFile> deptFiles) {
        this.deptFiles = deptFiles;
    }

    public List<DeptFileFolder> getDeptFileFolders() {
        return deptFileFolders;
    }

    public void setDeptFileFolders(List<DeptFileFolder> deptFileFolders) {
        this.deptFileFolders = deptFileFolders;
    }

    @Service(url = "/oa/cloudstorage/deptcloud/deptcloudcrud")
    public String toHomePage() throws Exception {
        String condition = " where f.deptId = " + userOnlineInfo.getDeptId();
        if (nowfolderId == null || nowfolderId ==0) {
            navList.removeAll(navList);
            nowfolderId = 0;
        } else {
            if (nowfolderId != 0) {
                DeptFileFolder d = dao.get(DeptFileFolder.class, nowfolderId);
                if (!navList.contains(d)) {
                    navList.add(d);
                } else {
                    navList = navList.subList(0, navList.indexOf(d) + 1);
                }
            }
        }
        if (type != null && type != 0) {
            navList.removeAll(navList);
        }
        RequestContext.getContext().getSession().setAttribute("navList", navList);
        deptFiles = dao.getObjectListByCondition(DeptFile.class, getFileCondition(condition));
        deptFileFolders = dao.getObjectListByCondition(DeptFileFolder.class, (type != null && type != 0) ? null : getFileFolderCondition(condition));
        fileCount = (deptFiles != null ? deptFiles.size() : 0) + (deptFileFolders == null ? 0 : deptFileFolders.size());
        return "/oa/cloudstorage/deptcloud/deptcloudhome.ptl";
    }

    /**
     * 导航显示文件夹路径
     *
     * @param deptFileFolders
     * @param index
     * @return
     */
    @ObjectResult
    public String createPath(List<DeptFileFolder> deptFileFolders, Integer index) {
        String path = "/";
        if (index == 0) {
            path += deptFileFolders.get(index).getFolderName();
        } else {
            for (int i = 0; i <= index; i++) {
                path += deptFileFolders.get(i).getFolderName() + "/";
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
    @Service(url = "/oa/cloudstorage/deptcloud/{$0}/down")
    public InputFile downloadFile(Integer fileId, String contentType) throws Exception {
        DeptFile deptFile = dao.get(DeptFile.class, fileId);
        DeptFileContent content = service.getFileContent(fileId);

        // 考虑fileType为null的情况
        String fileName = deptFile.getFileName();
        if (!StringUtils.isEmpty(deptFile.getFileType()))
            fileName += "." + deptFile.getFileType();

        if (contentType == null)
            contentType = deptFile.getFileType();

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
    @Service(method = HttpMethod.all)
    public DeptFileFolder createFolder(Integer parentId, String folderName) throws Exception {
        if (dao.checkNameRepeat(DeptFileFolder.class, folderName, userOnlineInfo.getDeptId(), parentId,0) > 0) {
            return null;
        }
        DeptFileFolder deptFileFolder = new DeptFileFolder();
        deptFileFolder.setParentFolder(dao.get(DeptFileFolder.class, parentId));
        deptFileFolder.setParentFolderId(parentId);
        deptFileFolder.setFolderName(folderName);
        deptFileFolder.setCreateTime(new Date());
        deptFileFolder.setDeptId(userOnlineInfo.getDeptId());
        dao.save(deptFileFolder);
        SystemCrudUtils.saveLog(deptFileFolder, LogAction.add,null,null);
        return deptFileFolder;
    }

    /**
     * 删除文件
     *
     * @return
     */
    @Service(url = "/oa/cloudstorage/deptcloud/deptcloudcrud/deleteFolderAndFiles")
    @Transactional
    public Boolean deleteFolderAndFiles() {
        try {
            List<Integer> folderIdsList = formatIdsByStr(folderIds);
            if (folderIdsList != null && folderIdsList.size() > 0) {
                for (Integer folderId : folderIdsList) {
                    DeptFileFolder deptFileFolder = dao.get(DeptFileFolder.class, folderId);
                    deleteAllFilesByFolder(deptFileFolder);
                }
            }

            List<Integer> fileIdsList = formatIdsByStr(fileIds);
            if (fileIdsList != null && fileIdsList.size() > 0) {
                for(Integer id:fileIdsList){
                    SystemCrudUtils.saveLog(dao.load(DeptFile.class,id), LogAction.delete,null,null);
                }
                dao.deleteByIds(fileIdsList);
            }
            return true;
        } catch (Exception e) {
            Tools.log(e.getMessage());
            return false;
        }
    }

    private void deleteAllFilesByFolder(DeptFileFolder deptFileFolder) throws Exception {
        List<DeptFile> deptFiles = dao.getDeptFiles(userOnlineInfo.getDeptId(), deptFileFolder.getFolderId());
        if (deptFiles != null && deptFiles.size() > 0) {
            for (DeptFile deptFile : deptFiles) {
                SystemCrudUtils.saveLog(deptFile, LogAction.delete,null,null);
                dao.delete(deptFile);
            }
        }
        List<DeptFileFolder> childeFileFolders = dao.getDeptFileFolders(userOnlineInfo.getDeptId(), deptFileFolder.getFolderId());
        if (childeFileFolders != null && childeFileFolders.size() > 0) {
            for (DeptFileFolder deptFileFolder1 : childeFileFolders) {
                deleteAllFilesByFolder(deptFileFolder1);
            }
        }
        SystemCrudUtils.saveLog(deptFileFolder, LogAction.delete,null,null);
        dao.delete(deptFileFolder);
    }

    /**
     * 上传
     *
     * @param filePaths
     * @throws Exception
     */
    @Service(url = "/oa/cloudstorage/deptcloud/upload", method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void upload(String[] filePaths) throws Exception {
        FileUploadService service = uploadServiceProvider.get();

        for (String filePath : filePaths) {
            InputFile file = service.getFile(filePath);
            try {
                setFile(new InputFile(file.getInputable(), file.getName()));
                DeptFile deptFile = new DeptFile();
                deptFile.setFolderId(nowfolderId);
                String[] nameAndType = file.getName().split("\\.");
                String name = nameAndType[0];
                String type = nameAndType[nameAndType.length - 1];
                deptFile.setFileName(name);
                deptFile.setFileType(type);
                deptFile.setFileSize(file.size());
                deptFile.setUploadTime(new Date());
                deptFile.setUpdateTime(deptFile.getUploadTime());
                deptFile.setSource("upload");
                deptFile.setEditFile(true);
                deptFile.setRemark("");
                deptFile.setCreateUser(userOnlineInfo.getUserEntity());
                deptFile.setCreator(userOnlineInfo.getUserId());
                deptFile.setDeptId(userOnlineInfo.getDeptId());
                dao.save(deptFile);
                SystemCrudUtils.saveLog(deptFile, LogAction.add,null,null);

                DeptFileContent deptFileContent = new DeptFileContent();
                deptFileContent.setFileId(deptFile.getFileId());
                deptFileContent.setContent(file.getInputable());
                dao.save(deptFileContent);
                SystemCrudUtils.saveLog(deptFileContent, LogAction.add,null,null);
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
    @Service(url = "/oa/cloudstorage/deptcloud/zip")
    public InputFile zipAll() throws Exception {
        List<Integer> folderIdsList = formatIdsByStr(folderIds);
        List<Integer> fileIdsList = formatIdsByStr(fileIds);
        if (folderIdsList != null && folderIdsList.size() == 1 && fileIdsList == null) {
            return service.zip(folderIdsList.get(0));
        } else if (folderIdsList == null && fileIdsList != null && fileIdsList.size() == 1) {
            return downloadFile(fileIdsList.get(0), null);
        }
        return service.zipFoldersAndFiles(folderIdsList, fileIdsList);
    }

    /**
     * 文件夹树生成(全部)
     *
     * @return
     */
    @Service(url = "/oa/cloudstorage/deptcloud/createAllUserFolder")
    @ObjectResult
    public String createAllUserFolder() {
        String preStr = "<ul class=\"fzd\">\n" +
                "<li class=\"current\" id='root'><span class='seled'";
        String lastStr = " onclick='spanToggle(this)'><img src=\"../image/jjh_03.png\" width=\"26\" height=\"21\"/>";
        StringBuffer sb = new StringBuffer().append(preStr + "code='0'" + lastStr + "全部文件</span>");
        List<DeptFileFolder> deptFileFolders = dao.getDeptFileFolders(userOnlineInfo.getDeptId(), 0);

        for (DeptFileFolder u : deptFileFolders) {
            createElement(preStr, lastStr, u, "");
            sb.append(eleStr);
            eleStr = "";
        }
        sb.append("</li></ul>");
        return sb.toString();
    }

    private void createElement(String preStr, String lastStr, DeptFileFolder deptFileFolder, String superStr) {
        List<DeptFileFolder> deptFileFolders = dao.getDeptFileFolders(userOnlineInfo.getDeptId(), deptFileFolder.getFolderId());
        if (deptFileFolders != null && deptFileFolders.size() > 0) {
            eleStr = eleStr + preStr + "code='" + deptFileFolder.getFolderId() + "'" + lastStr + deptFileFolder.getFolderName() + "</span>";
            for (DeptFileFolder uf : deptFileFolders) {
                createElement(preStr, lastStr, uf, eleStr);
            }
            eleStr += "</li></ul>";

        } else {
            eleStr = superStr + "<ul>\n" + "<li class='childli'><a class='seled' code='" + deptFileFolder.getFolderId() + "' href=\"#\"><img src=\"../image/jjh_03.png\" width=\"26\" height=\"21\"/>" + deptFileFolder.getFolderName() + "</a></li>\n" + "</ul>";
        }
    }

    /**
     * 将文件复制到目标文件夹
     *
     * @param seledFolderId
     * @return
     */
    @Service(url = "/oa/cloudstorage/deptcloud/copyToTargetFolder")
    public Boolean copyToTargetFolder(Integer seledFolderId) throws Exception {
        try {
            List<Integer> folderIdsList = formatIdsByStr(folderIds);
            if (folderIdsList != null && folderIdsList.size() > 0) {
                for (Integer id : folderIdsList) {
                    DeptFileFolder deptFileFolder = dao.get(DeptFileFolder.class, id);
                    copyTo(deptFileFolder, seledFolderId);
                }
            }
            List<Integer> fileIdsList = formatIdsByStr(fileIds);
            if (fileIdsList != null && fileIdsList.size() > 0) {
                for (Integer id : fileIdsList) {
                    DeptFile deptFileSource = dao.get(DeptFile.class, id);
                    DeptFile deptFile = new DeptFile();
                    BeanUtils.copyProperties(deptFileSource, deptFile, new BaseTypeFilter());
                    deptFile.setFolderId(seledFolderId);
                    deptFile.setFileId(null);
                    deptFile.setSource("copy");
                    deptFile.setDeptId(userOnlineInfo.getDeptId());
                    deptFile.setRemark("");
                    deptFile.setUpdateTime(new Date());
                    deptFile.setUploadTime(new Date());
                    dao.save(deptFile);
                    SystemCrudUtils.saveLog(deptFile, LogAction.add,null,null);

                    DeptFileContent ufc = new DeptFileContent();
                    BeanUtils.copyProperties(deptFileSource.getFileContent(), ufc, new BaseTypeFilter());
                    ufc.setFileId(deptFile.getFileId());
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

    private void copyTo(DeptFileFolder deptFileFolder, Integer folderId) throws Exception {
        DeptFileFolder u = new DeptFileFolder();
        BeanUtils.copyProperties(deptFileFolder, u, new BaseTypeFilter());
        u.setCreateTime(new Date());
        u.setParentFolderId(folderId);
        u.setDeptId(userOnlineInfo.getDeptId());
        u.setFolderId(null);
        dao.save(u);
        SystemCrudUtils.saveLog(u, LogAction.add,null,null);

        List<DeptFile> deptFiles = dao.getDeptFiles(userOnlineInfo.getDeptId(), deptFileFolder.getFolderId());
        if (deptFiles != null && deptFiles.size() > 0) {
            for (DeptFile deptFile : deptFiles) {
                DeptFile uf = new DeptFile();
                BeanUtils.copyProperties(deptFile, uf, new BaseTypeFilter());
                uf.setFileContent(null);
                uf.setFileId(null);
                uf.setFileFolder(null);
                uf.setFolderId(u.getFolderId());
                uf.setUpdateTime(new Date());
                uf.setDeptId(userOnlineInfo.getDeptId());
                uf.setUploadTime(uf.getUpdateTime());
                uf.setSource("copy");
                dao.save(uf);
                SystemCrudUtils.saveLog(uf, LogAction.add,null,null);

                DeptFileContent ufc = new DeptFileContent();
                BeanUtils.copyProperties(deptFile.getFileContent(), ufc, new BaseTypeFilter());
                ufc.setFileId(uf.getFileId());
                dao.save(ufc);
                SystemCrudUtils.saveLog(ufc, LogAction.add,null,null);
            }
        }

        List<DeptFileFolder> childeFileFolders = dao.getDeptFileFolders(userOnlineInfo.getUserId(), deptFileFolder.getFolderId());
        if (childeFileFolders != null && childeFileFolders.size() > 0) {
            for (DeptFileFolder deptFileFolder1 : childeFileFolders) {
                copyTo(deptFileFolder1, u.getFolderId());
            }
        }
    }

    /**
     * 移动到目标文件夹
     *
     * @param seledFolderId
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/cloudstorage/deptcloud/moveToTargetFolder")
    public Boolean moveToTargetFolder(Integer seledFolderId) throws Exception {
        try {
            List<Integer> folderIdsList = formatIdsByStr(folderIds);
            if (folderIdsList != null && folderIdsList.size() > 0) {
                for (Integer id : folderIdsList) {
                    DeptFileFolder deptFileFolder = dao.get(DeptFileFolder.class, id);
                    deptFileFolder.setParentFolderId(seledFolderId);
                    SystemCrudUtils.saveLog(deptFileFolder, LogAction.modify,null,null);
                    dao.save(deptFileFolder);
                }
            }
            List<Integer> fileIdsList = formatIdsByStr(fileIds);
            if (fileIdsList != null && fileIdsList.size() > 0) {
                for (Integer id : fileIdsList) {
                    DeptFile deptFileSource = dao.get(DeptFile.class, id);
                    deptFileSource.setFolderId(seledFolderId);
                    deptFileSource.setSource("move");
                    deptFileSource.setUpdateTime(new Date());
                    SystemCrudUtils.saveLog(deptFileSource, LogAction.modify,null,null);
                    dao.save(deptFileSource);
                }
            }
            return true;
        } catch (Exception e) {
            Tools.log(e);
            return false;
        }
    }

    /**
     * 根据父文件的ID获取子文件
     *
     * @param parentId
     * @return
     */
    @ObjectResult
    @Service(url = "/oa/cloudstorage/deptcloud/createFolderTreeChildren")
    public String createFolderTreeChildren(Integer parentId) {
        String treeStr = "";
        List<DeptFileFolder> deptFileFolders = dao.getDeptFileFolders(userOnlineInfo.getDeptId(), parentId);
        for (DeptFileFolder deptFileFolder : deptFileFolders) {
            if (deptFileFolder.getChildren() != null && deptFileFolder.getChildren().size() > 0) {
                treeStr += "<ul class='fzd'><li class='current' id='root'><span  state='0' class='seled'code= '" + deptFileFolder.getFolderId() + "'onclick='spanToggle(this)'><img src='/oa/cloudstorage/image/jjh_03.png' width='26' height='21'/>" + deptFileFolder.getFolderName() + "</span></ul>";
            } else {
                treeStr += "<ul>" + "<li class='childli'><a state='1' class='seled' code='" + deptFileFolder.getFolderId() + "' href='#'><img src='/oa/cloudstorage/image/jjh_03.png' width='26' height='21'/>" + deptFileFolder.getFolderName() + "</a></li></ul>";
            }
        }
        return treeStr;
    }
}