package com.gzzm.safecampus.device.attendance.dao;

import com.gzzm.safecampus.campus.device.*;
import net.cyan.commons.util.DateUtils;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * @author liyabin
 * @date 2018/3/27
 */
public abstract class CommonDao extends GeneralDao
{
    @OQL("select t from DeviceCard t where t.status =1")
    public abstract List<DeviceCard> getDeviceCard();

    @OQL("select t from DeviceCard t where t.status <> 0 and t.schoolId=:1 and t.releaseTime >?2 and t.releaseTime<?3 order by t.cardSn,t.status desc ")
    public abstract List<DeviceCard> getDeviceCard(Integer schoolId, Date startTime, Date endTime);

    @OQL("select t from WhiteList t where t.schoolId =:1 order by orderId")
    public abstract WhiteListVersion getWhiteList(Integer schoolId);

    @OQL("select t from WhiteList t where t.schoolId in (select w.schoolId from School w where w.deptId =:1) order by orderId")
    public abstract WhiteListVersion getWhiteListByDeptId(Integer deptId);

    @OQL("select t from DeviceCard t where t.cardNo =:1 and t.status =1")
    public abstract DeviceCard getDeviceCardByNo(String CardNo);

    @OQL("select t from DeviceCard t where t.cardSn =:1 and t.status =1")
    public abstract DeviceCard getDeviceCardBySn(String CardSn);

    @OQL("select t from AttendanceDevice t where t.deviceNo =:1 and concat(t.cityCode,t.enterpriseCode) =:2")
    public abstract AttendanceDevice getAttendanceDevice(String deviceNo, String cityAndEnterpriseCode);

    @OQL("select count(*) from AttendanceLog t where t.logNo =:1 and t.deviceSn =:2 ")
    public abstract Integer repeatSendCount(Integer logNo,String deviceSn);

    public List<DeviceCard> getDeviceCard(AttendanceDevice attendanceDevice)
    {
        if (attendanceDevice == null) return null;
        WhiteListVersion whiteVersion = attendanceDevice.getWhiteVersion();
        if (whiteVersion == null) return null;
        if (whiteVersion.getUpListVersion() == null)
            return getDeviceCard(whiteVersion.getSchoolId(), null, whiteVersion.getEndTime());
        Date startTime = whiteVersion.getUpListVersion().getEndTime();
        Date endTime = whiteVersion.getEndTime();
        if (startTime.getTime() > endTime.getTime()) return null;
        return getDeviceCard(whiteVersion.getSchoolId(), DateUtils.toSQLDate(startTime), endTime);
    }
}
