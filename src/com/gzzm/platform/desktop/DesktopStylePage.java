package com.gzzm.platform.desktop;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 用户桌面风格配置页面
 *
 * @author camel
 * @date 2010-3-25
 */
@Service
public class DesktopStylePage
{
    /**
     * 扩展属性列表，暂时没有使用其它扩展的属性，留空
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Collection<String> ATTRIBUTES = new ArrayList<String>();

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 用户桌面风格
     */
    private UserDesktopConfig config;

    /**
     * 是否登录一个部门能看到所有部门的数据
     */
    private Integer deptDataType;

    /**
     * 默认登录部门
     */
    private Integer defaultDept;

    /**
     * 扩展属性
     */
    private Map<String, String> attributes;

    /**
     * 配置属性
     */
    private Map<String, String> properties;

    @Inject
    private DesktopDao dao;

    @Inject
    private OrganDao organDao;

    public DesktopStylePage()
    {
    }

    public Integer getDeptDataType()
    {
        return deptDataType;
    }

    public void setDeptDataType(Integer deptDataType)
    {
        this.deptDataType = deptDataType;
    }

    public Integer getDefaultDept()
    {
        return defaultDept;
    }

    public void setDefaultDept(Integer defaultDept)
    {
        this.defaultDept = defaultDept;
    }

    public UserDesktopConfig getStyle()
    {
        return config;
    }

    public void setStyle(UserDesktopConfig config)
    {
        this.config = config;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }

    /**
     * 显示个人配置页面
     *
     * @return 页面的名称，默认转向/platform/desktop/user.ptl
     * @throws Exception 数据库异常
     */
    @Service(url = "/desktop/style")
    @Forward(page = "/platform/desktop/styleconfig.ptl")
    public String show() throws Exception
    {
        Integer userId = userOnlineInfo.getUserId();

        config = dao.getDesktopStyle(userId, "desktop");

        if (config == null)
            config = new UserDesktopConfig();

        if (config.getPageSize() == null)
            config.setPageSize(0);

        if (config.getAutoReload() == null)
            config.setAutoReload(true);

        final User user = organDao.getUser(userId);

        deptDataType = user.getDeptDataType();
        defaultDept = organDao.getDefaultDeptId(userId);

        if (ATTRIBUTES.size() > 0)
        {
            Map<String, String> attributes = user.getAttributes();
            this.attributes = new HashMap<String, String>();

            for (String attribute : ATTRIBUTES)
                this.attributes.put(attribute, attributes.get(attribute));
        }

        properties = new HashMap<String, String>()
        {
            private static final long serialVersionUID = 5105842299067056607L;

            @Override
            public boolean containsKey(Object key)
            {
                return true;
            }

            @Override
            public String get(Object key)
            {
                String value;
                if (!super.containsKey(key))
                {
                    try
                    {
                        String name = (String) key;
                        value = organDao.getUserPropertyValue(userOnlineInfo.getUserId(), name);
                        super.put(name, value);
                    }
                    catch (Throwable ex)
                    {
                        Tools.wrapException(ex);
                        return null;
                    }
                }
                else
                {
                    value = super.get(key);
                }

                return value;
            }
        };

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
        Integer userId = userOnlineInfo.getUserId();

        config.setUserId(userId);
        config.setGroupId("desktop");
        dao.save(config);

        User user = new User();
        user.setUserId(userId);
        user.setDeptDataType(deptDataType);

        if (attributes != null)
        {
            //删除掉不允许修改的属性
            for (Iterator<Map.Entry<String, String>> iterator = attributes.entrySet().iterator(); iterator.hasNext(); )
            {
                Map.Entry<String, String> entry = iterator.next();
                if (!ATTRIBUTES.contains(entry.getKey()))
                    iterator.remove();
            }

            user.setAttributes(attributes);
        }

        organDao.update(user);

        if (defaultDept != null && defaultDept != 0)
            organDao.setDefaultDeptId(userId, defaultDept);

        if (properties != null)
        {
            for (Map.Entry<String, String> entry : properties.entrySet())
            {
                organDao.saveUserPropertyValue(userId, entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 刷新当前登录的信息
     *
     * @throws Exception 加载菜单错误
     */
    @Service
    @ObjectResult
    public void refresh() throws Exception
    {
        userOnlineInfo.reset();

        RequestContext context = RequestContext.getContext();
        StyleUtils.clearStyle(context.getRequest(), context.getResponse());
    }
}
