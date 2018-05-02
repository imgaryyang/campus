package com.gzzm.ods.receivetype;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.wordnumber.*;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 11-11-18
 */
public class ReceiveTypeService
{
    public static final String RECEIVETYPE_SELECT_APP = "receiveType_select";

    public static final String RECEIVETYPE_SELECT_APP1 = "receiveType_select1";

    @Inject
    private ReceiveTypeDao dao;

    @Inject
    private DeptService deptService;

    @Inject
    private WordNumberDao wordNumberDao;

    public ReceiveTypeService()
    {
    }

    public ReceiveTypeDao getDao()
    {
        return dao;
    }

    @NotSerialized
    public List<ReceiveType> getTopReceiveTypes(Integer deptId, final int type, UserOnlineInfo userOnlineInfo)
            throws Exception
    {
        Collection<Integer> authDeptIds = userOnlineInfo.getAuthDeptIds(type == 0 ?
                RECEIVETYPE_SELECT_APP : RECEIVETYPE_SELECT_APP1);

        if (authDeptIds != null && authDeptIds.isEmpty())
            authDeptIds = Arrays.asList(deptId, 1);

        return deptService.loadDatasFromParentDepts(deptId, new DeptOwnedDataProvider<ReceiveType>()
        {
            public List<ReceiveType> get(Integer deptId) throws Exception
            {
                return dao.getTopReceiveTypes(deptId, type);
            }
        }, authDeptIds, null);
    }

    public String getSerial(Integer receiveTypeId, Integer deptId) throws Exception
    {
        return getSerial(dao.getReceiveType(receiveTypeId), deptId);
    }

    public String getSerial(ReceiveType receiveType, Integer deptId) throws Exception
    {
        String serial = null;
        while (receiveType.getReceiveTypeId() != 0 && StringUtils.isEmpty(serial = receiveType.getSerial()))
            receiveType = receiveType.getParentReceiveType();

        if (!StringUtils.isEmpty(serial))
        {
            WordNumber wordNumber = new WordNumber(serial);

            if (receiveType.getSerialDeptId() != null && receiveType.getSerialDeptId() > 0)
                wordNumber.setDeptId(receiveType.getSerialDeptId());
            else
                wordNumber.setDeptId(deptId);
            wordNumber.setType("od");

            return wordNumber.getResult();
        }

        return "";
    }

    public List<SerialMatch> matchSerial(String s, Integer receiveTypeId, Integer deptId) throws Exception
    {
        ReceiveType receiveType = dao.getReceiveType(receiveTypeId);

        String serial = null;
        while (receiveType.getReceiveTypeId() != 0 && StringUtils.isEmpty(serial = receiveType.getSerial()))
            receiveType = receiveType.getParentReceiveType();

        if (!StringUtils.isEmpty(serial))
        {
            WordNumber wordNumber = new WordNumber(serial);

            if (receiveType.getSerialDeptId() != null && receiveType.getSerialDeptId() > 0)
                wordNumber.setDeptId(receiveType.getSerialDeptId());
            else
                wordNumber.setDeptId(deptId);
            wordNumber.setType("od");

            return wordNumber.matchSerials(s);
        }

        return null;
    }

    public void setSerialValue(Integer deptId, String name, Integer value) throws Exception
    {
        wordNumberDao.setSerialValue("od", deptId, DateUtils.getYear(), name, value);
    }
}
