package com.gzzm.safecampus.campus.pay;

import com.gzzm.platform.commons.*;
import com.gzzm.safecampus.campus.wx.message.BillMsg;
import com.gzzm.safecampus.campus.wx.message.TemplateMessageType;
import com.gzzm.safecampus.campus.wx.message.WxTemplateMessage;
import com.gzzm.safecampus.pay.cmb.CmbConfig;
import net.cyan.commons.util.DataConvert;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yuanfang
 * @date 18-04-03 18:44
 */
public class PaymentService
{
    @Inject
    private static Provider<CmbConfig> cmbConfigProvider;

    @Inject
    private static Provider<BillDao> billDaoProvider;

    public PaymentService()
    {
    }

    public  void sendUnpaidBillMsg(List<Bill> list) throws Exception
    {
        for (Bill bill : list)
        {
            sendUnpaidBillMsg(bill);
        }
    }

    public void sendUnpaidBillMsg(Integer billId) throws Exception
    {
        Bill bill = billDaoProvider.get().getBillById(billId);
        sendUnpaidBillMsg(bill);
    }

    public void sendUnpaidBillMsg(Integer[] billIds) throws Exception
    {
        for (Integer billId : billIds)
        {
            Bill bill = billDaoProvider.get().getBillById(billId);
            sendUnpaidBillMsg(bill);
        }
    }

    public void sendUnpaidBillMsg(Bill bill)
    {
        Tools.debug("sending unpaid bill msg : " +bill.getPayItemId()+ bill.getBillId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        BillMsg billMsg = new BillMsg(TemplateMessageType.UNPAIDBILL);
        billMsg.setStudentId(bill.getStudentId());
        billMsg.setStudentName(bill.getStudent().getStudentName());
        billMsg.setMoney(bill.getMoney().toString());
        Date time = bill.getDeadline();
        String ftime = "无";
        if (time != null)
            ftime = sdf.format(bill.getDeadline());
        billMsg.setTime(ftime);
        billMsg.setPayItemName(bill.getPayItem().getPayItemName());
        billMsg.setPaymentType(DataConvert.toString(bill.getPayItem().getPaymentType()));
        billMsg.setBillStatus(BillStatus.Wait);
        billMsg.setUrl(cmbConfigProvider.get().getServerName() + "/wx/pay/billdetail/bid/" + bill.getBillId() + "/sid/" + bill.getStudentId());
        WxTemplateMessage.sendUnpaidBillMsg(billMsg);
        Tools.log("send unpaid bill msg : " + bill.getBillId());
    }

    public void sendPaidBillMsg(Integer billId) throws Exception
    {
        Bill bill = billDaoProvider.get().getBillById(billId);
        sendPaidBillMsg(bill);
    }

    public void sendPaidBillMsg(Integer[] billIds) throws Exception
    {
        for (Integer billId : billIds)
        {
            Bill bill = billDaoProvider.get().getBillById(billId);
            sendPaidBillMsg(bill);
        }
    }

    public void sendPaidBillMsg(Bill bill)
    {
        Tools.debug("sending paid bill msg : " +bill.getPayItemId()+"-"+ bill.getBillId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        BillMsg billMsg = new BillMsg(TemplateMessageType.PAIDBILL);
        billMsg.setStudentId(bill.getStudentId());
        billMsg.setStudentName(bill.getStudent().getStudentName());
        Tools.debug("sending paid bill msg -stu name: " +bill.getStudent().getStudentName());
        billMsg.setMoney(bill.getMoney().toString());
        billMsg.setTime(sdf.format(bill.getPayTime()));
        billMsg.setPayItemName(bill.getPayItem().getPayItemName());
        Tools.debug("sending paid bill msg -item name: " +bill.getPayItem().getPayItemName());
        billMsg.setPaymentType(DataConvert.toString(bill.getPayItem().getPaymentType()));
        billMsg.setBillStatus(BillStatus.Finnish);
        billMsg.setUrl(cmbConfigProvider.get().getServerName() + "/wx/pay/billdetail/bid/" + bill.getBillId() + "/sid/" + bill.getStudentId());
        Tools.debug("sending paid bill msg url: " +billMsg.getUrl());
        WxTemplateMessage.sendPaidBillMsg(billMsg);
        Tools.log("send paid bill msg : " + bill.getBillId());
    }
}
