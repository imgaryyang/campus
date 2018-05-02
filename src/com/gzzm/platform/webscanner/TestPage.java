package com.gzzm.platform.webscanner;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;

/**
 * @author camel
 * @date 2010-6-21
 */
@Service
public class TestPage
{
    private static String content;

    private InputFile file;

    public TestPage()
    {
    }

    @Service(url = "/webscaner/test")
    public String index()
    {
        return "test";
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public void save(String content)
    {
        TestPage.content = content;
        Tools.log(content);
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public void upload() throws Exception
    {
        TestPage.content = CommonUtils.byteArrayToBase64(file.getBytes());
    }

    @Service(url = "/webscaner/test/img")
    public InputFile getImage()
    {
        String content = TestPage.content;

        byte[] bs = null;
        for (int i = 0; i < 20; i++)
        {
            try
            {
                bs = CommonUtils.base64ToByteArray(content);
                break;
            }
            catch (Throwable ex)
            {
                content = content.substring(0, content.length() - 1);
            }
        }

        return new InputFile(bs, "aa.jpg");
    }
}
