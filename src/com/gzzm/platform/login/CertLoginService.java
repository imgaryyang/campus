package com.gzzm.platform.login;

import java.security.cert.X509Certificate;

/**
 * 证书登录服务，用于校验证书和签名的合法性，返回证书的id，名称
 *
 * @author camel
 * @date 2009-7-23
 */
public interface CertLoginService
{
    /**
     * 校验证书合法性并返回证书的用户信息，用于非双向ssl方式的证书登录
     *
     * @param cert   base64编码的证书
     * @param sign   签名
     * @param random 被签名的随机数
     * @return 证书对应的用户信息
     * @throws Exception 允许子类抛出异常
     */
    public CertUserInfo check(String cert, String sign, String random) throws Exception;

    /**
     * 获得证书的证书ID
     *
     * @param cert base64编码的证书
     * @return 证书ID
     * @throws Exception 允许子类抛出异常
     */
    public String[] getCertIds(String cert) throws Exception;

    /**
     * 判断是否接受此证书
     *
     * @param certificate x509证书
     * @return 接受返回true，不接受返回false
     * @throws Exception 允许子类抛出异常
     */
    public boolean accpet(X509Certificate certificate) throws Exception;

    /**
     * 获得证书对应的用户信息，用于双向ssl方式的证书登录
     *
     * @param certificate x509证书
     * @return 证书对应的用户信息
     * @throws Exception 允许子类抛出异常
     */
    public CertUserInfo getUserInfo(X509Certificate certificate) throws Exception;
}