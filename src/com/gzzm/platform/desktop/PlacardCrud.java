package com.gzzm.platform.desktop;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.message.comet.CometService;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 桌面公共维护
 *
 * @author camel
 * @date 2010-5-28
 */
@Service(url = "/PlacardCrud")
public class PlacardCrud extends DeptOwnedNormalCrud<Placard, Integer>
{
    @UserId
    private Integer userId;

    @Inject
    private CometService cometService;

    @Lower(column = "createTime")
    private Date time_start;

    @Upper(column = "createTime")
    private Date time_end;

    public PlacardCrud()
    {
        addOrderBy("createTime", OrderType.desc);
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (CrudAuths.isAddable())
            setDefaultDeptId();
    }

    @Override
    public void initEntity(Placard entity) throws Exception
    {
        super.initEntity(entity);
        entity.setValid(true);
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        if (getEntity().isValid() == null)
            getEntity().setValid(false);

        return true;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        getEntity().setCreateTime(new java.util.Date());
        getEntity().setCreator(userId);

        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view;
        if (getAuthDeptIds() != null && getAuthDeptIds().size() == 1 || !CrudAuths.isAddable())
        {
            view = new PageTableView(true);
        }
        else
        {
            view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);
        }

        view.addComponent("创建时间", "time_start", "time_end");

        view.addColumn("内容", "content");
        view.addColumn("创建人", "createUser.userName");
        view.addColumn("创建时间", "createTime");
        view.addColumn("是否有效", "valid");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("内容", new CTextArea("content"));
        view.addComponent("有效", new CCheckbox("valid"));

        view.addDefaultButtons();

        return view;
    }

    @Override
    public void afterSave() throws Exception
    {
        super.afterSave();

        sendMessage();
    }

    @Override
    public void afterDelete(Integer key, boolean exists) throws Exception
    {
        super.afterDelete(key, exists);

        if (exists)
            sendMessage();
    }

    @Override
    public void afterDeleteAll() throws Exception
    {
        super.afterDeleteAll();

        sendMessage();
    }

    private void sendMessage()
    {
        cometService.sendMessageToAllUser(PlacardUpdate.instance);
    }
}
