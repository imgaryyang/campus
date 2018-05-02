package com.gzzm.ods.bak;

import com.gzzm.ods.exchange.ExchangeReceiveService;
import com.gzzm.ods.flow.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.flow.FlowApi;
import com.gzzm.platform.message.*;
import com.gzzm.platform.message.Message;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.*;

/**
 * @author camel
 * @date 13-9-23
 */
public class OdDeptInstanceBakService extends OdBakService
{
    @Inject
    private OdDeptInstanceBakDao dao;

    @Inject
    private ExchangeReceiveService exchangeReceiveService;

    public OdDeptInstanceBakService()
    {
    }

    public OdDeptInstanceBakDao getDao()
    {
        return dao;
    }

    public void createZip(Integer bakId, Integer userId) throws Exception
    {
        OdDeptInstanceBak bak = dao.getOdDeptInstanceBak(bakId);

        try
        {
            File file = IOUtils.createTempFile();
            OutputStream out = new FileOutputStream(file);

            try
            {
                CompressUtils.Compress zip = CompressUtils.createCompress("zip", out);

                for (OdFlowInstance instance : bak.getInstances())
                {
                    zip(instance, zip);
                }

                zip.close();
                out.flush();
            }
            finally
            {
                IOUtils.close(out);
            }

            String path = "/odsbak/instance/" + bak.getBackId().toString() + ".zip";
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
            message.setUrl("/ods/bak/instance");
            message.setMessage(Tools.getMessage("ods.bak.instance.fail", new Object[]{bak.getBackName()}));
            message.send();

            Tools.handleException(ex);
        }

        Message message = new Message();
        message.setMethods(ImMessageSender.IM);
        message.setUserId(userId);
        message.setToDeptId(bak.getDeptId());
        message.setUrl("/ods/bak/instance");
        message.setMessage(Tools.getMessage("ods.bak.instance.notify", new Object[]{bak.getBackName()}));
        message.send();
    }

    @Transactional
    public void deleteInstances(Integer bakId) throws Exception
    {
        OdDeptInstanceBak bak = dao.getOdDeptInstanceBak(bakId);
        OdSystemFlowDao systemFlowDao = null;

        for (OdFlowInstance instance : bak.getInstances())
        {
            if (instance.getReceiveId() != null)
            {
                exchangeReceiveService.cannelReceive(instance.getReceiveId());
            }

            instance.setState(OdFlowInstanceState.canceled);
            dao.update(instance);

            if (systemFlowDao == null)
                systemFlowDao = OdSystemFlowDao.getInstance();
            FlowApi.getController(instance.getInstanceId(), systemFlowDao).deleteInstance();
        }
    }
}
