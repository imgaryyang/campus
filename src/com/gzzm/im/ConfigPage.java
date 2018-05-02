package com.gzzm.im;

import com.gzzm.im.entitys.ImUserConfig;
import com.gzzm.platform.annotation.UserId;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;

/**
 * 个人消息配置的页面
 *
 * @author camel
 * @date 2011-1-6
 */
@Service
public class ConfigPage
{
    @Inject
    private ImDao dao;

    @UserId
    private Integer userId;

    private ImUserConfig config;

    public ConfigPage()
    {
    }

    public ImUserConfig getConfig()
    {
        return config;
    }

    public void setConfig(ImUserConfig config)
    {
        this.config = config;
    }

    /**
     * 显示个人配置页面
     *
     * @return 页面的名称，默认转向/im/config.ptl
     * @throws Exception 数据库异常
     */
    @Service(url = "/im/myself/config")
    @Forward(page = "/im/config.ptl")
    public String show() throws Exception
    {
        config = dao.getImUserConfig(userId);

        if(config == null)
            config = new ImUserConfig();

        return null;
    }

    /**
     * 保存个人配置
     *
     * @throws Exception 数据库操作异常
     */
    @Service(method = HttpMethod.post, validateType = ValidateType.auto)
    @ObjectResult
    @Transactional
    public void save() throws Exception
    {
        config.setUserId(userId);

        if(config.getWindowFocus() == null)
            config.setWindowFocus(false);

        if(config.getPhoneBound() == null)
            config.setPhoneBound(false);

        if(config.getSysAutoShow() == null)
            config.setSysAutoShow(false);

        dao.save(config);
    }
}
