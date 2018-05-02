package com.gzzm.safecampus.campus.classes;

import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.account.SchoolDao;
import com.gzzm.safecampus.campus.base.DeptOwnedTreeDisplay;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 学校-年级-班级树形图
 * 根节点为0 负数为一级节点（年级:gradeId） 正数为二级节点（班级:classesId）
 *
 * @author yuanfang
 * @date 18-02-06 9:54
 */
public class ClassesTreeDisplay extends DeptOwnedTreeDisplay<Classes, Integer>
{
    //使用静态provider才能先于构造方法注入dao
    @Inject
    private static Provider<SchoolDao> daoProvider;

    @Inject
    private static Provider<GradeDao> gradeDaoProvider;

    @Inject
    private static Provider<ClassesDao> classesDaoProvider;

    public ClassesTreeDisplay()
    {
    }

    @Override
    public Classes getRoot() throws Exception
    {
        School school = daoProvider.get().getSchool(getDeptId());
        Classes root = new Classes();
        root.setClassesId(0);
        root.setClassesName(school.getSchoolName());
        return root;
    }

    @Override
    public boolean hasChildren(Classes parent) throws Exception
    {
        return getChildren(parent.getClassesId()).size() > 0;
    }

    @Override
    public List<Classes> getChildren(Integer parentKey) throws Exception
    {
        //学校节点
        if (parentKey == 0)
        {
            //获取当前学校下的所有年级
            List<Grade> gradeList = gradeDaoProvider.get().getChildren(getDeptId());
            List<Classes> classeses = new ArrayList<>(gradeList.size());
            for (Grade grade : gradeList)
            {
                classeses.add(new Classes(-grade.getGradeId(), grade.getGradeName()));
            }
            return classeses;
        }
        //年级节点
        if (parentKey < 0)
        {
            //获取年级下的所有班级
            return classesDaoProvider.get().getGradeClasses0(-parentKey);
        }
        return Collections.emptyList();
    }

    @Override
    protected String getTextField()
    {
        return "classesName";
    }

    @Override
    public boolean supportSearch()
    {
        return false;
    }
}
