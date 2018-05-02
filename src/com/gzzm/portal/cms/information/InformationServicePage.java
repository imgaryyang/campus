package com.gzzm.portal.cms.information;

import com.gzzm.platform.appauth.AppAuthService;
import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.attachment.AttachmentService;
import com.gzzm.platform.commons.crud.SystemCrudUtils;
import com.gzzm.platform.log.LogAction;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.portal.cms.channel.Channel;
import com.gzzm.portal.cms.channel.ChannelAuthCrud;
import com.gzzm.portal.cms.channel.ChannelCache;
import com.gzzm.portal.cms.channel.ChannelTree;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.HtmlUtils;
import net.cyan.commons.util.IOUtils;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.io.Base64;
import net.cyan.commons.util.io.CacheData;
import net.cyan.image.ImageZoomer;
import net.cyan.nest.annotation.Inject;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * @author camel
 * @date 2014/11/14
 */
@Service
public class InformationServicePage
{
    @Inject
    public InformationService service;

    @Inject
    protected UserOnlineInfo userOnlineInfo;

    @Inject
    private AppAuthService appAuthService;

    @Inject
    private ChannelTree channelTree;

    @Inject
    private AttachmentService attachmentService;

    public InformationServicePage()
    {
    }

    @Service(url = "/portal/information/service/publish/{$0}", method = HttpMethod.post)
    public void publish(Long informationId) throws Exception
    {
        service.publish(informationId, userOnlineInfo.getUserId());
    }

    @Transactional
    @Service(url = "/portal/information/service/saveTo/{$0}", method = HttpMethod.post)
    public Long saveTo(InformationEdit information, String channelCode) throws Exception
    {
        Channel channel = service.getDao().getChannelByCode(channelCode);
        if (channel != null)
        {
            information.setChannelId(channel.getChannelId());
        }

        return save(information);
    }

    @Transactional
    @Service(url = "/portal/information/service/save", method = HttpMethod.post)
    public Long save(InformationEdit information) throws Exception
    {
        information.setCreateTime(null);
        information.setState(null);
        information.setPublished(null);
        information.setCreator(null);
        information.setPublisher(null);
        information.setPublishTime(null);
        information.setUpdateTime(new Date());

        information.setDept(null);
        information.setCreateUser(null);
        information.setPublisher(null);
        information.setChannel(null);
        information.setCatalog(null);
        information.setDept(null);
        information.setReadTimes(null);
        information.setFile(null);
        information.setType(InformationType.information);

        information.setUpdateTime(new Date());

        InformationDao dao = service.getDao();

        if (information.getInformationId() != null)
        {
            SimpleInformation simpleInformation = dao.getSimpleInformation(information.getInformationId());

            if (simpleInformation != null)
            {
                List<SimpleInformationContent> contents = simpleInformation.getContents();

                List<InformationContentEdit> contentEdits = new ArrayList<InformationContentEdit>(contents.size());

                for (SimpleInformationContent content : contents)
                {
                    InformationContentEdit contentEdit = new InformationContentEdit();
                    contentEdit.setContent(content.getContent());

                    contentEdits.add(contentEdit);
                }

                information.setContents(contentEdits);

                information.setPhoto(simpleInformation.getPhoto());
                information.setExtName(simpleInformation.getExtName());
            }
        }

        if (information.getContents() != null)
        {
            int pageNo = 0;
            for (InformationContentEdit contentEdit : information.getContents())
            {
                contentEdit.setPageNo(pageNo++);
            }
        }

        if (!dao.save(information))
        {
            information.setCreator(userOnlineInfo.getUserId());
            information.setCreateTime(new Date());
            dao.update(information);
        }
        SystemCrudUtils.saveLog(information, LogAction.modify,null,null);
        return information.getInformationId();
    }

    @Service(url = "/portal/information/service/channelIds?deptId={$0}&type={$1}")
    public List<Integer> getChannelIds(Integer deptId, EditType editType, List<String> channelNames)
            throws Exception
    {
        Collection<Integer> channelIds =
                appAuthService.getAppIds(userOnlineInfo.getUserId(), userOnlineInfo.getDeptId(),
                        Collections.singleton(deptId), editType == EditType.edit ? ChannelAuthCrud.PORTAL_CHANNEL_EDIT :
                                ChannelAuthCrud.PORTAL_CHANNEL_PUBLISH
                );

        List<Integer> result = new ArrayList<Integer>();
        for (Integer channelId : channelIds)
        {
            ChannelCache channel = channelTree.getChannel(channelId);
            if (channel != null && channelNames.contains(channel.getChannelName()))
            {
                result.add(channel.getChannelId());
            }
        }

        return result;
    }

    @Service(url = "/portal/information/service/image/information/{$0}?title={$1}&width={$2}&height={$3}")
    public InputFile downImagesInInformation(Long informationId, String title, int width, int height) throws Exception
    {
        Information information = service.getDao().getInformation(informationId);
        if (title == null)
            title = information.getTitle() + "(采编图片)";

        return downImages(information, title, width, height);
    }

    @Service(url = "/portal/information/service/image/simple/{$0}?title={$1}&width={$2}&height={$3}")
    public InputFile downImagesInSimpleInformation(Long informationId, String title, int width, int height)
            throws Exception
    {
        SimpleInformation information = service.getDao().getSimpleInformation(informationId);

        if (StringUtils.isEmpty(title))
            title = "采编图片";

        return downImages(information, title, width, height);
    }

    private InputFile downImages(InformationBase0<?> information, String title, int width, int height)
            throws Exception
    {
        CacheData cache = new CacheData();

        try
        {
            ImageZoomer zoomer = new ImageZoomer(width, height, true);
            zoomer.setType(ImageZoomer.ZOOM_OUT);

            CompressUtils.Compress zip = CompressUtils.createCompress("zip", cache);

            int imageIndex = 1;

            for (InformationContentBase content : information.getContents())
            {
                for (String url : HtmlUtils.getImgURLs(content.getContentString()))
                {
                    url = url.trim();
                    if (url.startsWith("/attachment/"))
                    {
                        url = url.substring(12);
                        int index = url.indexOf('/');
                        if (index > 0)
                        {
                            String uuid = url.substring(0, index);

                            Attachment attachment = attachmentService.getAttachmentByUUID(uuid);
                            if (attachment != null)
                            {
                                String fileName = attachment.getFileName();
                                String extName = IOUtils.getExtName(fileName);
                                zoomer.setFormat(extName);
                                zoomer.read(attachment.getInputStream());
                                zip.addFile(fileName, new ByteArrayInputStream(zoomer.toBytes()));
                            }
                        }
                    }
                    else if (url.startsWith("data:image/"))
                    {
                        int index = url.indexOf(',');
                        if (index > 0)
                        {
                            String s = url.substring(11, index);
                            if (s.endsWith(";base64"))
                            {
                                String extName = s.substring(0, s.length() - 7);
                                String base64 = url.substring(index + 1);
                                byte[] data = Base64.base64ToByteArray(base64);

                                zoomer.read(data);

                                String fileName = "img" + imageIndex++ + "." + extName;
                                zip.addFile(fileName, new ByteArrayInputStream(zoomer.toBytes(extName)));
                            }
                        }
                    }
                }
            }

            zip.close();

            return new InputFile(cache.getInputStream(), title + ".zip");
        }
        catch (Exception ex)
        {
            //出错的时候清除缓存的文件
            try
            {
                cache.clear();
            }
            catch (Exception ex1)
            {
                //释放资源
            }

            throw ex;
        }
    }
}
