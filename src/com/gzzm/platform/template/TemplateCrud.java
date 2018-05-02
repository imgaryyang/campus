package com.gzzm.platform.template;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.menu.MenuTreeModel;
import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.io.InputStream;

/**
 * 模版维护
 *
 * @author camel
 * @date 2011-4-21
 */
@Service(url = "/TemplateCrud")
public class TemplateCrud extends BaseNormalCrud<Template, Integer>
{
    @Inject
    private static Provider<MenuTreeModel> menuTreeModelProvider;

    @Like
    private String templateName;

    /**
     * 通过功能点ID查询
     */
    private String appId;

    /**
     * 用于接收上传的文件
     */
    private InputFile file;

    /**
     * 菜单组
     */
    private String group;

    private MenuTreeModel menuTree;

    public TemplateCrud()
    {
        setLog(true);
        addOrderBy("templateName");
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        if (file != null)
        {
            String ext = file.getExtName();
            String fileName = file.getName();

            Inputable content = file.getInputable();

            if ("doc".equals(ext) || "docx".equals(ext))
            {
                content = OfficeUtils.wordToXml(content, ext);
                fileName = fileName.substring(fileName.lastIndexOf(".")) + ".xml";
            }

            getEntity().setContent(content.getInputStream());
            getEntity().setTemplateFileName(fileName);
        }

        return true;
    }

    @Select(field = {"entity.appId", "appId"})
    public MenuTreeModel getMenuTree()
    {
        if (menuTree == null)
        {
            menuTree = menuTreeModelProvider.get();
            menuTree.setCheckable(false);
            menuTree.setGroup(group);
        }

        return menuTree;
    }

    @Service(url = "/template/{$0}/down")
    public InputFile down(Integer templateId) throws Exception
    {
        Template template = getEntity(templateId);

        InputStream content = template.getContent();

        if (content != null)
        {
            String fileName = template.getTemplateFileName();
            if (fileName == null)
            {
                fileName = template.getTemplateName();
                if ("doc".equals(template.getFormat()) || StringUtils.isEmpty(template.getFormat()))
                {
                    fileName += ".xml";
                }
                else
                {
                    fileName += "." + template.getFormat();
                }
            }
            return new InputFile(content, fileName);
        }

        return null;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.addComponent("模版名称", "templateName");
        view.addComponent("菜单", "appId");

        view.addColumn("模版名称", "templateName");
        view.addColumn("菜单", "menuTitle");
        view.addColumn("类型", "type");
        view.addColumn("格式", "format");
        view.addColumn("修改时间", "lastTime");
        view.addColumn("下载", new CHref("下载", "/template/${templateId}/down").setTarget("_blank"));

        view.defaultInit(false);

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("名称", "templateName");
        view.addComponent("菜单", "appId").setProperty("text", getEntity().getMenuTitle());
        view.addComponent("文件名", "fileName");
        view.addComponent("文件", new CFile(null).setProperty("name", "file").setFileType("xml doc doc xls"));
        view.addComponent("类型", "type");
        view.addComponent("格式", new CCombox("format", new String[]{"doc", "xls"}));
        view.addComponent("条件", new CTextArea("condition"));

        view.importCss("/platform/template/template.css");

        view.addDefaultButtons();

        return view;
    }
}
