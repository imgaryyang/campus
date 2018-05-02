package com.gzzm.portal.cms.channel;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.wordnumber.WordNumber;
import com.gzzm.portal.cms.commons.CmsConfig;
import com.gzzm.portal.cms.information.InformationComponents;
import com.gzzm.portal.cms.template.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.crud.*;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 栏目维护
 *
 * @author camel
 * @date 2011-3-2
 */
@Service(url = "/portal/channel")
public class ChannelCrud extends BaseTreeCrud<Channel, Integer>
{
    static final EntityTreeOrganizer ORGANIZER = new NestedSetTreeOrganizer("leftValue", "rightValue", "orderId");

    @Inject
    private ChannelDao dao;

    @Inject
    private CmsConfig config;

    @Inject
    private PageTemplateDao templateDao;

    /**
     * 栏目树
     */
    private ChannelTreeModel channelTree;

    /**
     * 关联的栏目树
     */
    private ChannelTreeModel relatedChannelTree;

    /**
     * 关联的栏目树
     */
    private ChannelTreeModel linkChannelTree;

    /**
     * 临时变量，初始化栏目的编码的时候使用
     *
     * @see #initEntity(Channel)
     */
    private String oldTopCode;

    private String newTopCode;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 发布关联的栏目id数组
     */
    private Integer[] relatedChannelIds;

    /**
     * 网站上关联的栏目id数组
     */
    private Integer[] linkChannelIds;

    public ChannelCrud()
    {
        setLog(true);
        setDuplicateChildren(true);

        setOrganizer(ORGANIZER);
    }

    public Integer[] getRelatedChannelIds()
    {
        return relatedChannelIds;
    }

    public void setRelatedChannelIds(Integer[] relatedChannelIds)
    {
        this.relatedChannelIds = relatedChannelIds;
    }

    public Integer[] getLinkChannelIds()
    {
        return linkChannelIds;
    }

    public void setLinkChannelIds(Integer[] linkChannelIds)
    {
        this.linkChannelIds = linkChannelIds;
    }

    @Override
    protected String getParentField() throws Exception
    {
        return "parentChannelId";
    }

    @Override
    public Channel getRoot() throws Exception
    {
        return dao.getRootChannel();
    }

    @Override
    public void initEntity(Channel entity) throws Exception
    {
        super.initEntity(entity);

        if (entity.getType() == null)
            entity.setType(ChannelType.information);
        if (entity.getType().equals(ChannelType.information))
            entity.setGenerateIndex(true);
        else
        {
            entity.setGenerateIndex(false);
        }
        String channelCode = entity.getChannelCode();
        if (oldTopCode != null && channelCode != null && channelCode.startsWith(oldTopCode))
        {
            entity.setChannelCode(newTopCode + channelCode.substring(oldTopCode.length()));
        }
        else
        {
            oldTopCode = channelCode;
            newTopCode = initChannelCode(entity.getParentChannelId());
            entity.setChannelCode(newTopCode);
        }

        entity.setPropertyInherited(true);
    }

    /**
     * 初始化栏目编号
     *
     * @param parentChannelId 父栏目对象
     * @return 新栏目的编号
     * @throws Exception 数据库操作或查询错误
     */
    @Service
    @ObjectResult
    public String initChannelCode(Integer parentChannelId) throws Exception
    {
        return dao.initChannelCode(parentChannelId);
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    public void setString(Channel channel, String channelName) throws Exception
    {
        channel.setChannelName(channelName);
    }

    @NotSerialized
    @Select(field = "entity.page")
    public List<KeyValue<String>> getPages()
    {
        if (config != null)
            return config.getInformationPages();

        return null;
    }

    @Override
    public Channel clone(Channel entity) throws Exception
    {
        Channel c = super.clone(entity);

        if (entity.getProperties() != null)
            c.setProperties(new HashMap<String, String>(entity.getProperties()));

        List<InfoProperty> infoProperites = entity.getInfoProperties();
        if (infoProperites != null)
        {
            List<InfoProperty> infoProperties2 = new ArrayList<InfoProperty>(infoProperites.size());

            for (InfoProperty infoProperty : infoProperites)
            {
                InfoProperty infoProperty2 = new InfoProperty();
                infoProperty2.setEnumValues(infoProperty.getEnumValues());
                infoProperty2.setNullable(infoProperty.isNullable());
                infoProperty2.setPropertyName(infoProperty.getPropertyName());
                infoProperty2.setShowName(infoProperty.getShowName());

                infoProperties2.add(infoProperty2);
            }

            c.setInfoProperties(infoProperties2);
        }


        return c;
    }

    @Override
    public boolean beforeUpdate() throws Exception
    {
        super.beforeUpdate();

        Channel channel = getEntity();

        if (channel.getProperties() == null)
            channel.setProperties(new HashMap<String, String>(0));

        if (channel.getChannelId() > 0)
        {
            //修改了栏目编号，更新子栏目的栏目编号
            String oldChannelCode = dao.getChannelCode(channel.getChannelId());
            if (!oldChannelCode.equals(channel.getChannelCode()))
                dao.updateDescendantChannelCode(oldChannelCode, channel.getChannelCode());
        }

        if (channel.isGenerateIndex() == null)
            channel.setGenerateIndex(false);

        if (channel.isPropertyInherited() == null)
            channel.setPropertyInherited(false);

        if (channel.isReGenerateOrder() == null)
            channel.setReGenerateOrder(false);

        if (channel.isPublishTimeEditable() == null)
            channel.setPublishTimeEditable(false);

        if (channel.isGenOrderByTime() == null)
            channel.setGenOrderByTime(false);

        if (channel.getCataloged() == null)
            channel.setCataloged(false);
        return true;
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        if (relatedChannelIds == null)
        {
            if (!isNew$())
            {
                getEntity().setRelatedChannels(new ArrayList<Channel>(0));
            }
        }
        else
        {
            List<Channel> relatedChannels = new ArrayList<Channel>(relatedChannelIds.length);
            for (Integer relatedChannelId : relatedChannelIds)
            {
                Channel relatedChannel = new Channel();
                relatedChannel.setChannelId(relatedChannelId);

                relatedChannels.add(relatedChannel);
            }

            getEntity().setRelatedChannels(relatedChannels);
        }

        if (linkChannelIds == null)
        {
            if (!isNew$())
            {
                getEntity().setLinkChannels(new ArrayList<Channel>(0));
            }
        }
        else
        {
            List<Channel> linkChannels = new ArrayList<Channel>(linkChannelIds.length);
            for (Integer linkChannelId : linkChannelIds)
            {
                Channel linkChannel = new Channel();
                linkChannel.setChannelId(linkChannelId);

                linkChannels.add(linkChannel);
            }

            getEntity().setLinkChannels(linkChannels);
        }

        return super.beforeSave();
    }

    @Override
    public void afterChange() throws Exception
    {
        Channel.setUpdated();
    }

    @Override
    @Forward(page = "/portal/cms/channel/channel.ptl")
    public String add(Integer parentKey, String forward) throws Exception
    {
        return super.add(parentKey, forward);
    }

    @Override
    @Forward(page = "/portal/cms/channel/channel.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/portal/cms/channel/channel.ptl")
    public String duplicate(Integer key, String forward) throws Exception
    {
        return super.duplicate(key, forward);
    }

    @Override
    public void afterLoad() throws Exception
    {
        List<Channel> relatedChannels = getEntity().getRelatedChannels();
        if (relatedChannels != null)
        {
            int n = relatedChannels.size();
            relatedChannelIds = new Integer[n];
            for (int i = 0; i < n; i++)
            {
                Channel relatedChannelId = relatedChannels.get(i);
                relatedChannelIds[i] = relatedChannelId.getChannelId();
            }
        }

        List<Channel> linkChannels = getEntity().getLinkChannels();
        if (linkChannels != null)
        {
            int n = linkChannels.size();
            linkChannelIds = new Integer[n];
            for (int i = 0; i < n; i++)
            {
                Channel linkChannel = linkChannels.get(i);
                linkChannelIds[i] = linkChannel.getChannelId();
            }
        }
        super.afterLoad();
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        if ("exp".equals(getAction()))
        {
            PageTreeTableView view = new PageTreeTableView();

            view.setRootVisible(false);
            view.addColumn("栏目名称", "channelName");
            view.addColumn("栏目编号", "channelCode");
            return view;
        }
        else
        {
            PageTreeView view = new PageTreeView();

            view.addComponent("栏目名称", "text");
            view.addButton(Buttons.query());

            view.defaultInit();
            view.addButton("栏目赋权", "editAuth();");
            view.addButton(Buttons.export("xls"));
            view.addButton(Buttons.copy());
            view.addButton(Buttons.paste());

            if (userOnlineInfo.isAdmin())
                view.addButton("初始化", "initTree();");

            view.addButton("栏目URL初始化", "initChannelURL();");

            view.importJs("/portal/cms/channel/channel.js");

            return view;
        }
    }

    @Override
    @Transactional
    public void move(Integer key, Integer parentKey) throws Exception
    {
        //移动节点之前修改节点及子栏目的编号
        String oldChannlCode = dao.getChannelCode(key);
        String newChannelCode = initChannelCode(parentKey);
        dao.updateDescendantChannelCode(oldChannlCode, newChannelCode);

        super.move(key, parentKey);
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("栏目列表");
    }

    @Override
    protected String getSearchCondition() throws Exception
    {
        return "channelName like :searchText or channelCode like :searchText";
    }

    @Override
    public String toString(Channel channel) throws Exception
    {
        String code = channel.getChannelCode();
        String name = channel.getChannelName();
        if (StringUtils.isEmpty(code))
            return name;
        else
            return "[" + code + "]" + name;
    }

    @NotSerialized
    @Select(field = "entity.channelTemplateId")
    public List<PageTemplate> getChannelTemplates() throws Exception
    {
        return templateDao.getPageTemplates(getEntity().getStationId(), PageTemplateType.CHANNEL);
    }

    @NotSerialized
    @Select(field = "entity.infoTemplateId")
    public List<PageTemplate> getInfoTemplates() throws Exception
    {
        return templateDao.getPageTemplates(getEntity().getStationId(), PageTemplateType.INFO);
    }

    @Select(field = "entity.linkChannelId")
    public ChannelTreeModel getChannelTree()
    {
        if (channelTree == null)
            channelTree = new ChannelTreeModel();

        return channelTree;
    }

    @Select(field = "relatedChannelIds")
    public ChannelTreeModel getRelatedChannelTree()
    {
        if (relatedChannelTree == null)
        {
            relatedChannelTree = new ChannelTreeModel();
            relatedChannelTree.setShowBox(true);
            if (getEntity() != null && getEntity().getChannelId() != null)
                relatedChannelTree.addExcludedChannel(getEntity().getChannelId());
        }

        return relatedChannelTree;
    }

    @Select(field = "linkChannelIds")
    public ChannelTreeModel getLinkChannelTree()
    {
        if (linkChannelTree == null)
        {
            linkChannelTree = new ChannelTreeModel();
            linkChannelTree.setShowBox(true);
            if (getEntity() != null && getEntity().getChannelId() != null)
                linkChannelTree.addExcludedChannel(getEntity().getChannelId());
        }

        return linkChannelTree;
    }

    @NotSerialized
    @Select(field = "entity.componentType")
    public List<KeyValue<String>> getComponents()
    {
        return InformationComponents.getComponents();
    }

    /**
     * 获得标题图片
     *
     * @param channelId 栏目ID
     * @return 标题图片的字节数组
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/portal/channel/{$0}/photo")
    public byte[] getPhoto(Integer channelId) throws Exception
    {
        return getEntity(channelId).getPhoto();
    }

    /**
     * 删除标题图片
     *
     * @param channelId 栏目ID
     * @throws Exception 操作数据库错误
     */
    @Service(url = "/portal/channel/{$0}/photo", method = HttpMethod.delete)
    public void deletePhoto(Integer channelId) throws Exception
    {
        Channel channel = new Channel();
        channel.setChannelId(channelId);
        channel.setPhoto(Null.ByteArray);
        update(channel);
    }

    @NotSerialized
    public String getIndexCodeText() throws Exception
    {
        Channel entity = getEntity();
        if (entity != null && !StringUtils.isEmpty(entity.getIndexCode()))
        {
            WordNumber wordNumber = new WordNumber(entity.getIndexCode());

            return wordNumber.toString();
        }

        return null;
    }

    @Service
    @Transactional
    public void initTree() throws Exception
    {
        super.initTree(0);
    }

    /**
     * 栏目url初始化
     *
     * @param currentChannelId 栏目Id
     * @throws Exception 操作数据库错误
     */
    @Service
    @Transactional
    public void initChannelUrl(Integer currentChannelId) throws Exception
    {
        Channel channel = dao.getChannel(currentChannelId);

        String parentUrl = initParentUrl(channel);
//        非根节点时
        if (channel.getChannelId() != 0)
        {
            initCurrentChannelUrl(channel, parentUrl);
        }
    }

    /**
     * 获取父栏目的url,名称长度小于等于6个字时，全拼;大于6个字时，前面6个全拼 + 后面的首字母
     *
     * @param channel 当前栏目
     * @return
     */
    private String initParentUrl(Channel channel)
    {
        String currentUrl = "";

        if (channel.getParentChannelId() != null && channel.getParentChannelId() == 0)
        {
            return currentUrl;
        }

        StringBuilder parentUrl = new StringBuilder();

        Channel parentChannel = channel.getParentChannel();

        if (parentChannel != null)
        {
            currentUrl = "/" + Chinese.getFirstLetters(parentChannel.getChannelName());

            String parentName = parentChannel.getChannelName();

            if (parentChannel.getParentChannelId() != null && parentChannel.getParentChannelId() == 0)
            {

                if (parentName.length() > 6)
                {
                    currentUrl = "/" + Chinese.getLetters(parentName.substring(0, 6)) +
                            Chinese.getFirstLetters(parentName.substring(6));
                }
                else
                {
                    currentUrl = "/" + Chinese.getLetters(parentName);
                }

                return currentUrl;
            }

            parentUrl.append(currentUrl);
            parentUrl.insert(0, initParentUrl(parentChannel));
            return parentUrl.toString();
        }
        return currentUrl;
    }

    /**
     * 设置当前栏目与子栏目的url为: parentUrl + "/" + 栏目拼音首字母;
     * 当前栏目为门户节点时，若名称长度小于等于6个字，全拼;大于6个字，前面6个全拼 + 后面的首字母
     *
     * @param currentChannel 当前栏目
     * @param parentUrl      父栏目的url
     * @return
     * @throws Exception 操作数据库错误
     */
    public String initCurrentChannelUrl(Channel currentChannel, String parentUrl) throws Exception
    {
        String currentUrl = "";

        if (currentChannel.getParentChannelId() != null && currentChannel.getParentChannelId() == 0)
        {
            String currentName = currentChannel.getChannelName();
            if (currentName.length() > 6)
            {
                currentUrl = "/" + Chinese.getLetters(currentName.substring(0, 6)) +
                        Chinese.getFirstLetters(currentName.substring(6));
            }
            else
            {
                currentUrl = "/" + Chinese.getLetters(currentName);
            }
        }
        else
        {
            currentUrl = parentUrl + "/" + Chinese.getFirstLetters(currentChannel.getChannelName());
        }

        currentChannel.setUrl(currentUrl);
        update(currentChannel);

        List<Channel> channelList = dao.getChildChannels(currentChannel.getChannelId(), null);

        if (channelList.size() > 0)
        {
            for (Channel channel : channelList)
            {
                initCurrentChannelUrl(channel, currentUrl);
            }
        }
        return currentUrl;
    }

}
