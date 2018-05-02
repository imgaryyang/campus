package com.gzzm.portal.webdeptgroup;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author Xrd
 * @date 2018/3/20 17:35
 */
public abstract class WebDeptGroupDao extends GeneralDao
{
    public WebDeptGroupDao()
    {
    }

    /**
     * 根据用途id获取分组名称
     * @param purposeId
     * @return
     * @throws Exception
     */
    @OQL("select distinct groupName from WebDeptGroup where purposeId=?1 and notempty(groupName) order by groupName")
    public abstract List<String> getGroupName(String purposeId)throws Exception;

    @OQL("select t from WebDeptGroup t where t.purposeId=?1 and t.groupName = ?2 and t.deptId = ?3")
    public abstract WebDeptGroup getByPurposeIdAndGroupNameAndDeptId(String purposeId,String groupName,Integer deptId);
}
