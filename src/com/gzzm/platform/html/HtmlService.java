package com.gzzm.platform.html;

import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.Tools;
import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.RequestContext;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.util.*;

/**
 * @author camel
 * @date 2016/8/24
 */
public class HtmlService
{
    private HtmlUtils.URLMapper urlMapper;

    @Inject
    private AttachmentService attachmentService;

    public HtmlService()
    {
    }

    private String fetchImg(String html, HtmlUtils.URLMapper urlMapper)
    {
        try
        {
            html = HtmlUtils.changeImgURL(html, urlMapper);
        }
        catch (Throwable ex)
        {
            //出现错误返回原来的html
            Tools.log(ex);
        }

        return html;
    }

    private String fetchImg(File file, HtmlUtils.URLMapper urlMapper) throws IOException
    {
        String html = new String(IOUtils.fileToBytes(file), "GBK");

        int index1 = html.indexOf("<body");
        index1 = html.indexOf(">", index1);

        int index2 = html.lastIndexOf("</body>");

        html = html.substring(index1 + 1, index2);

        return fetchImg(html, urlMapper);
    }

    private HtmlUtils.URLMapper getUrlMapper()
    {
        if (urlMapper == null)
        {
            urlMapper = new HtmlUtils.URLMapper()
            {
                @Override
                public String map(String url) throws Exception
                {
                    if (!url.startsWith("data:"))
                    {
                        int index = url.indexOf(':');
                        if (index > 0)
                        {
                            index = url.lastIndexOf('/');
                            if (index > 0)
                            {
                                String fileName = url.substring(index + 1);
                                //是一个网络url
                                try
                                {
                                    InputStream stream = IOUtils.getStreamByUrl(url);

                                    Attachment attachment = new Attachment();
                                    attachment.setAttachmentName(fileName);
                                    attachment.setInputable(new Inputable.StreamInput(stream));
                                    attachment.setFileType(FileType.image);
                                    attachment.setUuid(CommonUtils.uuid());

                                    attachmentService
                                            .save(Collections.singleton(attachment), "file", null, null);

                                    return "/attachment/" + attachment.getUuid() + "/1/" + fileName;
                                }
                                catch (Throwable ex)
                                {
                                    //从网络抓取图片错误，忽略
                                    Tools.log("fetch img failed,url;" + url, ex);
                                }
                            }
                        }
                    }

                    return null;
                }
            };
        }

        return urlMapper;
    }

    private HtmlUtils.URLMapper getUrlMapper(final String dir)
    {
        return new HtmlUtils.URLMapper()
        {
            @Override
            public String map(String url) throws Exception
            {
                if (url.startsWith("./") || url.startsWith(".\\"))
                    url = url.substring(2);
                String path = dir + "/" + url;
                File file = new File(path);
                if (file.exists())
                {
                    try
                    {
                        Attachment attachment = new Attachment();
                        attachment.setAttachmentName(file.getName());
                        attachment.setInputable(new Inputable.FileInput(file));
                        attachment.setFileType(FileType.image);
                        attachment.setUuid(CommonUtils.uuid());

                        attachmentService
                                .save(Collections.singleton(attachment), "file", null, null);

                        return "/attachment/" + attachment.getUuid() + "/1/" + file.getName();
                    }
                    catch (Throwable ex)
                    {
                        //从网络抓取图片错误，忽略
                        Tools.log("fetch img failed,url;" + path, ex);
                    }
                }

                return null;
            }
        };
    }

    /**
     * 将html中的网络图片抓取到本地
     *
     * @param html html
     * @return 抓取后替换了图片url的html
     */
    public String fetchImg(String html)
    {
        return fetchImg(html, getUrlMapper());
    }

    public String[] toHTML(InputFile inputFile) throws Exception
    {
        String ext = inputFile.getExtName();
        if (ext != null)
        {
            if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext) || "wps".equalsIgnoreCase(ext))
                return new String[]{wordToHTML(inputFile)};
            else if ("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))
                return excelToHTML(inputFile);
        }

        return null;
    }

    public String wordToHTML(InputFile inputFile) throws Exception
    {
        String path = OfficeUtils.wordToHtml(inputFile.getInputable(), inputFile.getExtName());

        File file = new File(RequestContext.getContext().getRealPath(path));
        String dir = file.getParentFile().getAbsolutePath();

        return fetchImg(file, getUrlMapper(dir));
    }

    public String[] excelToHTML(InputFile inputFile) throws Exception
    {
        String path = OfficeUtils.excelToHtml(inputFile.getInputable(), inputFile.getExtName());

        File file = new File(RequestContext.getContext().getRealPath(path));

        String name = file.getName();
        File dirFile = new File(
                file.getParentFile().getAbsolutePath() + "/" + name.substring(0, name.lastIndexOf('.')) + ".files");

        if (!dirFile.isDirectory())
            return null;

        String dir = dirFile.getAbsolutePath();
        Map<Integer, String> htmlMap = null;
        HtmlUtils.URLMapper urlMapper = null;

        for (File file1 : dirFile.listFiles())
        {
            name = file1.getName();
            if (StringUtils.startsWithIgnoreCase(name, "Sheet"))
            {
                name = name.substring(5, name.indexOf('.'));

                try
                {
                    Integer index = Integer.valueOf(name);

                    if (htmlMap == null)
                        htmlMap = new TreeMap<Integer, String>();

                    if (urlMapper == null)
                        urlMapper = getUrlMapper(dir);

                    htmlMap.put(index, fetchImg(file1, urlMapper));
                }
                catch (NumberFormatException ex)
                {
                    //不是合法的整数，跳过
                }
            }
        }

        if (htmlMap == null)
            return null;

        return htmlMap.values().toArray(new String[htmlMap.size()]);
    }
}
