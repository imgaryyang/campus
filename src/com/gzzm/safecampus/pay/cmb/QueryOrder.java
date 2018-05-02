package com.gzzm.safecampus.pay.cmb;

import com.gzzm.platform.commons.*;
import com.gzzm.safecampus.campus.account.Merchant;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * 招行一网通接口对接：按时间/记录 查询 结账/退款账单、用户
 * 招行钱包跳转接口
 *
 * @author yuanfang
 * @date 18-01-31 15:45
 */
@Service
public class QueryOrder
{
    @Inject
    private static Provider<CmbConfig> cmbConfigProvider;

    public QueryOrder()
    {
    }

    @Service(url = "/cmb/query/date", method = HttpMethod.post)
    @Json
    public String queryDate(Merchant merchant, String beginDate, String endDate, String nextKeyValue)
    {
        if (nextKeyValue == null)
            nextKeyValue = "";
        Map<String, String> reqData = new HashMap<>();
        reqData.put("dateTime", PayUtil.getNowTime());
        reqData.put("branchNo", merchant.getBranchNo());
        reqData.put("merchantNo", merchant.getMerchantNo());
        reqData.put("beginDate", beginDate);
        reqData.put("endDate", endDate);
        reqData.put("operatorNo", "9999");
        reqData.put("nextKeyValue", nextKeyValue);
        String jsonParam = PayUtil.buildParam(reqData, merchant.getSecretKey());

        String result = PayUtil.postSimpleData(
                jsonParam,
                cmbConfigProvider.get().getQueryOrderByMerchantDateUrl());

        Tools.debug("querySettledOrder: " + result);
        return result;
    }


    @Service(url = "/queryByDate")
    public String queryByDate()
    {
        return "/safecampus/pay/QueryByDate.html";
    }

    @Service(url = "/querySingle")
    public String querySingle()
    {
        return "/safecampus/pay/QuerySingleOrder.html";
    }

    /**
     * 用户钱包管理入口
     *
     * @param agrNo    用户协议号
     * @param response 用户获取ip
     * @throws IOException json转换异常
     */
    @Service(url = "/cmb/query/account")
    public void queryAccount(String agrNo, HttpServletResponse response) throws IOException
    {
        Map<String, String> reqData = new HashMap<>();
        agrNo = "9934567890987654332";

        reqData.put("branchNo", "0020");
        reqData.put("merchantNo", "010030");
        reqData.put("customerNo", agrNo);
        //项目合作编码，30位数字字母,生产环境会下发,测试环境随便写，满足30位
        reqData.put("copCode", "M12345678901234567890123456789");
        //数据异常
        //reqData.put("title", URLEncoder.encode("我的钱包", PayUtil.CHARSET));
        reqData.put("rtnLink", "www.baidu.com");
        reqData.put("chnType", "01");
        //ip
        reqData.put("ip", "113.67.224.109");

        //钱包管理, post参数名为 data，参数值需要 base64 编码
        String result = PayUtil.postData(
                "data",
                PayUtil.encoder.encode(
                        PayUtil.buildParamForQueryAccount(reqData, cmbConfigProvider.get().getScrectKey()).getBytes()),
                cmbConfigProvider.get().getAccountOutputUrl(),true);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(result);
    }

    /**
     * 查询单个记录
     *
     * @param jsonRequestData json
     * @param response        响应
     * @throws IOException json解析异常
     */
    @Service(url = "/cmb/query/singleByJson", method = HttpMethod.post)
    public void querySingleOrderByJson(String jsonRequestData, HttpServletResponse response) throws IOException
    {
        String result = PayUtil.postSimpleData(
                PayUtil.addSignParam(jsonRequestData),
                cmbConfigProvider.get().getQuerySigleOrderUrl());
        Tools.debug("querySingleOrder: " + result);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(result);
    }

    /**
     * 根据账单日期、账单号查询单个记录
     *
     * @param orderNo  账单号
     * @param date     时间
     * @param response 响应
     * @throws IOException json解析异常
     */
    @Service(url = "/cmb/query/single", method = HttpMethod.post)
    public void querySingleOrder(String orderNo, String date, HttpServletResponse response) throws IOException
    {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("dateTime", PayUtil.getNowTime());
        reqData.put("branchNo", "0020");
        reqData.put("merchantNo", "010030");
        reqData.put("type", "B");
        reqData.put("date", date);
        reqData.put("orderNo", orderNo);
        reqData.put("operatorNo", "9999");

        String result = PayUtil.postSimpleData(
                PayUtil.buildParam(reqData, cmbConfigProvider.get().getScrectKey()),
                cmbConfigProvider.get().getQuerySigleOrderUrl());
        Tools.debug("querySingleOrder: " + result);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(result);

    }

    /**
     * 按时间段查询记录
     *
     * @param jsonRequestData reqdata内容
     */
    @Service(url = "/cmb/query/querySettledOrderByJson", method = HttpMethod.post)
    public void querySettledOrderByJson(String jsonRequestData, HttpServletResponse response) throws IOException
    {
        String result = PayUtil.postSimpleData(
                PayUtil.addSignParam(jsonRequestData),
                cmbConfigProvider.get().getQueryOrderByMerchantDateUrl());
        Tools.debug("querySettledOrder: " + result);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(result);
    }

    /**
     * 按时间查询账单
     *
     * @param beginDate    开始时间
     * @param endDate      结束时间
     * @param nextKeyValue 下一页
     */
    @Service(url = "/cmb/query/querySettledOrder", method = HttpMethod.post)
    @Json
    public String querySettledOrder(String beginDate, String endDate, String nextKeyValue)
    {

        Map<String, String> reqData = new HashMap<>();
        reqData.put("dateTime", PayUtil.getNowTime());
        reqData.put("branchNo", "0020");
        reqData.put("merchantNo", "010030");
        reqData.put("beginDate", beginDate);
        reqData.put("endDate", endDate);
        reqData.put("operatorNo", "9999");
        reqData.put("nextKeyValue", nextKeyValue);

        String result = PayUtil.postSimpleData(
                PayUtil.buildParam(reqData, cmbConfigProvider.get().getScrectKey()),
                cmbConfigProvider.get().getQueryOrderByMerchantDateUrl());
        Tools.debug("querySettledOrder: " + result);

        return result;
    }


    /**
     * 按退款时间查询退款订单
     *
     * @param beginDate    开始时间
     * @param endDate      结束时间
     * @param nextKeyValue 下一页
     * @param response     响应
     */
    @Service(url = "/cmb/query/queryRefundList", method = HttpMethod.post)
    public void queryRefundList(String beginDate, String endDate, String nextKeyValue, HttpServletResponse response) throws IOException
    {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("dateTime", PayUtil.getNowTime());
        reqData.put("branchNo", "0020");
        reqData.put("merchantNo", "010030");
        reqData.put("beginDate", beginDate);
        reqData.put("endDate", endDate);
        reqData.put("operatorNo", "9999");
        reqData.put("nextKeyValue", nextKeyValue);

        String result = PayUtil.postSimpleData(
                PayUtil.buildParam(reqData, cmbConfigProvider.get().getScrectKey()),
                cmbConfigProvider.get().getQueryOrderByMerchantDateUrl());
        Tools.debug("queryRefundList: " + result);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(result);
    }

    //发送JSON字符串 如果成功则返回成功标识。
    public static String doJsonPost(String urlPath, String Json)
    {
        String result = "";
        BufferedReader reader = null;
        try
        {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("accept", "application/json");
            // 往服务器里面发送数据
            if (Json != null && !StringUtils.isEmpty(Json))
            {
                byte[] writebytes = Json.getBytes();
                // 设置文件长度
                conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = conn.getOutputStream();
                outwritestream.write(Json.getBytes());
                outwritestream.flush();
                outwritestream.close();
                Tools.debug("doJsonPost: conn" + conn.getResponseCode());
            }
            if (conn.getResponseCode() == 200)
            {
                reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                result = reader.readLine();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
