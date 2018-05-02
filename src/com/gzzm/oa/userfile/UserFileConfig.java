package com.gzzm.oa.userfile;

import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.Entity;

/**
 * @author : wmy
 * @date : 2010-3-10
 */
@Entity(table = "OAUSERFILECONFIG", keys = "userId")
public class UserFileConfig
{
    /**
     * 关联USER表
     */
    private Integer userId;

    /**
     * 个人资料总容量，以K为单位
     * 修改了限制最大值和最小值，以限制只能输入整数，ccs
     */
    @MaxVal("1000000000")
    @MinVal("1")
    private Integer librarySize;

    /**
     * 本用户已使用的容量
     */
    private Long used;

    /**
     * 允许单个文件上传的大小，以K为单位，最大10M=10×1024K，(字段长度冗余一些)
     * 修改了限制最大值和最小值，以限制只能输入整数，ccs
     */
    @MaxVal("1000000000")
    @MinVal("1")
    private Integer uploadSize;

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getLibrarySize()
    {
        return librarySize;
    }

    public void setLibrarySize(Integer librarySize)
    {
        this.librarySize = librarySize;
    }

    public Long getUsed()
    {
        return used;
    }

    public void setUsed(Long used)
    {
        this.used = used;
    }

    public Integer getUploadSize()
    {
        return uploadSize;
    }

    public void setUploadSize(Integer uploadSize)
    {
        this.uploadSize = uploadSize;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof UserFileConfig))
            return false;

        UserFileConfig fileConfig = (UserFileConfig) o;

        return userId.equals(fileConfig.userId);

    }

    @Override
    public int hashCode()
    {
        return userId.hashCode();
    }

    public UserFileConfig copy()
    {
        UserFileConfig c = new UserFileConfig();
        c.setLibrarySize(librarySize);
        c.setUploadSize(uploadSize);

        return c;
    }
}
