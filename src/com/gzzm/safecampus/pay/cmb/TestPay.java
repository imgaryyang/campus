package com.gzzm.safecampus.pay.cmb;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.safecampus.campus.account.Merchant;
import com.gzzm.safecampus.campus.pay.Payment;
import com.gzzm.safecampus.pay.job.BankReconciliation;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 测试支付
 *
 * @author yuanfang
 * @date 18-04-13 14:07
 */
@Service
public class TestPay
{
    @Inject
    PayDao payDao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    private List bankList = new ArrayList();

    public List getBankList()
    {
        return bankList;
    }

    private String month;

    private String today;

    private Payment payment;

    public Payment getPayment()
    {
        return payment;
    }

    public void setPayment(Payment payment)
    {
        this.payment = payment;
    }

    public String getToday()
    {
        return PayUtil.getNowDate();
    }

    public String getMonth()
    {
        return PayUtil.getNowDate().substring(0, 7);
    }


    /**
     * 测试入口
     *
     */
    @Service(url = "/test")
    public String account()
    {
        if(userOnlineInfo==null)
            return null;
        return "/safecampus/pay/test/index.ptl";
    }

    /**
     * 测试对账
     */
    @Service(url = "/testReconcile")
    public String testReconcile() throws Exception
    {
        if(userOnlineInfo==null)
            return null;
        List<Merchant> merchantList = new ArrayList<>();
        Merchant merchant = new Merchant();
        merchant.setBranchNo("0020");
        merchant.setSecretKey("666666aaaaAAAAAA");
        merchant.setMerchantNo("010030");
        merchantList.add(merchant);
        String date = PayUtil.getNowDate();
        List<BankBusiness> list = new BankReconciliation().reconcile(merchantList, "20180101", date, false);
        Tools.debug("Reconciled : " + list.size());
        bankList = list;

        return "/safecampus/pay/test/reconcile.ptl";
    }

    /**
     * 测试对账 -账单详情
     */
    @Service(url = "/test/reconcile/detail/{$0}")
    public String testViewReconcile(String orderNo) throws Exception
    {
        if(userOnlineInfo==null)
            return null;
        payment = payDao.getPayment(orderNo);
        return "/safecampus/pay/test/payment.ptl";
    }


}
