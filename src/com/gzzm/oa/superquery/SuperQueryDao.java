package com.gzzm.oa.superquery;

import com.gzzm.oa.mail.Mail;
import com.gzzm.platform.organ.User;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author xzb
 * @date 2017-8-8
 */
public abstract class SuperQueryDao extends GeneralDao
{
    //邮件过滤条件
    final String mailOql="m.type=2 and m.userId=?3 and m.catalogId is null and (contains(m.body.text,?1) or m.title like ?2) and m.deleted=0";

    public SuperQueryDao()
    {
    }

    /**
     * 获得菜单
     *
     * @param menuIds 菜单id
     * @return url
     * @throws Exception 数据库异常
     */
    @OQL("select m.menuId,m.menuTitle,m.url from Menu m where valid=1 and m.menuId in :1 " +
            "and m.menuTitle like ?2 and m.url is not null order by m.orderId")
    public abstract List<Map<String, String>> getMenus(Collection menuIds, String menuTitle) throws Exception;

    @OQL("select u.u from (select u, min(select leftValue from u.depts d where d.state=0) as leftValue from User u where " +
            "(userName like '%'||?2||'%' or spell like ?2||'%' or simpleSpell like ?2||'%' or loginName like ?2||'%') and " +
            " state=0 and userId in (select d.userId from UserDept d where d.deptId in ?1)) u order by u.leftValue" +
            ",first(select d.orderId from UserDept d where d.userId=u.u.userId and d.dept.leftValue=u.leftValue and d.dept.state=0)")
    public abstract List<User> getUsers(Collection authDeptId,String filter) throws Exception;

    /**
     * 获得邮件
     *
     * @param filter
     * @throws Exception 数据库异常
     */
    @OQL("select m from com.gzzm.oa.mail.Mail m where "+mailOql+" order by m.acceptTime desc ")
    public abstract List<Mail> getMails(String condition,String filter,Integer userId) throws Exception;

    /**
     * 获得邮件总数
     *
     * @param filter
     * @throws Exception 数据库异常
     */
    @OQL("select count(m) from com.gzzm.oa.mail.Mail m where "+mailOql)
    public abstract Integer getMailsCount(String condition,String filter, Integer userId) throws Exception;

    /**
     * 获得菜单总数
     *
     * @param menuIds 菜单id
     * @return url
     * @throws Exception 数据库异常
     */
    @OQL("select count(m) from Menu m where valid=1 and m.menuId in :1 " +
            "and m.menuTitle like ?2 and m.url is not null")
    public abstract Integer getMenusCount(Collection menuIds, String menuTitle) throws Exception;

    @OQL("select count(u.u) from (select u, min(select leftValue from u.depts d where d.state=0) as leftValue from User u where " +
            "(userName like '%'||?2||'%' or spell like ?2||'%' or simpleSpell like ?2||'%' or loginName like ?2||'%') and " +
            " state=0 and userId in (select d.userId from UserDept d where d.deptId in ?1)) u")
    public abstract Integer getUsersCount(Collection authDeptId,String filter) throws Exception;
}
