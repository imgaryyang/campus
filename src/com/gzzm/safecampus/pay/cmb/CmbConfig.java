package com.gzzm.safecampus.pay.cmb;

/**
 * 招行一网通相关配置
 *
 * @author yuanfang
 * @date 18-04-10 9:54
 */
public class CmbConfig
{
    /**
     * 域名
     */
    private String serverName;

    /**
     * 服务端URL
     */
    private String server;

    /**
     * 查询公钥URL
     */
    private String publicKeyUrl;

    /**
     * 签约支付URL
     */
    private String payAndSignUrl;

    /**
     * 按商户日期查询订单URL
     */
    private String queryOrderByMerchantDateUrl;

    /**
     * 查询商户入账明细URL
     */
    private String queryAccountListUrl;

    /**
     * 查询单笔订单URL
     */
    private String querySigleOrderUrl;

    /**
     * 退款URL
     */
    private String refundUrl;

    /**
     * 一网通账户输出(我的钱包)URL
     */
    private String accountOutputUrl;

    /**
     * 一网通开户URL
     */
    private String openAccountUrl;

    /**
     * 绑定银行卡
     */
    private String bindCardUrl;

    /**
     * 编码
     */
    private String chartset;

    /**
     * 一网通账户输出固定参数
     */
    private String funcid;

    /**
     * 秘钥 - 用于获取公钥
     */
    private String screctKey;

    /**
     * 合作码
     */
    private String copCode;

    /**
     * 商户号 - 用于获取公钥
     */
    private String merchantNo;

    /**
     * 商户分行号 - 用于获取公钥
     */
    private String branchNo;


    public CmbConfig()
    {
    }

    public String getServerName()
    {
        return serverName;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public String getOpenAccountUrl()
    {
        return openAccountUrl;
    }

    public void setOpenAccountUrl(String openAccountUrl)
    {
        this.openAccountUrl = openAccountUrl;
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public String getPublicKeyUrl()
    {
        return publicKeyUrl;
    }

    public void setPublicKeyUrl(String publicKeyUrl)
    {
        this.publicKeyUrl = publicKeyUrl;
    }

    public String getPayAndSignUrl()
    {
        return payAndSignUrl;
    }

    public void setPayAndSignUrl(String payAndSignUrl)
    {
        this.payAndSignUrl = payAndSignUrl;
    }

    public String getQueryOrderByMerchantDateUrl()
    {
        return queryOrderByMerchantDateUrl;
    }

    public void setQueryOrderByMerchantDateUrl(String queryOrderByMerchantDateUrl)
    {
        this.queryOrderByMerchantDateUrl = queryOrderByMerchantDateUrl;
    }

    public String getQueryAccountListUrl()
    {
        return queryAccountListUrl;
    }

    public void setQueryAccountListUrl(String queryAccountListUrl)
    {
        this.queryAccountListUrl = queryAccountListUrl;
    }

    public String getQuerySigleOrderUrl()
    {
        return querySigleOrderUrl;
    }

    public void setQuerySigleOrderUrl(String querySigleOrderUrl)
    {
        this.querySigleOrderUrl = querySigleOrderUrl;
    }

    public String getRefundUrl()
    {
        return refundUrl;
    }

    public void setRefundUrl(String refundUrl)
    {
        this.refundUrl = refundUrl;
    }

    public String getAccountOutputUrl()
    {
        return accountOutputUrl;
    }

    public void setAccountOutputUrl(String accountOutputUrl)
    {
        this.accountOutputUrl = accountOutputUrl;
    }

    public String getChartset()
    {
        return chartset;
    }

    public void setChartset(String chartset)
    {
        this.chartset = chartset;
    }

    public String getFuncid()
    {
        return funcid;
    }

    public void setFuncid(String funcid)
    {
        this.funcid = funcid;
    }

    public String getScrectKey()
    {
        return screctKey;
    }

    public void setScrectKey(String screctKey)
    {
        this.screctKey = screctKey;
    }

    public String getCopCode()
    {
        return copCode;
    }

    public void setCopCode(String copCode)
    {
        this.copCode = copCode;
    }

    public String getMerchantNo()
    {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo)
    {
        this.merchantNo = merchantNo;
    }

    public String getBranchNo()
    {
        return branchNo;
    }

    public void setBranchNo(String branchNo)
    {
        this.branchNo = branchNo;
    }

    public String getBindCardUrl()
    {
        return bindCardUrl;
    }

    public void setBindCardUrl(String bindCardUrl)
    {
        this.bindCardUrl = bindCardUrl;
    }
}
