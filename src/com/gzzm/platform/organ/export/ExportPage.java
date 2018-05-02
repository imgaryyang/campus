package com.gzzm.platform.organ.export;

import com.gzzm.platform.menu.Menu;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.json.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.nest.annotation.Inject;

import java.io.InputStreamReader;
import java.util.List;

/**
 * @author camel
 * @date 14-1-15
 */
@Service
public class ExportPage
{
    @Inject
    private ExportService service;

    @Require
    private String[] groupIds;

    @Require
    private InputFile file;

    public ExportPage()
    {
    }

    public String[] getGroupIds()
    {
        return groupIds;
    }

    public void setGroupIds(String[] groupIds)
    {
        this.groupIds = groupIds;
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    @NotSerialized
    public List<Menu> getTopMenus() throws Exception
    {
        return service.getTopMenus();
    }

    @Service(url = "/export/exp/show")
    public String showExp()
    {
        return "/platform/organ/export.ptl";
    }

    @Service(url = "/export/imp/show")
    public String showImp()
    {
        return "/platform/organ/import.ptl";
    }

    @Service(url = "/export/exp/down")
    public InputFile exp() throws Exception
    {
        SystemExport export = service.export(groupIds);

        JsonSerializer serializer = new JsonSerializer();
        serializer.serialize(export);

        return new InputFile(serializer.toString().getBytes("UTF-8"), "系统基础数据.exp");
    }

    @Service(url = "/export/imp", method = HttpMethod.post)
    public void imp() throws Exception
    {
        JsonParser parser = new JsonParser(new InputStreamReader(file.getInputStream(), "UTF-8"));

        SystemExport export = parser.parse(SystemExport.class);

        service.imp(export);
    }
}
