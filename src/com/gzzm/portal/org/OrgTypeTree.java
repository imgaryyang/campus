package com.gzzm.portal.org;

import com.gzzm.platform.commons.components.EntityPageTreeModel;
import net.cyan.arachne.annotation.Service;
import net.cyan.arachne.components.CheckBoxTreeModel;
import net.cyan.commons.util.Null;

/**
 * 机构类型选择树
 *
 * @author camel
 * @date 2016/6/12
 */
@Service
public class OrgTypeTree extends EntityPageTreeModel<OrgType> implements CheckBoxTreeModel<OrgType>
{
    private boolean showBox;

    public OrgTypeTree()
    {
    }

    public boolean isShowBox()
    {
        return showBox;
    }

    public void setShowBox(boolean showBox)
    {
        this.showBox = showBox;
    }

    @Override
    protected Object getRootKey() throws Exception
    {
        return 0;
    }

    @Override
    protected OrgType createRoot() throws Exception
    {
        OrgType type = new OrgType();
        type.setTypeId(0);
        type.setTypeName("根节点");
        type.setParentTypeId(Null.Integer);
        return type;
    }

    @Override
    public Boolean isRootVisible()
    {
        return false;
    }

    @Override
    public boolean hasCheckBox(OrgType orgType) throws Exception
    {
        return showBox && orgType.getChildren().isEmpty();
    }

    @Override
    public Boolean isChecked(OrgType orgType) throws Exception
    {
        return false;
    }
}
