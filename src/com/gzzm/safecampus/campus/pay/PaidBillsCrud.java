package com.gzzm.safecampus.campus.pay;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.template.TemplateInput;
import com.gzzm.safecampus.campus.classes.Student;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.DateUtils;
import net.cyan.commons.util.InputFile;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * 已支付账单
 *
 * @author yuanfang
 * @date 18-03-05 13:44
 */

@Service(url = "/campus/pay/paidbillscrud")
public class PaidBillsCrud extends BillCrud
{

    @Inject
    private BillDao billDao;

    /**
     * 已支付账单状态
     * BillStatus.Finnish
     */
    @Override
    protected String getComplexCondition0() throws Exception
    {
        return "  billStatus=1";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("缴费项目", "payItem.payItemName");
        view.addColumn("账单编号", "serialNo");
        view.addColumn("金额(元)", "money").setWidth("80");
        view.addColumn("缴费类别", "payItem.paymentType");
        view.addColumn("缴费时间", "payTime").setWidth("95");
        view.addColumn("缴费方式", "paymentMethod").setWidth("90");
        view.addColumn("操作员", "operator.userName").setWidth("110");
        view.addColumn("学号", "student.studentNo").setWidth("110");
        view.addColumn("姓名", "student.studentName").setWidth("100");
        view.addColumn("班级", "student.classes.allName").setWidth("100");
        view.addColumn("打印", new CButton("打印", "exportDoc(${billId})").setClass("sendCol"));
        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));
        view.addButton(Buttons.delete());
        view.importJs("/safecampus/campus/payment/unpaid.js");
        view.importCss("/safecampus/campus/payment/view.css");
        initListView(view);
        return view;
    }

    @Override
    @Service(url = "/campus/pay/billPreview/{$0}")
    public String show(Integer key, String forward) throws Exception
    {
        getEntity(key);
        return "/safecampus/campus/payment/bill.ptl";
    }

    //TODO 永中转换错误
    @Service(url = "/campus/pay/billPrint/{$0}")
    public InputFile exportDoc(Integer billId) throws Exception
    {
        String templateXml = "/safecampus/campus/payment/bill.xml";
        Bill bill = billDao.load(Bill.class, billId);
        Student student = bill.getStudent();
        PayItem payItem = bill.getPayItem();
        Map<String, Object> data = new HashMap<>();

        data.put("now", DateUtils.getDefaultDateFormat());
        data.put("studentName", student.getStudentName());
        data.put("studentNo", student.getStudentNo());
        data.put("className", student.getClasses().getClassesName());
        data.put("gradeName", student.getClasses().getGrade().getGradeName());
        data.put("payItemName", payItem.getPayItemName());
        data.put("serialNo", bill.getSerialNo());
        data.put("paymentType", payItem.getPaymentType());
        data.put("paymentMethod", bill.getPaymentMethod());
        data.put("money", bill.getMoney());
        data.put("payTime", DateUtils.toDefaultString(bill.getPayTime()));
        data.put("schoolName", bill.getDept().getDeptName());
        data.put("desc", payItem.getDesc());

        TemplateInput input = new TemplateInput(templateXml, data, true);
        return new InputFile(input, student.getStudentName() + "-" + bill.getSerialNo() + ".doc");
    }

}
