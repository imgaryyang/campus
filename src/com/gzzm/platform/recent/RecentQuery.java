package com.gzzm.platform.recent;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.NotCondition;

import java.util.*;

/**
 * @author camel
 * @date 2016/1/15
 */
@Service(url = "/recent")
public class RecentQuery extends BaseQueryCrud<Recent, Integer>
{
    private String type;

    @NotCondition
    private String[] types;

    @UserId
    private Integer userId;

    public RecentQuery()
    {
        addOrderBy("lastTime", OrderType.desc);
        addOrderBy("recentId", OrderType.desc);

        setPageSize(6);
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (type == null && types != null && types.length > 0)
        {
            type = types[0];
        }
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String[] getTypes()
    {
        return types;
    }

    public void setTypes(String[] types)
    {
        this.types = types;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public List<RecentType> getTypeList()
    {
        if (types == null)
            return null;

        List<RecentType> typeList = new ArrayList<RecentType>(types.length);

        for (String type : types)
        {
            typeList.add(new RecentType(type));
        }

        return typeList;
    }

    @Override
    protected Object createListView() throws Exception
    {
        SimplePageListView view = new SimplePageListView();
        view.setDisplay("recentName", "showTime", "openRecent('${url}','${target}')");

        view.importJs("/platform/recent/list.js");
        view.importCss("/platform/recent/list.css");

        return view;
    }
}
