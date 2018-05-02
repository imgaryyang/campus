package com.gzzm.safecampus.pay.job;

import com.gzzm.platform.commons.*;
import com.gzzm.safecampus.pay.cmb.PayDao;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.*;

import java.sql.Timestamp;

/**
 * @author yuanfang
 * @date 18-04-20 14:44
 */
public class ClearAccountJob  implements Runnable
{
    @Inject
    private static Provider<PayDao> provider;

    public ClearAccountJob()
    {
    }

    @Override
    public void run()
    {

        Integer i = provider.get().checkTimeoutMerchantAccount(new Timestamp(System.currentTimeMillis()));
        Tools.log("已删除未成功签约账户：" + i + "个");

    }
}
