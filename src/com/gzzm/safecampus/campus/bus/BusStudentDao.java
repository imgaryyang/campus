package com.gzzm.safecampus.campus.bus;

import com.gzzm.safecampus.campus.common.Node;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author czy
 */
public abstract class BusStudentDao extends GeneralDao
{

    @OQL("select busId as nodeId, busLicense as nodeName from BusInfo where deptId =:1 and (deleteTag=0 or deleteTag is null)")
    public abstract List<Node> getBusByDeptId(Integer deptId) throws Exception;

    @OQL("select studentId as nodeId, student.studentName as nodeName, busId as parentId from BusStudent " +
            "where deptId =:1")
    public abstract List<Node> getBusStudentByDeptId(Integer deptId) throws Exception;

    @OQL("select bs from BusStudent bs where bs.busId = :1 and bs.studentId = :2")
    public abstract BusStudent getBusStuByName(Integer busId, Integer studentId);

    @OQL("select bs from BusStudent bs where bs.busId = :1 and bs.studentId = :2")
    public abstract BusStudent getBusStuByAll(Integer busId, Integer studentId);

    @OQL("select b from BusStudent b where b.studentId = :1")
    public abstract List<BusStudent> getBusStudentByStu(Integer studentId);

}
