package com.gzzm.safecampus.device.attendance;

import javax.crypto.Cipher;
import javax.crypto.spec.*;
import java.util.*;

/**
 * @author zy
 * @date 2018/3/26 17:53
 */
public class MacSecurityUtils
{
    public MacSecurityUtils()
    {
    }

    public static String macEncrypt(byte[] src, String secretKey) throws Exception
    {
        List<byte[]> srcList = getEightByteList(src);
        byte[] ret = new byte[8];
        for (byte[] aSrcList : srcList)
        {
            ret = byteXOR(ret, aSrcList);
            ret = encryption(ret, secretKey.substring(0, 16));
        }
        ret = decode(ret, secretKey.substring(16, 32));
        ret = encryption(ret, secretKey.substring(0, 16));
        return bytesToHexString(ret);
    }

    public static byte[] threeEncrypt(String key, byte[] bData, int mode)
    {
        byte[] start = new byte[8];
        //这里必须将16字节的密钥变为24字节的密钥，方法如ECB的处理方法一样
        if (key.length() == 16)
        {
            key = key + key + key;
        }
        // 将传过来的key和data转换为byte型
        byte[] bKey = hexStringToBytes(key);
        //byte[] bData = hexStringToBytes(data);
        String ds = "";
        // 创建一个DESKeySpec对象
        SecretKeySpec spec = new SecretKeySpec(bKey, "DESede");
        byte[] cData = null;
        // Cipher对象实际完成加密操作
        try
        {
            Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");//三DES算法这里必须使用DESede，单DES可以使用DES但必须是ECB模式
            // 实例化cipher
            cipher.init(mode, spec, new IvParameterSpec(start));
            // 真正的开始加密
            cData = cipher.doFinal(bData);
            // 将加密后的数据，转化为16进制
            //ds = HexConversionUtil.Bytes2HexString(cData);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return cData;
    }

    private static byte[] decode(byte[] src, String secretKey) throws Exception
    {
//        return encrypt(hexStringToBytes(secretKey), src);
        return threeEncrypt(secretKey, src, Cipher.DECRYPT_MODE);
    }

    private static byte[] encryption(byte[] src, String secretKey)
    {
//        return encrypt(hexStringToBytes(secretKey), src);
        return threeEncrypt(secretKey, src, Cipher.ENCRYPT_MODE);
    }

    private static byte[] byteXOR(byte[] src1, byte[] src2)
    {
        byte[] result = new byte[8];
        for (int i = 0; i < 8; i++)
        {
            result[i] = (byte) (src1[i] ^ src2[i]);
        }
        return result;
    }

    private static List<byte[]> getEightByteList(byte[] src)
    {
        List<byte[]> bytes = new ArrayList<>();
        if (src.length == 8)
        {
            bytes.add(src);
            byte[] def = hexStringToBytes("8000000000000000");
            bytes.add(def);
        }
        else if (src.length < 8)
        {
            byte[] def = new byte[8];
            for (int i = 0; i < 8; i++)
            {
                if (i < src.length)
                {
                    def[i] = src[i];
                }
                else if (i == src.length)
                {
                    def[i] = -128;
                }
                else
                {
                    def[i] = 0;
                }
            }
            bytes.add(def);
        }
        else
        {
            byte[] def = new byte[8];
            System.arraycopy(src, 0, def, 0, 8);
            bytes.add(def);
            byte[] bs = new byte[src.length - 8];
            System.arraycopy(src, 8, bs, 0, bs.length);
            bytes.addAll(getEightByteList(bs));
        }
        return bytes;
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

    public static String bytesToHexString(byte[] src, int starIndex, int len)
    {
        byte[] data = new byte[len];
        System.arraycopy(src, starIndex, data, 0, len);
        return bytesToHexString(data);
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


}
