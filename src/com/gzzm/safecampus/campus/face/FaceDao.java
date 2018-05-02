package com.gzzm.safecampus.campus.face;

import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.device.Camera;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 人脸识别持久层
 * @author zy
 * @date 2018/3/29 20:24
 */
public abstract class FaceDao extends GeneralDao
{
    public FaceDao()
    {
    }

    /**
     * 根据学生学号获取学校学生
     * @param schoolCode 学校编号
     * @param campusSerialNo 学生唯一标识号
     * @return 学生
     * @throws Exception 数据库异常
     */
    @OQL("select s from com.gzzm.safecampus.campus.classes.Student s where s.campusSerialNo=:2 and (select 1 from com.gzzm.safecampus.campus.account.School h where h.deptId=s.deptId and h.schoolCode=:1) is not empty limit 1")
    public abstract Student getStudent(String schoolCode, String campusSerialNo) throws Exception;

    /**
     * 摄像头
     * @param cameraNum 设备
     * @return 摄像头
     * @throws Exception 数据库异常
     */
    @OQL("select c from com.gzzm.safecampus.campus.device.Camera c where c.cameraNum=:1 limit 1")
    public abstract Camera getCameraByNum(String cameraNum) throws Exception;

    /**
     * 获取学校
     * @param deptId 所属部门
     * @return 学校
     * @throws Exception 数据库异常
     */
    @OQL("select s from com.gzzm.safecampus.campus.account.School s where s.deptId=:1 and s.schoolCode is not null order by s.schoolId desc limit 1")
    public abstract School getSchoolByDeptId(Integer deptId) throws Exception;

    /**
     * 查询人脸是否已经入库
     * @param deptId 部门主键
     * @param personId 人员主键
     * @param personType 人员类型
     * @return 人脸库
     * @throws Exception 数据库异常
     */
    @OQL("select f from com.gzzm.safecampus.campus.face.FaceLibrary f where f.deptId=:1 and f.personType=:3 and f.personId=:2 limit 1")
    public abstract FaceLibrary getFaceLibrary(Integer deptId,Integer personId,PersonType personType) throws Exception;

    /**
     * 获取人脸库图片
     * @param faceId 所属用户
     * @return 人脸库图片
     * @throws Exception
     */
    @OQL("select f from com.gzzm.safecampus.campus.face.FaceImageLib f where f.faceId=:1 and f.imageType=0 and (f.deleteTag is null or f.deleteTag=0) order by f.imageLibId")
    public abstract List<FaceImageLib> getFaceImageLibs(String faceId) throws Exception;

    /**
     * 查询客户端图片是否已上传过
     * @param faceId 所属用户
     * @param imageName 图片名称
     * @param imageType 图片类型
     * @return 人脸图片
     * @throws Exception
     */
    @OQL("select f from com.gzzm.safecampus.campus.face.FaceImageLib f  where f.faceId=:1 and f.imageName=:2 and f.imageType=:3 and  (f.deleteTag is null or f.deleteTag=0) order by f.imageLibId")
    public abstract FaceImageLib getClientFaceImage(String faceId,String imageName,ImageType imageType) throws Exception;

    /**
     * 查询入库的人脸图片
     * @param faceId 人脸主键
     * @param imageType 图片类型
     * @return 人脸记录图片
     * @throws Exception
     */
    @OQL("select f from com.gzzm.safecampus.campus.face.FaceImageLib f  where f.faceId=:1 and f.faceResult>0 and f.imageType=:2 and (f.deleteTag is null or f.deleteTag=0) order by f.createTime desc")
    public abstract FaceImageLib getFaceImageByFaceId(String faceId,ImageType imageType) throws Exception;
}
