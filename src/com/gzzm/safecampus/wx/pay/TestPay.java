package com.gzzm.safecampus.wx.pay;

import com.gzzm.platform.commons.*;
import com.gzzm.safecampus.campus.account.Merchant;
import com.gzzm.safecampus.campus.pay.Bill;
import com.gzzm.safecampus.campus.pay.BillDao;
import com.gzzm.safecampus.campus.pay.BillStatus;
import com.gzzm.safecampus.campus.pay.Payment;
import com.gzzm.safecampus.pay.cmb.*;
import com.gzzm.safecampus.pay.job.BillTimeoutJob;
import com.gzzm.safecampus.pay.job.CmbPublicKeySycJob;
import com.gzzm.safecampus.wx.personal.WxAuthDao;
import com.gzzm.safecampus.wx.user.WxUserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;

import static com.gzzm.safecampus.pay.cmb.PayUtil.buildParam;

/**
 * 支付测试
 *
 * @author yuanfang
 * @date 18-04-12 11:55
 */
@Service
public class TestPay
{
    @Inject
    private WxUserOnlineInfo wxUserOnlineInfo;

    @Inject
    private CmbConfig cmbConfig;

    @Inject
    private PayDao payDao;

    @Inject
    private BillDao billDao;

    @Inject
    private WxAuthDao wxAuthDao;

    private String bindCardUrl = "http://121.15.180.66:801/mobilehtml/DebitCard/M_NetPay/OneNetRegister/NP_BindCard.aspx";

    private Integer billId;

    private List<Bill> allBills = new ArrayList<>();

    public Integer getBillId()
    {
        return billId;
    }

    public void setBillId(Integer billId)
    {
        this.billId = billId;
    }

    public List<Bill> getAllBills()
    {
        return allBills;
    }

    public void setAllBills(List<Bill> allBills)
    {
        this.allBills = allBills;
    }

    /**
     * 设账单过期
     */
    @Service(url = "/timeout")
    public void timeout()
    {
        new Thread(new BillTimeoutJob()).start();
    }

    /**
     * 手动更新公钥
     */
    @Service(url = "/test/key")
    public void getPublicKey()
    {
        new Thread(new CmbPublicKeySycJob()).start();
    }

    @Service(url = "/test/pay/allbills")
    public String showAllBills() throws Exception
    {
        Integer[] studentIds = {2382, 2372, 2361, 2282};
        allBills = billDao.getBillsByStudentIds(studentIds);
        return "/safecampus/wx/pay/allbill.ptl";
    }

    @Service(url = "/test/pay/payment/{$0}")
    public String testWxPay(Integer billId)
    {
        this.billId = billId;
        return "/safecampus/wx/pay/wxpayjump.ptl";
    }

    /**
     * 发起支付
     *
     * @param billId 账单ID
     * @return 发送给支付接口的Json
     * @throws Exception 数据库查询异常
     */
    @Service(url = "/test/pay/payment/simple/{$0}")
    @Json
    public String simplePay(Integer billId) throws Exception
    {
        Bill bill = billDao.getBillById(billId);
        Merchant merchant = payDao.getMerchant(bill.getDeptId());
        //生成报文
        Map<String, String> reqData = paymentPostDataSerial(bill);
        //封装报文
        return buildParam(reqData, merchant.getSecretKey());
    }


    /**
     * ------测试-------
     * 我的钱包 - 单独签约
     * 一网通账户无当前用户时，先签约
     *
     * @param response 响应
     * @throws Exception json解析异常
     */
    @Service(url = "/openaccount")
    public void myWalletOpenAccountTest(HttpServletResponse response) throws Exception
    {
        String agrNo = PaySerialNoGenerator.getAgrSerialNo();
        List<Merchant> merchants = payDao.getMerchants();
        Merchant merchant = null;
        for (Merchant m : merchants)
        {
            if ("M12345678901234567890123456789".equals(m.getCopCode()))
            {
                merchant = m;
                break;
            }
        }
        if (merchant != null)
        {
            String branchNo = merchant.getBranchNo();
            String secretKey = merchant.getSecretKey();
            String merchantNo = merchant.getMerchantNo();
            String copCode = merchant.getCopCode();
            String dateTime = PayUtil.getNowTime();

            Map<String, String> reqData = new HashMap<>();
            reqData.put("dateTime", dateTime);
            reqData.put("agrNo", agrNo);
            reqData.put("merchantNo", merchantNo);
            reqData.put("merchantSerialNo", agrNo);
            reqData.put("branchNo", branchNo);
            reqData.put("noticeUrl", cmbConfig.getServer() + "/cmb/callback/sign/" + agrNo);
            reqData.put("returnUrl", cmbConfig.getServer() + "/wx/mywallet/index");
            reqData.put("signprd", "PROD0001");
            reqData.put("name", "小明");
            reqData.put("idno", "440104190001179819");
            Tools.debug("单独签约： " + agrNo);
            //模拟请求，获得请求结果
            String result =htmlJump(PayUtil.buildParam(reqData, secretKey), cmbConfig.getOpenAccountUrl() + copCode);
            response.setHeader("content-type", "text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(result);
        } else
        {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("暂无商户");
        }

    }

    @Service(url = "/openaccount2")
    public void myWalletOpenAccount2Test(HttpServletResponse response) throws Exception
    {
        String agrNo = PaySerialNoGenerator.getAgrSerialNo();
        List<Merchant> merchants = payDao.getMerchants();
        Merchant merchant = null;
        for (Merchant m : merchants)
        {
            if ("M12345678901234567890123456789".equals(m.getCopCode()))
            {
                merchant = m;
                break;
            }
        }
        if (merchant != null)
        {
            String branchNo = merchant.getBranchNo();
            String secretKey = merchant.getSecretKey();
            String merchantNo = merchant.getMerchantNo();
            String dateTime = PayUtil.getNowTime();

            Map<String, String> reqData = new HashMap<>();
            reqData.put("dateTime", dateTime);
            reqData.put("agrNo", agrNo);
            reqData.put("merchantNo", merchantNo);
            reqData.put("merchantSerialNo", agrNo);
            reqData.put("branchNo", branchNo);
            reqData.put("noticeUrl", cmbConfig.getServer() + "/cmb/callback/sign/" + agrNo);
            reqData.put("noticePara", "");
            //跳转我的账单页面
            reqData.put("returnUrl", "/wx/mywallet/index");
            String json = buildParam(reqData, secretKey);
            Tools.log("bindCardUrl: " + cmbConfig.getBindCardUrl());
            if (cmbConfig.getBindCardUrl() != null)
                bindCardUrl = cmbConfig.getBindCardUrl();
            String result = htmlJump(json, bindCardUrl);
            response.setHeader("content-type", "text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(result);
        } else
        {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("暂无商户");
        }

    }

    /**
     * 登录默认一网通账号
     *
     * @param response
     * @throws Exception
     */

    @Service(url = "/loginDefault")
    public void myWalletLoginAccountTest(HttpServletResponse response) throws Exception
    {
        QueryOrder queryOrder = new QueryOrder();
        queryOrder.queryAccount("", response);
    }


    /**
     * 由账单信息 生成支付报文格式化为Json字符串
     *
     * @param bill 账单
     * @return 支付报文
     * @throws Exception 数据库查询异常
     */
    public Map<String, String> paymentPostDataSerial(Bill bill) throws Exception
    {

        Integer deptId = bill.getDeptId();

        Merchant merchant = payDao.getMerchant(deptId);
        //获取发起缴费的用户的一网通协议号、商户信息
        String agrNo = "9934567890987654332";
        String branchNo = merchant.getBranchNo();
        String merchantNo = merchant.getMerchantNo();
        String merchantSerialNo = "";
        //无协议的首次签约，生成协议号、协议流水号，注册一网通账户
        if (agrNo == null)
        {
            agrNo = PaySerialNoGenerator.getAgrSerialNo();
            merchantSerialNo = PaySerialNoGenerator.getMerchantSerialNo();
            UserMerchantAccount account = new UserMerchantAccount();
            account.setWxUserId(1);
            account.setAgrNo(agrNo);
            account.setMerchantSerialNo(merchantSerialNo);
            account.setMerchantId(merchant.getMerchantId());
            account.setCreateTime(new Timestamp(System.currentTimeMillis()));
            payDao.save(account);

        }
        //生成支付信息
        String orderNo = PaySerialNoGenerator.getOrderNo();
        String dateTime = PayUtil.getNowTime();
        String date = PayUtil.getNowDate();
        Double amount = bill.getMoney();

        Payment payment = new Payment();
        payment.setDateTime(dateTime);
        payment.setBranchNo(branchNo);
        payment.setMerchantNo(merchantNo);
        payment.setDate(date);
        payment.setOrderNo(orderNo);
        payment.setAmount(amount);
        payment.setAgrNo(agrNo);
        payment.setBillId(bill.getBillId());
        payment.setPayItemId(bill.getPayItemId());
        // payment.setMerchantSerialNo(merchantSerialNo);
        payment.setStudentId(bill.getStudentId());
        payment.setBillStatus(BillStatus.Wait);
        payment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        payDao.save(payment);

        //生成支付报文
        Map<String, String> reqData = new HashMap<>();

        reqData.put("dateTime", dateTime);
        reqData.put("branchNo", branchNo);
        reqData.put("merchantNo", merchantNo);
        reqData.put("merchantSerialNo", merchantSerialNo);
        reqData.put("date", date);
        reqData.put("orderNo", orderNo);
        reqData.put("amount", amount.toString());
        reqData.put("payNoticeUrl", cmbConfig.getServer() + "/cmb/callback/pay/" + orderNo);
        reqData.put("signNoticeUrl", cmbConfig.getServer() + "/cmb/callback/sign/" + agrNo);
        reqData.put("signNoticePara", "");
        reqData.put("returnUrl", cmbConfig.getServer() + "/wx/pay/paysucceed/" + bill.getBillId());
        reqData.put("agrNo", agrNo);
        return reqData;
    }

    public String htmlJump(String json, String url)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<html action=\"false\"><head><meta charset=\"utf-8\"><title>我的钱包</title><script src=\"/safecampus/wx/pay/basepay.js\"></script></head></html>");
        sb.append("<script>sendFormRequest('");
        sb.append(json);
        sb.append("',");
        sb.append("'");
        sb.append(url);
        sb.append("')</script>");
        return sb.toString();
    }
}
