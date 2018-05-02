package com.gzzm.oa.mail;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.CTextArea;

import java.util.Collection;

/**
 * @author camel
 * @date 2015/6/23
 */
@Service(url = "/oa/mail/sign")
public class MaiLSignCrud extends BaseNormalCrud<MailSign, Integer>
{
    @UserId
    @NotCondition
    private Integer userId;

    @NotCondition
    private Integer deptId;

    @Like
    private String title;

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    /**
     * 0表示用户所属的签名，1表示部门所属的签名
     */
    private int type;

    public MaiLSignCrud()
    {
        setLog(true);
    }

    public Integer getUserId()
    {
        return userId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected String[] getOrderWithFields()
    {
        if (type == 0)
            return new String[]{"userId"};
        else
            return new String[]{"deptId"};
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (type == 0)
            return "userId=:userId";
        else
            return "deptId=:deptId";
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        if (type == 1 && deptId == null)
        {
            deptId = DeptOwnedCrudUtils.getDefaultDeptId(authDeptIds, this);
        }
    }

    @Override
    public void initEntity(MailSign entity) throws Exception
    {
        super.initEntity(entity);

        if (type == 1 && entity.getDeptId() == null)
        {
            if (deptId == null)
                deptId = DeptOwnedCrudUtils.getDefaultDeptId(authDeptIds, this);
            entity.setDeptId(deptId);
        }
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        MailSign sign = getEntity();

        if (type == 0)
            sign.setUserId(userId);
        else
            sign.setDeptId(deptId);

        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;

        if (type == 0 || authDeptIds != null && authDeptIds.size() == 1)
            view = new PageTableView(true);
        else
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);

        view.addComponent("标题", "title");

        view.addColumn("标题", "title").setWidth("200");
        view.addColumn("内容", "content").setWrap(true);

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("标题", "title");
        view.addComponent("内容", new CTextArea("content"));

        view.addDefaultButtons();

        return view;
    }
}
