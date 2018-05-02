package com.gzzm.platform.receiver;

import java.util.List;

/**
 * 接收者提供器，提供某种接收者
 *
 * @author camel
 * @date 2010-4-28
 */
public interface ReceiverProvider
{
    /**
     * 获得接收者组列表
     *
     * @param context    上下文信息
     * @param parentGroupId 上一级的接收组，如果为null表示加载根一级的接受者组
     * @return 接收者组列表
     * @throws Exception 允许子类抛出异常
     */
    public List<ReceiverGroup> getGroups(ReceiversLoadContext context, String parentGroupId) throws Exception;

    /**
     * 获得接收者列表
     *
     * @param context 上下文信息
     * @param groupId 所属的接收者组，当此类接收者无须分组时为null
     * @return 接收者列表
     * @throws Exception 允许子类抛出异常
     */
    public List<Receiver> getReceivers(ReceiversLoadContext context, String groupId) throws Exception;

    /**
     * 根据一个字符串查询接收者，返回匹配的接收者
     *
     * @param context 上下文信息
     * @param s       输入的字符串，可以是姓名，拼音，或者简拼
     * @return 与输入字符串匹配的接收者列表
     * @throws Exception 允许子类抛出异常
     */
    public List<Receiver> queryReceivers(ReceiversLoadContext context, String s) throws Exception;

    /**
     * 根据一个字符串搜索组
     *
     * @param context 上下文信息
     * @param s       输入的字符串，可以说组的名称，拼音或者简拼
     * @return 与输入的字符串相匹配的组
     * @throws Exception 允许子类抛出异常
     */
    public List<String> queryGroup(ReceiversLoadContext context, String s) throws Exception;

    /**
     * 获得某个组的上级组，可以为kong
     *
     * @param context 上下文信息
     * @param groupId 组ID
     * @return 上级组ID
     * @throws Exception 允许子类抛出异常
     */
    public String getParentGroupId(ReceiversLoadContext context, String groupId) throws Exception;

    /**
     * 名称，如组织机构，通讯录等
     *
     * @return 名称
     */
    public String getName();

    /**
     * 组ID，如user,address等
     *
     * @return 组ID
     */
    public String getId();

    /**
     * 接收者是否分组
     *
     * @return 分组返回true，不分组返回false
     */
    public boolean isGroup();

    /**
     * 是否显示此类的接收者
     *
     * @param context 上下文信息
     * @return 如果显示此类接收者返回true，不显示返回false
     * @throws Exception 允许实现类抛出异常
     */
    public boolean accept(ReceiversLoadContext context) throws Exception;
}