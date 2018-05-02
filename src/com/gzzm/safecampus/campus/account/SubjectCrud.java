package com.gzzm.safecampus.campus.account;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import com.gzzm.safecampus.campus.base.BaseCrud;
import com.gzzm.safecampus.campus.classes.Subject;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.annotation.NotCondition;

/**
 * 各个年级的科目维护
 *
 * @author Neo
 * @date 2018/4/9 11:54
 */
@Service(url = "/campus/account/subject")
public class SubjectCrud extends BaseCrud<Subject, Integer>
{
    /**
     * 年级Id
     * 0为根节点
     * 负数为学校等级Id
     * 正数为年级Id
     */
    @NotCondition
    private Integer gradeId;

    /**
     * 科目名称查询条件
     */
    @Like
    private String subjectName;

    public SubjectCrud()
    {
        addOrderBy("orderId");
    }

    public Integer getGradeId()
    {
        return gradeId;
    }

    public void setGradeId(Integer gradeId)
    {
        this.gradeId = gradeId;
    }

    public String getSubjectName()
    {
        return subjectName;
    }

    public void setSubjectName(String subjectName)
    {
        this.subjectName = subjectName;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return new String[]{"gradeId"};
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (gradeId == null || gradeId == 0)
            return null;
        if (gradeId < 0)
        {
            //加载某个等级下的所有科目
            return "grade.levelId=" + (-gradeId);
        }
        if (gradeId > 0)
        {
            //加载指定年级下的科目
            return "gradeId=?gradeId";
        }
        return super.getComplexCondition();
    }

    @Override
    public void initEntity(Subject entity) throws Exception
    {
        super.initEntity(entity);
        entity.setGradeId(gradeId);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new ComplexTableView(new GradeDisplay(), "gradeId");
        view.addComponent("科目名称", "subjectName");
        view.addColumn("年级", "grade.gradeName");
        view.addColumn("科目名称", "subjectName");
        view.defaultInit(false);
        view.addButton(Buttons.sort());
        view.importJs("/safecampus/campus/account/subject.js");
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("科目名称", "subjectName");
        view.addDefaultButtons();
        return view;
    }
}
