package com.gzzm.platform.dict;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.importers.CrudEntityImportor;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

/**
 * 列表结构的字典项维护
 *
 * @author camel
 * @date 13-3-26
 */
@Service(url = "/dict/item/list")
public class DictItemListCrud extends BaseNormalCrud<DictItem, Integer>
{
    @Inject
    private DictDao dao;

    /**
     * 字典编码
     */
    private String dictCode;

    private Integer dictId;

    private Dict dict;

    @Like
    private String itemName;

    @Like
    private String itemCode;

    public DictItemListCrud()
    {
        setLog(true);
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

    protected Dict getDict() throws Exception
    {
        if (dict == null)
        {
            if (dictId != null)
                dict = dao.getDict(dictId);
            else if (dictCode != null)
                dict = dao.getDict(dictCode);
        }

        return dict;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getItemCode()
    {
        return itemCode;
    }

    public void setItemCode(String itemCode)
    {
        this.itemCode = itemCode;
    }

    protected String getLevelSpace(int level)
    {
        return "";
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return new String[]{"dictId"};
    }

    @Override
    public void initEntity(DictItem entity) throws Exception
    {
        super.initEntity(entity);

        entity.setDictId(getDictId());
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        Dict dict = getDict();

        if (dict.getRequireCode() != null && dict.getRequireCode())
            view.addComponent(dict.getDictName() + "编码", "itemCode");

        view.addComponent(dict.getDictName() + "名称", "itemName");

        if (dict.getRequireCode() != null && dict.getRequireCode())
            view.addColumn(dict.getDictName() + "编码", "itemCode").setWidth("200");

        view.addColumn(dict.getDictName() + "名称", "itemName").setWidth("300");
        view.addColumn("备注", "remark");

        view.defaultInit();
        view.addButton(Buttons.sort());

        view.addButton(Buttons.export("xls"));
        view.addButton(Buttons.imp());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        Dict dict = getDict();

        if (dict.getRequireCode() != null && dict.getRequireCode())
            view.addComponent(dict.getDictName() + "编码", "itemCode");

        view.addComponent(dict.getDictName() + "名称", "itemName");
        view.addComponent("备注", new CTextArea("remark"));

        view.addDefaultButtons();

        return view;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        ExportParameters exportParameters = super.getExportParameters();

        exportParameters.setFileName(getDict().getDictName() + "列表");

        return exportParameters;
    }

    @Override
    protected void initImportor(CrudEntityImportor<DictItem, Integer> importor) throws Exception
    {
        super.initImportor(importor);

        Dict dict = getDict();
        importor.addColumnMap(dict.getDictName() + "编码", "itemCode");
        importor.addColumnMap(dict.getDictName() + "名称", "itemName");
        importor.addColumnMap("备注", "remark");
    }
}
