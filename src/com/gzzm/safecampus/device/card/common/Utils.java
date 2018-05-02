package com.gzzm.safecampus.device.card.common;

import com.gzzm.safecampus.device.card.dto.ErrorMsg;

/**
 * @author liyabin
 * @date 2018/3/21
 */
public class Utils
{
    public Utils()
    {
    }

    public static boolean checkBean(Object obk) throws NoSuchFieldException, IllegalAccessException
    {
        return BeanEncodeUtils.checkToken(obk);
    }

    public static void initBean(Object obk) throws NoSuchFieldException, IllegalAccessException
    {
        BeanEncodeUtils.initToken(obk);
    }

    public static String createError(String msg) throws Exception
    {
        ErrorMsg errorMsg = new ErrorMsg("-1", msg);
        Utils.initBean(errorMsg);
        return XMLUtils.toXML(errorMsg);
    }

    public static String createError( ) throws Exception
    {
        return createError("token 失效请检查！");
    }
}
