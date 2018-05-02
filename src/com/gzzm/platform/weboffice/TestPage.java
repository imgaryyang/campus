package com.gzzm.platform.weboffice;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.File;

/**
 * @author camel
 * @date 2010-6-21
 */
@Service
public class TestPage
{
    @Inject
    private UserOnlineInfo userOnlineInfo;


    private Inputable content;

    private boolean newFile;

    public TestPage()
    {
    }

    public Inputable getContent()
    {
        return content;
    }

    public void setContent(Inputable content)
    {
        this.content = content;
    }

    public boolean isNewFile()
    {
        return newFile;
    }

    public void setNewFile(boolean newFile)
    {
        this.newFile = newFile;
    }

    public String getUserName()
    {
        return userOnlineInfo.getUserName();
    }

    @Service(url = "/weboffice/test")
    public String index()
    {
        return "test";
    }

    @Service(url = "/weboffice/test", method = HttpMethod.post)
    @ObjectResult
    public String save() throws Exception
    {
        content.saveAs(Tools.getAppPath("/platform/weboffice/test.doc"));

        return "ok";
    }

    @Service(url = "/weboffice/test/load")
    public InputFile load() throws Exception
    {
        return new InputFile(new File(Tools.getAppPath("/platform/weboffice/test.doc")));
    }
}
