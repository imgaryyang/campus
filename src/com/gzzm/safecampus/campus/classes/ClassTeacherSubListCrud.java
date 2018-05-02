package com.gzzm.safecampus.campus.classes;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.campus.common.ScSubListCrud;
import com.gzzm.safecampus.campus.common.ScSubListView;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.*;

/**
 * 班级管理-班级科任老师子列表
 *
 * @author yuanfang
 * @date 18-03-19 20:02
 */
@Service
public class ClassTeacherSubListCrud extends ScSubListCrud<ClassTeacher, Integer>
{
    @Inject
    private ClassesDao classesDao;

    private Integer classesId;

    private SubjectListModel subjectListModel;

    private TeacherListModel teacherListModel;

    public ClassTeacherSubListCrud()
    {
    }

    public Integer getClassesId()
    {
        return classesId;
    }

    public void setClassesId(Integer classesId)
    {
        this.classesId = classesId;
    }

    @Select(field = {"entity.subjectId"})
    public SubjectListModel getSubjectListModel() throws Exception
    {
        Classes cla = classesDao.getClasses(classesId);
        if (subjectListModel == null)
            subjectListModel = Tools.getBean(SubjectListModel.class);
        if (cla.getGradeId() != null)
            subjectListModel.setGradeId(cla.getGradeId());
        return subjectListModel;
    }

    @Select(field = {"entity.teacherId"})
    public TeacherListModel getTeacherListModel() throws Exception
    {
        if (teacherListModel == null)
            teacherListModel = Tools.getBean(TeacherListModel.class);
        return teacherListModel;
    }

    @Override
    public String getOrderField()
    {
        //不需要排序功能
        return null;
    }

    @Override
    protected String getParentField()
    {
        return "classesId";
    }

    @Override
    public void initEntity(ClassTeacher entity) throws Exception
    {
        super.initEntity(entity);
        entity.setTeacherRole(TeacherRole.teacher);
    }

    @Override
    protected void initListView(ScSubListView view)
    {
        view.addColumn("老师", "teacher.teacherName");
        view.addColumn("科目", "subject.subjectName");
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "teacherRole<> 1";
    }

    @Override
    protected void initShowView(SimpleDialogView view)
    {
        view.setTitle("班级科任老师");
        view.addComponent("老师", "teacherId", true).setProperty("text", "${teacher.teacherName}");
        view.addComponent("科目", "subjectId", true).setProperty("text", "${subject.subjectName}");
    }
}
