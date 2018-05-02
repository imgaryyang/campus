package com.gzzm.portal.cms.information;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.appauth.*;
import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.Authoritys;
import com.gzzm.platform.organ.*;
import com.gzzm.portal.cms.channel.*;
import com.gzzm.portal.cms.commons.*;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.exts.ParameterCheck;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.importers.*;
import net.cyan.crud.util.JoinType;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.*;
import net.cyan.image.ImageZoomer;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 信息采编和发布
 *
 * @author camel
 * @date 2011-5-17
 */
@SuppressWarnings("NullableProblems")
@Service(url = "/portal/information")
public class InformationCrud extends InformationCrudBase<InformationEdit>
        implements OwnedCrud<InformationEdit, Long, Integer>
{
    static
    {
        ParameterCheck.addNoCheckURL("/portal/information");
    }

    @Inject
    private static Provider<FileUploadService> uploadServiceProvider;


    /**
     * 默认的标题图片最大大小，以K为单位，在PFCONFIG表中配置
     */
    @ConfigValue(name = "PLINFORMATION_MAXPHOTOSIZE", defaultValue = "" + IOUtils.K * 50)
    private Integer maxPhotoSize;

    @ConfigValue(name = "PLINFORMATION_ZIPPHOTOSIZE", defaultValue = "false")
    private Boolean zipPhoto;

    @ConfigValue(name = "PLINFORMATION_ZIPPHOTOWIDTH", defaultValue = "800")
    private Integer photoZipWidth;

    @ConfigValue(name = "PLINFORMATION_ZIPPHOTOHEIGHT", defaultValue = "600")
    private Integer photoZipHeight;

    @Inject
    protected InformationService service;

    @Inject
    private AppAuthService appAuthService;

    @Inject
    private ChannelTree channelTree;

    @Inject
    private CmsConfig config;

    //新增开始
    @Inject
    private AttachmentDao attachmentDao;

    @Inject
    private InformationDao informationDao;

    private Long attachmentId;

    @MenuId
    private String menuId;

    /**
     * 拥有权限的栏目的id
     */
    @NotSerialized
    private AppDeptList appDepts;

    /**
     * 所有子栏目的id，当当前为根栏目时，返回null
     */
    @NotSerialized
    private List<Integer> allChildrenChannelIds;

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    /**
     * 栏目ID，信息所属的栏目
     * 由于要求查询此栏目下所有子栏目的信息，因此不让其自动生成查询条件
     */
    @NotCondition
    private Integer channelId;

    /**
     * 根栏目ID
     */
    private Integer topChannelId;

    /**
     * 根栏目编号
     */
    private String topChannelCode;

    /**
     * 编辑类型，采编或者发布
     */
    private EditType editType;

    @Like
    private String title;

    @Lower(column = "updateTime")
    private java.sql.Date updateTime_start;

    @Upper(column = "updateTime")
    private java.sql.Date updateTime_end;

    @NotCondition
    private Integer deptId;

    private boolean includeSubDepts;

    /**
     * 接收上传的文件
     */
    private InputFile file;

    private InformationCatalogTreeModel catalogTree;

    private Channel channel;

    @NotSerialized
    private Collection<Integer> channelAuthDeptIds;

    private boolean channelAuthDeptIdsLoaded;

    private AuthDeptTreeModel channelDeptTree;

    private InformationComponent component;

    private Map<String, String[]> enumProperties;

    private PublishPeriod period;

    private InformationState state;

    private AuthChannelTreeModel authChannelTree;

    private Integer[] channelIds;

    private boolean cataloged;

    private String encodedAttachmentId;

    public InformationCrud()
    {
        addForceLoad("channel");
        addForceLoad("createUser");
    }

    public Integer getChannelId()
    {
        return channelId;
    }

    public void setChannelId(Integer channelId)
    {
        this.channelId = channelId;
    }

    public Integer getTopChannelId()
    {
        return topChannelId;
    }

    public void setTopChannelId(Integer topChannelId)
    {
        this.topChannelId = topChannelId;
    }

    public String getTopChannelCode()
    {
        return topChannelCode;
    }

    public void setTopChannelCode(String topChannelCode)
    {
        this.topChannelCode = topChannelCode;
    }

    public PublishPeriod getPeriod()
    {
        return period;
    }

    public void setPeriod(PublishPeriod period)
    {
        this.period = period;
    }

    public EditType getEditType()
    {
        return editType;
    }

    public void setEditType(EditType editType)
    {
        this.editType = editType;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public java.sql.Date getUpdateTime_start()
    {
        return updateTime_start;
    }

    public void setUpdateTime_start(java.sql.Date updateTime_start)
    {
        this.updateTime_start = updateTime_start;
    }

    public java.sql.Date getUpdateTime_end()
    {
        return updateTime_end;
    }

    public void setUpdateTime_end(java.sql.Date updateTime_end)
    {
        this.updateTime_end = updateTime_end;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public boolean isIncludeSubDepts()
    {
        return includeSubDepts;
    }

    public void setIncludeSubDepts(boolean includeSubDepts)
    {
        this.includeSubDepts = includeSubDepts;
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    public InformationState getState()
    {
        return state;
    }

    public void setState(InformationState state)
    {
        this.state = state;
    }

    public Integer[] getChannelIds()
    {
        return channelIds;
    }

    public void setChannelIds(Integer[] channelIds)
    {
        this.channelIds = channelIds;
    }

    public boolean isCataloged()
    {
        return cataloged;
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public String getEncodedAttachmentId()
    {
        return encodedAttachmentId;
    }

    @NotSerialized
    public String getChannelNames() throws Exception
    {
        if (channelIds == null)
            return null;

        StringBuilder builder = new StringBuilder();
        for (Integer channelId : channelIds)
        {
            Channel channel = service.getDao().getChannel(channelId);

            if (builder.length() > 0)
                builder.append(",");

            builder.append(channel.getChannelName());
        }

        return builder.toString();
    }

    @Select(field = "channelIds")
    public AuthChannelTreeModel getAuthChannelTree() throws Exception
    {
        if (authChannelTree == null)
        {
            authChannelTree = Tools.getBean(AuthChannelTreeModel.class);
            authChannelTree.setEditType(editType);
            authChannelTree.setHasCheckBox(true);
        }
        return authChannelTree;
    }

    public void setAuthChannelTree(AuthChannelTreeModel authChannelTree)
    {
        this.authChannelTree = authChannelTree;
    }

    @NotSerialized
    public boolean isPublishTimeEditable() throws Exception
    {
        Channel channel = getChannel();

        return channel != null && channel.isPublishTimeEditable() != null && channel.isPublishTimeEditable() &&
                (channel.getPeriod() == null || channel.getPeriod() == PublishPeriod.NULL);
    }

    @NotSerialized
    public boolean isShowPublishPeriod() throws Exception
    {
        Channel channel = getChannel();

        return channel.getPeriod() != null && channel.getPeriod() != PublishPeriod.NULL;
    }

    public boolean isGenerateIndex() throws Exception
    {
        Channel channel = getChannel();

        return channel != null && channel.isGenerateIndex() != null && channel.isGenerateIndex();
    }

    @NotSerialized
    public Channel getChannel() throws Exception
    {
        if (channel == null)
        {
            if (getEntity() != null && getEntity().getChannelId() != null)
                channel = service.getDao().getChannel(getEntity().getChannelId());
            else if (channelId != null)
                channel = service.getDao().getChannel(channelId);
        }

        return channel;
    }

    @NotSerialized
    public Map<String, String[]> getEnumProperties()
    {
        InformationEdit entity = getEntity();

        if (entity == null)
            return null;

        if (enumProperties == null)
        {
            enumProperties = new Map<String, String[]>()
            {
                public int size()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public boolean isEmpty()
                {
                    return false;
                }

                public boolean containsKey(Object key)
                {
                    return getEntity().getProperties().containsKey(key);
                }

                public boolean containsValue(Object value)
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public String[] get(Object key)
                {
                    Map<String, String> properties = getEntity().getProperties();

                    if (properties == null)
                        getEntity().setProperties(properties = new HashMap<String, String>());

                    String s = properties.get(key);

                    return s == null ? null : s.split("\\|");
                }

                public String[] put(String key, String[] value)
                {
                    if (value != null)
                    {
                        String s = StringUtils.concat(value, "|");

                        getEntity().getProperties().put(key, s);
                    }

                    return null;
                }

                public String[] remove(Object key)
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public void putAll(Map<? extends String, ? extends String[]> m)
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public void clear()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public Set<String> keySet()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public Collection<String[]> values()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }

                public Set<Entry<String, String[]>> entrySet()
                {
                    throw new UnsupportedOperationException("Method not implemented.");
                }
            };
        }

        return enumProperties;
    }

    public void setEnumProperties(Map<String, String[]> enumProperties)
    {
        this.enumProperties = enumProperties;
    }

    public InformationComponent getComponent() throws Exception
    {
        if (component == null)
        {
            Channel channel = getChannel();

            if (channel != null)
            {
                String componentType = channel.getComponentType();

                if (!StringUtils.isEmpty(componentType))
                {
                    component = (InformationComponent) Tools.getBean(Class.forName(componentType));
                }
            }
        }

        return component;
    }

    public Collection<Integer> getChannelAuthDeptIds() throws Exception
    {
        return getChannelAuthDeptIds(getEntity() != null);
    }

    public Collection<Integer> getChannelAuthDeptIds(boolean b) throws Exception
    {
        if (!channelAuthDeptIdsLoaded)
        {
            if (authDeptIds != null)
            {
                if (b)
                {
                    Integer channelId = this.channelId;

                    if (getEntity() != null && getEntity().getChannelId() != null)
                        channelId = getEntity().getChannelId();

                    channelAuthDeptIds = appAuthService.getDeptIdsForAuth(editType == EditType.edit ?
                                    ChannelAuthCrud.PORTAL_CHANNEL_EDIT : ChannelAuthCrud.PORTAL_CHANNEL_PUBLISH,
                            channelId, userOnlineInfo.getUserId(), userOnlineInfo.getDeptId(), authDeptIds
                    );
                }
                else
                {
                    AppDeptList appDepts = getAppDepts();
                    if (appDepts.getAppDepts() != null)
                    {
                        channelAuthDeptIds = new HashSet<Integer>();
                        for (AppDept appDept : appDepts.getAppDepts())
                        {
                            Integer deptId = appDept.getDeptId();
                            if (deptId == null)
                            {
                                channelAuthDeptIds = null;
                                break;
                            }
                            channelAuthDeptIds.add(deptId);
                        }
                    }
                    else
                    {
                        channelAuthDeptIds = getCrudService().getDao()
                                .sqlQuery("select distinct deptId from " + appDepts.getTableName(), Integer.class);
                        if (channelAuthDeptIds.contains(null))
                            channelAuthDeptIds = null;
                    }
                }
            }

            if (channelAuthDeptIds != null && channelAuthDeptIds.size() == 0 && userOnlineInfo.isAdmin())
                channelAuthDeptIds = null;


            channelAuthDeptIdsLoaded = true;
        }

        return channelAuthDeptIds;
    }

    @Select(field = {"entity.deptId", "deptId"})
    public AuthDeptTreeModel getChannelDeptTree() throws Exception
    {
        if (channelDeptTree == null)
        {
            channelDeptTree = new AuthDeptTreeModel();
            channelDeptTree.setAuthDeptIds(getChannelAuthDeptIds());
            channelDeptTree.setFull(true);
        }

        return channelDeptTree;
    }

    @Select(field = "entity.catalogId")
    public InformationCatalogTreeModel getCatalogTree() throws Exception
    {
        if (catalogTree == null)
        {
            catalogTree = Tools.getBean(InformationCatalogTreeModel.class);
        }
        return catalogTree;
    }

    public List<Integer> getAllChildrenChannelIds() throws Exception
    {
        if (allChildrenChannelIds == null && channelId != null && channelId != 0)
        {
            allChildrenChannelIds = channelTree.getChannel(channelId).getAllChildrenChannelIds();
        }

        return allChildrenChannelIds;
    }

    public AppDeptList getAppDepts() throws Exception
    {
        if (appDepts == null)
        {
            appDepts = appAuthService.getAppDepts(userOnlineInfo.getUserId(), userOnlineInfo.getDeptId(), authDeptIds,
                    editType == EditType.edit ? ChannelAuthCrud.PORTAL_CHANNEL_EDIT :
                            ChannelAuthCrud.PORTAL_CHANNEL_PUBLISH, getAllChildrenChannelIds()
            );
        }

        return appDepts;
    }

    @NotSerialized
    @Select(field = "entity.periodTime")
    public List<PublishPeriodTime> getPeriodTimes() throws Exception
    {
        return PublishPeriodTime.getTimes(getChannel().getPeriod());
    }

    /**
     * 当前部门的ID
     *
     * @return 当前部门的ID
     */
    private Integer getDefaultDeptId() throws Exception
    {
        return DeptOwnedCrudUtils.getDefaultDeptId(getChannelAuthDeptIds(true), this);
    }

    @Override
    public String createCondition() throws Exception
    {
        String s = super.createCondition();
        if (userOnlineInfo.isSelf(menuId))
        {
            if (StringUtils.isEmpty(s))
                return "i.creator=:userId";
            else
                return "(" + s + ") and i.creator=:userId";
        }

        return s;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        if (StringUtils.isNotBlank(topChannelCode))
        {
            Channel channel = service.getDao().getChannelByCode(topChannelCode);
            if (channel != null)
                topChannelId = channel.getChannelId();
        }

        super.beforeShowList();
    }

    @Override
    protected void beforeQuery() throws Exception
    {
        super.beforeQuery();

        if (channelId == null)
            channelId = topChannelId;

        if (editType == EditType.edit)
        {
            addOrderBy("updateTime", OrderType.desc);
        }
        else
        {
            addOrderBy("case when i.state=0 and i.updateTime>addday(sysdate(),-7) then 0 else 1 end");
            addOrderBy("case when i.state=0 and i.updateTime>addday(sysdate(),-7) then i.updateTime else null end");
            addOrderBy("updateTime", OrderType.desc);
            addOrderBy("informationId", OrderType.desc);
        }

        if (authDeptIds != null)
        {
            //只允许操作有权限的栏目和部门
            AppDeptList appDepts = getAppDepts();

            //不能操作任何部门的数据，跳出
            if (appDepts.getAppDepts() != null)
            {
                if (appDepts.getAppDepts().size() > 0)
                {
                    join(":appDepts.appDepts", "a", "a.appId=i.channelId and (a.deptId is null or a.deptId=i.deptId)",
                            JoinType.inner);
                }
            }
            else
            {
                join("[" + appDepts.getTableName() + "]", "a",
                        "a.appId=i.channelId and (a.deptId=0 or a.deptId=i.deptId)", JoinType.inner);
            }
        }

        if (includeSubDepts && !Null.isNull(deptId))
        {
            join("Dept", "d", "d.deptId=i.deptId");
            join("Dept", "d2", "d.leftValue>=d2.leftValue and d.leftValue<d2.rightValue");
        }
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        super.beforeInsert();

        InformationEdit information = getEntity();

        if (information.getChannelId() == null)
            information.setChannelId(getChannelId());

        if (information.getDeptId() == null)
            information.setDeptId(getDefaultDeptId());

        information.setCreator(userOnlineInfo.getUserId());
        information.setCreateTime(new Date());
        information.setPublished(false);

        if (information.getType() == null)
            information.setType(InformationType.information);

        return true;
    }

    @Override
    public Long save() throws Exception
    {
        return super.save();
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        checkCataloged(getChannel());

        super.beforeSave();

        InformationEdit information = getEntity();

        information.setAttachmentId(attachmentId);

        byte[] photo = information.getPhoto();
        if (photo != null && maxPhotoSize != null && maxPhotoSize > 0 && photo.length > maxPhotoSize)
        {
            if (zipPhoto != null && zipPhoto && photoZipWidth != null && photoZipWidth > 0 && photoZipHeight != null &&
                    photoZipHeight > 0)
            {
                ImageZoomer zoomer = new ImageZoomer(photoZipWidth, photoZipHeight, true);
                zoomer.read(photo);
                information.setPhoto(zoomer.toBytes("jpg"));
            }
            else
            {
                throw new NoErrorException("portal.cms.titlePhotoSizeExceeded", IOUtils.getSizeString(maxPhotoSize));
            }
        }

        information.setUpdateTime(new Date());

        //修改之后将状态变为未发布
        information.setState(InformationState.editing);

        //保存发布周期类型
        information.setPeriod(channel.getPeriod() == null ? PublishPeriod.NULL : channel.getPeriod());

        //扩展功能
        InformationComponent component = getComponent();
        if (component != null)
        {
            RequestContext.getContext().fillForm(component, "component");

            if (!component.beforeSave(information, isNew$(), this))
                return false;
        }

        return true;
    }

    @Override
    public void afterSave() throws Exception
    {
        super.afterSave();

        InformationEdit entity = getEntity();

        if (file != null)
        {
            InformationFileEdit informationFile = new InformationFileEdit();
            informationFile.setInformationId(entity.getInformationId());
            informationFile.setFileName(file.getName());
            informationFile.setContent(file.getInputStream());
            service.getDao().save(informationFile);
        }

        InformationComponent component = getComponent();
        if (component != null)
        {
            component.afterSave(entity, isNew$(), this);
        }
        else if (channelIds != null)
        {
            List<Integer> channelIds = new ArrayList<Integer>();

            if (this.channelIds != null && this.channelIds.length > 0)
            {
                channelIds.addAll(Arrays.asList(this.channelIds));
            }
            else
            {
                channelIds.add(entity.getChannelId());
            }

            if (!channelIds.contains(entity.getChannelId()))
            {
                entity.setChannelId(channelIds.get(0));
                service.getDao().update(entity);
            }

            List<InformationEdit> informations = null;

            if (!isNew$())
            {
                informations = service.getDao().getInformationEditsByMainInformationId(entity.getInformationId());
            }

            for (Integer channelId : channelIds)
            {
                if (!channelId.equals(entity.getChannelId()))
                {
                    InformationEdit entity1 = clone(entity);
                    entity1.setChannelId(channelId);
                    entity1.setMainInformationId(entity.getInformationId());
                    entity1.setUpdateTime(new Date());
                    entity1.setState(InformationState.editing);

                    if (entity.getPublishTime() != null &&
                            (getChannel().isPublishTimeEditable() != null && getChannel().isPublishTimeEditable()))
                    {
                        Channel channel = service.getDao().getChannel(channelId);

                        if (channel.isPublishTimeEditable() != null && channel.isPublishTimeEditable())
                        {
                            entity1.setPublishTime(entity.getPublishTime());
                        }
                    }

                    if (informations != null)
                    {
                        for (Iterator<InformationEdit> iterator = informations.iterator(); iterator.hasNext(); )
                        {
                            InformationEdit entity2 = iterator.next();
                            if (entity2.getChannelId().equals(channelId))
                            {
                                entity1.setInformationId(entity2.getInformationId());

                                iterator.remove();
                                break;
                            }
                        }
                    }

                    service.getDao().save(entity1);
                }
            }

            if (informations != null)
            {
                for (InformationEdit information : informations)
                {
                    if (information.isPublished() == null || !information.isPublished())
                        service.getDao().delete(information);
                }
            }
        }

        if (!StringUtils.isEmpty(entity.getOrgCode()))
        {
            OrgCode orgCode = service.getDao().getOrgCode(entity.getDeptId());
            if (orgCode == null)
            {
                orgCode = new OrgCode();
                orgCode.setDeptId(entity.getDeptId());
            }

            orgCode.setOrgCode(entity.getOrgCode());
            service.getDao().save(orgCode);
        }
    }

    @Override
    public void initEntity(InformationEdit entity) throws Exception
    {
        super.initEntity(entity);
        entity.setAuthor(userOnlineInfo.getUserName());
        entity.setType(InformationType.information);
        entity.setDeptId(getDefaultDeptId());

        service.initInformation(entity);

        if (entity.getChannelId() == null)
        {
            entity.setChannelId(channelId);
            entity.setChannel(getChannel());
        }

        InformationComponent component = getComponent();
        if (component != null)
        {
            component.init(entity, this);
        }

        channelIds = new Integer[]{entity.getChannelId()};
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        InformationEdit information = getEntity();

        if (information.getType() == null)
            information.setType(InformationType.information);

        InformationComponent component = getComponent();
        if (component != null)
        {
            component.load(information, this);
        }

        List<InformationEdit> informations =
                service.getDao().getInformationEditsByMainInformationId(information.getInformationId());

        if (informations.size() == 0)
        {
            channelIds = new Integer[]{information.getChannelId()};
        }
        else
        {
            List<Integer> channelIds = new ArrayList<Integer>();
            channelIds.add(information.getChannelId());

            for (InformationEdit information1 : informations)
            {
                if (!channelIds.contains(information1.getChannelId()) &&
                        (information1.getChannel().getDeleteTag() == null ||
                                information1.getChannel().getDeleteTag() == 0))
                {
                    channelIds.add(information1.getChannelId());
                }
            }

            this.channelIds = channelIds.toArray(new Integer[channelIds.size()]);
        }

        if (StringUtils.isEmpty(information.getOrgName()))
        {
            Dept dept = information.getDept();
            if (dept == null)
            {
                Integer deptId = getDefaultDeptId();
                dept = service.getDao().getDept(deptId);
                information.setDeptId(deptId);
            }

            information.setOrgName(dept.getDeptName());
        }

        Channel channel = information.getChannel();
        if (channel.getCataloged() != null && channel.getCataloged())
            cataloged = true;

        //当有attachmentId时
        if (information.getAttachmentId() != null)
        {
            attachmentId = information.getAttachmentId();
            encodedAttachmentId = Attachment.encodeId(attachmentId);
        }
    }

    @Override
    public InformationEdit clone(InformationEdit entity) throws Exception
    {
        InformationEdit c = super.clone(entity);

        c.setChannel(entity.getChannel());
        c.setCreateTime(new Date());
        c.setState(InformationState.editing);
        c.setPublishTime(null);
        c.setPublished(false);
        c.setPublisher(null);
        c.setOrderId(null);
        c.setIndexCode(null);
        c.setUpdateTime(null);
        c.setPhoto(entity.getPhoto());
        c.setValid(null);
        c.setTopmost(null);
        c.setLinkId(null);

        c.setPhoto(entity.getPhoto());
        c.setPhotoFilePath(null);

        //复制扩展属性
        c.setProperties(new HashMap<String, String>(entity.getProperties()));

        InformationComponent component = getComponent();
        if (component != null)
            component.clone(c, entity, this);

        return c;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        String s = "channel is not null";
        if (authDeptIds != null)
        {
            //只允许操作有权限的栏目和部门
            AppDeptList appDepts = getAppDepts();

            //不能操作任何部门的数据，跳出
            if (appDepts.getAppDepts() != null)
            {
                if (appDepts.getAppDepts().size() == 0)
                    return s + " and false";
            }
        }

        s += " and channel.deleteTag=0";
        if (channelId != null && !Null.isNull(channelId))
        {
            //查询此栏目的所有子栏目
            s += " and channel.leftValue>=:channel.leftValue and channel.leftValue<:channel.rightValue";
        }

        if (!Null.isNull(deptId))
        {
            if (includeSubDepts)
            {
                s += " and d2.deptId=:deptId";
            }
            else
            {
                s += " and i.deptId=:deptId";
            }
        }

        return s;
    }

    @Override
    protected void loadList() throws Exception
    {
        super.loadList();

        for (InformationEdit informationEdit : getList())
        {
            loadExtraData(informationEdit);
        }
    }

    @Override
    public String add(String forward) throws Exception
    {
        super.add(forward);
        getEntity().setPublishTime(new Date());
        return getForward(getChannel());
    }

    @Override
    public String duplicate(Long key, String forward) throws Exception
    {
        super.duplicate(key, forward);

        return getForward(getChannel());
    }

    @Override
    public String show(Long key, String forward) throws Exception
    {
        InformationEdit information = service.getDao().getInformationEdit(key);
        if (information.getMainInformationId() != null)
        {
            InformationEdit mainInformation = service.getDao().getInformationEdit(information.getMainInformationId());
            if (mainInformation != null)
                key = mainInformation.getInformationId();
        }

        super.show(key, forward);

        return getForward(getChannel());
    }

    protected String getForward(Channel channel) throws Exception
    {
        if (!StringUtils.isEmpty(channel.getPage()))
        {
            return channel.getPage();
        }

        InformationComponent component = getComponent();
        if (component != null)
        {
            String page = component.page();
            if (!StringUtils.isEmpty(page))
                return page;
        }

        return "default";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = createListView0();
        if ("exp".equals(getAction()))
        {
            initExportView(view);
            return view;
        }
        view.importJs("/portal/cms/information/information.js");

        initListView(view);

        return view;
    }

    protected void initExportView(PageTableView view) throws Exception
    {
        view.addColumn("所属栏目", "channel.channelName");
        view.addColumn("索引号", "indexCode");
        view.addColumn("标题", "title").setWidth("250").setAutoExpand(true);
        view.addColumn("采编部门", "dept.allName()");
        view.addColumn("采编人", "createUser.userName");
        view.addColumn("发布时间", new FieldCell("publishTime").setFormat("yyyy-MM-dd"));
        view.addColumn("排序号", "orderId");
    }

    /**
     * 允许子类重新此方法，构建不同的ChannelDisplay，可以在子类中对栏目进一步过滤
     *
     * @return 左边的栏目树
     * @throws Exception
     */
    protected ChannelDisplay createChannelDisplay() throws Exception
    {
        return Tools.getBean(ChannelDisplay.class);
    }

    protected void initChannelDisplay(ChannelDisplay channelDisplay) throws Exception
    {
        channelDisplay.setEditType(editType);
        channelDisplay.setTopChannelId(topChannelId);
        channelDisplay.setPeriod(period);
    }

    protected PageTableView createListView0() throws Exception
    {
        PageTableView view;

        if (channelId == null)
        {
            ChannelDisplay channelDisplay = createChannelDisplay();

            initChannelDisplay(channelDisplay);

            view = new ComplexTableView(channelDisplay, "channelId", true);
        }
        else
        {
            view = new PageTableView(true);
        }

        return view;
    }

    protected void initListView(PageTableView view) throws Exception
    {
        view.addComponent("标题", "title");
        addUpdateTimeComponent(view);
        view.addComponent("采编部门", "deptId");
        view.addComponent("包括下属部门", new CCombox("includeSubDepts").setNullable(false));
        view.addComponent("状态", "state");

        view.addColumn("所属栏目", "channel.channelName").setOrderFiled("channel.leftValue").setWidth("150");
        view.addColumn("标题", "title").setWidth("200").setAutoExpand(true);
        view.addColumn("采编人", "createUser.userName").setWidth("80");
        view.addColumn("更新时间", new FieldCell("updateTime").setFormat("yyyy-MM-dd HH:mm")).setWidth("135");
        view.addColumn("采编部门", "dept.allName()").setWidth("150").setOrderFiled("dept.leftValue");
        view.addColumn("状态", "state");

        defaultInitListView(view);
        view.addColumn("关联", new CButton("关联", "relateOtherInfo(${informationId})"));
    }

    protected void setCheckable(PageTableView view)
    {
        if (editType == EditType.edit)
        {
            view.setCheckable("${(state.toString()=='editing'&&(published!=null&&!published||publishTime==null))" +
                    "&&(channel.cataloged==null||!channel.cataloged)}");
        }
        else
        {
            view.setCheckable("${state.toString()=='editing'&&(channel.cataloged==null||!channel.cataloged)}");
        }
    }

    protected void addPreviewColumn(PageTableView view)
    {
        view.addColumn("预览", new CHref("预览").setAction("preview(${informationId})")).setWidth("40");
    }

    protected void addValidColumn(PageTableView view)
    {
        view.addColumn("有效", new ConditionComponent().add("valid!=null&&published!=null&&published",
                new CCheckbox("valid").setProperty("onclick", "setValid(${informationId},this.checked)")
                        .setProperty("disabled",
                                "${channel.publishTimeEditable==null||!channel.publishTimeEditable||channel.cataloged!=null&&channel.cataloged}")))
                .setWidth("40");
    }

    protected void addTopmostColumn(PageTableView view)
    {
        if (Authoritys.hasAuth("topmost"))
        {
            //拥有置顶的权限
            view.addColumn("置顶", new ConditionComponent().add("state.name()=='published'", new CCheckbox("topmost").
                    setProperty("onclick", "setTopmost(${informationId},this.checked)")
                    .setProperty("disabled", "${channel.cataloged!=null&&channel.cataloged}"))).setWidth("50")
                    .setOrderFiled("topmost");
        }
    }

    protected void addOrderColumn(PageTableView view)
    {
        if (Authoritys.hasAuth("order"))
        {
            //拥有排序的权限
            view.addColumn("排序号", new ConditionComponent().add("channel.cataloged==null||!channel.cataloged",
                    new CHref("${orderId}").setAction("setOrderId(${informationId},${orderId})"))
                    .add(new CLabel("${orderId}")))
                    .setWidth("60").setAlign(Align.center);
        }
    }

    protected void addDeleteColumn(PageTableView view)
    {
        if (CrudAuths.isDeletable())
        {
            view.addColumn("删除", new ConditionComponent().add("channel.cataloged==null||!channel.cataloged",
                    new CButton("删除", "deleteInformation(${informationId})"))).setWidth("50");
        }
    }

    protected void addUpdateTimeComponent(PageTableView view)
    {
        if (editType != EditType.edit)
        {
            view.addComponent("更新时间", "updateTime_start", "updateTime_end");
        }
    }

    protected void defaultInitListView(PageTableView view)
    {
        setCheckable(view);

        if (editType == EditType.edit)
        {
            view.defaultInit(true);

            view.addButton(Buttons.imp());
            view.addButton(Buttons.getButton("导入word", "", "doc", "impFiles"));

            addPreviewColumn(view);
        }
        else
        {
            view.addButton(Buttons.query());
            view.addButton("发布", "publish()");
            view.addButton(Buttons.export("xls"));
            view.makeEditable();

            addPreviewColumn(view);

            addValidColumn(view);

            addTopmostColumn(view);
            addOrderColumn(view);

            addDeleteColumn(view);
        }
    }

    /**
     * 删除标题图片
     *
     * @param informationId 信息ID
     * @throws Exception 操作数据库错误
     */
    @Service(url = "/portal/information/{$0}/photo", method = HttpMethod.delete)
    public void deletePhoto(Long informationId) throws Exception
    {
        InformationEdit information = getEntity(informationId);
        information.setInformationId(informationId);
        information.setPhoto(Null.ByteArray);
        update(information);
    }

    /**
     * 下载文件
     *
     * @param informationId 信息ID
     * @return 信息对应的文件，如果对应的文件不存在，返回null
     * @throws Exception 数据库读取数据错误
     */
    @Service(url = "/portal/information/{$0}/file")
    public InputFile downFile(Long informationId) throws Exception
    {
        InformationFileEdit file = service.getDao().getInformationFileEdit(informationId);

        if (file == null)
            return null;

        return new InputFile(file.getContent(), file.getFileName());
    }

    /**
     * 发布信息
     *
     * @throws Exception 数据库操作错误
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public void publish() throws Exception
    {
        for (Long informationId : getKeys())
        {
            checkCataloged(informationId);

            publish(informationId);
        }
    }

    private void publish(Long informationId) throws Exception
    {
        service.publish(informationId, userOnlineInfo.getUserId());

        InformationEdit informationEdit = service.getDao().getInformationEdit(informationId);
        Channel channel = informationEdit.getChannel();

        if (StringUtils.isEmpty(channel.getComponentType()))
        {
            List<Channel> relatedChannels = channel.getRelatedChannels2();
            if (relatedChannels != null && relatedChannels.size() > 0)
            {
                List<InformationEdit> informations =
                        service.getDao().getInformationEditsBySourceInformationId(informationId);

                for (Channel relatedChannel : relatedChannels)
                {
                    InformationEdit informationEdit1 = clone(informationEdit);
                    informationEdit1.setChannelId(relatedChannel.getChannelId());
                    informationEdit1.setSourceInformationId(informationEdit.getInformationId());
                    informationEdit1.setUpdateTime(new Date());
                    informationEdit1.setState(InformationState.editing);

                    if (informations != null)
                    {
                        for (InformationEdit informationEdit2 : informations)
                        {
                            if (informationEdit2.getChannelId().equals(relatedChannel.getChannelId()))
                            {
                                informationEdit1.setInformationId(informationEdit2.getInformationId());
                                break;
                            }
                        }
                    }

                    service.getDao().save(informationEdit1);
                }
            }
        }
    }

    @Service
    @ObjectResult
    @Transactional
    public void setValid(Long informationId, Boolean valid) throws Exception
    {
        checkCataloged(informationId);

        Information information = new Information();
        information.setInformationId(informationId);
        information.setValid(valid);
        service.getDao().update(information);

        InformationEdit informationEdit = new InformationEdit();
        informationEdit.setInformationId(informationId);
        informationEdit.setValid(valid);
        service.getDao().update(informationEdit);

        PageCache.updateCache();
    }

    @Service
    @ObjectResult
    @Transactional
    public void setTopmost(Long informationId, Boolean topmost) throws Exception
    {
        Information information = new Information();
        information.setInformationId(informationId);
        information.setTopmost(topmost);
        service.getDao().update(information);

        InformationEdit informationEdit = new InformationEdit();
        informationEdit.setInformationId(informationId);
        informationEdit.setTopmost(topmost);
        service.getDao().update(informationEdit);

        PageCache.updateCache();
    }

    @Service
    @ObjectResult
    @Transactional
    public void setOrderId(Long informationId, Integer orderId) throws Exception
    {
        Information information = new Information();
        information.setInformationId(informationId);
        information.setOrderId(orderId);
        service.getDao().update(information);

        InformationEdit informationEdit = new InformationEdit();
        informationEdit.setInformationId(informationId);
        informationEdit.setOrderId(orderId);
        service.getDao().update(informationEdit);

        PageCache.updateCache();
    }

    @Service
    @ObjectResult
    public void deleteInformation(Long informationId) throws Exception
    {
        checkCataloged(informationId);

        service.getDao().deleteInformation(informationId);
    }

    public String getOwnerField()
    {
        return "channelId";
    }

    public Integer getOwnerKey(InformationEdit entity) throws Exception
    {
        return entity.getChannelId();
    }

    public void setOwnerKey(InformationEdit entity, Integer ownerKey) throws Exception
    {
        entity.setChannelId(ownerKey);
        entity.setState(InformationState.editing);
    }

    public void copyAllTo(Long[] keys, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        OwnedCrudUtils.copyTo(Arrays.asList(keys), newOwnerKey, oldOwnerKey, this);
    }

    public void copyTo(Long key, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        OwnedCrudUtils.copyTo(Collections.singleton(key), newOwnerKey, oldOwnerKey, this);
    }

    public void moveAllTo(Long[] keys, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        OwnedCrudUtils.moveTo(Arrays.asList(keys), newOwnerKey, oldOwnerKey, this);
    }

    public void moveTo(Long key, Integer newOwnerKey, Integer oldOwnerKey) throws Exception
    {
        OwnedCrudUtils.moveTo(Collections.singleton(key), newOwnerKey, oldOwnerKey, this);
    }

    @Service
    @ObjectResult
    public String getDeptName(Integer deptId) throws Exception
    {
        return service.getDao().getDept(deptId).allName();
    }

    @Service
    @ObjectResult
    public String getDeptCode(Integer deptId) throws Exception
    {
        Dept dept = service.getDao().getDept(deptId);
        String code = dept.getOrgCode();
        if (StringUtils.isEmpty(code))
            code = dept.getDeptCode();

        if (!StringUtils.isEmpty(code))
            code = code.replace("-", "");

        return code;
    }

    /**
     * 加载用于列表显示的扩展数据，用于子类使用
     *
     * @param informationEdit 要加载扩展数据的信息
     * @throws Exception 允许子类抛出一次
     */
    @SuppressWarnings("UnusedDeclaration")
    protected void loadExtraData(InformationEdit informationEdit) throws Exception
    {
    }

    @Service
    @NotSerialized
    public boolean isSupportedImport() throws Exception
    {
        InformationComponent component = getComponent();

        return component != null && component.isSupportedImport(this);
    }

    @Service
    @NotSerialized
    public boolean isSupportedImportFiles() throws Exception
    {
        Channel channel = getChannel();
        if (channel == null || channel.getType() != null && channel.getType() != ChannelType.information)
            return false;

        InformationComponent component = getComponent();

        return component == null;
    }

    @Override
    protected CrudEntityImportor<InformationEdit, Long> getCrudEntityImportor() throws Exception
    {
        return new CrudEntityImportor<InformationEdit, Long>()
        {
            @Override
            protected void fill(InformationEdit entity, DataRecord record) throws Exception
            {
                getComponent().importFill(record, entity, InformationCrud.this);
            }
        };
    }

    @Override
    public void imp() throws Exception
    {
        InformationComponent component = getComponent();

        if (!component.imp(getImp(), this))
        {
            super.imp();
        }
    }

    @Service(method = HttpMethod.post)
    public void impFiles(String[] filePaths) throws Exception
    {
        FileUploadService fileUploadService = uploadServiceProvider.get();

        for (String filePath : filePaths)
        {
            InputFile file = fileUploadService.getFile(filePath);
            InformationEdit information = new InformationEdit();
            information.setCreator(userOnlineInfo.getUserId());
            information.setDeptId(getDefaultDeptId());

            try
            {
                service.impFile(file, Collections.singleton(channelId), information);
            }
            finally
            {
                try
                {
                    fileUploadService.deleteFile(filePath);
                }
                catch (Throwable ex)
                {
                    //删除临时文件错误，跳过
                }
            }
        }
    }

    private void checkCataloged(Long informationId) throws Exception
    {
        InformationEdit informationEdit = service.getDao().getInformationEdit(informationId);
        checkCataloged(informationEdit.getChannel());
    }

    private void checkCataloged(Channel channel)
    {
        if (channel.getCataloged() != null && channel.getCataloged())
        {
            throw new NoErrorException("portal.cms.channelCataloged");
        }
    }


    /**
     * 批量添加文件
     *
     * @param filePaths 要添加的文件的临时目录
     * @return 保存后的附件attachmentId和encodeAttachmentId
     * @throws Exception 数据库错误或者IO错误
     */
    @Service(method = HttpMethod.post)
    @Transactional
    public Tuple<Long, String> uploadFiles(String[] filePaths, Long attachmentId) throws Exception
    {
        FileUploadService fileUploadService = uploadServiceProvider.get();
        try
        {
            List<InputFile> files = new ArrayList<InputFile>(filePaths.length);
            for (String filePath : filePaths)
            {
                InputFile file = fileUploadService.getFile(filePath);
                files.add(file);
            }

            if (attachmentId == null)
                attachmentId = getEntity().getAttachmentId();

            List<Attachment> attachments = new ArrayList<Attachment>(files.size());

            for (InputFile file : files)
            {
                Attachment attachment = new Attachment();

                attachment.setAttachmentName(file.getName());
                attachment.setFileName(file.getName());
                attachment.setUserId(userOnlineInfo.getUserId());
                attachment.setInputable(file.getInputable());
                attachment.setDeptId(deptId);
                attachment.setTag("portal");
                attachments.add(attachment);
            }

            AttachmentSaver saver = Tools.getBean(AttachmentSaver.class);
            saver.setAttachmentId(attachmentId);
            saver.setAttachments(attachments);

            saver.save();

            if (attachmentId == null)
            {
                attachmentId = saver.getAttachmentId();
            }

            return new Tuple<Long, String>(attachmentId, Attachment.encodeId(attachmentId));
        }
        finally
        {
            if (filePaths != null)
            {
                for (String filePath : filePaths)
                {
                    try
                    {
                        fileUploadService.deleteFile(filePath);
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
     * 后台预览时，读取图片列表的attachmentNo
     *
     * @param attachmentId 附件列表ID
     * @return 图片的attachmentNo列表
     * @throws Exception
     */
    @ObjectResult
    public List<String> showImages(Long attachmentId) throws Exception
    {
        List<Attachment> attachmentList = attachmentDao.getAttachments(attachmentId);
        List<String> results = new ArrayList<String>(attachmentList.size());
        for (Attachment attachment : attachmentList)
        {
            results.add(attachment.getAttachmentNo().toString());
        }

        return results;
    }

    /**
     * 删除某个图片
     *
     * @param attachmentId 图片列表ID
     * @param attachmentNo 图片编号
     * @throws Exception
     */
    @Service
    @Transactional
    public void deleteImageByNo(Long attachmentId, Integer attachmentNo) throws Exception
    {
        attachmentDao.deleteAttachment(attachmentId, attachmentNo);
    }

    /**
     * 删除所有图片
     *
     * @param attachmentId 图片列表对应的附件ID
     * @throws Exception
     */
    @Service
    @Transactional
    public void clearImages(Long attachmentId) throws Exception
    {
        List<Attachment> attachmentList = attachmentDao.getAttachments(attachmentId);
        for (Attachment attachment : attachmentList)
        {
            attachmentDao.deleteAttachment(attachmentId, attachment.getAttachmentNo());
        }
    }

    /**
     * 对图片进行排序
     *
     * @param attachmentId  图片列表ID
     * @param attachmentNos 图片编号列表，按顺序排序
     * @throws Exception
     */
    @Service
    @Transactional
    public void sortImages(Long attachmentId, Integer[] attachmentNos) throws Exception
    {
        int orderId = 0;
        for (Integer attachmentNo : attachmentNos)
        {
            Attachment attachment = new Attachment();
            attachment.setAttachmentId(attachmentId);
            attachment.setAttachmentNo(attachmentNo);
            attachment.setOrderId(++orderId);

            attachmentDao.update(attachment);
        }
    }
}