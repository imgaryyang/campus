package com.gzzm.safecampus.identification;

import com.google.gson.Gson;
import com.gzzm.safecampus.identification.common.*;
import com.gzzm.safecampus.identification.server.ServerService;

import java.io.IOException;
import java.util.*;

/**
 * 人脸识别接口
 *
 * @author zy
 * @date 2018/3/21 17:20
 */
public class FaceRecognitionInvoke
{
    public static boolean loadLibrary;
    public FaceRecognitionInvoke()
    {
    }

    public boolean init()
    {
        try
        {
            System.loadLibrary("LOAD_DATA");
            loadLibrary=true;
            System.out.println("loadLibrary LOAD_DATA success");
            return true;
        }
        catch (Exception e)
        {
            System.out.println("loadLibrary LOAD_DATA fail");
        }
        return false;
    }

    /**
     * 处理发送过来的数据
     * @param sendJsonData 发送过来的json数据
     * @return 返回的数据
     * @throws IOException IO异常
     */
    public String makSendData(String sendJsonData) throws IOException
    {
        SendData sendData=new Gson().fromJson(sendJsonData,SendData.class);
        AcceptData acceptData=new AcceptData();
        acceptData.setResCode("0");
        acceptData.setErrorMsg("none");
        StringBuffer errorMsg=new StringBuffer();
        switch (sendData.getTypeCode())
        {
            case "add":
                System.out.println("执行添加人脸");

                int f = addFace(sendData.getSchoolCode(), sendData.getPersonId(), sendData.getBase64Pic(), errorMsg);
                if (f == 0)
                {
                    acceptData.setResCode("1");
                }
                else
                {
                    acceptData.setResCode("0");
                    acceptData.setImageId(f);
                }
                acceptData.setErrorMsg(errorMsg.toString());
                break;
            case "del":
            {
                System.out.println("执行删除人脸图片");
                String ret =
                        delFace(sendData.getSchoolCode(), sendData.getPersonId(), sendData.getDelImageIds(), errorMsg);
                if (ret != null)
                {
                    acceptData.setErrorMsg(ret);
                }
                else
                {
                    acceptData.setResCode("1");
                    acceptData.setErrorMsg(errorMsg.toString());
                }
                break;
            }
            case "predict":
            {
                System.out.println("执行人脸识别");
                List<ResultIde> resultIdes = predictFaceFromImage(sendData.getSchoolCode(), sendData.getBase64Pic());
                acceptData.setResultIdes(resultIdes);
                if (resultIdes == null)
                {
                    acceptData.setResCode("1");
                    acceptData.setErrorMsg("返回为空");
                }
                break;
            }
            case "retrain":
            {
                System.out.println("执行人脸训练");
                String ret = retrainFace(errorMsg);
                if (ret != null)
                {
                    acceptData.setErrorMsg(ret);
                }
                else
                {
                    acceptData.setErrorMsg(errorMsg.toString());
                    acceptData.setResCode("1");
                }
                break;
            }
            case "action":
            {
                System.out.println("执行行为识别");
                List<ResultIde> resultIdes = predictActionFromImage(sendData.getSchoolCode(), sendData.getBase64Pic());
                acceptData.setResultIdes(resultIdes);
                if (resultIdes == null)
                {
                    acceptData.setResCode("1");
                    acceptData.setErrorMsg("返回为空");
                }
                break;
            }
            case "test":
                System.out.println("执行服务器测试");
                break;
            case "exit":
                System.out.println("执行服务器关闭");
                ServerService.serverSocket.close();
                System.exit(0);
             default:
                 acceptData.setResCode("1");
                 acceptData.setErrorMsg("无此功能");
        }
        return new Gson().toJson(acceptData);
    }

    /**
     * 人脸培训
     * @param errorMsg 错误消息
     * @return 培训结果
     */
    public String retrainFace(StringBuffer errorMsg)
    {
        try
        {
            if(loadLibrary)
            {
                retrain_face retrainFace=new retrain_face();
                String errMsg="";
                String ret=retrainFace.retrain(errMsg);
                errorMsg.append(errMsg);
                System.out.println("培训结果："+ret);
                return ret;
            }
        }
        catch (Exception e)
        {
            System.out.println("人脸库人脸训练异常，请确认动态库环境正常");
        }
        return null;
    }

    /**
     * 删除人脸
     * @param schoolCode 学校编号
     * @param personId 用户编号
     * @param imageId 图片id
     * @param errorMsg 错误消息
     * @return 删除结果
     */
    public String delFace(String schoolCode,String personId,List<Integer> imageId,StringBuffer errorMsg)
    {
        try
        {
            if(loadLibrary)
            {
                del_face delFace=new del_face();
                String errMsg="";
                String ret=delFace.delface(schoolCode,personId, imageId,errMsg);
                errorMsg.append(errMsg);
                System.out.println("学校："+schoolCode+" 人："+personId+" 删除人脸消息："+ret);
                return ret;
            }
            else
            {
                System.out.println("动态库未加载，无法删除人脸图片 personId："+personId+" "+imageId.toString());
            }
        }
        catch (Exception e)
        {
            System.out.println("人脸库删除图片异常，请确认动态库环境正常");
        }
        return null;
    }

    /**
     * 添加人脸入库
     * @param schoolCode 学校编号
     * @param personId 人脸编号
     * @param base64Pic 图片base64编码
     * @param errorMsg 错误提示消息
     * @return 0成功 1失败
     */
    public int addFace(String schoolCode,String personId,String base64Pic,StringBuffer errorMsg)
    {
        try
        {
            if(loadLibrary)
            {
                ADDFACE_RESULT addfaceResult=new ADDFACE_RESULT();
                int i=addfaceResult.AddFaceFromImage(schoolCode,personId, base64Pic);
                System.out.println("学校："+schoolCode+" 人："+personId+" 添加人脸返回结果："+i+"   "+addfaceResult.results.size());
                if(addfaceResult.results!=null)
                {
                    errorMsg.append(addfaceResult.results.get(0).getsError());
                    return addfaceResult.results.get(0).getImageID();
                }
                else
                {
                    errorMsg.append("未找到人脸");
                }
            }
            else
            {
                System.out.println("动态库未加载，无法添加人脸 personId："+personId);
            }
        }
        catch (Exception e)
        {
            System.out.println("添加人脸入库异常，请确认动态库环境正常");
        }
        return 0;
    }

    /**
     * 人脸识别结果
     * @param schoolCode 学校编号
     * @param base64Pic 图片base64编码
     * @return 识别结果 null标识识别错误
     */
    public List<ResultIde> predictFaceFromImage(String schoolCode, String base64Pic)
    {
        try
        {
            if(loadLibrary)
            {
                face_result faceResult = new face_result();
                faceResult.PredictFaceFromImage(schoolCode, base64Pic, 0);
                if(faceResult.results!=null)
                {
                    List<ResultIde> resultIdes=new ArrayList<>();
                    for(PREFACE_RESULT prefaceResult:faceResult.results)
                    {
                        ResultIde resultIde=new ResultIde();
                        resultIde.setImageX(prefaceResult.getX());
                        resultIde.setImageY(prefaceResult.getY());
                        resultIde.setFaceWidth(prefaceResult.getWidth());
                        resultIde.setFaceHeight(prefaceResult.getHeight());
                        if(prefaceResult.getPreFaceItem()!=null)
                        {
                            List<ResultItem> resultItems=new ArrayList<>();
                            for(PREFACE_RESULT_ITEM prefaceResultItem:prefaceResult.getPreFaceItem())
                            {
                                ResultItem resultItem=new ResultItem();
                                resultItem.setPersonId(prefaceResultItem.getPersonId());
                                resultItem.setConfidence(prefaceResultItem.getConfidence());
                                resultItems.add(resultItem);
                            }
                            resultIde.setResultItems(resultItems);
                        }
                        resultIdes.add(resultIde);
                    }
                    return resultIdes;
                }
            }
            else
            {
                System.out.println("动态库未加载，无法识别人脸 schoolCode："+schoolCode);
            }
        }
        catch (Exception e)
        {
            System.out.println("人脸识别异常，请确认动态库环境正常");
        }
        return null;
    }

    public List<ResultIde> predictActionFromImage(String schoolCode, String base64Pic)
    {
        try
        {
            if(loadLibrary)
            {
                get_result getResult = new get_result();
                getResult.PredictActionFromImage(schoolCode, base64Pic, 0);
                if(getResult.results!=null)
                {
                    List<ResultIde> resultIdes=new ArrayList<>();
                    for(ACTION_RESULT actionResult:getResult.results)
                    {
                        ResultIde resultIde=new ResultIde();
                        resultIde.setImageX(actionResult.getX());
                        resultIde.setImageY(actionResult.getY());
                        resultIde.setFaceWidth(actionResult.getWidth());
                        resultIde.setFaceHeight(actionResult.getHeight());
                        resultIde.setActionPersonId(actionResult.getPersonId());
                        if(actionResult.getVecActionItem()!=null)
                        {
                            List<ResultItem> resultItems=new ArrayList<>();
                            for(ACTION_RESULT_ITEM actionResultItem:actionResult.getVecActionItem())
                            {
                                ResultItem resultItem=new ResultItem();
                                resultItem.setActionId(actionResultItem.getActionId());
                                resultItem.setConfidence(actionResultItem.getConfidence());
                                resultItems.add(resultItem);
                            }
                            resultIde.setResultItems(resultItems);
                        }
                        resultIdes.add(resultIde);
                    }
                    return resultIdes;
                }
            }
            else
            {
                System.out.println("动态库未加载，无法行为识别 schoolCode："+schoolCode);
            }
        }
        catch (Exception e)
        {
            System.out.println("行为识别异常，请确认动态库环境正常");
        }
        return null;
    }
}
