package com.gzzm.ods.documenttemplate;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.InputFile;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

/**
 * 红头模版维护
 *
 * @author camel
 * @date 2010-12-17
 */
@Service(url = "/ods/DocumentTemplate")
public class DocumentTemplateCrud extends DeptOwnedNormalCrud<DocumentTemplate, Integer>
{
    @Inject
    private DocumentTemplateDao dao;

    @Like
    private String templateName;

    public DocumentTemplateCrud()
    {
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = (getAuthDeptIds() != null && getAuthDeptIds().size() == 1) ? new PageTableView() :
                new ComplexTableView(new AuthDeptDisplay(), "deptId");

        view.addComponent("模板名称", "templateName");

        view.addColumn("模板名称", "templateName");
        view.addColumn("下载模板", new CHref("下载模板", "/ods/DocumentTemplate/${templateId}/down").setTarget("_blank"))
                .setWidth("100");

        view.defaultInit();
        view.addButton(Buttons.sort());

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("模板名称", "templateName");
        view.addComponent("模板文件", new CFile("template").setFileType("doc"));

        view.addDefaultButtons();

        view.importJs("/ods/documenttemplate/documenttemplate.js");

        return view;
    }

    @Service(url = "/ods/DocumentTemplate/{$0}/down")
    public InputFile downFile(Integer redHeadId) throws Exception
    {
        DocumentTemplate documentTemplate = getEntity(redHeadId);

        return new InputFile(documentTemplate.getTemplate(), documentTemplate.getTemplateName() + ".doc");
    }
}
