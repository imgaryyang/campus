package com.gzzm.safecampus.wx.personal;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.nest.annotation.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取微信jssdk配置
 *
 * @author yiuman
 * @date 2018/3/23
 */
@Service
public class WxJsapiAuthService {

    @Inject
    private WxMpInMemoryConfigStorage storage;

    @Inject
    private WxMpServiceImpl service;

    public WxJsapiAuthService() {
    }

    @Service(url = "/wx/personal/jsapiConfig", method = HttpMethod.all)
    public Map<String, Object> jsapiConfig(String url) throws Exception {
        service.setWxMpConfigStorage(storage);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", service.createJsapiSignature(url));
        return map;
    }
}
