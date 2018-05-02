package com.gzzm.safecampus.campus.face;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.ftpclient.*;
import net.cyan.commons.test.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.*;

/**
 * ftp操作服务
 * @author zy
 * @date 2018/3/28 14:52
 */
public class FtpService
{
    @Inject
    private FtpConfig ftpConfig;

    public FtpService()
    {
    }

    public FtpConfig getFtpConfig()
    {
        return ftpConfig;
    }

    /**
     * ftp 连接
     *
     * @param path ftp路径
     * @throws Exception
     */
    public FtpClient connect(String path, String userName, String password,int port) throws Exception
    {
        return FtpClientPool.getFtpClient(path, port, userName, password);
    }

    /**
     * 登录
     *
     * @param userName 用户名
     * @param password 密码
     * @throws IOException
     */
    public void login(FtpClient ftpClient, String userName, String password) throws IOException
    {
        ftpClient.login(userName, password);
        ftpClient.binary();
    }

    public FtpClient getFtpClient() throws Exception
    {
        FtpClient ftpClient = connect(ftpConfig.getFtpIp(), ftpConfig.getFtpUserName(), ftpConfig.getFtpPassword(),ftpConfig.getPort());
        if(StringUtils.isNotBlank(ftpConfig.getFtpMode()))
            ftpClient.setMode(FtpMode.valueOf(ftpConfig.getFtpMode()));
        if (ftpClient == null)
        {
            Tools.log("-------------------------------------------发现ftp连接失败！");
            ftpClient = connect(ftpConfig.getFtpIp(), ftpConfig.getFtpUserName(), ftpConfig.getFtpPassword(),ftpConfig.getPort());
        }
        login(ftpClient, ftpConfig.getFtpUserName(), ftpConfig.getFtpPassword());
        return ftpClient;
    }

    /**
     * 上传字符串
     *
     * @param content 上传内容
     * @param addr    地址
     * @throws Exception
     */
    public void upload(String content, String addr) throws Exception
    {
        OutputStream out;
        OutputStream fileout;
        File file = new File(ftpConfig.getFtpDownPath() + addr);
        Tools.log(file.getAbsolutePath());
        fileout = new FileOutputStream(file);
        if (!file.exists())
        {
            file.createNewFile();
        }
        fileout.write(content.getBytes());
        fileout.flush();

        FtpClient ftpClient = getFtpClient();
        Tools.log("上传开始");
        out = ftpClient.store(addr);
        out.write(content.getBytes());
        out.flush();
        Tools.log("上传结束");
        try
        {
            ftpClient.close();
            IOUtils.close(out);
            IOUtils.close(fileout);
        }
        catch (Throwable t)
        {
            Tools.log("流关闭失败！");
            Tools.log(t);
        }
    }

    public void upload(InputStream inputStream, String addr) throws Exception
    {
        OutputStream out;
        ByteArrayOutputStream swapStream;
        Tools.log("上传开始");
        Tools.log("上传路径：" + addr);
        FtpClient ftpClient = getFtpClient();
        out = ftpClient.store(addr);
        byte[] buffer = new byte[1024];
        int n;
        swapStream = new ByteArrayOutputStream();
        while ((n = inputStream.read(buffer)) > 0)
        {
            swapStream.write(buffer, 0, n);
        }
        out.write(swapStream.toByteArray());
        out.flush();
        Tools.log("上传结束");
        try
        {
            close(ftpClient);
            IOUtils.close(swapStream);
            IOUtils.close(out);
        }
        catch (Exception e)
        {
            Tools.log(e);
        }
    }

    /**
     * @param fielpath 下载文件
     * @param target   目标路径
     * @throws IOException
     */
    public void download(String fielpath, String target) throws Exception
    {
        InputStream in;
        FtpClient ftpClient = getFtpClient();
        Tools.log("下载开始");
        in = ftpClient.retrive(fielpath);
        IOUtils.streamToFile(in, target);
        Tools.log("下载结束");
        Tools.log("ftp下载文件失败！" + fielpath);
        try
        {
            close(ftpClient);
            IOUtils.close(in);
        }
        catch (Exception e)
        {
            Tools.log(e);
        }
    }


    /**
     * @param addr   下载目录中所有文件
     * @param target 目标路径
     * @throws IOException
     */
    public void download_DirFiles(String addr, String target) throws Exception
    {
        FtpClient ftpClient = getFtpClient();
        Tools.log("登录ftp,下载文件");
        String[] filepaths = null;
        try
        {
            filepaths = ftpClient.listNames(addr);
        }
        catch (IOException e)
        {
            Tools.log("列出目录文件出错");
            Tools.log(e);
        }
        if (filepaths != null && filepaths.length > 0)
        {
            for (String path : filepaths)
            {
                Tools.log(path);
                File file = new File(target + File.separator+ path);
                if (!file.exists())
                {
                    download(path, target + File.separator + path);
                }
            }
        }
        else
        {
            Tools.log("没有找到校验文件");
        }
        try
        {
            close(ftpClient);
        }
        catch (Exception e)
        {
            Tools.log(e);
        }
    }

    public void close(FtpClient ftpClient)
    {
        try
        {
            ftpClient.close();
        }
        catch (IOException e)
        {
            Tools.log(e);
        }
    }

    @TestCase
    public void ftpTest() throws Exception
    {
        //  connect("192.168.1.2");
        // login("ftp", "123456");
/*        upload(
                "我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试我在测试",
                "/up/text.txt");*/
        download("/check/Call_rec_info.20160224.20160224.D.00.YF.05.xml",
                "D:\\newworkspace\\hotline\\web\\hotline\\ftp_files\\Call_rec_info.20160224.20160224.D.00.YF.05.xml");
    }

    public static void main(String[] args) throws Exception
    {
        TestRunner.run();
    }
}
