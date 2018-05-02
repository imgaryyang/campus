package com.gzzm.platform.desktop;

import com.gzzm.platform.annotation.*;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 和系统公告相关的请求
 *
 * @author camel
 * @date 2010-6-2
 */
@Service
public class PlacardPage
{
    /**
     * 注入有权限访问的部门
     */
    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    @UserId
    private Integer userId;

    @Inject
    private PlacardService service;

    public PlacardPage()
    {
    }

    /**
     * 获得当前用户需要显示的公共
     *
     * @return 公共列表
     * @throws Exception 数据库查询错误
     */
    @Service(url = "/placards")
    @NotSerialized
    public List<String> getPlacards() throws Exception
    {
        return service.getPlacards(userId, authDeptIds);
    }
}
