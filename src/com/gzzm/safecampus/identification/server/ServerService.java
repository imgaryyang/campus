package com.gzzm.safecampus.identification.server;

import com.gzzm.safecampus.identification.FaceRecognitionInvoke;
import com.gzzm.safecampus.identification.common.IOServiceUtils;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

/**
 * socket连接服务端
 * @author zy
 * @date 2018/4/11 9:18
 */
public class ServerService
{
    public ServerService()
    {
    }

    public static int PORT = 12345;//监听的端口号
    public static ServerSocket serverSocket =null;

    public static void main(String[] args)
    {
        System.out.println("服务器启动...\n"+System.getProperty("user.dir"));
        ResourceBundle resource = ResourceBundle.getBundle("com.gzzm.safecampus.identification.server.face_server");
        String key = resource.getString("face.server.port");
        if(key!=null&&key.trim()!="")
        {
            PORT=Integer.valueOf(key);
        }
        ServerService server = new ServerService();
        server.init();
    }

    public void init()
    {
        try
        {
            FaceRecognitionInvoke faceRecognitionInvoke=new FaceRecognitionInvoke();
            if(!faceRecognitionInvoke.init())
            {
                return;
            }
            serverSocket = new ServerSocket(PORT);
            while (true)
            {
                try
                {
                    // 一旦有堵塞, 则表示服务器与客户端获得了连接
                    Socket client = serverSocket.accept();
                    // 处理这次连接
                    new HandlerThread(client);
                }
                catch (Exception e)
                {
                    if(e.getMessage().contains("closed"))
                    {
                        System.out.println("serverSocket 关闭");
                        break;
                    }
                    else
                    {
                        System.out.println("serverSocket 异常："+e.getMessage());
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("服务器异常: " + e.getMessage());
        }
    }

    private class HandlerThread implements Runnable
    {
        private Socket socket;

        public HandlerThread(Socket client)
        {
            socket = client;
            new Thread(this).start();
        }

        public void run()
        {
            try
            {
                FaceRecognitionInvoke faceRecognitionInvoke=new FaceRecognitionInvoke();
                InputStream in=socket.getInputStream();
                String clientInputStr = IOServiceUtils.readInputStream(in);//这里要注意和客户端输出流的写方法对应,否则会抛 EOFException
                System.out.println("接收长度:"+clientInputStr.length()+"   "+clientInputStr.substring(clientInputStr.length()>101?clientInputStr.length()-101:0,clientInputStr.length()) );
                // 向客户端回复信息
                OutputStream out = socket.getOutputStream();
                // 发送键盘输入的一行
               String s =faceRecognitionInvoke.makSendData(clientInputStr);
                System.out.println("返回:"+s );
                IOServiceUtils.writeOutputStream(out,s);
                out.close();
                in.close();
            }
            catch (Exception e)
            {
                System.out.println("服务器 run 异常: " + getExceptionDetail(e));
            }
            finally
            {
                if (socket != null)
                {
                    try
                    {
                        socket.close();
                    }
                    catch (Exception e)
                    {
                        socket = null;
                        System.out.println("服务端 finally 异常:" + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 获取exception详情信息
     *
     * @param e Excetipn type
     * @return String type
     */
    public String getExceptionDetail(Exception e)
    {
        StringBuffer msg = new StringBuffer("");
        if (e != null)
        {
            msg = new StringBuffer("");
            String message = e.toString();
            int length = e.getStackTrace().length;
            if (length > 0)
            {
                msg.append(message).append("\n");
                for (int i = 0; i < length; i++)
                {
                    msg.append("\t").append(e.getStackTrace()[i]).append("\n");
                }
            }
            else
            {
                msg.append(message);
            }
        }
        return msg.toString();
    }
}
