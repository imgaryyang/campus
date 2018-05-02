package com.gzzm.safecampus.pay.cmb;


import com.gzzm.platform.commons.*;
import com.gzzm.safecampus.campus.pay.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.json.JsonObject;
import net.cyan.commons.util.json.JsonParser;
import net.cyan.nest.annotation.*;

import java.sql.Timestamp;

/**
 * 支付、签约回调接口
 * 接收数据与数据验证
 *
 * @author yuanfang
 * @date 18-01-31 13:39
 */

@Service
public class CmbCallback
{

    @Inject
    private static Provider<PayDao> payDaoProvider;

    @Inject
    private PaymentService paymentService;

    @Inject
    private PayUtil payUtil;

    /**
     * 回调结果测试页面
     *
     * @return 测试页面
     */
    @Service(url = "/callback")
    public String test()
    {
        return "/safecampus/pay/callback.html";
    }

    /**
     * 签约回调
     *
     * @param agrNo           协议号
     * @param jsonRequestData post data
     * @return 200
     */
    @Service(url = "/cmb/callback/sign/{$0}", method = HttpMethod.post)
    @Json
    public String signCallback(String agrNo, String jsonRequestData)
    {
        Tools.debug("cmb callback sign post: " + jsonRequestData);
        try
        {
            JsonObject jsonObject = new JsonParser(jsonRequestData).parse();
            // 未加密数据
            String noticeData = jsonObject.get("noticeData").toString();
            //密文
            String sign = (String) jsonObject.get("sign");
            JsonObject jsondata = new JsonParser(noticeData).parse();
            //json 参数
            String rspCode = (String) jsondata.get("rspCode");
            String rspMsg = (String) jsondata.get("rspMsg");
            //验签
            Boolean validate = payUtil.validateJson(noticeData, sign);
            Tools.log("sign validate: " + validate);

            UserMerchantAccount account = payDaoProvider.get().getUserMerchantAccount(agrNo);
            //签约成功，保存用户UserMerchantAccount信息
            if (account != null && "SUC0000".equals(rspCode))
            {
                String dateTime = (String) jsondata.get("dateTime");
                String branchNo = (String) jsondata.get("branchNo");
                String merchantNo = (String) jsondata.get("merchantNo");
                String userID = (String) jsondata.get("userID");
                String noticeSerialNo = (String) jsondata.get("noticeSerialNo");
                //String agrNo_ = (String) jsondata.get("agrNo");

                account.setDateTime(dateTime);
                account.setBranchNo(branchNo);
                account.setUserID(userID);
                account.setMerchantNo(merchantNo);
                account.setNoticeSerialNo(noticeSerialNo);
                account.setAgrNo(agrNo);
                account.setSignTime(new Timestamp(System.currentTimeMillis()));
                if (validate)
                    account.setStatus(1);
                else
                    account.setStatus(2);
                payDaoProvider.get().save(account);
                Tools.debug("签约成功: " + validate + "-" + agrNo);
            } else
            {
                Tools.log("签约验证失败：" + agrNo + "；rspMsg：" + rspMsg + "；rspCode" + rspCode);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return "200";
    }


    /**
     * 支付结果回调
     *
     * @param orderNo         订单号
     * @param jsonRequestData post data
     * @return 200
     */

    @Service(url = "/cmb/callback/pay/{$0}", method = HttpMethod.post)
    @Json
    public String payCallback(String orderNo, String jsonRequestData)
    {

        Tools.log("cmb callback pay post: " + jsonRequestData);
        try
        {
            JsonObject jsonObject = new JsonParser(jsonRequestData).parse();
            // 未加密数据
            String noticeData = jsonObject.get("noticeData").toString();
            //密文
            String sign = (String) jsonObject.get("sign");
            JsonObject jsondata = new JsonParser(noticeData).parse();
            //验签
            Boolean validate = payUtil.validateJson(noticeData, sign);
            Tools.log("pay validate: " + validate);

            Payment payment = payDaoProvider.get().getPayment(orderNo);
            if (validate && payment != null)
            {
                //json 参数
                String dateTime = (String) jsondata.get("dateTime");
                String branchNo = (String) jsondata.get("branchNo");
                String merchantNo = (String) jsondata.get("merchantNo");
                String noticeSerialNo = (String) jsondata.get("noticeSerialNo");
                String date = (String) jsondata.get("date");
                String amount = (String) jsondata.get("amount");
                String bankDate = (String) jsondata.get("bankDate");
                String bankSerialNo = (String) jsondata.get("bankSerialNo");
                String discountFlag = (String) jsondata.get("discountFlag");
                String discountAmount = (String) jsondata.get("discountAmount");
                String cardType = (String) jsondata.get("cardType");

                payment.setDateTime(dateTime);
                payment.setDate(date);
                payment.setNoticeSerialNo(noticeSerialNo);

                //用户实际支付金额 = 订单金额 - 优惠金额
                payment.setMoney(Double.parseDouble(amount) - Double.parseDouble(discountAmount));
                //订单异常金额
                if (Double.parseDouble(amount) != payment.getAmount())
                {
                    payment.setNote(" amount:" + amount + "/" + payment.getAmount());
                }
                payment.setBankDate(bankDate);
                payment.setBankSerialNo(bankSerialNo);
                payment.setDiscountFlag(discountFlag);
                payment.setDiscountAmount(discountAmount);
                payment.setCardType(cardType);
                Timestamp timestamp = (new Timestamp(System.currentTimeMillis()));
                payment.setPayTime(timestamp);
                payment.setBillStatus(BillStatus.Finnish);

                payDaoProvider.get().save(payment);

                Bill bill = payment.getBill();
                bill.setBillStatus(BillStatus.Finnish);
                bill.setPayItemId(payment.getPayItemId());
                bill.setPayTime(timestamp);
                bill.setPaymentMethod(PaymentMethod.online);
                payDaoProvider.get().save(bill);
                //发送微信通知
                paymentService.sendPaidBillMsg(bill);
                Tools.log("支付成功: " + orderNo);
            } else
            {
                Tools.log("支付验证失败: " + orderNo);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return "200";
    }


}
