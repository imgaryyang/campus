package com.gzzm.safecampus.campus.attendance;

import net.cyan.thunwind.annotation.EnumValue;

public enum AttendanceType {

    //0、班级考勤 1、校巴考勤 2、午休考勤 3、托管考勤 4、校门考勤

    @EnumValue("0")
    ClassAttendance,

    @EnumValue("1")
    BusAttendance,

    @EnumValue("2")
    SiestaAttendance,

    @EnumValue("3")
    TruAttendance,

    @EnumValue("4")
    FaceAttendance;
}
