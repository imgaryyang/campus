package com.gzzm.ods.exchange.back;

import net.cyan.arachne.annotation.*;
import net.cyan.nest.annotation.Inject;

import java.util.List;

/**
 * @author camel
 * @date 2018/1/29
 */
@Service
public class BackPaperSelectPage
{
    @Inject
    private BackService backService;

    private Integer deptId;

    public BackPaperSelectPage()
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

    @NotSerialized
    public List<BackPaper> getBackPapers() throws Exception
    {
        return backService.getBackPapers(deptId);
    }

    @Service(url = "/ods/exchange/backpagerselect")
    public String show()
    {
        return "backpaperselect";
    }
}
