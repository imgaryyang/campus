package com.gzzm.ods.flow;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.platform.form.SystemFormContext;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.DateUtils;
import net.cyan.valmiki.flow.*;

/**
 * 发文流程处理
 *
 * @author camel
 * @date 11-9-21
 */
@Service(url = "/ods/flow/send")
public class SendFlowPage extends SendableFlowPage
{
    public SendFlowPage()
    {
    }

    @Override
    public SystemFormContext createSendFormContext() throws Exception
    {
        return getFormContext();
    }

    @Override
    @NotSerialized
    public SystemFormContext getSendFormContext() throws Exception
    {
        return getFormContext();
    }

    protected boolean isSendFrom(SystemFormContext formContext) throws Exception
    {
        return formContext.getFormName() == null;
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

    @Override
    public Long getSendDocumentId() throws Exception
    {
        return getOdFlowInstance().getDocumentId();
    }

    @Override
    public boolean isSended() throws Exception
    {
        return true;
    }

    @Override
    public boolean isNewSendText() throws Exception
    {
        FlowContext flowContext = getFlowContext();

        //草稿箱不需要留痕
        return flowContext.getStep().getState() == FlowStep.DRAFT;
    }

    @Override
    protected void extractOdData() throws Exception
    {
        super.extractOdData();

        //设置流程实例的编号为发文编号
        getOdFlowInstance().setSerial(getDocument().getSendNumber());

        getFlowContext().getInstance().setTitle(getDocument().getTitle());
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
