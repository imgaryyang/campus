package com.gzzm.safecampus.campus.score;

import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.wx.message.ScoreMsg;
import com.gzzm.safecampus.campus.wx.message.WxTemplateMessage;
import net.cyan.commons.util.DateUtils;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 成绩业务处理类
 *
 * @author Neo
 * @date 2018/4/2 11:30
 */
public class ScoreService
{
    @Inject
    private ScoreDao scoreDao;

    public ScoreService()
    {
    }

    /**
     * 发送考试成绩通知
     *
     * @param examId 考试成绩
     * @throws Exception 操作异常
     */
    public void sendScoreMsg(Integer examId) throws Exception
    {
        Exam exam = scoreDao.load(Exam.class, examId);
        Object[] objects = {examId};
        List<Student> studentList = scoreDao.oqlQuery("select st from Student st where st.studentId in(select s.studentId from SubjectScore s where s.examId=:1)", Student.class, objects);
        for (Student student : studentList)
        {
            ScoreMsg scoreMsg = new ScoreMsg();
            scoreMsg.setStudentName(student.getStudentName());
            scoreMsg.setStudentId(student.getStudentId());
            scoreMsg.setCourseName(exam.getExamName());
            scoreMsg.setTime(DateUtils.toString(exam.getExamTime()));
            //拼接每个科目的分数
            List<SubjectScore> subjectScores = scoreDao.getSubjectScoresByExamAndStudentId(student.getStudentId(), examId);
            String score = appendScore(subjectScores);
            scoreMsg.setScore(score);
            WxTemplateMessage.sendScoreMsg(scoreMsg);
        }
    }

    private String appendScore(List<SubjectScore> subjectScores)
    {
        StringBuilder score = new StringBuilder("\n");
        int i = 1;
        for (SubjectScore subjectScore : subjectScores)
        {
            score.append(subjectScore.getSubject().getSubjectName())
                    .append("\t\t").append(subjectScore.getScore()).append("分").append("\t\t");
            //每三个科目换行
            if (i % 3 == 0)
            {
                score.append("\n");
            }
            i++;
        }
        return score.toString();
    }
}
