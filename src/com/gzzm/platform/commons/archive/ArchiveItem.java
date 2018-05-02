package com.gzzm.platform.commons.archive;

/**
 * @author camel
 * @date 2017/8/11
 */
public class ArchiveItem
{
    private String name;

    private String size;

    private boolean directory;

    public ArchiveItem()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSize()
    {
        return size;
    }

    public void setSize(String size)
    {
        this.size = size;
    }

    public boolean isDirectory()
    {
        return directory;
    }

    public void setDirectory(boolean directory)
    {
        this.directory = directory;
    }
}
