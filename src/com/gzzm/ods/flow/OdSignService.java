package com.gzzm.ods.flow;

import com.gzzm.ods.document.SignInfo;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.flow.*;
import com.gzzm.platform.organ.User;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.valmiki.flow.FlowStep;
import net.cyan.valmiki.form.*;
import net.cyan.valmiki.form.components.*;

import java.util.*;

/**
 * 公文签发有关的操作
 *
 * @author camel
 * @date 12-4-25
 */
public class OdSignService
{
    @Inject
    private OdFlowDao dao;

    private SystemFlowDao systemFlowDao;

    public OdSignService()
    {
    }

    public SystemFlowDao getSystemFlowDao() throws Exception
    {
        if (systemFlowDao == null)
            systemFlowDao = OdSystemFlowDao.getInstance();
        return systemFlowDao;
    }

    public static boolean isSign(String opinion)
    {
        return opinion.length() > 0 && !"已阅".equals(opinion) && !"阅".equals(opinion) && !"已阅。".equals(opinion)
                && !"阅。".equals(opinion);
    }

    public static boolean isSigned(FormContext context)
    {
        if (context == null)
            return false;

        for (ComponentData componentData : context.getFormData())
        {
            if (componentData instanceof ParallelTextData && (componentData.getName().contains("领导") ||
                    componentData.getName().contains("签发") || componentData.getName().contains("批示")))
            {
                List<ParallelTextItem> items = ((ParallelTextData) componentData).getItems();
                for (ParallelTextItem item : items)
                {
                    String text = HtmlUtils.getPlainText(item.getText());
                    if (text != null)
                    {
                        text = text.trim();
                        if (isSign(text))
                            return true;
                    }
                }
            }
        }

        return false;
    }

    public List<Integer> getSignedUsers(FormContext context) throws Exception
    {
        if (context == null)
            return null;

        List<Integer> userIds = null;

        for (ComponentData componentData : context.getFormData())
        {
            if (componentData instanceof ParallelTextData && (componentData.getName().contains("领导") ||
                    componentData.getName().contains("签发") || componentData.getName().contains("批示")))
            {
                List<ParallelTextItem> items = ((ParallelTextData) componentData).getItems();
                for (ParallelTextItem item : items)
                {
                    String text = HtmlUtils.getPlainText(item.getText());
                    if (text != null)
                    {
                        text = text.trim();
                        if (isSign(text))
                        {
                            Integer userId = null;
                            String operator = item.getOperator();

                            if (StringUtils.isEmpty(operator))
                            {
                                String stepId = item.getOperationId();
                                FlowStep flowStep = getSystemFlowDao().loadStep(stepId);

                                if (flowStep != null)
                                    userId = Integer.valueOf(flowStep.getReceiver());
                            }
                            else
                            {
                                userId = Integer.valueOf(operator);
                            }

                            if (userId != null)
                            {
                                if (userIds == null)
                                    userIds = new ArrayList<Integer>();

                                if (!userIds.contains(userId))
                                    userIds.add(userId);
                            }
                        }
                    }
                }
            }
        }

        return userIds;
    }

    public static String getSigns(FormContext context)
    {
        if (context == null)
            return null;

        StringBuilder buffer = new StringBuilder();

        for (ComponentData componentData : context.getFormData())
        {
            if (componentData instanceof ParallelTextData && (componentData.getName().contains("领导") ||
                    componentData.getName().contains("签发") || componentData.getName().contains("批示")))
            {
                for (ParallelTextItem item : ((ParallelTextData) componentData).getItems())
                {
                    String text = HtmlUtils.getPlainText(item.getText());

                    if (text != null)
                    {
                        text = text.trim();
                        if (isSign(text))
                        {
                            if (buffer.length() > 0)
                                buffer.append("\r\n");

                            buffer.append(text);
                            buffer.append("\r\n                   ");
                            if (!StringUtils.isEmpty(item.getOrgName()))
                                buffer.append(item.getOrgName()).append(" ");
                            buffer.append(item.getOperatorName()).append(" ")
                                    .append(DateUtils.toString(item.getTime(), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }

        return buffer.toString();
    }

    public boolean isSigned(Long instanceId) throws Exception
    {
        return isSigned(FlowApi.getFormContext(instanceId, getSystemFlowDao()));
    }

    public String getSigns(Long instanceId) throws Exception
    {
        return getSigns(FlowApi.getFormContext(instanceId, getSystemFlowDao()));
    }

    public void initSigns(Collection<Integer> deptIds) throws Exception
    {
        while (true)
        {
            List<OdFlowInstance> instances = dao.queryNotSignedInstances(deptIds);

            if (instances.size() == 0)
                return;

            for (OdFlowInstance instance : instances)
            {
                initSign(instance);
                dao.update(instance);
            }
        }
    }

    public static ParallelTextItem getLastSignText(FormData formData)
    {
        if (formData == null)
            return null;

        ParallelTextItem lastItem = null;
        boolean signed = false;
        boolean approved = false;

        for (ComponentData componentData : formData)
        {
            if (componentData instanceof ParallelTextData)
            {
                String name = componentData.getName();
                if (name.contains("签发"))
                {
                    if (!signed && lastItem != null)
                        lastItem = null;

                    signed = true;
                    List<ParallelTextItem> items = ((ParallelTextData) componentData).getItems();
                    for (ParallelTextItem item : items)
                    {
                        String text = HtmlUtils.getPlainText(item.getText());
                        if (text != null)
                        {
                            text = text.trim();
                            if (text.length() > 0)
                            {
                                if (lastItem == null || lastItem.getTime().before(item.getTime()))
                                    lastItem = item;
                            }
                        }
                    }
                }
                else if (!signed && name.contains("批示"))
                {
                    if (!approved && lastItem != null)
                        lastItem = null;

                    approved = true;

                    List<ParallelTextItem> items = ((ParallelTextData) componentData).getItems();
                    for (ParallelTextItem item : items)
                    {
                        String text = HtmlUtils.getPlainText(item.getText());
                        if (text != null)
                        {
                            text = text.trim();
                            if (isSign(text))
                            {
                                if (lastItem == null || lastItem.getTime().before(item.getTime()))
                                    lastItem = item;
                            }
                        }
                    }
                }
                else if (!signed && !approved && name.contains("领导"))
                {
                    List<ParallelTextItem> items = ((ParallelTextData) componentData).getItems();
                    for (ParallelTextItem item : items)
                    {
                        String text = HtmlUtils.getPlainText(item.getText());
                        if (text != null)
                        {
                            text = text.trim();
                            if (isSign(text))
                            {
                                if (lastItem == null || lastItem.getTime().before(item.getTime()))
                                    lastItem = item;
                            }
                        }
                    }
                }
            }
        }

        return lastItem;
    }

    public void initSign(OdFlowInstance odFlowInstance) throws Exception
    {
        try
        {
            initSign(odFlowInstance, FlowApi.getFormContext(odFlowInstance.getInstanceId(), getSystemFlowDao()));
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }

    public void initSign(OdFlowInstance odFlowInstance, FormContext context) throws Exception
    {
        List<Integer> signedUserIds = null;

        try
        {
            signedUserIds = getSignedUsers(context);
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }

        if (signedUserIds == null)
        {
            odFlowInstance.setSigned(false);
        }
        else
        {
            odFlowInstance.setSigned(true);

            List<User> signers = new ArrayList<User>(signedUserIds.size());
            for (Integer userId : signedUserIds)
            {
                User user = new User();
                user.setUserId(userId);
                signers.add(user);
            }

            odFlowInstance.setSigners(signers);
        }
    }

    public static String getSigner(FormData formData)
    {
        String signer = null;
        ComponentData data = formData.getData(Constants.Document.SIGNER);
        if (data instanceof SimpleComponentData)
            signer = DataConvert.toString(((SimpleComponentData) data).getValue());

        if (signer == null)
        {
            ParallelTextItem lastSignText = getLastSignText(formData);
            if (lastSignText != null)
            {
                signer = lastSignText.getOperatorName();
            }
        }

        return signer;
    }

    public static Date getSignTime(FormData formData)
    {
        String signer = null;
        ComponentData data = formData.getData(Constants.Document.SIGNER);
        if (data instanceof SimpleComponentData)
            signer = DataConvert.toString(((SimpleComponentData) data).getValue());

        if (signer == null)
        {
            ParallelTextItem lastSignText = getLastSignText(formData);
            if (lastSignText != null)
            {
                return lastSignText.getTime();
            }

            return null;
        }
        else
        {
            return formData.getDate(Constants.Document.SIGNTIME);
        }
    }

    public static SignInfo getSignInfo(FormData formData)
    {
        if (formData == null)
            return null;

        String signer = null;
        ComponentData data = formData.getData(Constants.Document.SIGNER);
        if (data instanceof SimpleComponentData)
            signer = DataConvert.toString(((SimpleComponentData) data).getValue());
        Date signTime = null;

        if (signer == null)
        {
            ParallelTextItem lastSignText = getLastSignText(formData);
            if (lastSignText != null)
            {
                signer = lastSignText.getOperatorName();
                signTime = lastSignText.getTime();
            }
        }
        else
        {
            signTime = formData.getDate(Constants.Document.SIGNTIME);
        }

        return new SignInfo(signer, signTime);
    }
}
