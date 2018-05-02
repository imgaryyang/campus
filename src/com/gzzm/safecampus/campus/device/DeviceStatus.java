package com.gzzm.safecampus.campus.device;

import net.cyan.commons.util.AdvancedEnum;
import net.cyan.thunwind.annotation.Entity;

/**
 * 硬件状态
 *
 * @author yiuman
 * @date 2018/3/14
 */
public class DeviceStatus implements AdvancedEnum<String> {

    /**
     * 状态码
     */
    private String code;

    /**
     * 状态名
     */
    private String name;

    @Override
    public String valueOf() {
        return code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
