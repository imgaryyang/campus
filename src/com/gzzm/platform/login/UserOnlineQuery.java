package com.gzzm.platform.login;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.Filter;
import net.cyan.crud.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.FieldCell;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 在线用户查询
 *
 * @author camel
 * @date 2009-7-30
 */
@Service(url = "/UserOnline")
public class UserOnlineQuery extends DeptOwnedList<UserOnlineInfo> implements Filter<UserOnlineInfo>
{
    @Inject
    private UserOnlineList userOnlineList;

    private String userName;

    private String appId;

    public UserOnlineQuery()
    {
    }

    protected void loadList() throws Exception
    {
        //从在线列表中查询满足条件的数据
        List<UserOnlineInfo> list = userOnlineList.query(this);

        //排序
        if (getOrderBy() == null)
            setOrderBy(new OrderBy("loginTime", OrderType.desc));

        sort(list);
        setAllList(list);
    }

    public boolean accept(UserOnlineInfo userOnlineInfo) throws Exception
    {
        return (queryDeptIds == null || queryDeptIds.contains(userOnlineInfo.getDeptId())) &&
                (userName == null || userOnlineInfo.getUserName().contains(userName)) &&
                (appId == null || userOnlineInfo.hasApp(appId));
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("用户", "userName");
        view.addComponent("部门", "deptIds");
        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        view.addColumn("用户名称", "userName");
        view.addColumn("登录部门", "allDeptName").setAutoExpand(true);
        view.addColumn("登录时间", "loginTime");
        view.addColumn("IP", new FieldCell("ip").setOrderable(false)).setWidth("140");
        view.addColumn("浏览器", "navigator").setWidth("90");

        if (!"exp".equals(getAction()))
            view.addColumn("当前操作", "state+'['+timeMinute+']'").setWidth("200");

        view.addColumn("最后操作时间", "lastTime");


        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("在线用户");
    }
}
