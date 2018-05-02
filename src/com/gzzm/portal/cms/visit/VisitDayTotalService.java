package com.gzzm.portal.cms.visit;

import net.cyan.commons.util.*;
import net.cyan.nest.annotation.*;

import java.sql.Date;
import java.util.*;


/**
 * @author sjy
 * @date 2018/3/2
 */
public class VisitDayTotalService
{

    @Inject
    private VisitDao dao;

    private java.util.Date now = null;

    public void createData() throws Exception
    {
        now = new java.util.Date();
        Date startDay = null;
        VisitDayTotal currentRecord = dao.getCurrentVisitDayTotal();
        if (currentRecord == null)
        {
            startDay = dao.queryMinVisitTime();
            if (startDay == null)
            {
                return;
            }
        }
        else
        {
            startDay = currentRecord.getVisitDay();
        }
        initDayData(DateUtils.toSQLDate(startDay));
    }

    private void initDayData(Date day) throws Exception
    {
        Date nextDay = DateUtils.toSQLDate(DateUtils.addDate(day, 1));
        List<CountVisitBean> countVisitBeans = dao.countVisitByDay(day, nextDay);
        if (countVisitBeans != null && countVisitBeans.size() > 0)
        {
            for (CountVisitBean bean : countVisitBeans)
            {
                VisitDayTotal data = new VisitDayTotal();
                data.setType(bean.getType());
                data.setClickCount(bean.getClickCount());
                data.setVisitCount(bean.getVisitCount());
                data.setObjectId(bean.objectId);
                data.setVisitDay(day);
                dao.save(data);
            }
        }
        if (DateUtils.getDaysInterval(nextDay, now) > 0)
        {
            initDayData(nextDay);
        }
    }

    public static class CountVisitBean
    {

        private Integer type;

        private Integer objectId;

        private Integer clickCount;

        private Integer visitCount;

        public Integer getClickCount()
        {
            return clickCount;
        }

        public void setClickCount(Integer clickCount)
        {
            this.clickCount = clickCount;
        }

        public Integer getType()
        {
            return type;
        }

        public void setType(Integer type)
        {
            this.type = type;
        }

        public Integer getObjectId()
        {
            return objectId;
        }

        public void setObjectId(Integer objectId)
        {
            this.objectId = objectId;
        }

        public Integer getVisitCount()
        {
            return visitCount;
        }

        public void setVisitCount(Integer visitCount)
        {
            this.visitCount = visitCount;
        }
    }
}
