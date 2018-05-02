package com.gzzm.ods.business;

import com.gzzm.ods.flow.OdFlowComponent;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.form.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;


/**
 * @author fwj
 * @date 2011-7-4
 */
@Service(url = "/ods/business/model")
public class BusinessModelCrud extends DeptOwnedNormalCrud<BusinessModel, Integer>
{
    @Inject
    private List<FormType> formTypes;

    @Inject
    private List<FlowType> flowTypes;

    @Inject
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<BusinessType> businessTypes;

    @Like
    private String businessName;

    private BusinessType type;

    private BusinessTag tag;

    private boolean test;

    public BusinessModelCrud()
    {
    }

    public String getBusinessName()
    {
        return businessName;
    }

    public void setBusinessName(String businessName)
    {
        this.businessName = businessName;
    }

    public BusinessType getType()
    {
        return type;
    }

    public void setType(BusinessType type)
    {
        this.type = type;
    }

    public BusinessTag getTag()
    {
        return tag;
    }

    public void setTag(BusinessTag tag)
    {
        this.tag = tag;
    }

    public boolean isTest()
    {
        return test;
    }

    public void setTest(boolean test)
    {
        this.test = test;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (test)
            return "type in ('send','receive','inner')";
        return null;
    }

    private String getType(BusinessType type, List<? extends Value<String>> types)
    {
        if (type == null)
            return "inner";

        String typeString = type.getType();

        if ("unionseal".equals(typeString))
            return "union";

        for (Value<String> type1 : types)
        {
            String type1String = type1.valueOf();
            if (type1String.equals(typeString))
                return type1String;
        }

        return "inner";
    }

    private String getFormType(BusinessType type)
    {
        return getType(type, formTypes);
    }

    private String getFlowType(BusinessType type)
    {
        return getType(type, flowTypes);
    }

    @NotSerialized
    @Select(field = "entity.flowId", text = "flowName", value = "ieFlowId")
    public List<FlowInfo> getFlowInfos() throws Exception
    {
        if (getEntity() != null)
            return FlowApi.getSelectableFlowList(getDeptId(), getFlowType(getEntity().getType()), getAuthDeptIds());
        return null;
    }

    @NotSerialized
    @Select(field = "entity.formId", text = "formName", value = "ieFormId")
    public List<FormInfo> getFormInfos() throws Exception
    {
        if (getEntity() != null)
            return FormApi.getSelectableFormList(getDeptId(), getFormType(getEntity().getType()), getAuthDeptIds());

        return null;
    }

    @NotSerialized
    @Select(field = "entity.sendFormId", text = "formName", value = "ieFormId")
    public List<FormInfo> getSendFormInfos() throws Exception
    {
        if (getEntity() != null)
            return FormApi.getSelectableFormList(getDeptId(), "send", getAuthDeptIds());

        return null;
    }

    @NotSerialized
    @Select(field = "entity.componentType")
    public List<KeyValue<String>> getComponents()
    {
        return FlowComponents.getComponents(OdFlowComponent.class);
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public void initEntity(BusinessModel entity) throws Exception
    {
        super.initEntity(entity);

        if (entity.getState() == null)
            entity.setState(BusinessState.published);

        if (entity.getDisplay() == null)
            entity.setDisplay(true);

        if (entity.getType() == null)
            entity.setType(businessTypes.get(0));
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = (getAuthDeptIds() != null && getAuthDeptIds().size() == 1) ? new PageTableView() :
                new ComplexTableView(new AuthDeptDisplay(), "deptId");

        view.addComponent("业务名称", "businessName");
        view.addComponent("类型", "type");
        view.addComponent("使用方式", "tag");

        view.addColumn("业务名称", "businessName");
        view.addColumn("类型", "type").setWidth("60").setAlign(Align.left);
        view.addColumn("使用方式", "tag");
        view.addColumn("流程", "flow.flowName");
        view.addColumn("表单", "form.formName");
        view.addColumn("发文表单", "sendForm.formName");
        view.addColumn("业务简称", "simpleName").setWidth("60").setHidden(true);
        view.addColumn("业务状态", "state").setWidth("60");

        view.addColumn("测试", new CButton("测试", "testFlow(${businessId},${deptId})")).setWidth("60");

        if (test)
        {
            view.addButton(Buttons.query());
        }
        else
        {
            view.defaultInit();
            view.addButton(Buttons.sort());
        }

        view.importJs("/ods/business/model.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("业务名称", "businessName");
        view.addComponent("业务类型", "type").setProperty("onchange", "typeChange()");
        view.addComponent("使用方式", "tag");
        view.addComponent("关联流程", "flowId");
        view.addComponent("关联表单", "formId");
        view.addComponent("发文表单", "sendFormId");
        view.addComponent("关联功能", "componentType");
        view.addComponent("导入脚本", "jsFile");
        view.addComponent("业务简称", "simpleName");
        view.addComponent("类型名称", "typeName");
        view.addComponent("业务状态", "state");
        view.addComponent("可发起", new CCombox("display"));

        view.addDefaultButtons();

        view.importJs("/ods/business/model.js");

        return view;
    }
}
