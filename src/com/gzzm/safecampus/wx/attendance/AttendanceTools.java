package com.gzzm.safecampus.wx.attendance;

import com.gzzm.platform.commons.CommonDao;
import com.gzzm.safecampus.campus.attendance.*;
import com.gzzm.safecampus.campus.classes.Student;
import net.cyan.commons.util.Chinese;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;
import org.apache.poi.ss.formula.functions.T;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * 考勤工具类
 *
 * @author hmc
 * @date 2018/03/31
 */
public final class AttendanceTools {

    @Inject
    private static Provider<AttendanceDao> daoProvider;

    public static Map<String, Object> getStringListMap(List<Student> students) {
        Map<String, Object> map = new HashMap<>();
        map.put("sizeAll", students.size());
        Map<String, List<Student>> maps = new TreeMap<String, List<Student>>();
        for (Student student : students) {
            String py = Chinese.getFirstLetters(student.getStudentName().substring(0, 1));//根据汉字获取拼音首字母
            if (maps.get(py) == null) {
                List<Student> list = new ArrayList<Student>();
                list.add(student);
                maps.put(py, list);
            } else {
                List<Student> list = maps.get(py);
                list.add(student);
            }
        }
        map.put("stuMap", maps);
        return map;
    }


    public static Map<String, List<Student>> sortMapByKey(Map<String, List<Student>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, List<Student>> sortMap = new TreeMap<String, List<Student>>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    public static Map<String, Attendancevo> sortMapByKeyatt(Map<String, Attendancevo> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, Attendancevo> sortMap = new TreeMap<String, Attendancevo>(
                new MapKeyComparator2());

        sortMap.putAll(map);

        return sortMap;
    }

    public static List<AttendanceRecord> getAtts(Integer classesId, AttendanceType type) throws Exception {
        java.sql.Date date1 = new java.sql.Date(new java.util.Date().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        java.sql.Date date2 = new java.sql.Date(calendar.getTime().getTime());
        return daoProvider.get().getAttList(classesId, date1, date2, type);
    }

    public static List<Attendance> getSortList(List<Attendance> list) {
        Collections.sort(list, new Comparator<Attendance>() {
            @Override
            public int compare(Attendance o1, Attendance o2) {
                if (o1.getType() == AttendanceType.BusAttendance || o1.getType() == AttendanceType.FaceAttendance) {
                    if (o1.getAttendanceStatus() == AttendanceStatus.Arrived || o2.getAttendanceStatus() == AttendanceStatus.Arrived) {
                        if (o1.getAttendanceStatus().ordinal() < o2.getAttendanceStatus().ordinal()) {
                            return 1;
                        }
                    } else {
                        if (o1.getAttendanceStatus().ordinal() > o2.getAttendanceStatus().ordinal()) {
                            return 1;
                        }
                    }

                } else {
                    if (o1.getAttendanceStatus().ordinal() < o2.getAttendanceStatus().ordinal()) {
                        return 1;
                    }
                }
                if (o1.getAttendanceStatus().ordinal() == o2.getAttendanceStatus().ordinal()) {
                    return 0;
                }
                return -1;
            }
        });
        return list;
    }
}


class MapKeyComparator implements Comparator<String> {

    @Override
    public int compare(String str1, String str2) {

        return str1.compareTo(str2);
    }
}

class MapKeyComparator2 implements Comparator<String> {

    @Override
    public int compare(String str1, String str2) {

        return str2.compareTo(str1);
    }
}