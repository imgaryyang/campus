package com.gzzm.safecampus.campus.classes;

import com.gzzm.safecampus.campus.base.DeptOwnedEntityPageList;

/**
 * 课程列表，用于选择科任老师
 *
 * @author myf
 * @date 2018/3/19 12:29
 */
public class SubjectListModel extends DeptOwnedEntityPageList<Subject, Integer>
{
    private Integer gradeId;

    public SubjectListModel()
    {
    }

    public Integer getGradeId()
    {
        return gradeId;
    }

    public void setGradeId(Integer gradeId)
    {
        this.gradeId = gradeId;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "subject.subjectName like ?text and gradeId =?gradeId";
    }

    @Override
    public String toString(Subject record) throws Exception
    {
        return record.toString();
    }

    @Override
    public String getId(Subject subject) throws Exception
    {
        return subject.getSubjectId() + "";
    }
}
