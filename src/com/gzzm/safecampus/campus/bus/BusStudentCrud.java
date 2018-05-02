package com.gzzm.safecampus.campus.bus;

import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.safecampus.campus.classes.*;
import com.gzzm.safecampus.campus.base.StudentOwnedCrud;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.PropertyInfo;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.importers.CrudEntityImportor;
import net.cyan.crud.importers.DataRecord;
import net.cyan.crud.importers.EntityImportor;
import net.cyan.crud.importers.ToOneColumnDataLoader;
import net.cyan.nest.annotation.Inject;

import java.util.Objects;

/**
 * 校巴学生Crud类
 */
@Service(url = "/campus/bus/busstudent")
public class BusStudentCrud extends StudentOwnedCrud<BusStudent, Integer>
{
    @Inject
    private StudentDao studentDao;

    @Inject
    private ClassesDao classesDao;

    @Inject
    private GradeDao gradeDao;

    @Inject
    private BusDao busDao;

    @Inject
    private BusStudentDao busStudentDao;

    /**
     * busSiteId用于接收校巴id/站点id，需要手动拼接查询sql
     */
    private String busSiteId;

    /**
     * 站点名称查询条件
     */
    @Like("busSite.siteName")
    private String siteName;

    public BusStudentCrud()
    {
    }

    public String getBusSiteId()
    {
        return busSiteId;
    }

    public void setBusSiteId(String busSiteId)
    {
        this.busSiteId = busSiteId;
    }

    public String getSiteName()
    {
        return siteName;
    }

    public void setSiteName(String siteName)
    {
        this.siteName = siteName;
    }

    @Override
    protected String getComplexCondition0() throws Exception
    {
        //选中根节点或者没选中，加载全部
        if (busSiteId != null && !"0".equals(busSiteId))
        {
            //选中校巴节点
            if (!busSiteId.contains("-"))
            {
                //获取指定校巴的学生
                return "busId=" + (Integer.valueOf(busSiteId));
            } else
            {
                String[] busSiteIds = busSiteId.split("-");
                Integer busId = Integer.valueOf(busSiteIds[0]);
                Integer siteId = Integer.valueOf(busSiteIds[1]);
                //获取指定站点的学生
                return "busId=" + busId + " and siteId=" + siteId;
            }
        }
        return "(busInfo.deleteTag=0 or busInfo.deleteTag is null)";
    }

    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view = new ComplexTableView(Tools.getBean(BusSiteTreeModel.class), "busSiteId");
        view.addComponent("学号", "studentNo");
        view.addComponent("学生姓名", "studentName");
        view.addComponent("接送站点", "siteName");
        view.addColumn("学号", "student.studentNo").setWidth("200px");
        view.addColumn("学生姓名", "student").setWidth("150px");
        view.addColumn("班级", "classes.grade+classes").setWidth("150px");
        view.addColumn("校巴名称", "busInfo.busName");
        view.addColumn("接送站点", "busSite");
        view.addButton(Buttons.query());
        view.addButton(Buttons.add());
        view.addButton(Buttons.imp());
        view.importJs("/safecampus/campus/bus/busstudent.js");
        view.addButton(Buttons.delete());
        return view;
    }

    @Override
    public String getTemplatePath()
    {
        return "/safecampus/campus/bus/校巴管理_校巴学生信息导入模板.xlsx";
    }

    @Override
    protected void initImportor(CrudEntityImportor<BusStudent, Integer> importor) throws Exception
    {
        super.initImportor(importor);
        importor.addColumnMap("校巴", "busId", new ToOneColumnDataLoader(BusInfo.class, "busId", "busName")
        {
            @Override
            public Object load(PropertyInfo property, int index, DataRecord record, EntityImportor importor) throws Exception
            {
                String busName = record.get(String.class, "校巴");
                if (busName == null)
                    throw new NoErrorException("请填写校巴信息！");
                //查询当前学校的校巴
                BusInfo busInfo = busDao.getBusIdByBusName(busName, userOnlineInfo.getDeptId());
                if (busInfo == null)
                    throw new NoErrorException("[" + busName + "]" + "该校巴不存在！");
                return busInfo.getBusId();
            }
        });
        importor.addColumnMap("学号", "studentId", new ToOneColumnDataLoader(Student.class, "studentId", "studentName")
        {
            @Override
            public Object load(PropertyInfo property, int index, DataRecord record, EntityImportor importor) throws Exception
            {
                String studentNo = record.get(String.class, "学号");
                String studentName = record.get(String.class, "学生姓名");
                String busName = record.get(String.class, "校巴");
                String classesName = record.get(String.class, "班级");
                String gardeName = record.get(String.class, "年级");

                if (studentName == null || studentNo == null)
                    throw new NoErrorException("请填写学生信息！");

                if (classesName == null)
                    throw new NoErrorException("请填写班级信息！");

                if (gardeName == null)
                    throw new NoErrorException("请填写年级信息！");

                Grade grade = gradeDao.getGradeByName(gardeName, userOnlineInfo.getDeptId());

                if (grade == null)
                    throw new NoErrorException("年级信息不匹配！");

                Classes classes = classesDao.getClassesByGradeName(classesName, grade.getGradeId(), userOnlineInfo.getDeptId());

                if (classes == null)
                    throw new NoErrorException("[" + classesName + "]" + "班级信息不匹配！");

                //查询当前学校的学生
                Integer studentId = studentDao.getIdByStuNoAndCid(studentNo, studentName, classes.getClassesId(), userOnlineInfo.getDeptId());
                if (studentId == null)
                    throw new NoErrorException("[" + studentName + "." + studentNo + "]" + "学生信息不匹配！");

                //查询当前学校的校巴
                BusInfo busInfo = busDao.getBusIdByBusName(busName, userOnlineInfo.getDeptId());
                if (busInfo == null)
                    throw new NoErrorException("[" + busName + "]" + "该校巴不存在！");

                //查询该校巴上是否有该学生的信息
                BusStudent busStudent = busStudentDao.getBusStuByName(busInfo.getBusId(), studentId);

                //删除该校巴学生对象
                if (busStudent != null)
                    busStudentDao.delete(BusStudent.class, busStudent.getBusStudentId());

                return studentId;
            }
        });

        importor.addColumnMap("班级", "classesId", new ToOneColumnDataLoader(Classes.class, "classesId", "classesName")
        {
            @Override
            public Object load(PropertyInfo property, int index, DataRecord record, EntityImportor importor) throws Exception
            {
                String classesName = record.get(String.class, "班级");
                String gardeName = record.get(String.class, "年级");

                Grade grade = gradeDao.getGradeByName(gardeName, userOnlineInfo.getDeptId());

                if (grade == null)
                    throw new NoErrorException("年级信息不匹配！");

                Classes classes = classesDao.getClassesByGradeName(classesName, grade.getGradeId(), userOnlineInfo.getDeptId());

                if (classes == null)
                    throw new NoErrorException("[" + classesName + "]" + "班级信息不匹配！");
                return classes.getClassesId();
            }
        });
        importor.addColumnMap("接送点", "siteId", new ToOneColumnDataLoader(BusSite.class, "siteId", "siteName")
        {
            @Override
            public Object load(PropertyInfo property, int index, DataRecord record, EntityImportor importor) throws Exception
            {
                String siteName = record.get(String.class, "接送点");
                String busName = record.get(String.class, "校巴");

                if (siteName == null)
                    throw new NoErrorException("请填写站点信息！");

                //查询校巴站点
                BusSite busSite = busDao.getBusSite(busName, siteName, userOnlineInfo.getDeptId());
                if (busSite == null)
                    throw new NoErrorException("[" + siteName + "]" + "站点信息不匹配！");
                return busSite.getSiteId();
            }
        });
    }

    @Override
    public boolean beforeAddStudent(BusStudent busStudent) throws Exception
    {
        String[] busSiteIds = busSiteId.split("-");
        Integer busId = Integer.valueOf(busSiteIds[0]);
        Integer siteId = Integer.valueOf(busSiteIds[1]);
        busStudent.setBusId(busId);
        busStudent.setSiteId(siteId);
        BusStudent busStudent1 = busStudentDao.getBusStuByAll(busStudent.getBusId(), busStudent.getStudentId());
        if (busStudent1 != null)
        {
            if (Objects.equals(busStudent1.getSiteId(), busStudent.getSiteId()))
                return false;
            else
                busStudent.setBusStudentId(busStudent1.getBusStudentId());
        }
        return super.beforeAddStudent(busStudent);
    }
}
