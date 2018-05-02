package com.gzzm.safecampus.campus.device;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.service.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author liyabin
 * @date 2018/3/30
 */
@Service(url = "/campus/device/AttendanceDevice")
public class AttendanceDeviceCrud extends DeptOwnedNormalCrud<AttendanceDevice, Integer>
{
    @Like("school.schoolName")
    private String schoolName;
    private String deviceNo;
    private String enterpriseCode;
    private AttendanceStates states;
    private Integer versionId;
    @NotSerialized
    private List<WhiteListVersion> whiteListVersion;
    @Inject
    DeviceService service;

    @Inject
    ServiceDao serviceDao;

    @NotSerialized
    private DeptTreeModel deptTreeModel;


    public AttendanceDeviceCrud()
    {
    }

    public void setDeptTreeModel(DeptTreeModel deptTreeModel)
    {
        this.deptTreeModel = deptTreeModel;
    }

    public void setWhiteListVersion(List<WhiteListVersion> whiteListVersion)
    {
        this.whiteListVersion = whiteListVersion;
    }

    public String getSchoolName()
    {
        return schoolName;
    }

    public void setSchoolName(String schoolName)
    {
        this.schoolName = schoolName;
    }

    public String getDeviceNo()
    {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo)
    {
        this.deviceNo = deviceNo;
    }

    public String getEnterpriseCode()
    {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode)
    {
        this.enterpriseCode = enterpriseCode;
    }

    public Integer getVersionId()
    {
        return versionId;
    }

    public void setVersionId(Integer versionId)
    {
        this.versionId = versionId;
    }

    public AttendanceStates getStates()
    {
        return states;
    }

    public void setStates(AttendanceStates states)
    {
        this.states = states;
    }

    @Select(field = "entity.whiteListVersion")
    public List<WhiteListVersion> getWhiteListVersion()
    {
        Integer deptId = getEntity() == null ? null :
                getEntity().getSchool() == null ? null : getEntity().getSchool().getDeptId();
        if (deptId == null) whiteListVersion = new ArrayList<WhiteListVersion>();
        else whiteListVersion = service.getWhiteListVersion(deptId);
        whiteListVersion.add(0, new WhiteListVersion());
        return whiteListVersion;

    }

    @Select(field = "entity.school.deptId")
    public DeptTreeModel getDeptTreeModel() throws Exception
    {
        if (deptTreeModel == null)
        {
            deptTreeModel = Tools.getBean(DeptTreeModel.class);
            deptTreeModel.setRootId(userOnlineInfo.getDeptId(2));
        }
        return deptTreeModel;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "(select 1 from Dept l,Dept r where l.leftValue>=r.leftValue and l.rightValue<=r.rightValue and l.deptId=d.deptId and r.deptId=?deptId) is not empty";
    }

    @NotCondition
    @Override
    public Integer getDeptId()
    {
        return super.getDeptId();
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        setEntityInfo();
        return super.beforeUpdate();
    }

    private void setEntityInfo()
    {
        School school = serviceDao.getSchool(getEntity().getSchool().getDeptId());
        AttendanceDevice entity = getEntity();
        String deviceNo = entity.getDeviceNo();
        String enterpriseCode = entity.getEnterpriseCode();
        if (deviceNo.length() != 8)
        {
            StringBuilder deviceNos = new StringBuilder(deviceNo);
            while (deviceNos.length() != 8)
            {
                deviceNos.insert(0, "0");
            }
            entity.setDeviceNo(deviceNos.toString());
        }
        if (enterpriseCode.length() != 6)
        {
            StringBuilder enterpriseCodes = new StringBuilder(enterpriseCode);
            while (enterpriseCodes.length() != 6)
            {
                enterpriseCodes.insert(0, "0");
            }
            entity.setEnterpriseCode(enterpriseCodes.toString());
        }
        entity.setDeptId(school.getDeptId());
        entity.setSchoolId(school.getSchoolId());
        entity.setCityCode(Tools.getMessage("campus.device.AttendanceDevice.CityCode.Default"));
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        setEntityInfo();
        getEntity().setStates(AttendanceStates.UNINITIALIZED);
        return super.beforeInsert();
    }

    @Override
    public String getAlias()
    {
        return "d";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new ComplexTableView(new AuthDeptDisplay(), "deptId");
        view.addComponent("终端学校代码", "enterpriseCode");
        view.addComponent("终端编号", "deviceNo");
        view.addComponent("所属学校", "school.schoolName");
        view.addComponent("状态", "states");
        view.addColumn("学校编号", "enterpriseCode");
        view.addColumn("终端机器码", "deviceSn");
        view.addColumn("终端编号", "deviceNo").setWidth("150px");
        view.addColumn("学校", "school.schoolName").setWidth("150px");
        view.addColumn("白名单版本", "whiteVersion.versionNo").setWidth("150px");
        view.addColumn("通讯版本", "socketVersion").setWidth("150px");
        view.addColumn("设备状态", "states").setWidth("150px");
        view.importJs("/safecampus/campus/device/whitelist.js");
        view.defaultInit();
        view.addButton(Buttons.getButton("白名单升级", "upLevel()"));
        view.importJs("/safecampus/campus/device/whitelist.js");
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        //其他信息当考勤机签到的时候将会初始化 例如设备序列号-目前的ip地址-
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("终端编号", "deviceNo");
        view.addComponent("所属学校", "school.deptId", true).setProperty("onchange", "changeValue2()")
                .setProperty("text", "${school.schoolName}");
        view.addComponent("白名单版本", "whiteListVersion", true);
        if (!isNew$())
        {
            view.addComponent("终端机编号", "deviceSn");
            view.addComponent("ip", "ipAddress");
            view.addComponent("端口", "port");
            view.addComponent("设备状态", "states");
        }
        view.addComponent("学校编号", "enterpriseCode");
        view.addComponent("通讯版本", "socketVersion");
        view.importJs("/safecampus/campus/device/whitelist.js");
        view.addDefaultButtons();
        return view;
    }

    @Service
    @ObjectResult
    public Map<String, String> upWhiteList() throws Exception
    {
        Integer[] keys = this.getKeys();
        if (keys == null || keys.length == 0)
        {
            Map map = new HashMap();
            map.put("success", false);
            map.put("msg", "请选择需要升级设备！");
            return map;
        }
        else
        {
            service.upLevel(keys);
            Map map = new HashMap();
            map.put("success", true);
            map.put("msg", "升级成功！");
            return map;
        }
    }
}
