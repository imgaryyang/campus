package com.gzzm.safecampus.campus.tutorship;

import net.cyan.arachne.components.CheckBoxListModel;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.Collection;

/**
 * 课程多选控件
 *
 * @author yiuman
 * @date 2018/3/14
 */
public class CourseModel implements CheckBoxListModel<TutorCourse> {

    @Inject
    private Provider<TutorDao> daoProvider;


    @Override
    public boolean hasCheckBox(TutorCourse tutorCourse) throws Exception {
        return true;
    }

    @Override
    public Boolean isChecked(TutorCourse tutorCourse) throws Exception {
        return true;
    }

    @Override
    public Collection<TutorCourse> getItems() throws Exception {
        return daoProvider.get().getAllEntities(TutorCourse.class);
    }

    @Override
    public String getId(TutorCourse tutorCourse) throws Exception {
        return tutorCourse.getCourseId().toString();
    }

    @Override
    public String toString(TutorCourse tutorCourse) throws Exception {
        return tutorCourse.getCourseName();
    }
}
