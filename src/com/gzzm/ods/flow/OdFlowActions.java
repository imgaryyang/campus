package com.gzzm.ods.flow;

import net.cyan.valmiki.flow.*;

/**
 * @author camel
 * @date 2014/8/29
 */
public interface OdFlowActions
{
    /**
     * 送会签单位处理
     */
    public static final String SEND_COLLECTS = "sendCollects";

    /**
     * 送联合办文单位处理
     */
    public static final String SEND_UNIONDEALS = "sendUnionDeals";

    /**
     * 套红
     */
    public static final String HITCH_REDHEAD = "hitchRedHead";

    /**
     * 另存到
     */
    public static final String STORE_TO = "odStoreTo";

    /**
     * 归档
     */
    public static final String CATALOG = "catalog";

    /**
     * 取消归档
     */
    public static final String UNCATALOG = "uncatalog";

    /**
     * 删除实例
     */
    public static final String DELETE_OD_INSTANCE = "deleteOdInstance";

    public static final DefaultAction HITCH_REDHEAD_ACTION = new BaseDefaultAction(HITCH_REDHEAD, null);

    public static final DefaultAction STORE_TO_ACTION = new BaseDefaultAction(STORE_TO, null);

    public static final DefaultAction CATALOG_ACTION = new BaseDefaultAction(CATALOG, null);

    public static final DefaultAction UNCATALOG_ACTION = new BaseDefaultAction(UNCATALOG, null);

}