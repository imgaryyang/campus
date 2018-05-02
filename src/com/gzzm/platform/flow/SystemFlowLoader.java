package com.gzzm.platform.flow;

import net.cyan.valmiki.flow.XmlFlowLoader;

import java.io.*;

/**
 * 流程加载器，从数据库里加载流程
 *
 * @author camel
 * @date 2010-9-18
 */
public final class SystemFlowLoader extends XmlFlowLoader
{
    SystemFlowLoader()
    {
    }

    protected InputStream getInputStream(String flowId) throws Exception
    {
        return new ByteArrayInputStream(
                new String(FlowInfoDao.getInstance().getFlowContent(Integer.valueOf(flowId))).getBytes("UTF-8"));
    }

    public boolean check(String flowId, long loadTime) throws Exception
    {
        return FlowInfoDao.getInstance().getFlowUpdateTime(Integer.valueOf(flowId)).getTime() > loadTime;
    }
}
