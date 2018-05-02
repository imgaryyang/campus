package com.gzzm.platform.organ;

import com.gzzm.platform.commons.components.EntityPageListModel;
import net.cyan.arachne.annotation.Service;

/**
 * @author camel
 * @date 13-3-14
 */
@Service
public class UserList extends EntityPageListModel<User, Integer>
{
    public UserList()
    {
        setPageSize(20);
        addOrderBy("userId");
    }

    @Override
    protected String getTextField() throws Exception
    {
        return "userName";
    }

    @Override
    protected String getSearchCondition() throws Exception
    {
        return "userName like ?text or spell like ?text or simpleSpell like ?text1";
    }
}
