package com.gzzm.ods.receivetype;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.wordnumber.SerialMatch;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * 收文类型选择
 *
 * @author camel
 * @date 11-11-5
 */
@Service
public class ReceiveTypePage
{
    @Inject
    private ReceiveTypeService service;

    /**
     * 当前用户信息
     */
    @Inject
    private UserOnlineInfo userOnlineInfo;

    private Integer deptId;

    private Integer receiveTypeId;

    private ReceiveTypeTreeModel receiveTypeTree;

    private int type;

    public ReceiveTypePage()
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

    public Integer getReceiveTypeId()
    {
        return receiveTypeId;
    }

    public void setReceiveTypeId(Integer receiveTypeId)
    {
        this.receiveTypeId = receiveTypeId;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public ReceiveTypeTreeModel getReceiveTypeTree()
    {
        if (receiveTypeTree == null)
        {
            receiveTypeTree = new ReceiveTypeTreeModel();
            receiveTypeTree.setDeptId(deptId);
            receiveTypeTree.setType(type);
        }

        return receiveTypeTree;
    }

    @Service(url = "/ods/receivetype/select")
    public String show()
    {
        return "select";
    }

    @Service
    @ObjectResult
    public String getSerial(Integer receiveTypeId) throws Exception
    {
        return service.getSerial(receiveTypeId, deptId);
    }

    @Service(url = "/ods/receivetype/match")
    public List<SerialMatch> matchSerial(String s) throws Exception
    {
        return service.matchSerial(s, receiveTypeId, deptId);
    }

    @Service(url = "/ods/receivetype/serial", method = HttpMethod.post)
    public void setSerialValue(Integer deptId, String name, Integer value) throws Exception
    {
        service.setSerialValue(deptId, name, value);
    }
}
