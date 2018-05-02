package com.gzzm.safecampus.campus.account;

import com.gzzm.safecampus.campus.base.DeptOwnedTreeDisplay;
import com.gzzm.safecampus.campus.classes.Grade;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 学校等级 - 年级树左侧展示控件
 * 一级节点为学校等级，二级节点为该等级下的年级
 * 根节点为0，一级节点id为负数，二级节点为正数
 *
 * @author Neo
 * @date 2018/4/9 11:41
 */
public class GradeDisplay extends DeptOwnedTreeDisplay<Grade, Integer>
{
    @Inject
    private static Provider<SchoolDao> schoolDaoProvider;

    public GradeDisplay()
    {
    }

    @Override
    public Grade getRoot() throws Exception
    {
        Grade root = new Grade();
        //平台管理部门能看到所有等级的学校的年级
        if (getDeptId() == 1)
        {
            root.setGradeName("全部");
            root.setGradeId(0);
        } else
        {
            //非平台管理部门仅能看到当前学校下的年级
            School school = schoolDaoProvider.get().getSchool(getDeptId());
            root.setGradeName(school.getSchoolName());
            root.setGradeId(-school.getLevelId());
        }
        return root;
    }

    @Override
    public List<Grade> getChildren(Integer gradeId) throws Exception
    {
        if (gradeId == 0)
        {
            //加载学校等级节点
            List<SchoolLevel> schoolLevels = schoolDaoProvider.get().getSchoolLevel();
            List<Grade> grades = new ArrayList<>(schoolLevels.size());
            for (SchoolLevel level : schoolLevels)
            {
                Grade grade = new Grade();
                grade.setGradeId(-level.getLevelId());
                grade.setGradeName(level.getLevelName());
                grades.add(grade);
            }
            return grades;
        }
        if (gradeId < 0)
        {
            return schoolDaoProvider.get().getGradeByLevelId(-gradeId, getDeptId());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasChildren(Grade parent) throws Exception
    {
        List<Grade> children = getChildren(parent.getGradeId());
        return children != null && children.size() > 0;
    }
}
