package com.gzzm.platform.template;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 2011-4-21
 */
public abstract class TemplateDao extends GeneralDao
{
    public TemplateDao()
    {
    }

    @OQL("select t from Template t where appId=:1 and type=:2 and (format=:3 or format is null)")
    public abstract List<Template> getTemplate(String appId, TemplateType type, String format) throws Exception;
}
