package com.gzzm.platform.html;

import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.InputFile;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/8/24
 */
@Service
public class HtmlPage
{
    @Inject
    private HtmlService service;

    private InputFile file;

    public HtmlPage()
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

    @Service(url = "/toHTML", method = HttpMethod.post)
    @ObjectResult
    public String[] toHTML() throws Exception
    {
        return service.toHTML(file);
    }
}
