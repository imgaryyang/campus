package com.gzzm.platform.form;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.filestore.FileStoreService;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.fileupload.FileLoader;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.image.ImageZoomer;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.collect.*;
import net.cyan.valmiki.form.components.*;
import net.cyan.valmiki.form.html.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * 编辑表单数据的http入口
 *
 * @author camel
 * @date 11-9-15
 */
@Service
public abstract class FormBodyPage
{
    public static final HtmlGeneratorContainer HTML_GENERATOR_CONTAINER = new HtmlGeneratorContainer();

    public static final CollectorContainer COLLECTOR_CONTAINER = new CollectorContainer();

    protected static JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor()
    {
        @Override
        public void execute(String script)
        {
            HtmlPage page = HtmlPage.getPage();
            if (page != null)
                page.execute(script);
        }

        @Override
        public String invokeServerEvent(String formName, String componentName, String event)
        {
            return "invokeFormEvent(" + (formName == null ? null : ("\"" + formName + "\"")) +
                    ",\"" + componentName + "\",\"" + event + "\");";
        }

        @Override
        public String invokeServerAction(String formName, String actionName, String callback)
        {
            return "invokeFormAction(" + (formName == null ? null : ("\"" + formName + "\"")) +
                    ",\"" + actionName + "\"," + callback + ");";
        }
    };

    @Inject
    private static Provider<FileStoreService> fileStoreServiceProvider;

    @Inject
    private static Provider<FileUploadService> uploadServiceProvider;

    @Inject
    private FormAttachmentService formAttachmentService;

    /**
     * 用户信息
     */
    @Inject
    protected UserOnlineInfo userOnlineInfo;

    /**
     * 存放所有的表单上下文信息，支持多个表单，key为表单名称，value为表单上下文对象
     */
    private Map<String, SystemFormContext> formContexts;

    private Map<String, HtmlGenerationContext> htmlGenerationContexts;

    private DataProvider dataProvider;

    private BusinessContext businessContext;

    private String formName;

    private String componentName;

    private StringBuilder javascripts;

    protected Object result;

    public FormBodyPage()
    {
    }

    public String getFormName()
    {
        return formName;
    }

    public void setFormName(String formName)
    {
        this.formName = formName;
    }

    public String getComponentName()
    {
        return componentName;
    }

    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    public String getSimpleComponentName()
    {
        if (componentName != null)
        {
            int index = componentName.lastIndexOf('.');
            if (index >= 0)
                return componentName.substring(index + 1);
        }

        return componentName;
    }

    protected abstract Long getBodyId(String name) throws Exception;

    protected abstract FormRole getRole(SystemFormContext formContext) throws Exception;

    public abstract Integer getBusinessDeptId() throws Exception;

    protected BusinessContext createBusinessContext()
    {
        return new BusinessContext();
    }

    protected void initBusinessContext(BusinessContext businessContext) throws Exception
    {
        if (userOnlineInfo == null)
            throw new LoginExpireException();

        businessContext.setUser(userOnlineInfo);
        businessContext.put("ip", userOnlineInfo.getIp());
        businessContext.put("request", RequestContext.getContext().getRequest());
        businessContext.put("session", RequestContext.getContext().getSession());

        if (businessContext.getBusinessDeptId() == null)
            businessContext.setBusinessDeptId(getBusinessDeptId());
    }

    @NotSerialized
    public BusinessContext getBusinessContext() throws Exception
    {
        if (businessContext == null)
        {
            businessContext = new BusinessContext();
            initBusinessContext(businessContext);
        }

        return businessContext;
    }

    protected BusinessContext getBusinessContext(SystemFormContext formContext) throws Exception
    {
        return getBusinessContext();
    }

    @NotSerialized
    public SystemFormContext getFormContext() throws Exception
    {
        return getFormContext(null);
    }

    public SystemFormContext getFormContext(String name) throws Exception
    {
        if (formContexts == null)
            formContexts = new HashMap<String, SystemFormContext>();

        if (formContexts.containsKey(name))
        {
            return formContexts.get(name);
        }
        else
        {
            Long bodyId = getBodyId(name);

            SystemFormContext formContext = null;

            if (bodyId != null)
            {
                formContext = getFormContext(bodyId, name);

                formContexts.put(name, formContext);

                FormRole role = getRole(formContext);
                if (role != null)
                    formContext.setRole(role);
                else
                    formContext.setRole(formContext.getForm().createTopRole());

                initFormContext(formContext);

                formContext.init();
            }
            else
            {
                formContexts.put(name, formContext);
            }

            return formContext;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void initFormContext(SystemFormContext formContext) throws Exception
    {
    }

    protected SystemFormContext getFormContext(Long bodyId, String formName) throws Exception
    {
        SystemFormContext formContext = new SystemFormContext(formName, bodyId);

        //设置上下文信息
        formContext.setBusinessContext(getBusinessContext(formContext));

        FormApi.loadFormContext(formContext);

        return formContext;
    }

    protected boolean isReadOnly(SystemFormContext formContext) throws Exception
    {
        return false;
    }

    protected HtmlGenerationContext getHtmlGenerationContext(String name) throws Exception
    {
        if (htmlGenerationContexts == null)
            htmlGenerationContexts = new HashMap<String, HtmlGenerationContext>();

        if (htmlGenerationContexts.containsKey(name))
        {
            return htmlGenerationContexts.get(name);
        }
        else
        {
            SystemFormContext formContext = getFormContext(name);
            HtmlGenerationContext htmlGenerationContext = null;

            if (formContext != null)
            {
                htmlGenerationContext = getHtmlGenerationContext(formContext);
            }

            htmlGenerationContexts.put(name, htmlGenerationContext);

            return htmlGenerationContext;
        }
    }

    protected HtmlGenerationContext getHtmlGenerationContext(final SystemFormContext formContext) throws Exception
    {
        if (formContext == null)
            return null;

        ResultType resultType = RequestContext.getContext().getResultType();
        if (resultType != ResultType.page && resultType != ResultType.script)
            return null;

        HtmlGenerationContext htmlGenerationContext;
        final HtmlPage htmlPage = HtmlPage.getPage();
        if (htmlPage == null)
        {
            final ForwardContext forwardContext = RequestContext.getContext().getForwardContext();

            if (forwardContext == null)
                return null;

            htmlGenerationContext = new HtmlGenerationContext(formContext, new PrintWriter(new Writer()
            {
                @Override
                public void write(char[] cbuf, int off, int len) throws IOException
                {
                    forwardContext.getWriter().write(cbuf, off, len);
                }

                @Override
                public void flush() throws IOException
                {
                    forwardContext.getWriter().flush();
                }

                @Override
                public void close() throws IOException
                {
                    forwardContext.getWriter().close();
                }
            }));
            htmlGenerationContext.setJavaScriptExecutor(javaScriptExecutor);
        }
        else
        {
            htmlGenerationContext = new HtmlGenerationContext(formContext, javaScriptExecutor);
        }

        if (isReadOnly(formContext))
            htmlGenerationContext.setReadOnly(true);

        return htmlGenerationContext;
    }

    public void addJavascript(String javascript)
    {
        if (javascripts == null)
            javascripts = new StringBuilder();
        javascripts.append(javascript);
    }

    public void importJs(String javascript)
    {
        if (javascript != null)
        {
            javascript = javascript.trim();
            if (javascript.startsWith("/"))
            {
                addJavascript("Cyan.importJs(\"" + javascript + "\");\r\n");
            }
            else if (javascript.startsWith("function "))
            {
                addJavascript("(" + javascript + ")();");
            }
            else
            {
                addJavascript(javascript);
            }
        }
    }

    @NotSerialized
    public String getJavascripts() throws Exception
    {
        String javascript = null;
        StringBuilder buffer = null;

        if (this.javascripts != null)
            javascript = this.javascripts.toString();

        if (htmlGenerationContexts != null)
        {
            for (HtmlGenerationContext htmlGenerationContext : htmlGenerationContexts.values())
            {
                if (htmlGenerationContext != null)
                {
                    if (javascript == null)
                    {
                        javascript = htmlGenerationContext.getJavascripts();
                    }
                    else
                    {
                        if (buffer == null)
                            buffer = new StringBuilder(javascript);

                        buffer.append(htmlGenerationContext.getJavascripts());
                    }
                }
            }
        }

        if (buffer != null)
            javascript = buffer.toString();

        return javascript;
    }

    @NotSerialized
    public List<PageItem> getPageItems() throws Exception
    {
        return getPageItems((String) null);
    }

    public List<PageItem> getPageItems(String name) throws Exception
    {
        return getPageItems(getHtmlGenerationContext(name));
    }

    protected List<PageItem> getPageItems(HtmlGenerationContext htmlGenerationContext) throws Exception
    {
        if (htmlGenerationContext == null)
            return null;

        return HTML_GENERATOR_CONTAINER.getPageItems(htmlGenerationContext);
    }

    protected DataProvider getDataProvider()
    {
        if (dataProvider == null)
        {
            final RequestContext requestContext = RequestContext.getContext();
            dataProvider = new DataProvider()
            {
                public Object getValue(String name)
                {
                    return requestContext.get(name);
                }

                public Object[] getValues(String name)
                {
                    return requestContext.getRequest().getParameterValues(name);
                }

                public InputFile[] getFiles(String name)
                {
                    FileLoader fileLoader = requestContext.getFileLoader();
                    if (fileLoader != null)
                        return fileLoader.getFiles(name);
                    return null;
                }
            };
        }
        return dataProvider;
    }

    /**
     * 从httprequest中采集表单数据
     *
     * @param formContext 表单上下文
     * @throws Exception 采集数据错误
     */
    protected void collectFormData(SystemFormContext formContext) throws Exception
    {
        DataCollectContext collectContext = new DataCollectContext(getDataProvider(), formContext);

        COLLECTOR_CONTAINER.collect(collectContext);
    }

    protected void collectFormData() throws Exception
    {
        collectFormData(getFormContext());
    }

    protected void lockFormBody(Long bodyId) throws Exception
    {
        FormApi.lockFormBody(bodyId);
    }

    protected void lockFormBody(String name) throws Exception
    {
        lockFormBody(getBodyId(name));
    }

    protected void lockFormBody() throws Exception
    {
        lockFormBody((String) null);
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void save() throws Exception
    {
        //保存之前先锁住数据，防止其他进程在修改
        lockFormBody();

        //保存之前先采集数据
        collectFormData();

        saveForm(getFormContext());
    }

    protected void saveForm(SystemFormContext context) throws Exception
    {
        if (context != null)
        {
            HtmlGenerationContext htmlGenerationContext = getHtmlGenerationContext(context);
            if (htmlGenerationContext != null)
            {
                htmlGenerationContext.setContainer(HTML_GENERATOR_CONTAINER);
                FormContextLocal.setContext(htmlGenerationContext);
            }

            try
            {
                context.save();
            }
            finally
            {
                FormContextLocal.setContext(null);
            }

            saveFormAttachments(context);
        }
    }

    protected void saveFormAttachments(SystemFormContext context) throws Exception
    {
        formAttachmentService.saveFormAttachmet(context);
    }

    @Service
    @Forward(page = "/platform/form/baks.ptl")
    public String showTextAreaBaks(String formName, String componentName)
    {
        this.formName = formName;
        this.componentName = componentName;

        return null;
    }

    protected ComponentData getComponentData(String formName, String name) throws Exception
    {
        SystemFormContext formContext = getFormContext(formName);
        if (formContext == null)
            return null;

        return formContext.getFormData().getData(name);
    }

    @NotSerialized
    public List<TextAreaBak> getTextAreaBaks() throws Exception
    {
        ComponentData data = getComponentData(formName, componentName);
        if (data instanceof TextAreaData)
            return ((TextAreaData) data).getBaks();

        return null;
    }

    protected ParallelTextItem getParallelTextItem(String formName, String name, String id) throws Exception
    {
        ComponentData data = getComponentData(formName, name);
        if (data instanceof ParallelTextData)
            return ((ParallelTextData) data).getItem(id);

        return null;
    }

    @Service(url = {"/{@class}/form/{formName}/{componentName}/{id}/image",
            "/{@class}/component/{componentName}/{id}/image"})
    public byte[] downParallelTextImage(String formName, String componentName, String id) throws Exception
    {
        ParallelTextItem item = getParallelTextItem(formName, componentName, id);

        String image = item.getImage();
        if (StringUtils.isEmpty(image))
            return null;

        return CommonUtils.base64ToByteArray(image);
    }

    @Transactional
    @Service(url = {"/{@class}/form/{formName}/{componentName}/{id}/thumb",
            "/{@class}/component/{componentName}/{id}/thumb"})
    public byte[] downParallelTextThumbImage(String formName, String componentName, String id) throws Exception
    {
        lockFormBody(formName);

        ParallelTextItem item = getParallelTextItem(formName, componentName, id);

        String thumb = item.getThumb();
        if (StringUtils.isEmpty(thumb))
        {
            String image = item.getImage();
            if (!StringUtils.isEmpty(image))
            {
                //生成缩略图
                int width = 500;
                int height = 120;

                byte[] bytes = CommonUtils.base64ToByteArray(image);
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));

                if (bufferedImage.getWidth() > width || bufferedImage.getHeight() > height)
                {
                    ImageZoomer zoomer = new ImageZoomer(width, height, true, "png");
                    zoomer.setImage(bufferedImage);
                    bytes = zoomer.toBytes();
                }

                thumb = CommonUtils.byteArrayToBase64(bytes);
                item.setThumb(thumb);

                saveForm(getFormContext(formName));
            }
        }

        if (StringUtils.isEmpty(thumb))
            return null;

        return CommonUtils.base64ToByteArray(thumb);
    }

    @Transactional
    @Service(url = {"/{@class}/form/{formName}/{componentName}/{id}/thumb",
            "/{@class}/component/{componentName}/{id}/thumb1"})
    public byte[] downParallelTextThumbImage1(String formName, String componentName, String id) throws Exception
    {
        lockFormBody(formName);

        ParallelTextItem item = getParallelTextItem(formName, componentName, id);

        String thumb = item.getThumb1();
        if (StringUtils.isEmpty(thumb))
        {
            String image = item.getImage();
            if (!StringUtils.isEmpty(image))
            {
                //生成缩略图
                int width = 200;
                int height = 80;

                byte[] bytes = CommonUtils.base64ToByteArray(image);
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));

                if (bufferedImage.getWidth() > width || bufferedImage.getHeight() > height)
                {
                    ImageZoomer zoomer = new ImageZoomer(width, height, true, "png");
                    zoomer.setImage(bufferedImage);

                    bytes = zoomer.toBytes();
                }

                thumb = CommonUtils.byteArrayToBase64(bytes);
                item.setThumb1(thumb);

                saveForm(getFormContext(formName));
            }
        }

        if (StringUtils.isEmpty(thumb))
            return null;

        return CommonUtils.base64ToByteArray(thumb);
    }

    @Transactional
    @Service(url = {"/{@class}/form/{formName}/{componentName}/{id}/thumb",
            "/{@class}/component/{componentName}/{id}/thumb2"})
    public byte[] downParallelTextThumbImage2(String formName, String componentName, String id) throws Exception
    {
        lockFormBody(formName);

        ParallelTextItem item = getParallelTextItem(formName, componentName, id);

        String thumb = item.getThumb2();
        if (StringUtils.isEmpty(thumb))
        {
            String image = item.getImage();
            if (!StringUtils.isEmpty(image))
            {
                //生成缩略图
                int width = 200;
                int height = 80;

                byte[] bytes = CommonUtils.base64ToByteArray(image);
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));

                if (bufferedImage.getWidth() > width || bufferedImage.getHeight() > height)
                {
                    ImageZoomer zoomer = new ImageZoomer(width, height, true, "png");
                    zoomer.setImage(bufferedImage);

                    bytes = zoomer.toBytes();
                }

                thumb = CommonUtils.byteArrayToBase64(bytes);
                item.setThumb2(thumb);

                saveForm(getFormContext(formName));
            }
        }

        if (StringUtils.isEmpty(thumb))
            return null;

        return CommonUtils.base64ToByteArray(thumb);
    }

    @Service(url = {"/{@class}/form/{formName}/{componentName}/{id}/originalImage",
            "/{@class}/component/{componentName}/{id}/originalImage"})
    public byte[] downParallelTextOriginalImage(String formName, String componentName, String id) throws Exception
    {
        ParallelTextItem item = getParallelTextItem(formName, componentName, id);

        String originalImage = item.getOriginalImage();
        if (StringUtils.isEmpty(originalImage))
            return null;

        return CommonUtils.base64ToByteArray(originalImage);
    }

    public String getDefaultPage() throws Exception
    {
        return getDefaultPage(null);
    }

    public String getDefaultPage(String formName) throws Exception
    {
        SystemFormContext formContext = getFormContext(formName);
        return formContext == null ? null : formContext.getDefaultPage();
    }

    @Service
    public void refresh(String formName, String componentName) throws Exception
    {
        SystemFormContext context = getFormContext(formName);

        if (context != null)
        {
            ComponentData data = context.getData(componentName);

            if (data != null && (data instanceof Refreshable))
            {
                HtmlGenerationContext htmlGenerationContext = getHtmlGenerationContext(context);
                if (htmlGenerationContext != null)
                {
                    htmlGenerationContext.setContainer(HTML_GENERATOR_CONTAINER);
                    FormContextLocal.setContext(htmlGenerationContext);
                }

                try
                {
                    ((Refreshable) data).refresh();
                }
                finally
                {
                    FormContextLocal.setContext(null);
                }
            }
        }
    }

    protected void setResult(Object result)
    {
        HtmlPage htmlPage = HtmlPage.getPage();
        if (htmlPage != null)
            htmlPage.setResult(result);

        this.result = result;
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void addFiles(String[] fileIds, String formName, String componentName) throws Exception
    {
        SystemFormContext context = getFormContext(formName);

        if (context != null)
        {
            ComponentData data = context.getData(componentName);

            if (data != null && (data instanceof FileListData))
            {
                FileListData fileListData = (FileListData) data;

                FileStoreService fileStoreService = fileStoreServiceProvider.get();

                List<InputFile> files = fileStoreService.getFiles(fileIds,
                        getBusinessContext().getUserId(), getBusinessDeptId());

                fileListData.setUploads(files.toArray(new InputFile[files.size()]));

                HtmlGenerationContext htmlGenerationContext = getHtmlGenerationContext(context);
                if (htmlGenerationContext != null)
                {
                    htmlGenerationContext.setContainer(HTML_GENERATOR_CONTAINER);
                    FormContextLocal.setContext(htmlGenerationContext);
                }

                try
                {
                    saveForm(context);
                }
                finally
                {
                    FormContextLocal.setContext(null);
                }
            }
        }
    }

    @Service(method = HttpMethod.post)
    @Transactional
    public void uploadFiles(String[] filePaths, String formName, String componentName) throws Exception
    {
        FileUploadService service = uploadServiceProvider.get();
        try
        {
            SystemFormContext context = getFormContext(formName);

            if (context != null)
            {
                lockFormBody(formName);
                ComponentData data = context.getData(componentName);

                if (data != null && (data instanceof FileListData))
                {
                    FileListData fileListData = (FileListData) data;

                    List<InputFile> files = new ArrayList<InputFile>(filePaths.length);
                    for (String filePath : filePaths)
                    {
                        InputFile file = service.getFile(filePath);
                        files.add(file);
                    }

                    fileListData.setUploads(files.toArray(new InputFile[files.size()]));

                    HtmlGenerationContext htmlGenerationContext = getHtmlGenerationContext(context);
                    if (htmlGenerationContext != null)
                    {
                        htmlGenerationContext.setContainer(HTML_GENERATOR_CONTAINER);
                        FormContextLocal.setContext(htmlGenerationContext);
                    }

                    try
                    {
                        saveForm(context);
                    }
                    finally
                    {
                        FormContextLocal.setContext(null);
                    }
                }
            }
        }
        finally
        {
            if (filePaths != null)
            {
                for (String filePath : filePaths)
                {
                    try
                    {
                        service.deleteFile(filePath);
                    }
                    catch (Throwable ex)
                    {
                        //删除临时文件不影响主逻辑，跳过
                    }
                }
            }
        }
    }

    /**
     * 触发表单中某个控件的事件，即执行此事件对应的服务端脚本
     *
     * @param formName      表单名称
     * @param componentName 控件名称
     * @param eventName     事件名称
     * @throws Exception 从数据库加载数据，或者触发事件错误
     */
    @Service(method = HttpMethod.post)
    @Transactional
    public void invokeFormEvent(String formName, String componentName, String eventName) throws Exception
    {
        //保存之前先锁住数据，防止其他进程在修改
        lockFormBody(formName);

        SystemFormContext context = getFormContext(formName);

        if (context != null)
        {

            //执行之前先采集数据
            collectFormData(context);

            HtmlGenerationContext htmlGenerationContext = getHtmlGenerationContext(context);
            if (htmlGenerationContext != null)
            {
                htmlGenerationContext.setContainer(HTML_GENERATOR_CONTAINER);
                FormContextLocal.setContext(htmlGenerationContext);
            }

            try
            {
                Object result = BaseFormComponent.invokeEvent(componentName, eventName, htmlGenerationContext);

                saveForm(context);

                setResult(result);
            }
            finally
            {
                FormContextLocal.setContext(null);
            }
        }
    }

    /**
     * 触发表单的某个动作，即执行此动作对应的服务端脚本
     *
     * @param formName   表单名称
     * @param actionName 动作名称
     * @throws Exception 从数据库加载数据，或者触发动作错误
     */
    @Service(method = HttpMethod.post)
    @Transactional
    public void invokeFormAction(String formName, String actionName) throws Exception
    {
        //保存之前先锁住数据，防止其他进程在修改
        lockFormBody(formName);

        SystemFormContext context = getFormContext(formName);

        if (context != null)
        {
            //执行之前先采集数据
            collectFormData(context);

            HtmlGenerationContext htmlGenerationContext = getHtmlGenerationContext(context);
            if (htmlGenerationContext != null)
            {
                htmlGenerationContext.setContainer(HTML_GENERATOR_CONTAINER);
                FormContextLocal.setContext(htmlGenerationContext);
            }

            try
            {
                Object result = context.getForm().invokeAction(actionName, htmlGenerationContext);

                saveForm(context);

                setResult(result);
            }
            finally
            {
                FormContextLocal.setContext(null);
            }
        }
    }

    public void executePageItem(PageItem item) throws Exception
    {
        item.execute();
    }
}
