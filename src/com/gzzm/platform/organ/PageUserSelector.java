package com.gzzm.platform.organ;

import com.gzzm.platform.commons.components.PageDeptOwnedSelector;
import net.cyan.arachne.annotation.Service;

import java.util.*;

/**
 * 用户选择组件，左边是部门树，中间是可选的用户列表，右边是已选择的用户
 *
 * @author camel
 * @date 2010-4-5
 */
@Service
public class PageUserSelector extends PageDeptOwnedSelector
{
    public PageUserSelector()
    {
    }

    @Override
    protected String getDefaultId()
    {
        return "userselector";
    }

    protected List<Item> loadItems(Integer deptId) throws Exception
    {
        List<User> users = getOrganDao().getUsersInDept(deptId);

        if (users.size() == 0)
            return null;

        List<Item> items = new ArrayList<Item>(users.size());
        for (User user : users)
            items.add(new Item(user.getUserId().toString(), user.getUserName()));

        return items;
    }

    @Override
    @Service
    public List<Item> queryItems(String s) throws Exception
    {
        DeptService service = deptServiceProvider.get();

        Collection<Integer> authDeptIds = getAuthDeptIds();
        if (deptId != null)
        {
            List<Integer> subDeptIds = service.getDept(deptId).allSubDeptIds();
            if (authDeptIds != null)
                subDeptIds.retainAll(authDeptIds);
            authDeptIds = subDeptIds;
        }

        List<User> users = service.getDao().queryUsersByName(s, authDeptIds, 10);
        List<Item> result = new ArrayList<Item>(users.size());
        for (User user : users)
        {
            result.add(new Item(user.getUserId().toString(), user.getUserName()));
        }

        return result;
    }
}
