package com.gzzm.platform.log;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.components.CCombox;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 登录日志查询
 *
 * @author camel
 * @date 2009-7-24
 */
@Service(url = "/LoginLog")
public class LoginLogQuery extends UserLogQuery<LoginLog>
{
    /**
     * 用action作为查询条件
     */
    private LoginAction loginAction;

    /**
     * 注入ca服务列表
     */
    @Inject
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<CertLoginServiceItem> certServices;

    public LoginLogQuery()
    {
    }

    public LoginAction getLoginAction()
    {
        return loginAction;
    }

    public void setLoginAction(LoginAction loginAction)
    {
        this.loginAction = loginAction;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("用户", "userName");
        view.addComponent("部门", "deptIds");
        view.addComponent("操作时间", "time_start", "time_end");
        view.addComponent("动作", new CCombox("loginAction",
                new Object[]{LoginAction.login, LoginAction.logout, LoginAction.expire, LoginAction.kickout}));

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        view.addColumn("用户名称", "user.userName");
        view.addColumn("登录部门", "dept.allName()").setOrderFiled("dept.leftValue").setAutoExpand(true);
        view.addColumn("动作", "loginAction").setWidth("60");
        view.addColumn("操作时间", "logTime");
        view.addColumn("IP", "ip");
        view.addColumn("浏览器版本", "navigator");

        if (certServices != null && certServices.size() > 0)
        {
            view.addColumn("证书ID", "certId");
            view.addColumn("证书名称", "certName");

            if (certServices.size() > 1)
                view.addColumn("证书类型", "certType");
        }

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("登录日志");
    }
}