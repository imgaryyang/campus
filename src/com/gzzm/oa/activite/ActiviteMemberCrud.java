package com.gzzm.oa.activite;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

/**
 * 参与成员审核
 *
 * @author lfx
 * @date 11-10-8
 */
@Service(url = "/oa/activite/member")
public class ActiviteMemberCrud extends BaseNormalCrud<ActiviteMember, Integer>
{
    @Inject
    private ActiviteService service;

    private Integer activiteId;

    private MemberState state;

    @Like("user.userName")
    private String userName;

    private Activite activite;

    public ActiviteMemberCrud()
    {
    }

    public MemberState getState()
    {
        return state;
    }

    public void setState(MemberState state)
    {
        this.state = state;
    }

    public Integer getActiviteId()
    {
        return activiteId;
    }

    public void setActiviteId(Integer activiteId)
    {
        this.activiteId = activiteId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Activite getActivite() throws Exception
    {
        if (activite == null)
            activite = service.getDao().getActivite(getActiviteId());
        return activite;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public int getOrderFieldLength()
    {
        return 4;
    }

    @Override
    protected String[] getOrderWithFields()
    {
        if (getState() != null)
        {
            return new String[]{"activiteId", "state"};
        }
        else
        {
            return new String[]{"activiteId"};
        }
    }


    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.setCheckable("${state.name()!='invite'}");

        view.setTitle(getActivite().getTitle() + "-参与人员管理");

        view.addComponent("参与人员", "userName");
        view.addComponent("状态", "state");
        view.addColumn("参与人员", "user");
        view.addColumn("申请/邀请时间", "applyTime");
        view.addColumn("状态", "state");

        view.addButton(Buttons.query());
        view.addButton(new CButton("同意参与", "memberCheck('participation')").setIcon(Buttons.getIcon("ok")));
        view.addButton(new CButton("不同意参与", "memberCheck('fail')").setIcon(Buttons.getIcon("cancel")));
        view.addButton(new CButton("邀请其他人参加", "addMembers()").setIcon(Buttons.getIcon("add")));
        view.addButton(Buttons.delete());
        view.addButton(Buttons.sort());

        view.importJs("/oa/activite/activitemember.js");

        return view;
    }


    /**
     * 修改成员状态
     *
     * @param memberState 状态
     * @throws Exception 数据更新操作时间
     */
    @ObjectResult
    @Service(method = HttpMethod.post)
    public void memberCheck(MemberState memberState) throws Exception
    {
        if (getKeys() != null)
        {
            service.memberCheck(getKeys(), memberState);
        }
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public void addMembers(Integer[] userIds) throws Exception
    {
        service.addMembers(activiteId, userIds);
    }
}