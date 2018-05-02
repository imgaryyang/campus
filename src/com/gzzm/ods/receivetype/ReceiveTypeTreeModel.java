package com.gzzm.ods.receivetype;

import com.gzzm.platform.commons.components.EntityPageTreeModel;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 12-9-28
 */
public class ReceiveTypeTreeModel extends EntityPageTreeModel<ReceiveType>
{
    @Inject
    private static Provider<ReceiveTypeService> serviceProvider;

    @Inject
    private static Provider<UserOnlineInfo> userOnlineInfoProvider;

    private Integer deptId;

    private int type;

    public ReceiveTypeTreeModel()
    {
    }

    @Override
    protected Object getRootKey()
    {
        return 0;
    }

    public Boolean isRootVisible()
    {
        return false;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    @Override
    protected String getTextField() throws Exception
    {
        return "receiveTypeName";
    }

    @Override
    protected List<ReceiveType> getChildren(ReceiveType parent) throws Exception
    {
        if (parent.getReceiveTypeId() == 0)
        {
            return serviceProvider.get().getTopReceiveTypes(deptId, type, userOnlineInfoProvider.get());
        }

        return super.getChildren(parent);
    }
}
