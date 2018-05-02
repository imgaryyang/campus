package com.gzzm.safecampus.campus.score;

import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import com.gzzm.safecampus.campus.account.SchoolYear;
import com.gzzm.safecampus.campus.base.BaseCrud;
import net.cyan.arachne.annotation.Select;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.commons.util.Provider;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.HrefCell;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.util.List;
import java.util.Objects;

/**
 * 考试Crud
 *
 * @author yiuman
 * @date 2018/3/15
 */
@Service(url = "/campus/tutorship/examcrud")
public class ExamCrud extends BaseCrud<Exam, Integer>
{
    @Inject
    private ScoreService scoreService;

    @Inject
    private Provider<ScoreDao> provider;

    @Like
    private String examName;

    private Integer yearId;

    private Integer gradeId;

    private List<Integer> gradeIds;

    private List<Integer> grades;

    private GradeListModel gradeListModel;

    public String getExamName()
    {
        return examName;
    }

    public void setExamName(String examName)
    {
        this.examName = examName;
    }

    public Integer getYearId()
    {
        return yearId;
    }

    public void setYearId(Integer yearId)
    {
        this.yearId = yearId;
    }

    public void setGradeId(Integer gradeId)
    {
        this.gradeId = gradeId;
    }

    public Integer getGradeId()
    {
        return gradeId;
    }

    public List<Integer> getGradeIds()
    {
        return gradeIds;
    }

    public void setGradeIds(List<Integer> gradeIds)
    {
        this.gradeIds = gradeIds;
    }

    public List<Integer> getGrades()
    {
        return grades;
    }

    public void setGrades(List<Integer> grades)
    {
        this.grades = grades;
    }

    public ExamCrud()
    {
    }

    @Override
    protected String getComplexCondition() throws Exception
    {

        return super.getComplexCondition() == null ? " gradeId in ?grades" : super.getComplexCondition() + " and gradeId in ?grades";
    }

    @Select(field = {"gradeIds", "grades", "entity.gradeId"})
    public GradeListModel getGradeListModel() throws Exception
    {
        if (gradeListModel == null)
            gradeListModel = Tools.getBean(GradeListModel.class);
        return gradeListModel;
    }

    public void setGradeListModel(GradeListModel gradeListModel)
    {
        this.gradeListModel = gradeListModel;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.addComponent("考试名称", "examName");
        view.addComponent("所属年级", "grades");
        view.addColumn("考试名称", new HrefCell("examName").setAction(
                "goTOStudenExamPage('${examId}')"));

//        view.addColumn("考试名称", new CButton("examName","goTOStudenExamPage('${examId}')").setClass("thisYear"));
        view.addColumn("所属年级", "grade.gradeName");
        view.addColumn("所属学年", "year.year");
        view.addColumn("考试时间", "examTime");
        view.addColumn("操作",
                new CButton("学生成绩管理", "goTOStudenExamPage('${examId}')")
                        .setClass("addSendCol")).setWidth("110");
        view.addColumn("发送成绩单", new CButton("发送", "sendScoreMsg(${examId})"));
        view.setOnDblClick("goTOStudenExamPage(${entity.examId})");
        view.defaultInit(false);
        view.importJs("/safecampus/campus/exam/exam.js");
        view.importCss("/safecampus/campus/payment/view.css");
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        if (getEntity().getExamId() == null)
        {
            getGradeListModel().setCheckBox(true);
        } else
        {
            getGradeListModel().setCheckBox(false);
        }
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("考试名称", "examName", true);
        view.addComponent("考试时间", "examTime", true);
        if (isNew$())
        {
            view.addComponent("所属年级", "this.gradeIds", true).setProperty("text", "${grade.gradeName}");
        } else
        {
            view.addComponent("所属年级", "gradeId", true).setProperty("text", "${grade.gradeName}");
        }
        view.addDefaultButtons();
        setGradeId(getEntity().getGradeId());
        return view;
    }


    @Override
    public Integer save() throws Exception
    {

        if (CollectionUtils.isNotEmpty(gradeIds) && getEntity().getExamId() == null)
        {
            SchoolYear year = provider.get().getSchoolYear();
            for (Integer gradeId : gradeIds)
            {
                Exam exam = new Exam();
                exam.setDeptId(getDefaultDeptId());
                if (year != null)
                {
                    exam.setYearId(year.getSchoolYearId());
                    exam.setYear(year);
                }
                exam.setExamTime(getEntity().getExamTime());
                exam.setExamName(getEntity().getExamName());
                exam.setGradeId(gradeId);
                provider.get().save(exam);
            }
            return 1;
        } else
        {
            if (!Objects.equals(getEntity().getGradeId(), gradeId))
            {
                Exam exam = provider.get().getExamByGradeId(getEntity().getGradeId(),getEntity().getExamName());
                if (exam != null) throw new NoErrorException("对应年级已有对应考试信息！");
            }
            return super.save();
        }
    }

    /**
     * 发送一次考试的成绩单
     *
     * @param examId 考试Id
     * @throws Exception 操作异常
     */
    @Service
    @Transactional
    public void sendScoreMsg0(Integer examId) throws Exception
    {
        scoreService.sendScoreMsg(examId);
        provider.get().updateExamMessageStatus(examId);
    }

    @Override
    public String getOrderField()
    {
        return null;
    }
}
