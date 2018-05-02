package com.gzzm.safecampus.s3;

import com.gzzm.platform.commons.Tools;
import net.cyan.nest.annotation.Injectable;

/**
 * s3配置信息
 * @author zy
 * @date 2018/4/23 11:23
 */
@Injectable(singleton = true)
public class S3Config
{
    /**
     * S3所属区域
     */
    private String region;

    /**
     * 所属桶名字
     */
    private String bucketName;

    /**
     * s3配置文件路径
     */
    private String s3FilePath;

    /**
     * 图片根路径
     */
    private String imageRootPath;

    public S3Config()
    {
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getBucketName()
    {
        return bucketName;
    }

    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }

    public String getS3FilePath()
    {
        return s3FilePath;
    }

    public String getS3FileAppPath()
    {
        return Tools.getAppPath(s3FilePath);
    }

    public void setS3FilePath(String s3FilePath)
    {
        this.s3FilePath = s3FilePath;
    }

    public String getImageRootPath()
    {
        return imageRootPath;
    }

    public void setImageRootPath(String imageRootPath)
    {
        this.imageRootPath = imageRootPath;
    }
}
