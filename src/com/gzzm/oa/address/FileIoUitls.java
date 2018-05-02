package com.gzzm.oa.address;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.StringUtils;


import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 输出vrf格式工具
 *
 * @author ljb
 * @date 2017/2/20 0020
 */
public class FileIoUitls {
    public FileIoUitls() {
    }

    public static InputFile getVRF(Map<String, String> data,String fileName) throws Exception {
        if (data != null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("BEGIN:VCARD");
            stringBuffer.append("\r\n");
            stringBuffer.append("VERSION:2.1");
            stringBuffer.append("\r\n");
            for (String key : data.keySet()) {
                stringBuffer.append(key + ":" + qpEncodeing(data.get(key)));
                stringBuffer.append("\r\n");
            }
            stringBuffer.append("\r\n");
            stringBuffer.append("END:VCARD");
            stringBuffer.append("\r\n");
            return new InputFile(stringBuffer.toString().getBytes(), fileName+".vcf");
        }
        return null;
    }

    /**
     * 转化为全部大写
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String toAllUpper(String str) throws Exception {
        if (str != null) {
            StringBuffer stringBuffer = new StringBuffer();
            char chars[] = StringUtils.toCharArray(new StringBuffer(str));
            for (char chr : chars) {
                stringBuffer.append(Character.toUpperCase(chr));
            }
            return stringBuffer.toString();
        }
        return null;
    }

    /*
     * 编码
     */
    public static String qpEncodeing(String str) {
        char[] encode = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < encode.length; i++) {
            if ((encode[i] >= '!') && (encode[i] <= '~') && (encode[i] != '=') && (encode[i] != '\n')) {
                sb.append(encode[i]);
            } else if (encode[i] == '=') {
                sb.append("=3D");

            } else if (encode[i] == '\n') {

                sb.append("\n");

            } else {

                StringBuffer sbother = new StringBuffer();

                sbother.append(encode[i]);

                String ss = sbother.toString();

                byte[] buf = null;

                try {
                    buf = ss.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Tools.log(e);
                }
                if (buf.length == 3) {
                    for (int j = 0; j < 3; j++) {
                        String s16 = String.valueOf(Integer.toHexString(buf[j]));
                        // 抽取中文字符16进制字节的后两位,也就是=E8等号后面的两位,
                        // 三个代表一个中文字符
                        char c16_6;
                        char c16_7;
                        if (s16.charAt(6) >= 97 && s16.charAt(6) <= 122) {
                            c16_6 = (char) (s16.charAt(6) - 32);
                        } else {
                            c16_6 = s16.charAt(6);
                        }
                        if (s16.charAt(7) >= 97 && s16.charAt(7) <= 122) {
                            c16_7 = (char) (s16.charAt(7) - 32);
                        } else {
                            c16_7 = s16.charAt(7);
                        }
                        sb.append("=" + c16_6 + c16_7);

                    }

                }
            }
        }
        return sb.toString();

    }


}
