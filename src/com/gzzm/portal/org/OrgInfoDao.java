package com.gzzm.portal.org;

import net.cyan.commons.util.Null;
import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.annotation.OQLUpdate;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author lk
 * @date 13-9-26
 */
public abstract class OrgInfoDao extends GeneralDao
{
    public OrgInfoDao()
    {
    }

    public OrgType getRootType() throws Exception
    {
        OrgType root = load(OrgType.class, 0);
        if (root == null)
        {
            root = new OrgType();
            root.setTypeId(0);
            root.setTypeName("根节点");
            root.setParentTypeId(Null.Integer);
            add(root);
        }

        return root;
    }

    @OQL("select t from LeaderTitle t where deptId=:1 order by orderId")
    public abstract List<LeaderTitle> getLeaderTitles(Integer deptId);

    public OrgInfo getOrgInfo(Integer orgId) throws Exception
    {
        return load(OrgInfo.class, orgId);
    }

    public Leader getLeader(Integer leaderId) throws Exception
    {
        return load(Leader.class, leaderId);
    }

    public InternalOrg getInternalOrg(Integer internalId) throws Exception
    {
        return load(InternalOrg.class, internalId);
    }

    @OQL("select t from com.gzzm.portal.org.LeaderInformation t where leaderId=:1 and t.informationId=:2")
    public abstract LeaderInformation getLeaderInformation(Integer leaderId,Long informationId);

    @OQLUpdate("delete from com.gzzm.portal.org.LeaderInformation where leaderId=:1 and informationId=:2")
    public abstract void deleteLeaderInformation(Integer leaderId,Long informationId);


    @OQLUpdate("delete from com.gzzm.portal.org.LeaderInformation where leaderId=:1 and informationId in :2")
    public abstract void deleteLeaderInformations(Integer leaderId,Long[] informationIds);
}
