package com.gzzm.safecampus.campus.classes;

import com.gzzm.safecampus.campus.common.Node;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

import java.util.*;

/**
 * @author yuanfang
 * @date 18-02-06 10:00
 */
public abstract class GradeDao extends GeneralDao
{
    public GradeDao()
    {
    }

    @OQL("select schoolId from Grade where gradeId =:1")
    public abstract Integer getSchool(Integer parentId) throws Exception;

    @OQL("select gradeId from Grade where schoolId =:2 and parentId =:1")
    public abstract Integer getGradeId(int i, Integer schoolId) throws Exception;

    @OQL("select gradeId from Grade  where schoolId =:1")
    public abstract List<Integer>  getGradeId(Integer schoolId) throws Exception;

    @OQL("select gradeId from Grade  where deptId =:1")
    public abstract List<Integer>  getGradeIdByDept(Integer deptId) throws Exception;

    @OQL("select schoolId from Grade where gradeId =:1")
    public abstract Integer getSchoolId(Integer gradeId) throws Exception;

    @OQL("select gradeName from Grade where gradeId =:1")
    public abstract String getGradeName(Integer gradeId) throws Exception;

    @OQL("select gradeId as nodeId, gradeName as nodeName from Grade where deptId =:1 and (deleteTag=0 or deleteTag is null)")
    public abstract List<Node> getGradeByDept(Integer schoolId) throws Exception;

    @OQL("select g from Grade g where deptId =:1 and (g.deleteTag=0 or g.deleteTag is null) order by g.orderId")
    public abstract List<Grade> getChildren(Integer deptId) throws Exception;

    @GetByField({"gradeName", "deptId"})
    public abstract Grade getGradeByName(String gardeName, Integer deptId) throws Exception;
}
