package com.gzzm.safecampus.device.attendance.util;

import com.gzzm.platform.commons.Tools;
import com.gzzm.safecampus.device.attendance.MacSecurityUtils;
import net.cyan.commons.util.StringUtils;

/**
 * @author liyabin
 * @date 2018/3/27
 */
public abstract class ParseDataUtils
{

    public String createResponse()
    {
        return null;
    }

    public static byte[] parseData(byte[] data, String key)
    {
        if (data != null && StringUtils.isEmpty(key)) return null;

        return data;
    }

    public static String encryption(byte[] data, String key) throws Exception
    {
        if (data != null && StringUtils.isEmpty(key)) return null;
        String hexKey = MacSecurityUtils.bytesToHexString(key.getBytes());
        return MacSecurityUtils.macEncrypt(data, hexKey);
    }

    public static String encryption(byte[] data) throws Exception
    {
        return encryption(data, Tools.getMessage("MacSecurity.key")).substring(0, 8);
    }

    public static String encryptionStr(String data) throws Exception
    {
        byte[] parameter = MacSecurityUtils.hexStringToBytes(data);
        return encryption(parameter, Tools.getMessage("MacSecurity.key"));
    }

    public static boolean checkRequestData(byte[] data, String key) throws Exception
    {
        String reqMac = MacSecurityUtils.bytesToHexString(data, data.length - 4, 4);
        byte[] parameter = new byte[data.length - 4];
        System.arraycopy(data, 0, parameter, 0, data.length - 4);
        String mac = MacSecurityUtils.macEncrypt(parameter, key);
        String substring = mac.substring(0, 8);
        return reqMac.equals(substring);
    }

    public static boolean checkRequestData(byte[] data) throws Exception
    {
        return checkRequestData(data, Tools.getMessage("MacSecurity.key"));
    }
}
