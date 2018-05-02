package com.gzzm.safecampus.campus.pay;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.campus.base.StudentOwnedCrud;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.*;

/**
 * 所有缴费项生成的所有账单
 * @author yuanfang
 * @date 18-03-02 10:19
 */

@Service(url = "/campus/pay/billscrud")
public class BillCrud extends StudentOwnedCrud<Bill, Integer>
{

    /**
     * 缴费类型
     */
    @Equals("payItem.paymentType")
    protected PaymentType paymentType;

    /**
     * 账单状态
     */
    protected BillStatus billStatus;

    /**
     * 账单编号
     */
    @Like
    protected String serialNo;

    public String getSerialNo()
    {
        return serialNo;
    }

    public void setSerialNo(String serialNo)
    {
        this.serialNo = serialNo;
    }

    public PaymentType getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType)
    {
        this.paymentType = paymentType;
    }

    public BillStatus getBillStatus()
    {
        return billStatus;
    }

    public void setBillStatus(BillStatus billStatus)
    {
        this.billStatus = billStatus;
    }

    protected void initListView(PageTableView view)
    {
        view.addComponent("账单编号", "serialNo");
        view.addComponent("缴费类别", "paymentType");
        view.addComponent("学号", "studentNo");
        view.addComponent("姓名", "studentName");
        view.addComponent("班级", "classesName");
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("状态", "billStatus");

        view.addColumn("缴费项目", "payItem.payItemName");
        view.addColumn("账单编号", "serialNo");
        view.addColumn("缴费类别", "payItem.paymentType");
        view.addColumn("金额(元)", "money").setWidth("80");
        view.addColumn("学号", "student.studentNo");
        view.addColumn("姓名", "student.studentName");
        view.addColumn("班级", "classes.allName").setWidth("100");
        view.addColumn("状态", "billStatus");
        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());
        initListView(view);
        return view;
    }

    /**
     * 已缴账单、待缴账单删除使用 cancelFlag
     * 无效账单删除使用 deleteTag
     * @return 假删除标志 cancelFlag
     */
    @Override
    public String getDeleteTagField()
    {
        return "cancelFlag";
    }

}
