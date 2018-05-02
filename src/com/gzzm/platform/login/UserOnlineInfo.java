package com.gzzm.platform.login;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.menu.MenuItem;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.RequestSession;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.*;
import java.util.*;

/**
 * 用户在线信息
 *
 * @author camel
 * @date 2009-7-18
 */
public final class UserOnlineInfo extends UserInfo
{
    private static final long serialVersionUID = -2331705645331349175L;

    @Inject
    private static Provider<UserOnlineList> userOnlineListProvider;

    @Inject
    private static Provider<UserCheckService> userCheckServiceProvider;

    /**
     * UserOnlineInfo存放在session中的属性名
     */
    static final String SESSIONNAME_ID = "userOnlineInfo_id";

    static final String SESSIONNAME = "userOnlineInfo";

    static final String SESSIONNAME_TEMP = "userOnlineInfo_temp";

    static final String SESSIONNAME_TEMP1 = "userOnlineInfo_temp1";

    static final String COOKIE_LOGINID = "loginId";

    @Inject
    private static Provider<GlobalConfig> configProvider;

    /**
     * id，用来标识在线信息的唯一性
     */
    private String id;


    /**
     * 证书类型
     */
    private String certType;

    /**
     * 证书id
     */
    private String certId;

    /**
     * 证书名
     */
    private String certName;

    /**
     * ip
     */
    private String ip;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 服务器的名称
     */
    private String serverName;

    /**
     * 登录的系统名称
     */
    private String systemId;

    /**
     * 当前状态
     */
    private String state;

    /**
     * 最后一个改变状态的时间
     */
    private long lastTime;

    /**
     * 浏览器
     */
    private String navigator;

    UserOnlineInfo()
    {
        this.id = Tools.getUUID();
        lastTime = System.currentTimeMillis();
    }

    public static String getSessionId(HttpServletRequest request)
    {
        RequestSession session = RequestSession.getSession(request);
        if (session == null)
            return null;

        return session.get(SESSIONNAME_ID);
    }

    /**
     * 用Inject声明此方法为对象创建方法
     *
     * @param request httprequest
     * @return 从httprequest中获取UserOnlineInfo
     */
    @Inject
    public static UserOnlineInfo getUserOnlineInfo(HttpServletRequest request)
    {
        if (request == null)
            return null;

        String id = getSessionId(request);
        if (StringUtils.isEmpty(id))
            return null;

        return userOnlineListProvider.get().get(id);
    }

    public static UserOnlineInfo getUserOnlineInfo(String id)
    {
        return userOnlineListProvider.get().get(id);
    }

    public void login(HttpServletRequest request, HttpServletResponse response)
    {
        //添加到在线列表中
        userOnlineListProvider.get().put(this);

        RequestSession session = RequestSession.getSession(request, response);
        session.set(SESSIONNAME_ID, id);

        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute(SESSIONNAME, this);
    }

    void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getCertType()
    {
        return certType;
    }

    void setCertType(String certType)
    {
        this.certType = certType;
    }

    public String getCertId()
    {
        return certId;
    }

    void setCertId(String certId)
    {
        this.certId = certId;
    }

    public String getCertName()
    {
        return certName;
    }

    void setCertName(String certName)
    {
        this.certName = certName;
    }

    @Override
    public String getOperatorName()
    {
//        if (StringUtils.isEmpty(operatorName))
//            return certName;

        return super.getOperatorName();
    }

    public String getServerName()
    {
        return serverName;
    }

    void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public String getSystemId()
    {
        return systemId;
    }

    void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }

    public String getSystemName()
    {
        return Tools.getMessage(configProvider.get().getSystemName(systemId));
    }

    public Date getLoginTime()
    {
        return loginTime;
    }

    void setLoginTime(Date loginTime)
    {
        this.loginTime = loginTime;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    void setAllDeptData(boolean allDeptData)
    {
        this.allDeptData = allDeptData;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getNavigator()
    {
        return navigator;
    }

    public void setNavigator(String navigator)
    {
        this.navigator = navigator;
    }

    public synchronized void setState(String state)
    {
        if (state != null)
            this.state = state;
        this.lastTime = System.currentTimeMillis();
    }

    public void refreshTime()
    {
        setState(null);
    }

    /**
     * 最后访问时间
     *
     * @return 最后访问时间
     */
    public long getLastTimeMillis()
    {
        return lastTime;
    }

    public Date getLastTime()
    {
        return new Date(lastTime);
    }

    /**
     * 获得访问当前功能停留的毫秒数
     *
     * @return 访问当前功能停留的毫秒数
     */
    public long getTimeMillis()
    {
        return lastTime > 0 ? System.currentTimeMillis() - lastTime : 0;
    }

    /**
     * 获得访问当前功能停留的秒数
     *
     * @return 访问当前功能停留的秒数
     */
    public long getTimeMinute()
    {
        return getTimeMillis() / 1000 / 60;
    }

    public String getState()
    {
        return state == null ? Tools.getMessage("useronline.stat.empty") : state;
    }

    public String getId()
    {
        return id;
    }

    void setId(String id)
    {
        this.id = id;
    }

    public void setAdmin(boolean admin)
    {
        this.admin = admin || userId == 1;
    }

    public boolean hasAuth(HttpServletRequest request, String auth, boolean force)
    {
        return admin || hasAuth(MenuItem.getMenuId(request), auth, force);
    }

    public boolean hasAuth(HttpServletRequest request, String auth)
    {
        return hasAuth(request, auth, false);
    }

    /**
     * 获得对当前请求的功能拥有权限的部门
     *
     * @param request 当前请求
     * @param level   部门级别，－1表示所有部门
     * @param filter  部门过滤器
     * @return 满足条件的部门列表
     */
    public Collection<Integer> getAuthDeptIds(HttpServletRequest request, int level, Filter<DeptInfo> filter)
    {
        return getAuthDeptIds(MenuItem.getMenuId(request), level, filter);
    }

    public Collection<Integer> getAuthDeptIds(HttpServletRequest request)
    {
        return getAuthDeptIds(request, -1, null);
    }

    public Collection<Integer> getAuthDeptIds(HttpServletRequest request, int level)
    {
        return getAuthDeptIds(request, level, null);
    }

    public Collection<Integer> getAuthDeptIds(HttpServletRequest request, Filter<DeptInfo> filter)
    {
        return getAuthDeptIds(request, -1, filter);
    }

    public boolean isSelf(HttpServletRequest request)
    {
        return isSelf(MenuItem.getMenuId(request));
    }

    /**
     * 重设缓存的数据
     */
    public synchronized void reset()
    {
        deptName = null;
        dept = null;
        parentDepts = null;
        apps = null;
        allDeptName = null;
        state = null;
        lastTime = System.currentTimeMillis();
        if (attributes != null)
            attributes.clear();
    }

    public UserType getUserType()
    {
        return userType;
    }

    public String getCheckNumber()
    {
        return userCheckServiceProvider.get().getCheckNumber(userId);
    }

    public boolean equals(Object o)
    {
        return this == o || o instanceof UserOnlineInfo && id.equals(((UserOnlineInfo) o).id);
    }

    public int hashCode()
    {
        return id.hashCode();
    }
}
