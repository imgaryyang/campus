package com.gzzm.safecampus.wx.pay;

import com.gzzm.safecampus.campus.bus.BusStudent;
import com.gzzm.safecampus.campus.bus.BusStudentDao;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.pay.Bill;
import com.gzzm.safecampus.campus.pay.BillDao;
import com.gzzm.safecampus.campus.pay.PaymentType;
import com.gzzm.safecampus.wx.personal.WxAuthDao;
import com.gzzm.safecampus.wx.user.WxUserOnlineInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 微信-我的账单-我要缴费
 *
 * @author yuanfang
 * @date 18-03-21 16:15
 */
@Service
public class MyBills
{
    @Inject
    private WxUserOnlineInfo wxUserOnlineInfo;

    @Inject
    private BillDao billDao;

    @Inject
    private WxAuthDao wxAuthDao;

    @Inject
    private BusStudentDao busStudentDao;

    /**
     * 账单集合
     */
    private List<Bill> allBills = new ArrayList<>();

    /**
     * 校巴信息
     */
    private List<BusStudent> msg;

    public List<BusStudent> getMsg()
    {
        return msg;
    }

    public void setMsg(List<BusStudent> msg)
    {
        this.msg = msg;
    }

    public List<Bill> getAllBills()
    {
        return allBills;
    }


    /**
     * 所有账单 ：已缴-未缴-失效
     *
     * @return 所有账单页面
     * @throws Exception 数据库异常
     */
    @Service(url = "/wx/pay/allbills")
    public String showAllBills() throws Exception
    {
        if (wxUserOnlineInfo != null && wxUserOnlineInfo.getOpenId() != null)
        {
            List<Student> students = wxAuthDao.queryStudentByWxUserId(wxUserOnlineInfo.getUserId());
            Integer[] studentIds = new Integer[students.size()];
            for (int i = 0; i < students.size(); i++)
            {
                studentIds[i] = students.get(i).getStudentId();
            }
            List<Bill> bills = billDao.getBillsByStudentIds(studentIds);
            if (CollectionUtils.isNotEmpty(bills))
                allBills = bills;
        }
        return "/safecampus/wx/pay/allbill.ptl";
    }

    /**
     * 我的账单
     *
     * @param status 已缴/未缴
     * @param type   缴费类型
     * @return 我的账单页面
     * @throws Exception 数据库异常
     */
    @Service(url = "/wx/pay/billsofstatus/status/{$0}/type/{$1}")
    public String showUnpaidBills(Integer status, Integer type) throws Exception
    {
        if (wxUserOnlineInfo != null && wxUserOnlineInfo.getOpenId() != null)
        {
            List<Student> students = wxAuthDao.queryStudentByWxUserId(wxUserOnlineInfo.getUserId());
            Integer[] studentIds = new Integer[students.size()];
            for (int i = 0; i < students.size(); i++)
            {
                studentIds[i] = students.get(i).getStudentId();
            }
            List<Bill> bills = billDao.getBillsByStudentsAndType(studentIds, type);
            if (CollectionUtils.isNotEmpty(bills))
                allBills = bills;
        }
        return "/safecampus/wx/pay/paylistofstatus.ptl";
    }

    /**
     * 我要缴费
     *
     * @return 我要缴费
     */
    @Service(url = "/wx/pay/wanttopay")
    public String wantToPay()
    {
        return "/safecampus/wx/pay/wantpay.ptl";
    }

    /**
     * 我的账单 - 账单详情
     *
     * @param billId    账单ID
     * @param studentId 学生ID
     * @return 账单详情页面
     * @throws Exception 数据库异常
     */
    @Service(url = "/wx/pay/billdetail/bid/{$0}/sid/{$1}")
    public String showBillDetail(Integer billId, Integer studentId) throws Exception
    {
        allBills.add(billDao.getBillById(billId));
        if (allBills != null && allBills.size() > 0)
        {
            //对于校巴账单，获取校巴信息：站点、线路
            if (allBills.get(0).getPayItem().getPaymentType() == PaymentType.schoolBus)
            {
                msg = busStudentDao.getBusStudentByStu(studentId);
            }
        }
        return "/safecampus/wx/pay/billdeatail.ptl";
    }
}
