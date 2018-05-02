package com.gzzm.safecampus.campus.service;

import com.gzzm.platform.organ.Role;
import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.device.*;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 服务持久层
 *
 * @author zy
 * @date 2018/3/12 17:52
 */
public abstract class ServiceDao extends GeneralDao
{
    public ServiceDao()
    {
    }

    /**
     * 获取服务类型列表
     *
     * @return 服务类型
     * @throws Exception
     */
    @OQL("select t from com.gzzm.safecampus.campus.service.ServiceType t order by t.orderId")
    public abstract List<ServiceType> getServiceTypeList() throws Exception;

    /**
     * 获取有服务的服务类型
     *
     * @return 服务类型
     * @throws Exception
     */
    @OQL("select t from com.gzzm.safecampus.campus.service.ServiceType t where t.typeId in (select s.typeId from com.gzzm.safecampus.campus.service.ServiceInfo s) order by t.orderId")
    public abstract List<ServiceType> getServiceTypeListByNoNull() throws Exception;

    @OQL("select r from com.gzzm.platform.organ.Role r where r.roleName like ?2")
    public abstract List<Role> searchRoles(String text) throws Exception;

    @OQL("select r from com.gzzm.platform.organ.Role r where r.parentRoleId=0 or r.parentRoleId is null order by r.roleId")
    public abstract List<Role> getRootSubRole() throws Exception;

    @OQL("select d from WhiteListVersion d where (select 1 from Dept l,Dept r where l.leftValue>=r.leftValue and l.rightValue<=r.rightValue and l.deptId=d.deptId and r.deptId=?1) is not empty order By orderId")
    public abstract List<WhiteListVersion> getWhiteListVersion(Integer deptId);

    @OQL("select t from School t where t.deptId =:1")
    public abstract School getSchool(Integer deptId);

    @OQL("select t from AttendanceDevice t where t.deviceId  in(:1)")
    public abstract  List<AttendanceDevice> getAttendanceDevice(Integer [] ids);

    @OQL("select max(versionNo) from WhiteListVersion t where t.schoolId =:1")
    public abstract Integer getMaxWhiteList(Integer schoolId);
}
