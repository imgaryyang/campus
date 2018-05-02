package com.gzzm.safecampus.campus.attendance;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.safecampus.campus.base.StudentOwnedCrud;
import com.gzzm.safecampus.campus.classes.Classes;
import com.gzzm.safecampus.campus.classes.ClassesDao;
import com.gzzm.safecampus.campus.classes.StudentDao;
import com.gzzm.safecampus.campus.siesta.SiestaRoomListDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Lower;
import net.cyan.crud.annotation.Upper;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 午休考勤:
 *
 * @author Huangmincong
 * @date 2018/03/14
 */
@Service(url = "/campus/attendance/sieattend")
public class SiestaAttendanceCrud extends StudentOwnedCrud<Attendance, Integer>
{

    @Lower(column = "attendanceTime")
    private Date dateStart;

    @Upper(column = "attendanceTime")
    private Date dateEnd;

    private String nodeId;

    private Integer schoolId;

    private Integer gradeId;

    private AttendanceStatus attendanceStatus;

    private SendState sendState;

    private Date date;

    private Integer relationId;

    @Inject
    private StudentDao studentDao;

    @Inject
    private ClassesDao classesDao;

    public SiestaAttendanceCrud() {
        addOrderBy("attendanceTime", OrderType.desc);
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public SendState getSendState() {
        return sendState;
    }

    public void setSendState(SendState sendState) {
        this.sendState = sendState;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    protected Object createListView() throws Exception {
        ComplexTableView view = new ComplexTableView(new SiestaRoomListDisplay(), "relationId");

        view.addComponent("学号", "studentNo");
        view.addComponent("学生姓名", "studentName");
        view.addComponent("考勤情况", "attendanceStatus");
        view.addComponent("发送状态", "sendState");
        view.addComponent("考勤时间", "dateStart");
        view.addComponent("至", "dateEnd");
        view.addColumn("学号", "student.studentNo");
        view.addColumn("学生姓名", "student.studentName");
        view.addColumn("学生班级", "crud$.getClasses(relationId)");
        view.addColumn("考勤时间", "attendanceTime");
        view.addColumn("考勤情况", "attendanceStatus");
        view.addColumn("发送状态", "sendState");
        view.addColumn("考勤老师", "teacher");
        view.addColumn("备注", "remark");
        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());
        return view;
    }

    @Override
    protected String getComplexCondition() throws Exception {
        return "type=2";
    }

    public String getClasses(Integer classesId) throws Exception {
        Classes classes = classesDao.getClasses(classesId);
        String str="";
        if (classes != null) {
            str = classes.getClassesName();
        }
        return str;
    }
    @Override
    protected void beforeQuery() throws Exception {
        if (getRelationId()!=null&&getRelationId()==0){
            setRelationId(null);
        }
    }

}

