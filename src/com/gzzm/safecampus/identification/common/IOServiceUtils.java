package com.gzzm.safecampus.identification.common;

import java.io.*;

/**
 * @author zy
 * @date 2018/4/13 19:28
 */
public class IOServiceUtils
{
    public IOServiceUtils()
    {
    }

    /**
     * 读取数据
     * @param inputStream 输入流
     * @return 接收到数据
     * @throws IOException io异常
     */
    public static String readInputStream(InputStream inputStream) throws IOException
    {
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
        String s1=reader.readLine();
        int lines=Integer.valueOf(s1);
        StringBuilder buffer=new StringBuilder();
        for(int i=0;i<lines;i++)
        {
            buffer.append(reader.readLine());
        }
        return buffer.toString();//这里要注意和客户端输出流的写方法对应,否则会抛 EOFException
    }

    public static void writeOutputStream(OutputStream outputStream,String content)
    {
        PrintWriter writer=new PrintWriter(outputStream);
        int n=(content.length()/1000)+1;
        writer.println(n);
        System.out.println("行数"+n);
        for(int i=0;i<n;i++)
        {
            if(i<(n-1))
            {
                writer.println(content.substring(1000*i,1000*(i+1)));
            }
            else
            {
                writer.println(content.substring(1000*i,content.length()));
            }
            writer.flush();
        }
    }
}
