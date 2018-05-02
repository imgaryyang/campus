package com.gzzm.safecampus.campus.attendance;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.safecampus.campus.base.StudentOwnedCrud;
import com.gzzm.safecampus.campus.classes.Classes;
import com.gzzm.safecampus.campus.classes.ClassesDao;
import com.gzzm.safecampus.campus.classes.ClassesTreeDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Lower;
import net.cyan.crud.annotation.Upper;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 班级考勤:
 *
 * @author Huangmincong
 * @date 2018/03/14
 */
@Service(url = "/campus/attendance/classattend")
public class ClassesAttendanceCrud extends StudentOwnedCrud<Attendance, Integer>
{

    @Lower(column = "attendanceTime")
    private Date dateStart;

    @Upper(column = "attendanceTime")
    private Date dateEnd;

    private AttendanceStatus attendanceStatus;

    private Integer relationId;

    private SendState sendState;

    private AttendanceType type = AttendanceType.ClassAttendance;

    @Inject
    private ClassesDao classesDao;

    public ClassesAttendanceCrud() {
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

    public AttendanceType getType()
    {
        return type;
    }

    public void setType(AttendanceType type)
    {
        this.type = type;
    }

    @Override
    protected Object createListView() throws Exception {
        ComplexTableView view = new ComplexTableView(new ClassesTreeDisplay(), "classesId");

        view.addComponent("学号", "studentNo");
        view.addComponent("学生姓名", "studentName");
        view.addComponent("考勤情况", "attendanceStatus");
        view.addComponent("发送状态", "sendState");
        view.addComponent("考勤时间", "dateStart");
        view.addComponent("至", "dateEnd");
        view.addColumn("学号", "student.studentNo");
        view.addColumn("学生姓名", "student.studentName");
        view.addColumn("学生班级", "crud$.showClass(relationId)");
        view.addColumn("考勤时间", "attendanceTime");
        view.addColumn("考勤情况", "attendanceStatus");
        view.addColumn("发送状态", "sendState");
        view.addColumn("考勤老师", "teacher");
        view.addColumn("备注", "remark");
        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());
        return view;
    }

    public String showClass(Integer relationId) throws Exception {
        String str="";
        Classes classes = classesDao.get(Classes.class,relationId);
        if (classes!=null){
            str=classes.getClassesName();
        }
        return str;
    }

    @Override
    protected void beforeQuery() throws Exception {
       if (getClassesId()!=null&&getClassesId()>0){
           setRelationId(getClassesId());
           setClassesId(null);
       }
    }
}
