package com.gzzm.safecampus.wx.tutorship;

import com.gzzm.safecampus.campus.tutorship.TutorCourse;
import com.gzzm.safecampus.campus.tutorship.TutorDao;
import com.gzzm.safecampus.campus.tutorship.TutorInstitution;
import com.gzzm.safecampus.campus.tutorship.TutorTeacher;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 微信课外辅导
 *
 * @author yuanfang
 * @date 18-03-27 14:38
 */
@Service
public class WxTutorship
{
    @Inject
    private TutorDao tutorDao;

    /**
     * 课外辅导--名师
     */
    private List<TutorTeacher> tutorTeachers;

    /**
     * 课外辅导--机构
     */
    private List<TutorInstitution> tutorInstitutions;

    /**
     * 课外辅导--课程
     */
    private List<TutorCourse> tutorCourses;

    public List<TutorTeacher> getTutorTeachers()
    {
        return tutorTeachers;
    }

    public List<TutorInstitution> getTutorInstitutions()
    {
        return tutorInstitutions;
    }

    public List<TutorCourse> getTutorCourses()
    {
        return tutorCourses;
    }

    /**
     * 课外辅导 - 主页
     * @return 课外辅导 - 主页
     * @throws Exception 数据库异常
     * */
    @Service(url = "/wx/tutorship/index")
    public String secondClassIndex() throws Exception
    {
        tutorTeachers = tutorDao.getIndexTeachers(3);
        tutorCourses = tutorDao.getIndexCourses(3);
        tutorInstitutions = tutorDao.getIndexInstitutions(3);
        return "/safecampus/wx/tutorship/index.ptl";
    }

    /**
     * 课外辅导-展开更多页面
     * @param type 类型
     * @return 更多子页面
     * @throws Exception 数据库异常
     */
    @Service(url = "/wx/tutorship/more/{$0}")
    public String secondClassMore(Integer type) throws Exception
    {
        switch (type){
            case 0:
                tutorTeachers = tutorDao.getAllEntities(TutorTeacher.class);
                break;
            case 1:
                tutorCourses = tutorDao.getAllEntities(TutorCourse.class);
                break;
            case 2:
                tutorInstitutions = tutorDao.getAllEntities(TutorInstitution.class);
                break;
        }
        return "/safecampus/wx/tutorship/more.ptl";
    }

}
