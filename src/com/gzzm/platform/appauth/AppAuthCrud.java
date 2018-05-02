package com.gzzm.platform.appauth;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.crud.DeletableCrud;
import net.cyan.crud.exporters.ExportParameters;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 2011-5-13
 */
@Service
public abstract class AppAuthCrud extends BaseListCrud<AuthItemShow> implements DeletableCrud<AuthItemShow, String>
{
    @Inject
    protected AppAuthService service;

    @Inject
    private OrganDao organDao;

    @AuthDeptIds
    private Collection<Integer> authDeptIds;

    /**
     * 对象的名称，作为查询条件
     */
    private String name;

    /**
     * 要添加的对象ID列表
     */
    private Integer[] objectIds;

    /**
     * 访问ID
     */
    private Integer scopeId;

    /**
     * 添加对象时，展示和选择其添加的权限
     */
    @NotSerialized
    private App[] apps;

    private String appType;

    private Integer appId;

    private AuthType authType;

    private Integer objectId;

    /**
     * 可选择的部门树，当添加部门时使用
     */
    private AuthDeptTreeModel deptTree;

    @MenuId
    private String app;

    private Integer[] scopeIds;

    private String[] keys;

    public AppAuthCrud()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer[] getObjectIds()
    {
        return objectIds;
    }

    public void setObjectIds(Integer[] objectIds)
    {
        this.objectIds = objectIds;
    }

    public Integer getScopeId()
    {
        return scopeId;
    }

    public void setScopeId(Integer scopeId)
    {
        this.scopeId = scopeId;
    }

    public App[] getApps()
    {
        return apps;
    }

    public void setApps(App[] apps)
    {
        this.apps = apps;
    }

    public String getAppType()
    {
        return appType;
    }

    public void setAppType(String appType)
    {
        this.appType = appType;
    }

    public Integer getAppId()
    {
        return appId;
    }

    public void setAppId(Integer appId)
    {
        this.appId = appId;
    }

    public AuthType getAuthType()
    {
        return authType;
    }

    public void setAuthType(AuthType authType)
    {
        this.authType = authType;
    }

    public Integer getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Integer objectId)
    {
        this.objectId = objectId;
    }

    public String getApp()
    {
        return app;
    }

    public void setApp(String app)
    {
        this.app = app;
    }

    public Integer[] getScopeIds()
    {
        return scopeIds;
    }

    public void setScopeIds(Integer[] scopeIds)
    {
        this.scopeIds = scopeIds;
    }

    public String[] getKeys()
    {
        return keys;
    }

    public void setKeys(String[] keys)
    {
        this.keys = keys;
    }

    @Override
    public Class<String> getKeyType()
    {
        return String.class;
    }

    @Override
    public String getKey(AuthItemShow entity) throws Exception
    {
        return AuthObject.toString(entity);
    }

    protected List<AuthObject> getSelectedAuthObjects()
    {
        if (keys == null)
            return null;

        if (keys.length == 0)
            return null;

        List<AuthObject> authObjects = new ArrayList<AuthObject>(keys.length);

        for (String key : keys)
        {
            authObjects.add(AuthObject.parse(key));
        }

        return authObjects;
    }

    public AuthDeptTreeModel getDeptTree()
    {
        if (deptTree == null)
            deptTree = new AuthDeptTreeModel();

        return deptTree;
    }

    @NotSerialized
    public List<RoleScope> getScopes() throws Exception
    {
        if (scopeIds != null)
        {
            List<RoleScope> scopes = new ArrayList<RoleScope>(scopeIds.length);

            for (Integer scopeId : scopeIds)
            {
                if (scopeId == -1)
                {
                    RoleScope all = new RoleScope();
                    all.setScopeId(Null.Integer);
                    all.setScopeName("所有部门");
                    scopes.add(all);
                }
                else
                {
                    scopes.add(organDao.getScope(scopeId));
                }
            }

            return scopes;
        }

        return null;
    }

    protected abstract App[] createApps();

    @Override
    protected void loadList() throws Exception
    {
        App[] apps = createApps();

        List<AuthItemShow> list = service.getAuthItemShows(apps);

        if (!StringUtils.isEmpty(name))
        {
            for (Iterator<AuthItemShow> iterator = list.iterator(); iterator.hasNext(); )
            {
                AuthItemShow item = iterator.next();
                if (!item.getName().contains(name))
                    iterator.remove();
            }
        }

        setAllList(list);
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(true);

        view.addComponent("名称", "name");
        view.addButton(Buttons.query());

        view.addColumn("名称", "name").setWidth("150");
        view.addColumn("类型", "type");
        view.addColumn("所属部门", "deptName").setAutoExpand(true);

        App[] apps = createApps();

        for (int i = 0; i < apps.length; i++)
        {
            App app = apps[i];

            if ("exp".equals(getAction()))
            {
                view.addColumn(app.getName(), "valids[" + i + "]");
            }
            else
            {
                view.addColumn(app.getName(),
                        new CUnion(new CCheckbox("valids[" + i + "]").setProperty("onclick",
                                "setAuth('" + app.getType() + "'," + app.getId() +
                                        ",'${type}',${objectId},this.checked)"),
                                new CButton("设置范围", "showScope('" + app.getType() + "'," + app.getId() +
                                        ",'${type}',${objectId})"))).setWidth("170");
            }
        }

        view.addButton("添加部门", "showAddDepts()");
        view.addButton("添加用户", "showAddUsers()");
        view.addButton("添加岗位", "showAddStations()");
        view.addButton(Buttons.delete());
        view.addButton(Buttons.export("xls"));

        view.importJs("/platform/appauth/appauth.js");

        return view;
    }

    @Service
    @Forwards({@Forward(name = "user", page = "/platform/appauth/user.ptl"),
            @Forward(name = "dept", page = "/platform/appauth/dept.ptl"),
            @Forward(name = "station", page = "/platform/appauth/station.ptl")})
    public String showAdd(String forward)
    {
        setApps(createApps());

        if (scopeIds != null && scopeIds.length > 0)
            scopeId = scopeIds[0];

        return forward;
    }

    @ObjectResult
    @Service(method = HttpMethod.post)
    public void addUsers() throws Exception
    {
        addItems(AuthType.user);
    }

    @ObjectResult
    @Service(method = HttpMethod.post)
    public void addDepts() throws Exception
    {
        addItems(AuthType.dept);
    }

    @ObjectResult
    @Service(method = HttpMethod.post)
    public void addStations() throws Exception
    {
        addItems(AuthType.station);
    }

    private void addItems(AuthType type) throws Exception
    {
        if (apps == null)
            apps = createApps();

        service.addAuthItems(apps, type, objectIds, scopeId);
    }

    @ObjectResult
    @Service
    public void setAuth(String appType, Integer appId, AuthType authType, Integer objectId, boolean valid)
            throws Exception
    {
        service.setAuth(appType, appId, authType, objectId, valid);
    }

    /**
     * 设置范围
     *
     * @throws Exception 数据库操作错误
     */
    @ObjectResult
    @Service
    public void setScope() throws Exception
    {
        service.setScope(appType, appId, authType, objectId, scopeId);
    }

    /**
     * 显示设置范围的页面
     *
     * @param appType  应用类型，由具体的应用模块定义
     * @param appId    应用id
     * @param authType 权限类型，部门，用户或者岗位
     * @param objectId 对象ID
     * @return 设置范围的页面
     * @throws Exception 数据库获取数据错误
     */
    @Service
    @Forward(page = "/platform/appauth/scope.ptl")
    public String showScope(String appType, Integer appId, AuthType authType, Integer objectId) throws Exception
    {
        this.appType = appType;
        this.appId = appId;
        this.authType = authType;
        this.objectId = objectId;

        scopeId = service.getScopeId(appType, appId, authType, objectId);

        return null;
    }

    @Override
    protected ExportParameters getExportParameters() throws Exception
    {
        return new ExportParameters("权限列表");
    }

    private void remove(AuthObject authObject) throws Exception
    {
        for (App app : createApps())
        {
            service.setAuth(app.getType(), app.getId(), authObject.getType(), authObject.getObjectId(), false);
        }
    }

    @Override
    @Transactional
    public boolean remove(String key) throws Exception
    {
        remove(AuthObject.parse(key));
        return true;
    }

    @Override
    @Transactional
    public int removeAll(String[] keys) throws Exception
    {
        if (keys != null)
            this.keys = keys;

        List<AuthObject> authObjects = getSelectedAuthObjects();
        if (authObjects == null)
            return 0;

        for (AuthObject authObject : authObjects)
        {
            remove(authObject);
        }

        return authObjects.size();
    }
}