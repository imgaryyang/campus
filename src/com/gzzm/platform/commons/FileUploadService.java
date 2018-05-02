package com.gzzm.platform.commons;

import net.cyan.commons.file.*;
import net.cyan.commons.util.*;

import java.io.File;

/**
 * @author camel
 * @date 14-2-11
 */
public class FileUploadService
{
    private boolean serviceLoaded;

    private CommonFileService commonFileService;

    public FileUploadService()
    {
    }

    public CommonFileService getCommonFileService() throws Exception
    {
        if (!serviceLoaded)
        {
            commonFileService = Tools.getCommonFileService("temp");
            serviceLoaded = true;
        }

        return commonFileService;
    }

    public String uploadFile(InputFile file) throws Exception
    {
        CommonFileService service = getCommonFileService();

        if (service != null)
        {
            String path = service.createTempFile("temp", file.getName());
            service.upload(path, file.getInputable());

            return path;
        }
        else
        {
            File tempFile = new File(IOUtils.createTempDirectory(), file.getName());
            file.saveAs(tempFile.getAbsolutePath());

            return tempFile.getAbsolutePath();
        }
    }

    public void uploadFile(String path, Inputable in) throws Exception
    {
        CommonFileService service = getCommonFileService();

        if (service != null)
        {
            service.upload(path, in);
        }
        else
        {
            in.saveAs(path);
        }
    }

    public InputFile getFile(String path) throws Exception
    {
        CommonFileService service = getCommonFileService();

        if (service != null)
        {
            CommonFile file = service.getFile(path);

            if (file == null || !file.exists())
                return null;
            else
                return file.getInputFile();
        }
        else
        {
            return new InputFile(new File(path));
        }
    }

    public void deleteFile(String path) throws Exception
    {
        CommonFileService service = getCommonFileService();

        if (service != null)
        {
            CommonFile file = service.getFile(path);
            if (file != null)
                file.delete();
        }
        else
        {
            if (new File(path).delete())
            {
                //删除失败跳过
            }
        }
    }
}
