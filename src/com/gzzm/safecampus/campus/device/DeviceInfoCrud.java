package com.gzzm.safecampus.campus.device;

import com.gzzm.platform.commons.crud.DeptOwnedNormalCrud;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CTextArea;

/**
 * 硬件管理CRUD
 *
 * @author yiuman
 * @date 2018/3/15
 */
@Service(url = "/campus/tutorship/deviceinfocrud")
public class DeviceInfoCrud extends DeptOwnedNormalCrud<DeviceInfo, Integer> {

    @Like
    private String deviceName;

    @Like
    private String deviceNo;

    private DeviceStatus status;

    private Boolean using;

    public DeviceInfoCrud(){
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }


    public Boolean getUsing() {
        return using;
    }

    public void setUsing(Boolean using) {
        this.using = using;
    }

    @Override
    public String getOrderField() {
        return null;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView();
        view.addComponent("硬件名称", "deviceName");
        view.addComponent("硬件编号", "deviceNo");
        view.addComponent("硬件状态", "status");
        view.addComponent("是否在用", "using");

        view.addColumn("硬件名称", "deviceName");
        view.addColumn("硬件编号", "deviceNo");
        view.addColumn("硬件状态", "status");
        view.addColumn("是否在用", "using");
        view.addColumn("维护人员", "maintainer");
        view.addColumn("维护人员电话", "maPhone");
        view.defaultInit(false);
        return view;
    }

    @Override
    protected Object createShowView() throws Exception {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("硬件名称", "deviceName");
        view.addComponent("硬件编号", "deviceNo");
        view.addComponent("硬件状态", "status");
        view.addComponent("是否在用", "using");
        view.addComponent("维护人员", "maintainer");
        view.addComponent("维护人员电话", "maPhone");
        view.addComponent("硬件所在位置", "location");
        view.addComponent("描述", new CTextArea("describe"));
        view.addDefaultButtons();
        return view;
    }

    @Override
    public boolean beforeInsert() throws Exception {
        getEntity().setDeptId(getDefaultDeptId());
        return super.beforeInsert();
    }

    @Override
    public void initEntity(DeviceInfo entity) throws Exception {
        entity.setUsing(true);
        super.initEntity(entity);
    }
}
