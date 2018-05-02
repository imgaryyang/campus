package com.gzzm.platform.commons.crud;

import net.cyan.commons.util.*;
import net.cyan.commons.util.language.Language;
import net.cyan.crud.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;

import java.util.*;

/**
 * @author camel
 * @date 2009-10-21
 */
public abstract class AbstractPageTableView<V extends AbstractBaseTableView<?>, T extends Propertied>
        extends MainPageView<V, T> implements CheckboxTableView, RowPropertiedTableView<T>
{
    /**
     * 单字符宽度
     */
    static final int CHARWIDTH = 20;

    /**
     * 表头的最小宽度
     */
    static final int HEADMINWIDTH = 26;

    protected AbstractPageTableView(V body)
    {
        super(body);
    }

    static int getTextWidth(String s)
    {
        return s.length() * CHARWIDTH;
    }

    public List<Column> getColumns()
    {
        return mainBody.getColumns();
    }

    @SuppressWarnings("unchecked")
    public static <C extends Column> C initColumn(C column)
    {
        if (column instanceof BaseColumn)
        {
            return (C) initBaseColumn((BaseColumn) column);
        }
        else if (column instanceof ColumnGroup)
        {
            return (C) initColumnGroup((ColumnGroup) column);
        }

        return column;
    }

    public static BaseColumn initBaseColumn(BaseColumn column)
    {
        String title = column.getTitle();
        int width0 = 0;
        int width = 0;
        boolean locked = false;

        if (StringUtils.isEmpty(column.getWidth()))
            width0 = getTextWidth(Language.getLanguage().getWord(title, title));

        Cell cell = column.getCell();
        if (cell instanceof ComponentCell)
        {
            Component component = ((ComponentCell) cell).getComponent();

            if (component instanceof CImage)
            {
                width = 48;
                locked = true;
            }
            else if (component instanceof CButton)
            {
                width = 60;
                if (column.getLocked() == null || column.isLocked())
                    width0 += 20;
                locked = true;
            }
            else if (component instanceof CHref)
            {
                CHref href = (CHref) component;

                Object text = href.getText();

                if (text instanceof String)
                    width = getTextWidth((String) text);
            }
        }
        else if (cell instanceof EnumCell)
        {
            EnumCell enumCell = (EnumCell) cell;
            for (String s : enumCell.getEnumMap().values())
            {
                int width1 = getTextWidth(s);
                if (width1 > width)
                    width = width1;
            }
        }
        else
        {
            RecordCrud crud = (RecordCrud) CrudConfig.getContext().getCrud();
            Class type = cell.getType(crud.getRecordType());

            if (type != null)
            {
                if (type.isEnum() || AdvancedEnum.class.isAssignableFrom(type))
                {
                    for (Object e : CollectionUtils.getCollection(type))
                    {
                        int width1 = getTextWidth(DataConvert.toString(e));
                        if (width1 > width)
                            width = width1;
                    }
                }
                else if (DataConvert.isNumber(type))
                {
                    width = 65;
                }
                else if (type == boolean.class || type == Boolean.class)
                {
                    width = 35;
                }
                else if (type == java.sql.Date.class || type == java.sql.Time.class)
                {
                    width = 95;
                }
                else if (DataConvert.isDate(type))
                {
                    width = 160;
                }
            }
        }

        if (locked && column.getLocked() == null)
            column.setLocked(true);

        if (StringUtils.isEmpty(column.getWidth()) && width > 0)
        {
            if (!column.isLocked())
            {
                width0 += HEADMINWIDTH;
            }

            if (width0 > width)
                width = width0;

            column.setWidth(Integer.toString(width));
        }

        return column;
    }

    public static ColumnGroup initColumnGroup(final ColumnGroup columnGroup)
    {
        List<Column> columns = columnGroup.getColumns();
        if (columns != null && columns.size() > 0)
        {
            for (Column column : columns)
            {
                initColumn(column);
            }

            return columnGroup;
        }
        else
        {
            return new ColumnGroup(null)
            {
                @Override
                public ColumnGroup addColumn(Column column)
                {
                    columnGroup.addColumn(initColumn(column));
                    return this;
                }

                @Override
                public List<Column> getColumns()
                {
                    return columnGroup.getColumns();
                }

                @Override
                public String getTitle()
                {
                    return columnGroup.getTitle();
                }
            };
        }
    }

    /**
     * 添加一个列
     *
     * @param column 列
     * @return 返回添加的列，用于链是编程
     */
    public <C extends Column> C addColumn(C column)
    {
        return initColumn(mainBody.addColumn(column));
    }

    @SuppressWarnings("unchecked")
    public BaseColumn addColumn(String title, Cell cell)
    {
        return addColumn(new BaseColumn(title, cell));
    }

    public BaseColumn addColumn(Cell cell)
    {
        return addColumn(null, cell);
    }

    public BaseColumn addColumn(String title, String field)
    {
        return addColumn(title, new FieldCell(field));
    }

    public BaseColumn addColumn(String field)
    {
        return addColumn(null, field);
    }

    public BaseColumn addColumn(String title, String field, CellDisplayer<?> displayer)
    {
        return addColumn(title, new FieldCell(field, displayer));
    }

    public BaseColumn addColumn(String title, CellDisplayer<?> displayer)
    {
        return addColumn(title, new BaseCell(displayer));
    }

    public BaseColumn addColumn(CellDisplayer<?> displayer)
    {
        return addColumn(null, new BaseCell(displayer));
    }

    public BaseColumn addColumn(String title, Component component)
    {
        if (component == null)
            return null;

        return addColumn(new BaseColumn(title, component));
    }

    public T makeEditable()
    {
        return makeEditable(DEFAULTEDITFORWARD);
    }

    public T makeEditable(String forward)
    {
        return makeEditable(forward, null);
    }

    @SuppressWarnings("unchecked")
    public T makeEditable(String forward, String title)
    {
        addEditColumn(forward, title);
        setOnDblClick(Actions.show(forward));

        return (T) this;
    }

    public T enableShow(String forward)
    {
        return makeEditable(forward, "crud.show");
    }

    public T enableShow()
    {
        return enableShow(DEFAULTEDITFORWARD);
    }

    public BaseColumn getEditColumn(String forward, String title)
    {
        if (title == null)
        {
            title = CrudAuths.isModifiable() ? "crud.modify" : "crud.show";
        }

        return new BaseColumn(title, Buttons.editImg(forward, title));
    }

    public BaseColumn getEditColumn(String forward)
    {
        return getEditColumn(forward, null);
    }

    public BaseColumn getEditColumn()
    {
        return getEditColumn(DEFAULTEDITFORWARD, null);
    }

    public T addEditColumn()
    {
        return addEditColumn(DEFAULTEDITFORWARD, null);
    }

    public T addEditColumn(String forward)
    {
        return addEditColumn(forward, null);
    }

    @SuppressWarnings("unchecked")
    public T addEditColumn(String forward, String title)
    {
        addColumn(getEditColumn(forward, title));

        return (T) this;
    }

    public BaseColumn getDuplicateColumn(String forward, String title)
    {
        if (!CrudAuths.isModifiable())
            return null;

        if (title == null)
        {
            title = "crud.duplicate";
        }

        return new BaseColumn(title, Buttons.duplicateImg(forward, title));
    }

    public BaseColumn getDuplicateColumn(String forward)
    {
        return getDuplicateColumn(forward, null);
    }

    public BaseColumn getDuplicateColumn()
    {
        return getDuplicateColumn(DEFAULTEDITFORWARD, null);
    }

    @SuppressWarnings("unchecked")
    public T addDuplicateColumn(String forward, String title)
    {
        BaseColumn column = getDuplicateColumn(forward, title);
        if (column != null)
            addColumn(column);

        return (T) this;
    }

    public T addDuplicateColumn(String forward)
    {
        return addDuplicateColumn(forward, null);
    }

    public T addDuplicateColumn()
    {
        return addDuplicateColumn(DEFAULTEDITFORWARD, null);
    }

    @SuppressWarnings("unchecked")
    public T addDefaultButtons(String edtiForward)
    {
        Crud crud = CrudConfig.getContext().getCrud();

        if (components != null || moreComponents != null)
            addButton(crud instanceof StatCrud ? Buttons.stat() : Buttons.query());

        //crud可编辑
        if (crud instanceof EntityCrud)
        {
            addButton(Buttons.add(edtiForward));

            //前面有checkbox才能删除
            if (hasCheckbox())
                addButton(Buttons.delete());
        }

        return (T) this;
    }

    public T addDefaultButtons()
    {
        return addDefaultButtons(DEFAULTEDITFORWARD);
    }

    public T defaultInit(String editForward)
    {
        return defaultInit(editForward, true);
    }

    @SuppressWarnings("unchecked")
    public T defaultInit(String editForward, boolean duplicate)
    {
        addDefaultButtons(editForward);
        Crud crud = CrudConfig.getContext().getCrud();
        //crud可编辑
        if (crud instanceof EntityCrud)
        {
            if (CrudAuths.isModifiable())
            {
                makeEditable(editForward);

                if (duplicate)
                    addDuplicateColumn(editForward);
            }
        }

        return (T) this;
    }

    public T defaultInit()
    {
        return defaultInit(DEFAULTEDITFORWARD, true);
    }

    public T defaultInit(boolean duplicate)
    {
        return defaultInit(DEFAULTEDITFORWARD, duplicate);
    }

    public boolean isCheckable(Object entity) throws Exception
    {
        return mainBody.isCheckable(entity);
    }

    @SuppressWarnings("unchecked")
    public T setCheckable(Object checkable)
    {
        mainBody.setCheckable(checkable);
        return (T) this;
    }

    public Object getOnDblClick(Object entity) throws Exception
    {
        return mainBody.getOnDblClick(entity);
    }

    @SuppressWarnings("unchecked")
    public T setOnDblClick(Object ondblclick)
    {
        mainBody.setOnDblClick(ondblclick);
        return (T) this;
    }

    public Object getOnClick(Object entity) throws Exception
    {
        return mainBody.getOnClick(entity);
    }

    @SuppressWarnings("unchecked")
    public T setOnClick(Object ondblclick)
    {
        mainBody.setOnClick(ondblclick);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setTableOnClick(Object onclick)
    {
        mainBody.setOnDblClick(onclick);
        return (T) this;
    }

    public Object getOnTableDblClick() throws Exception
    {
        return mainBody.getOnTableDblClick();
    }

    @SuppressWarnings("unchecked")
    public T setOnTableDblClick(Object tableondblclick)
    {
        mainBody.setOnTableDblClick(tableondblclick);
        return (T) this;
    }

    public Object getOnTableClick() throws Exception
    {
        return mainBody.getOnTableClick();
    }

    @SuppressWarnings("unchecked")
    public T setOnTableClick(Object onclick)
    {
        mainBody.setOnTableClick(onclick);
        return (T) this;
    }

    public boolean hasCheckbox()
    {
        return mainBody.hasCheckbox();
    }

    @SuppressWarnings("unchecked")
    public T setHasCheckbox(boolean checkable)
    {
        mainBody.setHasCheckbox(checkable);
        return (T) this;
    }

    public CheckBoxType getCheckBoxType()
    {
        return getMainBody().getCheckBoxType();
    }

    @SuppressWarnings("unchecked")
    public T setCheckBoxType(CheckBoxType checkBoxType)
    {
        getMainBody().setCheckBoxType(checkBoxType);
        return (T) this;
    }

    public String getCheckBoxName()
    {
        return mainBody.getCheckBoxName();
    }

    @SuppressWarnings("unchecked")
    public T setCheckBoxName(String checkBoxName)
    {
        mainBody.setCheckBoxName(checkBoxName);
        return (T) this;
    }

    public Boolean isChecked(Object entity) throws Exception
    {
        return mainBody.isChecked(entity);
    }

    @SuppressWarnings("unchecked")
    public T setChecked(Object checked) throws Exception
    {
        mainBody.setChecked(checked);
        return (T) this;
    }

    public boolean isEnableDrop() throws Exception
    {
        return mainBody.isEnableDrop();
    }

    @SuppressWarnings("unchecked")
    public T setEnableDrop(boolean enableDrop)
    {
        mainBody.setEnableDrop(enableDrop);
        return (T) this;
    }

    public boolean isEnableDrag() throws Exception
    {
        return mainBody.isEnableDrag();
    }

    @SuppressWarnings("unchecked")
    public T setEnableDrag(boolean enableDrag)
    {
        mainBody.setEnableDrag(enableDrag);
        return (T) this;
    }

    public String getDDGroup() throws Exception
    {
        return mainBody.getDDGroup();
    }

    @SuppressWarnings("unchecked")
    public T setDDGroup(String ddGroup)
    {
        mainBody.setDDGroup(ddGroup);
        return (T) this;
    }

    public String getDragGroup() throws Exception
    {
        return mainBody.getDragGroup();
    }

    @SuppressWarnings("unchecked")
    public T setDragGroup(String dragGroup)
    {
        mainBody.setDragGroup(dragGroup);
        return (T) this;
    }

    public String getDropGroup() throws Exception
    {
        return mainBody.getDropGroup();
    }

    @SuppressWarnings("unchecked")
    public T setDropGroup(String dropGroup)
    {
        mainBody.setDropGroup(dropGroup);
        return (T) this;
    }

    public Object getStartDrag() throws Exception
    {
        return mainBody.getStartDrag();
    }

    @SuppressWarnings("unchecked")
    public T setStartDrag(Object startdrag)
    {
        mainBody.setStartDrag(startdrag);
        return (T) this;
    }

    public Object getEndDrag() throws Exception
    {
        return mainBody.getEndDrag();
    }

    @SuppressWarnings("unchecked")
    public T setEndDrag(Object endDrag)
    {
        mainBody.setEndDrag(endDrag);
        return (T) this;
    }

    public Object getOnDragOver() throws Exception
    {
        return mainBody.getOnDragOver();
    }

    @SuppressWarnings("unchecked")
    public T setOnDragOver(Object ondragover)
    {
        mainBody.setOnDragOver(ondragover);
        return (T) this;
    }

    public Object getOnDrop() throws Exception
    {
        return mainBody.getOnDrop();
    }

    @SuppressWarnings("unchecked")
    public T setOnDrop(Object ondrop)
    {
        mainBody.setOnDrop(ondrop);
        return (T) this;
    }

    public Object getCellSelectable() throws Exception
    {
        return mainBody.getCellSelectable();
    }

    @SuppressWarnings("unchecked")
    public T setCellSelectable(Object cellSelectable)
    {
        mainBody.setCellSelectable(cellSelectable);
        return (T) this;
    }

    public Object getForceFit() throws Exception
    {
        return mainBody.getForceFit();
    }

    @SuppressWarnings("unchecked")
    public T setForceFit(Object forceFit)
    {
        mainBody.setForceFit(forceFit);
        return (T) this;
    }

    public Object display(Object record) throws Exception
    {
        return mainBody.display(record);
    }

    public Set<String> getExcludedPropertiesForRow()
    {
        return mainBody.getExcludedPropertiesForRow();
    }
}
