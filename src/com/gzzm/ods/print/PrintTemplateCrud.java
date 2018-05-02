package com.gzzm.ods.print;

import com.gzzm.ods.business.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 打印模板维护
 *
 * @author camel
 * @date 11-11-9
 */
@Service(url = "/ods/PrintTemplate")
public class PrintTemplateCrud extends DeptOwnedNormalCrud<PrintTemplate, Integer>
{
    @Inject
    private BusinessDao businessDao;

    @Like
    private String templateName;

    private BusinessType businessType;

    /**
     * 用于接收上传的模板文件
     */
    private InputFile file;

    public PrintTemplateCrud()
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

    public BusinessType getBusinessType()
    {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType)
    {
        this.businessType = businessType;
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    @NotSerialized
    @Select(field = "entity.businessId")
    public List<BusinessModel> getBusinesses() throws Exception
    {
        if (getEntity() != null && getEntity().getBusinessType() != null)
        {
            return businessDao.getBusinesses(getDeptId(), getEntity().getBusinessType().getType());
        }
        else
        {
            return Collections.emptyList();
        }
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        if (file != null)
        {
            String ext = IOUtils.getExtName(file.getName());

            Inputable content = file.getInputable();

            if ("doc".equals(ext) || "docx".equals(ext))
                content = OfficeUtils.wordToXml(content, ext);

            getEntity().setContent(content.getInputStream());
        }

        return true;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = (getAuthDeptIds() != null && getAuthDeptIds().size() == 1) ? new PageTableView() :
                new ComplexTableView(new AuthDeptDisplay(), "deptId");

        view.addComponent("模板名称", "templateName");
        view.addComponent("业务类型", "businessType");

        view.addColumn("模板名称", "templateName");
        view.addColumn("业务类型", "businessType").setWidth("60").setAlign(Align.left);
        view.addColumn("关联业务", "businessModel.businessName");
        view.addColumn("显示名称", "showName");
        view.addColumn("关联表单", "formType");
        view.addColumn("下载模板", new CHref("下载模板", "/ods/PrintTemplate/${templateId}/down").setTarget("_blank"))
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
        view.addComponent("业务类型", "businessType").setProperty("onchange", "typeChange()");
        view.addComponent("关联业务", "businessId");
        view.addComponent("显示名称", "showName");
        view.addComponent("关联表单", "formType");
        view.addComponent("文件", new CFile(null).setProperty("name", "file").setFileType("xml doc docx"));
        view.addComponent("二维码左", "qrLeft");
        view.addComponent("二维码上", "qrTop");

        view.addDefaultButtons();

        view.importJs("/ods/print/template.js");

        return view;
    }

    @Service(url = "/ods/PrintTemplate/{$0}/down")
    public InputFile downFile(Integer templateId) throws Exception
    {
        PrintTemplate template = getEntity(templateId);

        return new InputFile(template.getContent(), template.getTemplateName() + ".xml");
    }
}
