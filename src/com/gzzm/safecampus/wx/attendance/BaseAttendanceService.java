package com.gzzm.safecampus.wx.attendance;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.attendance.*;
import com.gzzm.safecampus.campus.bus.BusInfo;
import com.gzzm.safecampus.campus.bus.BusRouteDao;
import com.gzzm.safecampus.campus.bus.BusStudent;
import com.gzzm.safecampus.campus.classes.Classes;
import com.gzzm.safecampus.campus.classes.ClassesDao;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.StudentDao;
import com.gzzm.safecampus.campus.common.SchoolYearContainer;
import com.gzzm.safecampus.wx.user.WxUserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.commons.util.DateUtils;
import net.cyan.nest.annotation.Inject;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * 基本考勤Service
 *
 * @author yiuman
 * @date 2018/3/30
 */
@Service
public class BaseAttendanceService {

    protected Integer attstatus;

    @Inject
    protected WxUserOnlineInfo wxUserOnlineInfo;

    @Inject
    protected AttendanceService attendanceService;

    @Inject
    private SchoolYearContainer schoolYearContainer;

    @Inject
    protected AttendanceDao attendanceDao;

    @Inject
    protected ClassesDao classesDao;

    @Inject
    protected BusRouteDao busRouteDao;

    private Map<String, Object> dataMap;

    protected Integer key;

    private List<Map<String, Object>> list;

    private AttendanceType type;

    private java.sql.Date hisDate;

    @Inject
    private StudentDao studentDao;

    public java.sql.Date getHisDate() {
        return hisDate;
    }

    public void setHisDate(java.sql.Date hisDate) {
        this.hisDate = hisDate;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public AttendanceType getType() {
        return type;
    }

    public void setType(AttendanceType type) {
        this.type = type;
    }

    public Integer getAttstatus() {
        return attstatus;
    }

    public void setAttstatus(Integer attstatus) {
        this.attstatus = attstatus;
    }

    /**
     * 获取考勤数据
     *
     * @param state 判断是否查询历史状态
     * @return
     */
    @Service
    public Map<String, Object> querySiertaData(Integer state, Integer attType) {
        Map<String, Object> map = new HashMap<>();
        Date today = null;
        Date tomorrow = null;
        //判断是不是查询历史考勤（0或null表示当天的考勤）
        if (state == null || state == 0) {
            today = new java.sql.Date(System.currentTimeMillis());//当天
            tomorrow = new java.sql.Date(today.getTime() + 24 * 60 * 60 * 1000);//明天
        } else {
            if (hisDate != null && hisDate.getTime() != 0) {
                today = hisDate;
                tomorrow = new java.sql.Date(today.getTime() + 24 * 60 * 60 * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String fdate  = sdf.format(hisDate);
                map.put("hisDate",fdate);
            }
        }
        if (key != null) {
            List<AttendanceRecord> attendanceRecords = null;
            attendanceRecords = attendanceDao.getAttendanceSiestaToday(key, today, tomorrow, attType);
            //存在考勤记录
            if (CollectionUtils.isNotEmpty(attendanceRecords)) {
                List<Map<String, Object>> dataList = new ArrayList<>();
                for (AttendanceRecord attendanceRecord : attendanceRecords) {
                    boolean history = true;
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("aoId", attendanceRecord.getAoId());
                    Date recordTime = attendanceRecord.getDate();
                    dataMap.put("date", DateUtils.toString(recordTime));
                    if (isThisTime(recordTime.getTime(), "yyyy-MM-dd")) {
                        history = false;
                    }
                    dataMap.put("history", history);
                    List<Student> noSenStudens = attendanceDao.getUnsendStudents(attendanceRecord.getAoId(), 1);
                    dataMap.put("noSenedCount", noSenStudens == null ? 0 : noSenStudens.size());
                    if (CollectionUtils.isNotEmpty(noSenStudens)) {
                        List<Map<String, Object>> unSendList = new ArrayList<>();
                        for (Student student : noSenStudens) {
                            Map<String, Object> noSendMap = new HashMap<>();
                            noSendMap.put("studentName", student.getStudentName());
                            noSendMap.put("studentId", student.getStudentId());
                            List<Map<String, Object>> relations = attendanceDao.getRelationsByStudenId(student.getStudentId());
                            noSendMap.put("relations", relations);
                            unSendList.add(noSendMap);
                        }
                        dataMap.put("unSendeStudents", unSendList);
                    }
                    Integer leaveCount = attendanceDao.countStatusByAoId(attendanceRecord.getAoId(), AttendanceStatus.Leave);
                    dataMap.put("leaveCount", leaveCount);
                    Integer arrivedCount = attendanceDao.countBusStatusByAoId(attendanceRecord.getAoId());
                    dataMap.put("arrivedCount", arrivedCount);
                    Integer lateCount = attendanceDao.countStatusByAoId(attendanceRecord.getAoId(), AttendanceStatus.Late);
                    dataMap.put("lateCount", lateCount);
                    dataList.add(dataMap);
                }
                map.put("items", dataList);
            }
            map.put("key", key);
            map.put("type", Tools.getMessage(Tools.getMessage(type.getClass().getName() + "." + type.name())));
            map.put("historyStatus", state);
        }
        return map;
    }

    /**
     * 保存 考勤学生数据
     *
     * @param studentIds 学生ID
     * @param type       考勤类型
     * @return
     * @throws Exception
     */
    @Service(method = HttpMethod.post)
    @Transactional
    public Map<String, Object> saveChecked(List<Integer> studentIds, AttendanceType type) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(studentIds)) {
            AttendanceRecord record = new AttendanceRecord();
            Date date = new Date();
            record.setDate(date);
            record.setRelationId(key);
            record.setType(type);
            Integer deptId = wxUserOnlineInfo.getDeptId();//TODO
//            Integer deptId = 184;
            record.setDeptId(deptId);
            Integer identifyId = wxUserOnlineInfo.getIdentifyId();
//            Integer identifyId = 1032;
            record.setTeacherId(identifyId);
            attendanceDao.save(record);
            map.put("aoId", record.getAoId());
            List<Student> students = null;
            if (type == AttendanceType.TruAttendance) {
                students = attendanceDao.getStudentsBytTrusteeRoomId(key);
            } else if (type == AttendanceType.SiestaAttendance) {
                students = attendanceDao.getStudentsBySiestaRoomId(key);
            } else if (type == AttendanceType.ClassAttendance || type == AttendanceType.FaceAttendance) {
                students = classesDao.getStudentsByClassesId(key);
            }

            if (CollectionUtils.isNotEmpty(students)) {
                for (Student student : students) {
                    Attendance attendance = new Attendance();
                    attendance.setAoId(record.getAoId());
                    if (studentIds.contains(student.getStudentId())) {
                        attendance.setSendState(SendState.Sended);
                        attendance.setAttendanceStatus(AttendanceStatus.Arrived);
                    } else {
                        attendance.setAttendanceStatus(AttendanceStatus.Notarrived);
                        attendance.setSendState(SendState.Notsended);
                    }
                    attendance.setAttendanceTime(date);
                    attendance.setStudentId(student.getStudentId());
                    attendance.setType(type);
                    attendance.setRelationId(key);
                    attendance.setDeptId(deptId);
                    attendance.setTeacherId(identifyId);
                    attendanceDao.save(attendance);
                }
                boolean finish = false;
                if (studentIds.size() == students.size()) {
                    finish = true;
                }
                map.put("finish", finish);
                //发送次消息操作
                attendanceService.sendAttendanceMsg(record.getAoId(), AttendanceStatus.Arrived);
                return map;
            }
        }
        return map;
    }

    /**
     * 保存 校巴考勤学生数据
     *
     * @param studentIds 学生ID
     * @param type       考勤类型
     * @return
     * @throws Exception
     */
    @Service(method = HttpMethod.post)
    @Transactional
    public Map<String, Object> saveBuschecked(List<Integer> studentIds, AttendanceType type) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(studentIds)) {
            String batch = "";
            boolean flag = false;
            AttendanceStatus status = null;
            if (getAttstatus() == 0) {
                if (type == AttendanceType.BusAttendance) {
                    batch = "老师上午上车";
                    status = AttendanceStatus.MorningGetOn;
                    flag = true;
                } else if (type == AttendanceType.FaceAttendance) {
                    batch = "上午进校";
                    status = AttendanceStatus.MorningComeIn;
                }
            } else if (getAttstatus() == 1) {
                if (type == AttendanceType.BusAttendance) {
                    batch = "老师上午下车";
                    status = AttendanceStatus.MorningGetOff;
                } else if (type == AttendanceType.FaceAttendance) {
                    batch = "中午出校";
                    status = AttendanceStatus.NoonOut;
                }
            } else if (getAttstatus() == 2) {
                if (type == AttendanceType.BusAttendance) {
                    batch = "老师下午上车";
                    status = AttendanceStatus.AfternoonGetOn;
                } else if (type == AttendanceType.FaceAttendance) {
                    batch = "中午进校";
                    status = AttendanceStatus.NoonComeIn;
                }
            } else if (getAttstatus() == 3) {
                if (type == AttendanceType.BusAttendance) {
                    batch = "老师下午下车";
                    status = AttendanceStatus.AfternoonGetOff;
                    flag = true;
                } else if (type == AttendanceType.FaceAttendance) {
                    batch = "下午出校";
                    status = AttendanceStatus.AfterNoonOut;
                }
            }
            AttendanceRecord record = new AttendanceRecord();
            Date date = new Date();
            record.setDate(date);
            record.setRelationId(key);
            record.setType(type);
            Integer deptId = wxUserOnlineInfo.getDeptId();//TODO
//            Integer deptId = 184;
            record.setDeptId(deptId);
            Integer identifyId = wxUserOnlineInfo.getIdentifyId();//TODO
//            Integer identifyId = 1032;
            record.setTeacherId(identifyId);
            record.setAttendanceBatch(batch);
            attendanceDao.save(record);
            map.put("aoId", record.getAoId());
            List<Student> students = null;
            students = classesDao.getStudentsByBus(record.getRelationId());
            if (type == AttendanceType.FaceAttendance) {
                students = classesDao.getStudentsByClassesId(key);
            }
            if (CollectionUtils.isNotEmpty(students)) {
                for (Student student : students) {
                    Attendance attendance = new Attendance();
                    attendance.setAoId(record.getAoId());
                    attendance.setTeacherId(wxUserOnlineInfo.getIdentifyId());
//                    attendance.setTeacherId(1032);
                    if (flag) {
                        BusStudent busStudent = studentDao.getByStuId(student.getStudentId());//获取校巴
                        if (busStudent != null) {
                            attendance.setSiteId(busStudent.getSiteId());
                        }
                    }
                    if (studentIds.contains(student.getStudentId())) {
                        attendance.setSendState(SendState.Sended);
                        attendance.setAttendanceStatus(status);
                    } else {
                        attendance.setAttendanceStatus(AttendanceStatus.Notarrived);
                        attendance.setSendState(SendState.Notsended);
                    }
                    attendance.setAttendanceTime(date);
                    attendance.setStudentId(student.getStudentId());
                    attendance.setType(type);
                    attendance.setRelationId(key);
                    attendance.setDeptId(deptId);
                    attendance.setTeacherId(identifyId);
                    attendanceDao.save(attendance);
                    //发送次消息操作
                    attendanceService.sendSingleMsg(attendance.getAttId(), status);
                }
                boolean finish = false;
                if (studentIds.size() == students.size()) {
                    finish = true;
                }
                map.put("finish", finish);

                return map;
            }
        }
        return map;
    }

    /**
     * 是否当天
     *
     * @param time
     * @param pattern
     * @return
     */
    private static boolean isThisTime(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    @Service(url = "/wx/attendance/allhistory")
    public String toHistoryPage() throws Exception {
        if (type == AttendanceType.ClassAttendance || type == AttendanceType.FaceAttendance) {
        List<Classes> classeses = classesDao.getclassByTeacherId(wxUserOnlineInfo.getIdentifyId());//TODO
//            List<Classes> classeses = classesDao.getclassByTeacherId(1032);
            List<Map<String, Object>> maplist = new ArrayList<>();
            for (Classes classes : classeses) {
                Map<String, Object> map = new HashMap<>();
                map.put("Id", classes.getClassesId());
                map.put("name", classes.getGrade() + classes.getClassesName());
                maplist.add(map);
            }
            setList(maplist);
            setType(type);
        } else if (type == AttendanceType.BusAttendance) {
        List<Map<String,Object>>  busInfos = busRouteDao.getBusInfos(wxUserOnlineInfo.getDeptId());
//            List<Map<String, Object>> busInfos = busRouteDao.getBusInfos(184);
            setList(busInfos);
            setType(type);
        } else if (type == AttendanceType.SiestaAttendance) {
        setList(attendanceDao.getSiertaInfoByTeacherId(wxUserOnlineInfo.getIdentifyId()));//TODO
//            setList(attendanceDao.getSiertaInfoByTeacherId(1032));
            setType(type);
        } else if (type == AttendanceType.TruAttendance) {
        setList(attendanceDao.getTrusteeInfoByTeacherId(wxUserOnlineInfo.getIdentifyId()));
//            setList(attendanceDao.getTrusteeInfoByTeacherId(1032));
            setType(type);
        }

        return "/safecampus/wx/attendance/history.ptl";
    }

}
