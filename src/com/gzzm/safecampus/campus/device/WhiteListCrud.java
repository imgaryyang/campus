package com.gzzm.safecampus.campus.device;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.campus.service.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author liyabin
 * @date 2018/3/30
 */
@Service(url = "/campus/device/WhiteListCrud")
public class WhiteListCrud extends DeptOwnedNormalCrud<WhiteListVersion, Integer>
{
    @Inject
    ServiceDao serviceDao;
    @Like("school.schoolName")
    private String schoolName;

    @Like("upListVersion.versionNo")
    private Integer versionNo;

    private DeptTreeModel deptTreeModel;

    @NotSerialized
    private List<WhiteListVersion> whiteListVersion;

    private Integer schoolId;

    @Inject
    private DeviceService service;

    public WhiteListCrud()
    {
    }

    public Integer getSchoolId()
    {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId)
    {
        this.schoolId = schoolId;
    }

    public Integer getVersionNo()
    {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo)
    {
        this.versionNo = versionNo;
    }

    @NotCondition
    @Override
    public Integer getDeptId()
    {
        return super.getDeptId();
    }

    public String getSchoolName()
    {
        return schoolName;
    }

    public void setSchoolName(String schoolName)
    {
        this.schoolName = schoolName;
    }

    @Select(field = {"entity.upVersion"})
    public List<WhiteListVersion> getWhiteListVersion()
    {
        Integer deptId = getEntity() == null ? null :
                getEntity().getSchool() == null ? null : getEntity().getSchool().getDeptId();
        if (deptId == null) whiteListVersion = new ArrayList<WhiteListVersion>();
        else whiteListVersion = service.getWhiteListVersion(deptId);
        whiteListVersion.add(0, new WhiteListVersion());
        return whiteListVersion;
    }

    public void setWhiteListVersion(List<WhiteListVersion> whiteListVersion)
    {
        this.whiteListVersion = whiteListVersion;
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

    public void setDeptTreeModel(DeptTreeModel deptTreeModel)
    {
        this.deptTreeModel = deptTreeModel;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "(select 1 from Dept l,Dept r where l.leftValue>=r.leftValue and l.rightValue<=r.rightValue and l.deptId=d.deptId and r.deptId=?deptId) is not empty";
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
        view.addComponent("白名单版本号", "versionNo");
        view.addComponent("所属学校", "school.schoolName");
        view.addComponent("前版本", "versionNo");
        view.addColumn("白名单版本号", "versionNo");
        view.addColumn("前版本", "upListVersion.versionNo").setWidth("250px");
        view.addColumn("学校", "school.schoolName").setWidth("250px");
        view.addColumn("名单生成开始时间", "upListVersion.endTime").setWidth("200px");
        view.addColumn("名单生成结束时间", "endTime").setWidth("200px");
        view.defaultInit();
        view.importJs("/safecampus/campus/device/whitelist.js");
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("版本号", "versionNo");
        view.addComponent("所属学校", "school.deptId", true).setProperty("onchange", "changeValue()")
                .setProperty("text", "${school.schoolName}");
        view.addComponent("前版本", "upVersion", true).setProperty("text", "${upListVersion}");
        view.addComponent("结束时间", "endTime");
        view.addComponent("版本描述", new CTextArea("versionDescriptive"));
        view.addDefaultButtons();
        view.importJs("/safecampus/campus/device/whitelist.js");
        return view;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        School school = serviceDao.getSchool(getEntity().getSchool().getDeptId());
        getEntity().setDeptId(getEntity().getSchool().getDeptId());
        getEntity().setSchoolId(school.getSchoolId());
        return super.beforeUpdate();
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        WhiteListVersion entity = getEntity();
        School school = serviceDao.getSchool(getEntity().getSchool().getDeptId());
        getEntity().setDeptId(getEntity().getSchool().getDeptId());
        getEntity().setSchoolId(school.getSchoolId());
        return super.beforeInsert();
    }
}
