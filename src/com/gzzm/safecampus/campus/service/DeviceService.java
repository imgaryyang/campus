package com.gzzm.safecampus.campus.service;

import com.gzzm.safecampus.campus.device.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author liyabin
 * @date 2018/3/30
 */
public class DeviceService
{
    @Inject
    ServiceDao serviceDao;

    public List<WhiteListVersion> getWhiteListVersion(Integer deptId)
    {
        return serviceDao.getWhiteListVersion(deptId);
    }

    public DeviceService()
    {
    }

    public void upLevel(Integer[] AttendanceDeviceIds) throws Exception
    {
        List<AttendanceDevice> attendanceDevices = serviceDao.getAttendanceDevice(AttendanceDeviceIds);
        for(AttendanceDevice attendanceDevice :attendanceDevices)
        {
            Integer no = serviceDao.getMaxWhiteList(attendanceDevice.getSchoolId());
            if(no==null)no=0;
            no++;
            WhiteListVersion version = new WhiteListVersion();
            version.setSchoolId(attendanceDevice.getSchoolId());
            version.setDeptId(attendanceDevice.getDeptId());
            version.setEndTime(new Date());
            version.setVersionNo(no);
            version.setVersionDescriptive("一键升级！");
            serviceDao.save(version);
            attendanceDevice.setWhiteListVersion(version.getId());
        }
        serviceDao.saveEntities(attendanceDevices);
    }
}
