package com.gzzm.safecampus.campus.score;

import com.gzzm.platform.annotation.DeptId;
import com.gzzm.platform.commons.crud.BaseOQLQueryCrud;
import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.Subject;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.KeyBaseCrud;
import net.cyan.nest.annotation.Inject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生考试成绩CRUD
 *
 * @author yiuman
 * @date 2018/3/15
 */
@Service(url = "/campus/tutorship/subjectscorecrud")
public class SubjectScoreCrud extends BaseOQLQueryCrud<Map<String, Object>> implements KeyBaseCrud<Map<String, Object>, Integer>
{
    @DeptId
    private Integer deptId;

    @Inject
    private ScoreDao dao;

    private Integer examId;

    private String studentName;

    private String classesName;

    @NotSerialized
    private List<Subject> subjectList;

    private Map<String, String> subjectScoreMap;

    private Integer studentId;

    @NotSerialized
    private Student student;

    public SubjectScoreCrud()
    {
    }

    public Integer getExamId()
    {
        return examId;
    }

    public void setExamId(Integer examId)
    {
        this.examId = examId;
    }

    public String getStudentName()
    {
        return studentName;
    }

    public void setStudentName(String studentName)
    {
        this.studentName = "%"+studentName+"%";
    }

    public String getClassesName()
    {
        return classesName;
    }

    public void setClassesName(String classesName)
    {
        this.classesName = "%" + classesName + "%";
    }

    public List<Subject> getSubjectList()
    {
        return subjectList;
    }

    public void setSubjectList(List<Subject> subjectList)
    {
        this.subjectList = subjectList;
    }

    public Integer getStudentId()
    {
        return studentId;
    }

    public void setStudentId(Integer studentId)
    {
        this.studentId = studentId;
    }

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }

    public Map<String, String> getSubjectScoreMap()
    {
        return subjectScoreMap;
    }

    public void setSubjectScoreMap(Map<String, String> subjectScoreMap)
    {
        this.subjectScoreMap = subjectScoreMap;
    }

    @Override
    protected String getQueryString() throws Exception
    {
        List<Subject> subjectList = dao.getSubjectListByGradeId(dao.getGradeIdByExamId(examId));
        StringBuilder oql = new StringBuilder("select s.studentId,min(s.studentName) as studentName,min(s.classes.classesName) as classesName,min(s.studentNo) as studentNo");
        for (Subject subject : subjectList)
        {
            oql.append(",sum(score,subjectId=").append(subject.getSubjectId()).append(") as ").append(subject.getSubjectName());
        }
        oql.append(" from SubjectScore ss,Student s where ss.studentId=s.studentId and examId=?examId");
        String condition = createCondition();
        if (StringUtils.isNotEmpty(condition))
            oql.append(condition);
        oql.append(" group by s.studentId");
        return oql.toString();
    }

    @Override
    protected void loadList() throws Exception
    {
        super.loadList();
    }

    private String createCondition()
    {
        StringBuilder condition = new StringBuilder();
        if (StringUtils.isNotEmpty(classesName))
        {
            condition.append(" and s.classes.classesName like ?classesName");
        }
        if (StringUtils.isNotEmpty(studentName))
        {
            condition.append(" and s.studentName like ?studentName");
        }
        return condition.toString();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();
        view.setChecked(false);
        view.addComponent("姓名", "studentName");
        view.addComponent("班级", "classesName");
        view.addColumn("姓名", "studentName");
        view.addColumn("班级", "classesName");
        view.addColumn("学号", "studentNo");
        List<Subject> subjectList = dao.getSubjectListByGradeId(dao.getGradeIdByExamId(examId));
        if (subjectList != null)
        {
            for (Subject subject : subjectList)
            {
                view.addColumn(subject.getSubjectName(), subject.getSubjectName()).setWidth("80px");
            }
        }
        view.addButton(Buttons.query());
        view.makeEditable();
        view.addButton(Buttons.getButton("成绩导入", "impScore(" + examId + ")", "imp"));
        view.addButton(Buttons.delete());
        view.importJs("/safecampus/campus/exam/exam.js");
        return view;
    }

    @Service
    public String show(Integer key, String forward) throws Exception
    {
        studentId = key;
        //加载年级下的科目列表
        subjectList = dao.getSubjectListByGradeId(dao.getGradeIdByExamId(examId));
        //加载学生基本信息，用于展示
        student = dao.get(Student.class, key);
        //获取学生各个科目的成绩
        List<SubjectScore> subjectScores = dao.getSubjectScoresByExamAndStudentId(studentId, examId);
        //封装到map中回显到界面
        subjectScoreMap = new HashMap<>(subjectScores.size());
        for (SubjectScore subjectScore : subjectScores)
        {
            //由于map的key为数字的情况下无法将数据展示到界面，在前面加上_转换为字符串
            subjectScoreMap.put("_" + subjectScore.getSubjectId(), subjectScore.getScore());
        }
        return "/safecampus/campus/exam/scorepage.ptl";
    }

    @Transactional
    @Service
    public boolean removeAll(Integer[] keys) throws Exception
    {
        if (keys != null)
        {
            dao.deleteSubjectScores(keys, examId);
        }
        return true;
    }

    /**
     * 保存学生科目成绩
     *
     * @throws Exception
     */
    @Service
    @Transactional
    public void saveScore() throws Exception
    {
        for (Map.Entry<String, String> subjectScore : subjectScoreMap.entrySet())
        {
            Integer subjectId = Integer.valueOf(subjectScore.getKey().substring(1));
            String score = subjectScore.getValue();
            //判断当前是否存在记录，存在则更新，不存在则新增
            SubjectScore subjectScore1 = dao.getStudentSubjectScore(studentId, subjectId, examId);
            if (subjectScore1 == null)
            {
                subjectScore1 = new SubjectScore();
                subjectScore1.setExamId(examId);
                subjectScore1.setStudentId(studentId);
                subjectScore1.setDeptId(deptId);
                subjectScore1.setSubjectId(subjectId);
            }
            subjectScore1.setScore(score);
            dao.save(subjectScore1);
        }
    }

    @Override
    public Class<Integer> getKeyType()
    {
        return Integer.class;
    }

    @Override
    public Integer getKey(Map<String, Object> entity) throws Exception
    {
        return (Integer) entity.get("studentId");
    }
}
