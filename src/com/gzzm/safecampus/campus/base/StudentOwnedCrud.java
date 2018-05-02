package com.gzzm.safecampus.campus.base;

import com.gzzm.platform.commons.Sex;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.StudentDao;
import com.gzzm.safecampus.campus.classes.StudentStatus;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Equals;
import net.cyan.crud.annotation.Like;
import net.cyan.nest.annotation.Inject;

/**
 * 学生所有者crud
 *
 * @author Neo
 * @date 2018/3/23 16:07
 */
@Service
public class StudentOwnedCrud<E extends BaseStudent, K> extends ClassesOwnedCrud<E, K>
{
    @Inject
    private StudentDao studentDao;

    /**
     * 学号查询条件
     */
    @Like("student.studentNo")
    private String studentNo;

    /**
     * 姓名查询条件
     */
    @Like("student.studentName")
    private String studentName;

    @Like("student.studentStatus")
    private StudentStatus studentStatus;

    @Equals("student.sex")
    private Sex sex;

    public StudentOwnedCrud()
    {
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

    public StudentStatus getStudentStatus()
    {
        return studentStatus;
    }

    public void setStudentStatus(StudentStatus studentStatus)
    {
        this.studentStatus = studentStatus;
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
     * 批量新增学生
     *
     * @param keys 学生Id
     * @throws Exception 操作异常
     */
    @Service(method = HttpMethod.post)
    public void addStudents(Integer[] keys) throws Exception
    {
        if (keys != null)
        {
            for (Integer key : keys)
            {
                Student student = studentDao.load(Student.class, key);
                if (student != null)
                {
                    E entity = getEntityType().newInstance();
                    entity.setDeptId(getDeptId());
                    entity.setClassesId(student.getClassesId());
                    entity.setStudentId(student.getStudentId());
                    boolean b = beforeAddStudent(entity);
                    if (b)
                    {
                        studentDao.save(entity);
                    }
                }
            }
        }
    }

    /**
     * 新增学生之前的操作
     *
     * @return 成功或失败
     * @throws Exception 操作异常
     */
    protected boolean beforeAddStudent(E entity) throws Exception
    {
        return true;
    }
}
