package com.gzzm.portal.cms.information.visit;

import com.gzzm.platform.commons.*;
import com.gzzm.portal.commons.PortalUtils;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.*;

import java.util.*;

/**
 * @author sjy
 * @date 2018/3/2
 */

@Service
public class InfoVisitRecordService
{
    @Inject
    private static Provider<InfoVisitDao> daoProvider;

    public static void addInfoVisitRecord(Long infoId)
    {
        try
        {
            if (infoId != null)
            {
                InfoVisitDao dao = daoProvider.get();
                String ip = PortalUtils.getIp(RequestContext.getContext().getRequest());
                InfoVisitRecord record = new InfoVisitRecord();
                record.setInformationId(infoId);
                record.setIp(ip);
                Date now = new Date();
                record.setVisitTime(now);
                InfoVisitTotal visitTotal = dao.load(InfoVisitTotal.class, infoId);
                if (visitTotal == null)
                {
                    visitTotal = new InfoVisitTotal();
                    visitTotal.setInformationId(infoId);
                    visitTotal.setVisitCount(1);
                    visitTotal.setClickCount(1);
                }
                else
                {
                    visitTotal.setClickCount(visitTotal.getClickCount() + 1);
                    Long todayVisitCount = dao.queryInfoVisitRecord(infoId, ip, DateUtils.toSQLDate(now),
                            DateUtils.toSQLDate(DateUtils.addDate(now, 1)));
                    if (todayVisitCount == null || todayVisitCount == 0)
                    {
                        visitTotal.setVisitCount(visitTotal.getVisitCount() + 1);
                    }
                }
                dao.saveInfoVisit(record, visitTotal);
            }
        }
        catch (Exception e)
        {
            Tools.log(e);
        }
    }

}
