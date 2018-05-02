package com.gzzm.ods.exchange.back;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.Null;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

/**
 * 退文理由维护
 *
 * @author ldp
 * @date 2018/1/10
 */
@Service(url = "/ods/exchange/backreason")
public class BackReasonCrud extends DeptOwnedNormalCrud<BackReason, Integer>
{
    @Inject
    private UserOnlineInfo userOnlineInfo;

    private Integer typeId;

    @Like
    private String reason;

    private BackReasonTypeModel backReasonTypeModel;

    public BackReasonCrud()
    {
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view;
        if (getAuthDeptIds() != null && getAuthDeptIds().size() == 1)
        {
            view = new ComplexTableView(new BackReasonTypeDisplay(), "typeId");
        }
        else
        {
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId");
            view.addComponent("退文类型", "typeId");
        }

        view.addComponent("退文理由", "reason");

        view.addColumn("退文理由", "reason");
        view.addColumn("退文类型", "type.typeName");

        view.defaultInit(false);
        view.addButton(Buttons.sort());

        view.importJs("/ods/exchange/back/backreason.js");

        return view;
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return new String[]{"deptId", "typeId"};
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

        BackReasonType type = getEntity().getType();
        view.addComponent("所属类型", "typeId").setProperty("text", type == null ? "" : type.getTypeName());
        view.addComponent("退文类型", new CTextArea("reason"));

        view.addDefaultButtons();

        return view;
    }

    @Override
    public void initEntity(BackReason entity) throws Exception
    {
        super.initEntity(entity);
        if (!Null.isNull(typeId))
        {
            entity.setTypeId(typeId);
            entity.setType(getCrudService().get(BackReasonType.class, typeId));
        }
    }

    @Select(field = {"typeId", "entity.typeId"})
    public BackReasonTypeModel getBackReasonTypeModel() throws Exception
    {
        if (backReasonTypeModel == null)
            backReasonTypeModel = new BackReasonTypeModel();

        return backReasonTypeModel;
    }
}
