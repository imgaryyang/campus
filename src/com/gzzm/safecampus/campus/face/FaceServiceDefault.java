package com.gzzm.safecampus.campus.face;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.device.Camera;
import com.gzzm.safecampus.identification.*;
import com.gzzm.safecampus.identification.client.ClientService;
import com.gzzm.safecampus.identification.common.*;
import com.gzzm.safecampus.s3.S3Service;
import com.gzzm.safecampus.wx.attendance.FaceRecAttendanceService;
import net.cyan.commons.transaction.*;
import net.cyan.commons.util.*;
import net.cyan.commons.util.json.JsonSerializer;
import net.cyan.nest.annotation.Inject;
import sun.misc.*;

import java.util.*;
import java.util.UUID;

/**
 * 默认的人脸识别实现方式
 * @author zy
 * @date 2018/4/24 15:47
 */
public class FaceServiceDefault implements FaceService
{
    /**
     * 持久层
     */
    @Inject
    private FaceDao faceDao;

    @Inject
    private FaceRecAttendanceService faceRecAttendanceService;

    @Inject
    private S3Service s3Service;

    public FaceServiceDefault()
    {
    }

    /**
     * 学校考勤
     * @param data 图片
     * @param fileName 图片名称
     * @throws Exception
     */
    @Override
    public void schoolAttendance(byte[] data,String fileName,String imagePath) throws Exception
    {
        String[] nameSplit=fileName.split("_");
        String prefix=null;
        if(nameSplit.length > 0)
            prefix=nameSplit[0];
        Date imageTime=null;
        if(nameSplit.length>2&&nameSplit[2].length()>13)
            imageTime= DateUtils.toDate(nameSplit[2].substring(0,14));
        else
            imageTime=new Date();
        Integer cameraId=null;
        Integer deptId=null;
        FaceResult faceResult=FaceResult.Nothing;
        StringBuffer resultMsg=new StringBuffer();
        String faceId=null;
        Integer schoolId=null;
        List<ResultIde> resultIdes =null;
        List<ResultIde> actionIdes=null;
        School school=null;
        if(StringUtils.isNotBlank(prefix))
        {
            Camera camera=faceDao.getCameraByNum(prefix);
            if(camera!=null)
            {
                cameraId=camera.getCameraId();
                deptId=camera.getDeptId();
                school=faceDao.getSchoolByDeptId(camera.getDeptId());
                if(school!=null)
                {
                    schoolId=school.getSchoolId();
                    resultIdes = predictFaceFromImage(school.getSchoolCode(),data,resultMsg);
                    //actionIdes=predictActionFromImage(school.getSchoolCode(),data,resultMsg);
                }
            }
        }
        if(resultIdes!=null&&resultIdes.size()>0&&resultIdes.get(0).getResultItems()!=null&&resultIdes.get(0).getResultItems().size()>0)
        {
            String personId=resultIdes.get(0).getResultItems().get(0).getPersonId();//用户主键 陌生人stranger  没有人脸noface
            if(!(personId.equals("noface")))
                faceResult=FaceResult.Face;
            if(!(personId.equals("noface")||personId.equals("stranger")))
            {
                faceId=personId;
                resultMsg.append(new JsonSerializer().serialize(resultIdes.get(0).getResultItems().get(0)).toString());
            }
        }
        if(actionIdes!=null&&actionIdes.size()>0&&actionIdes.get(0).getResultItems()!=null&&actionIdes.get(0).getResultItems().size()>0)
        {
            if(faceResult==FaceResult.Nothing)
                faceResult=FaceResult.Action;
            else
                faceResult=FaceResult.All;
            resultMsg.append(new JsonSerializer().serialize(actionIdes.get(0).getResultItems().get(0)).toString());
        }
        String imageId=null;
        if(StringUtils.isNotBlank(imagePath))
             imageId=addFaceImageLib(imagePath,fileName,faceId,deptId,cameraId,ImageType.Attendance,faceResult,resultMsg.toString(),schoolId,imageTime);
        else if(school!=null)
            imageId=addFaceImageLib(getUUID(),data,fileName,faceId,deptId,cameraId,ImageType.Attendance,faceResult,resultMsg.toString(),schoolId,null,imageTime,school.getSchoolCode());
        if(imageId!=null)
        {
            List<FacePersonResult> personResults=recordIdeResult(resultIdes,imageId);
            recordIdeAction(actionIdes,imageId);
            if(personResults!=null)
            {
                for(FacePersonResult personResult:personResults)
                {
                    FaceLibrary faceLibrary=faceDao.load(FaceLibrary.class,personResult.getFaceId());
                    if(faceLibrary!=null&&faceLibrary.getPersonType()!=null&&faceLibrary.getPersonType().equals(PersonType.student))
                    {
                        Tools.log("记录学生考勤："+faceLibrary.getPersonId()+" = "+cameraId+" = "+imageId+" = "+DateUtils.toString(imageTime));
                        faceRecAttendanceService.saveAttendance(faceLibrary.getPersonId(),cameraId,imageId,imageTime);
                    }
                }
            }
        }
    }

    public List<ActionPersonResult> recordIdeAction(List<ResultIde> actionIdes,String imageId)
    {
        List<ActionPersonResult> actionPersonResults=null;
        if(actionIdes!=null)
        {
            actionPersonResults=new ArrayList<>();
            for(ResultIde resultIde:actionIdes)
            {
                try
                {
                    if(resultIde!=null&&resultIde.getResultItems()!=null&&resultIde.getResultItems().get(0).getPersonId()!=null)
                    {
                        ActionPersonResult result=saveActionPersonResult(resultIde,imageId);
                        actionPersonResults.add(result);
                    }
                }
                catch (Exception e)
                {
                    Tools.log("记录行为识别结果异常",e);
                }
            }
        }
        return actionPersonResults;
    }

    public ActionPersonResult saveActionPersonResult(ResultIde actionIde,String imageId) throws Exception
    {
        if(actionIde!=null&&actionIde.getResultItems()!=null&&actionIde.getResultItems().size()>0)
        {
            ActionPersonResult actionPersonResult=new ActionPersonResult();
            actionPersonResult.setResultId(getUUID());
            actionPersonResult.setImageId(imageId);
            String faceId=actionIde.getActionPersonId();
            if(!(faceId.equals("noface")||faceId.equals("stranger")))
            {
                actionPersonResult.setFaceId(faceId);

            }
            String confidence="";
            String actionId="";
            boolean first=true;
            for(ResultItem resultItem:actionIde.getResultItems())
            {
                confidence=confidence+(first?resultItem.getConfidence():(","+resultItem.getConfidence()));
                actionId=actionId+(first?resultItem.getActionId():(","+resultItem.getActionId()));
                first=false;
            }
            actionPersonResult.setConfidence(confidence);
            actionPersonResult.setActionId(actionId);
            actionPersonResult.setImageX(actionIde.getImageX());
            actionPersonResult.setImageY(actionIde.getImageY());
            actionPersonResult.setActionWidth(actionIde.getFaceWidth());
            actionPersonResult.setActionHeight(actionIde.getFaceHeight());
            faceDao.save(actionPersonResult);
            return actionPersonResult;
        }
        return null;
    }

    /**
     * 记录人脸识别结果
     * @param resultIdes 识别结果
     * @param imageId 所属图片
     * @return 记录信息
     */
    public List<FacePersonResult> recordIdeResult(List<ResultIde> resultIdes,String imageId)
    {
        List<FacePersonResult> facePersonResults=null;
        if(resultIdes!=null)
        {
            facePersonResults=new ArrayList<>();
            for(ResultIde resultIde:resultIdes)
            {
                try
                {
                    if(resultIde!=null&&resultIde.getResultItems()!=null&&resultIde.getResultItems().get(0).getPersonId()!=null&&!resultIde.getResultItems().get(0).getPersonId().equals("noface"))
                    {
                        FacePersonResult result=saveFacePersonResult(resultIde,imageId);
                        facePersonResults.add(result);
                    }
                }
                catch (Exception e)
                {
                    Tools.log("记录识别人脸结果异常",e);
                }
            }
        }
        return facePersonResults;
    }

    /**
     * 保存识别人脸
     * @param resultIde 识别结果，无人脸的不要传入
     * @param imageId 所属识别图片
     * @return 保存记录结果主键
     * @throws Exception 数据库异常
     */
    public FacePersonResult saveFacePersonResult(ResultIde resultIde,String imageId)throws Exception
    {
        if(resultIde!=null&&resultIde.getResultItems()!=null&&resultIde.getResultItems().size()>0)
        {
            FacePersonResult facePersonResult=new FacePersonResult();
            facePersonResult.setResultId(getUUID());
            facePersonResult.setImageId(imageId);
            String faceId=resultIde.getResultItems().get(0).getPersonId();
            if(!(faceId.equals("noface")||faceId.equals("stranger")))
            {
                facePersonResult.setFaceId(faceId);
                facePersonResult.setConfidence(resultIde.getResultItems().get(0).getConfidence());
            }
            facePersonResult.setImageX(resultIde.getImageX());
            facePersonResult.setImageY(resultIde.getImageY());
            facePersonResult.setFaceWidth(resultIde.getFaceWidth());
            facePersonResult.setFaceHeight(resultIde.getFaceHeight());
            faceDao.save(facePersonResult);
            return facePersonResult;
        }
        return null;
    }

    /**
     * 删除人脸
     * @param imageId 图片主键
     * @return 删除结果
     */
    @Override
    public boolean delFace(String imageId) throws Exception
    {
        FaceImageLib faceImageLib=faceDao.get(FaceImageLib.class,imageId);
        StringBuffer errorMsg=new StringBuffer();
        String ret=null;
        if(StringUtils.isNotBlank(faceImageLib.getSchool().getSchoolCode())&&StringUtils.isNotBlank(faceImageLib.getFaceId())&&faceImageLib.getImageLibId()!=null)
        {
            ret=delFaceImageInvoke(faceImageLib.getSchool().getSchoolCode(),faceImageLib.getFaceId(),faceImageLib.getImageLibId(),errorMsg);
        }
        if(ret!=null)
        {
            faceImageLib.setResultMsg(errorMsg.toString());
            faceImageLib.setDeleteTag(1);
            faceDao.update(faceImageLib);
            return true;
        }
        return false;
    }

    /**
     * 修改人脸
     * @param imageId 图片主键
     * @param image 新的图片
     * @param imageName 新的图片名称
     * @return 修改后返回新的imageId图片主键
     */
    @Override
    public String updateFace(String imageId,byte[] image,String imageName) throws Exception
    {
        FaceImageLib faceImageLib=faceDao.get(FaceImageLib.class,imageId);
        StringBuffer errorMsg=new StringBuffer();
        String ret=null;
        if(StringUtils.isNotBlank(faceImageLib.getSchool().getSchoolCode())&&StringUtils.isNotBlank(faceImageLib.getFaceId())&&faceImageLib.getImageLibId()!=null)
        {
            ret=delFaceImageInvoke(faceImageLib.getSchool().getSchoolCode(),faceImageLib.getFaceId(),faceImageLib.getImageLibId(),errorMsg);
        }
        if(ret!=null)
        {
            faceImageLib.setDeleteTag(1);
            faceDao.update(faceImageLib);
            errorMsg.append("||");
            if(StringUtils.isNotBlank(faceImageLib.getSchool().getSchoolCode())&&StringUtils.isNotBlank(faceImageLib.getFaceId()))
            {
                Integer imageLibId=addFaceInvoke(faceImageLib.getSchool().getSchoolCode(),faceImageLib.getFaceId(),imageToBase64(image),errorMsg);
                errorMsg.append("||").append(faceImageLib.getImageId());
                String faceImageId=addFaceImageLib(getUUID(),image,imageName,faceImageLib.getFaceId(),faceImageLib.getDeptId(),null,ImageType.FaceLib,imageLibId!=0?FaceResult.Face:FaceResult.Nothing,errorMsg.toString(),faceImageLib.getSchoolId(),imageLibId,new Date(),faceImageLib.getSchool().getSchoolCode());
                if(imageLibId!=0)
                {
                    return faceImageId;
                }
            }
        }
        return null;
    }

    /**
     * 添加人脸
     * @param schoolCode 学校编号
     * @param campusSerialNo 学生唯一标识号
     * @param image 图片
     * @param imageName 图片名称
     * @param imageTime 图片时间
     * @return FaceImageLib主键 null失败
     * @throws Exception 数据库异常
     */
    @Override
    public String addFace(String schoolCode, String campusSerialNo,byte[] image,String imageName,Date imageTime) throws Exception
    {
        return addFace(schoolCode,campusSerialNo,imageToBase64(image),imageName,imageTime);
    }

    /**
     * 添加人脸
     * @param schoolCode 学校编号
     * @param campusSerialNo 学生唯一标识号
     * @param imageBase64 图片
     * @param imageName 图片名称
     * @param imageTime 图片时间
     * @return FaceImageLib主键 null失败
     * @throws Exception 数据库异常
     */
    @Override
    public String addFace(String schoolCode, String campusSerialNo,String imageBase64,String imageName,Date imageTime) throws Exception
    {
        Student student=faceDao.getStudent(schoolCode,campusSerialNo);
        if(student!=null)
        {
            return addFace(student.getStudentId(),imageBase64,imageName,imageTime,null);
        }
        return null;
    }

    /**
     * 添加人脸
     * @param schoolCode 学校编号
     * @param campusSerialNo 学生唯一标识号
     * @param imageBase64 图片
     * @param imageName 图片名称
     * @param imageTime 图片时间
     * @return FaceImageLib主键 null失败
     * @throws Exception 数据库异常
     */
    @Override
    public String addClientFace(String schoolCode, String campusSerialNo,String imageBase64,String imageName,Date imageTime) throws Exception
    {
        Student student=faceDao.getStudent(schoolCode,campusSerialNo);
        if(student!=null)
        {
            FaceLibrary faceLibrary=faceDao.getFaceLibrary(student.getDeptId(),student.getStudentId(),PersonType.student);
            if(faceLibrary!=null)
            {
                FaceImageLib faceImageLib=faceDao.getClientFaceImage(faceLibrary.getFaceId(),imageName,ImageType.FaceLib);
                if(faceImageLib!=null&&StringUtils.isNotBlank(faceImageLib.getSchool().getSchoolCode())&&StringUtils.isNotBlank(faceImageLib.getFaceId())&&faceImageLib.getImageLibId()!=null)
                {
                    StringBuffer errorMsg=new StringBuffer();
                    String ret=delFaceImageInvoke(faceImageLib.getSchool().getSchoolCode(),faceImageLib.getFaceId(),faceImageLib.getImageLibId(),errorMsg);
                    if(ret!=null)
                    {
                        faceImageLib.setDeleteTag(1);
                        faceDao.update(faceImageLib);
                    }
                }
            }
            return addFace(student.getStudentId(),imageBase64,imageName,imageTime,null);
        }
        return null;
    }

    /**
     * 添加人脸入库
     * @param studentId 学生主键
     * @param imageBase64 图片base64
     * @param imageName 图片名称没有可以为空
     * @param imageTime 图片时间
     * @param imagePath 图片存储路径没有为空
     * @return 保存结果
     * @throws Exception 数据库异常
     */
    @Transactional(mode= TransactionMode.not_supported)
    @Override
    public String addFace(Integer studentId,String imageBase64,String imageName,Date imageTime,String imagePath) throws Exception
    {
        Tools.log("studentId:"+studentId+" imageName:"+imageName);
        if(imageBase64==null)
            throw new MessageException("图片不能为空！");
        Student student=faceDao.get(Student.class,studentId);
        School school=faceDao.getSchoolByDeptId(student.getDeptId());
        StringBuffer errorMsg=new StringBuffer();
        FaceLibrary faceLibrary=faceDao.getFaceLibrary(student.getDeptId(),student.getStudentId(),PersonType.student);
        String uuid;
        if(faceLibrary!=null)
        {
            uuid=faceLibrary.getFaceId();
        }
        else
        {
            uuid= getUUID();
        }
        Integer imageLibId=0;
        if(StringUtils.isNotBlank(school.getSchoolCode()))
        {
            imageLibId=addFaceInvoke(school.getSchoolCode(),uuid,imageBase64,errorMsg);
        }
        else
        {
            errorMsg.append("学校编号为空，学生主键为：").append(studentId);
        }
        if(imageLibId!=0&&faceLibrary==null)
        {
            saveFaceLibrary(uuid,student.getStudentId(),PersonType.student,school.getSchoolId(),student.getDeptId());
        }
        else if(faceLibrary==null)
        {
            uuid=null;
        }
        String ret=null;
        if(imagePath!=null)
        {
            ret=addFaceImageLib(imagePath,imageName,uuid,student.getDeptId(),null,ImageType.FaceLib,imageLibId!=0?FaceResult.Face:FaceResult.Nothing,errorMsg.toString(),school.getSchoolId(),imageLibId,imageTime);
        }
        else
        {
            ret=addFaceImageLib(getUUID(),imageToByte(imageBase64) ,imageName,uuid,student.getDeptId(),null,ImageType.FaceLib,imageLibId!=0?FaceResult.Face:FaceResult.Nothing,errorMsg.toString(),school.getSchoolId(),imageLibId,imageTime,school.getSchoolCode());
        }
        if(imageLibId!=0)
        {
            return ret;
        }
        else
        {
            return null;
        }
    }

    /**
     * 添加学生人脸入库
     * @param studentId 学生主键
     * @param image 图片
     * @param imageName 图片名称
     * @param imageTime 图片时间
     * @param imagePath 图片路径
     * @return 0成功 1失败
     * @throws Exception 保存异常
     */
    @Override
    public String addFace(Integer studentId,byte[] image,String imageName,Date imageTime,String imagePath) throws Exception
    {
        return addFace(studentId,imageToBase64(image),imageName,imageTime,imagePath);
    }

    /**
     * base64图片抓byte[]
     * @param imageBase64 base64图片
     * @return byte[]图片
     */
    public static byte[] imageToByte(String imageBase64)
    {
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            return decoder.decodeBuffer(imageBase64);
        }
        catch (Exception e)
        {
            Tools.log("base64转图片异常",e);
        }
        return null;
    }

    /**
     * byte[]图片转base64字符串
     * @param image byte[]图片
     * @return base64字符串
     */
    public static String imageToBase64(byte[] image)
    {
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(image);//返回Base64编码过的字节数组字符串
    }

    /**
     * 添加人脸图片
     * @param imagePath 图片s3路径
     * @param imageName 图片名称没有可以为空
     * @param faceId 关联人脸库没有可以为空
     * @param deptId 所属学校的部门
     * @param cameraId 改图片是哪个设备拍摄的没有可以为空
     * @param imageType 图片功能类型
     * @param faceResult 人脸识别结果
     * @param resultMsg 识别返回消息
     * @param imageLibId 人脸库中每个人对应图片id
     * @param imageTime 图片时间
     * @throws Exception 数据库异常
     */
    @Override
    public String addFaceImageLib(String imagePath,String imageName,String faceId,Integer deptId,Integer cameraId,ImageType imageType,FaceResult faceResult,String resultMsg,Integer schoolId,Integer imageLibId,Date imageTime)
            throws Exception
    {
        return addFaceImageLib(getUUID(),imagePath,imageName,faceId,deptId,cameraId,imageType,faceResult,resultMsg,schoolId,imageTime,imageLibId);
    }

    /**
     * 添加人脸图片
     * @param imagePath 图片S3路径
     * @param imageName 图片名称没有可以为空
     * @param faceId 关联人脸库没有可以为空
     * @param deptId 所属学校的部门
     * @param cameraId 改图片是哪个设备拍摄的没有可以为空
     * @param imageType 图片功能类型
     * @param faceResult 人脸识别结果
     * @param resultMsg 识别返回消息
     * @param schoolId 学校主键
     * @param imageTime 图片时间
     * @throws Exception 数据库异常
     */
    @Override
    public String addFaceImageLib(String imagePath,String imageName,String faceId,Integer deptId,Integer cameraId,ImageType imageType,FaceResult faceResult,String resultMsg,Integer schoolId,Date imageTime)
            throws Exception
    {
        return addFaceImageLib(getUUID(),imagePath,imageName,faceId,deptId,cameraId,imageType,faceResult,resultMsg,schoolId,imageTime);
    }

    /**
     * 添加人脸图片
     * @param imageId 主键
     * @param imagePath 图片S3路径
     * @param imageName 图片名称没有可以为空
     * @param faceId 关联人脸库没有可以为空
     * @param deptId 所属学校的部门
     * @param cameraId 改图片是哪个设备拍摄的没有可以为空
     * @param imageType 图片功能类型
     * @param faceResult 人脸识别结果
     * @param resultMsg 识别返回消息
     * @param schoolId 学校主键
     * @param imageTime 图片时间
     * @throws Exception 数据库异常
     */
    @Override
    public String addFaceImageLib(String imageId,String imagePath,String imageName,String faceId,Integer deptId,Integer cameraId,ImageType imageType,FaceResult faceResult,String resultMsg,Integer schoolId,Date imageTime)
            throws Exception
    {
        return addFaceImageLib(imageId,imagePath,imageName,faceId,deptId,cameraId,imageType,faceResult,resultMsg,schoolId,imageTime,null);
    }

    /**
     * 添加人脸图片
     * @param imageId 主键
     * @param image 图片
     * @param imageName 图片名称没有可以为空
     * @param faceId 关联人脸库没有可以为空
     * @param deptId 所属学校的部门
     * @param cameraId 改图片是哪个设备拍摄的没有可以为空
     * @param imageType 图片功能类型
     * @param faceResult 人脸识别结果
     * @param resultMsg 识别返回消息
     * @param schoolId 学校主键
     * @param imageTime 图片时间
     * @param schoolCode 学校编号
     * @throws Exception 数据库异常
     */
    @Override
    public String addFaceImageLib(String imageId,byte[] image,String imageName,String faceId,Integer deptId,Integer cameraId,ImageType imageType,FaceResult faceResult,String resultMsg,Integer schoolId,Integer imageLibId,Date imageTime,String schoolCode)
            throws Exception
    {
        String imagePath=s3Service.getPhotoKey(getFaceImagePath(schoolCode,imageId));
        s3Service.addFile(imagePath,image);
        return addFaceImageLib(imageId,imagePath,imageName,faceId,deptId,cameraId,imageType,faceResult,resultMsg,schoolId,imageTime,imageLibId);
    }

    /**
     * 添加人脸图片
     * @param imageId 主键
     * @param imagePath 图片S3路径
     * @param imageName 图片名称没有可以为空
     * @param faceId 关联人脸库没有可以为空
     * @param deptId 所属学校的部门
     * @param cameraId 改图片是哪个设备拍摄的没有可以为空
     * @param imageType 图片功能类型
     * @param faceResult 人脸识别结果
     * @param resultMsg 识别返回消息
     * @param schoolId 学校主键
     * @param imageLibId 人脸库中每个人对应图片id
     * @param imageTime 图片时间
     * @throws Exception 数据库异常
     */
    @Override
    public String addFaceImageLib(String imageId,String imagePath,String imageName,String faceId,Integer deptId,Integer cameraId,ImageType imageType,FaceResult faceResult,String resultMsg,Integer schoolId,Date imageTime,Integer imageLibId)
            throws Exception
    {
        FaceImageLib faceImageLib=new FaceImageLib();
        faceImageLib.setImageId(imageId);
        faceImageLib.setImagePath(imagePath);
        faceImageLib.setCreateTime(imageTime);
        faceImageLib.setFaceId(faceId);
        faceImageLib.setResultMsg(resultMsg);
        faceImageLib.setFaceResult(faceResult);
        faceImageLib.setImageType(imageType);
        faceImageLib.setDeptId(deptId);
        faceImageLib.setCameraId(cameraId);
        faceImageLib.setImageName(imageName);
        faceImageLib.setSchoolId(schoolId);
        faceImageLib.setImageLibId(imageLibId);
        faceDao.save(faceImageLib);
        return faceImageLib.getImageId();
    }

    /**
     * 保存人脸库
     * @param uuid 主键
     * @param personId 所属人员
     * @param personType 人员类型
     * @param schoolId 所属学校
     * @param deptId 学校的部门
     * @throws Exception 数据库异常
     */
    @Override
    public void saveFaceLibrary(String uuid,Integer personId,PersonType personType,Integer schoolId,Integer deptId)
            throws Exception
    {
        FaceLibrary faceLibrary=new FaceLibrary();
        faceLibrary.setFaceId(uuid);
        faceLibrary.setPersonId(personId);
        faceLibrary.setPersonType(personType);
        faceLibrary.setSchoolId(schoolId);
        faceLibrary.setDeptId(deptId);
        faceLibrary.setCreateTime(new Date());
        faceDao.save(faceLibrary);
    }

    /**
     * 添加人脸入库
     * @param schoolCode 学校编号
     * @param personId 人脸编号
     * @param base64Pic 图片base64编码
     * @param errorMsg 错误提示消息
     * @return 0成功 1失败
     */
    public Integer addFaceInvoke(String schoolCode,String personId,String base64Pic,StringBuffer errorMsg)
    {
        SendData sendData=new SendData(schoolCode,personId,base64Pic);
        sendData.setTypeCode("add");
        AcceptData acceptData= ClientService.sendData(sendData);
        errorMsg.append(acceptData.getErrorMsg());
        if(acceptData.getResCode().equals("0"))
            return acceptData.getImageId();
        else
            return 0;
    }

    /**
     * 删除人脸图片
     * @param schoolCode 学校编号
     * @param personId 学校编号
     * @param imageLibId 人脸库图片id
     * @param errorMsg 接收返回的异常消息
     * @return 删除结果
     */
    public String delFaceImageInvoke(String schoolCode,String personId,Integer imageLibId,StringBuffer errorMsg)
    {
        List<Integer> imgList=new ArrayList<>();
        imgList.add(imageLibId);
        return delFaceImageInvoke(schoolCode,personId,imgList,errorMsg);
    }

    /**
     * 删除人脸图片
     * @param schoolCode 学校编号
     * @param personId 学校编号
     * @param imageLibIds 人脸库图片id
     * @param errorMsg 接收返回的异常消息
     * @return 删除结果
     */
    public String delFaceImageInvoke(String schoolCode,String personId,List<Integer> imageLibIds,StringBuffer errorMsg)
    {
        SendData sendData=new SendData(schoolCode,personId,imageLibIds);
        sendData.setTypeCode("del");
        AcceptData acceptData= ClientService.sendData(sendData);
        errorMsg.append(acceptData.getErrorMsg());
        if(acceptData.getResCode().equals("0"))
            return acceptData.getErrorMsg();
        else
            return null;
    }

    /**
     * 人脸识别结果
     * @param schoolCode 学校编号
     * @param image 图片
     * @return 识别结果 null标识识别错误
     */
    @Override
    public List<ResultIde> predictFaceFromImage(String schoolCode, byte[] image,StringBuffer errorMsg)
    {
        return predictFaceFromImageInvoke(schoolCode,imageToBase64(image),errorMsg);
    }

    /**
     * 人脸识别结果
     * @param schoolCode 学校编号
     * @param base64Pic 图片base64编码
     * @return 识别结果 null标识识别错误
     */
    public List<ResultIde> predictFaceFromImageInvoke(String schoolCode, String base64Pic,StringBuffer errorMsg)
    {
        SendData sendData=new SendData(schoolCode,base64Pic);
        sendData.setTypeCode("predict");
        AcceptData acceptData= ClientService.sendData(sendData);
        errorMsg.append(acceptData.getErrorMsg());
        return acceptData.getResultIdes();
    }

    /**
     * 行为识别结果
     * @param schoolCode 学校编号
     * @param image 图片
     * @param errorMsg 错误消息
     * @return 识别结果 null表示识别错误
     */
    @Override
    public List<ResultIde> predictActionFromImage(String schoolCode,byte[] image,StringBuffer errorMsg)
    {
        return predictActionFromImageInvoke(schoolCode,imageToBase64(image),errorMsg);
    }

    /**
     * 行为识别结果
     * @param schoolCode 学校编号
     * @param base64Pic 图片
     * @return 识别结果 null表示识别错误
     */
    public List<ResultIde> predictActionFromImageInvoke(String schoolCode,String base64Pic,StringBuffer errorMsg)
    {
        SendData sendData=new SendData(schoolCode,base64Pic);
        sendData.setTypeCode("action");
        AcceptData acceptData= ClientService.sendData(sendData);
        errorMsg.append(acceptData.getErrorMsg());
        return acceptData.getResultIdes();
    }

    /**
     * 执行人脸培训，建议每天晚上执行一次
     * @return 培训结果
     */
    @Override
    public boolean retrainFace()
    {
        SendData sendData=new SendData();
        sendData.setTypeCode("retrain");
        AcceptData ret=ClientService.sendData(sendData);
        return ret != null && ret.getResCode().equals("0");
    }

    /**
     * 测试人脸服务是否启动
     * @return 测试结果
     */
    @Override
    public boolean testServer()
    {
        SendData sendData=new SendData();
        sendData.setTypeCode("test");
        AcceptData ret=ClientService.sendData(sendData);
        return ret != null && ret.getResCode().equals("0");
    }

    /**
     * 关闭人脸服务，服务器进程无法停止
     */
    public void closeServer()
    {
        SendData sendData=new SendData();
        sendData.setTypeCode("exit");
        ClientService.sendData(sendData);
    }

    /**
     * 获取uuid并且大写
     * @return uuid
     */
    public static String getUUID()
    {
        String uuid=Tools.getUUID().toUpperCase();
        if(uuid.length()>36)
        {
            uuid=UUID.randomUUID().toString().toUpperCase();
            if(uuid.length()>36)
            {
                uuid=uuid.substring(0,36);
            }
        }
        return uuid;
    }

    /**
     * 获取人脸识别图片的存放文件
     * @param schoolCode 学校编号
     * @param uuid 图片主键
     * @return 路径
     */
    public static String getFaceImagePath(String schoolCode,String uuid)
    {
        return schoolCode+"/FaceImageLib/"+DateUtils.toString(new Date(),"yyyyMMdd")+"/"+uuid;
    }
}
