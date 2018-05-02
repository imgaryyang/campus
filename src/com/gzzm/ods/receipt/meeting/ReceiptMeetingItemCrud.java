package com.gzzm.ods.receipt.meeting;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.view.components.*;

import java.util.Date;

/**
 * @author camel
 * @date 12-4-8
 */
@Service(url = "/ods/receipt/meeting/items/crud")
public class ReceiptMeetingItemCrud extends SubListCrud<ReceiptMeetingItem, Long>
{
    private Long receiptId;

    private Integer deptId;

    @UserId
    private Integer userId;

    public ReceiptMeetingItemCrud()
    {
    }

    public Long getReceiptId()
    {
        return receiptId;
    }

    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    @Override
    protected String getParentField()
    {
        return "receiptId";
    }

    @Override
    public void initEntity(ReceiptMeetingItem entity) throws Exception
    {
        entity.setDeptId(deptId);
        entity.setReceiptId(receiptId);
        entity.setReplied(false);

        super.initEntity(entity);
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setCreateTime(new Date());
        getEntity().setCreator(userId);

        return true;
    }

    @Override
    protected void initListView(SubListView view) throws Exception
    {
        view.addColumn("参会人", "userName");
        view.addColumn("性别", "sex");
        view.addColumn("联系电话", "phone");
        view.addColumn("职务", "station");
        view.addColumn("参会时段", "joinTime");
        view.addColumn("食宿安排", "accommodation");
        view.addColumn("备注", "remark");
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        CData component = view.addComponent("参会人", "userName");

        if (getEntity().getUserId() != null)
            component.setProperty("readOnly", null);

        view.addComponent("性别", "sex");
        view.addComponent("联系电话", "phone");
        view.addComponent("职务", "station");
        view.addComponent("参会时段", "joinTime");
        view.addComponent("食宿安排", "accommodation");
        view.addComponent("备注", new CTextArea("remark"));

        view.addDefaultButtons();

        return view;
    }
}
