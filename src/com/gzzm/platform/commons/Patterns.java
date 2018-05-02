package com.gzzm.platform.commons;

import net.cyan.commons.util.StringUtils;

/**
 * @author camel
 * @date 2016/6/15
 */
public interface Patterns
{
    public static final String EMAIL0 = StringUtils.EMAIL0;

    public static final String IP0 = StringUtils.IP0;

    public static final String URL0 = StringUtils.URL0;

    public static final String EMAIL = StringUtils.EMAIL;

    public static final String IP = StringUtils.IP;

    public static final String URL = StringUtils.URL;

    public static final String MOIBLE_PHONE0 = "1[0-9]{10}";

    public static final String TELEPHONE0 = "0[1-9]([0-9]{9})|((0[1-9][0-9]-)?[0-9]{8})|((0[1-9][0-9]{2}-)?[0-9]{7})|1770[0-9]{7}";

    public static final String PHONE0 =
            "(0[1-9]|1[0-9])([0-9]{9})|((0[1-9][0-9]-)?[0-9]{8})|((0[1-9][0-9]{2}-)?[0-9]{7})";

    public static final String MOIBLE_PHONE = "^" + MOIBLE_PHONE0 + "$";

    public static final String TELEPHONE = "^(" + TELEPHONE0 + ")$";

    public static final String PHONE = "^(" + PHONE0 + ")$";
}