package com.gzzm.portal.inquiry;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.DeptService;
import com.gzzm.platform.timeout.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * @author camel
 * @date 2014/11/10
 */
@Service
public abstract class BaseInquiryCrud<E> extends DeptOwnedEditableCrud<E, Long>
{
    @Inject
    protected InquiryService service;

    @Inject
    protected DeptService deptService;

    @Inject
    protected TimeoutService timeoutService;

    private Date time_start;

    private Date time_end;

    private String title;

    private String inquirerName;

    private String code;

    private String content;

    private Integer wayId;

    private Integer typeId;

    private Integer catalogId;

    private InquiryCatalogTreeModel catalogTree;

    @NotSerialized
    private List<InquiryWay> inquiryWays;

    /**
     * 警告级别
     */
    private Integer[] timeoutLevelId;

    private String[] timeoutTypeId;

    @Lower
    private java.sql.Date timeoutTime_start;

    @Upper
    private java.sql.Date timeoutTime_end;

    @NotSerialized
    private List<TimeoutConfigInfo> timeoutConfigs;

    private Long lastProcessId;

    private String config;

    private Map<String, String> attributes;

    @NotSerialized
    private InquiryConfig inquiryConfig;

    private PublishType publishType;

    public BaseInquiryCrud()
    {
    }

    public Date getTime_start()
    {
        return time_start;
    }

    public void setTime_start(Date time_start)
    {
        this.time_start = time_start;
    }

    public Date getTime_end()
    {
        return time_end;
    }

    public void setTime_end(Date time_end)
    {
        this.time_end = time_end;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getInquirerName()
    {
        return inquirerName;
    }

    public void setInquirerName(String inquirerName)
    {
        this.inquirerName = inquirerName;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Integer getWayId()
    {
        return wayId;
    }

    public void setWayId(Integer wayId)
    {
        this.wayId = wayId;
    }

    public Integer getTypeId()
    {
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public Integer getCatalogId()
    {
        return catalogId;
    }

    public void setCatalogId(Integer catalogId)
    {
        this.catalogId = catalogId;
    }

    public PublishType getPublishType()
    {
        return publishType;
    }

    public void setPublishType(PublishType publishType)
    {
        this.publishType = publishType;
    }

    public String getConfig()
    {
        return config;
    }

    public void setConfig(String config)
    {
        this.config = config;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }

    public InquiryConfig getInquiryConfig() throws Exception
    {
        if (inquiryConfig == null && !StringUtils.isEmpty(config))
            inquiryConfig = Tools.getBean(InquiryConfig.class, config);

        return inquiryConfig;
    }

    @NotSerialized
    @Select(field = "typeId")
    public List<InquiryType> getInquiryTypes() throws Exception
    {
        return service.getDao().getTypes();
    }

    @Select(field = {"catalogId"})
    public InquiryCatalogTreeModel getCatalogTree()
    {
        if (catalogTree == null)
            catalogTree = new InquiryCatalogTreeModel();

        return catalogTree;
    }

    @Select(field = "wayId")
    public List<InquiryWay> getInquiryWays() throws Exception
    {
        if (inquiryWays == null)
            inquiryWays = service.getDao().getWays();

        return inquiryWays;
    }

    public Integer[] getTimeoutLevelId()
    {
        return timeoutLevelId;
    }

    public void setTimeoutLevelId(Integer[] timeoutLevelId)
    {
        this.timeoutLevelId = timeoutLevelId;
    }

    public String[] getTimeoutTypeId()
    {
        return timeoutTypeId;
    }

    public void setTimeoutTypeId(String[] timeoutTypeId)
    {
        this.timeoutTypeId = timeoutTypeId;
    }

    public Date getTimeoutTime_start()
    {
        return timeoutTime_start;
    }

    public void setTimeoutTime_start(Date timeoutTime_start)
    {
        this.timeoutTime_start = timeoutTime_start;
    }

    public Date getTimeoutTime_end()
    {
        return timeoutTime_end;
    }

    public void setTimeoutTime_end(Date timeoutTime_end)
    {
        this.timeoutTime_end = timeoutTime_end;
    }

    public abstract boolean isTimeLimitEditable();

    public List<TimeoutConfigInfo> getTimeoutConfigs(InquiryProcess process) throws Exception
    {
        if (timeoutConfigs == null || lastProcessId == null || !lastProcessId.equals(process.getProcessId()))
        {
            timeoutConfigs = new ArrayList<TimeoutConfigInfo>(2);
            Integer deptId = process.getDeptId();

            TimeoutConfigInfo accpetConfig =
                    timeoutService.getTimeoutConfig(InquiryTimeout.ACCEPTID, process, deptId);
            if (accpetConfig != null)
                timeoutConfigs.add(accpetConfig);

            TimeoutConfigInfo processConfig =
                    timeoutService.getTimeoutConfig(InquiryTimeout.PROCESSID, process, deptId);
            if (processConfig != null && !timeoutConfigs.contains(processConfig))
                timeoutConfigs.add(processConfig);

            lastProcessId = process.getProcessId();
        }

        return timeoutConfigs;
    }

    @NotSerialized
    public boolean isShowTimeout(InquiryProcess process) throws Exception
    {
        List<TimeoutConfigInfo> configs = getTimeoutConfigs(process);
        return configs != null && configs.size() > 0;
    }

    @NotSerialized
    public boolean isShowTimeout() throws Exception
    {
        return timeoutService.hasTimeoutConfig(InquiryTimeout.PROCESSID, getAuthDeptIds());
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (timeoutLevelId != null)
        {
            if (timeoutTypeId == null)
                timeoutTypeId = new String[]{InquiryTimeout.ACCEPTID, InquiryTimeout.PROCESSID};
        }
    }

    protected abstract Long getInquiryId(E entity) throws Exception;

    @Service
    public InquiryCatalog modifyCatalog(Integer catalogId) throws Exception
    {
        return service.modifyCatalog(getInquiryId(getEntity()), catalogId);
    }

    @Service
    @ObjectResult
    public void modifyPublishType(PublishType type) throws Exception
    {
        service.modifyPublishType(getInquiryId(getEntity()), type);
    }

    protected void initConfig(PageTableView view) throws Exception
    {
        InquiryConfig inquiryConfig = getInquiryConfig();
        if (inquiryConfig != null)
        {
            if (inquiryConfig.getConditions() != null)
            {
                for (InquiryAttribute attribute : inquiryConfig.getConditions())
                {
                    view.addComponent(attribute.getLabel(), "attributes." + attribute.getName());
                }
            }

            if (inquiryConfig.getMore() != null)
            {
                for (InquiryAttribute attribute : inquiryConfig.getMore())
                {
                    view.addMoreComponent(attribute.getLabel(), "attributes." + attribute.getName());
                }
            }

            Class<E> entityType = getEntityType();
            if (inquiryConfig.getColumns() != null)
            {
                for (InquiryAttribute attribute : inquiryConfig.getColumns())
                {
                    String field = "attributes." + attribute.getName();
                    if (entityType == InquiryProcess.class)
                        field = "inquiry." + field;

                    view.addColumn(attribute.getLabel(), field);
                }
            }
        }
    }
}
