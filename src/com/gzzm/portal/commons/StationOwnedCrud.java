package com.gzzm.portal.commons;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.portal.cms.station.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.BeanUtils;
import net.cyan.crud.CrudConfig;
import net.cyan.crud.annotation.*;

import java.util.*;

/**
 * 属于某个站点的数据的维护
 *
 * @author camel
 * @date 13-9-24
 */
@Service
public abstract class StationOwnedCrud<E, K> extends DeptOwnedEditableCrud<E, K> implements OwnedCrud<E, K, Integer>
{
    private static String[] ORDERWITHFIELDS = new String[]{"stationId"};

    /**
     * 站点作为查询条件
     */
    private Integer stationId;

    @NotSerialized
    private Station station;

    private StationList stationList;

    public StationOwnedCrud()
    {
        setLog(true);
    }

    public Integer getStationId()
    {
        return stationId;
    }

    public void setStationId(Integer stationId)
    {
        this.stationId = stationId;
    }

    @Override
    @In("station.deptId")
    @NotSerialized
    public Collection<Integer> getQueryDeptIds()
    {
        return super.getQueryDeptIds();
    }

    @Override
    @Equals("station.deptId")
    public Integer getDeptId()
    {
        return super.getDeptId();
    }

    @Select(field = {"stationId", "entity.stationId"})
    public StationList getStationList() throws Exception
    {
        if (stationList == null)
            stationList = Tools.getBean(StationList.class);

        return stationList;
    }

    protected Collection<Station> getStations() throws Exception
    {
        return getStationList().getItems();
    }

    @NotSerialized
    public boolean isOneStationOnly() throws Exception
    {
        return getAuthDeptIds() != null && getStations().size() == 1;
    }

    public Station getStation() throws Exception
    {
        if (station == null && stationId != null)
            station = getStation(stationId);

        return station;
    }

    protected Integer getStationId(E entity) throws Exception
    {
        return (Integer) BeanUtils.getValue(entity, "stationId");
    }

    protected Station getStation(Integer stationId) throws Exception
    {
        return getCrudService().get(Station.class, stationId);
    }

    protected Station getStation(E entity) throws Exception
    {
        Integer stationId = getStationId(entity);

        if (stationId == null)
            return null;

        return getStation(stationId);
    }

    protected void setStationId(E entity, Integer stationId) throws Exception
    {
        BeanUtils.setValue(entity, "stationId", stationId);
    }

    protected void setStation(E entity, Station station)
    {
        try
        {
            BeanUtils.setValue(entity, "station", station);
        }
        catch (Throwable ex)
        {
            //原因是setStation方法不存在，跳过
        }
    }

    @Override
    public Integer getDeptId(E entity) throws Exception
    {
        Station station = getStation(entity);

        if (station == null)
            return null;

        return station.getDeptId();
    }

    @Override
    public void setDeptId(E entity, Integer deptId) throws Exception
    {
    }

    @Override
    public List<Integer> getDeptIds(Collection<K> keys) throws Exception
    {
        String keyField = CrudConfig.getPersistenceDialect().getKeyFields(getEntityType()).get(0);
        return getCrudService().oqlQuery("select station.deptId from " + getEntityName() + " where " +
                keyField + " in ?", keys, Integer.class);
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        if (isOneStationOnly())
        {
            Collection<Station> stations = getStations();

            Station station = stations.iterator().next();
            setStationId(station.getStationId());
        }
    }

    @Override
    public void initEntity(E entity) throws Exception
    {
        super.initEntity(entity);

        if (stationId != null)
        {
            setStationId(entity, stationId);
            setStation(entity, getStation(stationId));
        }
    }

    @Override
    protected String[] getOrderWithFields()
    {
        return ORDERWITHFIELDS;
    }

    public String getOwnerField()
    {
        return "stationId";
    }

    public Integer getOwnerKey(E entity) throws Exception
    {
        return getStationId(entity);
    }

    public void setOwnerKey(E entity, Integer deptId) throws Exception
    {
        setStationId(entity, deptId);
    }

    protected void checkKeys(Collection<K> keys, Integer stationId) throws Exception
    {
        DeptOwnedCrudUtils.checkKeys(keys, stationId == null ? null : getStation(stationId).getDeptId(), this);
    }

    protected void checkKeys(K[] key, Integer stationId) throws Exception
    {
        checkKeys(Arrays.asList(key), stationId);
    }

    protected void checkKey(K key, Integer stationId) throws Exception
    {
        checkKeys(Collections.singleton(key), stationId);
    }

    protected void checkKeys(Collection<K> keys) throws Exception
    {
        checkKeys(keys, null);
    }

    protected void checkKeys(K[] key) throws Exception
    {
        checkKeys(Arrays.asList(key), null);
    }

    protected void checkKey(K key) throws Exception
    {
        checkKeys(Collections.singleton(key), null);
    }

    protected void checkeKeys() throws Exception
    {
        checkKeys(getKeys());
    }

    public void moveTo(K key, Integer newStationId, Integer oldStationId) throws Exception
    {
        Collection<K> keyList = Collections.singleton(key);

        checkKeys(keyList, newStationId);

        OwnedCrudUtils.moveTo(keyList, newStationId, oldStationId, this);
    }

    @Transactional
    public void moveAllTo(K[] keys, Integer newStationId, Integer oldStationId) throws Exception
    {
        if (keys != null)
            setKeys(keys);

        Collection<K> keyList = Arrays.asList(keys);

        checkKeys(keyList, newStationId);

        OwnedCrudUtils.moveTo(keyList, newStationId, oldStationId, this);
    }

    public void copyTo(K key, Integer newStationId, Integer oldStationId) throws Exception
    {
        Collection<K> keyList = Collections.singleton(key);

        checkKeys(keyList, newStationId);

        OwnedCrudUtils.copyTo(keyList, newStationId, oldStationId, this);
    }

    @Transactional
    public void copyAllTo(K[] keys, Integer newStationId, Integer oldStationId) throws Exception
    {
        if (keys != null)
            setKeys(keys);

        Collection<K> keyList = Arrays.asList(keys);

        checkKeys(keyList, newStationId);

        OwnedCrudUtils.copyTo(keyList, newStationId, oldStationId, this);
    }
}
