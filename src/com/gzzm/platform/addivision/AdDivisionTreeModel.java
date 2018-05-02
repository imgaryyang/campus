package com.gzzm.platform.addivision;

import com.gzzm.platform.commons.components.EntityPageTreeModel;
import net.cyan.arachne.annotation.*;
import net.cyan.arachne.components.*;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.io.Serializable;

/**
 * @author camel
 * @date 13-3-26
 */
@Service
public class AdDivisionTreeModel extends EntityPageTreeModel<AdDivision>
        implements CheckBoxTreeModel<AdDivision>, TextModel<AdDivision>, ValueTreeModel<AdDivision>, Serializable
{
    private static final long serialVersionUID = -7853070705546880991L;

    @Inject
    private static Provider<AdDivisionDao> daoProvider;

    private boolean showBox;

    private AdDivisionValueType valueType = AdDivisionValueType.ID;

    private boolean showFullName;

    public AdDivisionTreeModel()
    {
    }

    public AdDivisionValueType getValueType()
    {
        return valueType;
    }

    public void setValueType(AdDivisionValueType valueType)
    {
        this.valueType = valueType;
    }

    public boolean isShowBox()
    {
        return showBox;
    }

    public void setShowBox(boolean showBox)
    {
        this.showBox = showBox;
    }

    public boolean hasCheckBox(AdDivision adDivision) throws Exception
    {
        return showBox;
    }

    public Boolean isChecked(AdDivision adDivision) throws Exception
    {
        return false;
    }

    @Override
    protected Object getRootKey()
    {
        return 0;
    }

    @Override
    @NotSerialized
    public AdDivision getRoot() throws Exception
    {
        return daoProvider.get().getRoot();
    }

    @NotSerialized
    public Boolean isRootVisible()
    {
        return false;
    }

    public boolean isShowFullName()
    {
        return showFullName;
    }

    public void setShowFullName(boolean showFullName)
    {
        this.showFullName = showFullName;
    }

    @Override
    protected String getTextField() throws Exception
    {
        return "divisionName";
    }

    @Override
    protected String getSearchCondition() throws Exception
    {
        return "divisionName like ?text or spell like ?text or simpleSpell like ?text1";
    }

    @Override
    public Object valueOf(AdDivision division) throws Exception
    {
        switch (valueType)
        {
            case ID:
                return division.getDivisionId();
            case CODE:
                return division.getDivisionCode();
            case NAME:
                return division.getDivisionName();
            default:
                return division.getDivisionId();
        }
    }

    @Override
    public String getText(AdDivision adDivision) throws Exception
    {
        if (showFullName)
            return adDivision.getFullName();
        else
            return null;
    }
}
