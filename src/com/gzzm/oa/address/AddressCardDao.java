package com.gzzm.oa.address;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.*;


/**
 * 通讯录操作类
 *
 * @author whf
 * @date 2010-3-15
 */
public abstract class AddressCardDao extends GeneralDao
{
    //此处删除userId的注入，不要在非Page类中注入session和request相关信息，ccs

    public AddressCardDao()
    {
    }

    public AddressCard getAddressCard(Integer cardId) throws Exception
    {
        //通过主键获得对象无须使用@OQL标签，ccs
        return load(AddressCard.class, cardId);
    }

    /**
     * 获得通讯录组
     *
     * @param groupId groupId
     * @return AddressGroup对象
     * @throws Exception 数据库异常
     */
    public AddressGroup getAddressGroup(Integer groupId) throws Exception
    {
        //通过主键获得对象无须使用@OQL标签，ccs
        return load(AddressGroup.class, groupId);
    }

    /**
     * 获得通讯组列表
     *
     * @param type  拥有者类型
     * @param owner 通讯录拥有者
     * @return 通讯组列表
     * @throws Exception 数据库异常
     */
    @OQL("select g from AddressGroup g where type=:1 and owner=:2 order by orderId")
    public abstract List<AddressGroup> getGroupsByOwner(AddressType type, Integer owner) throws Exception;

    /**
     * 获得通讯组列表
     *
     * @param deptIds 通讯录所属的部门
     * @return 通讯组列表
     * @throws Exception 数据库异常
     */
    @OQL("select g from AddressGroup g join Dept d on g.owner=d.deptId where " +
            "type=1 and owner in :1 order by d.leftValue,g.orderId")
    public abstract List<AddressGroup> getGroupsByDeptIds(Collection<Integer> deptIds) throws Exception;

    /**
     * 查询所有联系人列表（导出用
     *
     * @param type  拥有者类型
     * @param owner 通讯录拥有者
     * @return 联系人列表
     * @throws Exception 数据库
     */
    @OQL("select c from AddressCard c where type=:1 and owner=:2 order by cardName")
    public abstract List<AddressCard> getCardsByOwner(AddressType type, Integer owner) throws Exception;

    /**
     * 查询所有联系人列表（导出用
     *
     * @param userId  用户Id
     * @param deptIds 部门ID列表
     * @return 联系人列表
     * @throws Exception 数据库
     */
    @OQL("select c from AddressCard c where type=0 and owner=:1 or type=1 and owner in :2 order by cardName")
    public abstract List<AddressCard> getCardsByUserAndDepts(Integer userId, Collection<Integer> deptIds)
            throws Exception;

    /**
     * 获得某个组里的联系人
     *
     * @param groupId 联系人组
     * @return 此组里的联系人列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select c from AddressCard c where exists g in c.groups : g.groupId=:1 order by cardName")
    public abstract List<AddressCard> getCardsInGroup(Integer groupId) throws Exception;

    /**
     * 根据名称或给定属性查询匹配的匹配联系人，只要姓名，昵称，简拼全拼或给定属性和输入的字符串匹配，则返回此联系人
     *
     * @param type  联系人类型，个人联系人或单位联系人
     * @param owner 联系人拥有者，用户ID或部门ID
     * @param field 给定的属性，如email,mobilePhone等，当此属性的值与给定的字符串匹配时，返回联系人
     * @param s     输入的字符串，当联系人的姓名，昵称，简拼全拼或给定属性与此字符串匹配时返回
     * @return 匹配的联系人列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select c from AddressCard c where type=:1 and owner=:2 and " +
            "(cardName like '%'||:4||'%' or nick like '%'||:4||'%' or " +
            "simpleSpell like :4||'%' or completeSpell like :4||'%'" +
            " or attributes[:3] like :4||'%') order by cardName limit :5")
    public abstract List<AddressCard> queryCardsByNameOrField(AddressType type, Integer owner, String field, String s,
                                                              int max) throws Exception;


    /**
     * 统计通讯录个数
     *
     * @param type  拥有者类型
     * @param owner 通讯录拥有者
     * @return 成员个数
     * @throws Exception 数据库异常
     */
    @OQL("select count(*) from AddressCard c where type=:1 and owner=:2")
    public abstract int getCardsCount(AddressType type, Integer owner) throws Exception;

    /**
     * 删除一个联系人组中的所有成员
     *
     * @param groupId 组ID
     * @throws Exception 数据库错误
     */
    public void deleteCardsInGroup(Integer groupId) throws Exception
    {
        get(AddressGroup.class, groupId).getCards().clear();
    }

    /**
     * 复制联系人，因为是复制多个联系人，将方法名改成复数
     *
     * @param cardIds cardIds
     * @param groupId groupId
     * @throws Exception 异常
     */
    public void copyCardstoGroup(Integer[] cardIds, Integer groupId) throws Exception
    {
        Set<AddressCard> cards = get(AddressGroup.class, groupId).getCards();
        for (Integer cardId : cardIds)
            cards.add(new AddressCard(cardId));
    }

    /**
     * 移动联系人，因为是复制多个联系人，将方法名改成复数
     *
     * @param cardIds    cardIds
     * @param groupId    新的组ID
     * @param oldGroupId 旧的组ID
     * @throws Exception 数据库异常
     */
    public void moveCardstoGroup(Integer[] cardIds, Integer groupId, Integer oldGroupId) throws Exception
    {
        Set<AddressCard> cards = get(AddressGroup.class, groupId).getCards();
        Set<AddressCard> oldCards = null;
        if (oldGroupId != null && oldGroupId > 0)
            oldCards = get(AddressGroup.class, oldGroupId).getCards();
        for (Integer cardId : cardIds)
        {
            AddressCard card = new AddressCard(cardId);
            if (oldCards != null)
                oldCards.remove(card);
            cards.add(card);
        }
    }

    /**
     * 复制组成员
     *
     * @param newGroupId groupId
     * @param oldGroupId oldGroupId
     * @throws Exception 数据库异常
     */
    public void copyGroupToGroup(Integer oldGroupId, Integer newGroupId) throws Exception
    {
        Set<AddressCard> cards = get(AddressGroup.class, newGroupId).getCards();
        Set<AddressCard> oldCards = get(AddressGroup.class, oldGroupId).getCards();

        cards.addAll(oldCards);
    }

    /**
     * 移动组成员
     *
     * @param newGroupId groupId
     * @param oldGroupId oldGroupId
     * @throws Exception 数据库异常
     */
    public void moveGroupToGroup(Integer oldGroupId, Integer newGroupId) throws Exception
    {
        Set<AddressCard> cards = get(AddressGroup.class, newGroupId).getCards();
        Set<AddressCard> oldCards = get(AddressGroup.class, oldGroupId).getCards();

        cards.addAll(oldCards);
        oldCards.clear();
    }

    /**
     * 获得某个名称的联系人，用于联系人重复性提示
     *
     * @param name  联系人名称
     * @param type  类型
     * @param owner 拥有者
     * @return 使用此名称的联系人
     * @throws Exception 数据库操作异常
     */
    @OQL("select c from AddressCard c where cardName=:1 and type=:2 and owner=:3")
    public abstract List<AddressCard> getCardsWithName(String name, AddressType type, Integer owner) throws Exception;
}
