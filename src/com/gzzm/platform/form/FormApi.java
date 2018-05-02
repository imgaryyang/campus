package com.gzzm.platform.form;

import com.gzzm.platform.attachment.AttachmentService;
import com.gzzm.platform.commons.BusinessContext;
import com.gzzm.platform.organ.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.*;
import net.cyan.valmiki.form.components.table.*;
import net.cyan.valmiki.form.xml.dataload.XmlDataLoaderContainer;
import net.cyan.valmiki.form.xml.datasave.XmlDataSaverContainer;
import net.cyan.valmiki.form.xml.serialize.XmlSerializerContainer;

import java.util.*;

/**
 * @author camel
 * @date 11-8-31
 */
public final class FormApi
{
    private static final String CHARSET = "UTF-8";

    @Inject
    private static Provider<FormDao> daoProvider;

    @Inject
    private static Provider<DeptService> deptServiceProvider;

    @Inject
    private static Provider<AttachmentService> attachmentServiceProvider;

    static final XmlSerializerContainer XML_SERIALIZER_CONTAINER = new XmlSerializerContainer();

    private static final XmlDataLoaderContainer XML_DATA_LOADER_CONTAINER = new XmlDataLoaderContainer();

    private static final XmlDataSaverContainer XML_DATA_SAVER_CONTAINER = new XmlDataSaverContainer();

    private static final FormContainer FORM_CONTAINER =
            new FormContainer(SystemFormLoader.INSTANCE, 1000 * 60 * 60 * 24);

    private FormApi()
    {
    }

    public static FormDao getDao() throws Exception
    {
        return daoProvider.get();
    }

    /**
     * 根据表单ID加载表单
     *
     * @param formId 表单ID
     * @return 表单对象
     * @throws Exception 数据库获取表单信息错误
     */
    public static WebForm getForm(Integer formId) throws Exception
    {
        return FORM_CONTAINER.get(formId.toString());
    }

    /**
     * 将字节数组加载为表单数据对象
     *
     * @param content 字节数组，此字节数组为一个xml，保存了表单数据
     * @param context 表单上下文信息
     * @throws Exception 加载错误，由表单引擎抛出
     */
    public static void loadFormData(SystemFormContext context, byte[] content) throws Exception
    {
        XML_DATA_LOADER_CONTAINER.load(context, content);
    }

    public static void loadFormData(SystemFormContext context, char[] content) throws Exception
    {
        XML_DATA_LOADER_CONTAINER.load(context, new String(content).getBytes(CHARSET));
    }

    /**
     * 将表单数据对象保存为xml
     *
     * @param formContext 表单上下文信息
     * @return xml的字节数组
     * @throws Exception 由表单引擎抛出
     */
    public static byte[] saveFormData(SystemFormContext formContext) throws Exception
    {
        return XML_DATA_SAVER_CONTAINER.save(formContext, CHARSET);
    }

    public static SystemFormContext newFormContext(Integer formId, String formName) throws Exception
    {
        WebForm form = getForm(formId);

        if (form != null)
        {
            setFormUsed(formId);
            return new SystemFormContext(formName, form);
        }

        return null;
    }

    public static SystemFormContext newFormContext(Integer formId) throws Exception
    {
        return newFormContext(formId, null);
    }

    public static SystemFormContext getFormContext(Long bodyId) throws Exception
    {
        return getFormContext(bodyId, null);
    }

    public static SystemFormContext getFormContext(Long bodyId, UserInfo userInfo) throws Exception
    {
        if (userInfo == null)
            userInfo = new UserInfo(1, 1);

        BusinessContext businessContext = new BusinessContext();
        businessContext.setUser(userInfo);

        SystemFormContext formContext = new SystemFormContext(null, bodyId);
        formContext.setBusinessContext(businessContext);

        loadFormContext(formContext);

        return formContext;
    }

    public static void loadFormContext(SystemFormContext context) throws Exception
    {
        FormDao dao = getDao();

        FormBody body = dao.getFormBody(context.getBodyId());

        if (body != null)
        {
            WebForm form = getForm(body.getFormId());

            if (form != null)
            {
                context.init(form);

                if (body.getContent() != null && body.getContent().length > 0)
                {
                    loadFormData(context, body.getContent());
                }
            }
        }
    }

    /**
     * 将表单数据对象以xml格式保存到数据库的FormBody表中
     *
     * @param formContext 表单上下文信息
     * @throws Exception 数据库操作错误，或者由表单引擎产生的错误
     */
    public static void saveFormBody(SystemFormContext formContext) throws Exception
    {
        char[] content = new String(saveFormData(formContext), CHARSET).toCharArray();

        Long bodyId = formContext.getBodyId();

        FormDao dao = getDao();

        FormBody body;
        if (bodyId == null)
            body = new FormBody();
        else
            body = dao.getFormBody(bodyId);

        if (bodyId != null)
        {
            try
            {
                FormBodyBak bak = new FormBodyBak();
                bak.setBodyId(bodyId);
                bak.setContent(body.getContent());
                bak.setUpdateTime(new Date());
                bak.setUserId(formContext.getUserId());

                dao.add(bak);
            }
            catch (Throwable ex)
            {
                //保存备份表失败跳过
            }
        }

        body.setContent(content);
        body.setFormId(Integer.valueOf(formContext.getForm().getFormId()));

        if (bodyId == null)
        {
            dao.add(body);
            formContext.setBodyId(body.getBodyId());
        }
        else
        {
            dao.update(body);
        }
    }

    /**
     * 获得某个表单的最后一个版本
     *
     * @param ieFormId 忽略版本号的表单ID
     * @return 最后一个发布版本的表单信息
     * @throws Exception 数据库查询错误
     */
    public static Integer getLastFormId(Integer ieFormId) throws Exception
    {
        return getDao().getLastFormId(ieFormId);
    }

    /**
     * 获得某个表单的最后一个版本
     *
     * @param ieFormId 忽略版本号的表单信息
     * @return 最后一个发布版本的表单信息
     * @throws Exception 数据库查询错误
     */
    public static FormInfo getLastForm(Integer ieFormId) throws Exception
    {
        return getDao().getLastForm(ieFormId);
    }


    /**
     * 根据表单名称和部门ID获得一个发布的流程
     *
     * @param formName 表单名称
     * @param type     表单类型
     * @param deptId   部门ID
     * @return 对应的表单的id
     * @throws Exception 数据库查询错误
     */
    public static Integer getFormIdByName(String formName, String type, Integer deptId) throws Exception
    {
        return getDao().getFormIdByName(formName, type, deptId);
    }


    private static void setFormUsed(Integer formId, FormDao dao) throws Exception
    {
        dao.setFormUsed(formId);
    }

    /**
     * 设置表单被使用
     *
     * @param formId 流程ID
     * @throws Exception 数据库操作错误
     */
    public static void setFormUsed(Integer formId) throws Exception
    {
        setFormUsed(formId, getDao());
    }

    /**
     * 获得可选的表单列表
     *
     * @param deptId      表单所属的部门ID
     * @param type        流程类型
     * @param authDeptIds 拥有权限的部门
     * @return 表单列表
     * @throws Exception 数据库查询错误
     */
    public static List<FormInfo> getSelectableFormList(Integer deptId, final String type,
                                                       Collection<Integer> authDeptIds) throws Exception
    {
        final FormDao dao = getDao();

        return deptServiceProvider.get().loadDatasFromParentDepts(deptId, new DeptOwnedDataProvider<FormInfo>()
        {
            public List<FormInfo> get(Integer deptId) throws Exception
            {
                return dao.getLastFormInfos(deptId, type);
            }
        }, authDeptIds, null);
    }

    public static void lockFormBody(Long bodyId) throws Exception
    {
        getDao().lockFormBody(bodyId);
    }

    public static void copyForm(FormContext sourceContext, FormContext targetContext, boolean cloneData)
            throws Exception
    {
        FormData targetFormData = targetContext.getFormData();

        for (ComponentData sourceData : sourceContext.getFormData())
        {
            ComponentData targetData = targetFormData.getData(sourceData.getFullName());
            if (targetData == null)
                targetData = targetFormData.getData(sourceData.getName());

            if (targetData != null)
                copyComponent(sourceData, targetData, sourceContext, targetContext, cloneData);
        }
    }

    public static void copyComponent(ComponentData sourceData, ComponentData targetData, FormContext sourceContext,
                                     FormContext targetContext, boolean cloneData)
            throws Exception
    {
        if (sourceData instanceof FileListData)
        {
            if (targetData instanceof FileListData)
            {
                String fileListId = ((FileListData) sourceData).getFileListId();

                if (fileListId != null)
                {
                    StringBuilder buffer = new StringBuilder();
                    for (String listId : fileListId.split(","))
                    {
                        if (!listId.startsWith("@"))
                        {
                            if (cloneData)
                            {
                                Long attachmentId = Long.parseLong(listId);

                                Long attachmentId1 = attachmentServiceProvider.get().clone(attachmentId);

                                if (attachmentId1 != null)
                                    listId = attachmentId1.toString();
                                else
                                    listId = null;
                            }
                            else
                            {
                                listId = "@" + listId;
                            }
                        }

                        if (listId != null)
                        {
                            if (buffer.length() > 0)
                                buffer.append(",");

                            buffer.append(listId);
                        }
                    }

                    ((FileListData) targetData).setFileListId(buffer.toString());
                }
            }
        }
        else if (sourceData instanceof ParallelTextData)
        {
            if (targetData instanceof ParallelTextData)
            {
                ((ParallelTextData) targetData).getItems().addAll(((ParallelTextData) sourceData).getItems());
            }
        }
        else if (sourceData instanceof SimpleComponentData)
        {
            if (targetData instanceof SimpleComponentData)
            {
                ((SimpleComponentData) targetData).setValue(((SimpleComponentData) sourceData).getValue());
            }
        }
        else if (sourceData instanceof ArrayComponentData)
        {
            if (targetData instanceof ArrayComponentData)
            {
                ((ArrayComponentData) targetData).setValues(((ArrayComponentData) sourceData).getValues());
            }
        }
        else if (sourceData instanceof RepeatTableData)
        {
            if (targetData instanceof RepeatTableData)
            {
                for (RepeatTableRow sourceRow : ((RepeatTableData) sourceData).getRows())
                {
                    RepeatTableRow targetRow = ((RepeatTableData) targetData).addRow(targetContext);

                    for (ComponentData sourceRowData : sourceRow)
                    {
                        ComponentData targetRowData = targetRow.getData(sourceRowData.getName());

                        if (targetRowData != null)
                            copyComponent(sourceRowData, targetRowData, sourceContext, targetContext, cloneData);
                    }
                }
            }
        }
        else if (targetData instanceof ValueSettableData)
        {
            ((ValueSettableData) targetData).setValue(sourceData.valueOf(), null, null);
        }
    }
}
