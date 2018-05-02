package com.gzzm.safecampus.campus.attendance;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.safecampus.campus.base.StudentOwnedCrud;
import com.gzzm.safecampus.campus.classes.ClassesDao;
import com.gzzm.safecampus.campus.classes.ClassesTreeDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Lower;
import net.cyan.crud.annotation.Upper;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 人脸识别
 *
 * @author hmc
 * @date 2018/04/13
 */
@Service(url = "/campus/attendance/faceattend")
public class FaceAttendanceCrud extends StudentOwnedCrud<Attendance, Integer> {


    @Lower(column = "attendanceTime")
    private Date dateStart;

    @Upper(column = "attendanceTime")
    private Date dateEnd;

    private AttendanceStatus attendanceStatus;

    private Integer relationId;

    private SendState sendState;

    private AttendanceType type = AttendanceType.FaceAttendance;

    @Inject
    private ClassesDao classesDao;

    public FaceAttendanceCrud() {
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

    public AttendanceType getType() {
        return type;
    }

    public void setType(AttendanceType type) {
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
        view.addColumn("学生班级", "classes");
        view.addColumn("考勤时间", "attendanceTime");
        view.addColumn("考勤情况", "attendanceStatus");
        view.addColumn("发送状态", "sendState");
        view.addColumn("考勤行为", "crud$.showBatch(aoId)");
        view.addColumn("备注", "remark");
        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());
        return view;
    }

    /**
     * 获取考勤的行为（进校/出校）
     *
     * @param aoId
     * @return
     * @throws Exception
     */
    public String showBatch(Integer aoId) throws Exception {
        String batch = "";
        AttendanceRecord record = classesDao.get(AttendanceRecord.class, aoId);
        if (record != null) {
            batch = record.getAttendanceBatch();
        }
        return batch;
    }

    @Override
    protected void beforeQuery() throws Exception {
        if (getClassesId()!=null&&getClassesId()>0){
            setRelationId(getClassesId());
            setClassesId(null);
        }
    }
}
