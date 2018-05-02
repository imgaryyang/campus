package com.gzzm.ods.documenttemplate;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.util.*;

/**
 * @author camel
 * @date 11-9-26
 */
public class DocumentTemplateService
{
    public static final String DOCUMENT_TEMPLATE_SELECT_APP = "document_template_select";

    @Inject
    private DeptService deptService;

    @Inject
    private DocumentTemplateDao dao;

    public DocumentTemplateService()
    {
    }

    public DocumentTemplateDao getDao()
    {
        return dao;
    }

    public DocumentTemplate getDocumentTemplate(Integer templateId) throws Exception
    {
        return dao.getDocumentTemplate(templateId);
    }

    public String getDocumentTemplatePath(Integer templateId) throws Exception
    {
        return getDocumentTemplatePath(getDocumentTemplate(templateId));
    }

    public static String getDocumentTemplatePath(DocumentTemplate documentTemplate) throws Exception
    {
        String path = "/temp/documenttemplate/" + documentTemplate.getTemplateId();

        String result = Tools.getAppPath(path + ".doc");

        File file = new File(result);

        if (!file.exists() || file.lastModified() < documentTemplate.getLastModified().getTime())
        {
            InputStream in = documentTemplate.getTemplate();
            if (in != null)
                IOUtils.streamToFile(in, file);
        }

        return result;
    }

    public InputFile getDocumentTemplateFile(Integer templateId) throws Exception
    {
        String path = getDocumentTemplatePath(templateId);

        if (path == null)
            return null;
        else
            return new InputFile(new File(path));
    }

    public List<DocumentTemplate> getDocumentTemplates(Integer deptId, UserInfo userInfo)
            throws Exception
    {
        Collection<Integer> authDeptIds = userInfo.getAuthDeptIds(DOCUMENT_TEMPLATE_SELECT_APP);
        if (authDeptIds != null && authDeptIds.isEmpty())
            authDeptIds = Arrays.asList(deptId, 1);

        return deptService.loadDatasFromParentDepts(deptId, new DeptOwnedDataProvider<DocumentTemplate>()
        {
            public List<DocumentTemplate> get(Integer deptId) throws Exception
            {
                return dao.getDocumentTemplates(deptId);
            }
        }, authDeptIds, null);
    }

}
