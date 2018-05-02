package com.gzzm.platform.dict;

import com.gzzm.platform.commons.components.EntityPageTreeModel;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.arachne.components.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.io.Serializable;
import java.util.List;

/**
 * @author camel
 * @date 13-3-26
 */
public class DictItemTreeModel extends EntityPageTreeModel<DictItem>
        implements CheckBoxTreeModel<DictItem>, SelectableModel<DictItem>, ValueTreeModel<DictItem>, Serializable
{
    private static final long serialVersionUID = -1123886245378511846L;

    @Inject
    private static Provider<DictDao> daoProvider;

    private transient DictDao dao;

    private Integer dictId;

    private String dictCode;

    private transient Dict dict;

    private DictValueType valueType = DictValueType.ID;

    private boolean showBox;

    private boolean leafSelectOnly;

    public DictItemTreeModel()
    {
    }

    protected DictDao getDao()
    {
        if (dao == null)
            dao = daoProvider.get();
        return dao;
    }

    public Integer getDictId() throws Exception
    {
        if (dictId == null && dictCode != null)
            dictId = getDict().getDictId();

        return dictId;
    }

    public void setDictId(Integer dictId)
    {
        this.dictId = dictId;
    }

    public String getDictCode()
    {
        return dictCode;
    }

    public void setDictCode(String dictCode)
    {
        this.dictCode = dictCode;
    }

    public DictValueType getValueType()
    {
        return valueType;
    }

    public void setValueType(DictValueType valueType)
    {
        this.valueType = valueType;
    }

    public boolean isShowBox()
    {
        return showBox;
    }

    public void setShowBox(boolean showBox)
    {
        this.showBox = showBox;
    }

    public boolean isLeafSelectOnly()
    {
        return leafSelectOnly;
    }

    public void setLeafSelectOnly(boolean leafSelectOnly)
    {
        this.leafSelectOnly = leafSelectOnly;
    }

    protected Dict getDict() throws Exception
    {
        if (dict == null)
        {
            if (dictId != null)
                dict = getDao().getDict(dictId);
            else if (dictCode != null)
                dict = getDao().getDict(dictCode);
        }

        return dict;
    }

    public boolean hasCheckBox(DictItem dictItem) throws Exception
    {
        return showBox;
    }

    public Boolean isChecked(DictItem dictItem) throws Exception
    {
        return false;
    }

    @Override
    protected Object getRootKey() throws Exception
    {
        return getRoot().getDictId();
    }

    @Override
    @NotSerialized
    public DictItem getRoot() throws Exception
    {
        return getDao().getRootItem(getDictId());
    }

    @NotSerialized
    public Boolean isRootVisible()
    {
        return false;
    }

    @Override
    protected String getTextField() throws Exception
    {
        return "itemName";
    }

    @Override
    protected String getSearchCondition() throws Exception
    {
        return "itemName like ?text or spell like ?text or simpleSpell like ?text1";
    }

    @Override
    public Object valueOf(DictItem dictItem) throws Exception
    {
        switch (valueType)
        {
            case ID:
                return dictItem.getItemId();
            case CODE:
                return dictItem.getItemCode();
            case NAME:
                return dictItem.getItemName();
            default:
                return dictItem.getItemId();
        }
    }

    @Override
    public boolean isSelectable(DictItem dictItem) throws Exception
    {
        if (leafSelectOnly)
        {
            List<DictItem> children = dictItem.getChildren();
            return children == null || children.size() == 0;
        }
        else
        {
            return true;
        }
    }
}
