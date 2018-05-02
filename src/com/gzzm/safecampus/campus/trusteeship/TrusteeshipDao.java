package com.gzzm.safecampus.campus.trusteeship;

import com.gzzm.safecampus.campus.common.Node;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author czy
 */
public abstract class TrusteeshipDao extends GeneralDao
{

    @OQL("select t from TrusteeshipRoom t where t.name = :1 and t.deptId = :2")
    public abstract TrusteeshipRoom getTruRoomByName(String roomName, Integer deptId);

    @OQL("select troomId as nodeId, name as nodeName from TrusteeshipRoom where deptId =:1 and (deleteTag=0 or deleteTag is null)")
    public abstract List<Node> getRoomByDeptId(Integer deptId) throws Exception;

    @OQL("select studentId as nodeId, student.studentName as nodeName, troomId as parentId " +
            " from TrusteeshipStudent where deptId =:1 ")
    public abstract List<Node> getRoomStudentByDeptId(Integer deptId) throws Exception;

    @OQL("select t.ttId from TrusteeshipStudent t where t.studentId=:1")
    public abstract Integer selectByRoomIdAndStuId(Integer studentId);

    @OQL("select ts from TrusteeshipStudent ts where ts.troomId = :1 and ts.studentId = :2")
    public abstract TrusteeshipStudent getRoomByStu(Integer troomId, Integer studentId);
}
