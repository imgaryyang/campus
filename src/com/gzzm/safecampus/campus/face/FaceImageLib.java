package com.gzzm.safecampus.campus.face;

import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.base.BaseBean;
import com.gzzm.safecampus.campus.device.Camera;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.StringUtils;
import net.cyan.thunwind.annotation.*;

import java.util.*;

/**
 * 人脸图片库
 * @author zy
 * @date 2018/3/28 14:52
 */
@Entity(table = "SCFACEIMAGELIB" , keys = "imageId")
public class FaceImageLib extends BaseBean
{
    /**
     * 图片主键
     */
    @ColumnDescription(type = "varchar2(36)")
    private String imageId;

    /**
     * 图片名称
     */
    @ColumnDescription(type = "varchar2(500)")
    private String imageName;

    /**
     * 学校图片
     */
    @Transient
    private byte[] image;

    /**
     * 图片路径
     */
    @ColumnDescription(type = "varchar2(250)")
    private String imagePath;

    /**
     * 存放
     */
    @ColumnDescription(type = "varchar2(36)")
    private String pathType;

    /**
     * 图片类型
     */
    @Index
    private ImageType imageType;

    /**
     * 所属人脸
     */
    @ColumnDescription(type = "varchar2(36)")
    private String faceId;

    /**
     * 人脸库的图片id
     */
    @ColumnDescription(type = "number(9)")
    private Integer imageLibId;

    @ToOne("FACEID")
    @NotSerialized
    private FaceLibrary faceLibrary;

    /**
     * 识别结果
     */
    private FaceResult faceResult;

    /**
     *  相识度
     */
    @ColumnDescription(type = "varchar2(1000)")
    private String resultMsg;

    /**
     * 人脸识别结果
     */
    @NotSerialized
    @ComputeColumn("select s from FacePersonResult s where s.imageId=this.imageId order by s.resultId")
    private List<FacePersonResult> facePersonResultList;

    /**
     * 行为识别结果
     */
    @NotSerialized
    @ComputeColumn("select s from ActionPersonResult s where s.imageId=this.imageId order by s.resultId")
    private List<ActionPersonResult> actionPersonResultList;

    /**
     * 图片时间
     */
    private Date createTime;

    /**
     * 摄像头
     */
    private Integer cameraId;

    @ToOne("CAMERAID")
    @NotSerialized
    private Camera camera;

    /**
     * 所属学校
     */
    private Integer schoolId;

    @ToOne("SCHOOLID")
    @NotSerialized
    private School school;

    /**
     * 删除标志 1删除 0未删除
     */
    @ColumnDescription(type = "number(1)",defaultValue = "0")
    private Integer deleteTag;

    public FaceImageLib()
    {
    }

    public String getImageId()
    {
        return imageId;
    }

    public void setImageId(String imageId)
    {
        this.imageId = imageId;
    }

    public String getImageName()
    {
        return imageName;
    }

    public void setImageName(String imageName)
    {
        this.imageName = imageName;
    }

    public byte[] getImage()
    {
        return image;
    }

    public void setImage(byte[] image)
    {
        this.image = image;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    public String getPathType()
    {
        if(StringUtils.isBlank(pathType))
        {
            pathType=ImageType.Other.toString();
        }
        return pathType;
    }

    public void setPathType(String pathType)
    {
        this.pathType = pathType;
    }

    public ImageType getImageType()
    {
        return imageType;
    }

    public void setImageType(ImageType imageType)
    {
        this.pathType=imageType.toString();
        this.imageType = imageType;
    }

    public String getFaceId()
    {
        return faceId;
    }

    public void setFaceId(String faceId)
    {
        this.faceId = faceId;
    }

    public Integer getImageLibId()
    {
        return imageLibId;
    }

    public void setImageLibId(Integer imageLibId)
    {
        this.imageLibId = imageLibId;
    }

    public FaceLibrary getFaceLibrary()
    {
        return faceLibrary;
    }

    public void setFaceLibrary(FaceLibrary faceLibrary)
    {
        this.faceLibrary = faceLibrary;
    }

    public FaceResult getFaceResult()
    {
        return faceResult;
    }

    public void setFaceResult(FaceResult faceResult)
    {
        this.faceResult = faceResult;
    }

    public String getResultMsg()
    {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg)
    {
        this.resultMsg = resultMsg;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Integer getCameraId()
    {
        return cameraId;
    }

    public void setCameraId(Integer cameraId)
    {
        this.cameraId = cameraId;
    }

    public Camera getCamera()
    {
        return camera;
    }

    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }

    public Integer getSchoolId()
    {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId)
    {
        this.schoolId = schoolId;
    }

    public School getSchool()
    {
        return school;
    }

    public void setSchool(School school)
    {
        this.school = school;
    }

    public List<FacePersonResult> getFacePersonResultList()
    {
        return facePersonResultList;
    }

    public void setFacePersonResultList(List<FacePersonResult> facePersonResultList)
    {
        this.facePersonResultList = facePersonResultList;
    }

    public List<ActionPersonResult> getActionPersonResultList()
    {
        return actionPersonResultList;
    }

    public void setActionPersonResultList(
            List<ActionPersonResult> actionPersonResultList)
    {
        this.actionPersonResultList = actionPersonResultList;
    }

    public Integer getDeleteTag()
    {
        return deleteTag;
    }

    public void setDeleteTag(Integer deleteTag)
    {
        this.deleteTag = deleteTag;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof FaceImageLib)) return false;
        FaceImageLib that = (FaceImageLib) o;
        return Objects.equals(imageId, that.imageId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(imageId);
    }
}
