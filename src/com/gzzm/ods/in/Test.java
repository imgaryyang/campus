package com.gzzm.ods.in;

import com.gzzm.in.*;
import net.cyan.commons.httpclient.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.json.JsonParser;

import java.io.File;
import java.util.*;

/**
 * @author camel
 * @date 2016/12/3
 */
public class Test
{
    private static String server = "http://oa.yunfu.gov.cn";

    private static String userName = "test";

    private static String password = "test123456";

    public Test()
    {
    }

    public static void testReceives() throws Exception
    {
        HttpClient client = new HttpClient();
        HttpHelper helper = client.getHelper(server);

        HttpRequest request = helper.get("/interface/ods/receives");
        request.addHeader("userName", userName);
        request.addHeader("password", password);
        request.addHeader("Accept", "text/json");
        HttpResponse response = request.connect();
        String s = response.getString();
        ReceiveList receiveList = new JsonParser(s).parse(ReceiveList.class);


        if (StringUtils.isEmpty(receiveList.getError()))
        {
            for (ReceiveInfo receiveInfo : receiveList.getReceives())
            {
                System.out.println(
                        receiveInfo.getReceiveId() + "," + receiveInfo.getTitle() + "," + receiveInfo.getUrl());
            }
        }
        else
        {
            System.out.println(receiveList.getError());
        }
    }

    public static void testSetReceive(Long receiveId) throws Exception
    {
        HttpClient client = new HttpClient();
        HttpHelper helper = client.getHelper(server);

        HttpRequest request = helper.get("/interface/ods/receive/" + receiveId + "/get");
        request.addHeader("userName", userName);
        request.addHeader("password", password);
        request.addHeader("Accept", "text/json");
        HttpResponse response = request.connect();
        String s = response.getString();
        BooleanResult result = new JsonParser(s).parse(BooleanResult.class);

        System.out.println(result.getError());
    }

    public static void testAcceptReceive(Long receiveId) throws Exception
    {
        HttpClient client = new HttpClient();
        HttpHelper helper = client.getHelper(server);

        HttpRequest request = helper.get("/interface/ods/receive/" + receiveId + "/accept");
        request.addHeader("userName", userName);
        request.addHeader("password", password);
        request.addHeader("Accept", "text/json");
        request.addParameter("acceptTime", new Date());

        HttpResponse response = request.connect();
        String s = response.getString();
        BooleanResult result = new JsonParser(s).parse(BooleanResult.class);

        System.out.println(result.getError());
    }

    public static void testBackReceive(Long receiveId, String reason) throws Exception
    {
        HttpClient client = new HttpClient();
        HttpHelper helper = client.getHelper(server);

        HttpRequest request = helper.get("/interface/ods/receive/" + receiveId + "/back");
        request.addHeader("userName", userName);
        request.addHeader("password", password);
        request.addHeader("Accept", "text/json");
        request.addParameter("backTime", new Date());
        request.addParameter("reason", reason);

        HttpResponse response = request.connect();
        String s = response.getString();
        BooleanResult result = new JsonParser(s).parse(BooleanResult.class);

        System.out.println(result.getError());
    }

    public static String uploadFile(String path) throws Exception
    {
        HttpClient client = new HttpClient();
        HttpHelper helper = client.getHelper(server);

        HttpRequest request = helper.post("/interface/fileupload");
        request.addHeader("userName", userName);
        request.addHeader("password", password);
        request.addHeader("Accept", "text/json");
        HttpResponse response1 = request.connect(new StreamHttpContent(new File(path)));
        FileUploadResult fileUploadResult = new JsonParser(response1.getString()).parse(FileUploadResult.class);

        if (!StringUtils.isEmpty(fileUploadResult.getError()))
            throw new Exception(fileUploadResult.getError());

        return fileUploadResult.getId();
    }

    public static void testSend() throws Exception
    {
        String contentId = uploadFile("d:\\test.doc");
        String attachmentId = uploadFile("d:\\aaa.txt");

        HttpClient client = new HttpClient();
        HttpHelper helper = client.getHelper(server);

        HttpRequest request = helper.post("/interface/ods/send");
        request.addHeader("userName", userName);
        request.addHeader("password", password);
        request.addHeader("Accept", "text/json");

        SendInfo sendInfo = new SendInfo();
        sendInfo.setTitle("测试从接口发文555");
        sendInfo.setPriority("普通");
        sendInfo.setSecret("普通");
        sendInfo.setTextType("doc");
        sendInfo.setSendNumber("测试发1号");
        sendInfo.setReceiveDeptIds(new String[]{"1"});
        sendInfo.setContentId(contentId);
        sendInfo.setSendTime(new Date());
        AttachmentInfo attachmentInfo = new AttachmentInfo();
        attachmentInfo.setId(attachmentId);
        attachmentInfo.setAttachmentName("test.txt");
        sendInfo.setAttachments(Collections.singletonList(attachmentInfo));

        HttpResponse response = request.connect(new JsonHttpContent(sendInfo));
        String s = response.getString();
        BooleanResult result = new JsonParser(s).parse(BooleanResult.class);

        System.out.println(result.getError());
    }

    public static void main(String[] args) throws Exception
    {
        testReceives();
//        testBackReceive(262L, "哈哈哈哈");
    }
}
