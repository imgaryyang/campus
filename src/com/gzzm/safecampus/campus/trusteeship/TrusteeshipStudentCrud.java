package com.gzzm.safecampus.campus.trusteeship;

import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.safecampus.campus.classes.*;
import com.gzzm.safecampus.campus.base.StudentOwnedCrud;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.NotCondition;
import net.cyan.crud.importers.CrudEntityImportor;
import net.cyan.crud.importers.DataRecord;
import net.cyan.nest.annotation.Inject;

/**
 * 托管学生管理CRUD
 */
@Service(url = "/campus/trusteeship/student")
public class TrusteeshipStudentCrud extends StudentOwnedCrud<TrusteeshipStudent, Integer>
{
    @Inject
    private TrusteeshipDao trusteeshipDao;

    @Inject
    private StudentDao studentDao;

    @Inject
    private ClassesDao classesDao;

    @NotCondition
    private Integer troomId;

    public TrusteeshipStudentCrud()
    {
    }

    public Integer getTroomId()
    {
        return troomId;
    }

    public void setTroomId(Integer troomId)
    {
        this.troomId = troomId;
    }

    @Override
    protected String getComplexCondition0() throws Exception
    {
        if (troomId == null || troomId == 0)
        {
            //选择全部的需要过滤掉已删除
            return "(trusteeshipRoom.deleteTag=0 or trusteeshipRoom.deleteTag is null)";
        }
        return "troomId=?troomId";
    }

    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view = new ComplexTableView(new TrusteeshipRoomListDisplay(), "troomId");
        view.addComponent("学号", "studentNo");
        view.addComponent("学生姓名", "studentName");
        view.addComponent("年级", "gradeName");
        view.addComponent("班级", "classesName");
        view.addColumn("学号", "student.studentNo").setWidth("200px");
        view.addColumn("学生姓名", "student");
        view.addColumn("班级", "classes.grade+classes");
        view.addColumn("托管室名称", "trusteeshipRoom.name");
        view.addButton(Buttons.query());
        view.addButton(Buttons.add());
        view.addButton(Buttons.imp());
        view.addButton(Buttons.delete());
        view.importJs("/safecampus/campus/trusteeship/trusteeship.js");
        return view;
    }

    @Override
    public String getTemplatePath()
    {
        return "/safecampus/campus/trusteeship/托管管理_托管学生导入模板.xlsx";
    }

    @Override
    protected void initImportor(CrudEntityImportor<TrusteeshipStudent, Integer> importor) throws Exception
    {
        super.initImportor(importor);
        importor.addColumnMap("托管室名称", "roomName");
        importor.addColumnMap("学号", "studentNo");
        importor.addColumnMap("学生姓名", "studentName");
        importor.addColumnMap("年级", "gradeName");
        importor.addColumnMap("班级", "className");
    }

    @Override
    protected CrudEntityImportor<TrusteeshipStudent, Integer> getCrudEntityImportor() throws Exception
    {
        return new CrudEntityImportor<TrusteeshipStudent, Integer>()
        {
            @Override
            protected void save(TrusteeshipStudent entity, DataRecord record) throws Exception
            {
                String gradeName = entity.getGradeName();
                String className = entity.getClassName();
                String studentNo = entity.getStudentNo();
                String studentName = entity.getStudentName();
                String roomName = entity.getRoomName();

                if (gradeName == null)
                    throw new NoErrorException("请填写年级信息");
                if (className == null)
                    throw new NoErrorException("请填写班级信息");
                if (studentNo == null || studentName == null)
                    throw new NoErrorException("请填写学生信息");
                if (roomName == null)
                    throw new NoErrorException("请填写托管室信息");

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
                            entity.setClassesId(cla.getClassesId());
                            entity.setDeptId(cla.getDeptId());

                            TrusteeshipRoom room = trusteeshipDao.getTruRoomByName(entity.getRoomName(), getDefaultDeptId());
                            if (room != null)
                            {
                                entity.setTroomId(room.getTroomId());
                            } else
                            {
                                throw new NoErrorException("[" + entity.getRoomName() + "]" + "托管室不存在！");
                            }
                        } else
                        {
                            throw new NoErrorException("[" + entity.getStudentName() + "]" + "学生信息不匹配！");
                        }
                    } else
                    {
                        throw new NoErrorException("[" + entity.getClassName() + "]" + "班级信息不匹配！");
                    }
                } else
                {
                    throw new NoErrorException("[" + entity.getGradeName() + "]" + "年级信息不匹配！");
                }

                Integer ttId = trusteeshipDao.selectByRoomIdAndStuId(entity.getStudentId());
                if (ttId != null)
                {
                    TrusteeshipStudent trusteeshipStudent = trusteeshipDao.get(TrusteeshipStudent.class, ttId);
                    if (trusteeshipStudent == null)
                    {
                        trusteeshipDao.save(entity);
                    } else
                    {
                        entity.setTtId(ttId);
                        trusteeshipDao.update(entity);
                    }
                } else
                {
                    trusteeshipDao.save(entity);
                }

            }
        };
    }

    @Override
    protected boolean beforeAddStudent(TrusteeshipStudent entity) throws Exception
    {
        entity.setTroomId(getTroomId());
        TrusteeshipStudent trusteeshipStudent = trusteeshipDao.getRoomByStu(entity.getTroomId(), entity.getStudentId());
        return trusteeshipStudent == null && super.beforeAddStudent(entity);
    }
}
