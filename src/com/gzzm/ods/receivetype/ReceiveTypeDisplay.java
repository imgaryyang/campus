package com.gzzm.ods.receivetype;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.crud.annotation.NotCondition;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 12-2-10
 */
public class ReceiveTypeDisplay extends BaseTreeDisplay<ReceiveType, Integer>
{
    @Inject
    private ReceiveTypeService service;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @NotCondition
    private Integer deptId;

    private int type;

    public ReceiveTypeDisplay()
    {
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
    public ReceiveType getRoot() throws Exception
    {
        return service.getDao().getRootReceiveType();
    }

    public Integer getKey(ReceiveType entity) throws Exception
    {
        return entity.getReceiveTypeId();
    }

    @Override
    public List<ReceiveType> getChildren(Integer parentKey) throws Exception
    {
        if (parentKey == 0)
        {
            List<ReceiveType> topReceiveTypes = service.getTopReceiveTypes(deptId, type, userOnlineInfo);

            topReceiveTypes.add(0, new ReceiveType(-2, "全部公文"));
            topReceiveTypes.add(new ReceiveType(-1, "其他公文"));

            return topReceiveTypes;
        }
        else if (parentKey < 0)
        {
            return Collections.emptyList();
        }

        return super.getChildren(parentKey);
    }

    @Override
    public boolean hasChildren(ReceiveType parent) throws Exception
    {
        return parent.getReceiveTypeId() >= 0 && super.hasChildren(parent);
    }

    @Override
    protected Object createView() throws Exception
    {
        SelectableTreeView view = new SelectableTreeView();

        view.setRootVisible(false);

        return view;
    }
}
