package com.gzzm.platform.commons;

import net.cyan.arachne.encrypt.PageEncryptStorer;
import net.cyan.commons.util.*;

/**
 * @author camel
 * @date 2018/1/19
 */
public class SystemPageEncryptStorer implements PageEncryptStorer
{
    public SystemPageEncryptStorer()
    {
    }

    @Override
    public void save(String modulus, String privateExponent) throws Exception
    {
        Tools.setConfig("encrypt_modulus", modulus);
        Tools.setConfig("encrypt_private_exponent", privateExponent);
    }


    @Override
    public Tuple<String, String> load() throws Exception
    {
        String modulus = Tools.getConfig("encrypt_modulus");
        String privateExponent = Tools.getConfig("encrypt_private_exponent");
        if (!StringUtils.isEmpty(modulus) && !StringUtils.isEmpty(privateExponent))
            return new Tuple<String, String>(modulus, privateExponent);
        else
            return null;
    }
}
