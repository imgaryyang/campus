package com.gzzm.safecampus.campus.face;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.ftpclient.FtpClient;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.io.*;

/**
 * 人脸识别图片扫描
 * @author zy
 * @date 2018/3/29 11:37
 */
public class FtpFaceImageJob implements Runnable
{
    @Inject
    private Provider<FtpService> ftpServiceProvider;

    @Inject
    private Provider<FaceService> faceServiceProvider;

    public FtpFaceImageJob()
    {
    }

    @Override
    public void run()
    {
        if(!faceServiceProvider.get().testServer())
        {
            Tools.log("人脸服务器未启动");
            return;
        }
        FtpService ftpService=ftpServiceProvider.get();
        FtpClient ftpClient;
        try
        {
            ftpClient = ftpService.getFtpClient();
            Tools.log("登录ftp,下载文件");
            //String fileDir=ftpConfigProvider.get().getFtpDownPath()+File.separator+ DateUtils.toString(new Date(),"yyyy_MM_dd")+"-"+DateUtils.toString(new Date(),"yyyy_MM_dd");
            String fileDir=ftpService.getFtpConfig().getFtpDownPath();
            String[] filepaths = null;
            try
            {
                filepaths = ftpClient.listNames(fileDir);
            }
            catch (IOException e)
            {
                Tools.log("列出目录文件出错");
                Tools.log(e);
            }
            if (filepaths != null && filepaths.length > 0)
            {
                for (String fileName : filepaths)
                {
//                    download(ftpClient,fileName,fileDir);
                    break;
                }
            }
            else
            {
                Tools.log("没有找到校验文件");
            }
            ftpService.close(ftpClient);
        }
        catch (Exception e)
        {
            Tools.log(e);
        }
    }

    /**
     * 下载文件
     * @param ftpClient ftp连接
     * @param fileName 下载文件
     * @param filePath 下载文件路径
     */
    public void download(FtpClient ftpClient,String fileName,String filePath)
    {
        try
        {
            Tools.log("下载开始:"+fileName);
            byte[] data = ftpClient.retriveBytes(filePath+ File.separator+fileName);
            Tools.log("下载结束");
//            faceServiceProvider.get().schoolAttendance(data,fileName);
            ftpClient.delete(filePath+ File.separator+fileName);
            Tools.log("删除ftp文件："+fileName);
        }
        catch (Exception e)
        {
            Tools.log("ftp下载文件失败！" + fileName,e);
        }
    }
}
