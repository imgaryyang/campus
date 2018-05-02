package com.gzzm.ods.flow;

import com.gzzm.ods.business.*;
import com.gzzm.ods.document.*;
import com.gzzm.platform.commons.SystemException;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.form.FormApi;
import com.gzzm.platform.organ.SimpleDeptInfo;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.*;
import net.cyan.valmiki.form.FormContext;

import java.util.Date;

/**
 * 公文流程启动上下文，对FlowStartContext做一些扩展，加入公文相关信息
 *
 * @author camel
 * @date 11-9-22
 */
public class OdFlowStartContext extends FlowStartContext
{
    @Inject
    private static Provider<BusinessService> businessServiceProvider;

    @Inject
    protected OdFlowDao dao;

    private BusinessService businessService;

    private Integer businessId;

    private BusinessModel business;

    /**
     * 公文流程信息，可以通过在发起流程之前设置此属性，来设置一些初始信息
     * 如果没有设置初始值，则在发起流程之后自动创建默认值
     */
    private OdFlowInstance odFlowInstance;

    private Class<? extends OdFlowComponent> component;

    private Long sourceDocumentId;

    private Inputable text;

    private String textType = "doc";

    private String businessType;

    public static OdFlowStartContext create() throws Exception
    {
        return create(OdFlowStartContext.class);
    }

    public OdFlowStartContext()
    {
    }

    @Override
    protected SystemFlowDao createSystemFlowDao() throws Exception
    {
        return OdSystemFlowDao.getInstance();
    }

    protected BusinessService getBusinessService()
    {
        if (businessService == null)
            businessService = businessServiceProvider.get();
        return businessService;
    }

    public Class<? extends OdFlowComponent> getComponent()
    {
        return component;
    }

    public void setComponent(Class<OdFlowComponent> component)
    {
        this.component = component;
    }

    public Integer getBusinessId() throws Exception
    {
        if (businessId == null)
        {
            Class<? extends OdFlowComponent> component = getComponent();
            if (component != null)
            {
                business = getBusinessService().getBusinessByComponent(getBusinessContext().getBusinessDeptId(),
                        businessType == null ? "inner" : "send", component.getName());

                if (business == null)
                {
                    throw new SystemException("business for " + component.getName() + " is not exists,deptId:" +
                            getBusinessContext().getBusinessDeptId());
                }

                businessId = business.getBusinessId();
            }
        }

        return businessId;
    }

    public void setBusinessId(Integer businessId)
    {
        this.businessId = businessId;
    }

    public String getBusinessType()
    {
        return businessType;
    }

    public void setBusinessType(String businessType)
    {
        this.businessType = businessType;
    }

    public Long getSourceDocumentId()
    {
        return sourceDocumentId;
    }

    public void setSourceDocumentId(Long sourceDocumentId)
    {
        this.sourceDocumentId = sourceDocumentId;
    }

    public OdFlowInstance getOdFlowInstance()
    {
        return odFlowInstance;
    }

    public void setOdFlowInstance(OdFlowInstance odFlowInstance)
    {
        this.odFlowInstance = odFlowInstance;
    }

    public Inputable getText()
    {
        return text;
    }

    public void setText(Inputable text)
    {
        this.text = text;
    }

    public String getTextType()
    {
        return textType;
    }

    public void setTextType(String textType)
    {
        this.textType = textType;
    }

    public BusinessModel getBusiness() throws Exception
    {
        if (business == null)
        {
            Integer businessId = getBusinessId();
            if (businessId != null && business == null)
                business = dao.getBusiness(businessId);
        }

        return business;
    }

    public Long getDocumentId() throws Exception
    {
        if (odFlowInstance != null)
        {
            if (odFlowInstance.getDocumentId() != null)
                return odFlowInstance.getDocumentId();
            else if (odFlowInstance.getDocument() != null)
                return odFlowInstance.getDocument().getDocumentId();
        }

        return null;
    }

    public OfficeDocument getDocument() throws Exception
    {
        if (odFlowInstance != null)
        {
            if (odFlowInstance.getDocument() == null && odFlowInstance.getDocumentId() != null)
            {
                odFlowInstance.setDocument(dao.getDocument(odFlowInstance.getDocumentId()));
            }

            return odFlowInstance.getDocument();
        }

        return null;
    }

    @Override
    public Integer getIeFlowId() throws Exception
    {
        return getBusiness().getFlowId();
    }

    @Override
    public String getFlowTag() throws Exception
    {
        if (flowTag == null)
        {
            flowTag = getBusiness().getType().getType();

            if ("unionseal".equals(flowTag))
                flowTag = "union";
        }
        return flowTag;
    }

    @Override
    public void setIeFlowId(Integer ieFlowId)
    {
        //ieFlowId由businessId决定，不允许修改
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getIeFormId() throws Exception
    {
        return getBusiness().getFormId();
    }

    @Override
    public void setIeFormId(Integer ieFormId)
    {
        //ieFormId由businessId决定，不允许修改
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFlowId(Integer flowId)
    {
        //flowId由businessId决定，不允许修改
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFormId(Integer formId)
    {
        //formId由businessId决定，不允许修改
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFlowTag(String flowTag)
    {
        //flowTag由businessId决定，不允许修改
        throw new UnsupportedOperationException();
    }

    @Override
    protected void initScriptContext(FlowContext flowContext) throws Exception
    {
        super.initScriptContext(flowContext);

        BusinessModel business = getBusiness();
        if (business.getSendFormId() != null)
        {
            Integer sendFormId = FormApi.getLastFormId(business.getSendFormId());

            if (sendFormId != null)
            {
                FormContext sendFormcContext = FormApi.newFormContext(sendFormId);
                flowContext.getScriptContext()
                        .addVariableContainer(new MapVariableContainer(sendFormcContext.getContext()));
            }
        }
    }

    @Override
    protected void initFlowContext(FlowContext flowContext) throws Exception
    {
        super.initFlowContext(flowContext);

        flowContext.getStep().setReceiver(flowContext.getInstance().getCreator());
        flowContext.getStep().setProperty("deptId", businessContext.getDeptId());

        if ("receive".equals(getFlowTag()))
        {
            //收文都不放草稿箱
            flowContext.getInstance().setState(0);
            flowContext.getStep().setState(FlowStep.NODEAL);
        }
    }

    @Override
    @Transactional
    public void start() throws Exception
    {
        if (odFlowInstance != null && odFlowInstance.getDocument() != null)
        {
            //同步标题
            setTitle(odFlowInstance.getDocument().getTitle());
        }

        super.start();

        //创建公文流转信息
        if (odFlowInstance == null)
            odFlowInstance = createOdFlowInstance();

        initOdFlowInstance(odFlowInstance);

        OfficeDocument document = null;

        if (odFlowInstance.getDocumentId() == null)
        {
            document = odFlowInstance.getDocument();
            if (document == null)
            {
                if (sourceDocumentId == null)
                {
                    //创建公文信息
                    document = new OfficeDocument();
                }
                else
                {
                    OfficeDocument sourceDocument = dao.getDocument(sourceDocumentId);

                    document = dao.copyDocument(sourceDocument);
                }
            }

            document.setCreateDeptId(getBusinessContext().getBusinessDeptId());

            initDocument(document);
            if (document.getDocumentId() == null)
            {
                if (text != null)
                {
                    DocumentText documentText = new DocumentText();
                    documentText.setTextBody(text.getInputStream());
                    documentText.setType(textType);
                    documentText.setFileSize(text.size());

                    dao.add(documentText);
                    document.setTextId(documentText.getTextId());
                }

                dao.add(document);
            }
            else
            {
                dao.update(document);
            }

            if (odFlowInstance.getDocumentId() == null)
                odFlowInstance.setDocumentId(document.getDocumentId());
        }

        dao.add(odFlowInstance);

        if (document != null && odFlowInstance.getDocument() == null)
            odFlowInstance.setDocument(document);

        ((OdSystemFlowDao) getSystemFlowDao()).refreshStepQ(getInstanceId());
    }

    protected OdFlowInstance createOdFlowInstance() throws Exception
    {
        return new OdFlowInstance();
    }

    protected void initOdFlowInstance(OdFlowInstance odFlowInstance) throws Exception
    {
        odFlowInstance.setInstanceId(getInstanceId());
        odFlowInstance.setBusinessId(getBusinessId());
        odFlowInstance.setDeptId(getBusinessContext().getBusinessDeptId());
        odFlowInstance.setType(getFlowTag());
        odFlowInstance.setState(OdFlowInstanceState.notStarted);
        odFlowInstance.setStartTime(new Date());
        odFlowInstance.setCreator(getBusinessContext().getUserId());
        odFlowInstance.setCreateDeptId(getBusinessContext().getDeptId());
        odFlowInstance.setSimpleName(getBusiness().getSimpleName());

        if ("send".equals(getFlowTag()) || ("inner".equals(getFlowTag()) && StringUtils.isEmpty(
                getBusiness().getComponentType())))
        {
            odFlowInstance.setDealer(getBusinessContext().getUserId());
            odFlowInstance.setDealDeptId(getBusinessContext().getDeptId());
        }
    }

    protected void initDocument(OfficeDocument document) throws Exception
    {
        if (getTitle() != null && document.getTitle() == null)
        {
            //设置公文标题
            document.setTitle(getTitle());
        }

        if ("send".equals(odFlowInstance.getType()))
        {
            document.setSourceDept(getBusinessContext().getBusinessDept().getAllName(1));

            SimpleDeptInfo dept = getBusinessContext().getBusinessDept();
            String sourceDeptCode;
            if (!StringUtils.isEmpty(dept.getOrgCode()))
                sourceDeptCode = dept.getOrgCode();
            else
                sourceDeptCode = dept.getDeptCode();

            document.setSourceDeptCode(sourceDeptCode);
        }
    }
}
