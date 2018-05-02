package com.gzzm.im;

import com.gzzm.im.entitys.*;
import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.message.comet.CometService;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.util.JoinType;
import net.cyan.crud.view.FieldCell;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 公共用户即时通讯
 *
 * @author zy
 * @date 2017/1/15 20:08
 */
@Service(url = "/im/AllUserConfigCrud")
public class AllUserConfigCrud extends DeptOwnedNormalCrud<ImUserConfig, Integer>
{

    @Inject
    private CometService cometService;

    @Inject
    private ImDao dao;

    private Integer deptId;

    @UserId
    private Integer userId;

    /**
     * 用户名查询条件
     */
    @Like("user.userName")
    private String userName;

    /**
     * 是否包括下级部门的用户
     */
    private boolean all;

    private boolean panel;

    /**
     * 拥有权限的部门列表，通过setAuthDeptIds注入，以方便子类覆盖注入方式
     */
    @NotSerialized
    private Collection<Integer> authDeptIds;

    public AllUserConfigCrud()
    {
        join("UserDept", "ud", "ud.deptId=:deptId and ud.userId=u.userId", JoinType.left);
        addOrderBy("ud.orderId");
    }

    public String getOrderField()
    {
        //默认认为此类对象的crud维护支持排序，并约定排序字段为orderId
        return null;
    }


    public Integer getDeptId()
    {
        if (deptId == null)
        {
            Dept root;
            try
            {
                root = dao.getRootDept();
                if (root == null)
                {
                    root = dao.getDeptById(0);
                    if (root == null)
                    {
                        root = dao.getDeptById(1);
                    }
                }
            }
            catch (Exception e)
            {
                throw new NoErrorException("无部门ID！");
            }

            deptId = root != null ? root.getDeptId() : 1;
        }
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public boolean isAll()
    {
        return all;
    }

    public void setAll(boolean all)
    {
        this.all = all;
    }


    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    @AuthDeptIds
    protected void setAuthDeptIds(Collection<Integer> authDeptIds)
    {
        this.authDeptIds = authDeptIds;
    }

    @Override
    protected String getUserIdField() throws Exception
    {
        return "u.userId";
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        if (all)
        {
            return "u.userId in (select d.userId from UserDept d " +
                    "join Dept d1 on d.dept.leftValue>=d1.leftValue and d.dept.leftValue<d1.rightValue " +
                    "where d1.deptId=:deptId and d.dept.state=0)";
        }
        else
        {
            return "u.userId in (select d.userId from UserDept d where d.deptId=:deptId)";
        }
    }

    public boolean isPanel()
    {
        return panel;
    }

    public void setPanel(boolean panel)
    {
        this.panel = panel;
    }

    @Override
    public String getAlias()
    {
        return "u";
    }

    @Override
    protected Object createListView() throws Exception
    {

        if (!panel)
        {
            PageTableView view = new ComplexTableView(new AuthDeptDisplay(), "deptId", true);
            view.setTitle("全部联系人");
            view.addComponent("姓名", "userName");
            view.addComponent("包括下属部门", new CCombox("all").setNullable(false));
            view.addColumn("头像", new FieldCell("userId")
            {
                @Override
                public String display(Object entity) throws Exception
                {
                    ImUserConfig imUserConfig = (ImUserConfig) entity;
                    return "<div style=\"background-color:#d9d9d9;height:64px;width:64px;\">" +
                            "<image src=\"/im/user/" + imUserConfig.getUserId() + "/head/" +
                            (cometService.isOnline(imUserConfig.getUserId()) ? "on" : "off") +
                            "\" style=\"width:64px;height:64px;\"/>" +
                            "</div>";
                }

            }).setLocked(true).setWidth("74px");
            view.addColumn("姓名", "user.userName").setWidth("100");
            view.addColumn("性别", "user.sex").setWidth("60");
            view.addColumn("单位", "user.allSimpleDeptName()").setWidth("250");
            view.addColumn("手机号码", "user.phone").setWidth("80");
            view.addColumn("签名", "signature");
            view.addColumn("操作", new CButton("发送消息", "getImUserInfo(${userId})")).setWidth("80");
            view.importJs("/im/alluser_config.js");
            view.addButton(Buttons.query());
            return view;
        }
        else
        {
            PagePanelView view;

            if (getAuthDeptIds() == null || getAuthDeptIds().size() > 0)
                view = new ComplexPanelView(new AuthDeptDisplay(), "deptId");
            else
                view = new PagePanelView();
            view.addComponent("姓名", "userName");
            view.addComponent("包括下属部门", new CCombox("all").setNullable(false));
            view.addButton(Buttons.query());
            view.importJs("/im/alluser_show.js");
            view.importJs("/im/alluser_config.js");
            view.importCss("/im/alluser_show.css");

//            view.setPage("double");
            return view;

        }


    }

    @Service
    @ObjectResult
    public ImUserInfo getImUserInfo(Integer friendUserId) throws Exception
    {
        Friend friend = dao.getFriend(userId, friendUserId);
        ImUserConfig userConfig;
        User user;
        if (friend == null)
        {
            userConfig = dao.load(ImUserConfig.class, friendUserId);
            user = userConfig.getUser();
        }
        else
        {
            user = friend.getUser();
            userConfig = friend.getConfig();
        }

        ImUserInfo friendInfo = new ImUserInfo(friendUserId, user.getUserName(),
                user.allSimpleDeptName());
        friendInfo.setSignature(userConfig.getSignature());
        friendInfo.setPhone(user.getPhone());
        friendInfo.setOfficePhone(user.getOfficePhone());
        friendInfo.setSex(DataConvert.toString(user.getSex()));
        friendInfo.setDuty(DataConvert.toString(user.getDuty()));
        friendInfo.setMyFriend(friend != null);
        if (!StringUtils.isEmpty(user.getPhone()))
        {
            //由原来的需要用户确定绑定手机改成只要有手机就可以发短信
            friendInfo.setPhoneBound(true);
        }
        return friendInfo;
    }

    @Service(url = "/im/AllUserConfigCrud/{$0}/photo")
    public byte[] getPhoto(Integer userId) throws Exception
    {
        if (cometService.isOnline(userId))
        {
            if (getEntity(userId).getHeadImg() != null)
            {
                return getEntity(userId).getHeadImg();
            }
            else
            {
                return getOnPhoto(userId, "on");
            }
        }
        else
        {
            if (getEntity(userId).getOffHeadImg() != null)
            {
                return getEntity(userId).getOffHeadImg();
            }
            else
            {
                return getOnPhoto(userId, "off");
            }
        }
    }

    /**
     * 返回没默认头像
     *
     * @param userId
     * @param state
     * @return
     * @throws Exception
     */
    byte[] getOnPhoto(Integer userId, String state) throws Exception
    {
        ImUserConfig config = getEntity(userId);
        Sex sex = config.getUser().getSex();
        return IOUtils.fileToBytes(Tools.getAppPath(sex == Sex.female ?
                "im/images/" + state + "line_female.jpg" : "im/images/" + state + "line_male.jpg"));
    }

    @Service
    @ObjectResult
    public String getDeptStr(Integer userId) throws Exception
    {
        User user = dao.get(User.class, userId);
        List<Dept> deptList = user.getDepts();
        String name = "";
        for (Dept dept : deptList)
        {
            name = StringUtils.concat(name, dept.getDeptName(), ",");
        }
        return name;
    }

}
