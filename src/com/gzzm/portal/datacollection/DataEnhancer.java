package com.gzzm.portal.datacollection;

import java.util.Map;

/**
 * 数据增强器
 *
 * @author ldp
 * @date 2018/4/26
 */
public interface DataEnhancer {

    /**
     * 接收前台传过来的参数，并进行增强
     */
    void enhance(Map<String, Object> map) throws Exception;

}