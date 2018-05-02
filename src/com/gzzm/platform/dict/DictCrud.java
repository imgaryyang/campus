package com.gzzm.platform.dict;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CCheckbox;

/**
 * @author camel
 * @date 13-3-26
 */
@Service(url = "/dict/dict")
public class DictCrud extends BaseNormalCrud<Dict, Integer>
{
    @Like
    private String dictName;

    @Like
    private String dictCode;

    public DictCrud()
    {
        setLog(true);
        addOrderBy("dictName");
    }

    public String getDictName()
    {
        return dictName;
    }

    public void setDictName(String dictName)
    {
        this.dictName = dictName;
    }

    public String getDictCode()
    {
        return dictCode;
    }

    public void setDictCode(String dictCode)
    {
        this.dictCode = dictCode;
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        if (getEntity().getRequireCode() == null)
            getEntity().setRequireCode(false);

        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("字典名称", "dictName");
        view.addComponent("字典编码", "dictCode");

        view.addColumn("字典名称", "dictName");
        view.addColumn("字典编码", "dictCode");
        view.addColumn("需要编码", "requireCode");

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("字典名称", "dictName");
        view.addComponent("字典编码", "dictCode");
        view.addComponent("需要编码", new CCheckbox("requireCode"));

        view.addDefaultButtons();

        return view;
    }
}
