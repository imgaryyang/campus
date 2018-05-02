package com.gzzm.platform.commons.components;

import com.gzzm.platform.commons.Tools;
import net.cyan.arachne.annotation.Service;
import net.cyan.arachne.components.SearchablePageListModel;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.*;
import net.cyan.crud.annotation.*;

import java.util.*;

/**
 * @author camel
 * @date 13-3-14
 */
@Service
public abstract class EntityPageListModel<E, K> extends NormalQueryCrud<E, K> implements SearchablePageListModel<E>
{
    @Like
    private String text;

    private List<E> items;

    public EntityPageListModel()
    {
        setPageSize(0);
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    @StartsWith
    public String getText1()
    {
        return text;
    }

    protected String getTextField() throws Exception
    {
        return null;
    }

    protected String getSearchCondition() throws Exception
    {
        String textField = getTextField();

        if (textField != null)
            return textField + " like ?text";

        return null;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return getSearchCondition();
    }

    public boolean isSearchable() throws Exception
    {
        return getSearchCondition() != null;
    }

    public Collection<E> search(String text) throws Exception
    {
        setText(text);
        return getItems();
    }

    public Collection<E> getItems() throws Exception
    {
        if (items == null)
        {
            try
            {
                CrudAdvice.before(this);

                items = getList();

                CrudAdvice.after(this, list);
            }
            catch (Throwable ex)
            {
                CrudAdvice.catchHandle(this, ex);

                Tools.handleException(ex);

                return null;
            }
            finally
            {
                CrudAdvice.finallyHandle(this);
            }
        }

        return items;
    }

    public String getId(E e) throws Exception
    {
        return StringUtils.toString(getKey(e));
    }
}
