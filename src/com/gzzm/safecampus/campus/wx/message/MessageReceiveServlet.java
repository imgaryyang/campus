package com.gzzm.safecampus.campus.wx.message;

import net.cyan.commons.util.Provider;
import net.cyan.commons.util.xml.XmlUtils;
import net.cyan.nest.annotation.Inject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信事件推送响应
 * 响应微信推送过来的用户关注/取消关注事件，菜单点击事件，用户发送消息（文本消息、图片消息、语音消息、视频消息、小视频消息、地理位置消息、链接消息）
 *
 * @author Neo
 * @date 2018/4/23 15:16
 */
public class MessageReceiveServlet extends HttpServlet
{
    @Inject
    private static Provider<List<MessageProcessor>> messageProcessorsProvider;

    public MessageReceiveServlet()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        //接口url配置时微信会通过get的方式进行验证，直接返回微信访问参数中的随机字符串即可
        String echoStr = request.getParameter("echostr");
        PrintWriter out = response.getWriter();
        out.print(echoStr);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        process(request, response);
    }

    protected void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String result = "";
        try
        {
            InputStream ins = request.getInputStream();
            Document document = XmlUtils.createDocument(ins);
            Map<String, String> map = xmlToMap(document);
            String msgType = map.get("MsgType");
            String type = msgType.equals("event") ? map.get("Event") : msgType;
            List<MessageProcessor> messageProcessors = messageProcessorsProvider.get();
            if(messageProcessors != null)
            {
                for (MessageProcessor messageProcessor : messageProcessors)
                {
                    if (messageProcessor.getMessageType().equals(type))
                    {
                        messageProcessor.process(map);
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            out.println(result);
            out.close();
        }
    }

    private static Map<String, String> xmlToMap(Document document) throws Exception
    {
        Map<String, String> map = new HashMap<>();
        NodeList childNodes = document.getChildNodes().item(0).getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            map.put(childNodes.item(i).getNodeName(), childNodes.item(i).getTextContent());
        }
        return map;
    }
}
