package com.gzzm.portal.commons;

/**
 * 能在网页上显示的列表的项
 *
 * @author camel
 * @date 2011-7-14
 */
public interface ListItem
{
    /**
     * 标题
     *
     * @return 标题
     */
    public String getTitle();

    /**
     * url
     *
     * @return url
     */
    public String getUrl();

    /**
     * 浏览器打开链接的目标，可以是_self,_target等,
     *
     * @return 浏览器打开链接的目标
     */
    public String getTarget();

    /**
     * 和信息相关的图片，可以为空
     *
     * @return 和信息相关的图片的url
     */
    public String getPhoto();
}