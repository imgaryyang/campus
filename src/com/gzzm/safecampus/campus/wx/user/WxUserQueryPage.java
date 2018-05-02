package com.gzzm.safecampus.campus.wx.user;

import com.gzzm.platform.commons.crud.BaseQueryCrud;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.safecampus.wx.user.WxUser;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * 微信用户管理
 *
 * @author Neo
 * @date 2018/4/3 10:08
 */
@Service(url = "/campus/wx/user/wxuser")
public class WxUserQueryPage extends BaseQueryCrud<WxUser, Integer>
{
    /**
     * 微信名称查询条件
     */
    @Like
    private String userName;

    /**
     * 昵称查询条件
     */
    @Like
    private String nickName;

    public WxUserQueryPage()
    {
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

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);
        view.addComponent("用户名", "userName");
        view.addComponent("昵称", "nickName");
        view.addColumn("用户名", "userName").setWidth("130px");
        view.addColumn("昵称", "nickName").setWidth("180px");
        view.addColumn("性别", "sex").setWidth("70px");
        view.addColumn("手机号码", "phone").setWidth("130px");
        view.addColumn("微信号", "openId");
        view.addColumn("关注时间", "subscribeTime");
        view.addColumn("认证时间", "authTime");
        view.addColumn("认证身份", "identifyType");
        view.defaultInit(false);
        return view;
    }
}
