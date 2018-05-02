package com.gzzm.safecampus.s3;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.face.FaceService;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.io.IOException;

/**
 * 对接s3服务接口
 *
 * @author zy
 * @date 2018/4/19 14:59
 */
@Service
public class S3ServicePage
{
    @Inject
    private S3Service service;

    @Inject
    private FaceService faceService;

    private String name;

    private String currentDir;

    public S3ServicePage()
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

    public String getCurrentDir()
    {
        return currentDir;
    }

    public void setCurrentDir(String currentDir)
    {
        this.currentDir = currentDir;
    }

    /**
     * 接收人脸图片
     * @return
     * @throws IOException
     */
    @Service(url = "/safecampus/s3/uploadfaceimage", method = HttpMethod.post)
    @PlainText
    public String uploadFaceImage() throws IOException
    {
        if(currentDir.startsWith("/"))
            currentDir=currentDir.substring(1,currentDir.length());
        Tools.log("currentDir:" + currentDir + " filename:" + name);
        try
        {
            byte[] image = service.readFile(currentDir + name);
            faceService.schoolAttendance(image,name,currentDir + name);
        }
        catch (Exception e)
        {
            Tools.log(e);
        }
        return "1";
    }
}
