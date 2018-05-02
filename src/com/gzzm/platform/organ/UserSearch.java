package com.gzzm.platform.organ;

import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.FieldCell;
import net.cyan.crud.view.components.CButton;

import java.util.Collection;

/**
 * 用户搜索
 *
 * @author camel
 * @date 13-11-18
 */
@Service(url = "/UserSearch")
public class UserSearch extends BaseQueryCrud<User, Integer>
{
    private String text;

    /**
     * 用电话号码做查询条件
     */
    @Like
    private String phone;

    @NotSerialized
    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    public UserSearch()
    {
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    @Override
    protected String getQueryString() throws Exception
    {
        return "select u.u from (select u, min(select leftValue from u.depts d where d.state=0) as leftValue from User u where " +
                "(userName like '%'||?text||'%' or spell like ?text||'%' or simpleSpell like ?text||'%') and phone like ?phone and " +
                " state=0 and userId in (select d.userId from UserDept d where d.deptId in ?authDeptIds)) u order by u.leftValue" +
                ",first(select d.orderId from UserDept d where d.userId=u.u.userId and d.dept.leftValue=u.leftValue and d.dept.state=0)";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);

        view.addComponent("姓名", "text");
        view.addComponent("手机号码", "phone");

        view.addColumn("姓名", new FieldCell("userName").setOrderable(false));
        view.addColumn("所属部门", "allSimpleDeptName()").setAutoExpand(true);
        view.addColumn("岗位", "allStationName()").setWidth("150");
        view.addColumn("性别", new FieldCell("sex").setOrderable(false));
        view.addColumn("电话", new FieldCell("phone").setOrderable(false));
        view.addColumn("办公电话", new FieldCell("officePhone").setOrderable(false));

        view.addColumn("写邮件", new CButton("写邮件", "sendMail(${userId},'${userName}')"));
        view.addColumn("发送短信", new CButton("发送短信", "sendSms(${userId},'${userName}')"));
        view.addColumn("发送消息", new CButton("发送消息", "sendIm(${userId},'${userName}')"));

        view.addButton(Buttons.query());
        view.addButton(Buttons.export("xls"));

        view.importJs("/platform/organ/user_search.js");

        return view;
    }
}
