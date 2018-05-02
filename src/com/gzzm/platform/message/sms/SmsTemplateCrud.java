package com.gzzm.platform.message.sms;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.CTextArea;

import java.util.Collection;

/**
 * @author camel
 * @date 13-8-15
 */
@Service(url = "/message/sms/template")
public class SmsTemplateCrud extends BaseNormalCrud<SmsTemplate, Integer>
{
    @Like
    private String templateName;

    @Like
    private String content;

    private boolean dept;

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    @NotCondition
    private Integer deptId;

    @UserId
    @NotCondition
    private Integer userId;

    public SmsTemplateCrud()
    {
        addOrderBy("templateName");
    }

    public boolean isDept()
    {
        return dept;
    }

    public void setDept(boolean dept)
    {
        this.dept = dept;
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (dept)
            return "deptId=:deptId";
        else
            return "userId=:userId";
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        if (dept)
            setDeptId(DeptOwnedCrudUtils.getDefaultDeptId(authDeptIds, this));

        super.beforeShowList();
    }

    @Override
    public void initEntity(SmsTemplate entity) throws Exception
    {
        super.initEntity(entity);

        if (dept)
            entity.setDeptId(deptId);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setUserId(userId);

        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;

        if (dept && (authDeptIds == null || authDeptIds.size() > 1))
        {
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId");
        }
        else
        {
            view = new PageTableView();
        }

        view.addComponent("模板名称", "templateName");
        view.addComponent("内容", "content");

        view.addColumn("模板名称", "templateName").setWidth("150");
        view.addColumn("内容", "content").setAutoExpand(true).setWrap(true);

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("模板名称", "templateName");
        view.addComponent("模板内容", new CTextArea("content"));

        view.addDefaultButtons();

        return view;
    }
}
