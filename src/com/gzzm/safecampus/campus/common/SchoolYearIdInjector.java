package com.gzzm.safecampus.campus.common;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.safecampus.campus.account.SchoolYear;
import net.cyan.nest.BeanContainer;
import net.cyan.nest.InjectAnnotationParser;
import net.cyan.nest.ValueFactory;

import java.lang.reflect.Member;
import java.lang.reflect.Type;

/**
 * @author Neo
 * @date 2018/3/24 19:36
 */
public class SchoolYearIdInjector implements InjectAnnotationParser<SchoolYearId>, ValueFactory
{
    public SchoolYearIdInjector()
    {
    }

    @Override
    public ValueFactory parse(SchoolYearId annotation)
    {
        return this;
    }

    @Override
    public Object getValue(Type fieldType, BeanContainer container, Object bean, Type beanType, String beanName, Member member) throws Exception
    {
        SchoolYearContainer schoolYearContainer = container.get(SchoolYearContainer.class);
        UserOnlineInfo userOnlineInfo = container.get(UserOnlineInfo.class);
        SchoolYear currentSchoolYear = schoolYearContainer.getCurrentSchoolYear(userOnlineInfo.getDeptId());
        return currentSchoolYear == null ? null : currentSchoolYear.getSchoolYearId();
    }
}
