package com.gzzm.oa.userfile;

import com.gzzm.oa.cloudstorage.usercloud.UserCloudShare;
import com.gzzm.platform.commons.NoErrorException;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.CacheData;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 和个人资料相关的一些方法
 *
 * @author camel
 * @date 2011-4-6
 */
public class UserFileService {
    /**
     * 使用Provider实现配置的惰性加载，避免每次访问都从数据库加载配置
     */
    @Inject
    private static Provider<UserFileCommonConfig> configProvider;

    @Inject
    private UserFileDao dao;

    private UserFileCommonConfig config;

    public UserFileService() {
    }

    public UserFileDao getDao() {
        return dao;
    }

    public UserFileCommonConfig getConfig() {
        if (config == null)
            config = configProvider.get();

        return config;
    }

    public UserFile getUserFile(Integer fileId) throws Exception {
        return dao.getUserFile(fileId);
    }

    public UserFileContent getFileContent(Integer fileId) throws Exception {
        return dao.getFileContent(fileId);
    }

    public UserFileFolder getFolder(Integer folderId) throws Exception {
        return dao.getFolder(folderId);
    }

    public List<UserFileFolder> getFolders(Integer userId, Integer parentFolderId) throws Exception {
        return dao.getFolders(userId, parentFolderId);
    }

    public UserFileConfig getUserFileConfig(Integer userId) throws Exception {
        UserFileConfig fileConfig = dao.getUserFileConfig(userId);

        if (fileConfig == null)
            fileConfig = new UserFileConfig();

        if (fileConfig.getLibrarySize() == null)
            fileConfig.setLibrarySize(getConfig().getLibrarySize());

        if (fileConfig.getUploadSize() == null)
            fileConfig.setUploadSize(getConfig().getUploadSize());

        if (fileConfig.getUsed() == null)
            fileConfig.setUsed((long) 0);

        return fileConfig;
    }

    public Long sumFileSize(Integer userId) throws Exception {
        return dao.sumFileSize(userId);
    }

    public void initFile(UserFile userFile, InputFile file) throws Exception {
        String fileName = file.getName();

        String fileType = IOUtils.getExtName(fileName);
        if (fileType == null)
            fileType = "";
        userFile.setFileType(fileType);

        userFile.setFileSize(file.size());

        if (StringUtils.isEmpty(userFile.getFileName())) {
            if (fileType.length() > 0) {
                fileName = fileName.substring(0, fileName.length() - fileType.length() - 1);
            }

            userFile.setFileName(fileName);
        }
    }

    /**
     * 检查文件是否超过上限
     *
     * @param file   要检查的文件
     * @param userId 当前用户
     * @param fileId 此文件将覆盖的文件的id，可以为null，表示不覆盖任何文件
     * @throws Exception 数据库读取数据错误
     * @author wmy
     */
    public void checkFile(InputFile file, Integer userId, Integer fileId) throws Exception {
        if (file != null) {
            checkFile(file.size(), userId, fileId);
        }
    }

    public void checkFile(long fileSize, Integer userId, Integer fileId) throws Exception {
        UserFileConfig config = dao.getUserFileConfig(userId);

        // 限制文件大小
        long uploadSize;
        if (config != null && config.getUploadSize() != null)
            uploadSize = config.getUploadSize();
        else
            uploadSize = getConfig().getUploadSize();

        uploadSize *= 1024;

        // 将提示信息写到资源文件里
        if (fileSize > uploadSize)
            throw new NoErrorException("oa.userfile.sizeExceeded", IOUtils
                    .getSizeString(uploadSize), IOUtils.getSizeString(fileSize));

        long used = 0;
        if (config != null && config.getUsed() != null)
            used = config.getUsed();

        boolean check = true;
        long oldFileSize = 0;
        if (fileId != null) {
            // 原来的文件存在，减掉原来的文件大小
            UserFile userFile = dao.getUserFile(fileId);

            oldFileSize = userFile.getFileSize();

            // 文件小于原来的，不需要检查
            check = fileSize > oldFileSize;
        }

        if (check) {
            long remainSize;
            if (config != null && config.getLibrarySize() != null)
                remainSize = config.getLibrarySize();
            else
                remainSize = getConfig().getLibrarySize();

            remainSize *= 1024;
            remainSize -= used;
            remainSize += oldFileSize;

            // 将提示信息写到资源文件里
            if (fileSize > remainSize)
                throw new NoErrorException("oa.userfile.noCapacity",
                        IOUtils.getSizeString(remainSize), IOUtils
                        .getSizeString(fileSize));
        }

        used += fileSize - oldFileSize;
        if (used <= 0) {
            // 考虑到各种原因数据库配置表的数据可能出错没有初始化，从数据库直接sum计算占有总空间
            Long sumFileSize = dao.sumFileSize(userId);

            if (sumFileSize == null)
                sumFileSize = 0L;

            used = sumFileSize + fileSize - oldFileSize;
        }

        // 更新用户文件使用量
        updateUsed(config, userId, used);
    }

    /**
     * 更新使用量
     *
     * @param config: 配置信息
     * @param userId  当前用户ID
     * @param used:   使用量
     * @throws Exception 写数据库错误
     * @author wmy
     */
    public void updateUsed(UserFileConfig config, Integer userId, long used) throws Exception {
        if (config == null) {
            config = new UserFileConfig();
            config.setUserId(userId);
            config.setUsed(used);
            dao.add(config);
        } else {
            config.setUsed(used);
            dao.update(config);
        }
    }

    /**
     * 删除后删除文件内容并修改个人文件配置中的已使用空间
     * 之所以在beforeDelete中写而不在afterDelete写，是因为afterDelete后无法获得被删除的文件的大小
     *
     * @param userId  当前用户ID
     * @param fileIds 删除的文件的id
     * @throws Exception 数据库操作异常
     */
    public void updateForDelete(Integer userId, Integer... fileIds) throws Exception {
        dao.deleteFileContent(fileIds);

        UserFileConfig config = dao.getUserFileConfig(userId);

        long used = 0;
        if (config != null && config.getUsed() != null)
            used = config.getUsed();

        // 将删除的文件占用的容量从使用的容量中减去
        long filesSize = dao.getFilesSize(fileIds);

        used -= filesSize;
        if (used < 0) {
            // 考虑到各种原因数据库配置表的数据可能出错没有初始化，从数据库直接sum计算占有总空间
            used = dao.sumFileSize(userId) - filesSize;
        }

        updateUsed(config, userId, used);
    }

    /**
     * 添加一个文件到某个目录
     *
     * @param file     文件内容
     * @param userId   用户id，文件所属的用户
     * @param folderId 目录ID，如果为空则默认为根目录0
     * @param source   文件来源
     * @param remark   文件备注
     * @throws Exception 数据库操作异常
     */
    @Transactional
    public void addFile(InputFile file, Integer userId, Integer folderId, String source, String remark) throws Exception {
        checkFile(file, userId, null);

        if (folderId == null)
            folderId = 0;

        Date now = new Date();

        UserFile userFile = new UserFile();
        userFile.setUserId(userId);
        userFile.setUploadTime(now);
        userFile.setUpdateTime(now);
        userFile.setFolderId(folderId);
        userFile.setSource(source);
        userFile.setRemark(remark);

        initFile(userFile, file);

        dao.add(userFile);

        UserFileContent content = new UserFileContent();
        content.setFileId(userFile.getFileId());
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
        UserFileFolder folder = dao.getFolder(folderId);

        CacheData cacheData = new CacheData();
        cacheData.setAutoClear(true);
        CompressUtils.Compress compress = CompressUtils.createCompress("zip", cacheData);

        zip(compress, folder, null);

        compress.close();

        return new InputFile(cacheData, folder.getFolderName() + ".zip");
    }

    private void zip(CompressUtils.Compress compress, UserFileFolder folder, String path) throws Exception {
        String name = folder.getFolderName();
        if (path != null)
            name = path + "/" + name;
        compress.addDirectory(name);

        for (UserFileFolder childFolder : folder.getChildren()) {
            zip(compress, childFolder, name);
        }

        List<UserFile> files = dao.getFiles(folder.getUserId(), folder.getFolderId());
        for (UserFile file : files) {
            zip(compress, file, name);
        }
    }

    private void zip(CompressUtils.Compress compress, UserFile file, String path) throws Exception {
        String fileName = file.getFileName();
        if (!StringUtils.isEmpty(file.getFileType()))
            fileName += "." + file.getFileType();

        if (path != null)
            fileName = path + "/" + fileName;

        compress.addFile(fileName, file.getFileContent().getContent().getInputStream(),
                file.getUpdateTime().getTime(), file.getRemark());
    }

    public InputFile zipFoldersAndFiles(List<UserFileFolder> folders, List<UserFile> files, String zipName) throws Exception {
        CacheData cacheData = new CacheData();
        cacheData.setAutoClear(true);
        CompressUtils.Compress compress = CompressUtils.createCompress("zip", cacheData);
        if(CollectionUtils.isNotEmpty(folders)){
            for (UserFileFolder folder : folders) {
                zip(compress, folder, null);
            }
        }
        if(CollectionUtils.isNotEmpty(files)){
            for (UserFile file : files) {
                zip(compress, file, null);
            }
        }
        compress.close();
        return new InputFile(cacheData, zipName + ".zip");
    }

    public InputFile zipShareFolder(List<Integer> shareIdsList) throws Exception {
        if (shareIdsList.size() == 1) {
            UserCloudShare ucs = dao.get(UserCloudShare.class, shareIdsList.get(0));
            return zipFoldersAndFiles(ucs.getUserShareFolders(), ucs.getUserShareFiles(), ucs.getShareName());
        } else {
            CacheData cacheData = new CacheData();
            cacheData.setAutoClear(true);
            CompressUtils.Compress compress = CompressUtils.createCompress("zip", cacheData);
            for (Integer id : shareIdsList) {
                UserCloudShare ucs = dao.get(UserCloudShare.class, id);
                zipShare(compress,ucs,null);
            }
            compress.close();
            return new InputFile(cacheData,"云盘分享.zip");
        }
    }

    private void zipShare(CompressUtils.Compress compress, UserCloudShare cloudShare, String path) throws Exception {
        String name = cloudShare.getShareName();
        if (path != null)
            name = path + "/" + name;
        compress.addDirectory(name);

        for (UserFileFolder childFolder : cloudShare.getUserShareFolders()) {
            zip(compress, childFolder, name);
        }

        List<UserFile> files = cloudShare.getUserShareFiles();
        for (UserFile file : files) {
            zip(compress, file, name);
        }
    }
}
