package com.gzzm.sms;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 短信服务用户维护
 *
 * @author camel
 * @date 2010-11-4
 */
@Service(url = "/sms/user")
public class SmsUserCrud extends BaseNormalCrud<SmsUser, Integer>
{
    /**
     * 旧密码，当接收到此密码时表示保留原来的密码
     */
    private static final String OLDPASSWOED = "######$$$$$$";

    @Inject
    private SmsDao dao;

    @Inject
    private SmsConfig config;

    /**
     * 用户名
     */
    @Like
    private String userName;

    private UserGateway gateway;

    private GatewayType gatewayType;

    private DeptTreeModel deptTree;

    public SmsUserCrud()
    {
        setLog(true);
        addOrderBy("userName");
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public UserGateway getGateway()
    {
        return gateway;
    }

    public void setGateway(UserGateway gateway)
    {
        this.gateway = gateway;
    }

    public GatewayType getGatewayType()
    {
        return gatewayType;
    }

    public void setGatewayType(GatewayType gatewayType)
    {
        this.gatewayType = gatewayType;
    }

    @Select(field = "entity.deptId")
    public DeptTreeModel getDeptTree()
    {
        if (deptTree == null)
            deptTree = new DeptTreeModel();

        return deptTree;
    }

    @Select(field = "entity.processor")
    @NotSerialized
    @NotCondition
    public List<String> getProcessors()
    {
        return config.getProcessors();
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        if ("auth".equals(getForward()))
        {
            // 授权时不修改其它字段，重新创建一个对象
            SmsUser user = new SmsUser();
            user.setUserId(getEntity().getUserId());

            // 设置授权用户时，如果把所有人清空了，request中取不到数据，要情空授权用户
            List<User> users = getEntity().getAuthUsers();
            if (users == null)
                user.setAuthUsers(new ArrayList<User>(0));
            else
                user.setAuthUsers(users);

            setEntity(user);
        }
        else
        {
            SmsUser user = getEntity();
            if (OLDPASSWOED.equals(user.getPassword()))
                user.setPassword(null);

            if (user.isInheritable() == null)
                user.setInheritable(false);
        }

        return true;
    }

    private List<GatewayClass> getGatewayClasses()
    {
        return GatewayClass.getGatewayClasses(gatewayType);
    }

    @NotSerialized
    @Select(field = "gateway.gatewayId")
    public List<Gateway> getGateways() throws Exception
    {
        return gatewayType == null ? null : dao.getGateways(getGatewayClasses());
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.importJs("/sms/user.js");

        view.addComponent("用户名", "userName");

        view.addColumn("用户名", "userName");
        view.addColumn("登录名", "loginName");

        for (GatewayType gatewayType : GatewayType.values())
        {
            if (gatewayType != GatewayType.common)
            {
                view.addColumn(gatewayType.toString(),
                        new CHref("${getGatewayName('" + gatewayType.name() + "')}").setAction(
                                "editGateway(${userId},'" + gatewayType.name() + "')"));
            }
        }

        view.addColumn("处理器", "processor");
        view.addColumn("发送短信", new CButton("发送短信", "sendSms(${userId})"));

        view.defaultInit();

        view.addColumn("授权", new CButton("授权", Actions.show("auth"))).setWidth("50").setLocked(true);

        return view;
    }

    @Override
    protected Object createView() throws Exception
    {
        if (gatewayType != null)
        {
            SimpleDialogView view = new SimpleDialogView();

            view.importJs("/sms/user.js");

            view.addComponent("网关", "this.gateway.gatewayId");
            view.addComponent("服务代码", "this.gateway.serviceCode");
            view.addComponent("短信子码", "this.gateway.subNumber");
            view.addComponent("用户名", "this.gateway.userName");
            view.addComponent("密码", new CPassword("this.gateway.password"));

            view.addButton(new CButton("crud.save", "saveGateway();"));
            view.addButton(Buttons.close());

            return view;
        }
        else
        {
            return super.createView();
        }
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("用户名", "userName");
        view.addComponent("登录名", "loginName");
        view.addComponent("所属部门", "deptId")
                .setProperty("text", getEntity().getDept() != null ? getEntity().getDept().getDeptName() : "");

        String password = isNew$() ? "" : OLDPASSWOED;

        view.addComponent("密码", new CPassword("password").setProperty("value", password));
        view.addComponent("密码确认",
                new CPassword(null).setProperty("name", "password_confirm").setProperty("equal", "entity.password")
                        .setProperty("value", password).setProperty("require", null));

        view.addComponent("处理器", "processor");
        view.addComponent("被部门使用", new CCheckbox("inheritable"));

        view.addDefaultButtons();

        view.addButton(new CBr());

        for (GatewayType gatewayType : GatewayType.values())
        {
            if (gatewayType != GatewayType.common)
            {
                view.addButton(new CButton(gatewayType.toString(),
                        "editGateway(${entity.userId},'" + gatewayType.name() + "')"));
            }
        }

        view.importJs("/sms/user.js");
        view.importCss("/sms/user.css");

        return view;
    }

    @Forward(page = Pages.EDIT)
    @Service
    public String showGateway(Integer userId, GatewayType type) throws Exception
    {
        gatewayType = type;

        gateway = dao.getUserGateway(userId, type);

        if (gateway == null)
        {
            gateway = new UserGateway();
            gateway.setUserId(userId);
        }

        return null;
    }

    @ObjectResult
    @Service
    public void saveGateway() throws Exception
    {
        if (gateway.getGatewayId() != null)
        {
            gateway.setType(gatewayType);

            dao.save(gateway);
        }
        else
        {
            dao.deleteUserGateway(gateway.getUserId(), gatewayType);
        }
    }
}
