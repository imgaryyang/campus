package com.gzzm.safecampus.identification.client;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.identification.common.*;
import net.cyan.commons.util.json.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * socket客户端
 *
 * @author zy
 * @date 2018/4/11 9:19
 */
public class ClientService
{
    public ClientService()
    {
    }

    public static AcceptData sendData(SendData sendData)
    {
        Tools.log("客户端启动..."+System.getProperty("user.dir")+"发送typeCode为exit关闭服务端");
        Socket socket = null;
        AcceptData acceptData=null;
        try
        {
            ResourceBundle resource = ResourceBundle.getBundle("com.gzzm.safecampus.identification.server.face_server");
            String ip = resource.getString("face.server.ip");
            String port = resource.getString("face.server.port");
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            socket = new Socket(ip, (port!=null&&port.trim()!="")?Integer
                    .valueOf(port):12345);
            socket.setSoTimeout(10000);
            //读取服务器端数据
            InputStream in = socket.getInputStream();
            String jsonStr = new JsonSerializer().serialize(sendData).toString();
            OutputStream out=socket.getOutputStream();
            IOServiceUtils.writeOutputStream(out,jsonStr);
            Tools.log("发送数据长度: "+jsonStr.substring(jsonStr.length()>101?jsonStr.length()-101:0,jsonStr.length()));
            String ret = IOServiceUtils.readInputStream(in);
            Tools.log("服务器端返回过来的是: " + ret);
            acceptData=new JsonParser(ret).parse(AcceptData.class);
            out.close();
            in.close();
        }
        catch (Exception e)
        {
            Tools.log("客户端异常:" ,e);
        }
        if(acceptData==null)
        {
            acceptData = new AcceptData();
            acceptData.setResCode("-1");
            acceptData.setErrorMsg("服务器连接异常");
        }
        return acceptData;
    }

    public static void main(String[] args)
    {
        SendData sendData=new SendData();
        if(args!=null)
        {
            sendData.setTypeCode(args[0]);
            sendData.setSchoolCode(args[1]);
            sendData.setPersonId(args[2]);
            System.out.println(args[0]+" "+args[1]+" "+args[2]);
        }
        else
        {
            sendData.setTypeCode("predict");
            sendData.setSchoolCode("1");
            sendData.setPersonId("wym");
        }
        StringBuilder test_string = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get("wym.txt"),
                                                    Charset.defaultCharset());
            for (String line : lines) {
                test_string.append(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        sendData.setBase64Pic(test_string.toString());
        AcceptData ret=sendData(sendData);
        System.out.println("返回结果："+ret.getResCode());
    }
}
