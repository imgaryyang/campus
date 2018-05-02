package com.gzzm.oa.version;

import net.cyan.nest.annotation.Injectable;

/**
 * 版本更新发布配置信息
 * @author zy
 * @date 2017/1/22 14:27
 */
@Injectable(singleton = true)
public class VersionConfig
{
    private String filePath;

    private String fileLastName;

    public VersionConfig()
    {
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFileLastName()
    {
        return fileLastName;
    }

    public void setFileLastName(String fileLastName)
    {
        this.fileLastName = fileLastName;
    }
}
