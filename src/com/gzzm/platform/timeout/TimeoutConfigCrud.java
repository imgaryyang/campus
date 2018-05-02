package com.gzzm.platform.timeout;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.view.*;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 12-8-17
 */
@Service(url = "/timeout/config")
public class TimeoutConfigCrud extends DeptOwnedNormalCrud<TimeoutConfig, Integer>
{
    @Inject
    private TimeoutDao dao;

    @NotSerialized
    private List<TimeoutLevel> levels;

    /**
     * 主键为d+levelId
     */
    @NotSerialized
    private Map<String, Integer> days;

    private TimeoutTypeTreeModel typeTree;

    public TimeoutConfigCrud()
    {
        addOrderBy("configId");
    }

    public Map<String, Integer> getDays()
    {
        return days;
    }

    public void setDays(Map<String, Integer> days)
    {
        this.days = days;
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    public List<TimeoutLevel> getLevels() throws Exception
    {
        if (levels == null)
            levels = dao.getLevelLists();
        return levels;
    }

    @Select(field = "entity.typeId")
    public TimeoutTypeTreeModel getTypeTree()
    {
        if (typeTree == null)
            typeTree = new TimeoutTypeTreeModel();

        return typeTree;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public void initEntity(TimeoutConfig entity) throws Exception
    {
        super.initEntity(entity);
    }

    @Override
    public void afterLoad() throws Exception
    {
        super.afterLoad();

        days = new HashMap<String, Integer>();

        for (TimeoutDay day : getEntity().getDays())
        {
            days.put("d" + day.getLevelId(), day.getDay());
        }

        for (TimeoutLevel level : getLevels())
        {
            String s = "d" + level.getLevelId();
            if (!days.containsKey(s))
            {
                days.put(s, null);
            }
        }
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        super.beforeSave();

        if (days != null)
        {
            List<TimeoutDay> dayList = new ArrayList<TimeoutDay>(days.size());

            for (Map.Entry<String, Integer> entry : days.entrySet())
            {
                Integer levelId = Integer.valueOf(entry.getKey().substring(1));

                TimeoutDay day = new TimeoutDay();
                day.setDay(entry.getValue());
                day.setLevelId(levelId);

                dayList.add(day);
            }

            getEntity().setDays(dayList);
        }

        return true;
    }

    @Override
    public void afterChange() throws Exception
    {
        TimeoutConfig.setUpdated();
    }

    @Override
    protected Object createListView() throws Exception
    {
        ComplexTableView view = new ComplexTableView(new AuthDeptDisplay(), "deptId");

        view.addColumn("配置名称", "configName");
        view.addColumn("超时内容", new FieldCell("typeName").setOrderable(false)).setWidth("150");
        view.addColumn("时限单位", "unit");
        view.addColumn("默认时限", new FieldCell("timeLimit").setUnit("${unit}")).setWidth("80").setAlign(Align.center);

        for (TimeoutLevel level : getLevels())
        {
            Integer levelId = level.getLevelId();
            view.addColumn(level.getLevelName(), new FieldCell("getDay(" + levelId + ")").setUnit("${unit}"))
                    .setWidth("80").setAlign(Align.center);
        }

        view.defaultInit(false);

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("配置名称", "configName");
        view.addComponent("超时内容", "typeId").setProperty("text", getEntity().getTypeName());
        view.addComponent("时限单位", "unit");
        view.addComponent("默认时限", "timeLimit");

        for (TimeoutLevel level : getLevels())
        {
            view.addComponent(level.getLevelName(), "this.days.d" + level.getLevelId()).setProperty("max", 99)
                    .setProperty("min", -99);
        }

        view.addComponent("条件", new CTextArea("condition"));

        view.importCss("/platform/timeout/config.css");

        view.addDefaultButtons();

        return view;
    }
}