package com.gzzm.safecampus.campus.score;

import com.gzzm.platform.annotation.DeptId;
import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.organ.User;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.Subject;
import com.gzzm.safecampus.campus.common.Constants;
import com.gzzm.safecampus.campus.common.Importable;
import net.cyan.arachne.annotation.Forward;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.DataConvert;
import net.cyan.crud.importers.CrudEntityImportor;
import net.cyan.crud.importers.DataRecord;
import net.cyan.nest.annotation.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生考试成绩导入
 *
 * @author zy
 * @date 2018/3/16 16:25
 */
@Service(url = "/campus/tutorship/subjectscoreimp")
public class SubjectScoreImp extends BaseNormalCrud<Map<String, Object>, Integer> implements Importable
{
    @Inject
    private ScoreDao dao;

    @DeptId
    private Integer deptId;

    private Integer examId;

    public SubjectScoreImp()
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

    @Override
    @Forward(page = Constants.IMP_PAGE)
    public String showImp() throws Exception
    {
        return super.showImp();
    }

    @Override
    public boolean isShowTemplate()
    {
        return true;
    }

    public String getTemplatePath()
    {
        return "/safecampus/campus/exam/学生成绩导入模板.xlsx";
    }

    @Override
    protected void initImportor(CrudEntityImportor<Map<String, Object>, Integer> importor) throws Exception
    {
        super.initImportor(importor);
        if (examId == null)
            throw new NoErrorException("请关联考试！");
        Integer gradeId = dao.getGradeIdByExamId(examId);
        if (gradeId != null)
        {
            importor.addColumnMap("学生姓名", "studentName");
            importor.addColumnMap("学号", "studentNo");
            List<Subject> subjectList = dao.getSubjectListByGradeId(gradeId);
            for (Subject subject : subjectList)
            {
                importor.addColumnMap(subject.getSubjectName(),
                        User.getSpell(subject.getSubjectName()) + subject.getSubjectId());
            }
        } else
        {
            throw new NoErrorException("请选择班级！");
        }
    }

    @Override
    protected CrudEntityImportor<Map<String, Object>, Integer> getCrudEntityImportor() throws Exception
    {
        return new CrudEntityImportor<Map<String, Object>, Integer>()
        {
            @Override
            protected void save(Map<String, Object> entity, DataRecord record) throws Exception
            {
                Student student =
                        dao.getStudentByName(DataConvert.toString(entity.get("studentName")), DataConvert.toString(entity.get("studentNo")),
                                deptId);
                if (student != null)
                {
                    Integer gradeId = dao.getGradeIdByExamId(examId);
                    if (gradeId != null)
                    {
                        List<Subject> subjectList = dao.getSubjectListByGradeId(gradeId);
                        for (Subject subject : subjectList)
                        {
                            SubjectScore subjectScore = dao.getStudentSubjectScore(student.getStudentId(), subject.getSubjectId(), examId);
                            if (subjectScore == null)
                            {
                                subjectScore = new SubjectScore();
                                subjectScore.setExamId(getExamId());
                                subjectScore.setStudentId(student.getStudentId());
                                subjectScore.setSubjectId(subject.getSubjectId());
                                subjectScore.setDeptId(deptId);
                            }
                            String score = DataConvert.toString(entity.get(User.getSpell(subject.getSubjectName()) + subject.getSubjectId()));
                            subjectScore.setScore(score);
                            dao.save(subjectScore);
                        }
                    } else
                    {
                        throw new NoErrorException("请选择班级！");
                    }
                } else
                {
                    throw new NoErrorException(DataConvert.toString(entity.get("studentName")) + "学生关联失败！");
                }
            }

            @Override
            protected void process(DataRecord record) throws Exception
            {
                process(new HashMap<String, Object>(), record);
            }
        };
    }
}
