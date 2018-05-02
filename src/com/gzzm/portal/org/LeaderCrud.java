package com.gzzm.portal.org;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Null;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 2014/8/13
 */
@Service(url = "/portal/org/leader")
public class LeaderCrud extends DeptOwnedNormalCrud<Leader, Integer>
{
    @Inject
    private OrgInfoDao dao;

    private PageUserSelector userSelector;

    @Like
    private String leaderName;

    private boolean deletePhoto;

    @NotSerialized
    private List<LeaderTitle> titles;

    private boolean panel;

    public LeaderCrud()
    {
        setLog(true);
    }

    public String getLeaderName()
    {
        return leaderName;
    }

    public void setLeaderName(String leaderName)
    {
        this.leaderName = leaderName;
    }

    public boolean isPanel()
    {
        return panel;
    }

    public void setPanel(boolean panel)
    {
        this.panel = panel;
    }

    @Override
    public void initEntity(Leader entity) throws Exception
    {
        super.initEntity(entity);
        entity.setUserId(null);
    }

    @Select(field = "entity.userId")
    public PageUserSelector getUserSelector() throws Exception
    {
        if (userSelector == null)
        {
            userSelector = new PageUserSelector();

            if (getEntity() != null)
                userSelector.setRootId(getEntity().getDeptId());
        }

        return userSelector;
    }

    public void setUserSelector(PageUserSelector userSelector)
    {
        this.userSelector = userSelector;
    }

    public boolean isDeletePhoto()
    {
        return deletePhoto;
    }

    public void setDeletePhoto(boolean deletePhoto)
    {
        this.deletePhoto = deletePhoto;
    }

    @NotSerialized
    @Select(field = "entity.titleId")
    public List<LeaderTitle> getTitles() throws Exception
    {
        if (titles == null)
        {
            if (getEntity() != null)
                titles = dao.getLeaderTitles(getEntity().getDeptId());
            else
                titles = dao.getLeaderTitles(getDeptId());
        }

        return titles;
    }

    @Service(url = "/portal/org/leader/{$0}/photo")
    public byte[] getPhoto(Integer leaderId) throws Exception
    {
        return getEntity(leaderId).getPhoto();
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "orgId is null";
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        Leader leader = getEntity();
        if (deletePhoto && leader.getPhoto() == null)
            leader.setPhoto(Null.ByteArray);

        return true;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("领导姓名", "leaderName").setProperty("onchange", "leaderNameChange()");
        view.addComponent("关联用户", "userId").setProperty("onchange", "userIdChange()")
                .setProperty("text", getEntity().getUserId() == null ? "" : getEntity().getLeaderName());

        List<LeaderTitle> titles = getTitles();

        if (titles.size() > 0)
            view.addComponent("工作职务", "titleId");
        else
            view.addComponent("工作职务", "titleName");

        List<net.cyan.crud.view.components.Component> photoComponents =
                new ArrayList<net.cyan.crud.view.components.Component>(3);
        photoComponents.add(new CFile("photo").setProperty("id", "photo"));

        byte[] photo = getEntity().getPhoto();
        if (photo != null)
        {
            photoComponents.add(new CHref("查看照片").setProperty("id", "showPhoto"));
            photoComponents.add(new CHref("删除照片").setProperty("id", "deletePhoto"));
        }

        view.addComponent("照片", new CUnion(photoComponents));

        if (titles.size() > 0)
        {
            view.addDoubleComponent("职务说明", "titleName");
        }

        view.addComponent("联系电话", "phone");
        view.addComponent("电子邮件", "email");

        view.addDoubleComponent("工作地址", "workAddress");

        view.addDoubleComponent("职责分工", new CTextArea("responsibility").setHeight("60px"));
//        view.addDoubleComponent("介绍", new CTextArea("introduction").setHeight("60px"));
        view.addDoubleComponent("简历", new CTextArea("resume").setHeight("200px"));

        view.addDefaultButtons();

        view.importJs("/portal/org/leader.js");
        view.importCss("/portal/org/leader.css");

        view.setPage("double");

        return view;
    }

    @Override
    protected Object createListView() throws Exception
    {
        if (panel)
        {
            PagePanelView view;


            if (getAuthDeptIds() == null || getAuthDeptIds().size() > 0)
                view = new ComplexPanelView(new AuthDeptDisplay(), "deptId");
            else
                view = new PagePanelView();

            view.addComponent("领导姓名", "leaderName");
            view.addButton(Buttons.query());
            view.addButton(Buttons.add());
            view.addButton(Buttons.delete());
            view.addButton(Buttons.sort());

            view.importJs("/portal/org/leader_panel.js");
            view.importCss("/portal/org/leader_panel.css");

            return view;
        }
        else
        {
            PageTableView view;

            if (getAuthDeptIds() == null || getAuthDeptIds().size() > 0)
                view = new ComplexTableView(new AuthDeptDisplay(), "deptId");
            else
                view = new PageTableView();

            view.addComponent("领导姓名", "leaderName");

            view.addColumn("领导姓名", "leaderName").setWidth("100");
            view.addColumn("职务", "title!=null?title.titleName:titleName").setWidth("150");
            view.addColumn("联系电话", "phone");
            view.addColumn("职责分工", "responsibility").setAutoExpand(true);
            view.addColumn("有效", new CCheckbox("valid").setProperty("onclick",
                    "setValid(${leaderId},this.checked)")).setWidth("40");
            view.addColumn("关联信息",new CButton("关联信息","openInformationPage(${leaderId})")).setWidth("100");
            view.defaultInit();
            view.addButton(Buttons.sort());

            if (view instanceof ComplexTableView)
                ((ComplexTableView) view).enableDD();
            view.importJs("/portal/org/leader.js");
            return view;
        }
    }

    @Service
    @ObjectResult
    public void setValid(Integer leaderId, Boolean valid) throws Exception
    {
        Leader leader = new Leader();
        leader.setLeaderId(leaderId);
        leader.setValid(valid);
        dao.update(leader);
    }
}
