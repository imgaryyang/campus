package com.gzzm.safecampus.wx.user;

import com.gzzm.platform.commons.Sex;
import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.Provider;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Date;

/**
 * 微信用户在线信息
 *
 * @author Neo
 * @date 2018/3/21 15:44
 */
public final class WxUserOnlineInfo implements Serializable
{
    @Inject
    private static Provider<WxUserOnlineList> wxUserOnlineListProvider;

    /**
     * UserOnlineInfo存放在session中的属性名
     */
    static final String SESSIONNAME_ID = "wxuserOnlineInfo_id";

    public static final String SESSIONNAME = "wxuserOnlineInfo";

    public static final String COOKIE_LOGINID = "wxloginId";

    /**
     * 微信用户Id
     */
    private Integer userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别
     */
    private Sex sex;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 微信openId
     */
    private String openId;

    /**
     * 家属Id identifyType为GUARDIAN时有效
     */
    private Integer identifyId;

    /**
     * identifyType为TEACHER时有效,表示教师所在学校
     */
    private Integer deptId;

    /**
     * 认证身份
     */
    private IdentifyType identifyType;

    private String idCard;

    /**
     * id，用来标识在线信息的唯一性
     */
    private String id;

    /**
     * 登录时间
     */
    private Date loginTime;

    private Long lastTime;

    /**
     * 头像链接
     */
    private String imgUrl;

    public WxUserOnlineInfo()
    {
        this.id = Tools.getUUID();
    }

    public WxUserOnlineInfo(WxUser wxUser)
    {
        this.id = Tools.getUUID();
        this.lastTime = System.currentTimeMillis();
        this.loginTime = new Date();
        this.identifyType = wxUser.getIdentifyType();
        this.userId = wxUser.getWxUserId();
        this.identifyId = wxUser.getIdentifyId();
        this.deptId = wxUser.getDeptId();
        this.openId = wxUser.getOpenId();
        this.nickName = wxUser.getNickName();
        this.phone = wxUser.getPhone();
        this.userName = wxUser.getUserName();
        this.idCard = wxUser.getIdCard();
        this.imgUrl =wxUser.getImgUrl();
    }

    /**
     * 用Inject声明此方法为对象创建方法
     *
     * @param request httprequest
     * @return 从httprequest中获取WxUserOnlineInfo
     */
    @Inject
    public static WxUserOnlineInfo getWxUserOnlineInfo(HttpServletRequest request)
    {
        if (request == null)
            return null;

        return getWxUserOnlineInfo(request.getSession(false));
    }

    public static WxUserOnlineInfo getWxUserOnlineInfo(HttpSession session)
    {
        if (session == null)
            return null;

        String id = (String) session.getAttribute(SESSIONNAME_ID);
        if (StringUtils.isEmpty(id))
            return null;

        return wxUserOnlineListProvider.get().get(id);
    }

    public void login(HttpSession session)
    {
        //添加到在线列表中
        wxUserOnlineListProvider.get().put(this);
        session.setAttribute(SESSIONNAME_ID, id);
        session.setAttribute(SESSIONNAME, this);
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public Sex getSex()
    {
        return sex;
    }

    public void setSex(Sex sex)
    {
        this.sex = sex;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getOpenId()
    {
        return openId;
    }

    public void setOpenId(String openId)
    {
        this.openId = openId;
    }

    public Integer getIdentifyId()
    {
        return identifyId;
    }

    public void setIdentifyId(Integer identifyId)
    {
        this.identifyId = identifyId;
    }

    public IdentifyType getIdentifyType()
    {
        return identifyType;
    }

    public void setIdentifyType(IdentifyType identifyType)
    {
        this.identifyType = identifyType;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getIdCard()
    {
        return idCard;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setLoginTime(Date loginTime)
    {
        this.loginTime = loginTime;
    }

    public Date getLoginTime()
    {
        return loginTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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
}
