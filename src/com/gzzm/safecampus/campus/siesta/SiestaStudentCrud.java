package com.gzzm.safecampus.campus.siesta;

import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.safecampus.campus.classes.Classes;
import com.gzzm.safecampus.campus.classes.ClassesDao;
import com.gzzm.safecampus.campus.classes.Grade;
import com.gzzm.safecampus.campus.classes.StudentDao;
import com.gzzm.safecampus.campus.base.StudentOwnedCrud;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.annotation.NotCondition;
import net.cyan.crud.importers.CrudEntityImportor;
import net.cyan.crud.importers.DataRecord;
import net.cyan.nest.annotation.Inject;

/**
 * 午休学生管理
 * Created by Huangmincong on 2018/3/12.
 */
@Service(url = "/campus/siesta/siestastu")
public class SiestaStudentCrud extends StudentOwnedCrud<SiestaStudent, Integer>
{
    @NotCondition
    private Integer sroomId;

    @Inject
    private SiestaDao siestaDao;

    @Inject
    private StudentDao studentDao;

    @Inject
    private ClassesDao classesDao;



    public SiestaStudentCrud()
    {
    }

    public Integer getSroomId()
    {
        return sroomId;
    }

    public void setSroomId(Integer sroomId)
    {
        this.sroomId = sroomId;
    }

    @Override
    protected String getComplexCondition0() throws Exception
    {
        if (sroomId == null || sroomId == 0)
            return "(siestaRoom.deleteTag=0 or siestaRoom.deleteTag is null)";
        return "sroomId=?sroomId";
    }

    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view = new ComplexTableView(new SiestaRoomListDisplay(), "sroomId");
        view.addComponent("学号", "studentNo");
        view.addComponent("学生姓名", "studentName");
        view.addComponent("年级", "gradeName");
        view.addComponent("班级", "classesName");
        view.addColumn("学号", "student.studentNo").setWidth("200px");
        view.addColumn("学生姓名", "student");
        view.addColumn("午休室", "siestaRoom");
        view.addColumn("(年级)班级", "classes.grade+classes.classesName");
        view.addButton(Buttons.query());
        view.addButton(Buttons.add());
        view.addButton(Buttons.imp());
        view.addButton(Buttons.delete());
        view.importJs("/safecampus/campus/siesta/siesta.js");
        return view;
    }

    @Override
    public String getTemplatePath()
    {
        return "/safecampus/campus/siesta/午休学生导入模板.xls";
    }

    @Override
    protected void initImportor(CrudEntityImportor<SiestaStudent, Integer> importor) throws Exception
    {
        super.initImportor(importor);
        importor.addColumnMap("午休室名称", "roomName");
        importor.addColumnMap("学号", "studentNo");
        importor.addColumnMap("学生姓名", "studentName");
        importor.addColumnMap("年级", "gradeName");
        importor.addColumnMap("班级", "className");
    }

    @Override
    protected CrudEntityImportor<SiestaStudent, Integer> getCrudEntityImportor() throws Exception
    {
        return new CrudEntityImportor<SiestaStudent, Integer>()
        {
            @Override
            protected void save(SiestaStudent entity, DataRecord record) throws Exception
            {
                //entity是excel每一行的内容
                //                SiestaStudent siestaStudent = new SiestaStudent();

                String gradeName = entity.getGradeName();
                String className = entity.getClassName();
                String studentNo = entity.getStudentNo();
                String studentName = entity.getStudentName();

                if (gradeName == null)
                    throw new NoErrorException("请填写年级信息");
                if (className == null)
                    throw new NoErrorException("请填写班级信息");
                if (studentNo == null || studentName == null)
                    throw new NoErrorException("请填写学生信息");

                Grade grade = classesDao.getGradeByName(entity.getGradeName(), getDefaultDeptId());
                if (grade != null)
                {
                    Classes cla = classesDao.getClassesByNameAndGradeId(entity.getClassName(), grade.getGradeId(), getDefaultDeptId());
                    if (cla != null)
                    {
                        Integer stuId = studentDao.getIdByStuNoAndCid(entity.getStudentNo(), entity.getStudentName(), cla.getClassesId(), getDefaultDeptId());
                        if (stuId != null)
                        {
                            entity.setStudentId(stuId);
                            Classes classes = classesDao.getByStudentId(stuId);
                            if (classes != null)
                            {
                                if (!classes.getClassesId().toString().equals(cla.getClassesId().toString()))
                                {
                                    throw new NoErrorException("[" + entity.getStudentName() + "]" + "学生与班级不匹配！");
                                }
                                entity.setClassesId(classes.getClassesId());
                                entity.setDeptId(getDefaultDeptId());
                                SiestaRoom siestaRoom = siestaDao.getSiestaRoomByName(entity.getRoomName(), getDefaultDeptId());
                                if (siestaRoom != null)
                                {
                                    entity.setSroomId(siestaRoom.getSroomId());
                                } else
                                {
                                    throw new NoErrorException("[" + entity.getRoomName() + "]" + "午休室不存在！");
                                }
                            } else
                            {
                                throw new NoErrorException("[" + entity.getStudentName() + "]" + "学生没有所属的班级！");
                            }
                        } else
                        {
                            throw new NoErrorException("[" + entity.getStudentName() + "]" + "学生不存在！");
                        }
                    } else
                    {
                        throw new NoErrorException("[" + entity.getGradeName() + "]" + "年级找不到" + "[" + entity.getClassName() + "]" + "班级");
                    }
                } else
                {
                    throw new NoErrorException("[" + entity.getGradeName() + "]" + "年级不存在！");
                }
                Integer ssId = siestaDao.selectByRoomIdAndStuId(entity.getStudentId());
                if (ssId != null)
                {
                    SiestaStudent siestaStudent = siestaDao.get(SiestaStudent.class, ssId);
                    if (siestaStudent == null)
                    {
                        siestaDao.save(entity);
                    } else
                    {
                        entity.setSsId(ssId);
                        siestaDao.update(entity);
                    }
                } else
                {
                    siestaDao.save(entity);
                }
            }
        };
    }

    @Override
    protected boolean beforeAddStudent(SiestaStudent entity) throws Exception
    {
        //午休学生判重
        entity.setSroomId(getSroomId());
        SiestaStudent siestaStudent = siestaDao.getsietaByStu(entity.getSroomId(), entity.getStudentId());
        return siestaStudent == null && super.beforeAddStudent(entity);
    }
}
