package com.gzzm.portal.cms.information;

import com.gzzm.platform.annotation.ConfigValue;
import com.gzzm.platform.commons.Tools;
import com.gzzm.portal.cms.channel.Channel;
import com.gzzm.portal.cms.commons.CmsPage;
import com.gzzm.portal.cms.information.visit.InfoVisitRecordService;
import com.gzzm.portal.cms.station.Station;
import com.gzzm.portal.cms.station.StationDao;
import com.gzzm.portal.cms.template.PageTemplate;
import net.cyan.activex.OfficeUtils;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.IOUtils;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.DownloadFile;
import net.cyan.commons.util.io.Mime;
import net.cyan.commons.util.io.WebUtils;
import net.cyan.nest.annotation.Inject;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 显示文章，在网站上展示文章内容
 *
 * @author camel
 * @date 2011-6-9
 */
@Service(url = "/web/info")
public class InformationPage extends CmsPage {
    @Inject
    private InformationDao dao;

    @Inject
    private StationDao stationDao;

    private Long informationId;

    /**
     * 页面号，默认从1开始
     */
    private int pageNo;

    /**
     * 文章信息，临时变量，通过此变量可以获得更多信息
     */
    private InformationBase<?, ?> informationObject;

    private Channel channelObject;

    /**
     * 当前页面所属的站点，临时变量，避免站点对象的重复加载
     */
    private Station stationObject;

    /**
     * 文章信息
     */
    private InformationInfo information;

    /**
     * 信息实际展示的栏目可以和信息真正所在的栏目不一样
     */
    private Integer channelId;

    /**
     * 栏目编号
     */
    private String channelCode;

    /**
     * 使用的模板ID
     */
    private Integer templateId;

    private boolean preview;

    @ConfigValue(name = "PORTAL_INFORMATION_TOHTML", defaultValue = "true")
    private Boolean toHtml;

    private String contentType;

    public InformationPage() {
    }

    public Long getInformationId() {
        return informationId;
    }

    public void setInformationId(Long informationId) {
        this.informationId = informationId;
    }

    public Integer getChannelId() throws Exception {
        if (channelId == null) {
            if (channelCode != null) {
                channelObject = dao.getChannelByCode(channelCode);
                channelId = channelObject.getChannelId();
            } else {
                channelId = getInformationObject().getChannelId();
            }
        }

        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelCode() throws Exception {
        if (channelCode == null) {
            channelCode = getChannelObject().getChannelCode();
        }

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

    public boolean isPreview() {
        return preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public Boolean getToHtml() {
        return toHtml;
    }

    public void setToHtml(Boolean toHtml) {
        this.toHtml = toHtml;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    protected InformationBase<?, ?> getInformationObject() throws Exception {
        if (informationObject == null) {
            if (informationId != null) {
                if (preview) {
                    informationObject = dao.getInformationEdit(informationId);
                } else {
                    informationObject = dao.getInformation(informationId);
                    if (informationObject != null && informationObject.isValid() != null &&
                            !informationObject.isValid())
                        informationObject = null;
                }
            }
        }

        return informationObject;
    }

    @NotSerialized
    public InformationInfo getInformation() throws Exception {
        if (information == null)
            information = new InformationInfo(getInformationObject(), getChannelObject(), pageNo);

        return information;
    }

    @Override
    protected Station getStationObject() throws Exception {
        if (stationObject == null)
            stationObject = dao.getStation(getChannelObject().getStationId());

        return stationObject;
    }

    @Override
    protected Channel getChannelObject() throws Exception {
        if (channelObject == null)
            channelObject = dao.getChannel(getChannelId());

        return channelObject;
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
            PageTemplate template = channel.getInfoTemplate();
            if (template != null)
                return template;

            channel = channel.getParentChannel();
        }

        return null;
    }

    @Override
    protected String getForward() throws Exception {
        if (templateId != null) {
            PageTemplate template = dao.getPageTemplate(templateId);
            if (template != null) return template.getPath();
        }

        Channel channel = getChannelObject();
        while (channel != null) {
            PageTemplate template = channel.getInfoTemplate();
            if (template != null) return template.getPath();

            channel = channel.getParentChannel();

            Station station = stationDao.getStationByChannelId(channel.getChannelId());
            if (station != null && station.getTemplate() != null && StringUtils.isNotBlank(station.getTemplate().getTextPath()))
                return station.getTemplate().getTextPath();
        }

        return null;
    }

    @Service(url = "/channel/{channelCode}/info/{informationId}")
    public String showByCode() throws Exception {
        return show();
    }

    @Service(url = "/channel/{channelCode}/info/{informationId}/preview")
    public String previewByCode() throws Exception {
        return preview();
    }

    @Service(url = "/info/{informationId}/preview")
    public String preview() throws Exception {
        preview = true;
        return show();
    }

    @Service(url = "/info/{informationId}")
    public String show() throws Exception {
        InformationBase<?, ?> information = getInformationObject();

        if (information == null) {
            RequestContext.getContext().sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        //更新阅读次数
        if (!preview) {
            dao.updateReadTimes(informationId);
            //添加访问记录
            InfoVisitRecordService.addInfoVisitRecord(information.getInformationId());
        }

        if (information.getType() == null) {
            //信息发布，转向显示信息的页面
            return super.show();
        } else {
            switch (information.getType()) {
                case information:

                    //信息发布，转向显示信息的页面
                    String page = super.show();

                    if (StringUtils.isEmpty(page) && preview)
                        page = "/portal/cms/information/preview.ptl";

                    return page;

                case images:
                    //信息发布，转向显示信息的页面
                    String imagesPage = super.show();
                    if (StringUtils.isEmpty(imagesPage) && preview)
                        imagesPage = "/portal/cms/information/preview.ptl";
                    return imagesPage;

                case file:
                    //文件，下载文件
                    InformationFileBase<?> file = information.getFile();
                    RequestContext context = RequestContext.getContext();

                    if (toHtml != null && toHtml) {
                        String ext = IOUtils.getExtName(file.getFileName());

                        String s = null;
                        if (OfficeUtils.canChangeToHtml(ext)) {
                            s = OfficeUtils.toHtml(file.getContent(), ext);
                        } else if (ext != null && "pdf".equalsIgnoreCase(ext)) {
                            if (preview)
                                s = Tools.getPdfViewUrl("/info/" + informationId + "/preview?toHtml=false");
                            else
                                s = Tools.getPdfViewUrl("/info/" + informationId + "?toHtml=false");
                        }

                        if (s != null) {
                            RequestContext.getContext().redirect(s);
                            break;
                        } else {
                            contentType = "application/pdf";
                        }
                    }

                    WebUtils.download(new DownloadFile(file.getContent(), file.getFileName(), contentType, false),
                            context.getResponse(), context.getRequest());

                    break;

                case url:
                    //url，转向url
                    RequestContext.getContext().redirect(information.getLinkUrl());
                    break;
            }
        }

        return null;
    }

    /**
     * 获得标题图片
     *
     * @return 标题图片的字节数组
     * @throws Exception 从数据库读取数据错误
     */
    @Service(url = {"/info/{informationId}/photo", "/info/{informationId}/photo.jpg",
            "/info/{informationId}/photo.gif", "/info/{informationId}/photo.png", "/info/{informationId}/photo.JPG",
            "/info/{informationId}/photo.GIF", "/info/{informationId}/photo.PNG"})
    public InputFile getPhoto() throws Exception {
        InformationBase<?, ?> information = getInformationObject();
        byte[] photo = information.getPhoto();

        if (photo == null)
            return null;

        String extName = information.getExtName();
        if (StringUtils.isEmpty(extName))
            extName = "jpg";

        return new DownloadFile(photo, "photo_" + informationId + "." + extName,
                Mime.getContentTypeForExt(extName), false);
    }

    @Override
    protected int getVisitType() {
        return -1;
    }

    @Override
    protected Integer getVisitId() {
        return null;
    }

    @NotSerialized
    public Long getNextInformationId() throws Exception {
        return dao.getNextInformationId(informationId);
    }

    @NotSerialized
    public Long getPrevInformationId() throws Exception {
        return dao.getPrevInformationId(informationId);
    }

    public List<Information> getRelatedInfos() {
        return information.getRelatedInformationList();
    }
}
