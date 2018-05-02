package com.gzzm.ods.exchange.back;

import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 2018/1/29
 */
@Service
public class BackNumberPage
{
    @Inject
    private BackService service;

    private Integer deptId;

    private Integer backNumberId;

    public BackNumberPage()
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

    public Integer getBackNumberId()
    {
        return backNumberId;
    }

    public void setBackNumberId(Integer backNumberId)
    {
        this.backNumberId = backNumberId;
    }

    @Service(url = "/ods/exchange/backnumberselect")
    public String selectPage()
    {
        return "backnumberselect";
    }

    @Service
    @ObjectResult
    @NotSerialized
    public String getBackNumberString() throws Exception
    {
        return service.getBackNumberString(backNumberId);
    }

    @NotSerialized
    @Select(field = "backNumberId")
    public List<BackNumber> getBackNumbers() throws Exception
    {
        return service.getBackNumbers(deptId);
    }
}
