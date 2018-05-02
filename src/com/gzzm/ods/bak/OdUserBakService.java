package com.gzzm.ods.bak;

import com.gzzm.ods.flow.OdFlowInstance;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.flow.FlowApi;
import com.gzzm.platform.message.*;
import com.gzzm.platform.message.Message;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.sql.Date;
import java.util.List;

/**
 * @author camel
 * @date 13-9-23
 */
public class OdUserBakService extends OdBakService
{
    @Inject
    private OdUserBakDao dao;

    public OdUserBakService()
    {
    }

    public OdUserBakDao getDao()
    {
        return dao;
    }

    public void createZip(Long bakId) throws Exception
    {
        OdUserBak bak = dao.getOdUserBak(bakId);

        try
        {
            File file = IOUtils.createTempFile();
            OutputStream out = new FileOutputStream(file);

            Date startTime = bak.getStartTime();
            Date endTime = new Date(DateUtils.addDate(bak.getEndTime(), 1).getTime());

            try
            {
                CompressUtils.Compress zip = CompressUtils.createCompress("zip", out);
                List<Long> instanceIds =
                        dao.getInstanceIds(FlowApi.getReceivers(bak.getUserId()), startTime, endTime);

                if (lisenter != null)
                    lisenter.init(bak.getBackName(), instanceIds.size());

                for (Long instanceId : instanceIds)
                {
                    OdFlowInstance odFlowInstance = dao.getOdFlowInstance(instanceId);
                    zip(odFlowInstance, zip);

                    if (lisenter != null)
                        lisenter.add(odFlowInstance.getDocument().getTitle(), odFlowInstance.getStartTime());
                }

                zip.close();
                out.flush();
            }
            finally
            {
                IOUtils.close(out);
            }

            String path = "/odsbak/user/" + bak.getBackId().toString() + ".zip";
            getCommonFileService().upload(path, new Inputable.FileInput(file));

            bak.setPath(path);
            dao.update(bak);

            Message message = new Message();
            message.setMethods(ImMessageSender.IM);
            message.setUserId(bak.getUserId());
            message.setUrl("/ods/bak/user");
            message.setMessage(Tools.getMessage("ods.bak.user.notify", new Object[]{bak.getBackName()}));
            message.send();
        }
        catch (Throwable ex)
        {
            Message message = new Message();
            message.setMethods(ImMessageSender.IM);
            message.setUserId(bak.getUserId());
            message.setUrl("/ods/bak/user");
            message.setMessage(Tools.getMessage("ods.bak.user.fail", new Object[]{bak.getBackName()}));
            message.send();

            Tools.handleException(ex);
        }
    }
}
