package com.gzzm.ods.print;

import com.gzzm.ods.business.BusinessModel;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import net.cyan.commons.util.IOUtils;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.util.*;

/**
 * @author camel
 * @date 11-11-10
 */
public class PrintService
{
    @Inject
    private DeptService deptService;

    @Inject
    private PrintDao dao;

    public PrintService()
    {
    }

    public PrintTemplate getPrintTemplate(Integer templateId) throws Exception
    {
        return dao.getPrintTemplate(templateId);
    }

    public List<PrintTemplate> getPrintTemplates(Integer businessId, Integer deptId, String businessType)
            throws Exception
    {
        if (businessId != null)
        {
            List<PrintTemplate> templates = dao.getPrintTemplates(businessId);
            if (templates.size() > 0)
                return templates;

            BusinessModel businessModel = dao.getBusiness(businessId);

            if (businessModel != null)
            {
                deptId = businessModel.getDeptId();
                businessType = businessModel.getType().getType();
            }
        }

        if (deptId == null)
            return Collections.emptyList();

        return getPrintTemplates(deptId, businessType);
    }

    public List<PrintTemplate> getPrintTemplates(Integer deptId, String businessType) throws Exception
    {
        DeptInfo dept = deptService.getDept(deptId);

        if (dept == null)
            return Collections.emptyList();

        List<PrintTemplate> templates = dao.getPrintTemplates(deptId, businessType);
        if (templates.size() > 0)
            return templates;

        while (dept.parentDept() != null)
        {
            dept = dept.parentDept();

            templates = dao.getPrintTemplates(dept.getDeptId(), businessType);

            if (templates.size() > 0)
                return templates;
        }

        return templates;
    }

    public String getTemplatePath(PrintTemplate template) throws Exception
    {
        String path = "/temp/ods/print/" + template.getTemplateId();

        File file = new File(Tools.getAppPath(path + ".xml"));

        if (!file.exists() || file.lastModified() < template.getLastModified().getTime())
        {
            InputStream content = template.getContent();
            if (content == null)
                return null;

            IOUtils.streamToFile(content, file);
        }

        return path;
    }
}
