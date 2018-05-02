package com.gzzm.ods.bak;

import com.gzzm.ods.document.OfficeDocument;
import com.gzzm.ods.exchange.*;
import com.gzzm.ods.flow.*;
import com.gzzm.ods.print.FormType;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.message.*;
import com.gzzm.platform.message.Message;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.util.*;
import net.cyan.crud.exporters.*;
import net.cyan.crud.view.*;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.sql.Date;
import java.util.*;

/**
 * @author camel
 * @date 13-9-23
 */
public class OdDeptBakService extends OdBakService
{
    @Inject
    private OdDeptBakDao dao;

    public OdDeptBakService()
    {
    }

    public OdDeptBakDao getDao()
    {
        return dao;
    }

    public void createZip(Integer bakId, Integer userId) throws Exception
    {
        OdDeptBak bak = dao.getOdDeptBak(bakId);
        OdDeptBakType type = bak.getType();

        try
        {
            File file = IOUtils.createTempFile();
            OutputStream out = new FileOutputStream(file);

            try
            {
                CompressUtils.Compress zip = CompressUtils.createCompress("zip", out);

                Date startTime = bak.getStartTime();
                Date endTime = new Date(DateUtils.addDate(bak.getEndTime(), 1).getTime());

                if (type == OdDeptBakType.SEND)
                {
                    List<Send> sends = dao.getSendIds(startTime, endTime, bak.getDeptId(),
                            bak.getSendNumberId());

                    if (lisenter != null)
                        lisenter.init(bak.getBackName(), sends.size());

                    //建发文excel记录表
                    zipXls(sends, zip, OdDeptBakType.SEND);

                    for (Send send : sends)
                    {
                        zip(send, zip);
                    }
                }
                else if (type == OdDeptBakType.RECEIVE)
                {
                    List<Receive> receives = dao.getReceiveIds(startTime, endTime, bak.getDeptId(),
                            bak.getReceiveTypeId() == null ? null : bak.getReceiveType().getAllReceiveTypeIds());

                    if (lisenter != null)
                        lisenter.init(bak.getBackName(), receives.size());

                    zipXls(receives, zip, OdDeptBakType.RECEIVE);

                    for (Receive receive : receives)
                    {
                        zip(receive, zip);
                    }
                }

                zip.close();
                out.flush();
            }
            finally
            {
                IOUtils.close(out);
            }

            String path = "/odsbak/dept/" + bak.getBackId().toString() + ".zip";
            getCommonFileService().upload(path, new Inputable.FileInput(file));

            bak.setPath(path);
            dao.update(bak);
        }
        catch (Throwable ex)
        {
            Message message = new Message();
            message.setMethods(ImMessageSender.IM);
            message.setUserId(userId);
            message.setToDeptId(bak.getDeptId());
            message.setUrl("/ods/bak/dept");
            message.setMessage(Tools.getMessage("ods.bak.dept.fail", new Object[]{bak.getBackName()}));
            message.send();

            Tools.handleException(ex);
        }

        Message message = new Message();
        message.setMethods(ImMessageSender.IM);
        message.setUserId(userId);
        message.setToDeptId(bak.getDeptId());
        message.setUrl("/ods/bak/dept");
        message.setMessage(Tools.getMessage("ods.bak.dept.notify", new Object[]{bak.getBackName()}));
        message.send();
    }

    public void zip(Send send, CompressUtils.Compress zip) throws Exception
    {
        OfficeDocument document = send.getDocument();

        if (lisenter != null)
            lisenter.add(document.getTitle(), send.getSendTime());


        String title = createTitle(document.getTitle());

        zip(document, zip, title);

        SendFlowInstance sendFlowInstance = flowDao.getSendFlowInstanceByDocumentId(send.getDocumentId());

        if (sendFlowInstance != null)
        {
            OdFlowInstance instance = flowDao.getOdFlowInstance(sendFlowInstance.getInstanceId());

            if (instance != null)
                zipTemplate(instance, zip, title, FormType.send);
        }
    }

    public void zipXls(List<?> dataList, CompressUtils.Compress zip, OdDeptBakType type) throws Exception
    {
        String name;
        List<Column> columns = new ArrayList<Column>();
        if (type == OdDeptBakType.SEND)
        {
            name = "发文";
            columns.add(new ExpressionColumn("文号", "sendNumber.sendNumberName"));
            columns.add(new ExpressionColumn("发文日期", "sendTime"));
            columns.add(new ExpressionColumn("主送单位", "document.mainReceivers"));
            columns.add(new ExpressionColumn("文件标题", "document.title"));
        }
        else
        {
            name = "收文";
            columns.add(new ExpressionColumn("收文编号", "serial"));
            columns.add(new ExpressionColumn("日期", "receiveBase.acceptTime"));
            columns.add(new ExpressionColumn("来文单位", "receiveBase.document.sourceDept"));
            columns.add(new ExpressionColumn("文件标题", "receiveBase.document.title"));
        }

        InputFile inputFile = ExportUtils.export(dataList, columns, new ExportParameters(name + "记录"), "xls");
        zip.addFile(name + "记录.xls", inputFile.getInputStream());
    }

    public void zip(Receive receive, CompressUtils.Compress zip) throws Exception
    {
        ReceiveBase receiveBase = receive.getReceiveBase();
        OfficeDocument document = receiveBase.getDocument();

        if (lisenter != null)
            lisenter.add(document.getTitle(), receiveBase.getSendTime());

        String title = createTitle(document.getTitle());

        zip(document, zip, title);

        OdFlowInstance instance = flowDao.getOdFlowInstanceByReceiveId(receive.getReceiveId());

        if (instance != null)
            zipTemplate(instance, zip, title, FormType.def);

    }
}
