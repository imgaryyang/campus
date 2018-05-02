package com.gzzm.platform.form;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.InputFile;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.*;

import java.util.*;

/**
 * 表单维护
 *
 * @author fwj
 * @date 2011-7-12
 */
@Service(url = "/forminfo")
public class FormInfoCrud extends DeptOwnedNormalCrud<FormInfo, Integer>
{
    @Inject
    private FormDao dao;

    @UserId
    private Integer userId;

    @Like
    private String formName;

    private FormType type;

    private WebForm webForm;

    public String getFormName()
    {
        return formName;
    }

    public void setFormName(String formName)
    {
        this.formName = formName;
    }

    public FormType getType()
    {
        return type;
    }

    public void setType(FormType type)
    {
        this.type = type;
    }

    public FormInfoCrud()
    {
        addOrderBy("formName", OrderType.desc);
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "published=?false";
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
        PageTableView view = new ComplexTableView(new AuthDeptDisplay(), "deptId");

        view.addComponent("表单名称", "formName");
        view.addComponent("表单类型", "type");

        view.addColumn("表单名称", "formName");
        view.addColumn("表单类型", "type");
        view.addColumn("创建人", "createUser.userName");
        view.addColumn("发布", new CButton("发布", "publish(${formId})"));
        view.addColumn("下载", new CHref("下载", "/form/${formId}/down").setTarget("_blank"));
        view.addColumn("表单页管理", new CButton("表单页管理", "editPages(${formId})"));
        view.addColumn("角色管理", new CButton("角色管理", "showRoles(${formId})"));
        view.addColumn("控件管理", new CButton("控件管理", "showComponents(${formId})"));
        view.defaultInit();
        view.importJs("/platform/form/forminfo.js");
        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("表单名称", "formName");
        view.addComponent("表单类型", "type");
        view.addComponent("上传文件", new CFile("form").setFileType("xml"));
        view.addDefaultButtons();
        return view;
    }

    @Override
    public FormInfo clone(FormInfo entity) throws Exception
    {
        FormInfo c = super.clone(entity);

        c.setIeFormId(null);

        return c;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();
        getEntity().setCreator(userId);
        getEntity().setPublishTime(new Date());
        getEntity().setPublished(false);
        getEntity().setUsed(false);
        getEntity().setUpdateTime(new Date());
        return true;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();
        getEntity().setUpdateTime(new Date());
        return true;
    }

    @Service(url = "/form/{$0}/down")
    public InputFile down(Integer formId) throws Exception
    {
        FormInfo formInfo = getEntity(formId);
        String formName = formInfo.getFormName() + ".xml";
        if (formInfo.getForm() != null)
        {
            return new InputFile(new String(formInfo.getForm()).getBytes("UTF-8"), formName);
        }
        return null;
    }

    /**
     * 获得表单的最后一个版本
     *
     * @param formId 表单ID
     * @return 要发布的流程的Id
     * @throws Exception 数据库操作异常
     */
    @Service
    public FormInfo getLastForm(Integer formId) throws Exception
    {
        FormInfo formInfo = dao.getFormInfo(formId);

        return dao.getLastForm(formInfo.getIeFormId());
    }

    /**
     * 发布按钮事件处理
     *
     * @param cover  是否覆盖原来的版本
     * @param formId 表单Id
     * @return 新版本的版本号
     * @throws Exception 数据库异常
     */
    @Service
    public Integer publish(Integer formId, boolean cover) throws Exception
    {
        FormInfo formInfo = dao.getFormInfo(formId);

        if (cover)
        {
            FormInfo lastFormInfo = dao.getLastForm(formInfo.getIeFormId());

            lastFormInfo.setFormName(formInfo.getFormName());
            lastFormInfo.setForm(formInfo.getForm());
            lastFormInfo.setUpdateTime(new Date());
            lastFormInfo.setType(formInfo.getType());

            dao.update(lastFormInfo);

            return lastFormInfo.getVersion();
        }
        else
        {
            FormInfo newFormInfo = new FormInfo();

            newFormInfo.setFormName(formInfo.getFormName());
            newFormInfo.setIeFormId(formInfo.getIeFormId());
            newFormInfo.setType(formInfo.getType());

            Integer version = dao.getMaxVersion(formInfo.getIeFormId());
            if (version != null)
            {
                newFormInfo.setVersion(++version);
            }
            else
            {
                newFormInfo.setVersion(version = 1);
            }

            newFormInfo.setForm(formInfo.getForm());
            newFormInfo.setCreator(userId);
            newFormInfo.setPublishTime(new Date());
            newFormInfo.setPublished(true);
            newFormInfo.setUsed(false);
            newFormInfo.setDeptId(formInfo.getDeptId());
            newFormInfo.setUpdateTime(new Date());

            dao.add(newFormInfo);

            return version;
        }
    }

    @NotSerialized
    public WebForm getWebForm() throws Exception
    {
        if (webForm == null)
            webForm = FormApi.getForm(getEntity().getFormId());

        return webForm;
    }

    @NotSerialized
    public Set<String> getRoleNames() throws Exception
    {
        return getWebForm().getRoles().keySet();
    }

    @NotSerialized
    public FormRole getRole(String roleName) throws Exception
    {
        return getWebForm().getRole(roleName);
    }
}
