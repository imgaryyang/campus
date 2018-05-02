package com.gzzm.safecampus.campus.account;

import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.safecampus.campus.classes.Grade;
import com.gzzm.safecampus.campus.classes.Subject;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 学校账户相关业务处理类
 *
 * @author Neo
 * @date 2018/4/9 10:55
 */
public class SchoolService
{
    @Inject
    private SchoolDao dao;

    public SchoolService()
    {
    }

    /**
     * 给学校账户初始化年级、科目等数据
     *
     * @param school 学校账户信息
     * @throws Exception 操作异常
     */
    public void init(School school) throws Exception
    {
        //根据学校等级获取年级列表，遍历年级获取该年级下的科目
        List<Grade> grades = dao.getGradeByLevelId(school.getLevelId(), 1);
        if (grades == null || grades.size() == 0)
        {
            throw new NoErrorException("请先初始化年级！");
        }
        for (Grade grade : grades)
        {
            //拷贝年级到学校
            Grade g = new Grade();
            g.setLevelId(school.getLevelId());
            g.setGradeName(grade.getGradeName());
            g.setSchoolId(school.getSchoolId());
            g.setDeptId(school.getDeptId());
            g.setOrderId(grade.getOrderId());
            g.setSourceId(grade.getGradeId());
            dao.add(g);
            //拷贝科目到年级
            List<Subject> subjects = dao.getSubjectByGradeId(grade.getGradeId(), 1);
            for (Subject subject : subjects)
            {
                Subject s = new Subject();
                s.setGradeId(g.getGradeId());
                s.setDeptId(school.getDeptId());
                s.setSubjectCode(subject.getSubjectCode());
                s.setSubjectName(subject.getSubjectName());
                s.setOrderId(subject.getOrderId());
                dao.add(s);
            }
        }
    }
}
