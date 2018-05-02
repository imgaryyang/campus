package com.gzzm.safecampus.device.attendance.util;

import net.cyan.commons.util.StringUtils;

/**
 * @author liyabin
 * @date 2018/3/26
 */
public class DataUtils
{
    public DataUtils()
    {
    }

    public static String bytesToHexString(byte[] src)
    {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0)
        {
            return null;
        }
        for (byte aSrc : src)
        {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2)
            {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String hexString)
    {
        if (hexString == null || hexString.equals(""))
        {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++)
        {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c)
    {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static byte encryption(char c)
    {
        byte temp = 0x00;
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 返回业务数据16进制报文
     *
     * @return
     */
    public static String getHexBusiness(byte[] data)
    {
        byte[] result = new byte[data.length - 8];
        System.arraycopy(data, 4, result, 0, result.length);
        return bytesToHexString(result);
    }

    public static String getHexDefault(int len)
    {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; i++)
        {
            result.append("00");
        }
        return result.toString();
    }

    public static byte[] getChineseByte(String chinese, int len)
    {
        byte[] result = new byte[len];
        if (StringUtils.isEmpty(chinese)) return null;
        byte[] temp = DataUtils.hexStringToBytes(getChineseHEX(chinese));
        System.arraycopy(temp, 0, result, 0, temp.length);
        return result;
    }

    public static boolean isCharacter(Object value)
    {
        return String.class.isInstance(value)
                || Character.class.isInstance(value)
                || Character.TYPE.isInstance(value);
    }

    /**
     * 获取机内码
     *
     * @param chineseName
     * @return
     */
    public static String getChineseHEX(String chineseName)
    {
        StringBuffer result = new StringBuffer();
        try
        {
            char[] chineses = chineseName.toCharArray();
            for (char chine : chineses)
            {
                if (isCharacter(String.valueOf(chine)))
                {
                    byte[] by = String.valueOf(chine).getBytes("GBK");
                    for (byte b : by)
                    {
                        result.append(Integer.toHexString(b & 0xff));
                    }
                }
                else
                {
                    byte temp = (byte) chine;
                    result.append(Integer.toHexString(temp & 0xff));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result.toString().toUpperCase();
    }


    public static void main(String[] agrs)
    {
        String data = "2\n" +
                "5\n" +
                "4\n" +
                "1\n" +
                "4\n" +
                "2\n" +
                "4";
        String s = data.replaceAll("\\n", "_");
        String[] split = s.split("_");
        int count = 0;
        for (String s1 : split)
        {
            if (StringUtils.isEmpty(s1)) continue;
            count += Integer.valueOf(s1);
        }
        System.out.println(count);
    }
}
