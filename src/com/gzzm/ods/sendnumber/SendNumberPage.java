package com.gzzm.ods.sendnumber;

import com.gzzm.ods.business.BusinessModel;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.wordnumber.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.DateUtils;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 选择发文字号
 *
 * @author camel
 * @date 11-11-5
 */
@Service
public class SendNumberPage
{
    public static final String SENDNUMBER_SELECT_APP = "sendNumber_select";

    @Inject
    private SendNumberDao dao;

    @Inject
    private WordNumberDao wordNumberDao;

    private Integer deptId;

    private Integer sendNumberId;

    private Integer businessId;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    public SendNumberPage()
    {
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getBusinessId()
    {
        return businessId;
    }

    public void setBusinessId(Integer businessId)
    {
        this.businessId = businessId;
    }

    public Integer getSendNumberId()
    {
        return sendNumberId;
    }

    public void setSendNumberId(Integer sendNumberId)
    {
        this.sendNumberId = sendNumberId;
    }

    @NotSerialized
    @Select(field = "sendNumberId")
    public List<SendNumber> getSendNumbers() throws Exception
    {
        Collection<Integer> authDeptIds = userOnlineInfo.getAuthDeptIds(SENDNUMBER_SELECT_APP);

        if (authDeptIds == null || authDeptIds.isEmpty())
        {
            authDeptIds = null;

            if (businessId != null)
            {
                BusinessModel business = dao.getBusiness(businessId);
                if (business != null)
                {
                    Integer businessDeptId = business.getDeptId();
                    if (!businessDeptId.equals(deptId))
                    {
                        authDeptIds = Arrays.asList(deptId, businessDeptId);
                    }
                }
            }

            if (authDeptIds == null)
                authDeptIds = Collections.singleton(deptId);
        }

        return dao.getSendNumbers(authDeptIds);
    }

    @Service(url = "/ods/sendnumber/select")
    public String show()
    {
        return "select";
    }

    @Service
    @ObjectResult
    @NotSerialized
    public String getWordNumber() throws Exception
    {
        SendNumber sendNumber = dao.getSendNumber(sendNumberId);

        WordNumber wordNumber = new WordNumber(sendNumber.getSendNumber());

        wordNumber.setDeptId(deptId);
        wordNumber.setType("od");

        return wordNumber.getResult();
    }

    @Service(url = "/ods/sendnumber/match")
    public List<SerialMatch> matchSendNumber(String s) throws Exception
    {
        SendNumber sendNumber = dao.getSendNumber(sendNumberId);

        WordNumber wordNumber = new WordNumber(sendNumber.getSendNumber());

        wordNumber.setDeptId(deptId);
        wordNumber.setType("od");

        return wordNumber.matchSerials(s);
    }

    @Service(url = "/ods/sendnumber/serial", method = HttpMethod.post)
    public void setSerialValue(String name, Integer value) throws Exception
    {
        wordNumberDao.setSerialValue("od", deptId, DateUtils.getYear(), name, value);
    }
}
