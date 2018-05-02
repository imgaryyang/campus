package com.gzzm.platform.form;

import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.xml.serialize.*;

import java.util.*;

/**
 * @author camel
 * @date 2014/6/25
 */
@Service(url = "/forminfo_role")
public class FormRoleCrud extends BaseNormalCrud<FormRoleInfo, String>
{
    private static final XmlSerializerContainer XML_SERIALIZER_CONTAINER = new XmlSerializerContainer();

    @Inject
    private FormDao dao;

    private Integer formId;

    private WebForm webForm;

    public FormRoleCrud()
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

    @NotSerialized
    public WebForm getWebForm() throws Exception
    {
        if (webForm == null)
            webForm = SystemFormLoader.INSTANCE.loadForm(formId.toString());

        return webForm;
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
    public String getKey(FormRoleInfo entity) throws Exception
    {
        return entity.getRoleName();
    }

    @Override
    public boolean isUnique(String... fields) throws Exception
    {
        return !isNew$() || !getWebForm().getRoles().containsKey(getEntity().getRoleName());
    }

    @Override
    public FormRoleInfo clone(FormRoleInfo entity) throws Exception
    {
        return new FormRoleInfo("", entity.getRole());
    }

    @Override
    public void initEntity(FormRoleInfo entity) throws Exception
    {
        super.initEntity(entity);

        if (entity.getRole() == null)
            entity.setRole(getWebForm().createDefaultRole());
    }

    @Override
    protected void loadList() throws Exception
    {
        List<FormRoleInfo> list = new ArrayList<FormRoleInfo>();

        for (Map.Entry<String, FormRole> entity : getWebForm().getRoles().entrySet())
        {
            FormRoleInfo info = new FormRoleInfo();

            info.setRoleName(entity.getKey());

            list.add(info);
        }

        setList(list);
    }

    @Override
    public int deleteAll() throws Exception
    {
        int result = 0;

        String[] keys = getKeys();
        if (keys != null && keys.length > 0)
        {
            WebForm webForm = getWebForm();

            for (String key : keys)
            {
                if (webForm.removeRole(key) != null)
                    result++;
            }

            saveForm();
        }

        return result;
    }

    @Override
    public FormRoleInfo getEntity(String key) throws Exception
    {
        FormRole role = getWebForm().getRole(key);

        return new FormRoleInfo(key, role);
    }

    @Override
    public boolean delete(String key) throws Exception
    {
        setKeys(new String[]{key});

        return deleteAll() > 0;
    }

    @Override
    public void insert(FormRoleInfo entity) throws Exception
    {
        entity.fill();

        getWebForm().addRole(entity.getRoleName(), entity.getRole());

        saveForm();
    }

    @Override
    public void update(FormRoleInfo entity) throws Exception
    {
        entity.fill();

        WebForm webForm = getWebForm();
        String roleName = entity.getRoleName();

        webForm.removeRole(roleName);
        webForm.addRole(roleName, entity.getRole());

        saveForm();
    }

    @Override
    @Forward(page = "/platform/form/role.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/platform/form/role.ptl")
    public String show(String key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/platform/form/role.ptl")
    public String duplicate(String key, String forward) throws Exception
    {
        return super.duplicate(key, forward);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.setTitle("表单角色管理");

        view.addColumn("角色名称", "roleName");

        view.defaultInit();

        return view;
    }
}
