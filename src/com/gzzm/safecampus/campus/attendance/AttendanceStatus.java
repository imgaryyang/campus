package com.gzzm.safecampus.campus.attendance;

import net.cyan.thunwind.annotation.EnumValue;

/**
*考勤情况
*
*@author Huangmincong
*@date 2018/3/14
*/
public enum AttendanceStatus {

    // 0、已到 1、迟到 2、请假 3、未到 4、其他 5、上午上车 6、上午下车 7、下午上车 8、下午下车 9、上午进校 10、中午出校 11、中午进校 12、下午出校

    @EnumValue("0")
    Arrived,

    @EnumValue("1")
    Late,

    @EnumValue("2")
    Leave,

    @EnumValue("3")
    Notarrived,

    @EnumValue("4")
    Others,

    @EnumValue("5")
    MorningGetOn,

    @EnumValue("6")
    MorningGetOff,

    @EnumValue("7")
    AfternoonGetOn,

    @EnumValue("8")
    AfternoonGetOff,

    @EnumValue("9")
    MorningComeIn,

    @EnumValue("10")
    NoonOut,

    @EnumValue("11")
    NoonComeIn,

    @EnumValue("12")
    AfterNoonOut;
}
