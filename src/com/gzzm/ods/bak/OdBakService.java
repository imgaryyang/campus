package com.gzzm.ods.bak;

import com.gzzm.ods.document.*;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.flow.*;
import com.gzzm.ods.print.FormType;
import com.gzzm.ods.print.*;
import com.gzzm.platform.attachment.Attachment;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.form.*;
import com.gzzm.platform.organ.UserInfo;
import com.gzzm.platform.template.TemplateInput;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.file.CommonFileService;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.util.*;

/**
 * @author camel
 * @date 13-9-24
 */
public class OdBakService
{
    protected CommonFileService commonFileService;

    @Inject
    protected OdFlowDao flowDao;

    @Inject
    protected PrintService printService;

    /**
     * 保存过使用过的标题
     */
    private Set<String> titles = new HashSet<String>();

    protected OdBakLisenter lisenter;

    public OdBakService()
    {
    }

    public CommonFileService getCommonFileService() throws Exception
    {
        if (commonFileService == null)
            commonFileService = Tools.getCommonFileService("odsbak");

        return commonFileService;
    }

    public void setLisenter(OdBakLisenter lisenter)
    {
        this.lisenter = lisenter;
    }

    /**
     * 多个公文同名时，在公文标题后面加数字后缀，避免目录重复
     *
     * @param title0 标题
     * @return 加后数字后缀后的公文标题
     */
    protected String createTitle(String title0)
    {
        String title = title0;

        int index = 1;
        while (titles.contains(title))
        {
            title = title0 + (index++);
        }

        titles.add(title);

        return title;
    }

    /**
     * 将公文正文和附件添加到压缩包
     *
     * @param document 要添加的公文
     * @param zip      压缩包
     * @param title    公文标题
     * @throws Exception 压缩错误或者数据库查询错误
     */
    protected void zip(OfficeDocument document, CompressUtils.Compress zip, String title) throws Exception
    {
        Long documentId = document.getDocumentId();
        try
        {
            zip.addDirectory(title);

            DocumentText text = document.getText();
            if (text != null)
            {
                if (!StringUtils.isEmpty(text.getType()) && text.getTextBody() != null)
                {
                    zip.addFile(title + "/" + document.getTitle() + "." +
                            text.getType(), text.getTextBody());
                }

                if (!StringUtils.isEmpty(text.getOtherFileName()) && text.getOtherFile() != null)
                {
                    zip.addFile(title + "/" + text.getOtherFileName(), text.getOtherFile());
                }
            }

            if (document.getAttachments() != null)
            {
                boolean first = true;
                for (Attachment attachment : document.getAttachments())
                {
                    if (first)
                    {
                        first = false;
                        zip.addDirectory(title + "/附件");
                    }

                    try
                    {
                        zip.addFile(title + "/附件/" + attachment.getAttachmentName(), attachment.getInputStream());
                    }
                    catch (IOException e)
                    {
                        //如果其中一个文件有问题，不影响其他的文件打包
                        Tools.log(e);
                    }
                }
            }
        }
        catch (Throwable ex)
        {
            throw new SystemException("zip instance failed,instanceId:" + documentId, ex);
        }
    }

    protected void zip(OdFlowInstance instance, CompressUtils.Compress zip) throws Exception
    {
        Long instanceId = instance.getInstanceId();
        try
        {
            OfficeDocument document = instance.getDocument();
            String title = createTitle(document.getTitle());

            zip(document, zip, title);

            if ("receive".equals(instance.getType()))
            {
                SendFlowInstance sendFlowInstance = flowDao.getSendFlowInstance(instanceId);
                if (sendFlowInstance != null &&
                        (sendFlowInstance.isSended() != null && sendFlowInstance.isSended()))
                {
                    //收文转发文，需要把发文的公文的正文也打进压缩包
                    zip(sendFlowInstance.getDocument(), zip, title);
                }
            }

            zipTemplate(instance, zip, title, null);
        }
        catch (Throwable ex)
        {
            throw new SystemException("zip instance failed,instanceId:" + instanceId, ex);
        }
    }

    protected void zipTemplate(OdFlowInstance instance, CompressUtils.Compress zip, String title, FormType formType)
            throws Exception
    {
        zipTemplate(instance, zip, title, formType, null);
    }

    /**
     * 将公文的打印稿笺加到压缩包
     *
     * @param instance 公文流程实例
     * @param zip      压缩包
     * @param title    公文标题
     * @param formType 稿笺类型，对于收文，可以指定只压缩收文的稿笺，或者转发文的发文稿笺，如果不指定表示压缩所有稿笺
     * @param prefix   稿笺名称的前缀，一般是部门名称,用于联合办文和会签
     * @throws Exception 压缩错误或者数据库查询错误
     */
    protected void zipTemplate(OdFlowInstance instance, CompressUtils.Compress zip, String title, FormType formType,
                               String prefix) throws Exception
    {
        Long instanceId = instance.getInstanceId();

        String type = instance.getType();
        List<PrintTemplate> printTemplates = printService.getPrintTemplates(instance.getBusinessId(),
                instance.getDeptId(), type);

        for (PrintTemplate template : printTemplates)
        {
            //是否压缩此模板
            boolean b = true;

            //要压缩的稿笺对应的表单数据的bodyId
            Long bodyId = null;

            if ("receive".equals(type))
            {
                //收文
                FormType formType1 = template.getFormType();
                if (formType1 == null)
                    formType1 = FormType.def;

                SendFlowInstance sendFlowInstance;
                if (formType != null)
                {
                    //指定了稿笺类型，如果不打印模板类型和稿笺类型不一样，则不压缩此模板
                    if (formType1 != formType)
                    {
                        b = false;
                    }
                    else if (formType == FormType.send)
                    {
                        //压缩发文稿笺，获取发文表单
                        sendFlowInstance = flowDao.getSendFlowInstance(instanceId);
                        bodyId = sendFlowInstance.getBodyId();
                    }
                }
                else if (formType1 == FormType.send)
                {
                    sendFlowInstance = flowDao.getSendFlowInstance(instanceId);
                    if (sendFlowInstance != null &&
                            (sendFlowInstance.isSended() != null && sendFlowInstance.isSended()))
                    {
                        //压缩发文稿笺，获取发文表单
                        bodyId = sendFlowInstance.getBodyId();
                    }
                    else
                    {
                        //如果没有转发文，不压缩发文稿笺
                        b = false;
                    }
                }
            }

            if (b)
            {
                if (bodyId == null)
                    bodyId = OdSystemFlowDao.getInstance().getBodyId(instanceId);

                if (bodyId != null)
                {
                    String path = printService.getTemplatePath(template);

                    SystemFormContext formContext = FormApi.getFormContext(bodyId);

                    BusinessContext businessContext = new BusinessContext();
                    businessContext.setBusinessDeptId(instance.getDeptId());

                    Integer userId = instance.getCreator();
                    if (userId == null)
                        userId = 1;

                    Integer deptId = instance.getCreateDeptId();
                    if (deptId == null)
                        deptId = instance.getDeptId();

                    businessContext.setUser(new UserInfo(userId, deptId));
                    formContext.setBusinessContext(businessContext);

                    formContext.setRole(formContext.getForm().createDefaultRole());

                    String showName = template.getShowName();
                    if (prefix != null)
                        showName = prefix + showName;

                    InputStream in = null;
                    try
                    {
                        in = new TemplateInput(path, formContext.getTextContext(), true).getInputStream();
                    }
                    catch (Throwable ex)
                    {
                        //将稿笺转为doc失败，跳过稿笺
                    }
                    if (in != null)
                        zip.addFile(title + "/" + showName + ".doc", in);
                }
            }
        }

        if ("union".equals(type) || "unionseal".equals(type))
        {
            //联合发文
            SendFlowInstance sendFlowInstance = flowDao.getSendFlowInstanceByDocumentId(instance.getDocumentId());
            OdFlowInstance sendOdFlowInstance = flowDao.getOdFlowInstance(sendFlowInstance.getInstanceId());

            zipTemplate(sendOdFlowInstance, zip, title, null, sendOdFlowInstance.getDept().getDeptName());
        }
        else if ("uniondeal".equals(type))
        {
            //联合办文
            UnionDeal unionDeal = flowDao.getUnionDeal(instance.getReceiveId());
            OdFlowInstance unionInstance = unionDeal.getUnionInstance();

            zipTemplate(unionInstance, zip, title, null, unionInstance.getDept().getDeptName());
        }
        else if ("collect".equals(type))
        {
            Collect collect = flowDao.getCollect(instance.getReceiveId());
            OdFlowInstance collectInstance = collect.getCollectInstance();

            zipTemplate(collectInstance, zip, title, null, collectInstance.getDept().getDeptName());
        }
    }
}
