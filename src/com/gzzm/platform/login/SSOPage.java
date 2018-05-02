package com.gzzm.platform.login;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 单点登录
 *
 * @author camel
 * @date 11-11-25
 */
@Service
public class SSOPage
{
    @Inject
    private UserCheckService userCheckService;

    @Inject
    private SSOConfig config;

    @Inject
    private SSODao dao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 外部系统的用户ID
     */
    private String userId;

    /**
     * 外部系统的部门ID
     */
    private String deptId;

    private Integer uid;

    private Integer did;

    private String path;

    private String index;

    private String menuId;

    private String server;

    private String checkNumber;

    public SSOPage()
    {
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getDeptId()
    {
        return deptId;
    }

    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }

    public Integer getUid()
    {
        return uid;
    }

    public void setUid(Integer uid)
    {
        this.uid = uid;
    }

    public Integer getDid()
    {
        return did;
    }

    public void setDid(Integer did)
    {
        this.did = did;
    }

    public String getMenuId()
    {
        return menuId;
    }

    public void setMenuId(String menuId)
    {
        this.menuId = menuId;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getIndex()
    {
        return index;
    }

    public void setIndex(String index)
    {
        this.index = index;
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public String getCheckNumber()
    {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber)
    {
        this.checkNumber = checkNumber;
    }

    @Service(url = "/sso/login", session = true)
    public String ssoLogin() throws Exception
    {
        if (StringUtils.isEmpty(userId) && uid == null)
        {
            return null;
        }
        else
        {
            if (config != null && config.getItems() != null)
            {
                if (StringUtils.isEmpty(server))
                    return null;
                String url = config.getItems().get(server);

                if (StringUtils.isEmpty(url))
                    return null;

                url = url.replace("{id}", StringUtils.isEmpty(userId) ? uid.toString() : userId);
                url = url.replace("{checkNumber}", checkNumber);

                String result = IOUtils.urlToString(url);
                if (!"true".equals(result))
                    return null;
            }

            User user;
            if (uid != null)
                user = dao.getUser(uid);
            else
                user = dao.getUserBySourceId(userId);

            if (user == null)
            {
                return null;
            }
            else
            {
                Dept dept = null;
                if (StringUtils.isEmpty(deptId) && did == null)
                {
                    dept = user.getDepts().get(0);
                }
                else
                {
                    for (Dept dept1 : user.getDepts())
                    {
                        if (did != null)
                        {
                            if (did.equals(dept1.getDeptId()))
                            {
                                dept = dept1;
                                break;
                            }
                        }
                        else
                        {
                            if (deptId.equals(dept1.getSourceId()))
                            {
                                dept = dept1;
                                break;
                            }
                        }
                    }

                    if (dept == null)
                        return null;
                }

                if (dept != null)
                {
                    RequestContext context = RequestContext.getContext();

                    if (userOnlineInfo != null && (!userOnlineInfo.getUserId().equals(user.getUserId()) ||
                            !userOnlineInfo.getDeptId().equals(dept.getDeptId())))
                    {
                        userOnlineInfo = null;

                        LoginPage.logout(context.getRequest(), context.getResponse(), true);
                    }

                    if (userOnlineInfo == null)
                    {
                        Login login = new Login();
                        login.setUserId(user.getUserId());
                        login.setDeptId(dept.getDeptId());
                        login.setLoginTime(new Date());

                        dao.add(login);

                        context.setCookie(UserOnlineInfo.COOKIE_LOGINID, login.getLoginId(), true);

                        if (index != null)
                            context.setCookie("index.page", index);
                    }
                }
            }
        }

        return "/platform/login/sso.ptl";
    }

    @Service(url = "/sso")
    @Json
    public SSOResult sso() throws Exception
    {
        SSOResult result = new SSOResult();

        if (StringUtils.isEmpty(userId) && uid == null)
        {
            result.setErrorCode("00");
        }
        else
        {
            User user;
            if (uid != null)
                user = dao.getUser(uid);
            else
                user = dao.getUserBySourceId(userId);

            if (user == null)
            {
                result.setErrorCode("01");
            }
            else
            {
                Dept dept = null;
                if (StringUtils.isEmpty(deptId) && did == null)
                {
                    dept = user.getDepts().get(0);
                }
                else
                {
                    for (Dept dept1 : user.getDepts())
                    {
                        if (did != null)
                        {
                            if (did.equals(dept1.getDeptId()))
                            {
                                dept = dept1;
                                break;
                            }
                        }
                        else
                        {
                            if (deptId.equals(dept1.getSourceId()))
                            {
                                dept = dept1;
                                break;
                            }
                        }
                    }

                    if (dept == null)
                        result.setErrorCode("02");
                }

                if (dept != null)
                {
                    Login login = new Login();
                    login.setUserId(user.getUserId());
                    login.setDeptId(dept.getDeptId());
                    login.setLoginTime(new Date());

                    dao.add(login);

                    result.setLoginId(login.getLoginId());
                }
            }
        }

        return result;
    }

    /**
     * 根据用户ID或者证书ID得到用户的验证码
     *
     * @param id 用户id或者证书id
     * @return 用户的在线验证码
     */
    @Service(url = "/sso/check")
    @PlainText
    public String check(String id, String checkNumber) throws Exception
    {
        Integer userId = null;

        try
        {
            userId = Integer.valueOf(id);
        }
        catch (NumberFormatException ex)
        {
            //不是整数，传入的id不是用户id，跳过
        }

        User user = null;

        if (userId != null)
        {
            user = dao.getUser(userId);
        }

        if (user == null)
        {
            //id不是用户id，尝试作为证书id
            user = dao.getUserByCert(id, null);
        }

        if (user != null)
        {
            try
            {
                return Boolean.toString(userCheckService.check(userId, checkNumber));
            }
            catch (Exception ex)
            {
                Tools.log(ex);
                //在下面统一返回false
            }
        }

        return "false";
    }

    @Service(url = "/sso/turn")
    @Redirect
    public String turn(String url) throws Exception
    {
        url = url + "?uid=" + userOnlineInfo.getUserId() + "&did=" + userOnlineInfo.getDeptId() + "&checkNumber=" +
                userCheckService.getCheckNumber(userOnlineInfo.getUserId()) + "&server=" + server;

        if (!StringUtils.isEmpty(path))
            url += "&path=" + path;

        if (!StringUtils.isEmpty(menuId))
            url += "&menuId=" + menuId;

        return url;
    }
}
