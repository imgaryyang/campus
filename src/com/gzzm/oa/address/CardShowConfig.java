package com.gzzm.oa.address;

import net.cyan.nest.annotation.Injectable;

/**
 * 条码服务器信息配置
 * @author ljb
 * @date 2017/2/20 0020
 */
@Injectable(singleton = true)
public class CardShowConfig {

    private String serviceUrl;

    private String areaStr;

    public CardShowConfig() {
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getAreaStr() {
        return areaStr;
    }

    public void setAreaStr(String areaStr) {
        this.areaStr = areaStr;
    }
}
