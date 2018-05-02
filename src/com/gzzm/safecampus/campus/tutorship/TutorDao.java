package com.gzzm.safecampus.campus.tutorship;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.annotation.OQLUpdate;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 课程Dao
 *
 * @author yiuman
 * @date 2018/3/14
 */
public abstract class TutorDao extends GeneralDao {

    @OQL("select c from CourseTypes c where c.courseId = ?1")
    public abstract List<CourseTypes> getCourseTypessByCId(Integer courseId);

    @OQL("select c.courseId from CourseTypes c where c.typeItemId in ?1 ")
    public abstract List<Integer> getCourseByItemIds(List<String> courseTypes);

    @OQLUpdate("delete  from CourseTypes c where c.courseId = ?1 ")
    public abstract void deleteCourseTypes(Integer key);

    @OQL("select c from TutorSubjectTypeItem c where c.itemName like ?1")
    public abstract List<TutorSubjectTypeItem> getItemsOrdName(String s);

    @OQL("select i from TutorSubjectTypeItem i where i.typeId=?1 order by i.typeItemId")
    public abstract List<TutorSubjectTypeItem> getItemsBytypeId(Integer integer);

    @OQL("select c from TeacherCourse c where c.teacherId = ?1")
    public abstract List<TeacherCourse> getTCoursesByTId(Integer teacherId);

    @OQLUpdate("delete  from TeacherCourse c where c.teacherId = ?1 ")
    public abstract void deleteTeacherCourse(Integer key);

    @OQL("select c.teacherId from TeacherCourse c where c.courseId in ?1 ")
    public abstract List<Integer> getTeachersByItemIds(List<String> teacherCourses);

    @OQL("select c.courseId from TeacherCourse c where c.teacherId = ?1")
    public abstract List<Integer> getCoursesIdByTId(Integer teacherId);

    @OQLUpdate("delete from TeacherCourse c where c.teacherId =?1")
    public abstract void deleteCourseTypesByTeacherId(Integer teacherId);

    @OQL("select c.typeItemId from CourseTypes c where c.courseId = ?1")
    public abstract List<Integer> getItemsByCourseId(Integer key);

    @OQLUpdate("delete from CourseTypes c where c.courseId =?1")
    public abstract void deleteItemsByCourseId(Integer courseId);

    @OQL("select c from TutorTeacher c  limit :1")
    public abstract List<TutorTeacher> getIndexTeachers(Integer max) throws Exception;

    @OQL("select c from TutorCourse c  limit :1")
    public abstract List<TutorCourse> getIndexCourses(Integer max) throws Exception;

    @OQL("select c from TutorInstitution c  limit :1")
    public abstract List<TutorInstitution> getIndexInstitutions(Integer max) throws Exception;

}
