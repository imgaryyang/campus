package com.gzzm.oa.deptfile;

import com.gzzm.platform.commons.filestore.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.UserInfo;
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
public class DeptFileStorer implements FileStorer
{
    public static final String EDIT_URL = "/oa/deptfile/file";

    public static final String QUERY_URL = "/oa/deptfile/query";

    @Inject
    private static Provider<DeptFileService> serviceProvider;

    @Inject
    private static Provider<UserOnlineInfo> userOnlineInfoProvider;

    public DeptFileStorer()
    {
    }

    public static UserInfo getUserInfo(Integer userId)
    {
        UserOnlineInfo userOnlineInfo = userOnlineInfoProvider.get();

        if (userOnlineInfo != null && userOnlineInfo.getUserId().equals(userId))
        {
            return userOnlineInfo;
        }
        else
        {
            return new UserInfo(userId, null);
        }
    }

    public static Collection<Integer> getAuthDeptIds(Integer userId, String url)
    {
        UserInfo userInfo = getUserInfo(userId);

        return userInfo.getAuthDeptIdsByUrl(url);
    }

    public String getName()
    {
        return null;
    }

    public String getId()
    {
        return "deptfile";
    }

    @Override
    public boolean isValid(Integer userId, Integer deptId, boolean readOnly)
    {
        UserInfo userInfo = getUserInfo(userId);

        if (userInfo.isAdmin())
            return true;

        return !userInfo.canAccessUrl(readOnly ? QUERY_URL : EDIT_URL);
    }

    public void save(InputFile file, Integer userId, Integer deptId, String catalogId, String source, String remark)
            throws Exception
    {
        Integer folderId = StringUtils.isEmpty(catalogId) ? null : Integer.valueOf(catalogId);

        if (folderId == null || folderId == 0)
        {
            Collection<Integer> authDeptIds = getAuthDeptIds(userId, EDIT_URL);
            if (authDeptIds != null && !authDeptIds.contains(deptId) && !authDeptIds.isEmpty())
            {
                deptId = authDeptIds.iterator().next();
            }
        }

        serviceProvider.get().addFile(file, deptId, userId, folderId, source, remark);
    }

    public List<InputFile> get(String fileId, Integer userId, Integer deptId) throws Exception
    {
        DeptFile deptFile = serviceProvider.get().getUserFile(Integer.valueOf(fileId));

        String fileName = deptFile.getFileName();
        if (!StringUtils.isEmpty(deptFile.getFileType()))
            fileName += "." + deptFile.getFileType();

        return Collections.singletonList(new InputFile(deptFile.getFileContent().getContent(), fileName));
    }

    public List<FileCatalog> getCatalogs(String parentCatalogId, Integer userId, Integer deptId, boolean writable)
            throws Exception
    {
        Collection<Integer> deptIds = null;
        int folderId = StringUtils.isEmpty(parentCatalogId) ? 0 : Integer.valueOf(parentCatalogId);
        if (folderId == 0)
        {
            deptIds = getAuthDeptIds(userId, writable ? EDIT_URL : QUERY_URL);
        }

        List<DeptFileFolder> folders = serviceProvider.get().getFolders(deptIds, folderId);

        List<FileCatalog> catalogs = new ArrayList<FileCatalog>(folders.size());

        for (DeptFileFolder folder : folders)
        {
            catalogs.add(new FileCatalog(folder.getFolderId().toString(), folder.getFolderName(),
                    folder.getChildren().size() > 0));
        }

        return catalogs;
    }

    public void loadFileList(FileQuery fileQuery) throws Exception
    {
        if (StringUtils.isEmpty(fileQuery.getId()))
        {
            fileQuery.setId("0");

            fileQuery.setAuthDeptIds(getAuthDeptIds(fileQuery.getUserId(), QUERY_URL));
        }

        fileQuery.setQueryType(QueryType.oql);
        fileQuery.setBeanType(DeptFile.class);
        fileQuery.setQueryString("select f from DeptFile f where folderId=:id and fileName like ?title" +
                " and contains(text,?text) and updateTime>=?time_start and updateTime<?time_end" +
                " and deptId in ?authDeptIds order by updateTime desc");

        fileQuery.setTransformer(new FileItemTransformer()
        {
            public FileItem transform(Object bean) throws Exception
            {
                DeptFile deptFile = (DeptFile) bean;

                FileItem fileItem = new FileItem();

                fileItem.setId(deptFile.getFileId().toString());

                String fileName = deptFile.getFileName();
                if (!StringUtils.isEmpty(deptFile.getFileType()))
                    fileName += "." + deptFile.getFileType();
                fileItem.setTitle(fileName);

                fileItem.setTime(deptFile.getUpdateTime());
                fileItem.setSource(deptFile.getSourceName());
                fileItem.setRemark(deptFile.getRemark());

                return fileItem;
            }
        });

        fileQuery.loadList0();
    }
}
