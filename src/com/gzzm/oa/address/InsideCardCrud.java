package com.gzzm.oa.address;

import com.gzzm.platform.commons.crud.Buttons;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.organ.AuthDeptDisplay;
import com.gzzm.platform.organ.UserConfigCrud;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CCombox;

/**
 * 内部通信录
 *
 * @author gyw
 * @date 2017/7/14 0014
 */
@Service(url = "/oa/address/InsideCardCrud")
public class InsideCardCrud extends UserConfigCrud {

    @Like
    private String userName;

    @Like
    private String phone;

    @Like
    private String duty;

    public InsideCardCrud() {
        setLog(true);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    @Override
    public String getOrderField() {
        return null;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);
        view.addComponent("包括下属部门", new CCombox("all").setNullable(false));
        view.addComponent("姓名", "userName");
        view.addComponent("手机", "phone");
        view.addComponent("职务", "duty");

        view.addColumn("姓名", "userName");
        view.addColumn("职务", "duty").setWidth("150");
        view.addColumn("手机", "phone").setWidth("120");
        view.addColumn("办公电话", "officePhone").setWidth("120");
        view.addColumn("岗位", "allStationName()").setWidth("200");
        view.addButton(Buttons.query());
        return view;
    }

    @Override
    protected String getCondition() throws Exception {
        StringBuilder buffer = new StringBuilder(super.getCondition())
                .append(" and user.phone like ?phone " +
                        "and user.duty like ?duty");
        return buffer.toString();
    }
}
