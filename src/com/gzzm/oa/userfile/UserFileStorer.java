package com.gzzm.oa.userfile;

import com.gzzm.platform.commons.filestore.*;
import net.cyan.commons.util.*;
import net.cyan.crud.QueryType;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 将文件保存到个人资料
 *
 * @author camel
 * @date 2011-4-6
 */
public class UserFileStorer implements FileStorer
{
    @Inject
    private static Provider<UserFileService> serviceProvider;

    public UserFileStorer()
    {
    }

    public String getName()
    {
        return null;
    }

    public String getId()
    {
        return "userfile";
    }

    @Override
    public boolean isValid(Integer userId, Integer deptId, boolean readOnly)
    {
        return true;
    }

    public void save(InputFile file, Integer userId, Integer deptId, String catalogId, String source, String remark)
            throws Exception
    {
        serviceProvider.get().addFile(file, userId, StringUtils.isEmpty(catalogId) ? null : Integer.valueOf(catalogId),
                source, remark);
    }

    public List<InputFile> get(String fileId, Integer userId, Integer deptId) throws Exception
    {
        UserFile userFile = serviceProvider.get().getUserFile(Integer.valueOf(fileId));

        String fileName = userFile.getFileName();
        if (!StringUtils.isEmpty(userFile.getFileType()))
            fileName += "." + userFile.getFileType();

        return Collections.singletonList(new InputFile(userFile.getFileContent().getContent(), fileName));
    }

    public List<FileCatalog> getCatalogs(String parentCatalogId, Integer userId, Integer deptId, boolean writable)
            throws Exception
    {
        List<UserFileFolder> folders = serviceProvider.get().getFolders(userId,
                StringUtils.isEmpty(parentCatalogId) ? 0 : Integer.valueOf(parentCatalogId));

        List<FileCatalog> catalogs = new ArrayList<FileCatalog>(folders.size());

        for (UserFileFolder folder : folders)
        {
            catalogs.add(new FileCatalog(folder.getFolderId().toString(), folder.getFolderName(),
                    folder.getChildren().size() > 0));
        }

        return catalogs;
    }

    public void loadFileList(FileQuery fileQuery) throws Exception
    {
        if (StringUtils.isEmpty(fileQuery.getId()))
            fileQuery.setId("0");

        fileQuery.setQueryType(QueryType.oql);
        fileQuery.setBeanType(UserFile.class);
        fileQuery.setQueryString("select f from UserFile f where userId=:userId and folderId=:id and " +
                "fileName like ?title and contains(text,?text) and updateTime>=?time_start and updateTime<?time_end" +
                " order by updateTime desc");

        fileQuery.setTransformer(new FileItemTransformer()
        {
            public FileItem transform(Object bean) throws Exception
            {
                UserFile userFile = (UserFile) bean;

                FileItem fileItem = new FileItem();

                fileItem.setId(userFile.getFileId().toString());

                String fileName = userFile.getFileName();
                if (!StringUtils.isEmpty(userFile.getFileType()))
                    fileName += "." + userFile.getFileType();
                fileItem.setTitle(fileName);

                fileItem.setTime(userFile.getUpdateTime());
                fileItem.setSource(userFile.getSourceName());
                fileItem.setRemark(userFile.getRemark());

                return fileItem;
            }
        });

        fileQuery.loadList0();
    }
}
