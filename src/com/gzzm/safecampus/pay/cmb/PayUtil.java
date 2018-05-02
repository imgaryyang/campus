package com.gzzm.safecampus.pay.cmb;

import com.gzzm.platform.commons.*;
import com.gzzm.safecampus.pay.job.CmbPublicKeySycJob;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.json.JsonObject;
import net.cyan.commons.util.json.JsonParser;
import net.cyan.commons.util.json.JsonSerializer;
import net.cyan.nest.annotation.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 招行一网通接口对接，工具类
 * 数据封装、加密、请求
 *
 * @author yuanfang
 * @date 18-01-31 9:33
 */

public class PayUtil
{
    @Inject
    private static Provider<CmbConfig> cmbConfigProvider;

    // 测试钱包可用商户
    public static String SECRET_KEY_ACC = "1234567890abcABC";//1234567890abcABC//666666aaaaAAAAAA

    //退款用
    public static String OPERATEPWD = "010030";

    //提交数据jsonRequestData前缀
    private static String jsonRequestDataPre = "{\"version\":\"1.0\",\"charset\":\"UTF-8\",\"signType\":\"SHA-256\"}";

    //公钥
    public static String publicKey;

    public static final BASE64Encoder encoder = new BASE64Encoder();

    public static final BASE64Decoder decoder = new BASE64Decoder();

    public static void setPublicKey(String publicKey)
    {
        PayUtil.publicKey = publicKey;
    }

    /*
      若 publicKey 为空，则 获取 公钥
       仅作为 定时获取 公钥的 Job 未执行前的补充
     */
    static
    {
        Tools.log("初始化公钥");
        new Thread(new CmbPublicKeySycJob()).start();
    }

    /**
     * 发送POST请求-通用版
     * 默认参数名为  jsonRequestData
     *
     * @param jsonParam json 字符串
     * @param url       url
     * @return 网页返回值
     */
    public static String postSimpleData(String jsonParam, String url)
    {
        return postData("jsonRequestData", jsonParam, url, false);
    }

    public static String postWebviewData(String jsonParam, String url)
    {
        return postData("jsonRequestData", jsonParam, url, true);
    }


    /**
     * 发送POST请求
     *
     * @param url 网址
     * @return 返回数据
     */
    public static String postData(String paramName, String data, String url, Boolean wrap)
    {
        Tools.log("POST URL: " + url);
        String charset = "UTF-8";//cmbConfigProvider.get().getChartset();
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try
        {
            URL httpUrl = new URL(url);
            HttpURLConnection urlCon = (HttpURLConnection) httpUrl.openConnection();
            Tools.debug(httpUrl.toString()+"-"+urlCon.toString());
            urlCon.setRequestMethod("POST");
            urlCon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setReadTimeout(5 * 1000);
            out = new OutputStreamWriter(urlCon.getOutputStream(), charset);// 指定编码格式
            out.write(paramName + "=" + data);
            Tools.debug("POST DATA: " + paramName + "=" + data);
            out.flush();
            in = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), charset));
            String str;
            while ((str = in.readLine()) != null)
            {
                if (wrap)
                    //换行，否则html页面js可能由于缺少分号出错
                    str += "\n";
                result.append(str);
            }
        } catch (IOException e)
        {
            Tools.debug(PayUtil.getTrace(e));
        } finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            } catch (IOException e)
            {
                Tools.debug(PayUtil.getTrace(e));
            }
        }
        return result.toString();
    }

    public static BufferedReader postDataReader(String data, String url)
    {
        String charset = "UTF-8";
        OutputStreamWriter out;
        BufferedReader in = null;
        try
        {
            URL httpUrl = new URL(url);
            HttpURLConnection urlCon = (HttpURLConnection) httpUrl.openConnection();
            urlCon.setRequestMethod("POST");
            urlCon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setReadTimeout(5 * 1000);
            out = new OutputStreamWriter(urlCon.getOutputStream(), charset);// 指定编码格式
            out.write("jsonRequestData=" + data);
            out.flush();
            //招行返回的是utf-8
            in = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), "utf-8"));

        } catch (IOException e)
        {
            Tools.debug(PayUtil.getTrace(e));
        }
        return in;
    }

    /**
     * 钱包管理, post参数名为 data，参数值需要 base64 编码
     *
     * @param reqData   请求
     * @param secretKey 商户秘钥
     * @return 响应值
     */
    public static String loginWallet(Map reqData, String secretKey)
    {
        return PayUtil.postData("data",
                PayUtil.encoder.encode(PayUtil.buildParamForQueryAccount(reqData, secretKey).getBytes()),
                cmbConfigProvider.get().getAccountOutputUrl(), true);
    }

    public static String openAccount(Map reqData, String secretKey, String copCode)
    {
        String url = cmbConfigProvider.get().getOpenAccountUrl() + copCode;
        return PayUtil.postWebviewData(
                PayUtil.buildParam(reqData, secretKey), url);
    }

    /**
     * 使用商户秘钥 给报文添加签名，封装报文
     *
     * @param reqJSON 报文
     * @return 封装后的报文
     */
    public static String addSignParam(String reqJSON)
    {
        try
        {
            JsonObject param = new JsonParser(PayUtil.jsonRequestDataPre).parse();
            String sign = sign(reqJSON, cmbConfigProvider.get().getScrectKey());
            param = putJson(param, "sign", sign, false);
            param = putJson(param, "reqData", reqJSON, true);
            return param.toString();
        } catch (Exception e)
        {
            Tools.debug(PayUtil.getTrace(e));
        }
        return "";
    }

    /**
     * 将 Map 数据序列化为 Json，同时添加头部，加密签名
     *
     * @param reqDataMap 键值对
     * @return 完整可用的 Json 数据
     */
    public static String buildParam(Map<String, String> reqDataMap, String secretKey)
    {
        try
        {
            JsonSerializer jsonSerializer = new JsonSerializer();
            Map<String, Object> jsonParam = new HashMap<>();
            jsonParam.put("version", "1.0");
            jsonParam.put("charset", "UTF-8");// 支持GBK和UTF-8两种编码
            jsonParam.put("sign", sign(reqDataMap, secretKey));
            jsonParam.put("signType", "SHA-256");
            jsonParam.put("reqData", reqDataMap);
            jsonSerializer.serialize(jsonParam);
            Tools.log("build: " + jsonSerializer.toString());
            return jsonSerializer.toString();
        } catch (Exception e)
        {
            Tools.log("序列化錯誤");
            Tools.debug(PayUtil.getTrace(e));
            return "";
        }
    }

    /**
     * 将 Map 数据序列化为 Json，同时添加头部，加密签名
     * 钱包管理专用
     *
     * @param reqDataMap 键值对
     * @return 完整可用的 Json 数据
     */
    public static String buildParamForQueryAccount(Map<String, String> reqDataMap, String secretKey)
    {
        return "{\"funcid\":\"" + cmbConfigProvider.get().getFuncid()
                + "\",\"param\":" + buildParam(reqDataMap, secretKey) + "}";
    }

    /**
     * json 拼接
     *
     * @param jsonObject 被拼接的 json 对象
     * @param key        拼接的键
     * @param value      拼接的值
     * @param isSub      是否为子
     * @return 拼接完成的 json 对象
     * @throws Exception json转换异常
     */
    public static JsonObject putJson(JsonObject jsonObject, String key, String value, Boolean isSub) throws Exception
    {
        String json = jsonObject.toString();
        //将String变成StringBuilder，字符串可编辑模式
        StringBuilder sb = new StringBuilder(json);
        if (isSub)
            sb.insert(json.indexOf("}"), ",\"" + key + "\":" + value);//插入
        else
            sb.insert(json.indexOf("}"), ",\"" + key + "\":\"" + value + "\"");//插入
        Tools.log(sb.toString());
        return new JsonParser(sb.toString()).parse();
    }

    /**
     * 对参数签名：
     * 对reqData所有请求参数按从a到z的字典顺序排列，如果首字母相同，按第二个字母排列，
     * 排序完成后按将所有键值对以“&”符号拼接
     * 拼接完成后再加上商户密钥
     *
     * @param reqDataMap 请求参数
     * @param secretKey  商户密钥
     */
    public static String sign(Map<String, String> reqDataMap, String secretKey)
    {
        StringBuilder buffer = new StringBuilder();
        List<String> keyList = sortParams(reqDataMap);
        for (String key : keyList)
        {
            buffer.append(key).append("=").append(reqDataMap.get(key)).append("&");
        }
        buffer.append(secretKey);
        //Tools.debug("encode: " + buffer.toString());

        try
        {
            // 创建加密对象
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            // 传入要加密的字符串,按指定的字符集将字符串转换为字节流
            messageDigest.update(buffer.toString().getBytes("UTF-8"));
            byte byteBuffer[] = messageDigest.digest();

            // 將 byte转换为16进制string
            return byteToHexString(byteBuffer);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            Tools.debug(PayUtil.getTrace(e));
        }
        return "";
    }

    /**
     * 对参数用秘钥签名
     * 对待验签的所有请求参数按从 a 到 z 的字典顺序排列，如果首字母相同，按第二个字母排列。
     * 排序完成后按将所有键值对以 “&” 符号拼接
     * 将商户签名密钥增加 “&” 附加到待签名字符串后，按指定签名算法进行运算
     * 请求参数值中有特殊字符和汉字，如 &、@等，需进行 URLEncoding 处理
     *
     * @param reqDataJSON 请求的json数据
     * @param secretKey   秘钥
     * @return 签名
     */
    public static String sign(String reqDataJSON, String secretKey)
    {
        StringBuilder buffer = new StringBuilder();
        try
        {
            JsonObject json = new JsonParser(reqDataJSON).parse();
            List<String> keyList = sortParams(json);
            for (String key : keyList)
            {
                buffer.append(key).append("=").append(json.get(key)).append("&");
            }
            buffer.append(secretKey);
            // Tools.debug("encode: " + buffer.toString());
            // 创建加密对象
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            // 传入要加密的字符串,按指定的字符集将字符串转换为字节流
            messageDigest.update(buffer.toString().getBytes("UTF-8"));
            byte byteBuffer[] = messageDigest.digest();

            // 將 byte转换为16进制string
            return byteToHexString(byteBuffer);
        } catch (Exception e)
        {
            Tools.debug(PayUtil.getTrace(e));
        }
        return "";
    }

    /**
     * 对参数按字典顺序排序，不区分大小写
     *
     * @param reqDataMap map 数据
     * @return 将 map 排序完成得到的的 List
     */
    private static List<String> sortParams(Map<String, String> reqDataMap)
    {
        List<String> list = new ArrayList<>(reqDataMap.keySet());
        return sort(list);
    }

    /**
     * 对 Json 参数排序
     *
     * @param json json 对象
     * @return list
     */
    public static List<String> sortParams(JsonObject json)
    {
        List<String> list = new ArrayList<>();
        //遍历键值对
        Iterator it = json.keySet().iterator();
        while (it.hasNext())
        {
            list.add((String) it.next());
        }
        return sort(list);
    }

    public static List<String> sort(List<String> list)
    {
        Collections.sort(list, new Comparator<String>()
        {
            public int compare(String o1, String o2)
            {
                String[] temp = {o1.toLowerCase(), o2.toLowerCase()};
                Arrays.sort(temp);
                if (o1.equalsIgnoreCase(temp[0]))
                {
                    return -1;
                } else if (temp[0].equalsIgnoreCase(temp[1]))
                {
                    return 0;
                } else
                {
                    return 1;
                }
            }
        });
        return list;
    }

    /**
     * 获取当前时间戳 -年月日时分秒
     * yyyyMMddHHmmss
     *
     * @return 时间字符串
     */
    public static String getNowTime()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date());
    }

    /**
     * 获取当前时间 -年月日
     * yyyyMMdd
     *
     * @return 时间字符串
     */
    public static String getNowDate()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date());
    }

    /**
     * DES加密
     *
     * @param plain 原文
     * @param key   秘钥
     * @return 密文
     */
    public static String DesEncrypt(byte[] plain, byte[] key)
    {
        try
        {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKeySpec = new DESKeySpec(key);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKeySpec);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");// DES/ECB/PKCS5Padding
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            // 现在，获取数据并加密
            // 正式执行加密操作
            byte[] byteBuffer = cipher.doFinal(plain);
            // 將 byte转换为16进制string
            return byteToHexString(byteBuffer);
        } catch (Throwable e)
        {
            Tools.debug(PayUtil.getTrace(e));
        }
        return null;
    }

    /**
     * 將 byte转换为16进制string
     *
     * @param byteBuffer byte
     * @return string
     */
    private static String byteToHexString(byte[] byteBuffer)
    {
        StringBuilder strHexString = new StringBuilder();

        for (byte aByteBuffer : byteBuffer)
        {
            String hex = Integer.toHexString(0xff & aByteBuffer);
            if (hex.length() == 1)
            {
                strHexString.append('0');
            }
            strHexString.append(hex);
        }
        return strHexString.toString();
    }

    /**
     * RC4加密
     *
     * @param plain 原文
     * @param key   秘钥
     * @return 密文
     */
    public static String RC4Encrypt(byte[] plain, byte[] key)
    {
        try
        {
            SecretKey secretKey = new SecretKeySpec(key, "RC4");
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("RC4");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // 现在，获取数据并加密
            // 正式执行加密操作
            byte[] byteBuffer = cipher.doFinal(plain);
            // 將 byte转换为16进制string
            return byteToHexString(byteBuffer);
        } catch (Exception e)
        {
            Tools.debug(PayUtil.getTrace(e));
        }
        return "";
    }

    /**
     * 对Json进行验证；
     *
     * @param noticeData json原文
     * @param sign       加密密文
     * @return 验证结果 true/false
     * @throws Exception json转换异常
     */
    public Boolean validateJson(String noticeData, String sign) throws Exception
    {

        //对参数排序、拼接
        StringBuilder buffer = new StringBuilder();
        JsonObject json = new JsonParser(noticeData).parse();
        List<String> keyList = PayUtil.sortParams(json);
        for (String key : keyList)
        {
            buffer.append(key).append("=").append(json.get(key)).append("&");
        }
        String sortData = buffer.toString();
        sortData = sortData.substring(0, sortData.length() - 1);
        Tools.debug("toSign_str : " + sortData);
        Tools.debug("sign: " + sign);
        Tools.debug("public key: " + PayUtil.publicKey);

        if (PayUtil.publicKey == null)
        {
            new Thread(new CmbPublicKeySycJob()).start();
            Thread.sleep(3000);
        }
        if (PayUtil.publicKey == null)
        {
            Tools.log("公钥获取失败");
        }
        return PayUtil.isValidSignature(sortData, sign, PayUtil.publicKey);
    }

    /**
     * 报文签名，使用招行私钥对 noticeData 内的数据进行签名；
     * 商户需使用招行公钥验签。
     *
     * @param strToSign 待验证签名字符串
     * @param strSign   签名结果
     * @param publicKey 公钥
     * @return 真假
     */

    public static boolean isValidSignature(String strToSign, String strSign, String publicKey)
    {
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = decoder.decodeBuffer(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature
                    .getInstance("SHA1WithRSA");
            signature.initVerify(pubKey);
            signature.update(strToSign.getBytes("UTF-8"));
            return signature.verify(decoder.decodeBuffer(strSign));
        } catch (Exception e)
        {
            Tools.debug(PayUtil.getTrace(e));
        }
        return false;
    }

    public static String getTrace(Throwable t) {
        StringWriter stringWriter= new StringWriter();
        PrintWriter writer= new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer= stringWriter.getBuffer();
        return buffer.toString();
    }

}
