package com.gzzm.platform.organ;

import com.gzzm.platform.annotation.UserId;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/11/30
 */
@Service
public class UserPropertyPage
{
    @UserId
    private Integer userId;

    @Inject
    private OrganDao dao;

    public UserPropertyPage()
    {
    }

    @Service(url = "/userProperty", method = HttpMethod.post)
    public void setProperty(String propertyName, String propertyValue) throws Exception
    {
        dao.saveUserPropertyValue(userId, propertyName, propertyValue);
    }

    @Service(url = "/userProperty", method = HttpMethod.get)
    @ObjectResult
    public String getProperty(String propertyName) throws Exception
    {
        return dao.getUserPropertyValue(userId, propertyName);
    }
}
