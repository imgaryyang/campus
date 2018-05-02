package com.gzzm.portal.org;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.PageUserSelector;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Null;
import net.cyan.crud.view.components.*;
import net.cyan.crud.view.components.Component;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author lk
 * @date 13-9-30
 */
@Service(url = "/portal/org/org_leader")
public class OrgLeaderCrud extends SubListCrud<Leader, Integer>
{
    @Inject
    private OrgInfoDao dao;

    private Integer orgId;

    @NotSerialized
    private OrgInfo orgInfo;

    private PageUserSelector userSelector;

    private boolean deletePhoto;

    @NotSerialized
    private List<LeaderTitle> titles;

    public OrgLeaderCrud()
    {
        setLog(true);
    }

    public Integer getOrgId()
    {
        return orgId;
    }

    public void setOrgId(Integer orgId)
    {
        this.orgId = orgId;
    }

    public OrgInfo getOrgInfo() throws Exception
    {
        if (orgId != null && orgInfo == null)
            orgInfo = dao.getOrgInfo(orgId);
        return orgInfo;
    }

    @Select(field = "entity.userId")
    public PageUserSelector getUserSelector() throws Exception
    {
        if (userSelector == null)
        {
            userSelector = new PageUserSelector();

            OrgInfo orgInfo = getOrgInfo();
            if (orgInfo != null)
                userSelector.setRootId(orgInfo.getDeptId());
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
            titles = dao.getLeaderTitles(getOrgInfo().getDeptId());

        return titles;
    }

    @Override
    protected String getParentField()
    {
        return "orgId";
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        Leader leader = getEntity();
        leader.setDeptId(getOrgInfo().getDeptId());

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        Leader leader = getEntity();
        if (deletePhoto && leader.getPhoto() == null)
            leader.setPhoto(Null.ByteArray);

        return true;
    }

    protected void initListView(SubListView view) throws Exception
    {
        view.addColumn("领导姓名", "leaderName");
        view.addColumn("职务", "title!=null?title.titleName:titleName");
        view.addColumn("联系电话", "phone");
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

        List<Component> photoComponents = new ArrayList<Component>(3);
        photoComponents.add(new CFile("photo").setProperty("id", "photo"));

        byte[] photo = getEntity().getPhoto();
        if (photo != null)
        {
            photoComponents.add(new CHref("查看照片").setProperty("id", "showPhoto"));
            photoComponents.add(new CHref("删除照片").setProperty("id", "deletePhoto"));
        }

        view.addComponent("照片", new CUnion(photoComponents));

        view.addComponent("联系电话", "phone");
        view.addComponent("电子邮件", "email");

        view.addDoubleComponent("工作地址", "workAddress");

        view.addDoubleComponent("职责分工", new CTextArea("responsibility").setHeight("60px"));
        view.addDoubleComponent("介绍", new CTextArea("introduction").setHeight("60px"));
//        view.addDoubleComponent("简历", new CTextArea("resume").setHeight("200px"));

        view.addDefaultButtons();

        view.importJs("/portal/org/leader.js");
        view.importCss("/portal/org/leader.css");

        view.setPage("double");

        return view;
    }
}
