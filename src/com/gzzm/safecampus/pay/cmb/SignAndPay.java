package com.gzzm.safecampus.pay.cmb;

import com.gzzm.platform.commons.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.gzzm.safecampus.pay.cmb.PayUtil.buildParam;

/**
 * 招行一网通 支付、退款、签约、解约 接口对接
 * @author yuanfang
 * @date 18-01-31 9:31
 */

@Service
public class SignAndPay
{
    @Inject
    private static Provider<CmbConfig> cmbConfigProvider;
    
    @Service(url = "/signAndPay")
    public String prePay()
    {
        return "/safecampus/pay/PrePay.html";
    }

    @Service(url = "/refund")
    public String refund()
    {
        return "/safecampus/pay/refund.html";
    }

    /**
     * 单独签约
     *
     * @return sign
     */
    @Service(url = "/cmb/pay/sign", method = HttpMethod.post)
    @Json
    public String sign() throws Exception
    {
        String agrNo = PaySerialNoGenerator.getAgrSerialNo();
        Map<String, String> reqData = new HashMap<>();
        reqData.put("dateTime", PayUtil.getNowTime());
        reqData.put("agrNo", agrNo);
        reqData.put("merchantNo", cmbConfigProvider.get().getMerchantNo());
        reqData.put("merchantSerialNo", agrNo);
        reqData.put("branchNo", cmbConfigProvider.get().getBranchNo());
        reqData.put("noticeUrl", cmbConfigProvider.get().getServer() + "/cmb/callback/sign/" + agrNo);
        reqData.put("noticePara", "");
        //跳转我的账单页面
        reqData.put("returnUrl", "");
        String json = buildParam(reqData,cmbConfigProvider.get().getScrectKey());
        Tools.debug(json);
        String sign = PayUtil.sign(json, cmbConfigProvider.get().getScrectKey());
        Tools.debug(sign);
        return json;
    }

    /**
     * 签约+支付 ：若无签约，先签约绑定，已签约直接支付
     *
     * @param amount   支付金额
     * @param response 输出提交招行的Json
     * @throws IOException json解析异常
     */

    @Service(url = "/cmb/pay/signAndPay", method = HttpMethod.post)
    public void signAndPay(String amount,  HttpServletResponse response) throws Exception
    {

        //根据 openId 获取 agrNo 协议号
        String agrNo = "9934567890987654332";//32

        String oderNo = PaySerialNoGenerator.getOrderNo();

        Map<String, String> reqData = new HashMap<>();
        reqData.put("dateTime", PayUtil.getNowTime());
        reqData.put("branchNo", "0020");
        reqData.put("merchantNo", "010030");
        reqData.put("merchantSerialNo", oderNo.substring(8));
        reqData.put("date", PayUtil.getNowDate());
        reqData.put("orderNo", oderNo);
        reqData.put("amount", amount);
        reqData.put("payNoticeUrl", cmbConfigProvider.get().getServer() + "/cmb/callback/pay/" + oderNo);
        reqData.put("signNoticeUrl", cmbConfigProvider.get().getServer() + "/cmb/callback/sign/" + agrNo);
        reqData.put("signNoticePara", "");
        //跳转我的账单页面
        reqData.put("returnUrl", "");
        reqData.put("agrNo", agrNo);

        String json = buildParam(reqData,cmbConfigProvider.get().getScrectKey());
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(json);
    }


    /**
     * 退款
     * @param amount 金额
     * @param date 原订单时间
     * @param orderNo 订单号
     * @param desc 退款描述
     * @param response 响应
     * @throws IOException json解析异常
     */

    @Service(url = "/cmb/do/refund", method = HttpMethod.post)
    public void doRefund(String amount, String date, String orderNo, String desc,
                        HttpServletResponse response) throws IOException
    {
        Map<String, String> reqData = new HashMap<>();
        reqData.put("dateTime", PayUtil.getNowTime());
        reqData.put("branchNo", "0020");
        reqData.put("merchantNo", "010030");
        reqData.put("date", date);
        reqData.put("orderNo", orderNo);
        reqData.put("amount", amount);
        reqData.put("desc", desc);
        //  reqData.put("encrypType", "RC4");
        //reqData.put("pwd", PayUtil.RC4Encrypt(PayUtil.OPERATEPWD.getBytes(),cmbConfigProvider.get().getScrectKey().getBytes()));
        //不加密
        reqData.put("pwd", PayUtil.OPERATEPWD);
        reqData.put("encrypType", "");
        reqData.put("operatorNo", "9999");

        String result = PayUtil.postSimpleData(
                PayUtil.buildParam(reqData,cmbConfigProvider.get().getScrectKey()),
                cmbConfigProvider.get().getRefundUrl());
        Tools.debug("querySingleOrder: " + result);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(result);
    }

    /**
     * 查询用户协议
     *
     * @param response 响应
     * @throws IOException json解析异常
     */
    @Service(url = "/cmb/query/queryAgrNo", method = HttpMethod.post)
    public void queryAgrNo(HttpServletResponse response) throws IOException
    {
        //创建 agrNo
        String agrNo = "9934567890987654332";//32
        Map<String, String> reqData = new HashMap<>();
        reqData.put("dateTime", PayUtil.getNowTime());
        reqData.put("branchNo", "0020");
        reqData.put("merchantNo", "010030");
        reqData.put("txCode", "CMCX");
        reqData.put("agrNo", agrNo);
        reqData.put("merchantSerialNo", PayUtil.getNowTime());
        String result = PayUtil.postSimpleData(
                PayUtil.buildParam(reqData,cmbConfigProvider.get().getScrectKey()),
                cmbConfigProvider.get().getQueryOrderByMerchantDateUrl());
        Tools.debug("queryAgrNo: " + result);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(result);
    }

    /**
     * 取消协议
     * @param response 响应
     * @throws IOException json解析异常
     */
    @Service(url = "/cmb/query/cancelAgrNo", method = HttpMethod.post)
    public void cancelAgrNo(String openId, HttpServletResponse response) throws IOException
    {
        //创建 agrNo
        String agrNo = "9934567890987654332";//32
        Map<String, String> reqData = new HashMap<>();
        reqData.put("dateTime", PayUtil.getNowTime());
        reqData.put("branchNo", "0020");
        reqData.put("merchantNo", "010030");
        reqData.put("txCode", "CMCX");
        reqData.put("agrNo", agrNo);
        reqData.put("merchantSerialNo", PayUtil.getNowTime());
        String result = PayUtil.postSimpleData(
                PayUtil.buildParam(reqData,cmbConfigProvider.get().getScrectKey()),
                cmbConfigProvider.get().getQueryOrderByMerchantDateUrl());
        Tools.debug("cancelAgrNo: " + result);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(result);
    }

}
