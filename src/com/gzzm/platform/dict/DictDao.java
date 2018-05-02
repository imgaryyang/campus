package com.gzzm.platform.dict;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 字典相关的dao
 *
 * @author camel
 * @date 13-3-26
 */
public abstract class DictDao extends GeneralDao
{
    public DictDao()
    {
    }

    public Dict getDict(Integer dictId) throws Exception
    {
        return load(Dict.class, dictId);
    }

    public Dict getDict(String dictCode) throws Exception
    {
        Dict dict = getDictByCode(dictCode);

        if (dict == null)
        {
            dict = new Dict();
            dict.setDictCode(dictCode);
            dict.setDictName(dictCode);

            add(dict);
        }

        return dict;
    }

    @GetByField("dictCode")
    protected abstract Dict getDictByCode(String dictCode) throws Exception;

    @GetByField("dictName")
    public abstract Dict getDictByName(String name) throws Exception;

    public DictItem getRootItem(Integer dictId) throws Exception
    {
        DictItem root = queryRootItem(dictId);

        if (root == null)
        {
            root = new DictItem();
            root.setDictId(dictId);
            root.setItemName("根节点");
            root.setItemCode("");
            root.setOrderId(0);

            add(root);
        }

        return root;
    }

    public DictItem getRootItem(String dictCode) throws Exception
    {
        return getRootItem(getDict(dictCode).getDictId());
    }

    @OQL("select i from DictItem where i.dict.dictCode=:1 and i.itemCode=:2")
    public abstract DictItem getItemByCode(String dictCode, String itemCode) throws Exception;

    @OQL("select i from DictItem where i.dictId=:1 and i.itemCode=:2")
    public abstract DictItem getItemByCode(Integer dictId, String itemCode) throws Exception;

    @OQL("select i from DictItem i where i.dictId=:1 and i.parentItemId is null")
    protected abstract DictItem queryRootItem(Integer dictId) throws Exception;

    @OQL("select i from DictItem i where dictId=:1 order by orderId")
    public abstract List<DictItem> getItemsByDictId(Integer dictId) throws Exception;

    @OQL("select i from DictItem i where i.dict.dictCode=:1 order by orderId")
    public abstract List<DictItem> getItemsByDictCode(Integer dictCode) throws Exception;
}
