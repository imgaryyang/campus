package com.gzzm.platform.template;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.proteus.TemplateContext;
import net.cyan.proteus.report.word.*;
import net.cyan.valmiki.form.print.ParallelTextWordElementProcesser;

import java.io.*;
import java.util.*;

/**
 * @author camel
 * @date 2011-4-21
 */
public class TemplateService
{
    public static final WordTemplateContainer WORD_TEMPLATE_CONTAINER =
            new WordTemplateContainer(SystemConfig.getInstance().getAppPath());

    @Inject
    private TemplateDao dao;

    public TemplateService()
    {
    }

    public Template getTemplate(String appId, TemplateType type, String format, Object page) throws Exception
    {
        List<Template> templates = dao.getTemplate(appId, type, format);

        if (templates.size() == 1)
            return templates.get(0);

        for (Template template : templates)
        {
            if (StringUtils.isEmpty(template.getCondition()))
                return template;

            if (DataConvert.toBoolean(BeanUtils.eval(template.getCondition(), page, null)))
                return template;
        }

        return null;
    }

    public String getTemplatePath(Template template) throws Exception
    {
        InputStream content = template.getContent();
        if (content != null)
        {
            String path = "/temp/template/" + template.getTemplateId();

            String format = template.getFormat();
            if (StringUtils.isEmpty(format) || "doc".equals(format))
                path += ".xml";
            else
                path += "." + format;

            File file = new File(Tools.getAppPath(path));

            if (!file.exists() || file.lastModified() < template.getLastTime().getTime())
            {
                if (content == null)
                    return null;

                IOUtils.streamToFile(content, file);
            }

            return path;
        }
        return null;
    }

    public String getTemplatePath(String appId, TemplateType type, String format, Object page) throws Exception
    {
        Template template = getTemplate(appId, type, format, page);
        if (template == null)
            return null;

        //模版存在

        return getTemplatePath(template);
    }

    public static void processTemplate(String name, Object model, Map<String, ?> context, OutputStream out)
            throws Exception
    {
        TemplateContext templateContext = WORD_TEMPLATE_CONTAINER.createContext(model, context, out);

        WordReportUtils.addElementProcessor(templateContext, ParallelTextWordElementProcesser.INSTANCE);

        WORD_TEMPLATE_CONTAINER.process(name, templateContext);
    }

    public static void processTemplate(String name, Object model, OutputStream out) throws Exception
    {
        processTemplate(name, model, null, out);
    }
}
