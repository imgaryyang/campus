package com.gzzm.platform.commons.archive;

import com.gzzm.platform.commons.Tools;
import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.file.*;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.*;
import net.cyan.image.ImageZoomer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;

/**
 * 压缩文件预览
 *
 * @author camel
 * @date 2017/6/13
 */
@Service
public class ArchivePage
{
    private String file;

    private String path = "";

    private String name;

    private String path1;

    private InputStream in;

    private String extName;

    private boolean show;

    public ArchivePage()
    {
    }

    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isShow()
    {
        return show;
    }

    public void setShow(boolean show)
    {
        this.show = show;
    }

    private void init() throws Exception
    {
        CommonFileService fileService = Tools.getCommonFileService("temp");
        CommonFile file = fileService.getFile(this.file);
        in = file.getInputable().getInputStream();
        extName = IOUtils.getExtName(this.file);

        String s0 = path;
        if (s0.endsWith("!"))
            s0 += "/";
        String[] ss = s0.split("!");
        int n = ss.length - 1;
        for (int i = 0; i < n; i++)
        {
            String s = ss[i];
            if (s.startsWith("/"))
                s = s.substring(1);
            CacheData cacheData = new CacheData();
            CompressUtils.getEngine(extName).extract(in, s, cacheData);

            in = cacheData.getInputStream();
            extName = IOUtils.getExtName(s);
        }

        path1 = ss[n];
        if (path1.startsWith("/"))
            path1 = path1.substring(1);
    }

    @Service(url = "/archive")
    public List<ArchiveItem> list() throws Exception
    {
        init();

        List<CompressUtils.CompressEntry> entries = CompressUtils.getEngine(extName).list(in, path1);

        int size = entries.size();
        List<ArchiveItem> items = new ArrayList<ArchiveItem>(size);
        for (CompressUtils.CompressEntry entry : entries)
        {
            ArchiveItem item = new ArchiveItem();
            String name = entry.getName();

            int index = name.lastIndexOf('/');
            if (index > 0)
                name = name.substring(index + 1);
            item.setName(name);
            item.setDirectory(entry.isDirectory());
            item.setSize(IOUtils.getSizeString(entry.getSize()));
            items.add(item);
        }

        return items;
    }

    @Service(url = "/archive")
    public String show() throws Exception
    {
        return "list";
    }

    @Service(url = "/archive/thumb")
    public InputFile thumb(int width, int height) throws Exception
    {
        init();

        String name = path1;
        int index = name.lastIndexOf('/');
        if (index > 0)
            name = name.substring(index + 1);

        CacheData cacheData = new CacheData();
        CompressUtils.getEngine(extName).extract(in, path1, cacheData);

        BufferedImage image = ImageIO.read(cacheData.getInputStream());
        if (image.getWidth() > width || image.getHeight() > height)
        {
            ImageZoomer zoomer = new ImageZoomer(width, height, true, IOUtils.getExtName(name));
            zoomer.setImage(image);

            byte[] bytes = zoomer.toBytes();
            return new DownloadFile(bytes, name, false);
        }
        else
        {
            return new DownloadFile(cacheData.getInputStream(), name, false);
        }
    }

    @Service(url = "/archive/down")
    public InputFile down() throws Exception
    {
        init();

        String name = path1;
        int index = name.lastIndexOf('/');
        if (index > 0)
            name = name.substring(index + 1);

        CacheData cacheData = new CacheData();
        CompressUtils.getEngine(extName).extract(in, path1, cacheData);

        return new DownloadFile(cacheData.getInputStream(), name, !show);
    }

    @Service(url = "/archive/img")
    public InputFile img() throws Exception
    {
        show = true;
        return down();
    }

    @Service(url = "/archive/html")
    @Redirect
    public String html() throws Exception
    {
        init();

        String name = path1;
        int index = name.lastIndexOf('/');
        if (index > 0)
            name = name.substring(index + 1);

        CacheData cacheData = new CacheData();
        CompressUtils.getEngine(extName).extract(in, path1, cacheData);

        String ext = IOUtils.getExtName(name);
        if (!StringUtils.isEmpty(ext))
        {
            if (OfficeUtils.canChangeToHtml(ext))
            {
                return OfficeUtils.toHtml(cacheData, ext);
            }
        }

        return null;
    }
}
