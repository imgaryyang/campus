package com.gzzm.safecampus.campus.pay;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.*;

import java.sql.Timestamp;

/**
 * 未支付账单
 *
 * @author yuanfang
 * @date 18-03-05 13:44
 */

@Service(url = "/campus/pay/unpaidbillscrud")
public class UnpaidBillCrud extends BillCrud
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private BillDao billDao;

    private Integer[] billIds;

    public Integer[] getBillIds()
    {
        return billIds;
    }

    public void setBillIds(Integer[] billIds)
    {
        this.billIds = billIds;
    }

    /**
     * 未支付账单状态
     * BillStatus.Wait
     */
    @Override
    protected String getComplexCondition0()
    {
        return "billStatus=0";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("缴费项目", "payItem.payItemName");
        view.addColumn("账单编号", "serialNo");
        view.addColumn("金额(元)", "money").setWidth("80");
        view.addColumn("缴费类别", "payItem.paymentType").setWidth("90");
        view.addColumn("出账日期", "createTime").setWidth("95");
        view.addColumn("截止日期", "deadline").setWidth("95");
        view.addColumn("学号", "student.studentNo").setWidth("110");
        view.addColumn("姓名", "student.studentName").setWidth("110");
        view.addColumn("班级", "classes.allName").setWidth("100");

        view.addButton(Buttons.query());
        view.addButton(new CButton("标注已缴", "signPayed()"));
        view.addButton(new CButton("提醒", "noticePay()"));
        view.addButton(Buttons.export("xls"));
        view.addButton(Buttons.delete());
        initListView(view);
        view.importJs("/safecampus/campus/payment/unpaid.js");
        return view;
    }

    /**
     * 标注已缴
     *
     * @param billIds 账单id 数组
     * @return 标注数量
     * @throws Exception 数据库异常
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    public Integer signAndPayed(Integer[] billIds) throws Exception
    {
        if (billIds.length > 0)
        {
            //标注已缴
            billDao.setPaid(PaymentMethod.other, BillStatus.Finnish, new Timestamp(System.currentTimeMillis()), userOnlineInfo.getUserId(), billIds);
            //发送微信已缴通知
//            for (Integer billId : billIds)
//            {
            PaymentService paymentService = new PaymentService();
            //       Bill bill = billDao.getBillById(billId);
            paymentService.sendPaidBillMsg(billIds);
//            }
        }
        return billIds.length;
    }

    /**
     * 发送微信提醒缴费通知
     *
     * @param billIds 账单id数组
     * @return 发送数量
     * @throws Exception 数据库异常
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    public Integer noticeToPay(Integer[] billIds) throws Exception
    {
//        for (Integer billId : billIds)
//        {
            PaymentService paymentService = new PaymentService();
 //           Bill bill = billDao.getBillById(billId);
            paymentService.sendUnpaidBillMsg(billIds);
 //       }
        return billIds.length;
    }

}
