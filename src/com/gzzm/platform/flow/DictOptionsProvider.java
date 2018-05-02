package com.gzzm.platform.flow;

import com.gzzm.platform.dict.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.FormContext;
import net.cyan.valmiki.form.components.FListComponent;
import net.cyan.valmiki.form.components.list.OptionsProvider;

import java.util.*;

/**
 * @author camel
 * @date 2015/2/3
 */
public class DictOptionsProvider implements OptionsProvider
{
    public static final DictOptionsProvider INSTANCE = new DictOptionsProvider();

    @Inject
    private static Provider<DictDao> daoProvider;

    public DictOptionsProvider()
    {
    }

    @Override
    public boolean accept(FListComponent component, FormContext context) throws Exception
    {
        Dict dict = daoProvider.get().getDictByName(component.getName());
        return dict != null;
    }

    @Override
    public List<?> getOptions(FListComponent component, FormContext context) throws Exception
    {
        DictDao dictDao = daoProvider.get();
        Dict dict = dictDao.getDictByName(component.getName());
        if (dict != null)
        {
            List<DictItem> items = dictDao.getItemsByDictId(dict.getDictId());

            List<KeyValue<String>> result = new ArrayList<KeyValue<String>>(items.size());
            for (DictItem item : items)
            {
                String key;

                if (dict.getRequireCode() != null && dict.getRequireCode())
                {
                    key = item.getItemCode();
                }
                else
                {
                    key = item.getItemName();
                }

                String name = item.getItemName();

                result.add(new KeyValue<String>(key, name));
            }

            return result;
        }

        return null;
    }

    @Override
    public String getText(Object value, FListComponent component, FormContext context) throws Exception
    {
        DictDao dictDao = daoProvider.get();
        Dict dict = dictDao.getDictByName(component.getName());

        String s = DataConvert.getValueString(value);

        if (dict != null)
        {
            if (dict.getRequireCode() != null && dict.getRequireCode())
            {
                DictItem dictItem = dictDao.getItemByCode(dict.getDictId(), s);
                if (dictItem != null)
                    return dictItem.getItemName();
            }
        }

        return s;
    }
}
