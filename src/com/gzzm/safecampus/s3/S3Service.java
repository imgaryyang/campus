package com.gzzm.safecampus.s3;

import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import com.gzzm.platform.commons.*;
import net.cyan.commons.util.IOUtils;
import net.cyan.nest.annotation.Inject;

import java.io.*;

/**
 * s3桶业务处理
 * @author zy
 * @date 2018/4/23 13:58
 */
public class S3Service
{
    @Inject
    private S3Config config;

    public S3Service()
    {
    }

    /**
     * 连接s3桶
     * @return s3桶
     */
    private AmazonS3 getAmazonS3()
    {
        AWSCredentials credentials = null;
        try
        {
            ProfileCredentialsProvider profileCredentialsProvider =
                    new ProfileCredentialsProvider(config.getS3FileAppPath(),
                            "default");
            credentials = profileCredentialsProvider.getCredentials();
        }
        catch (Exception e)
        {
            Tools.log(config.getS3FileAppPath());
            throw new SystemMessageException("s3桶无法连接","连接异常");
        }
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(config.getRegion())
                .build();
    }

    /**
     * 获取图片的key值
     * @param filePath 文件路径
     * @return 图片存放路径
     */
    public String getPhotoKey(String filePath)
    {
        if(!filePath.startsWith("/"))
        {
            filePath="/"+filePath;
        }
        return config.getImageRootPath()+filePath;
    }

    /**
     * 添加文件进s3桶
     * @param key 文件主键
     * @param image 图片
     * @throws IOException 添加异常
     */
    public void addFile(String key,byte[] image) throws IOException
    {
        File file=IOUtils.createTempFile(image);
        AmazonS3 s3 = getAmazonS3();
        s3.putObject(new PutObjectRequest(config.getBucketName(), key, file));
    }

    /**
     * 读取s3文件内容
     * @param key 主键
     * @return 读取内容
     * @throws IOException 读取异常
     */
    public byte[] readFile(String key) throws Exception
    {
        AmazonS3 s3 = getAmazonS3();
        S3Object object = s3.getObject(new GetObjectRequest(config.getBucketName(), key));
        return IOUtils.streamToBytes(object.getObjectContent());
    }
}
