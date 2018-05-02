package com.gzzm.portal.webdeptgroup;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author Xrd
 * @date 2018/3/21 9:32
 */
@Service(url = "/portal/webdeptgroup/webdeptgroup")
public class WebDeptGroupCrud extends BaseNormalCrud<WebDeptGroup,Integer>
{
    private static String[] ORDERWITHFIELDS = new String[]{"purposeId","groupName"};

    @Inject
    private WebDeptGroupDao dao;

    private String purposeId;

    @NotSerialized
    private List<Integer> deptIds;

    @NotSerialized
    private List<Dept> selectedDepts;

    private String groupName;

    @Like("dept.deptName")
    private String deptName;

    public WebDeptGroupCrud()
    {
        setLog(true);
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public List<Integer> getDeptIds()
    {
        return deptIds;
    }

    public void setDeptIds(List<Integer> deptIds)
    {
        this.deptIds = deptIds;
    }

    public List<Dept> getSelectedDepts()
    {
        return selectedDepts;
    }

    public void setSelectedDepts(List<Dept> selectedDepts)
    {
        this.selectedDepts = selectedDepts;
    }

    public String getPurposeId()
    {
        return purposeId;
    }

    public void setPurposeId(String purposeId)
    {
        this.purposeId = purposeId;
    }

    @NotSerialized
    @NotCondition
    @Select(field = {"groupName","entity.groupName","groupNames"})
    public List<String> getGroupNames()throws Exception{

        return dao.getGroupName(purposeId);
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return ORDERWITHFIELDS;
    }

    @Override
    protected String getQueryString() throws Exception
    {
        String sql =  super.getQueryString();
        return sql;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("部门名称","deptName");
        view.addComponent("分组名称","groupName").setWidth("120px").setProperty("onchange", "stationChange();");

        view.addColumn("分组名称","groupName").setWidth("150px");
        view.addColumn("部门","dept.deptName");
        view.addButton(Buttons.query());
        view.addButton(Buttons.add());
        view.addButton(Buttons.delete());
        view.addButton(Buttons.sort());
        view.importJs("/portal/webdeptgroup/webdeptgroup.js");
        return view;
    }

    @Override
    public String add(String forward) throws Exception
    {

        super.add(forward);
        return "/portal/webdeptgroup/webdeptgroup.ptl";
    }

    public DeptTreeModel getAvailableDepts() throws Exception
    {

        return Tools.getBean(DeptTreeModel.class);
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }


    @Transactional
    @Service
    public boolean saveinfo()throws Exception{
        if(!StringUtils.isEmpty(purposeId)&&deptIds.size()!=0)
        {

            try
            {
                for (Integer deptId : deptIds)
                {
                    WebDeptGroup webDeptGroup = new WebDeptGroup();
                    webDeptGroup.setPurposeId(purposeId);
                    webDeptGroup.setGroupName(getEntity().getGroupName());
                    webDeptGroup.setDeptId(deptId);
                    WebDeptGroup w = dao.getByPurposeIdAndGroupNameAndDeptId(purposeId,getEntity().getGroupName(),deptId);
                    if(w==null){
                        dao.save(webDeptGroup);
                    }
                }
                return true;
            }
            catch (Exception e)
            {
                Tools.log("========新增分组时出错=======");
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }


}
