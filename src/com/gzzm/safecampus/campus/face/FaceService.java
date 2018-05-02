package com.gzzm.safecampus.campus.face;

import com.gzzm.safecampus.identification.ResultIde;
import net.cyan.commons.transaction.Transactional;

import java.util.*;

/**
 * 人脸识别业务处理
 * @author zy
 * @date 2018/3/30 9:10
 */
public interface FaceService
{
    /**
     * 学校考勤
     * @param data 图片
     * @param fileName 图片名称
     * @throws Exception
     */
    public void schoolAttendance(byte[] data,String fileName,String imagePath) throws Exception;

    /**
     * 删除人脸
     * @param imageId 图片主键
     * @return 删除结果
     */
    public boolean delFace(String imageId) throws Exception;

    /**
     * 修改人脸
     * @param imageId 图片主键
     * @param image 新的图片
     * @param imageName 新的图片名称
     * @return 修改后返回新的imageId图片主键
     */
    public String updateFace(String imageId,byte[] image,String imageName) throws Exception;

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
    public String addFace(String schoolCode, String campusSerialNo,byte[] image,String imageName,Date imageTime) throws Exception;

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
    public String addFace(String schoolCode, String campusSerialNo,String imageBase64,String imageName,Date imageTime) throws Exception;

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
    public String addClientFace(String schoolCode, String campusSerialNo,String imageBase64,String imageName,Date imageTime) throws Exception;

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
    @Transactional
    public String addFace(Integer studentId,String imageBase64,String imageName,Date imageTime,String imagePath) throws Exception;

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
    public String addFace(Integer studentId,byte[] image,String imageName,Date imageTime,String imagePath) throws Exception;

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
    public String addFaceImageLib(String imagePath,String imageName,String faceId,Integer deptId,Integer cameraId,ImageType imageType,FaceResult faceResult,String resultMsg,Integer schoolId,Integer imageLibId,Date imageTime)
            throws Exception;

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
    public String addFaceImageLib(String imagePath,String imageName,String faceId,Integer deptId,Integer cameraId,ImageType imageType,FaceResult faceResult,String resultMsg,Integer schoolId,Date imageTime)
            throws Exception;

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
    public String addFaceImageLib(String imageId,byte[] image,String imageName,String faceId,Integer deptId,Integer cameraId,ImageType imageType,FaceResult faceResult,String resultMsg,Integer schoolId,Integer imageLibId,Date imageTime,String schoolCode)
            throws Exception;

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
    public String addFaceImageLib(String imageId,String imagePath,String imageName,String faceId,Integer deptId,Integer cameraId,ImageType imageType,FaceResult faceResult,String resultMsg,Integer schoolId,Date imageTime)
            throws Exception;

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
    public String addFaceImageLib(String imageId,String imagePath,String imageName,String faceId,Integer deptId,Integer cameraId,ImageType imageType,FaceResult faceResult,String resultMsg,Integer schoolId,Date imageTime,Integer imageLibId)
            throws Exception;

    /**
     * 保存人脸库
     * @param uuid 主键
     * @param personId 所属人员
     * @param personType 人员类型
     * @param schoolId 所属学校
     * @param deptId 学校的部门
     * @throws Exception 数据库异常
     */
    public void saveFaceLibrary(String uuid,Integer personId,PersonType personType,Integer schoolId,Integer deptId)
            throws Exception;

    /**
     * 人脸识别结果
     * @param schoolCode 学校编号
     * @param image 图片
     * @param errorMsg 错误消息
     * @return 识别结果 null表示识别错误
     */
    public List<ResultIde> predictFaceFromImage(String schoolCode, byte[] image,StringBuffer errorMsg);

    /**
     * 行为识别结果
     * @param schoolCode 学校编号
     * @param image 图片
     * @return 识别结果 null表示识别错误
     */
    public List<ResultIde> predictActionFromImage(String schoolCode,byte[] image,StringBuffer errorMsg);

    /**
     * 执行人脸培训，建议每天晚上执行一次
     * @return 培训结果
     */
    public boolean retrainFace();

    /**
     * 测试人脸服务是否启动
     * @return 测试结果
     */
    public boolean testServer();
}
