package com.gzzm.safecampus.pay.job;

import com.gzzm.platform.commons.*;
import com.gzzm.safecampus.pay.cmb.PayDao;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.*;

import java.sql.Timestamp;

/**
 * @author yuanfang
 * @date 18-04-17 10:30
 */
public class ClearTimoutPaymentJob implements Runnable
{
    @Inject
    private static Provider<PayDao> provider;

    public ClearTimoutPaymentJob()
    {
    }

    @Override
    public void run()
    {
        Integer i = provider.get().checkTimeoutPayment(new Timestamp(System.currentTimeMillis() - 30 * 60 * 1000));
        Tools.log("已删除超时未支付交易：" + i + "个");
    }
}
