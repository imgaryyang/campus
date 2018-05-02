package com.gzzm.oa.deptfile;

import com.gzzm.platform.commons.crud.*;
import net.cyan.crud.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author ccs
 */
public class DeptFileFolderDisplay extends BaseTreeDisplay<DeptFileFolder, Integer>
{
    @Inject
    private DeptFileDao dao;

    /**
     * 部门ID
     */
    private Integer deptId;

    public DeptFileFolderDisplay()
    {
        addOrderBy(new OrderBy("orderId", OrderType.asc));
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    @Override
    public DeptFileFolder getRoot() throws Exception
    {
        return dao.getRootFolder();
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        return new SelectableTreeView();
    }

    @Override
    public boolean supportSearch() throws Exception
    {
        return true;
    }
}
