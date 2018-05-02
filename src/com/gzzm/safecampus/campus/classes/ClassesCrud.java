package com.gzzm.safecampus.campus.classes;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.safecampus.campus.account.GradeDisplay;
import com.gzzm.safecampus.campus.base.BaseCrud;
import net.cyan.arachne.annotation.Forward;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.annotation.NotCondition;
import net.cyan.nest.annotation.Inject;

/**
 * 班级信息管理
 *
 * @author yuanfang
 * @date 18-02-05 17:53
 */
@Service(url = "/campus/classes/classescrud")
public class ClassesCrud extends BaseCrud<Classes, Integer>
{
    @Inject
    private TeacherDao teacherDao;

    @NotCondition
    private Integer gradeId;

    @Like
    private String classesName;

    @Like("master.teacherName")
    private String masterName;

    public ClassesCrud()
    {
        addOrderBy("orderId");
    }

    public String getMasterName()
    {
        return masterName;
    }

    public void setMasterName(String masterName)
    {
        this.masterName = masterName;
    }

    public Integer getGradeId()
    {
        return gradeId;
    }

    public void setGradeId(Integer gradeId)
    {
        this.gradeId = gradeId;
    }

    public String getClassesName()
    {
        return classesName;
    }

    public void setClassesName(String classesName)
    {
        this.classesName = classesName;
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return new String[]{"gradeId"};
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new ComplexTableView(new GradeDisplay(), "gradeId");
        view.addComponent("班级名称", "classesName");
        view.addComponent("班主任", "masterName");
        view.addColumn("班级名称", "allName");
        view.addColumn("班主任", "master.teacherName").setWidth("220");
        view.addColumn("班主任电话", "master.phone").setWidth("220");
        view.defaultInit(false);
        view.addButton(Buttons.sort());
        view.importJs("/safecampus/campus/classes/classes/classes.js");
        return view;
    }

    @Override
    @Forward(page = "/safecampus/campus/classes/classes/classes.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    public void initEntity(Classes entity) throws Exception
    {
        super.initEntity(entity);
        entity.setGradeId(gradeId);
    }

    @Override
    @Forward(page = "/safecampus/campus/classes/classes/classes.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (gradeId != null && gradeId > 0)
        {
            return "gradeId=?gradeId";
        }
        return super.getComplexCondition();
    }

    @Override
    public void afterSave() throws Exception
    {
        ClassTeacher master;
        if (isNew$())
        {
            //新增，将班主任add到科任老师表中
            master = new ClassTeacher();
            master.setClassesId(getEntity().getClassesId());
            master.setTeacherId(getEntity().getMasterId());
            master.setTeacherRole(TeacherRole.master);
        } else
        {
            //修改，更新班主任在科任老师中的记录
            master = teacherDao.getMaster(getEntity().getClassesId());
            master.setTeacherId(getEntity().getMasterId());
        }
        teacherDao.save(master);
        super.afterSave();
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }
}