package com.gzzm.ods.document;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.wordnumber.WordNumberDao;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2016/9/21
 */
public class OdService
{
    @Inject
    private static Provider<WordNumberDao> wordNumberDaoProvider;

    public OdService()
    {
    }

    public static OdService getInstance() throws Exception
    {
        return Tools.getBean(OdService.class);
    }

    public String getSerial(String name, int length, Integer deptId) throws Exception
    {
        Integer serialValue = wordNumberDaoProvider.get().getSerialValue("od", deptId, DateUtils.getYear(), name);

        String s = serialValue.toString();
        if (length > 0)
            s = StringUtils.leftPad(s, length, '0');

        return s;
    }
}
