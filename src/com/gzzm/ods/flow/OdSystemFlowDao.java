package com.gzzm.ods.flow;

import com.gzzm.platform.flow.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.OQL;

import java.util.List;

/**
 * @author camel
 * @date 2014/8/21
 */
public abstract class OdSystemFlowDao extends SystemFlowDao
{
    @Inject
    private static Provider<OdSystemFlowDao> daoProvider;

    public static OdSystemFlowDao getInstance()
    {
        return daoProvider.get();
    }

    public static SystemFlowDao getInstance(int year) throws Exception
    {
        if (year > 0)
        {
            return getInstance("PFFLOWINSTANCE_OD" + year, "PFFLOWSTEP_OD" + year);
        }
        else if (year < -99)
        {
            return BakSystemFlowDao.getInstance();
        }
        else
        {
            return getInstance();
        }
    }

    public OdSystemFlowDao()
    {
        super(OdSystemFlowInstance.class, OdSystemFlowStep.class);
    }

    @Override
    public void stop(String instanceId) throws Exception
    {
        super.stop(instanceId);

        refreshStepQ(Long.valueOf(instanceId));
    }

    @Override
    public void deleteInstance(String instanceId) throws Exception
    {
        super.deleteInstance(instanceId);

        refreshStepQ(Long.valueOf(instanceId));
    }

    @Override
    public void restore(String instanceId) throws Exception
    {
        super.restore(instanceId);

        refreshStepQ(Long.valueOf(instanceId));
    }

    @Override
    public void recover(Long instanceId) throws Exception
    {
        super.recover(instanceId);

        refreshStepQ(instanceId);
    }

    @Transactional
    public void refreshStepQ(Long instanceId) throws Exception
    {
        setNativeSql(true);
        try
        {
            executeSql("delete from ODFLOWSTEPQ where INSTANCEID=:1", instanceId);
            executeSql(
                    "insert into ODFLOWSTEPQ(STEPID,GROUPID,INSTANCEID,NODEID,USERID,STATE,RECEIVETIME,SOURCENAME," +
                            "SHOWTIME,LASTSTEP,CONSIGNATIONID,HIDDEN,TITLE,INSTANCESTATE,SENDNUMBER," +
                            "SOURCEDEPT,SUBJECT,DOCUMENTID,TYPE,TAG,PRIORITY,SERIAL,URGED,BUSINESSID,SIMPLENAME," +
                            "SENDNUMBERID,RECEIVETYPEID,LINKID,CREATEDEPTID,FIRSTSTEP,COLLECTED) " +
                            "select s.STEPID,max(s.GROUPID),max(s.INSTANCEID),max(s.NODEID),max(s.RECEIVER)," +
                            "max(s.STATE),max(s.RECEIVETIME),max(s.SOURCENAME),max(s.SHOWTIME),max(s.LASTSTEP)," +
                            "max(s.CONSIGNATIONID),max(s.HIDDEN),max(d.TITLE),max(i.STATE)," +
                            "max(d.SENDNUMBER),max(d.SOURCEDEPT),max(d.SUBJECT),max(i.DOCUMENTID),max(i.TYPE),max(i.TAG)," +
                            "max(i.PRIORITY),max(i.SERIAL),max(i.URGED),max(i.BUSINESSID),max(i.SIMPLENAME),max(si.SENDNUMBERID)," +
                            "max(i.RECEIVETYPEID),max(i.LINKID),max(i.CREATEDEPTID),max(case when s.PRESTEPID=0 then 1 else 0 end)," +
                            "max(case when exists(select * from ODCOLLECT c where c.COLLECTSTEPID=s.STEPID) then 1 else 0 end) " +
                            "from PFFLOWSTEP s join ODFLOWINSTANCE i on s.INSTANCEID=i.INSTANCEID join ODDOCUMENT d " +
                            "on i.DOCUMENTID=d.DOCUMENTID left join ODFLOWSENDINSTANCE si on si.INSTANCEID=s.INSTANCEID " +
                            "where s.INSTANCEID=:1 and s.STATE not in (8) and s.NODEID not in ('#collect','#unionDeal'," +
                            "'#unionDead','#union','#unionseal','#turn') group by s.STEPID", instanceId);


            executeSql("delete from ODFLOWSTEPD where INSTANCEID=:1", instanceId);
            executeSql(
                    "insert into ODFLOWSTEPD(STEPID,GROUPID,INSTANCEID,NODEID,DEPTID,RECEIVETIME,SOURCENAME," +
                            "SHOWTIME,TITLE,INSTANCESTATE,SENDNUMBER,SOURCEDEPT,SUBJECT,DOCUMENTID,TYPE," +
                            "TAG,PRIORITY,SERIAL,URGED,BUSINESSID,SIMPLENAME,SENDNUMBERID,RECEIVETYPEID,LINKID,CREATEDEPTID) " +
                            "select s.STEPID,max(s.GROUPID),max(s.INSTANCEID),max(s.NODEID),max(s.DEPTID)," +
                            "max(s.RECEIVETIME),max(s.SOURCENAME),max(s.SHOWTIME),max(d.TITLE),max(i.STATE)," +
                            "max(d.SENDNUMBER),max(d.SOURCEDEPT),max(d.SUBJECT),max(i.DOCUMENTID),max(i.TYPE),max(i.TAG)," +
                            "max(i.PRIORITY),max(i.SERIAL),max(i.URGED),max(i.BUSINESSID),max(i.SIMPLENAME),max(si.SENDNUMBERID)," +
                            "max(i.RECEIVETYPEID),max(i.LINKID),max(i.CREATEDEPTID) " +
                            "from PFFLOWSTEP s join ODFLOWINSTANCE i on s.INSTANCEID=i.INSTANCEID join ODDOCUMENT d " +
                            "on i.DOCUMENTID=d.DOCUMENTID left join ODFLOWSENDINSTANCE si on si.INSTANCEID=s.INSTANCEID " +
                            "where s.INSTANCEID=:1 and s.STATE in (0,2,3,6,7,15,16,17,18) and s.DEPTLAST=1 " +
                            "and s.NODEID not in ('#collect','#unionDeal','#unionDeal','#union','#unionseal','#turn')" +
                            " and s.DEPTID is not null group by s.STEPID",
                    instanceId);
        }
        finally
        {
            setNativeSql(false);
        }
    }

    @OQL("select instanceId from OdFlowInstance i where instanceId>?1 order by instanceId limit 300")
    public abstract List<Long> queryInstanceIds(Long instanceId) throws Exception;
}
