package com.gzzm.safecampus.device.card.dao;

import com.gzzm.platform.organ.*;
import com.gzzm.safecampus.campus.account.*;
import com.gzzm.safecampus.campus.classes.*;
import com.gzzm.safecampus.campus.device.*;
import com.gzzm.safecampus.campus.device.CardType;
import com.gzzm.safecampus.device.card.common.*;
import com.gzzm.safecampus.device.card.dto.*;
import com.gzzm.safecampus.device.card.entity.*;
import net.cyan.commons.util.*;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;

/**
 * 发卡器对接dao
 *
 * @author liyabin
 * @date 2018/3/21
 */
public abstract class CommonDao extends GeneralDao
{
    @OQL("select u from CardParam u where u.paramType =:1")
    public abstract CardParam getCardParamOne(Integer type);

    @OQL("select u from User u where u.userId =:1")
    public abstract User getUserByUserId(Integer userId);

    public abstract List<DeptDto> getChildDeptDto(Integer schoolId);

    @OQL("select t from School t where t.deptId =:1")
    public abstract School getSchool(Integer deptId);

    @OQL("select t from Dept t where t.deptCode =:1 and t.state = 0")
    public abstract Dept getDeptInfoByCode(String deptCode);

    @OQL("select t from Grade t where t.deptId =:1 order by orderId")
    public abstract List<Grade> getGradeList(Integer deptId);

    @OQL("select t from SchoolYear t where t.deptId =:1 order by orderId")
    public abstract SchoolYear getSchoolYearBydeptId(Integer deptId);

    @OQL("select t from IDType t")
    public abstract IDType findIDtype();

    @OQL("select t from Job t")
    public abstract Job findJob();

    @OQL("select t from GroupInfo t")
    public abstract GroupInfo findGroupInfo();

    @OQL("select t from Classes t where t.deptId =:1")
    public abstract List<Classes> getClassesList(Integer deptId, Integer yearId);

    @OQL("select t from Student t where (t.deleteTag = 0 or t.deleteTag is null)  and t.deptId=:1 " +
            "and t.studentId not in(select v.targetId from  DeviceCard v where v.type= 1 and v.status=1)")
    public abstract List<Student> searchStudents(Integer schoolId);

    @OQL("select t from Student t where (t.deleteTag = 0 or t.deleteTag is null) and t.studentNo =:2  and t.deptId=:1 " +
            "and t.studentId not in(select v.targetId from  DeviceCard v where v.type= 1 and v.status=1)")
    public abstract Student findOne(Integer schoolId, String studentNo);

    @OQL("select t from Student t where (t.deleteTag = 0 or t.deleteTag is null) and t.studentNo =:2  and t.deptId=:1")
    public abstract Student findStudent(Integer schoolId, String studentNo);

    @OQL("select t from Student t where (t.deleteTag = 0 or t.deleteTag is null)  and t.deptId=:1  " +
            "and t.studentId not in(select v.targetId from  DeviceCard v where v.type= 1 and v.status=1)")
    public abstract List<Student> searchStudents(Integer schoolId, Integer gradId);

    /**
     * 查未发卡用户
     *
     * @param studentNo
     * @param idCard
     * @param studentName
     * @param phone
     * @return
     */
    @OQL("select t from Student t where (t.deleteTag = 0 or t.deleteTag is null) and t.deptId=:5  and t.studentNo like ?1 and t.idCard" +
            " like ?2 and t.studentName like ?3 and t.phone like ?4 and" +
            " t.studentId not in(select v.targetId from  DeviceCard v where v.type= 1 and deptId=:5 and v.status=1)")
    public abstract List<Student> searchNoCardStudents(String studentNo, String idCard, String studentName,
                                                       String phone, Integer deptId);

    @OQL("select t from Student t where (t.deleteTag = 0 or t.deleteTag is null) and t.deptId=:5   and t.studentNo like ?1 and t.idCard" +
            " like ?2 and t.studentName like ?3 and t.phone like ?4 and t.studentId in " +
            "(select v.targetId from DeviceCard v where v.type= 1 and v.status=1 and deptId=:5)")
    public abstract List<Student> searchHasCardStudents(String studentNo, String idCard, String studentName,
                                                        String phone, Integer deptId);

    @OQL("select t from Student t where (t.deleteTag = 0 or t.deleteTag is null) and t.deptId=:5 and t.studentNo like ?1 and t.idCard" +
            " like ?2 and t.studentName like ?3 and t.phone like ?4")
    public abstract List<Student> searchStudents(String studentNo, String idCard, String studentName, String phone,
                                                 Integer deptId);

    @OQL("select t from DeviceCard t where t.deptId =:1 and t.type=1 and t.targetId =:2")
    public abstract List<DeviceCard> getDeviceCardList(Integer deptId, Integer targetId);

    /**
     * 根据部门id构建学校的部门树
     *
     * @param deptId
     * @return
     */
    public List<DeptDto> getDeptTree(Integer deptId)
    {
        School school = this.getSchool(deptId);
        SchoolYear year = this.getSchoolYearBydeptId(deptId);
        List<Grade> gradeList = this.getGradeList(deptId);
        if (year == null || gradeList == null || school == null) return null;
        List<Classes> classesList = this.getClassesList(deptId, year.getSchoolYearId());
        if (classesList == null) return null;
        List<DeptDto> deptDtos = new ArrayList<>();
        DeptDto deptDto = new DeptDto(String.valueOf(deptId), school.getSchoolName(), null);
        deptDtos.add(deptDto);
        for (Grade grade : gradeList)
        {
            DeptDto dto = new DeptDto(deptId + "_" + grade.getGradeId(), grade.getGradeName(), "" + deptId);
            deptDtos.add(dto);
            for (Classes classes : classesList)
            {
                if (classes.getGradeId().equals(grade.getGradeId()))
                {
                    DeptDto child = new DeptDto(deptId + "_" + grade.getGradeId() + "_" + classes.getClassesId(),
                            classes.getClassesName(), deptId + "_" + grade.getGradeId());
                    deptDtos.add(child);

                }
            }
        }
        return deptDtos;
    }

    /**
     * @param deptId      传过来的企业代码
     * @param deptDto     拼接的学校组织信息
     * @param emp_id      用户编号
     * @param emp_fname   用户姓名
     * @param mobilePhone 手机号
     * @param id_card     证件号
     * @param hadCard     是否发卡 0-全部；1-未发过卡；2-发过卡
     * @return
     */
    public List<EmpDto> getEmpList(Integer deptId, String deptDto,
                                   String emp_id, String emp_fname,
                                   String mobilePhone, String id_card,
                                   String hadCard) throws Exception
    {
        SchoolYear year = this.getSchoolYearBydeptId(deptId);
        if (year == null) return null;
        if (!StringUtils.isEmpty(emp_id)) emp_id = emp_id + "%";
        if (!StringUtils.isEmpty(emp_fname)) emp_fname = emp_fname + "%";
        if (!StringUtils.isEmpty(mobilePhone)) mobilePhone = mobilePhone + "%";
        if (!StringUtils.isEmpty(id_card)) id_card = id_card + "%";
        //部门树选择
        if (StringUtils.isEmpty(emp_id) && StringUtils.isEmpty(emp_fname) && StringUtils.isEmpty(mobilePhone) &&
                StringUtils.isEmpty(id_card) && !StringUtils.isEmpty(deptDto))
        {
            String[] notes = deptDto.split("_");
            if (notes.length == 1)//只有学校-查学生
            {
                List<Student> students = this.searchStudents(deptId);
                return createEmpDtosByStudents(students);
            } else if (notes.length == 2)//查学校和年级
            {
                String gradId = notes[1];
                List<Student> students = this.searchStudents(deptId, Integer.valueOf(gradId));
                return createEmpDtosByStudents(students);
            } else if (notes.length == 3)//查学校和年级和班级
            {
                String classesId = notes[2];
                Classes classes = load(Classes.class, classesId);
                List<Student> students = classes.getStudents();
                return createEmpDtosByStudents(students);
            } else
            {
                return null;
            }
        } else
        {
            if (StringUtils.isEmpty(emp_id)) emp_id = null;
            if (StringUtils.isEmpty(emp_fname)) emp_fname = null;
            if (StringUtils.isEmpty(mobilePhone)) mobilePhone = null;
            if (StringUtils.isEmpty(id_card)) id_card = null;
            if ("1".equals(hadCard))
            {
                List<Student> students = searchNoCardStudents(emp_id, id_card, emp_fname, mobilePhone, deptId);
                return createEmpDtosByStudents(students);
            } else if ("2".equals(hadCard))
            {
                List<Student> students = searchHasCardStudents(emp_id, id_card, emp_fname, mobilePhone, deptId);
                return createEmpDtosByStudents(students);
            } else
            {
                List<Student> students = searchStudents(emp_id, id_card, emp_fname, mobilePhone, deptId);
                return createEmpDtosByStudents(students);
            }
        }
    }

    private List<EmpDto> createEmpDtosByStudents(List<Student> students) throws Exception
    {
        List<EmpDto> listBean =
                ReflectionUtils
                        .createListBean(EmpDto.class, students, Student.class, new OptionInterface<EmpDto, CardType>()
                        {
                            @Override
                            public void pushBefore(EmpDto bean, CardType type)
                            {
                                if ("0".equals(bean.getSex()))
                                    bean.setSex("男");
                                else
                                    bean.setSex("女");
                                bean.setEmpId(type.ordinal() + "_" + bean.getEmpId());
                            }
                        }, CardType.STUDENT);
        return listBean;
    }

}
