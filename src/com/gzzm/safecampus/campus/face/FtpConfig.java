package com.gzzm.safecampus.campus.face;

import net.cyan.nest.annotation.Injectable;

/**
 * ftp配置
 * @author zy
 * @date 2018/3/29 11:20
 */
@Injectable(singleton = true)
public class FtpConfig
{
    /**
     * ftp端口
     */
    private Integer port;

    /**
     * ftp ip地址
     */
    private String ftpIp;

    /**
     * ftp用户名
     */
    private String ftpUserName;

    /**
     * ftp密码
     */
    private String ftpPassword;

    /**
     * ftp下载路径
     */
    private String ftpDownPath;

    /**
     * ftp文件备份目录
     */
    private String ftpBakPath;

    /**
     * 请求方式
     */
    private String ftpMode;

    public FtpConfig()
    {
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public String getFtpIp()
    {
        return ftpIp;
    }

    public void setFtpIp(String ftpIp)
    {
        this.ftpIp = ftpIp;
    }

    public String getFtpUserName()
    {
        return ftpUserName;
    }

    public void setFtpUserName(String ftpUserName)
    {
        this.ftpUserName = ftpUserName;
    }

    public String getFtpPassword()
    {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword)
    {
        this.ftpPassword = ftpPassword;
    }

    public String getFtpDownPath()
    {
        return ftpDownPath;
    }

    public void setFtpDownPath(String ftpDownPath)
    {
        this.ftpDownPath = ftpDownPath;
    }

    public String getFtpBakPath()
    {
        return ftpBakPath;
    }

    public void setFtpBakPath(String ftpBakPath)
    {
        this.ftpBakPath = ftpBakPath;
    }

    public String getFtpMode()
    {
        return ftpMode;
    }

    public void setFtpMode(String ftpMode)
    {
        this.ftpMode = ftpMode;
    }
}
