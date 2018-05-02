package com.gzzm.platform.barcode;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 2015/8/9
 */
public abstract class BarCodeDao extends GeneralDao
{
    public BarCodeDao()
    {
    }

    @LoadByKey
    public abstract BarCode getBarCode(Long barCodeId) throws Exception;

    @GetByField("content")
    public abstract List<BarCode> getBarCodeByContent(String content) throws Exception;

    @GetByField("linkContent")
    public abstract List<BarCode> getBarCodeByLinkContent(String content) throws Exception;
}
