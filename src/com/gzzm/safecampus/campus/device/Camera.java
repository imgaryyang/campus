package com.gzzm.safecampus.campus.device;

import com.gzzm.safecampus.campus.base.BaseBean;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.thunwind.annotation.ColumnDescription;
import net.cyan.thunwind.annotation.Entity;
import net.cyan.thunwind.annotation.Generatable;

/**
 * 摄像头信息
 *
 * @author Neo
 * @date 2018/3/29 10:07
 */
@Entity(table = "SCCAMERA", keys = "cameraId")
public class Camera extends BaseBean
{
    @Generatable(length = 7)
    private Integer cameraId;

    /**
     * 名称
     */
    @ColumnDescription(type = "VARCHAR2(250)")
    @Require
    private String cameraName;

    /**
     * 摄像头编号
     */
    @ColumnDescription(type = "VARCHAR2(100)")
    @Require
    private String cameraNum;

    /**
     * 用途
     */
    @Require
    private Purpose purpose;

    public Camera()
    {
    }

    public Integer getCameraId()
    {
        return cameraId;
    }

    public void setCameraId(Integer cameraId)
    {
        this.cameraId = cameraId;
    }

    public String getCameraName()
    {
        return cameraName;
    }

    public void setCameraName(String cameraName)
    {
        this.cameraName = cameraName;
    }

    public String getCameraNum()
    {
        return cameraNum;
    }

    public void setCameraNum(String cameraNum)
    {
        this.cameraNum = cameraNum;
    }

    public Purpose getPurpose()
    {
        return purpose;
    }

    public void setPurpose(Purpose purpose)
    {
        this.purpose = purpose;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Camera)) return false;

        Camera camera = (Camera) o;

        return cameraId.equals(camera.cameraId);

    }

    @Override
    public int hashCode()
    {
        return cameraId.hashCode();
    }
}
