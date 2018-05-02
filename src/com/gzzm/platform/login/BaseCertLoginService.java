package com.gzzm.platform.login;

import com.gzzm.platform.commons.*;
import net.cyan.commons.security.*;
import net.cyan.commons.util.*;

import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.security.cert.*;

/**
 * 默认的证书登录服务
 *
 * @author camel
 * @date 2009-7-23
 */
public class BaseCertLoginService implements CertLoginService
{
    private X509TrustManager trustManager;

    protected boolean checkCert = true;

    /**
     * 0表示自动匹配，1表示简单签名，2表示pkcs7
     */
    protected int type = 0;

    public BaseCertLoginService()
    {
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public boolean isCheckCert()
    {
        return checkCert;
    }

    public void setCheckCert(boolean checkCert)
    {
        this.checkCert = checkCert;
    }

    public CertUserInfo check(String cert, String sign, String random) throws Exception
    {
        Tools.log("cert:" + cert);
        Tools.log("sign:" + sign);
        Tools.log("random:" + random);

        byte[] signData = CommonUtils.base64ToByteArray(sign.replaceAll("\\s", ""));

        PKCS7SignedData pkcs7SignedData = null;
        if (type != 1)
        {
            try
            {
                PKCS7 pkcs7 = PKCS7.parse(signData);

                if (pkcs7 instanceof PKCS7SignedData)
                    pkcs7SignedData = (PKCS7SignedData) pkcs7;
            }
            catch (Exception ex)
            {
                //不是合法的pkcs7格式，把传进来的签名当简单的签名数据处理
                if (type == 2)
                    throw ex;
            }
        }

        //加载证书
        X509Certificate certificate;

        if (!StringUtils.isEmpty(cert))
        {
            try
            {
                certificate = loadCertificate(cert);
            }
            catch (Exception ex)
            {
                //证书格式错误
                throw new SystemMessageException("login.cert_error", "parse cert fail:\n" + cert + "\n", ex);
            }
        }
        else if (pkcs7SignedData != null)
        {
            certificate = pkcs7SignedData.getFinalCertificates().get(0);
        }
        else
        {
            throw new SystemMessageException("login.cert_error", "cert is empty");
        }

        //校验证书的期限
        try
        {
            certificate.checkValidity();
        }
        catch (CertificateExpiredException ex)
        {
            //证书已过期
            throw new NoErrorException("login.cert_expired");
        }
        catch (CertificateNotYetValidException e)
        {
            //证书未生效
            throw new NoErrorException("login.cert_not_yet_valid");
        }

        if (checkCert)
        {
            //校验证书是否在信任队列
            verify(certificate, cert);
        }

        boolean b;
        if (pkcs7SignedData == null)
        {
            b = SecurityUtils.verify(signData, random.getBytes(), certificate);
        }
        else
        {
            b = checkRandom(random, pkcs7SignedData.getContentData()) && pkcs7SignedData.verify(certificate);
        }

        //签名不合法
        if (!b)
        {
            throw new SystemMessageException("login.sign_error",
                    "cert:" + cert + "\nsign:" + sign + "\nrandom:" + random);
        }

        return getUserInfo(certificate);
    }

    protected boolean checkRandom(String random, byte[] data)
    {
        return checkRandom(random, data, "UTF-8") || checkRandom(random, data, "UTF-16LE");
    }

    protected boolean checkRandom(String random, byte[] data, String charset)
    {
        try
        {
            return random.equals(new String(data, charset));
        }
        catch (UnsupportedEncodingException ex)
        {
            Tools.wrapException(ex);
            return false;
        }
    }

    public String[] getCertIds(String cert)
    {
        //加载证书
        X509Certificate certificate;

        try
        {
            certificate = loadCertificate(cert);
        }
        catch (Exception ex)
        {
            //证书格式错误
            throw new SystemMessageException("login.cert_error", "parse cert fail:\n" + cert + "\n", ex);
        }

        return getCertIds(certificate);
    }

    public CertUserInfo getUserInfo(X509Certificate certificate) throws Exception
    {
        return new CertUserInfo(getCertIds(certificate), getCertName(certificate));
    }

    public boolean accpet(X509Certificate certificate) throws Exception
    {
        return true;
    }

    protected X509Certificate loadCertificate(String cert) throws Exception
    {
        return SecurityUtils.loadCertificate(cert);
    }

    /**
     * 校验证书的是否受信任
     *
     * @param certificate 证书
     * @param cert        base64格式的证书
     * @throws Exception 校验错误或者证书不受信任
     */
    protected void verify(X509Certificate certificate, String cert) throws Exception
    {
        synchronized (this)
        {
            if (trustManager == null)
                trustManager = createTrustManager();
        }

        try
        {
            trustManager.checkServerTrusted(new X509Certificate[]{certificate}, "");
        }
        catch (CertificateException ex)
        {
            throw new SystemMessageException("login.cert_error", "valid certificate failed:\n" + cert + "\n", ex);
        }
    }

    protected X509TrustManager createTrustManager() throws Exception
    {
        return new PathCertsTrustManager(Tools.getConfigPath("/certs/"));
    }

    protected String[] getCertIds(X509Certificate certificate)
    {
        //用16进制表示的证书序列号作证书id
        return new String[]{certificate.getSerialNumber().toString(16)};
    }

    protected String getCertName(X509Certificate certificate)
    {
        try
        {
            //用cn项作为证书名称
            return DistinguishedName.getSubject(certificate).getValue("CN");
        }
        catch (Exception ex)
        {
            Tools.wrapException(ex);
            return null;
        }
    }
}
