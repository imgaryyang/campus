package com.gzzm.oa.deptfile;

import com.gzzm.oa.userfile.UserFile;
import com.gzzm.oa.userfile.UserFileFolder;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.CacheData;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 和部门资料相关的一些方法
 *
 * @author camel
 * @date 2011-4-6
 */
public class DeptFileService {
    @Inject
    private DeptFileDao dao;

    public DeptFileService() {
    }

    public DeptFileDao getDao() {
        return dao;
    }

    public DeptFile getUserFile(Integer fileId) throws Exception {
        return dao.getDeptFile(fileId);
    }

    public DeptFileContent getFileContent(Integer fileId) throws Exception {
        return dao.getFileContent(fileId);
    }

    public DeptFileFolder getFolder(Integer folderId) throws Exception {
        return dao.getFolder(folderId);
    }

    public List<DeptFileFolder> getFolders(Collection<Integer> deptIds, Integer parentFolderId) throws Exception {
        if (parentFolderId == 0)
            return dao.getTopFolders(deptIds);
        else
            return dao.getChildFolders(parentFolderId);
    }

    public void initFile(DeptFile deptFile, InputFile file) throws Exception {
        String fileName = file.getName();

        String fileType = IOUtils.getExtName(fileName);
        if (fileType == null)
            fileType = "";
        deptFile.setFileType(fileType);

        deptFile.setFileSize(file.size());

        if (StringUtils.isEmpty(deptFile.getFileName())) {
            if (fileType.length() > 0) {
                fileName = fileName.substring(0, fileName.length() - fileType.length() - 1);
            }

            deptFile.setFileName(fileName);
        }
    }

    /**
     * 添加一个文件到某个目录
     *
     * @param file     文件内容
     * @param deptId   文件保存到的部门
     * @param userId   用户id，保存文件的用户
     * @param folderId 目录ID，如果为空则默认为根目录0
     * @param source   文件来源
     * @param remark   文件备注
     * @throws Exception 数据库操作异常
     */
    @Transactional
    public void addFile(InputFile file, Integer deptId, Integer userId, Integer folderId, String source, String remark)
            throws Exception {
        if (folderId == null)
            folderId = 0;

        Date now = new Date();

        DeptFile deptFile = new DeptFile();
        deptFile.setCreator(userId);
        deptFile.setUploadTime(now);
        deptFile.setUpdateTime(now);
        deptFile.setFolderId(folderId);
        deptFile.setSource(source);
        deptFile.setRemark(remark);

        if (folderId != 0)
            deptId = dao.getFolder(folderId).getDeptId();

        deptFile.setDeptId(deptId);

        initFile(deptFile, file);

        dao.add(deptFile);

        DeptFileContent content = new DeptFileContent();
        content.setFileId(deptFile.getFileId());
        content.setContent(file.getInputable());

        dao.add(content);
    }

    /**
     * 压缩目录里的文件到一个zip文件中
     *
     * @param folderId 目录ID
     * @return 压缩后的zip文件
     * @throws Exception 数据库或io异常
     */
    public InputFile zip(Integer folderId) throws Exception {
        DeptFileFolder folder = dao.getFolder(folderId);

        CacheData cacheData = new CacheData();
        cacheData.setAutoClear(true);
        CompressUtils.Compress compress = CompressUtils.createCompress("zip", cacheData);

        zip(compress, folder, null);

        compress.close();

        return new InputFile(cacheData, folder.getFolderName() + ".zip");
    }

    private void zip(CompressUtils.Compress compress, DeptFileFolder folder, String path) throws Exception {
        String name = folder.getFolderName();
        if (path != null)
            name = path + "/" + name;
        compress.addDirectory(name);

        for (DeptFileFolder childFolder : folder.getChildren()) {
            zip(compress, childFolder, name);
        }

        List<DeptFile> files = dao.getFiles(folder.getDeptId(), folder.getFolderId());
        for (DeptFile file : files) {
            zip(compress, file, name);
        }
    }

    private void zip(CompressUtils.Compress compress, DeptFile file, String path) throws Exception {
        String fileName = file.getFileName();
        if (!StringUtils.isEmpty(file.getFileType()))
            fileName += "." + file.getFileType();

        if (path != null)
            fileName = path + "/" + fileName;

        compress.addFile(fileName, file.getFileContent().getContent().getInputStream(),
                file.getUpdateTime().getTime(), file.getRemark());
    }

    public InputFile zipFoldersAndFiles(List<Integer> folderIds, List<Integer> fileIds) throws Exception {
        CacheData cacheData = new CacheData();
        cacheData.setAutoClear(true);
        CompressUtils.Compress compress = CompressUtils.createCompress("zip", cacheData);
        if (folderIds != null && folderIds.size() > 0) {
            for (Integer folderId : folderIds) {
                DeptFileFolder folder = dao.getFolder(folderId);
                zip(compress, folder, null);
            }
        }
        if (fileIds != null && fileIds.size() > 0) {
            for (Integer fileId : fileIds) {
                DeptFile file = dao.getDeptFile(fileId);
                zip(compress, file, null);
            }
        }
        compress.close();
        return new InputFile(cacheData, "部门资料.zip");
    }
}
