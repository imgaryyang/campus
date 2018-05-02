package com.gzzm.safecampus.campus.base;

import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.Null;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.annotation.NotCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Neo
 * @date 2018/3/26 11:02
 */
@Service
public class ClassesOwnedCrud<E extends BaseClasses, K> extends BaseCrud<E, K>
{
    /**
     * 班级名称查询条件
     */
    @NotCondition
    protected String classesName;

    @NotCondition
    protected Integer classesId;

    @NotCondition
    private String gradeName;

    public ClassesOwnedCrud()
    {

    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getClassesName()
    {
        return classesName;
    }

    public void setClassesName(String classesName)
    {
        this.classesName = classesName;
    }

    public Integer getClassesId()
    {
        return classesId;
    }

    public void setClassesId(Integer classesId)
    {
        this.classesId = classesId;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        if(!Null.isNull(classesId)) {
            getEntity().setClassesId(classesId);

        }return super.beforeInsert();
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        List<String> conditions = new ArrayList<>();
        if (classesId != null && classesId != 0)
        {
            if (classesId < 0)
            {
                //选中年级节点，查询年级下的所有班级的数据
                conditions.add("classes.gradeId=" + (-classesId));
            } else
            {
                //选中班级节点,查询该班级的数据
                conditions.add("classesId=?classesId");
            }
        }
        if (StringUtils.isNotEmpty(classesName))
        {
            conditions.add("(classes.classesName like '%" + classesName + "%' or classes.grade.gradeName like '%" + classesName + "%')");
        }

        if(StringUtils.isNotEmpty(gradeName)){
            conditions.add("(classes.grade.gradeName like '%" + gradeName + "%')");
        }

        //拼接子类的复杂条件
        String complexCondition = getComplexCondition0();
        if (StringUtils.isNotEmpty(complexCondition))
        {
            conditions.add(complexCondition);
        }

        if (conditions.size() > 0)
        {
            StringBuilder condition = new StringBuilder();
            boolean first = true;
            for (String c : conditions)
            {
                if (first)
                {
                    first = false;
                } else
                {
                    condition.append(" and ");
                }
                condition.append(c);
            }
            return condition.toString();
        }
        return null;
    }

    protected String getComplexCondition0() throws Exception
    {
        return null;
    }
}
