package com.gzzm.portal.cms.information;

import com.gzzm.platform.attachment.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.html.HtmlService;
import com.gzzm.platform.organ.Dept;
import com.gzzm.platform.wordnumber.WordNumber;
import com.gzzm.portal.cms.channel.Channel;
import com.gzzm.portal.cms.commons.PageCache;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 信息采编和信息展示相关的逻辑
 *
 * @author camel
 * @date 13-12-13
 */
public class InformationService
{
    @Inject
    private InformationDao dao;

    @Inject
    private HtmlService htmlService;

    @Inject
    private AttachmentDao attachmentDao;


    public InformationService()
    {
    }

    public InformationDao getDao()
    {
        return dao;
    }

    public void initInformation(InformationBase information) throws Exception
    {
        //设置来源为当前部门的名称
        Dept dept = dao.getDept(information.getDeptId());
        String deptName = dept.allName();

        if (information.getSource() == null)
            information.setSource(deptName);

        if (information.getOrgName() == null)
            information.setOrgName(deptName);

        if (information.getOrgCode() == null)
        {
            if (!StringUtils.isEmpty(dept.getDeptCode()))
            {
                information.setOrgCode(dept.getDeptCode());
            }
            else
            {
                OrgCode orgCode = dao.getOrgCode(information.getDeptId());
                if (orgCode != null)
                    information.setOrgCode(orgCode.getOrgCode());
            }
        }

        information.setDept(dept);
    }

    /**
     * 发布一条信息
     *
     * @param informationId 信息id
     * @param userId        发布的用户
     * @throws Exception 数据库操作错误
     */
    @Transactional
    public void publish(Long informationId, Integer userId) throws Exception
    {
        InformationEdit informationEdit = dao.getInformationEdit(informationId);
        Channel channel = informationEdit.getChannel();

        InformationComponent component = null;
        String componentType = channel.getComponentType();
        if (!StringUtils.isEmpty(componentType))
        {
            component = (InformationComponent) Tools.getBean(Class.forName(componentType));
        }

        Date now = new Date();

        //是否第一次发布
        boolean first = false;

        Date editPublishTime = informationEdit.getPublishTime();
        Date oldPublishTime = null;

        Information information = dao.getInformation(informationId);

        //发布表的attachmentId
        Long publishAttachmentId = null;
        if (information != null)
            publishAttachmentId = information.getAttachmentId();

        if (information == null)
        {
            first = true;

            //信息编辑记录的发布时间
            if (informationEdit.getPublishTime() == null || channel.isPublishTimeEditable() == null ||
                    !channel.isPublishTimeEditable())
            {
                informationEdit.setPublishTime(now);
            }
            informationEdit.setPublisher(userId);

            //第一次发布时设置的信息
            information = new Information();
            information.setInformationId(informationId);
        }
        else
        {
            oldPublishTime = information.getPublishTime();
        }

        information.setDeptId(informationEdit.getDeptId());

        //每次发布时设置的信息
        information.setValid(true);
        if (information.isTopmost() == null)
            information.setTopmost(false);

        information.setTitle(informationEdit.getTitle());
        information.setChannelId(informationEdit.getChannelId());
        information.setType(informationEdit.getType());
        if (informationEdit.getSubTitle() == null)
            information.setSubTitle(Null.String);
        else
            information.setSubTitle(informationEdit.getSubTitle());
        if (informationEdit.getLinkUrl() == null)
            information.setLinkUrl(Null.String);
        else
            information.setLinkUrl(informationEdit.getLinkUrl());

        if (informationEdit.getKeywords() == null)
            information.setKeywords(Null.String);
        else
            information.setKeywords(informationEdit.getKeywords());

        if (informationEdit.getSource() == null)
            information.setSource(Null.String);
        else
            information.setSource(informationEdit.getSource());

        if (informationEdit.getFileCode() == null)
            information.setFileCode(Null.String);
        else
            information.setFileCode(informationEdit.getFileCode());

        if (informationEdit.getIndexCode() == null)
            information.setIndexCode(Null.String);
        else
            information.setIndexCode(informationEdit.getIndexCode());

        if (informationEdit.getSubject() == null)
            information.setSubject(Null.String);
        else
            information.setSubject(informationEdit.getSubject());

        if (informationEdit.getSummary() == null)
            information.setSummary(Null.String);
        else
            information.setSummary(informationEdit.getSummary());

        if (informationEdit.getOrgCode() == null)
            information.setOrgCode(Null.String);
        else
            information.setOrgCode(informationEdit.getOrgCode());

        if (informationEdit.getOrgName() == null)
            information.setOrgName(Null.String);
        else
            information.setOrgName(informationEdit.getOrgName());

        if (informationEdit.getValidTime() == null)
            information.setValidTime(Null.Date);
        else
            information.setValidTime(informationEdit.getValidTime());

        if (informationEdit.getPhoto() == null)
            information.setPhoto(Null.ByteArray);
        else
            information.setPhoto(informationEdit.getPhoto());

        if (informationEdit.getExtName() == null)
            information.setExtName(Null.String);
        else
            information.setExtName(informationEdit.getExtName());

        information.setPublishTime(informationEdit.getPublishTime());
        information.setCatalogId(informationEdit.getCatalogId());

        if (informationEdit.getLang() == null)
            information.setLang(Null.String);
        else
            information.setLang(informationEdit.getLang());

        if (informationEdit.getAuthor() == null)
            information.setAuthor(Null.String);
        else
            information.setAuthor(informationEdit.getAuthor());

        information.setPeriod(informationEdit.getPeriod());

        if (information.getPeriodTime() == null)
            information.setPeriodTime(Null.Date);
        else
            information.setPeriodTime(informationEdit.getPeriodTime());

        information.setUpdateTime(now);

        if (information.getDeptId() != null && StringUtils.isEmpty(information.getIndexCode()) &&
                channel.isGenerateIndex() != null && channel.isGenerateIndex())
        {
            String indexCode = channel.getValidIndexCode();
            if (!StringUtils.isEmpty(indexCode))
            {
                WordNumber wordNumber = new WordNumber(indexCode);
                wordNumber.setDeptId(information.getDeptId());
                wordNumber.setType("portal");
                wordNumber.setTime(information.getPublishTime());
                wordNumber.setProperty("orgCode", informationEdit.getOrgCode());

                information.setIndexCode(wordNumber.getResult());
            }
        }

        if (editPublishTime == null || channel.isGenOrderByTime() == null || !channel.isGenOrderByTime())
        {
            //没有填写发布时间，或者顺序号不按发布时间生成，而是安装顺序生成，取一个最新的顺序号
            if (information.getOrderId() == null || information.getOrderId() == 0 ||
                    (channel.isReGenerateOrder() != null && channel.isReGenerateOrder()))
            {
                information.setOrderId(dao.getId("portal_information_order", 6, Integer.class));
            }
        }
        else
        {
            if (information.getOrderId() == null || information.getOrderId() == 0 ||
                    (channel.isReGenerateOrder() != null && channel.isReGenerateOrder() &&
                            (oldPublishTime == null || !editPublishTime.equals(oldPublishTime))))
            {
                //原来没有顺序号或者修改了发布时间，根据时间生成一个新的顺序号
                Integer orderId = dao.getOrderIdByPublishTime(information.getChannelId(), editPublishTime);

                //如果此栏目之前没有发布过信息，取一个最新的id
                if (orderId == null)
                    orderId = dao.getId("portal_information_order", 6, Integer.class);

                information.setOrderId(orderId);
            }
        }

        switch (informationEdit.getType())
        {
            case information:
                //信息发布，复制信息内容
                List<InformationContent> contents = new ArrayList<InformationContent>();
                int pageNo = 0;
                for (InformationContentEdit contentEdit : informationEdit.getContents())
                {
                    InformationContent content = new InformationContent();

                    content.setInformationId(informationId);
                    content.setPageNo(pageNo++);
                    content.setContent(contentEdit.getContent());

                    contents.add(content);
                }

                information.setContents(contents);

                break;
            case file:
                //文件，复制文件内容

                InformationFileEdit fileEdit = informationEdit.getFile();

                InformationFile file = new InformationFile();
                file.setContent(fileEdit.getContent());
                file.setFileName(fileEdit.getFileName());
                file.setInformationId(informationId);

                dao.save(file);

                break;
            case url:

                //链接目标，不需要复制什么内容
                break;
        }

        information.setProperties(new HashMap<String, String>(informationEdit.getProperties()));

        if (component != null)
            component.beforePublish(informationEdit, information, first);

        if (publishAttachmentId != null)
        {
            //文章有附件时，清空该文章对应的所有附件
            List<Attachment> attachmentList = attachmentDao.getAttachments(publishAttachmentId);
            if (attachmentList != null && attachmentList.size() > 0)
            {
                for (Attachment attachment : attachmentList)
                {
                    dao.delete(attachment);
                }
            }
        }

        //拷贝采编表的图片数据，并将新的attachmentId保存到发布表中
        Long editAttachmentId = informationEdit.getAttachmentId();
        if (editAttachmentId != null)
        {
            List<Attachment> editAttachmentList = attachmentDao.getAttachments(editAttachmentId);

            if (editAttachmentList != null && editAttachmentList.size() > 0)
            {
                List<Attachment> list = new ArrayList<Attachment>();

                for (Attachment attachment : editAttachmentList)
                {
                    Attachment attachment1 = new Attachment();
                    attachment1.setAttachmentId(null);
                    attachment1.setAttachmentNo(null);
                    attachment1.setAttachmentName(attachment.getAttachmentName());
                    attachment1.setFileName(attachment.getFileName());
                    attachment1.setUserId(attachment.getUserId());
                    attachment1.setInputable(attachment.getInputable());
                    attachment1.setDeptId(attachment.getDeptId());
                    attachment1.setTag("portal");
                    list.add(attachment1);
                }

                AttachmentSaver saver = Tools.getBean(AttachmentSaver.class);
                saver.setAttachmentId(publishAttachmentId);
                saver.setAttachments(list);
                saver.save();
                information.setAttachmentId(saver.getAttachmentId());
            }
        }
        //保存信息
        dao.save(information);

        //处理关联其他信息采编
        if(!first){
            dao.deleteAllInfoRelated(information.getInformationId());
        }
        List<InformationEditRelated> relatedInfos = dao.getInformationEditRelated(informationId);
        if ((relatedInfos != null && relatedInfos.size() > 0))
        {
            for(InformationEditRelated edit:relatedInfos)
            {
                InformationRelated info = new InformationRelated();
                BeanUtils.copyProperties(edit,info);
                dao.add(info);
            }
        }

        //信息编辑记录的状态
        informationEdit.setState(InformationState.published);
        informationEdit.setValid(true);
        informationEdit.setPublished(true);
        if (informationEdit.isTopmost() == null)
            informationEdit.setTopmost(false);
        informationEdit.setOrderId(information.getOrderId());
        informationEdit.setIndexCode(information.getIndexCode());
        dao.update(informationEdit);

        if (component != null)
            component.afterPublish(informationEdit, information, first);

        //刷新页面缓存
        PageCache.updateCache();
    }

    public String fetchImg(String html)
    {
        return htmlService.fetchImg(html);
    }

    public void impFile(InputFile file, Collection<Integer> channelIds, InformationEdit information) throws Exception
    {
        String[] htmls = htmlService.toHTML(file);

        if (htmls != null && htmls.length > 0)
        {
            if (information.getTitle() == null)
            {
                String title = file.getName();
                int index = title.lastIndexOf('.');

                if (index > 0)
                    title = title.substring(0, index);

                information.setTitle(title);
            }

            saveContent(htmls, channelIds, information);
        }
    }

    @Transactional
    public void saveContent(String[] contents, Collection<Integer> channelIds, InformationEdit information)
            throws Exception
    {
        initInformation(information);
        information.setDept(null);

        for (Integer channelId : channelIds)
        {
            InformationEdit information1;
            if (channelIds.size() == 1)
            {
                information1 = information;
            }
            else
            {
                information1 = new InformationEdit();
                BeanUtils.copyProperties(information, information1);
            }

            information1.setChannelId(channelId);

            saveContent(contents, information1);
        }
    }

    @Transactional
    public void saveContent(String[] contents, InformationEdit information) throws Exception
    {
        if (information.getOrgName() == null)
            initInformation(information);

        information.setType(InformationType.information);
        information.setState(InformationState.editing);
        information.setCreateTime(new Date());
        information.setUpdateTime(new Date());

        int n = contents.length;
        List<InformationContentEdit> contentEdits = new ArrayList<InformationContentEdit>(n);

        for (int i = 0; i < n; i++)
        {
            String content = contents[i];
            InformationContentEdit contentEdit = new InformationContentEdit();
            contentEdit.setContentString(content);
            contentEdit.setPageNo(i);
            contentEdits.add(contentEdit);
        }

        information.setContents(contentEdits);

        dao.add(information);
    }
}
