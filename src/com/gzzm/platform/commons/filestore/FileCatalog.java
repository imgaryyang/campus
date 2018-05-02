package com.gzzm.platform.commons.filestore;

/**
 * 文件目录
 *
 * @author camel
 * @date 13-10-17
 */
public class FileCatalog
{
    private String id;

    private String name;

    private boolean hasChildCatalogs;

    public FileCatalog(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public FileCatalog(String id, String name, boolean hasChildCatalogs)
    {
        this.id = id;
        this.name = name;
        this.hasChildCatalogs = hasChildCatalogs;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public boolean hasChildCatalogs()
    {
        return hasChildCatalogs;
    }
}
