package com.gzzm.safecampus.wx.attendance;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.campus.attendance.*;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.StudentDao;
import com.gzzm.safecampus.wx.user.WxUserOnlineInfo;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 孩子安全
 *
 * @author hmc
 * @date 2018/03/29
 */
@Service
public class ChildSafety {

    @Inject
    private WxUserOnlineInfo wxUserOnlineInfo;

    @Inject
    private StudentDao studentDao;

    @Inject
    private AttendanceDao attendanceDao;

    private Integer studentId;

    private String studentName;

    private java.sql.Date dateStart;

    private java.sql.Date dateEnd;

    private Student student;

    @NotSerialized
    private List<Student> students;

    private Integer state = 0;

    private Integer count;

    private Map<String, List<Attendancevo>> attMap;

    public ChildSafety() {
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Map<String, List<Attendancevo>> getAttMap() {
        return attMap;
    }

    public void setAttMap(Map<String, List<Attendancevo>> attMap) {
        this.attMap = attMap;
    }

    @Service(url = "/wx/attendance/childsafe")
    public String showAllChildren() throws Exception {
        System.out.println("选择的日期：" + getDateStart() + "==========================");
        if (getDateStart() != null && getDateStart().getTime() != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getDateStart());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dateEnd = new Date(calendar.getTime().getTime());
        }

        DateFormat df = new SimpleDateFormat("HH:mm");
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        //加载当前微信用户关联的所有学生

        if (getStudentId() == null) {
            state = 0;
        } else {
            state += 1;
        }
        students=studentDao.getStudentsByUserId(wxUserOnlineInfo.getUserId());
//        students=studentDao.getStudentsByUserId(117);
        student = studentDao.getOneStudentByUserId(wxUserOnlineInfo.getUserId(),state);
//        student = studentDao.getOneStudentByUserId(117, state);
        if (student == null) {
            state = 0;
            student = studentDao.getOneStudentByUserId(wxUserOnlineInfo.getUserId(),state);
//            student = studentDao.getOneStudentByUserId(117, state);
        }
        if (student != null) {
            Map<String, List<Attendance>> map1 = new LinkedHashMap<>();
            List<Attendance> attendances;
            if (getStudentId() == null) {
                attendances = attendanceDao.getStudentAttendance(student.getStudentId(), dateStart, dateEnd);
                studentId = student.getStudentId();
            } else {
                studentId = student.getStudentId();
                attendances = attendanceDao.getStudentAttendance(getStudentId(), dateStart, dateEnd);
            }
            count=attendances.size();
            for (Attendance attendance : attendances) {
                //根据日期分组
                if (map1.get(sf.format(attendance.getAttendanceTime())) == null) {
                    List<Attendance> list = new ArrayList<>();
                    list.add(attendance);
                    map1.put(sf.format(attendance.getAttendanceTime()), list);
                } else {
                    List<Attendance> list = map1.get(sf.format(attendance.getAttendanceTime()));
                    list.add(attendance);
                }
            }
            attMap = new TreeMap<>();
            for (Map.Entry<String, List<Attendance>> map : map1.entrySet()) {
                List<Attendancevo> attendancevos = new ArrayList<>();
                ListSort(map.getValue());
                for (Attendance att : map.getValue()) {
                    Attendancevo attendancevo = new Attendancevo();
                    attendancevo = getAttName(att, attendancevo);
                    attendancevo.setArrivedTime(df.format(att.getAttendanceTime()));
                    if (attendancevo.getAttName() != null) {
                        attendancevos.add(attendancevo);
                    }
                }
                attMap.put(map.getKey(), attendancevos);
                setAttMap(sortMapByKeyatt(attMap));
            }
        }
        return "safecampus/wx/attendance/child.ptl";
    }

    private Attendancevo getAttName(Attendance att, Attendancevo attendancevo) throws Exception {
        if (att.getAttendanceStatus() == AttendanceStatus.MorningGetOn) {
            attendancevo.setAttName("上学已上车");
        } else if (att.getAttendanceStatus() == AttendanceStatus.MorningGetOff) {
            attendancevo.setAttName("上学已下车");
        } else if (att.getAttendanceStatus() == AttendanceStatus.AfternoonGetOn) {
            attendancevo.setAttName("放学已上车");
        } else if (att.getAttendanceStatus() == AttendanceStatus.AfternoonGetOff) {
            attendancevo.setAttName("放学已下车");
        } else if (att.getAttendanceStatus() == AttendanceStatus.MorningComeIn) {
            attendancevo.setAttName("上午进校");
        } else if (att.getAttendanceStatus() == AttendanceStatus.NoonOut) {
            attendancevo.setAttName("中午出校");
        } else if (att.getAttendanceStatus() == AttendanceStatus.NoonComeIn) {
            attendancevo.setAttName("中午进校");
        } else if (att.getAttendanceStatus() == AttendanceStatus.AfterNoonOut) {
            attendancevo.setAttName("下午出校");
        } else if (att.getAttendanceStatus() == AttendanceStatus.Arrived) {
            if (att.getType() == AttendanceType.ClassAttendance) {
                attendancevo.setAttName("已到班");
            } else if (att.getType() == AttendanceType.SiestaAttendance) {
                attendancevo.setAttName("已在午休");
            } else if (att.getType() == AttendanceType.TruAttendance) {
                attendancevo.setAttName("已在托管");
            } else if (att.getType() == AttendanceType.BusAttendance) {
                AttendanceRecord record = attendanceDao.getRecord(att.getAoId());
                if (record != null && record.getAttendanceBatch().contains("上午上车")) {
                    attendancevo.setAttName("上学已上车");
                } else if (record != null && record.getAttendanceBatch().contains("上午下车")) {
                    attendancevo.setAttName("上学已下车");
                } else if (record != null && record.getAttendanceBatch().contains("下午上车")) {
                    attendancevo.setAttName("放学已上车");
                } else if (record != null && record.getAttendanceBatch().contains("下午下车")) {
                    attendancevo.setAttName("放学已下车");
                }

            } else if (att.getType() == AttendanceType.FaceAttendance) {
                AttendanceRecord record = attendanceDao.getRecord(att.getAoId());
                if (record != null && record.getAttendanceBatch().contains("上午进校")) {
                    attendancevo.setAttName("上午进校");
                } else if (record != null && record.getAttendanceBatch().contains("中午出校")) {
                    attendancevo.setAttName("中午出校");
                } else if (record != null && record.getAttendanceBatch().contains("中午进校")) {
                    attendancevo.setAttName("中午进校");
                } else if (record != null && record.getAttendanceBatch().contains("下午出校")) {
                    attendancevo.setAttName("下午出校");
                }

            }
        } else if (att.getAttendanceStatus() == AttendanceStatus.Late) {
            if (att.getType() == AttendanceType.ClassAttendance) {
                attendancevo.setAttName("上课迟到");
            } else if (att.getType() == AttendanceType.SiestaAttendance) {
                attendancevo.setAttName("午休迟到");
            } else if (att.getType() == AttendanceType.TruAttendance) {
                attendancevo.setAttName("托管迟到");
            } else if (att.getType() == AttendanceType.BusAttendance) {
                AttendanceRecord record = attendanceDao.getRecord(att.getAoId());
                if (record != null && record.getAttendanceBatch().contains("上车")) {
                    attendancevo.setAttName("上车迟到");
                }
            } else if (att.getType() == AttendanceType.FaceAttendance) {
                AttendanceRecord record = attendanceDao.getRecord(att.getAoId());
                if (record != null && record.getAttendanceBatch().contains("上午进校")) {
                    attendancevo.setAttName("迟到到校");
                }
            }
        } else if (att.getAttendanceStatus() == AttendanceStatus.Leave) {
            attendancevo.setAttName("请假");
        } else if (att.getAttendanceStatus() == AttendanceStatus.Notarrived) {
            if (att.getType() == AttendanceType.ClassAttendance) {
                attendancevo.setAttName("上课未到");
            } else if (att.getType() == AttendanceType.SiestaAttendance) {
                attendancevo.setAttName("午休未到");
            } else if (att.getType() == AttendanceType.TruAttendance) {
                attendancevo.setAttName("托管未到");
            } else if (att.getType() == AttendanceType.BusAttendance) {
                AttendanceRecord record = attendanceDao.getRecord(att.getAoId());
                if (record != null && record.getAttendanceBatch().contains("上车")) {
                    attendancevo.setAttName("上车未到");
                }
            } else if (att.getType() == AttendanceType.FaceAttendance) {
                AttendanceRecord record = attendanceDao.getRecord(att.getAoId());
                if (record != null && record.getAttendanceBatch().contains("上午进校")) {
                    attendancevo.setAttName("未进校门");
                }
            }
        }
        return attendancevo;
    }

    public static Map<String, List<Attendancevo>> sortMapByKeyatt(Map<String, List<Attendancevo>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, List<Attendancevo>> sortMap = new TreeMap<String, List<Attendancevo>>(
                new MapKeyComparator2());

        sortMap.putAll(map);

        return sortMap;
    }

    public static void ListSort(List<Attendance> list) {
        Collections.sort(list, new Comparator<Attendance>() {
            @Override
            public int compare(Attendance o1, Attendance o2) {
                try {
                    if (o1.getAttendanceTime().getTime() > o2.getAttendanceTime().getTime()) {
                        return 1;
                    } else if (o1.getAttendanceTime().getTime() < o2.getAttendanceTime().getTime()) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}
