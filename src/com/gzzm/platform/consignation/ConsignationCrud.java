package com.gzzm.platform.consignation;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.message.Message;
import com.gzzm.platform.organ.PageUserSelector;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.KeyValue;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 委托crud
 *
 * @author camel
 * @date 2010-9-1
 */
@Service(url = "/Consignation")
public class ConsignationCrud extends BaseNormalCrud<Consignation, Integer>
{
    @Inject
    @NotSerialized
    private List<ConsignationModule> modules;

    @UserId
    private Integer userId;

    @AuthDeptIds
    @NotSerialized
    private Collection<Integer> authDeptIds;

    private PageUserSelector userSelector;

    @Inject
    private ConsignationDao dao;

    private Date time_start;

    private Date time_end;

    /**
     * 0表示委托者视图，1表示被委托者视图，2表示第三方委托列表查询
     */
    private int type;

    /**
     * 用委托人的姓名做查询条件
     */
    @Like("consignerUser.userName")
    private String consignerName;

    /**
     * 用被委托人的姓名做查询条件
     */
    @Like("consigneeUser.userName")
    private String consigneeName;

    /**
     * 是否允许拒绝委托
     */
    private boolean rejectable;

    /**
     * 是否只查询有效的委托
     */
    private boolean available;

    @NotCondition
    private ConsignationState state;

    private String page;

    public ConsignationCrud()
    {
        addOrderBy("startTime", OrderType.desc);
    }

    public Integer getUserId()
    {
        return userId;
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
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

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getConsignerName()
    {
        return consignerName;
    }

    public void setConsignerName(String consignerName)
    {
        this.consignerName = consignerName;
    }

    public String getConsigneeName()
    {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName)
    {
        this.consigneeName = consigneeName;
    }

    public boolean isRejectable()
    {
        return rejectable;
    }

    public void setRejectable(boolean rejectable)
    {
        this.rejectable = rejectable;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public void setAvailable(boolean available)
    {
        this.available = available;
    }

    public ConsignationState getState()
    {
        return state;
    }

    public void setState(ConsignationState state)
    {
        this.state = state;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public List<ConsignationModule> getModules()
    {
        return modules;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        StringBuilder s = new StringBuilder("endTime>=?time_start and startTime<=?time_end");

        if (available)
        {
            s.append(" and state=1 and startTime<=sysdate() and addday(endTime,1)>sysdate()");
        }
        else if (state != null)
        {
            if (state == ConsignationState.available)
            {
                s.append(" and state=1 and startTime<=sysdate() and addday(endTime,1)>sysdate()");
            }
            else if (state == ConsignationState.end)
            {
                s.append(" and (state=4 or addday(endTime,1)<=sysdate())");
            }
            else if (state == ConsignationState.notStarted)
            {
                s.append(" and (state=5 or startTime>sysdate())");
            }
            else
            {
                s.append(" and state=:state");
            }
        }

        if (type == 0)
        {
            s.append(" and consigner=:userId");
        }
        else if (type == 1)
        {
            s.append(" and consignee=:userId");
        }
        else if (type == 2 && authDeptIds != null)
        {
            s.append(" and (exists d in consignerUser.depts : d.deptId in ?authDeptIds)");
        }

        return s.toString();
    }

    @Override
    public String check() throws Exception
    {
        if (getEntity().getConsignee().equals(userId))
            return "com.gzzm.platform.consignation.consignToSelf";

        List<Consignation> consignations = dao.getConsignations(userId, getEntity().getStartTime(),
                getEntity().getEndTime(), getEntity().getModules());

        if (consignations.size() > 0)
        {
            for (Consignation consignation : consignations)
            {
                if (getEntity().getConsignationId() == null ||
                        !getEntity().getConsignationId().equals(consignation.getConsignationId()))
                {
                    Date startTime = consignation.getStartTime();
                    if (startTime.before(getEntity().getStartTime()))
                        startTime = getEntity().getStartTime();

                    Date endTime = consignation.getEndTime();
                    if (endTime.after(getEntity().getEndTime()))
                        endTime = getEntity().getEndTime();

                    String consigneeName = consignation.getConsigneeUser().getUserName();

                    return Tools.getMessage("com.gzzm.platform.consignation.repetition", startTime, endTime,
                            consigneeName);
                }
            }
        }

        return null;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();
        getEntity().setConsigner(userId);

        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        //不允许改委托人
        getEntity().setConsigner(null);

        return true;
    }

    @Override
    public void afterSave() throws Exception
    {
        super.afterSave();

        Consignation consignation = getEntity();

        if (consignation.getConsigneeUser() == null)
        {
            consignation.setConsignerUser(dao.getUser(userId));
        }

        Message message = new Message();
        message.setSender(consignation.getConsigner());
        message.setUserId(consignation.getConsignee());
        message.setMessage(Tools.getMessage("com.gzzm.platform.consignation.notify", consignation));
        message.setUrl("/Consignation?type=1");

        message.send();
    }

    @Override
    @Forward(page = "/platform/consignation/consignation.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/platform/consignation/consignation.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(type == 0);

        if ("table".equals(page))
        {
            view.addColumn("委托人", "consignerUser.userName");
            view.addColumn("开始时间", "startTime");
            view.addColumn("结束时间", "endTime");
            view.setPage("table");

            view.importJs("/platform/consignation/list.js");

        }
        else
        {
            view.addComponent("时间", "time_start", "time_end");

            if (type != 0)
                view.addComponent("委托人", "consignerName");

            if (type != 1)
                view.addComponent("被委托人", "consigneeName");

            if (!rejectable)
            {
                view.addComponent("状态", new CCombox("available", new Object[]{
                        new KeyValue<String>("false", "全部委托"),
                        new KeyValue<String>("true", "有效委托")
                }).setNullable(false));
            }
            else
            {
                view.addComponent("状态", new CCombox("state", new Object[]{
                        ConsignationState.noAccepted,
                        ConsignationState.notStarted,
                        ConsignationState.available,
                        ConsignationState.end,
                        ConsignationState.stopped,
                        ConsignationState.rejected,
                }));
            }

            if (type != 0)
                view.addColumn("委托人", "consignerUser.userName");

            if (type != 1)
                view.addColumn("被委托人", "consigneeUser.userName");

            view.addColumn("开始时间", "startTime");
            view.addColumn("结束时间", "endTime");
            view.addColumn("委托范围", new FieldCell("moduleNames").setOrderable(false)).setWrap(true).setWidth("200");
            view.addColumn("状态", new FieldCell("showState").setOrderable(false)).setWidth("70");
            view.addColumn("说明", "remark").setAlign(Align.left).setAutoExpand(true).setWrap(true);

            if (type == 0)
            {
                view.addColumn("终止/恢复", new ConditionComponent()
                        .add("state.name()=='available'&&!end", new CButton("终止", "stop(${consignationId})"))
                        .add("state.name()=='stopped'&&!end", new CButton("恢复", "restore(${consignationId})")));

                view.addColumn("查看委托事项", new CHref("查看委托事项").setAction("showItems(${consignationId})"));

                view.defaultInit(false);
            }
            else if (type == 1)
            {
                if (rejectable)
                {
                    view.addColumn("接受", new ConditionComponent()
                            .add("state.name()=='noAccepted'&&!end", new CButton("接受", "accept(${consignationId})")));
                    view.addColumn("拒绝", new ConditionComponent().add("state.name()=='noAccepted'&&!end",
                            new CButton("拒绝", "reject(${consignationId})")));
                }

                view.addColumn("查看委托事项", new CHref("查看委托事项").setAction("showItems(${consignationId})"));

                view.addButton(Buttons.query());
            }
            else
            {
                view.addButton(Buttons.query());
            }

            view.importJs("/platform/consignation/consignation.js");

        }

        return view;
    }

    @Select(field = "entity.consignee")
    public PageUserSelector getUserSelector()
    {
        if (userSelector == null)
            userSelector = new PageUserSelector();

        return userSelector;
    }

    /**
     * 修改委托的状态
     *
     * @param consignationId 要修改状态的委托ID
     * @param state          要改变的目标状态
     * @throws Exception 数据库操作错误
     */
    private void setState(Integer consignationId, ConsignationState state) throws Exception
    {
        Consignation consignation = new Consignation();
        consignation.setConsignationId(consignationId);
        consignation.setState(state);

        update(consignation);
    }

    /**
     * 终止委托
     *
     * @param consignationId 要终止的委托ID
     * @throws Exception 数据操作异常
     */
    @Service
    @ObjectResult
    public void stop(Integer consignationId) throws Exception
    {
        setState(consignationId, ConsignationState.stopped);
    }

    /**
     * 恢复委托
     *
     * @param consignationId 要终止的委托ID
     * @throws Exception 数据操作异常
     */
    @Service
    @ObjectResult
    public void restore(Integer consignationId) throws Exception
    {
        setState(consignationId, ConsignationState.available);
    }

    /**
     * 接收委托
     *
     * @param consignationId 要接受的委托ID
     * @throws Exception 数据操作异常
     */
    @Service
    @ObjectResult
    public void accept(Integer consignationId) throws Exception
    {
        setState(consignationId, ConsignationState.available);
    }

    /**
     * 拒绝委托
     *
     * @param consignationId 要拒绝的委托ID
     * @throws Exception 数据操作异常
     */
    @Service
    @ObjectResult
    public void reject(Integer consignationId) throws Exception
    {
        setState(consignationId, ConsignationState.rejected);
    }
}
