package com.gzzm.safecampus.campus.common;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.wordnumber.WordNumber;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 序号生成
 *
 * @author yuanfang
 * @date 18-03-27 16:32
 */
public class SerialNoGenerator
{
    @Inject
    private static Provider<UserOnlineInfo> userOnlineInfoProvider;

    public SerialNoGenerator()
    {
    }

    private static String getSerialNo(Integer deptId) throws Exception
    {
        WordNumber wordNumber = new WordNumber("$date(yyMMdd)$serial(0,4)");
        wordNumber.setTime(new Date());
        wordNumber.setType("campus");
        wordNumber.setDeptId(deptId);
        return wordNumber.getResult();
    }

    public static String getStudentSerialNo() throws Exception
    {
        return 1 + getSerialNo(userOnlineInfoProvider.get().getDeptId());
    }

    public static String getTeacherSerialNo() throws Exception
    {
        return 2 + getSerialNo(userOnlineInfoProvider.get().getDeptId());
    }

}
