package com.gzzm.safecampus.campus.pay;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.PageTableView;
import net.cyan.arachne.annotation.Service;

/**
 * 无效账单
 *
 * @author yuanfang
 * @date 18-03-05 13:44
 */

@Service(url = "/campus/pay/uselessbillscrud")
public class UselessBillsCrud extends BillCrud
{

    /**
     * 无效账单状态
     * 包含：异常账单，删除的待缴、已缴账单、
     */
    @Override
    protected String getComplexCondition0()
    {
        return "  billStatus in (2,3,4) or cancelFlag = 1";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addColumn("缴费项目", "payItem.payItemName");
        view.addColumn("账单编号", "serialNo");
        view.addColumn("金额(元)", "money").setWidth("80");
        view.addColumn("缴费类别", "payItem.paymentType").setWidth("90");
        view.addColumn("学号", "student.studentNo").setWidth("110");
        view.addColumn("姓名", "student.studentName").setWidth("110");
        view.addColumn("班级", "classes.allName").setWidth("100");
        view.addColumn("出账日期", "createTime").setWidth("95");
        view.addColumn("截止日期", "deadline").setWidth("95");

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));
        view.addButton(Buttons.delete());
        initListView(view);
        return view;
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

}
