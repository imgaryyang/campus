package com.gzzm.oa.address;

/**
 * 导入文件的缓存信息，包括临时文件路径，文件扩展名和文件头信息
 * @author camel
 * @date 2010-4-30
 */
public class ImpFileCache
{
    private String path;

    private String ext;

    private String[] headers;

    public ImpFileCache()
    {
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getExt()
    {
        return ext;
    }

    public void setExt(String ext)
    {
        this.ext = ext;
    }

    public String[] getHeaders()
    {
        return headers;
    }

    public void setHeaders(String[] headers)
    {
        this.headers = headers;
    }
}
