package com.gzzm.safecampus.campus.device;

import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.platform.commons.crud.DeptOwnedNormalCrud;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.annotation.NotCondition;

/**
 * 摄像头管理
 *
 * @author Neo
 * @date 2018/3/29 10:06
 */
@Service(url = "/campus/device/camera")
public class CameraCrud extends DeptOwnedNormalCrud<Camera, Integer>
{
    /**
     * 名称查询条件
     */
    @Like
    private String cameraName;

    /**
     * 编号查询条件
     */
    @Like
    private String cameraNum;

    /**
     * 用途查询条件
     */
    private Purpose purpose;

    public CameraCrud()
    {
    }

    public String getCameraName()
    {
        return cameraName;
    }

    public void setCameraName(String cameraName)
    {
        this.cameraName = cameraName;
    }

    public String getCameraNum()
    {
        return cameraNum;
    }

    public void setCameraNum(String cameraNum)
    {
        this.cameraNum = cameraNum;
    }

    public Purpose getPurpose()
    {
        return purpose;
    }

    public void setPurpose(Purpose purpose)
    {
        this.purpose = purpose;
    }

    @NotCondition
    @Override
    public Integer getDeptId()
    {
        return super.getDeptId();
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "(select 1 from Dept l,Dept r where l.leftValue>=r.leftValue and l.rightValue<=r.rightValue and l.deptId=d.deptId and r.deptId=?deptId) is not empty";
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();
        setDefaultDeptId();
    }

    @Override
    public String getAlias()
    {
        return "d";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new ComplexTableView(new AuthDeptDisplay(),"deptId");
        view.addComponent("摄像机名称", "cameraName");
        view.addComponent("编号", "cameraNum");
        view.addComponent("用途", "purpose");
        view.addColumn("摄像机名称", "cameraName");
        view.addColumn("所属学校", "dept.deptName");
        view.addColumn("编号", "cameraNum").setWidth("150px");
        view.addColumn("用途", "purpose").setWidth("100px");
        view.defaultInit();
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("摄像机名称", "cameraName");
        view.addComponent("编号", "cameraNum");
        view.addComponent("用途", "purpose");
        view.addDefaultButtons();
        return view;
    }
}
