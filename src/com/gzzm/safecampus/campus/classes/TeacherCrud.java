package com.gzzm.safecampus.campus.classes;

import com.gzzm.platform.addivision.AdDivision;
import com.gzzm.platform.addivision.AdDivisionDao;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.campus.base.BaseCrud;
import com.gzzm.safecampus.campus.common.SerialNoGenerator;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.Provider;
import net.cyan.crud.annotation.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.exporters.ExportUtils;
import net.cyan.crud.importers.CrudEntityImportor;
import net.cyan.crud.importers.DataRecord;
import net.cyan.crud.importers.ToOneColumnDataLoader;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.Column;
import net.cyan.crud.view.ExpressionColumn;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 教师管理
 *
 * @author yuanfang
 * @date 18-02-06 16:53
 */
@Service(url = "/campus/classes/teachercrud")
public class TeacherCrud extends BaseCrud<Teacher, Integer>
{
    @Inject
    private static Provider<FileUploadService> uploadServiceProvider;

    @Inject
    private TeacherDao teacherDao;

    @Inject
    private AdDivisionDao dao;

    /**
     * 姓名查询条件
     */
    @Like
    private String teacherName;

    /**
     * 职位查询条件
     */
    @Like
    private String job;

    /**
     * 性别查询条件
     */
    private Sex sex;

    @NotSerialized
    private String[] exportCols;

    public TeacherCrud()
    {
    }

    public String[] getExportCols()
    {
        return exportCols;
    }

    public void setExportCols(String[] exportCols)
    {
        this.exportCols = exportCols;
    }

    public String getTeacherName()
    {
        return teacherName;
    }

    public void setTeacherName(String teacherName)
    {
        this.teacherName = teacherName;
    }

    public String getJob()
    {
        return job;
    }

    public void setJob(String job)
    {
        this.job = job;
    }

    public Sex getSex()
    {
        return sex;
    }

    public void setSex(Sex sex)
    {
        this.sex = sex;
    }

    /**
     * 省份选择控件
     *
     * @return 省份选择控件
     * @throws Exception 操作异常
     */
    @NotSerialized
    @Select(field = {"entity.provinceId"})
    public List<AdDivision> getProvinces() throws Exception
    {
        return dao.getFirstLevelDivisions();
    }

    /**
     * 城市选择控件
     *
     * @return 城市选择控件
     * @throws Exception 操作异常
     */
    @SuppressWarnings("unchecked")
    @NotSerialized
    @Select(field = {"entity.cityId"})
    public List<AdDivision> getCitys() throws Exception
    {
        Integer provinceId = getEntity().getProvinceId();
        return provinceId == null ? Collections.EMPTY_LIST : dao.getAdDivisions(provinceId);
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("姓名", "teacherName");
        view.addComponent("职务", "job");
        view.addComponent("性别", "sex");

        view.addColumn("工号", "teacherNo").setWidth("150");
        view.addColumn("姓名", "teacherName").setWidth("120");
        view.addColumn("性别", "sex").setWidth("80");
        view.addColumn("入职时间", "entryTime").setAlign(Align.left).setWidth("120");
        view.addColumn("职务", "job").setWidth("100");
        view.addColumn("电话", "phone").setWidth("140");
        view.addColumn("籍贯", "province.divisionName+city.divisionName");
        view.addColumn("头像", "picture==null?'未录入':'已录入'").setWidth("80").setAlign(Align.center);
        view.defaultInit(false);
        view.addButton(new CButton("导出", "exportTeachers()")
                .setIcon(Buttons.getIcon("export")));
        view.addButton(Buttons.imp());
        return view;
    }

    /**
     * 获取教师教授：班级-课程，及教师详细信息
     */
    @Override
    @Forward(page = "/safecampus/campus/classes/teacher/teacher.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/safecampus/campus/classes/teacher/teacher.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    public String getTemplatePath()
    {
        return "/safecampus/campus/classes/teacher/教职工导入模板.xls";
    }


    @Override
    public boolean beforeInsert() throws Exception
    {
        //新增的时候生成老师序列号
        getEntity().setCampusSerialNo(SerialNoGenerator.getTeacherSerialNo());
        return super.beforeInsert();
    }

    @Service(url = "/campus/classes/teacherimage/{$0}")
    public byte[] getTeacherImage(Integer teacherId) throws Exception {
        if (teacherId == null)
            return new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/campus/icons/teacher.jpg"))).getBytes();
        Teacher teacher = getEntity(teacherId);
        return teacher.getPicture() == null ? new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/campus/icons/teacher.jpg"))).getBytes() : teacher.getPicture().getBytes();
    }

    @Override
    protected void initImportor(CrudEntityImportor<Teacher, Integer> importor) throws Exception
    {
        super.initImportor(importor);
        importor.addColumnMap("工号", "teacherNo");
        importor.addColumnMap("姓名", "teacherName");
        importor.addColumnMap("性别", "sex");
        importor.addColumnMap("职务", "job");
        importor.addColumnMap("入职时间", "entryTime");
        importor.addColumnMap("出生日期", "birthday");
        importor.addColumnMap("省份", "provinceId", new ToOneColumnDataLoader(AdDivision.class, "divisionId", "divisionName"));
        importor.addColumnMap("城市", "cityId", new ToOneColumnDataLoader(AdDivision.class, "divisionId", "divisionName"));
        importor.addColumnMap("电话", "phone");
        importor.addColumnMap("住址", "address");
        importor.addColumnMap("身份证号", "idCard");
        importor.addColumnMap("备注", "desc");
    }

    @Override
    protected CrudEntityImportor<Teacher, Integer> getCrudEntityImportor()
    {
        return new CrudEntityImportor<Teacher, Integer>()
        {
            @Override
            protected void save(Teacher entity, DataRecord record) throws Exception
            {
                Teacher teacher = new Teacher();
                String teacherNo = entity.getTeacherNo();
                String teacherName = entity.getTeacherName();
                Integer deptId = getDefaultDeptId();
                teacher.setTeacherNo(teacherNo);
                teacher.setTeacherName(teacherName);
                teacher.setSex(entity.getSex());
                teacher.setJob(entity.getJob());
                teacher.setEntryTime(entity.getEntryTime());
                teacher.setBirthday(entity.getBirthday());
                teacher.setPhone(entity.getPhone());
                teacher.setAddress(entity.getAddress());
                teacher.setDesc(entity.getDesc());
                teacher.setIdCard(entity.getIdCard());
                teacher.setDeptId(deptId);
                teacher.setProvinceId(entity.getProvinceId());
                teacher.setCityId(entity.getCityId());
                //相同教师(工号姓名相同的)则update
                Integer tid = teacherDao.getTeacherId(teacherNo, teacherName, deptId);
                //重复电话号码
                Integer repeatPhone = teacherDao.checkTeacherPhone(entity.getPhone(), deptId, tid);
                if (repeatPhone != null)
                    teacher.setPhone(null);
                //重复身份证号
                Integer repeatIdCard = teacherDao.checkTeacherIdCard(entity.getIdCard(), deptId, tid);
                if (repeatIdCard != null)
                    teacher.setIdCard(null);
                if (tid != null)
                {
                    //更新当前老师信息
                    teacher.setTeacherId(tid);
                    Tools.debug("import to update teacher");
                } else
                {
                    //不同教师，工号重复时，学号添加后缀标识
                    Integer s = teacherDao.checkTeacherNo(teacherNo, deptId, null);
                    if (s != null)
                    {
                        teacher.setTeacherNo(teacherNo + "-repeat");
                    }
                    //新增教师，生成序列号
                    teacher.setCampusSerialNo(SerialNoGenerator.getTeacherSerialNo());
                }
                dao.save(teacher);
            }
        };
    }

    @Service(url = "/campus/classes/teacher/export")
    public InputFile exportTeachers() throws Exception
    {
        List<Teacher> dataList = teacherDao.getTeachers(getDefaultDeptId());
        List<TeacherItem> items = TeacherItem.getItems();
        List<Column> columns = new ArrayList<Column>(items.size());
        for (TeacherItem item : items)
        {
            if (item != null)
            {
                columns.add(new ExpressionColumn(item.getName(), item.getField()));
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return ExportUtils.export(dataList, columns,
                new ExportParameters("教师通讯录" + sdf.format(new Date())),
                "xls");
    }
}
