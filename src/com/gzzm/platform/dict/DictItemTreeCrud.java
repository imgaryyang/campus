package com.gzzm.platform.dict;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.NotCondition;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.importers.*;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

/**
 * 树形结构的字典项维护
 *
 * @author camel
 * @date 13-3-26
 */
@Service(url = "/dict/item/tree")
public class DictItemTreeCrud extends BaseTreeCrud<DictItem, Integer>
{
    @Inject
    private DictDao dao;

    /**
     * 字典编码
     */
    private String dictCode;

    @NotCondition
    private Integer dictId;

    private Dict dict;

    private boolean codeIndent;

    public DictItemTreeCrud()
    {
    }

    public boolean isCodeIndent()
    {
        return codeIndent;
    }

    public void setCodeIndent(boolean codeIndent)
    {
        this.codeIndent = codeIndent;
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

    @Override
    public DictItem getRoot() throws Exception
    {
        return dao.getRootItem(getDictId());
    }

    @Override
    public String getOrderField()
    {
        return "orderId";
    }

    @Override
    public void initEntity(DictItem entity) throws Exception
    {
        super.initEntity(entity);

        entity.setDictId(getDictId());
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        if ("exp".equals(getAction()))
        {
            PageTreeTableView view = new PageTreeTableView();

            Dict dict = getDict();

            if (dict.getRequireCode() != null && dict.getRequireCode())
                view.addColumn(dict.getDictName() + "编码", "itemCode").setWidth("200");

            view.addColumn(dict.getDictName() + "名称", "itemName").setWidth("200");
            view.addColumn("备注", "remark").setWidth("400");

            return view;
        }
        else
        {
            PageTreeView view = new PageTreeView();

            view.defaultInit();

            view.addButton(Buttons.export("xls"));
            view.addButton(Buttons.imp());

            return view;
        }
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

        if (dict.getRequireCode() != null && dict.getRequireCode())
            importor.addColumnMap(dict.getDictName() + "编码", "itemCode");

        importor.addColumnMap(dict.getDictName() + "名称", "itemName");
        importor.addColumnMap("备注", "remark");

        if (codeIndent)
        {
            importor.setTreeParser(new CodeTreeImportParser<DictItem, Integer>("itemCode")
            {
                @Override
                protected String getCondition() throws Exception
                {
                    return "dictId=:dictId";
                }
            });
        }
    }

    @Override
    public void setString(DictItem node, String s) throws Exception
    {
        node.setItemCode(s);
    }

    @Override
    public String toString(DictItem node) throws Exception
    {
        if ("exp".equals(getAction()) || "imp".equals(getAction()))
            return node.getItemCode();
        else
            return node.getItemName();
    }

    @Override
    protected String getLevelSpace(int level)
    {
        if (codeIndent)
            return "";
        else
            return super.getLevelSpace(level);
    }
}
