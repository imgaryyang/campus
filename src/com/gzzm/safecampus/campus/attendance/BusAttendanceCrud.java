package com.gzzm.safecampus.campus.attendance;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.safecampus.campus.base.StudentOwnedCrud;
import com.gzzm.safecampus.campus.bus.BusInfoListDisplay;
import com.gzzm.safecampus.campus.classes.Classes;
import com.gzzm.safecampus.campus.classes.ClassesDao;
import com.gzzm.safecampus.campus.classes.StudentDao;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Lower;
import net.cyan.crud.annotation.Upper;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 校巴考勤:
 *
 * @author Huangmincong
 * @date 2018/03/15
 */
@Service(url = "/campus/attendance/busattend")
public class BusAttendanceCrud extends StudentOwnedCrud<Attendance, Integer> {
    @Lower(column = "attendanceTime")
    private Date dateStart;

    @Upper(column = "attendanceTime")
    private Date dateEnd;

    private String nodeId;

    /**
     * 学校id
     */
    private Integer schoolId;

    /**
     * 年级id
     */
    private Integer gradeId;

    /**
     * 考勤情况
     */
    private AttendanceStatus attendanceStatus;

    /**
     * 发送状态
     */
    private SendState sendState;

    private Date date;

    private Integer relationId;

    private String siteName;

    @Inject
    private StudentDao studentDao;

    @Inject
    private ClassesDao classesDao;

    @Inject
    private AttendanceDao attendanceDao;

    public BusAttendanceCrud() {
        addOrderBy("attendanceTime", OrderType.desc);
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

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @Override
    protected Object createListView() throws Exception {
        ComplexTableView view = new ComplexTableView(new BusInfoListDisplay(), "relationId");
        view.addComponent("学号", "studentNo");
        view.addComponent("学生姓名", "studentName");
        view.addComponent("打卡站点", "siteName");
        view.addComponent("发送状态", "sendState");
        view.addComponent("打卡时间", "dateStart");
        view.addComponent("至", "dateEnd");
        view.addColumn("学号", "student.studentNo");
        view.addColumn("学生姓名", "student.studentName");
        view.addColumn("学生班级", "crud$.getClasses(relationId)");
        view.addColumn("打卡时间", "attendanceTime");
        view.addColumn("打卡站点", "busSite");
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
        return "type=1";
    }

    /**
     * 根据班级id获取对应的班级名称
     *
     * @param classesId
     * @return
     * @throws Exception
     */
    public String getClasses(Integer classesId) throws Exception {
        Classes classes = classesDao.getClasses(classesId);
        String str = "";
        if (classes != null) {
            str = classes.getClassesName();
        }
        return str;
    }

    /**
     * 根据考勤详情id获取站点名称
     *
     * @param attId
     * @return
     * @throws Exception
     */
    public String getSiteName(Integer attId) throws Exception {
        String str = attendanceDao.getSiteName(attId);
        if (str != null) {
            return str;
        }
        return "";
    }

    @Override
    public String show(Integer key, String forward) throws Exception {
        return "/safecampus/campus/siesta/busattmessage.ptl";
    }

    @Override
    protected void beforeQuery() throws Exception {
        if (getRelationId()!=null&&getRelationId()==0){
            setRelationId(null);
        }
    }
}
