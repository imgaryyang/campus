package com.gzzm.ods.dispatch;

import com.gzzm.ods.receipt.Receipt;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 数据结构修改，需要对已有的数据进行初始化
 *
 * @author LDP
 * @date 2017/8/3
 */
@Service
public class DispatchDataInit
{
    @Inject
    private DispatchRecordDao dao;

    public DispatchDataInit()
    {
    }

    @Service(url = "/ods/dispatch/datainit/init")
    public void init() throws Exception
    {
        List<DispatchRecord> list = dao.getAllEntities(DispatchRecord.class);
        if(CollectionUtils.isEmpty(list)) return;

        for (DispatchRecord r : list)
        {
            if(r.getLimitDate() == null) continue;

            //初始化办结时限 00：00 ——> 23:59
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(r.getLimitDate());
            if(calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0)
            {
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                r.setLimitDate(calendar.getTime());
            }

            //初始预警时间 根据原来的提前预警天数计算预警时间
            if(r.getWarningDate() == null && r.getWarningDays() != null) {
                r.setWarningDate(DateUtils.addDate(r.getLimitDate(), -r.getWarningDays()));
            }

            //初始化receiptId 通过sendId获得对应的回执ID
            if(r.getReceiptId() == null && r.getSendId() != null) {
                Receipt receipt = dao.getReceiptByDocumentId(r.getSend().getDocumentId());
                if(receipt != null) r.setReceiptId(receipt.getReceiptId());
            }

            dao.update(r);
        }
    }
}
