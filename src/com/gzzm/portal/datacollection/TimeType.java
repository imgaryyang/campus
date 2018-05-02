package com.gzzm.portal.datacollection;

/**
 * @author ldp
 * @date 2018/4/25
 */
public enum TimeType {

    /**
     * 年
     */
    YEAR("{year}年"),

    /**
     * 季度
     */
    QUARTER("{year}年第{quarter}季度"),

    /**
     * 月
     */
    MOUNTH("{year}年{mouth}月", MouthDataEnhancer.class);

    private String template;

    private Class<? extends DataEnhancer> dataEnhancer;

    TimeType(String template) {
        this.template = template;
    }

    TimeType(String template, Class<? extends DataEnhancer> dataEnhancer) {
        this.template = template;
        this.dataEnhancer = dataEnhancer;
    }

    public String getTemplate() {
        return template;
    }

    public Class<? extends DataEnhancer> getDataEnhancer() {
        return dataEnhancer;
    }
}
