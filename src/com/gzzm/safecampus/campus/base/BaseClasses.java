package com.gzzm.safecampus.campus.base;

import com.gzzm.safecampus.campus.classes.Classes;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.thunwind.annotation.ToOne;

/**
 * @author Neo
 * @date 2018/3/26 10:59
 */
public class BaseClasses extends BaseBean
{
    /**
     * 班级ID
     */
    protected Integer classesId;

    @NotSerialized
    @ToOne
    protected Classes classes;

    public BaseClasses()
    {
    }

    public Integer getClassesId()
    {
        return classesId;
    }

    public void setClassesId(Integer classesId)
    {
        this.classesId = classesId;
    }

    public Classes getClasses()
    {
        return classes;
    }

    public void setClasses(Classes classes)
    {
        this.classes = classes;
    }
}
