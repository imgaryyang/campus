package com.gzzm.sms;

import net.cyan.commons.util.KeyValue;
import net.cyan.nest.annotation.Injectable;

import java.util.*;

/**
 * 短信服务配置信息
 *
 * @author camel
 * @date 2010-11-15
 */
@Injectable(singleton = true)
public class SmsConfig
{
    /**
     * 手机号码前缀和运营商的映射
     */
    private List<KeyValue<GatewayType>> prefixs = new ArrayList<KeyValue<GatewayType>>();

    private final static List<String> processors = new ArrayList<String>();

    public SmsConfig()
    {
        //联通
        addPrefix("130", GatewayType.union);
        addPrefix("131", GatewayType.union);
        addPrefix("132", GatewayType.union);
        addPrefix("156", GatewayType.union);
        addPrefix("145", GatewayType.union);
        addPrefix("186", GatewayType.union);
        addPrefix("185", GatewayType.union);

        //电信
        addPrefix("133", GatewayType.telecom);
        addPrefix("153", GatewayType.telecom);
        addPrefix("177", GatewayType.telecom);
        addPrefix("189", GatewayType.telecom);
        addPrefix("181", GatewayType.telecom);
        addPrefix("180", GatewayType.telecom);
        addPrefix("0", GatewayType.telecom);
    }

    private void addPrefix(String prefix, GatewayType type)
    {
        prefixs.add(new KeyValue<GatewayType>(prefix, type));
    }

    public List<KeyValue<GatewayType>> getPrefixs()
    {
        return prefixs;
    }

    public void setPrefixs(List<KeyValue<GatewayType>> prefixs)
    {
        this.prefixs = prefixs;
    }

    public GatewayType getGatewayType(String phone)
    {
        for (KeyValue<GatewayType> keyValue : prefixs)
        {
            GatewayType type = keyValue.getValue();
            if (phone.startsWith(keyValue.getKey()))
                return type;
        }

        return GatewayType.mobile;
    }

    List<String> getProcessors()
    {
        return processors;
    }

    void addProcessor(String processor)
    {
        processors.add(processor);
    }
}