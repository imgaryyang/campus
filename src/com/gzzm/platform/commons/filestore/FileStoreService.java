package com.gzzm.platform.commons.filestore;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 文件存储服务
 *
 * @author camel
 * @date 2011-4-6
 */
public class FileStoreService
{
    @Inject
    private List<FileStorer> storers;

    public FileStoreService()
    {
    }

    public List<FileStorer> getStorers()
    {
        return storers;
    }

    public FileStorer getStorer(String id)
    {
        for (FileStorer storer : storers)
        {
            if (storer.getId().equals(id))
                return storer;
        }

        return null;
    }

    public void save(InputFile file, Integer userId, Integer deptId, String target, String source, String remark)
            throws Exception
    {
        int index = target.indexOf(":");

        String type;
        String catalogId;
        if (index > 0)
        {
            type = target.substring(0, index);
            catalogId = target.substring(index + 1);
        }
        else
        {
            type = target;
            catalogId = null;
        }

        FileStorer storer = getStorer(type);
        if (storer != null)
            storer.save(file, userId, deptId, catalogId, source, remark);
    }

    public List<CatalogItem> getChildCatalogs(String type, String id, Integer userId, Integer deptId, boolean readOnly)
            throws Exception
    {
        List<CatalogItem> children;

        if ("root".equals(type))
        {
            List<FileStorer> storers = getStorers();
            children = new ArrayList<CatalogItem>(storers.size());

            for (FileStorer storer : storers)
            {
                if (storer.isValid(userId, deptId, readOnly))
                {
                    String name = storer.getName();
                    if (StringUtils.isEmpty(name))
                        name = Tools.getMessage(storer.getClass().getName());

                    children.add(new CatalogItem(storer.getId(), null, name, false));
                }
            }
        }
        else
        {
            List<FileCatalog> catalogs = getStorer(type).getCatalogs(id, userId, deptId, !readOnly);

            if (catalogs != null && catalogs.size() > 0)
            {
                children = new ArrayList<CatalogItem>(catalogs.size());
                for (FileCatalog catalog : catalogs)
                {
                    children.add(new CatalogItem(type, catalog.getId(), catalog.getName(),
                            !catalog.hasChildCatalogs()));
                }
            }
            else
            {
                children = Collections.emptyList();
            }
        }

        return children;
    }

    public List<InputFile> getFiles(String[] fileIds, Integer userId, Integer deptId) throws Exception
    {
        List<InputFile> result = null;

        for (String fileId : fileIds)
        {
            int index = fileId.indexOf(':');

            String type = fileId.substring(0, index);
            fileId = fileId.substring(index + 1);

            FileStorer storer = getStorer(type);

            if (storer != null)
            {
                List<InputFile> files = storer.get(fileId, userId, deptId);

                if (result == null)
                {
                    if (fileIds.length == 1)
                        return files;

                    result = new ArrayList<InputFile>(files);
                }
                else
                {
                    result.addAll(files);
                }
            }
        }

        return result;
    }
}
