package com.gzzm.sms;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 短信息启动
 *
 * @author camel
 * @date 2010-11-8
 */
public class SmsStart implements Runnable
{
    @Inject
    private static Provider<SmsClients> clientsProvider;

    @Inject
    private static Provider<SmsConfig> configProvider;

    public SmsStart()
    {
    }

    public void run()
    {
        final List<String> processors = new ArrayList<String>();

        SystemConfig.getInstance().addAfterLoad(new Runnable()
        {
            public void run()
            {
                try
                {
                    clientsProvider.get().startAll();
                }
                catch (Throwable ex)
                {
                    // 启动服务失败，，这里无法再往外面抛异常，仅记录日志
                    Tools.log(ex);
                }

                SmsConfig config = configProvider.get();
                for (String processor : processors)
                    config.addProcessor(processor);

                SmsService.startSmsSend();
            }
        });

        SystemConfig.getInstance().addClassResolver(new ClassResolver()
        {
            public void resolve(Class<?> c) throws Exception
            {
                if (Processor.class.isAssignableFrom(c) && BeanUtils.isRealClass(c))
                {
                    processors.add(c.getName());
                }
            }
        });
    }
}
