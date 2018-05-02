package com.gzzm.safecampus.pay.job;

import com.gzzm.platform.commons.*;
import com.gzzm.safecampus.campus.pay.BillDao;
import com.gzzm.safecampus.campus.pay.BillStatus;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.*;

import java.sql.Date;

/**
 * 账单超期未支付 设账单过期状态
 * @author yuanfang
 * @date 18-04-17 9:15
 */
public class BillTimeoutJob implements Runnable
{

    @Inject
    private static Provider<BillDao> provider;

    @Override
    public void run()
    {
        Integer i = provider.get().checkBillsTimeout(BillStatus.Expired, new Date(System.currentTimeMillis()));
        Tools.log("账单超期未支付：" + i + " 个");
    }
}
