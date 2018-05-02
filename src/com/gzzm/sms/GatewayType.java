package com.gzzm.sms;

/**
 * 短信网关类型
 *
 * @author camel
 * @date 2010-11-4
 */
public enum GatewayType
{
    mobile,

    union,

    telecom,

    common;

    @Override
    public String toString()
    {
        switch (this)
        {
            case mobile:
                return "移动网关";
            case union:
                return "联通网关";
            case telecom:
                return "电信网关";
            case common:
                return "通用网关";
        }

        return null;
    }
}
