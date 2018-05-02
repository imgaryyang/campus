package com.gzzm.platform.commons.archive;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.file.CommonFileService;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.io.WebUtils;

/**
 * @author camel
 * @date 2017/6/13
 */
public class ArchiveUtils
{
    public ArchiveUtils()
    {
    }

    public static String archive(InputFile file) throws Exception
    {
        CommonFileService fileService = Tools.getCommonFileService("temp");

        String path = fileService.createTempFile("temp/archive", "." + file.getExtName());
        fileService.upload(path, file.getInputable());

        return "/archive?file=" + path + "&name=" + WebUtils.encode(file.getName());
    }

    public static boolean isArchive(String extName) throws Exception
    {
        return extName!=null&&("zip".equalsIgnoreCase(extName) || "rar".equalsIgnoreCase(extName));
    }
}
