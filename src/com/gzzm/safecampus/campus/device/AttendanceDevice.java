package com.gzzm.safecampus.campus.device;

import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.base.BaseBean;
import net.cyan.commons.validate.annotation.*;
import net.cyan.thunwind.annotation.*;

/**
 * 设备表-
 *
 * @author liyabin
 * @date 2018/3/30
 */
@Entity(table = "SCATTENDANCEDEVICE", keys = "deviceId")
public class AttendanceDevice extends BaseBean
{
    @Generatable(length = 9)
    private Integer deviceId;
    /**
     * 城市代码- 由于考勤机特殊性这里测试代码最好固定在配置文件
     */
    @ColumnDescription(type = "varchar(16)")
    private String cityCode;
    /**
     * 企业代码-可以对应学校
     */
    @MaxLen(value = 6)
    @ColumnDescription(type = "varchar(16)")
    private String enterpriseCode;
    /**
     * 考勤机IP
     */
    @ColumnDescription(type = "varchar(16)")
    private String ipAddress;
    /**
     * 考勤监听端口
     */
    @ColumnDescription(type = "varchar(16)")
    private String port;
    /**
     * 终端编号
     */
    @ColumnDescription(type = "varchar(16)")
    private String deviceNo;
    /**
     * 终端机编号
     */
    @ColumnDescription(type = "varchar(16)")
    private String deviceSn;
    /**
     * 所属学校
     */
    private Integer schoolId;

    @ToOne("SCHOOLID")
    private School school;
    /**
     * 通信版本号-通过提升改版本号来提升考勤机的读卡区域的一些参数-以及加密密钥之类初始化信息
     */
    @MaxVal(value = "255", message = "版本号不能大于256")
    @MinVal(value = "0", message = "版本号必须自然数")
    private Integer socketVersion;
    /**
     * 当前设备白名单版本号
     */
    private Integer whiteListVersion;

    @ToOne("WHITELISTVERSION")
    private WhiteListVersion whiteVersion;
    /**
     * 设备当前黑名单版本号
     */
    private Integer blackListVersion;
    /**
     * 设备状态
     */
    private AttendanceStates states;

    private Integer orderId;

    public AttendanceDevice()
    {
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public String getEnterpriseCode()
    {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode)
    {
        this.enterpriseCode = enterpriseCode;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public String getDeviceNo()
    {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo)
    {
        this.deviceNo = deviceNo;
    }

    public String getDeviceSn()
    {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn)
    {
        this.deviceSn = deviceSn;
    }

    public Integer getSocketVersion()
    {
        return socketVersion;
    }

    public void setSocketVersion(Integer socketVersion)
    {
        this.socketVersion = socketVersion;
    }

    public Integer getWhiteListVersion()
    {
        return whiteListVersion;
    }

    public void setWhiteListVersion(Integer whiteListVersion)
    {
        this.whiteListVersion = whiteListVersion;
    }

    public Integer getBlackListVersion()
    {
        return blackListVersion;
    }

    public void setBlackListVersion(Integer blackListVersion)
    {
        this.blackListVersion = blackListVersion;
    }

    public AttendanceStates getStates()
    {
        return states;
    }

    public void setStates(AttendanceStates states)
    {
        this.states = states;
    }

    public Integer getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId)
    {
        this.deviceId = deviceId;
    }

    public WhiteListVersion getWhiteVersion()
    {
        return whiteVersion;
    }

    public void setWhiteVersion(WhiteListVersion whiteVersion)
    {
        this.whiteVersion = whiteVersion;
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

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof AttendanceDevice)) return false;

        AttendanceDevice that = (AttendanceDevice) o;

        return deviceId != null ? deviceId.equals(that.deviceId) : that.deviceId == null;
    }

    @Override
    public int hashCode()
    {
        return deviceId != null ? deviceId.hashCode() : 0;
    }
}
