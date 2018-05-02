package com.gzzm.ods.flow;

import com.gzzm.ods.document.*;
import com.gzzm.platform.organ.SimpleDeptInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.valmiki.flow.Action;
import net.cyan.valmiki.form.FormData;

import java.util.List;

/**
 * 内部流程处理
 *
 * @author camel
 * @date 11-12-6
 */
@Service(url = "/ods/flow/inner")
public class InnerFlowPage extends TurnSendableFlowPage
{
    public InnerFlowPage()
    {
    }

    @Override
    @NotSerialized
    public String getTitle() throws Exception
    {
        return getFlowContext().getInstance().getTitle();
    }

    @Override
    public OfficeDocument createSendDocument() throws Exception
    {
        //发文的文档和公文的文档是同一个
        return getDocument();
    }

    @Override
    @NotSerialized
    public OfficeDocument getSendDocument() throws Exception
    {
        //发文的文档和公文的文档是同一个
        return getDocument();
    }

    public boolean isSendTextEditable() throws Exception
    {
        return isEditable() && SendFlowUtils.hasEditSendTextAction(this);
    }

    @Override
    public boolean isNewSendText() throws Exception
    {
        OfficeDocument document = getSendDocument();
        if (document == null)
            return true;

        DocumentText text = document.getText();
        if (text == null)
            return true;

        Long stepId = text.getStepId();
        return stepId == null || stepId.equals(getStepId());
    }

    @Override
    protected List<Action> createActions() throws Exception
    {
        List<Action> actions = super.createActions();

        int n;

        if (!isSended() && getDocument().getTextId() != null)
        {
            //添加查看正文的按钮
            boolean showTextExists = false;

            n = actions.size();
            for (int i = 0; i < n; i++)
            {
                Action action = actions.get(i);

                String actionId = action.getActionId();
                if (SHOW_SEND_TEXT.equals(actionId))
                {
                    showTextExists = true;
                }
                else if (EDIT_SEND_TEXT.equals(actionId))
                {
                    if (!isEditable() || getOdFlowInstance().getState() == OdFlowInstanceState.closed)
                    {
                        //只读状态或者流程结束状态，不允许编辑正文，把编辑正文按钮换成查看正文
                        actions.set(i, SHOW_SEND_TEXT_ACTION);
                    }

                    showTextExists = true;
                }
            }

            if (!showTextExists)
            {
                //在前面加入查看正文的按钮
                actions.add(0, EDIT_SEND_TEXT_ACTION);
            }
        }

        //已发文，需要增加取消发文按钮
        if (isSended() && getUserId().equals(getSendFlowInstance(true).getCreator()))
        {
            n = actions.size();
            boolean cancelSendExists = false;
            int editSendTextIndex = -1;
            for (int i = 0; i < n; i++)
            {
                Action action = actions.get(i);

                String actionId = action.getActionId();
                if (CANCEL_TURN_SEND.equals(actionId))
                {
                    cancelSendExists = true;
                    break;
                }
                else if (EDIT_SEND_TEXT.equals(actionId))
                {
                    editSendTextIndex = i;
                }
            }

            if (!cancelSendExists && editSendTextIndex >= 0)
            {
                actions.add(CANCEL_TURN_SEND_ACTION);
            }
        }

        return actions;
    }

    @Override
    public Action[] getTextActions(String type, boolean editable) throws Exception
    {
        if ("send".equals(type) && !isSended())
        {
            if (editable)
            {
                if (getFormContext().isWritable(Constants.Send.SELECTREDHEAD))
                {
                    //有选择红头模板权限，除了保存按钮以外，添加套红按钮
                    return new Action[]{SAVE_ACTION, HITCH_REDHEAD_ACTION};
                }
                else
                {
                    //没有成文权限，仅留保存按钮，没有套红和成文按钮
                    return new Action[]{SAVE_ACTION};
                }
            }
            else
            {
                return new Action[]{};
            }
        }

        return super.getTextActions(type, editable);
    }

    @Override
    public boolean accept(Action action) throws Exception
    {
        String id = action.getActionId();

        if (SHOW_SEND_TEXT.equals(id))
            return getDocument().getTextId() != null;

        return EDIT_SEND_TEXT.equals(id) || super.accept(action);
    }

    @Override
    protected void extractOdData() throws Exception
    {
        super.extractOdData();

        if (!isSended())
        {
            FormData formData = getFormContext().getFormData();

            OfficeDocument document = getDocument();

            //采集标题
            Object title = formData.getValue(Constants.Document.TITLE);
            if (title == null)
                title = formData.getValue(Constants.Document.TITLE1);

            if (title != null)
            {
                String s = StringUtils.toString(title).replaceAll("[\\r|\\n]+", " ");
                document.setTitle(s);
                getFlowContext().getInstance().setTitle(s);
            }
        }
        else
        {
            getFlowContext().getInstance().setTitle(getDocument().getTitle());
        }
    }

    @Override
    protected void saveOdData() throws Exception
    {
        if (!isSended())
        {
            OfficeDocument document = getDocument();

            document.setTitle(getFlowContext().getInstance().getTitle());

            getDao().update(document);
        }

        super.saveOdData();
    }

    /**
     * 转发文
     *
     * @throws Exception 转发文错误，由数据库操作错误引起
     */
    @Service
    @ObjectResult
    public void turnSend() throws Exception
    {
        SendFlowInstance sendFlowInstance = getSendFlowInstance(true);
        sendFlowInstance.setCreator(getOdFlowInstance().getCreator());
        sendFlowInstance.setCreateDeptId(getOdFlowInstance().getCreateDeptId());
        sendFlowInstance.setSended(true);
        sendFlowInstance.setSendStepId(getStepId());
        getDao().update(sendFlowInstance);

        OfficeDocument document = getDocument();
        document.setSourceDept(getBusinessContext().getBusinessDept().getAllName(1));

        SimpleDeptInfo dept = getBusinessContext().getBusinessDept();
        String sourceDeptCode;
        if (!StringUtils.isEmpty(dept.getOrgCode()))
            sourceDeptCode = dept.getOrgCode();
        else
            sourceDeptCode = dept.getDeptCode();
        document.setSourceDeptCode(sourceDeptCode);

        getDao().update(document);
    }

    @Override
    protected void extractMessageFromForm() throws Exception
    {
        super.extractMessageFromForm();

        if (isSended())
        {
            extractMessageFromForm("send");
        }
    }

    @Override
    protected void getQRContent(StringBuilder buffer) throws Exception
    {
        OfficeDocument document = getDocument();
        buffer.append("文件标题:").append(document.getTitle());
        buffer.append("\r\n拟稿时间:").append(DateUtils.toString(getOdFlowInstance().getStartTime()));
        buffer.append("\r\n拟稿人:").append(getOdFlowInstance().getCreateUser().getUserName());
        buffer.append("\r\n拟稿处室:").append(getOdFlowInstance().getCreateDept().getDeptName());
    }
}