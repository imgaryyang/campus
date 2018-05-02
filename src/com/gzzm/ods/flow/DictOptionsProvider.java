package com.gzzm.ods.flow;

import com.gzzm.ods.dict.DictDao;
import com.gzzm.platform.form.SystemFormContext;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.FormContext;
import net.cyan.valmiki.form.components.FListComponent;
import net.cyan.valmiki.form.components.list.OptionsProvider;

import java.util.*;

/**
 * @author camel
 * @date 12-3-15
 */
public class DictOptionsProvider implements OptionsProvider
{
    public static final DictOptionsProvider INSTANCE = new DictOptionsProvider();

    @Inject
    private static Provider<DictDao> daoProvider;

    private DictOptionsProvider()
    {
    }

    public boolean accept(FListComponent component, FormContext context) throws Exception
    {
        String name = component.getName();
        return Constants.Document.PRIORITY.equals(name) || Constants.Document.SECRET.equals(name) ||
                Constants.Send.SEAL.equals(name) || Constants.Document.PRIORITY1.equals(name) ||
                Constants.Document.PRIORITY2.equals(name) || Constants.Document.PRIORITY3.equals(name) ||
                Constants.Document.SECRET1.equals(name) || Constants.Document.PRIORITY4.equals(name) ||
                Constants.Document.SECRET2.equals(name);
    }

    public List<?> getOptions(FListComponent component, FormContext context) throws Exception
    {
        String name = component.getName();
        List<KeyValue<String>> result = null;
        List<?> datas = null;
        if (Constants.Document.PRIORITY.equals(name) || Constants.Document.PRIORITY1.equals(name) ||
                Constants.Document.PRIORITY2.equals(name) || Constants.Document.PRIORITY3.equals(name) ||
                Constants.Document.PRIORITY4.equals(name))
        {
            datas = daoProvider.get().getPriorityList();
        }
        else if (Constants.Document.SECRET.equals(name) || Constants.Document.SECRET1.equals(name) ||
                Constants.Document.SECRET2.equals(name))
        {
            datas = daoProvider.get().getSecretList();
        }
        else if (Constants.Send.SEAL.equals(name))
        {
            Integer deptId = ((SystemFormContext) context).getBusinessDeptId();
            datas = daoProvider.get().getSealList(deptId);
        }

        if (datas != null)
        {
            result = new ArrayList<KeyValue<String>>(datas.size());

            for (Object data : datas)
            {
                result.add(new KeyValue<String>(data.toString(), data.toString()));
            }
        }

        return result;
    }

    public String getText(Object value, FListComponent component, FormContext context) throws Exception
    {
        return DataConvert.getValueString(value);
    }
}
