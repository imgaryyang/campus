package com.gzzm.safecampus.device.attendance.server;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.device.attendance.service.AttendanceService;
import com.gzzm.safecampus.device.attendance.util.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.io.IOException;
import java.net.*;

/**
 * 考勤报文处理-后续可以加入线程池
 * @author liyabin
 * @date 2018/3/26
 */
public class ClientSocket implements Runnable
{
    @Inject
    private static Provider<AttendanceService> attendanceService;

    private DatagramPacket packet;

    public ClientSocket(DatagramPacket packet)
    {
        this.packet = packet;
    }

    @Override
    public void run()
    {
        byte[] input = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), 0, input, 0, packet.getLength());
        String ip = packet.getAddress().getHostAddress();
        int port = packet.getPort();
        Tools.log("已接收考勤机报文请求-----》ip地址：" + ip + " ----》端口号：" + port + " 消息：" + DataUtils.bytesToHexString(input));
        try
        {
            AttendanceService attendanceService = ClientSocket.attendanceService.get();
            //设置考勤机地址物理信息
            attendanceService.setSocketAddress(packet.getSocketAddress());
            attendanceService.setIpAdress(ip);
            attendanceService.setPort(port);
            //解析命令
            attendanceService.resolveCommand(DataUtils.getHexBusiness(input));
        }
        catch (Exception e)
        {
            try
            {
                ResponseUtils.sendData(input, packet.getSocketAddress());
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            Tools.log("考勤机报文解析异常：",e);
        }
    }
}
