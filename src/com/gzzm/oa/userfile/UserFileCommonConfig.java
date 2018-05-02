package com.gzzm.oa.userfile;

import com.gzzm.platform.annotation.ConfigValue;

/**
 * 资料管理的全局配置，将原来分别写在几个地方的全局配置加载移到这里，减少代码的重复
 *
 * @author camel
 * @date 2010-4-10
 */
public class UserFileCommonConfig
{
    /**
     * 默认的总容量大小，以K为单位，在PFCONFIG表中配置
     */
    @ConfigValue(name = "OAUSERFILE_LIBRARYSIZE", defaultValue = "102400")
    private Integer librarySize;

    /**
     * 默认的允许上传单个文件的大小，以K为单位，在PFCONFIG表中配置
     */
    @ConfigValue(name = "OAUSERFILE_UPLOADSIZE", defaultValue = "10240")
    private Integer uploadSize;


    public UserFileCommonConfig()
    {
    }

    public Integer getLibrarySize()
    {
        return librarySize;
    }

    public void setLibrarySize(Integer librarySize)
    {
        this.librarySize = librarySize;
    }

    public Integer getUploadSize()
    {
        return uploadSize;
    }

    public void setUploadSize(Integer uploadSize)
    {
        this.uploadSize = uploadSize;
    }
}
