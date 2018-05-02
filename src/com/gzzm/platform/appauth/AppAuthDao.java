package com.gzzm.platform.appauth;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.IOUtils;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;
import net.cyan.thunwind.meta.*;

import java.sql.ResultSet;
import java.util.*;

/**
 * 和应用模块权限相关的数据操作
 *
 * @author camel
 * @date 2011-5-13
 */
public abstract class AppAuthDao extends GeneralDao
{
    public AppAuthDao()
    {
    }

    @LoadByKey
    public abstract AppAuth getAuth(Integer authId) throws Exception;

    @OQL("select authId from AppAuth where type=:1 and appId=:2")
    public abstract Integer getAuthId(String type, Integer appId) throws Exception;

    @OQL("select a from AppAuth a where type=:1 and appId=:2")
    public abstract AppAuth getAuth(String type, Integer appId) throws Exception;

    @OQL("select i from AppAuthItem i where auth.type=:1 and auth.appId=:2 order by type,objectId")
    public abstract List<AppAuthItem> getAuthItems(String type, Integer appId) throws Exception;

    @OQL("select i from AppAuthItem i where authId=:1")
    public abstract List<AppAuthItem> getAuthItems(Integer authId) throws Exception;

    public AppAuthItem getAuthItem(Integer authId, AuthType type, Integer id) throws Exception
    {
        return load(AppAuthItem.class, authId, type, id);
    }

    /**
     * 获得某些对象拥有权限的应用Id
     *
     * @param appType   应用类型，为一个字符串，由具体的应用模块定义
     * @param authType  权限对象类型，如用户，部门，岗位
     * @param objectIds 对象ID列表，如用户ID，部门ID，岗位ID
     * @return objectIds代表的对象拥有的应用的id列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select distinct auth.appId from AppAuthItem where auth.type=:1 and type=:2 and objectId in :3")
    public abstract List<Integer> getAppIds(String appType, AuthType authType, Collection<Integer> objectIds)
            throws Exception;

    @OQL("select i,[i.auth] from AppAuthItem i where auth.type=:1 and auth.appId in ?4 and type=:2 and objectId in :3")
    public abstract List<AppAuthItem> getAuthItems(String appType, AuthType authType, Collection<Integer> objectIds,
                                                   Collection<Integer> appIds) throws Exception;

    @OQL("select distinct i.type,i.objectId,i.scopeId from AppAuthItem i where auth.type=:1 and auth.appId in :2 and type=?3")
    public abstract List<ObjectScope> getObjectScopes(String appType, Collection<Integer> appIds, AuthType authType)
            throws Exception;


    /**
     * 获得某些对象对某个应用拥有权限的部门范围ID
     *
     * @param appType   应用类型，为一个字符串，由具体的应用模块定义
     * @param appId     应用ID
     * @param authType  权限对象类型，如用户，部门，岗位
     * @param objectIds 对象ID列表，如用户ID，部门ID，岗位ID
     * @return objectIds代表的对象拥有的应用的id列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select distinct scopeId from AppAuthItem where auth.type=:1 and auth.appId=:2 " +
            "and type=:3 and objectId in :4")
    public abstract List<Integer> getScopeIds(String appType, Integer appId, AuthType authType,
                                              Collection<Integer> objectIds) throws Exception;

    /**
     * 获得某些对象对某个应用拥有权限的部门范围ID
     *
     * @param authId    权限ID
     * @param authType  权限对象类型，如用户，部门，岗位
     * @param objectIds 对象ID列表，如用户ID，部门ID，岗位ID
     * @return objectIds代表的对象拥有的应用的id列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select distinct scopeId from AppAuthItem where authId=:1 and type=:2 and objectId in :3")
    public abstract List<Integer> getScopeIds(Integer authId, AuthType authType, Collection<Integer> objectIds)
            throws Exception;

    public void deleteItem(Integer authId, AuthType type, Integer objectId) throws Exception
    {
        delete(AppAuthItem.class, authId, type, objectId);
    }

    public boolean tableExists(String schema, String tableName) throws Exception
    {
        return getSession().getDialect().isObjectExists(null, schema, tableName, ObjectType.table, getConnection());
    }

    public void createTable(String schema, String tableName, List<AppDept> appDepts) throws Exception
    {
        String tableName1 = tableName + "_t";

        try
        {
            TableInfo tableInfo = new TableInfo(null, schema, tableName1);

            tableInfo.addColumn(new ColumnInfo("APPID", "number(9)"));
            tableInfo.addColumn(new ColumnInfo("DEPTID", "number(7)"));
            tableInfo.addConstraint(new PrimaryKeyConstraint(tableName, "APPID", "DEPTID"));

            tableInfo.create(getConnection(), getSession().getDialect());

            if (schema != null)
                tableName1 = schema + "." + tableName1;

            Map<String, Object> map = new HashMap<String, Object>();
            for (AppDept appDept : appDepts)
            {
                map.put("APPID", appDept.getAppId());
                Integer deptId = appDept.getDeptId();

                if (deptId == null)
                    map.put("DEPTID", 0);
                else
                    map.put("DEPTID", deptId);

                sqlInsert(tableName1, map);
            }

            executeNativeSql("alter table " + tableName1 + " rename to " + tableName);
        }
        catch (Throwable ex)
        {
            try
            {
                executeNativeSql("drop table " + tableName1);
            }
            catch (Throwable ex1)
            {
                Tools.log(ex1);
            }
        }
    }

    public void clearTables(String type, String schema) throws Exception
    {
        ResultSet rs = null;
        try
        {
            rs = getConnection().getMetaData()
                    .getTables(null, schema, getTablePrefix(type.toUpperCase()) + "%", new String[]{"TABLE"});

            List<String> tables = new ArrayList<String>();
            while (rs.next())
            {
                tables.add(rs.getString("TABLE_NAME"));
            }

            rs.close();
            rs = null;

            for (String table : tables)
            {
                if (schema != null)
                    table = schema + "." + table;

                executeNativeSql("drop table " + table);
            }
        }
        finally
        {
            IOUtils.close(rs);
        }
    }

    public static String getTablePrefix(String type)
    {
        type = type.toUpperCase();

        String[] ss = type.split("_");

        if (ss.length > 1)
        {
            String s1 = ss[ss.length - 1];
            if (s1.length() > 3)
                s1 = s1.substring(0, 3);
            return ss[0] + "_" + s1;
        }

        return type;
    }
}
