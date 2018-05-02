package com.gzzm.platform.form;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.Base64_0;
import net.cyan.crud.view.FieldCell;
import net.cyan.nest.annotation.Inject;
import net.cyan.nest.integration.RequestParameter;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.*;
import net.cyan.valmiki.form.xml.serialize.*;

import java.util.*;

/**
 * @author camel
 * @date 2015/1/14
 */
@Service(url = "/forminfo_component")
public class FormComponentCrud extends BaseNormalCrud<FormComponentInfo, String>
{
    public static final Base64_0 BASE64 = new Base64_0('_', '-', '$');

    private static final XmlSerializerContainer XML_SERIALIZER_CONTAINER = new XmlSerializerContainer();

    @Inject
    private FormDao dao;

    private Integer formId;

    private WebForm webForm;

    private String componentName;

    private String page;

    @RequestParameter
    private String key;

    public FormComponentCrud()
    {
        setPageNo(-1);
    }

    public Integer getFormId()
    {
        return formId;
    }

    public void setFormId(Integer formId)
    {
        this.formId = formId;
    }

    public String getComponentName()
    {
        return componentName;
    }

    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    @NotSerialized
    public WebForm getWebForm() throws Exception
    {
        if (webForm == null)
            webForm = SystemFormLoader.INSTANCE.loadForm(formId.toString());

        return webForm;
    }

    @NotSerialized
    @Select(field = "page")
    public List<String> getPages() throws Exception
    {
        List<String> result = new ArrayList<String>();
        for (FormPage formPage : getWebForm().getPages())
        {
            result.add(formPage.getName());
        }

        return result;
    }

    private void saveForm() throws Exception
    {
        if (webForm != null)
        {
            XmlSerializeContext xmlSerializeContext = new XmlSerializeContext(XML_SERIALIZER_CONTAINER);

            byte[] bytes = xmlSerializeContext.saveForm(webForm, "UTF-8");

            FormInfo formInfo = new FormInfo();
            formInfo.setFormId(formId);
            formInfo.setForm(new String(bytes, "UTF-8").toCharArray());

            dao.update(formInfo);
        }
    }

    @Override
    public String getKey(FormComponentInfo info) throws Exception
    {
        return BASE64.byteArrayToBase64((info.getPage() + "." + info.getComponent().getName()).getBytes("UTF-8"));
    }

    @Override
    public FormComponentInfo getEntity(String key) throws Exception
    {
        key = new String(BASE64.base64ToByteArray(key), "UTF-8");

        int index = key.indexOf('.');

        String page = key.substring(0, index);
        String componentName = key.substring(index + 1);

        WebForm webForm = getWebForm();
        FormPage formPage = webForm.getPage(page);

        FormComponent component = formPage.getComponent(componentName);

        return new FormComponentInfo(page, component);
    }

    @Override
    public FormComponentInfo getEntity()
    {
        FormComponentInfo entity = super.getEntity();

        if (entity == null && key != null)
        {
            try
            {
                entity = getEntity(key);
            }
            catch (Exception ex)
            {
                Tools.wrapException(ex);
            }
        }

        return entity;
    }

    @Override
    public void update(FormComponentInfo entity) throws Exception
    {
        RequestContext.getContext().fillForm(entity.getComponent(), "entity.component");

        saveForm();
    }

    @Override
    protected void loadList() throws Exception
    {
        List<FormComponentInfo> list = new ArrayList<FormComponentInfo>();

        for (FormPage formPage : getWebForm().getPages())
        {
            if (StringUtils.isEmpty(page) || formPage.getName().contains(page))
            {
                for (FormComponent formComponent : formPage.getComponents())
                {
                    if (formComponent instanceof FLabel)
                    {
                        if (StringUtils.isEmpty(componentName) || formComponent.getName().contains(componentName))
                        {
                            FormComponentInfo componentInfo = new FormComponentInfo(formPage.getName(), formComponent);

                            list.add(componentInfo);
                        }
                    }
                    else
                    {
                        for (FormComponent component : formComponent.getAllComponents())
                        {
                            if (!(component instanceof FLabel) && !(component instanceof FLine))
                            {
                                if (StringUtils.isEmpty(componentName) || component.getName().contains(componentName))
                                {
                                    FormComponentInfo componentInfo =
                                            new FormComponentInfo(formPage.getName(), component);

                                    list.add(componentInfo);
                                }
                            }
                        }
                    }
                }
            }
        }

        setList(list);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.setTitle("表单控件管理");

        view.addComponent("表单页", "page");
        view.addComponent("控件名称", "componentName");

        view.addColumn("表单页", new FieldCell("page").setOrderable(false)).setWidth("250");
        view.addColumn("控件名称", new FieldCell("component.name").setOrderable(false));

        view.addButton(Buttons.query());
        view.makeEditable();

        return view;
    }

    @Override
    public String show(String key, String forward) throws Exception
    {
        this.key = key;

        super.show(key, forward);

        FormComponentInfo entity = getEntity();
        FormComponent component = entity.getComponent();

        String s = "default";
        if (component instanceof FSession)
        {
            s = "session";
        }
        else if (component instanceof FInput)
        {
            s = "input";
        }
        else if (component instanceof FTextArea)
        {
            s = "textarea";
        }
        else if (component instanceof FLabel)
        {
            s = "label";
        }
        else if (component instanceof FParallelText)
        {
            s = "parallel";
        }
        else if (component instanceof FFileList)
        {
            s = "file";
        }
        else if (component instanceof FRadioList)
        {
            s = "radiolist";
        }

        return "/platform/form/components/" + s + ".ptl";
    }
}
