package com.gzzm.portal.cms.information;

import net.cyan.commons.util.InputFile;
import net.cyan.crud.importers.DataRecord;

/**
 * @author camel
 * @date 13-11-3
 */
public abstract class AbstractInformationComponent implements InformationComponent
{
    public AbstractInformationComponent()
    {
    }

    public String page()
    {
        return null;
    }

    public boolean beforeSave(InformationEdit information, boolean isNew, InformationCrud crud) throws Exception
    {
        return true;
    }

    public void afterSave(InformationEdit information, boolean isNew, InformationCrud crud) throws Exception
    {
    }

    public void init(InformationEdit information, InformationCrud crud) throws Exception
    {
    }

    public void load(InformationEdit information, InformationCrud crud) throws Exception
    {
    }

    @Override
    public void clone(InformationEdit information, InformationEdit sourceInformation, InformationCrud crud)
            throws Exception
    {
    }

    public boolean beforePublish(InformationEdit informationEidt, Information information, boolean first)
            throws Exception
    {
        return true;
    }

    public void afterPublish(InformationEdit informationEdit, Information information, boolean first) throws Exception
    {
    }

    public String getText(InformationBase<?, ?> information) throws Exception
    {
        return null;
    }

    @Override
    public boolean isSupportedImport(InformationCrud crud) throws Exception
    {
        return false;
    }

    @Override
    public void importFill(DataRecord record, InformationEdit information, InformationCrud crud) throws Exception
    {
    }

    @Override
    public boolean imp(InputFile file, InformationCrud crud) throws Exception
    {
        return false;
    }
}
