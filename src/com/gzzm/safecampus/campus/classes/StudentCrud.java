package com.gzzm.safecampus.campus.classes;

import com.gzzm.platform.addivision.AdDivision;
import com.gzzm.platform.addivision.AdDivisionDao;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.campus.base.ClassesOwnedCrud;
import com.gzzm.safecampus.campus.common.SerialNoGenerator;
import com.gzzm.safecampus.wx.personal.WxAuthDao;
import com.gzzm.safecampus.wx.personal.WxStudent;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.Provider;
import net.cyan.crud.annotation.*;
import net.cyan.crud.importers.CrudEntityImportor;
import net.cyan.crud.importers.DataRecord;
import net.cyan.crud.importers.ToOneColumnDataLoader;
import net.cyan.crud.view.Align;
import net.cyan.nest.annotation.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

/**
 * 学生信息管理
 *
 * @author yuanfang
 * @date 18-02-07 10:46
 */
@Service(url = "/campus/classes/studentcrud")
public class StudentCrud extends ClassesOwnedCrud<Student, Integer>
{
    @Inject
    private StudentService studentService;

    @Inject
    private AdDivisionDao dao;

    @Inject
    private ClassesDao classesDao;

    @Inject
    protected static Provider<FileUploadService> uploadServiceProvider;

    @Inject
    private WxAuthDao wxAuthDao;

    private ClassesTreeDisplay classesTreeDisplay;

    /**
     * 姓名
     */
    @Like
    private String studentName;

    /**
     * 学号
     */
    @Like
    private String studentNo;

    /**
     * 性别
     */
    private Sex sex;

    /**
     * 入学时间
     */
    @Lower(column = "entryTime")
    private java.sql.Date timeStart;

    /**
     * 入学时间
     */
    @Upper(column = "entryTime")
    private java.sql.Date timeEnd;

    public StudentCrud()
    {
        addOrderBy("classes.orderId");
        addOrderBy("orderId");
    }

    public java.sql.Date getTimeStart()
    {
        return timeStart;
    }

    public void setTimeStart(java.sql.Date timeStart)
    {
        this.timeStart = timeStart;
    }

    public java.sql.Date getTimeEnd()
    {
        return timeEnd;
    }

    public void setTimeEnd(java.sql.Date timeEnd)
    {
        this.timeEnd = timeEnd;
    }

    public String getStudentNo()
    {
        return studentNo;
    }

    public void setStudentNo(String studentNo)
    {
        this.studentNo = studentNo;
    }

    public String getStudentName()
    {
        return studentName;
    }

    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }

    public Sex getSex()
    {
        return sex;
    }

    public void setSex(Sex sex)
    {
        this.sex = sex;
    }

    @Select(field = {"entity.classesId"})
    public ClassesTreeDisplay getClassesTreeDisplay() throws Exception
    {
        if (classesTreeDisplay == null)
            classesTreeDisplay = Tools.getBean(ClassesTreeDisplay.class);
        return classesTreeDisplay;
    }

    public void setClassesTreeDisplay(ClassesTreeDisplay classesTreeDisplay)
    {
        this.classesTreeDisplay = classesTreeDisplay;
    }

    @NotSerialized
    @Select(field = {"entity.provinceId"})
    public List<AdDivision> getProvinces() throws Exception
    {
        return dao.getFirstLevelDivisions();
    }

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
        PageTableView view = new ComplexTableView(new ClassesTreeDisplay(), "classesId");

        view.addComponent("姓名", "studentName");
        view.addComponent("学号", "studentNo");
        view.addComponent("性别", "sex");
        view.addMoreComponent("入学时间从", "timeStart");
        view.addMoreComponent("到", "timeEnd");

        view.addColumn("学号", "studentNo");
        view.addColumn("姓名", "studentName");
        view.addColumn("性别", "sex").setWidth("50");
        view.addColumn("班级", "classes.allName").setWidth("110");
        view.addColumn("联系方式", "phone").setWidth("125");
        view.addColumn("籍贯", "province.divisionName+city.divisionName").setWidth("130");
        view.addColumn("人脸库编号", "campusSerialNo").setWidth("120");
        view.addColumn("头像", "picture==null?'未录入':'已录入'").setWidth("70").setAlign(Align.center);
        view.defaultInit(false);
        view.addButton(Buttons.imp());
        view.addButton(Buttons.export("xls").setAction("exportStudents()"));
        view.importJs("/safecampus/campus/classes/student/student.js");
        return view;
    }

    @Override
    public String getTemplatePath()
    {
        return "/safecampus/campus/classes/student/学生导入模板.xls";
    }

    @Override
    @Forward(page = "/safecampus/campus/classes/student/student.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/safecampus/campus/classes/student/student.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    public void initEntity(Student entity) throws Exception
    {
        super.initEntity(entity);
        entity.setClasses(classesDao.getClasses(classesId));
        entity.setClassesId(classesId);
    }

    @Override
    protected void initImportor(CrudEntityImportor<Student, Integer> importor) throws Exception
    {
        super.initImportor(importor);
        importor.addColumnMap("学号", "studentNo");
        importor.addColumnMap("姓名", "studentName");
        importor.addColumnMap("性别", "sex");
        importor.addColumnMap("出生日期", "birthday");
        importor.addColumnMap("省份", "provinceId", new ToOneColumnDataLoader(AdDivision.class, "divisionId", "divisionName"));
        importor.addColumnMap("城市", "cityId", new ToOneColumnDataLoader(AdDivision.class, "divisionId", "divisionName"));
        importor.addColumnMap("电话", "phone");
        importor.addColumnMap("住址", "address");
        importor.addColumnMap("入学时间", "entryTime");
        importor.addColumnMap("身份证号", "idCard");
    }

    @Override
    protected CrudEntityImportor<Student, Integer> getCrudEntityImportor() throws Exception
    {
        return new CrudEntityImportor<Student, Integer>()
        {

            @Override
            protected void save(Student entity, DataRecord record) throws Exception
            {
                //保存学生的家属信息
                String guardianName = record.get(String.class, "家属姓名");
                String guardianRelation = record.get(String.class, "家属关系");
                String guardianPhone = record.get(String.class, "家属联系方式");
                //都为空，跳过
                if (StringUtils.isEmpty(guardianName) && StringUtils.isEmpty(guardianRelation) && StringUtils.isEmpty(guardianPhone))
                {
                    super.save(entity, record);
                    return;
                }
                String[] guardianNames = StringUtils.isEmpty(guardianName) ? new String[0] : guardianName.split(";");
                String[] guardianRelations = StringUtils.isEmpty(guardianRelation) ? new String[0] : guardianRelation.split(";");
                String[] guardianPhones = StringUtils.isEmpty(guardianPhone) ? new String[0] : guardianPhone.split(";");
                int nameCount = guardianNames.length;
                int relationCount = guardianRelations.length;
                int phoneCount = guardianPhones.length;
                int l = nameCount;
                if (relationCount > l)
                    l = relationCount;
                if (phoneCount > l)
                    l = phoneCount;
                List<Guardian> guardians = new ArrayList<>(l);
                for (int i = 0; i < l; i++)
                {
                    Guardian guardian = new Guardian();
                    guardian.setName(nameCount > i ? guardianNames[i] : null);
                    guardian.setRelationInfo(relationCount > i ? guardianRelations[i] : null);
                    guardian.setPhone(phoneCount > i ? guardianPhones[i] : null);
                    guardians.add(guardian);
                }
                entity.setGuardians(guardians);
                super.save(entity, record);

                //导入时关联微信用户
                for (Guardian guardian : guardians)
                {
                    //新增家属信息到微信用户-学生关联表
                    //根据手机查微信用户（家长）
                    if (guardian.getPhone() != null)
                    {
                        List<WxUser> wxUsers = wxAuthDao.getWxUserByPhone(guardian.getPhone());
                        if (CollectionUtils.isNotEmpty(wxUsers))
                            for (WxUser wxUser : wxUsers)
                            {
                                wxAuthDao.save(new WxStudent(wxUser.getWxUserId(), guardian.getStudentId(), new Date()));
                                Tools.debug("关联微信家长：" + wxUser.getNickName());
                            }
                    }
                }
            }
        };
    }

    /**
     * 导出学生信息
     */
    @Service(method = HttpMethod.post)
    public InputFile exportStudents() throws Exception
    {
        return studentService.exportStudent(classesId);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        //生成序号
        getEntity().setCampusSerialNo(SerialNoGenerator.getStudentSerialNo());
        return super.beforeInsert();
    }

    @Service(url = "/campus/classes/studentimage/{$0}")
    public byte[] getTeacherImage(Integer studentId) throws Exception
    {
        if (studentId == null)
            return new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/campus/icons/student.jpg"))).getBytes();
        Student student = getEntity(studentId);
        return student.getPicture() == null ? new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/campus/icons/student.jpg"))).getBytes() : student.getPicture().getBytes();
    }
}