package com.gzzm.safecampus.campus.attendance;

import net.cyan.thunwind.annotation.EnumValue;

/**
 * Created by Huangmincong on 2018/3/12.
 */
public enum SendState {

    //0、已发送 1、未发送

    @EnumValue("0")
    Sended,

    @EnumValue("1")
    Notsended;
}
