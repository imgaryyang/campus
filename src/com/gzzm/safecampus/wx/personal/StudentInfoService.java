package com.gzzm.safecampus.wx.personal;

import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.classes.ClassTeacher;
import com.gzzm.safecampus.campus.classes.Guardian;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.Teacher;
import com.gzzm.safecampus.campus.device.DeviceCard;
import com.gzzm.safecampus.campus.score.Exam;
import com.gzzm.safecampus.campus.score.SubjectScore;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生信息
 *
 * @author Yiuman
 * @date 2018/4/27
 */
@Service
public class StudentInfoService extends BaseService {

    private Map<String, Object> dataMap;

    private Map<String, Object> scoreMap;

    private Integer studentId;

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public Map<String, Object> getScoreMap() {
        return scoreMap;
    }

    public void setScoreMap(Map<String, Object> scoreMap) {
        this.scoreMap = scoreMap;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public StudentInfoService() {
    }

    @Service(url = "/wx/stuedntInfoPage")
    public String studentInfoPage() throws Exception {
        initData();
        return "/safecampus/wx/personal/student_info.ptl";
    }

    public void initData() throws Exception {
        if (dataMap == null) dataMap = new HashMap<>();
        Student student = wxAuthDao.load(Student.class, studentId);
        dataMap.put("studentId", student.getStudentId());
        dataMap.put("studentName", student.getStudentName());
        dataMap.put("className", student.getClasses() == null ? "" : student.getClasses().getClassesName());
        ClassTeacher teacher = wxAuthDao.getMasterByClassId(student.getClassesId());
        dataMap.put("master", teacher.getTeacher() == null ? "" : teacher.getTeacher().getTeacherName());
        dataMap.put("teacherPhone",teacher.getTeacher()==null?"":teacher.getTeacher().getPhone());
        DeviceCard card = wxAuthDao.getDeviceCardByStutdentId(studentId);
        dataMap.put("card", card == null ? "" : card.getCardNo());
        Guardian guardian = wxAuthDao.getGuardian(studentId, wxUserOnlineInfo.getPhone());
        dataMap.put("relation", guardian == null ? "" : guardian.getRelationInfo());
        dataMap.put("school", student.getDept() == null ? "" : student.getDept().getDeptName());
    }

    /**
     * 分数页面
     *
     * @param studentId
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/scorePage/{$0}")
    public String scorePage(Integer studentId) throws Exception {
        scoreMap = studentScoreData(studentId);
        return "/safecampus/wx/personal/score.ptl";
    }


    /**
     * 分数数据结构
     *
     * @param studentId
     * @return
     * @throws Exception
     */
    public Map<String, Object> studentScoreData(Integer studentId) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        Student student = wxAuthDao.get(Student.class, studentId);
        map.put("student", student);
        List<Exam> examList = wxAuthDao.getExamByStudentId(studentId);
        List<Map<String, Object>> examMapList = new ArrayList<>();
        for (Exam exam : examList) {
            Map<String, Object> examMap = new HashMap<String, Object>();
            examMap.put("examName", exam.getExamName());
            examMap.put("examTime", exam.getExamTime());
            List<SubjectScore> subjectScoreList = wxAuthDao.getSubjectScoreByExamIdAndStuId(exam.getExamId(), studentId);
            List<Map<String, Object>> studentScoreList = new ArrayList<Map<String, Object>>();
            for (SubjectScore subjectScore : subjectScoreList) {
                Map<String, Object> scoreMap = new HashMap<String, Object>();
                scoreMap.put("subjectName", subjectScore.getSubject().getSubjectName());
                scoreMap.put("score", subjectScore.getScore());
                List<Integer> studentIds = wxAuthDao.getStudentIdsByClsId(student.getClassesId());
                List<SubjectScore> allExamSubcore = wxAuthDao.getSubjectScoreByExamIdAndSubjectId(exam.getExamId(), subjectScore.getSubjectId(), studentIds);
                scoreMap.put("rank", StringUtils.isEmpty(subjectScore.getScore()) ? "-" : WxHandler.getSort(allExamSubcore, subjectScore.getScoreId()));
                scoreMap.put("avg", WxHandler.getAvg(allExamSubcore));
                studentScoreList.add(scoreMap);
            }
            examMap.put("SubjectScoreList", studentScoreList);
            examMapList.add(examMap);
        }
        map.put("examList", examMapList);
        return map;
    }

}
