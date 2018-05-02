package com.gzzm.platform.commons.crud;

import net.cyan.crud.*;
import net.cyan.crud.view.*;

/**
 * 本系统使用的TreeView
 *
 * @author camel
 * @date 2009-10-21
 */
public class PageTreeView extends MainPageView<BaseTreeView, PageTreeView>
        implements CheckBoxTreeView, NodePropertiedView<PageTreeView>
{
    public PageTreeView(BaseTreeView mainBody)
    {
        super(mainBody);
    }

    public PageTreeView(String checkBoxName, CheckBoxType checkBoxType)
    {
        super(new BaseTreeView(checkBoxName, checkBoxType));
    }

    public PageTreeView()
    {
        super(new BaseTreeView());
    }

    public String getCheckBoxName()
    {
        return mainBody.getCheckBoxName();
    }

    public CheckBoxType getCheckBoxType()
    {
        return mainBody.getCheckBoxType();
    }

    public boolean isCheckable(Object node) throws Exception
    {
        return mainBody.isCheckable(node);
    }

    public PageTreeView setNodeCheckable(Object checkable)
    {
        mainBody.setCheckable(checkable);
        return this;
    }

    public Boolean isChecked(Object node) throws Exception
    {
        return mainBody.isChecked(node);
    }

    public PageTreeView setChecked(Object checked)
    {
        mainBody.setChecked(checked);
        return this;
    }

    public boolean isRootVisible()
    {
        return mainBody.isRootVisible();
    }

    public PageTreeView setRootVisible(boolean rootVisible)
    {
        mainBody.setRootVisible(rootVisible);
        return this;
    }

    public boolean isMultiple() throws Exception
    {
        return mainBody.isMultiple();
    }

    public PageTreeView setMultiple(boolean multiple)
    {
        mainBody.setMultiple(multiple);
        return this;
    }

    public boolean isSingleExpand() throws Exception
    {
        return mainBody.isSingleExpand();
    }

    public PageTreeView setSingleExpand(boolean singleExpand)
    {
        mainBody.setSingleExpand(singleExpand);
        return this;
    }

    public Object getOnTreeClick() throws Exception
    {
        return mainBody.getOnTreeClick();
    }

    public PageTreeView setOnTreeClick(Object ontreeclick)
    {
        mainBody.setOnTreeClick(ontreeclick);
        return this;
    }

    public Object getOnTreeDblClick() throws Exception
    {
        return mainBody.getOnTreeDblClick();
    }

    public PageTreeView setOnTreeDblClick(Object ontreedblclick)
    {
        mainBody.setOnTreeDblClick(ontreedblclick);
        return this;
    }

    public Object getOnTreeSelect() throws Exception
    {
        return mainBody.getOnTreeSelect();
    }

    public PageTreeView setOnTreeSelect(Object ontreeselect)
    {
        mainBody.setOnTreeSelect(ontreeselect);
        return this;
    }

    public PageTreeView setHref(Object url)
    {
        mainBody.setHref(url);
        return this;
    }

    public String getHref(Object obj) throws Exception
    {
        return mainBody.getHref(obj);
    }

    public PageTreeView setTarget(Object action)
    {
        mainBody.setTarget(action);
        return this;
    }

    public Object getTarget(Object obj) throws Exception
    {
        return mainBody.getTarget(obj);
    }

    public PageTreeView setAction(Object action)
    {
        mainBody.setAction(action);
        return this;
    }

    public Object getAction(Object obj) throws Exception
    {
        mainBody.getAction(obj);
        return this;
    }

    public PageTreeView setIcon(Object icon)
    {
        mainBody.setIcon(icon);
        return this;
    }

    public Object getIcon(Object obj) throws Exception
    {
        return mainBody.getIcon(obj);
    }

    public PageTreeView setIconClass(Object name)
    {
        mainBody.setIconClass(name);
        return this;
    }

    public String getIconClass(Object obj) throws Exception
    {
        return mainBody.getIconClass(obj);
    }

    public PageTreeView setOnDblClick(Object ondblclick)
    {
        mainBody.setOnDblClick(ondblclick);
        return this;
    }

    public Object getOnDblClick(Object obj) throws Exception
    {
        return mainBody.getOnDblClick(obj);
    }

    public PageTreeView setOnClick(Object onclick)
    {
        mainBody.setOnClick(onclick);
        return this;
    }

    public Object getOnClick(Object obj) throws Exception
    {
        return mainBody.getOnClick(obj);
    }

    public PageTreeView setOnSelect(Object onselect)
    {
        mainBody.setOnSelect(onselect);
        return this;
    }

    public Object getOnSelect(Object obj) throws Exception
    {
        return mainBody.getOnSelect(obj);
    }

    public boolean isEnableDrop() throws Exception
    {
        return mainBody.isEnableDrop();
    }

    public PageTreeView setEnableDrop(boolean enableDrop)
    {
        mainBody.setEnableDrop(enableDrop);
        return this;
    }

    public boolean isEnableDrag() throws Exception
    {
        return mainBody.isEnableDrag();
    }

    public PageTreeView setEnableDrag(boolean enableDrag)
    {
        mainBody.setEnableDrag(enableDrag);
        return this;
    }

    public String getDDGroup() throws Exception
    {
        return mainBody.getDDGroup();
    }

    public PageTreeView setDDGroup(String ddGroup)
    {
        mainBody.setDDGroup(ddGroup);
        return this;
    }

    public String getDragGroup() throws Exception
    {
        return mainBody.getDragGroup();
    }

    public PageTreeView setDragGroup(String dragGroup)
    {
        mainBody.setDragGroup(dragGroup);
        return this;
    }

    public String getDropGroup() throws Exception
    {
        return mainBody.getDropGroup();
    }

    public PageTreeView setDropGroup(String dropGroup)
    {
        mainBody.setDropGroup(dropGroup);
        return this;
    }

    public Object getStartDrag() throws Exception
    {
        return mainBody.getStartDrag();
    }

    public PageTreeView setStartDrag(Object startdrag)
    {
        mainBody.setStartDrag(startdrag);
        return this;
    }

    public Object getEndDrag() throws Exception
    {
        return mainBody.getEndDrag();
    }

    public PageTreeView setEndDrag(Object endDrag)
    {
        mainBody.setEndDrag(endDrag);
        return this;
    }

    public Object getOnDragOver() throws Exception
    {
        return mainBody.getOnDragOver();
    }

    public PageTreeView setOnDragOver(Object ondragover)
    {
        mainBody.setOnDragOver(ondragover);
        return this;
    }

    public Object getOnDrop() throws Exception
    {
        return mainBody.getOnDrop();
    }

    public PageTreeView setOnDrop(Object ondrop)
    {
        mainBody.setOnDrop(ondrop);
        return this;
    }

    public boolean isSupportSearch()
    {
        return mainBody.isSupportSearch();
    }

    public PageTreeView enableSupportSearch()
    {
        mainBody.enableSupportSearch();
        return this;
    }

    public Object display(Object record) throws Exception
    {
        return mainBody.display(record);
    }

    public PageTreeView addDefaultButtons(String edtiForward)
    {
        Crud crud = CrudConfig.getContext().getCrud();
        //crud可编辑
        if (crud instanceof EntityCrud && getCheckBoxType() == CheckBoxType.none)
        {
            EntityCrud entityCrud = (EntityCrud) crud;
            addButton(Buttons.add(edtiForward));
            addButton(Buttons.edit(edtiForward));
            addButton(Buttons.duplicate(edtiForward));
            addButton(Buttons.delete());
            if (entityCrud.getOrderField() != null ||
                    ((entityCrud instanceof BaseTreeCrud) && ((BaseTreeCrud) crud).getOrganizer() != null))
            {
                addButton(Buttons.up());
                addButton(Buttons.down());
            }
        }

        return this;
    }

    public PageTreeView addDefaultButtons()
    {
        return addDefaultButtons(DEFAULTEDITFORWARD);
    }

    public PageTreeView makeEditable(String forward)
    {
        Object action = Actions.show(forward);
        setOnTreeDblClick(action);

        return this;
    }

    /**
     * 支持复制粘贴
     *
     * @return 返回本身，以支持链式编程
     */
    public PageTreeView enableCP()
    {
        if (CrudAuths.isAddable())
            run("enableCP();");

        return this;
    }

    /**
     * 支持拖放
     *
     * @return 返回本身，以支持链式编程
     */
    public PageTreeView enableDD()
    {
        if (CrudAuths.isModifiable() || CrudAuths.isAddable())
        {
            setEnableDrag(true);
            setEnableDrop(true);
            setOnDrop(Actions.move());
        }

        return this;
    }


    public PageTreeView defaultInit(String editForward)
    {
        addDefaultButtons(editForward);
        Crud crud = CrudConfig.getContext().getCrud();
        //crud可编辑
        if (crud instanceof EntityCrud && getCheckBoxType() == CheckBoxType.none)
        {
            makeEditable(editForward);

            enableCP();
            enableDD();

            if (crud instanceof SearchTreeCrud)
                enableSupportSearch();

            setMultiple(true);
        }

        return this;
    }

    public PageTreeView defaultInit()
    {
        return defaultInit(DEFAULTEDITFORWARD);
    }
}
