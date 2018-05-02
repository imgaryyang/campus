package com.gzzm.safecampus.device.attendance.util;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.device.attendance.server.SocketMain;

import java.io.IOException;
import java.net.*;

/**
 * @author liyabin
 * @date 2018/3/27
 */
public class ResponseUtils
{
    public ResponseUtils()
    {
    }

    public static void sendData(byte[] data, SocketAddress address) throws IOException
    {
        DatagramPacket packet = new DatagramPacket(data, data.length, address);
        SocketMain.getServer().send(packet);
        Tools.log(DataUtils.bytesToHexString(data));
        return;
    }

    public static void sendData(byte[] data, String ipAddress, Integer port) throws IOException
    {
        DatagramSocket server = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(data,   //数据都是已字节数据进行发送的，因此需要将数据进行转换
                data.length, //发送数据的长度
                InetAddress.getByName(ipAddress),  //发送数据的源ip地址
                port   //发送数据的端口号
        );
        server.send(packet);
        server.close();
        System.out.println(DataUtils.bytesToHexString(data));
        return;
    }


}
