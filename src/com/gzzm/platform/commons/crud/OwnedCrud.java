package com.gzzm.platform.commons.crud;

import net.cyan.crud.EntityCrud;

/**
 * 可编辑的OwnedDisplayCrud
 *
 * @author camel
 * @date 2010-3-4
 */
public interface OwnedCrud<E, K, OK> extends EntityCrud<E, K>
{
    public String getOwnerField();

    public OK getOwnerKey(E entity) throws Exception;

    public void setOwnerKey(E entity, OK ownerKey) throws Exception;

    /**
     * 移动数据
     *
     * @param key         移动的记录的主键
     * @param newOwnerKey    拥有者的主键
     * @param oldOwnerKey 旧的拥有者的主键,可以为空
     * @throws Exception 异常
     */
    public void moveTo(K key, OK newOwnerKey, OK oldOwnerKey) throws Exception;

    /**
     * 移动多条数据
     *
     * @param keys        移动的记录的主键
     * @param newOwnerKey    拥有者的主键
     * @param oldOwnerKey 旧的拥有者的主键,可以为空
     * @throws Exception 异常
     */
    public void moveAllTo(K[] keys, OK newOwnerKey, OK oldOwnerKey) throws Exception;

    /**
     * 复制数据
     *
     * @param key         复制记录的主键
     * @param newOwnerKey    拥有者的主键
     * @param oldOwnerKey 旧的拥有者的主键,可以为空
     * @throws Exception 异常
     */
    public void copyTo(K key, OK newOwnerKey, OK oldOwnerKey) throws Exception;

    /**
     * 复制多条数据
     *
     * @param keys        复制记录的主键
     * @param newOwnerKey    拥有者的主键
     * @param oldOwnerKey 旧的拥有者的主键,可以为空
     * @throws Exception 异常
     */
    public void copyAllTo(K[] keys, OK newOwnerKey, OK oldOwnerKey) throws Exception;
}