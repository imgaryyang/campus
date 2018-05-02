package com.gzzm.platform.commons;

import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.image.*;
import net.cyan.nest.annotation.Inject;

/**
 * 接收多文件上传
 *
 * @author camel
 * @date 2010-8-5
 */
@Service
public class FileUploadPage
{
    /**
     * 上传的文件
     */
    private InputFile file;

    @Inject
    private FileUploadService uploadService;

    public FileUploadPage()
    {
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    @Service(url = "/richupload/upload", method = HttpMethod.post)
    @ObjectResult
    public String upload() throws Exception
    {
        return uploadService.uploadFile(file);
    }

    @Service(url = "/richupload/base64", method = HttpMethod.post)
    @ObjectResult
    public String uploadBase64(String base64, String fileName) throws Exception
    {
        base64 = base64.replaceAll("\\s", "");

        byte[] bs = null;
        for (int i = 0; i < 20; i++)
        {
            try
            {
                String s1 = base64;
                while (s1.length() % 4 != 0)
                    s1 += "=";

                bs = CommonUtils.base64ToByteArray(s1);
            }
            catch (Exception ex)
            {
                base64 = base64.substring(0, base64.length() - 1);
            }
        }

        if (bs == null)
            return null;

        return uploadService.uploadFile(new InputFile(bs, fileName));
    }

    @Service(url = "/richupload?path={$0}")
    public InputFile down(String path) throws Exception
    {
        return uploadService.getFile(path);
    }

    @Service(url = "/richupload/thumb?path={$0}")
    public InputFile thumb(String path) throws Exception
    {
        String ext = IOUtils.getExtName(path);
        if (IOUtils.isImage(ext))
        {
            ImageZoomer zoomer = new ImageZoomer(200, 120, true, ext);
            InputFile file = uploadService.getFile(path);

            zoomer.read(file.getInputStream());

            int pos = Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\"));
            if (pos >= 0)
                path = path.substring(pos + 1);

            return new InputFile(zoomer.toBytes(ext), path);
        }
        else
        {
            return uploadService.getFile(path);
        }
    }

    @Service(url = "/richupload/rotate/{$1}?path={$0}")
    @ObjectResult
    public void rotate(String path, int type) throws Exception
    {
        String ext = IOUtils.getExtName(path);
        if (IOUtils.isImage(ext))
        {
            ImageRotator rotator = new ImageRotator(type);

            InputFile file = uploadService.getFile(path);
            rotator.read(file.getInputStream());

            uploadService.uploadFile(path, new Inputable.ByteInput(rotator.toBytes(ext)));
        }
    }
}
