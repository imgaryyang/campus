package com.gzzm.portal.cms.channel;

import com.gzzm.platform.commons.Tools;
import com.gzzm.portal.cms.commons.CmsPage;
import com.gzzm.portal.cms.station.Station;
import com.gzzm.portal.cms.station.StationDao;
import com.gzzm.portal.cms.template.PageTemplate;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.IOUtils;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 栏目访问的入口
 *
 * @author camel
 * @date 2011-6-9
 */
@Service(url = "/web/channel")
public class ChannelPage extends CmsPage {
    @Inject
    private ChannelDao dao;

    @Inject
    private StationDao stationDao;

    /**
     * 要访问的栏目的ID
     */
    private Integer channelId;

    /**
     * 栏目编号
     */
    private String channelCode;

    /**
     * 栏目对象，临时变量，用于获得其他数据
     */
    private Channel channel;

    /**
     * 当前栏目所属的站点，临时变量，避免站点对象的重复加载
     */
    private Station station;

    /**
     * 使用的模板ID
     */
    private Integer templateId;

    public ChannelPage() {
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    @Override
    protected Channel getChannelObject() throws Exception {
        if (channel == null) {
            if (channelId != null) {
                channel = dao.getChannel(channelId);
                if (channel != null)
                    channelCode = channel.getChannelCode();
            } else if (channelCode != null) {
                channel = dao.getChannelByCode(channelCode);
                if (channel != null)
                    channelId = channel.getChannelId();
            }

            if (channel != null && channel.getDeleteTag() != null && channel.getDeleteTag() == 1)
                channel = null;
        }

        return channel;
    }

    @Override
    protected Station getStationObject() throws Exception {
        if (station == null) {
            Integer stationId = getChannelObject().getStationId();
            if (stationId != null)
                station = dao.getStation(stationId);
        }

        return station;
    }

    @Override
    protected PageTemplate getPageTemplate() throws Exception {
        if (templateId != null) {
            PageTemplate template = dao.getPageTemplate(templateId);
            if (template != null)
                return template;
        }

        Channel channel = getChannelObject();
        while (channel != null) {
            PageTemplate template = channel.getChannelTemplate();
            if (template != null)
                return template;

            channel = channel.getParentChannel();
        }

        return null;
    }

    @Override
    protected String getForward() throws Exception {
        Channel channel = getChannelObject();

        if (channel.getType() == ChannelType.url) {
            //链接页面，转向url
            String url = channel.getLinkUrl();

            RequestContext.getContext().redirect(url);

            return null;
        } else if (channel.getType() == ChannelType.page) {
            return channel.getLinkUrl();
        }

        if (templateId != null) {
            PageTemplate template = dao.getPageTemplate(templateId);
            if (template != null) return template.getPath();
        }

        while (channel != null) {
            PageTemplate template = channel.getChannelTemplate();
            if (template != null) return template.getPath();

            channel = channel.getParentChannel();

            Station station = stationDao.getStationByChannelId(channel.getChannelId());
            if(station != null && station.getTemplate() != null && StringUtils.isNotBlank(station.getTemplate().getChannelPath()))
                return station.getTemplate().getChannelPath();
        }

        return super.getForward();
    }

    @Service(url = "/channel/{channelCode}")
    public String showByCode() throws Exception {
        Channel channel = getChannelObject();

        if (channel == null) {
            RequestContext.getContext().sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return show();
    }

    /**
     * 获得标题图片
     *
     * @return 标题图片的字节数组
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = "/channel/{channelCode}/photo")
    public byte[] showPhoto() throws Exception {
        Channel channel = getChannelObject();

        File file = new File(Tools.getAppPath("/temp/channel/icon/" + channel.getChannelId() + ".gif"));

        boolean generate = !file.exists() ||
                channel.getLastModified() != null && file.lastModified() < channel.getLastModified().getTime();

        byte[] photo;

        if (generate) {
            photo = channel.getPhoto();
            try {
                IOUtils.bytesToFile(photo, file);
            } catch (IOException ex) {
                //可能由于文件保护无法写文件，跳过
            }
        } else {
            photo = IOUtils.fileToBytes(file);
        }

        return photo;
    }

    @Override
    protected int getVisitType() {
        return CHANNEL;
    }

    @Override
    protected Integer getVisitId() {
        return getChannelId();
    }
}
