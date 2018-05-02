package com.gzzm.safecampus.identification;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.face.FaceService;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;
import sun.misc.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;

/**
 * 人脸web接口
 *
 * @author zy
 * @date 2018/3/26 9:25
 */
@Service
public class FaceWebPage
{
    @Inject
    private FaceService faceService;

    private FaceContent content;

    private FaceFile faceFile;

    public FaceWebPage()
    {
    }

    public FaceContent getContent()
    {
        return content;
    }

    public void setContent(FaceContent content)
    {
        this.content = content;
    }

    public FaceFile getFaceFile()
    {
        return faceFile;
    }

    public void setFaceFile(FaceFile faceFile)
    {
        this.faceFile = faceFile;
    }

    @Service(url = "/campus/identification/imagefile" , method = HttpMethod.post)
    @Json
    public FaceResult saveImageFile() throws Exception
    {
        FaceResult result = new FaceResult();
        if(faceFile!=null&&faceFile.getImageFile()!=null)
        {
            Tools.log("-----接收图片开始："+faceFile.getImageFile().getName()+"-----");
            Tools.log("扩展："+faceFile.getImageFile().getExtName());
            Tools.log("类型："+faceFile.getImageFile().getType());
            Tools.log("长度："+faceFile.getImageFile().getBytes().length);
            Tools.log("-----接收图片结束："+faceFile.getImageFile().getName()+"-----");
            result.setRet_code("0");
        }
        return result;
    }

    /**
     * 接收客户端上传的人脸图片
     * @return 接收结果
     * @throws Exception 异常信息
     */
    @Service(url = "/campus/identification/faceimage", method = HttpMethod.all)
    @Json
    public FaceResult saveFaceImage() throws Exception
    {
        FaceResult result = new FaceResult();
        if (content == null)
        {
            result.setError_msg("接收内容为空！");
        }
        else if (StringUtils.isBlank(content.getOrg_id()) || StringUtils.isBlank(content.getPerson_id()))
        {
            result.setError_msg("学校编号或人员编号不能为空！");
        }
        else if (content.getImages() == null || content.getImages().size() == 0)
        {
            result.setError_msg("图片不能为空！");
        }
        else
        {
            result.setRet_code("0");
            int i=0;
            for(String image:content.getImages())
            {
                String ret=faceService.addClientFace(content.getOrg_id(),content.getPerson_id(),image,"clientFaceImage"+i,new Date());
                if(StringUtils.isBlank(ret))
                {
                    result.setRet_code("1");
                    result.setError_msg("保存人脸失败");
                    break;
                }
            }
        }
        return result;
    }

    public static void main(String[] args)
    {
        String base64=GetImageStr();
        System.out.println(base64);
        GenerateImage(base64);
    }

    //图片转化成base64字符串
    public static String GetImageStr()
    {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        String imgFile = "C:\\Users\\zy\\Desktop\\video\\video\\192.168.1.64_01_20180322164640370_FACE_ALARM.jpg";//待处理的图片
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    //base64字符串转化成图片
    public static boolean GenerateImage(String imgStr)
    {
        //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i)
            {
                if (b[i] < 0)
                {//调整异常数据
                    b[i] += 256;
                }
            }
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(b));
            System.out.println(image.getType()+" "+image.getWidth()+" "+image.getHeight()+" ");
            //生成jpeg图片
            String imgFilePath = "D:\\zmwork\\work\\safecampus\\192.168.1.60_01_20180326202816131_FACE_ALARM.png";//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
