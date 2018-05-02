package com.gzzm.safecampus.wx.pay;

import com.gzzm.platform.commons.*;
import com.gzzm.safecampus.campus.account.Merchant;
import com.gzzm.safecampus.campus.pay.Bill;
import com.gzzm.safecampus.campus.pay.BillDao;
import com.gzzm.safecampus.campus.pay.BillStatus;
import com.gzzm.safecampus.campus.pay.Payment;
import com.gzzm.safecampus.pay.cmb.*;
import com.gzzm.safecampus.wx.personal.WxAuthDao;
import com.gzzm.safecampus.wx.user.WxUserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static com.gzzm.safecampus.pay.cmb.PayUtil.buildParam;

/**
 * 微信一网通支付
 *
 * @author yuanfang
 * @date 18-03-22 10:29
 */
@Service
public class WxCmbPay
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

    /**
     * 缴费成功后结果回显
     */
    private Bill bill;

    private String json;

    public Bill getBill()
    {
        return bill;
    }

    public void setBill(Bill bill)
    {
        this.bill = bill;
    }

    public WxCmbPay()
    {
    }

    /**
     * 发起支付
     *
     * @param billId 账单ID
     * @return 发送给支付接口的Json
     * @throws Exception 数据库查询异常
     */
    @Service(url = "/wx/pay/payment/simple/{$0}")
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
     * 支付成功
     *
     * @param billId 账单ID
     * @return 支付成功页面
     * @throws Exception 数据库异常
     */
    @Service(url = "/wx/pay/paysucceed/{$0}")
    public String afterPay(Integer billId) throws Exception
    {
        bill = billDao.getBillById(billId);
        return "/safecampus/wx/pay/paysucceed.ptl";
    }

    /**
     * 我的钱包入口
     * 用户切换进入学生对应的不同的学校账户
     * 若账户唯一则直接跳转该账户
     *
     * @throws Exception 数据库异常
     */
    @Service(url = "/wx/mywallet/index")
    public void myWalletIndex(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Integer wxUserId = wxUserOnlineInfo.getUserId();
        List<UserMerchantAccount> userMerchantAccounts = payDao.getAccount(wxUserId);
        if (userMerchantAccounts != null && userMerchantAccounts.size() > 0)
        {
            String agrNo = userMerchantAccounts.get(0).getAgrNo();
            Integer deptId = userMerchantAccounts.get(0).getMerchant().getDeptId();
            myWalletLogin(deptId, agrNo, request, response);
        } else
        {
            //使用该学生学校的商户进行签约
            Integer deptId = wxAuthDao.getDeptIdByWxUserId(wxUserId);
            if (deptId != null)
            {
                myWalletOpenAccount(deptId, wxUserId, response);
            } else
            {
                List<Merchant> merchants = payDao.getMerchants();
                for (Merchant m : merchants)
                {
                    if ("M12345678901234567890123456789".equals(m.getCopCode()))
                    {
                        Integer d = m.getDeptId();
                        myWalletOpenAccount(d, wxUserId, response);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 我的钱包 - 登录
     *
     * @param request  请求
     * @param response 响应
     * @throws IOException 数据库异常
     */
    private void myWalletLogin(Integer deptId, String agrNo,
                               HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String ip = request.getRemoteAddr();
        Merchant merchant = payDao.getMerchant(deptId);
        String branchNo = merchant.getBranchNo();
        String merchantNo = merchant.getMerchantNo();
        String copCode = merchant.getCopCode();

        //生成报文
        Map<String, String> reqData = new HashMap<>();
        reqData.put("branchNo", branchNo);
        reqData.put("merchantNo", merchantNo);
        reqData.put("customerNo", agrNo);
        reqData.put("copCode", copCode);
        reqData.put("rtnLink", "");
        reqData.put("chnType", "01");
        reqData.put("ip", ip);

        //模拟请求，获得请求结果
        String result = PayUtil.loginWallet(reqData, merchant.getSecretKey());
        response.setHeader("content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(result);
    }

    /**
     * 我的钱包 - 单独签约
     * 一网通账户无当前用户时，先签约
     *
     * @param deptId   部门ID
     * @param response 响应
     * @throws Exception json解析异常
     */
    private void myWalletOpenAccount(Integer deptId, Integer wxUserId, HttpServletResponse response) throws Exception
    {
        String agrNo = PaySerialNoGenerator.getAgrSerialNo();
        Merchant merchant = payDao.getMerchant(deptId);
        //签约前暂存账户信息
        UserMerchantAccount account = new UserMerchantAccount();
        account.setWxUserId(wxUserId);
        account.setAgrNo(agrNo);
        account.setMerchantSerialNo(PaySerialNoGenerator.getMerchantSerialNo());
        account.setMerchantId(merchant.getMerchantId());
        account.setCreateTime(new Timestamp(System.currentTimeMillis()));
        account.setStatus(0);
        payDao.save(account);

        //String result = sign(merchant, agrNo);
        String result = signWithBanner(merchant, agrNo);
        response.setHeader("content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(result);
    }

    /**
     * 用户签约支付 -登录 有招商银行banner版入口
     * 有未知问题
     *
     * @param merchant 商户
     * @param agrNo    协议号
     * @return json
     */
    public String signWithBanner(Merchant merchant, String agrNo)
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
        reqData.put("noticePara", "");
        reqData.put("returnUrl", cmbConfig.getServer() + "/wx/mywallet/index");
        reqData.put("signprd", "PROD0001");
        //TODO 测试数据
        reqData.put("name", "小明");
        reqData.put("idno", "440104190001179819");
        Tools.debug("单独签约： " + agrNo);
        //模拟请求，获得请求结果
        return htmlJump(PayUtil.buildParam(reqData, secretKey), cmbConfig.getOpenAccountUrl() + copCode);
        //  return PayUtil.openAccount(reqData, secretKey, copCode);
    }

    /**
     * 用户签约支付 -登录 通用版入口
     *
     * @param merchant 商户
     * @param agrNo    协议号
     * @return json
     */
    public String sign(Merchant merchant, String agrNo)
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

        if (cmbConfig.getBindCardUrl() != null)
            bindCardUrl = cmbConfig.getBindCardUrl();
        return htmlJump(json, bindCardUrl);
        //  return PayUtil.postSimpleData(json, bindCardUrl);
    }

    /**
     * 由账单信息 生成支付报文格式化为Json字符串
     *
     * @param bill 账单
     * @return 支付报文
     * @throws Exception 数据库查询异常
     */
    private Map<String, String> paymentPostDataSerial(Bill bill) throws Exception
    {

        Integer deptId = bill.getDeptId();

        //获取发起缴费的用户的一网通协议号、商户信息
        String agrNo = payDao.getAgrNo(deptId);
        Merchant merchant = payDao.getMerchant(deptId);
        String branchNo = merchant.getBranchNo();
        String merchantNo = merchant.getMerchantNo();
        String merchantSerialNo = "";
        //无协议的首次签约，生成协议号、协议流水号，注册一网通账户
        if (agrNo == null)
        {
            agrNo = PaySerialNoGenerator.getAgrSerialNo();
            merchantSerialNo = PaySerialNoGenerator.getMerchantSerialNo();
            UserMerchantAccount account = new UserMerchantAccount();
            account.setWxUserId(wxUserOnlineInfo.getUserId());
            account.setAgrNo(agrNo);
            account.setMerchantSerialNo(merchantSerialNo);
            account.setMerchantId(merchant.getMerchantId());
            account.setCreateTime(new Timestamp(System.currentTimeMillis()));
            account.setStatus(0);
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
        //payment.setMerchantSerialNo(merchantSerialNo);
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
