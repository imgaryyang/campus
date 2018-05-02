package com.gzzm.safecampus.campus.classes;

import com.gzzm.safecampus.campus.base.DeptOwnedEntityPageList;

/**
 * 老师列表
 *
 * @author zy
 * @date 2018/3/18 12:29
 */
public class TeacherListModel extends DeptOwnedEntityPageList<Teacher, Integer>
{
    public TeacherListModel()
    {
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "(deleteTag=0 or deleteTag is null)";
    }

    @Override
    protected String getSearchCondition() throws Exception
    {
        return "teacherName like ?text or teacherNo like ?text ";
    }

    @Override
    public String toString(Teacher record) throws Exception
    {
        return record.getTeacherName() + " [" + record.getTeacherNo() + "]";
    }
}
