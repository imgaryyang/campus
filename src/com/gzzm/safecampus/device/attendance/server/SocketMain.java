package com.gzzm.safecampus.device.attendance.server;

import com.gzzm.platform.commons.Tools;

import java.io.IOException;
import java.net.*;

/**
 * @author liyabin
 * @date 2018/3/26
 */
public class SocketMain implements Runnable
{
    private static DatagramSocket server = null;

    //  private Vector<ClientSocket> pools = new Vector<ClientSocket>(100);
    public SocketMain()
    {
    }

    public static DatagramSocket getServer()
    {
        return server;
    }

    @Override
    public void run()
    {

        try
        {
            server = new DatagramSocket(6000);
        }
        catch (SocketException e)
        {
            Tools.log("考勤节报文监听启动失败！");
        }
        while (true)
        {
            //创建数据接收的数据缓冲区
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, 1024);
            try
            {
                server.receive(packet);
                Thread resolve = new Thread(new ClientSocket(packet));
                resolve.start();
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                Tools.log("线程堵塞！");
            }
            catch (IOException e)
            {
                Tools.log("获取报文信息异常！");
                Tools.log(e);
                try
                {
                    Thread.sleep(100);
                    String ip = packet.getAddress().getHostAddress();
                    int port = packet.getPort();
                    Tools.log("已接收考勤机报文请求-----》ip地址：" + ip + " ----》端口号：" + port );
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void destroyed()
    {
        if (server != null) server.close();
    }
}
