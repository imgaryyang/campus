package com.gzzm.sms;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.AdvancedEnum;
import net.cyan.sms.*;
import net.cyan.sms.cmpp.CmppClient;
import net.cyan.sms.cmpp3.Cmpp3Client;
import net.cyan.sms.hbwebservice.HbWebserviceClient;
import net.cyan.sms.mas.MasClient;
import net.cyan.sms.modem.ModemClient;
import net.cyan.sms.sgip.SgipClient;
import net.cyan.sms.smgp.SmgpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关类
 *
 * @author camel
 * @date 11-8-29
 */
public class GatewayClass implements AdvancedEnum<String>
{
    private static final long serialVersionUID = -2186489868511289166L;

    private static final List<GatewayClass> gatewayClasses = new ArrayList<GatewayClass>();

    /**
     * 网关的id，如cmpp,sgip等
     */
    private String id;

    /**
     * 网关的名称
     */
    private String name;

    /**
     * 网关所属的类型，移动，联通或者电信
     */
    private GatewayType type;

    /**
     * 网关的客户端类
     */
    private Class<? extends BaseSmsClient> clientClass;

    /**
     * 默认端口
     */
    private int defaultPort;

    static
    {
        try
        {
            addGatewayClass("cmpp", "CMPP网关", GatewayType.common, CmppClient.class)
                    .setDefaultPort(CmppClient.DEFAULTPOET);
            addGatewayClass("cmpp3", "CMPP3.0", GatewayType.common, Cmpp3Client.class)
                    .setDefaultPort(Cmpp3Client.DEFAULTPOET);
            addGatewayClass("sgip", "SGIP网关", GatewayType.union, SgipClient.class)
                    .setDefaultPort(SgipClient.DEFAULTPOET);
            addGatewayClass("smgp", "SMGP网关", GatewayType.telecom, SmgpClient.class)
                    .setDefaultPort(SmgpClient.DEFAULTPOET);
            addGatewayClass("jasson", "嘉讯短信机", GatewayType.common, JassonClient.class);
            addGatewayClass("jassondb", "嘉讯DB多用户接口", GatewayType.common, JassonDbClient.class);
            addGatewayClass("jassondbSingle", "嘉讯DB单用户接口", GatewayType.common, JassonDbSingleClient.class);
            addGatewayClass("qxt", "企信通", GatewayType.common, QxtClient.class);
            addGatewayClass("qxt_http", "企信通http接口", GatewayType.common, Qxt2Client.class);
            addGatewayClass("yxt", "一信通", GatewayType.common, YxtClient.class);
            addGatewayClass("hbwebservice", "商信通", GatewayType.common, HbWebserviceClient.class);
            addGatewayClass("hwdb", "华为短信机", GatewayType.common, HWDBClient.class);
            addGatewayClass("modem", "短信猫", GatewayType.common, ModemClient.class);
            addGatewayClass("ctxc", "创天炫彩", GatewayType.common, CtxcClient.class);
            addGatewayClass("yw", "亿纬短信猫", GatewayType.common, YwClient.class);
            addGatewayClass("ykt", "翼客通", GatewayType.common, YktClient.class);
            addGatewayClass("mas", "mas短信机", GatewayType.common, MasClient.class);
            addGatewayClass("remote", "远程短信网关", GatewayType.common, RemoteClient.class);
        }
        catch (Throwable ex)
        {
            Tools.log(ex);
        }
    }

    private GatewayClass(String id, String name, GatewayType type, Class<? extends BaseSmsClient> clientClass)
    {
        this.id = id;
        this.name = name;
        this.type = type;
        this.clientClass = clientClass;
    }

    private static GatewayClass addGatewayClass(GatewayClass gatewayClass)
    {
        gatewayClasses.add(gatewayClass);

        return gatewayClass;
    }

    private static GatewayClass addGatewayClass(String id, String name, GatewayType type,
                                                Class<? extends BaseSmsClient> clientClass)
    {
        return addGatewayClass(new GatewayClass(id, name, type, clientClass));
    }

    public static GatewayClass getGatewayClass(String id)
    {
        for (GatewayClass gatewayClass : gatewayClasses)
        {
            if (gatewayClass.getId().equals(id))
                return gatewayClass;
        }

        return null;
    }

    public static List<GatewayClass> getGatewayClasses()
    {
        return gatewayClasses;
    }

    public static List<GatewayClass> getGatewayClasses(GatewayType type)
    {
        List<GatewayClass> result = new ArrayList<GatewayClass>();

        for (GatewayClass gatewayClass : gatewayClasses)
        {
            if (gatewayClass.getType() == type || gatewayClass.getType() == GatewayType.common)
                result.add(gatewayClass);
        }

        return result;
    }

    public static List<GatewayClass> values()
    {
        return gatewayClasses;
    }

    public static GatewayClass valueOf(String id) throws Exception
    {
        return getGatewayClass(id);
    }

    public static GatewayClass getValue(String id) throws Exception
    {
        return getGatewayClass(id);
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public GatewayType getType()
    {
        return type;
    }

    public Class<? extends BaseSmsClient> getClientClass()
    {
        return clientClass;
    }

    public int getDefaultPort()
    {
        return defaultPort;
    }

    private void setDefaultPort(int defaultPort)
    {
        this.defaultPort = defaultPort;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public String valueOf()
    {
        return id;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        GatewayClass that = (GatewayClass) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }
}
