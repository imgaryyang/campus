package com.gzzm.platform.dict;

import com.gzzm.platform.commons.components.EntityPageListModel;
import net.cyan.arachne.annotation.Service;
import net.cyan.arachne.components.CheckBoxListModel;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 13-3-26
 */
@Service
public class DictItemListModel extends EntityPageListModel<DictItem, Integer> implements CheckBoxListModel<DictItem>
{
    @Inject
    private static Provider<DictDao> daoProvider;

    private DictDao dao;

    private Integer dictId;

    private String dictCode;

    private Dict dict;

    private DictValueType valueType = DictValueType.ID;

    private boolean showBox;

    public DictItemListModel()
    {
        addOrderBy("orderId");
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
    public String getId(DictItem dictItem) throws Exception
    {
        switch (valueType)
        {
            case ID:
                return dictItem.getItemId().toString();
            case CODE:
                return dictItem.getItemCode();
            case NAME:
                return dictItem.getItemName();
            default:
                return dictItem.getItemId().toString();
        }
    }
}
