package com.gzzm.platform.login;

import java.util.*;

/**
 * @author camel
 * @date 2018/1/30
 */
public interface LoginErrorService
{
    public List<Date> getErrorTimes(Date date) throws Exception;

    public int getErrorCount(Date date) throws Exception;

    public Date getLastErrorTime(Date date) throws Exception;
}