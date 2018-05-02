package com.gzzm.platform.wordnumber;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.DateUtils;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.CButton;
import net.cyan.nest.annotation.Inject;

import java.util.Date;

/**
 * 流水号维护
 *
 * @author camel
 * @date 11-10-27
 */
@Service(url = "/serial")
public class SerialCrud extends DeptOwnedEditableCrud<Serial, String>
{
    @Inject
    private WordNumberDao dao;

    private String type;

    private Integer year;

    @Like
    private String serialName;

    public SerialCrud()
    {
        addOrderBy("serialName");
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    public String getSerialName()
    {
        return serialName;
    }

    public void setSerialName(String serialName)
    {
        this.serialName = serialName;
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    public String getKey(Serial entity) throws Exception
    {
        return entity.getSerialName();
    }

    @Override
    public void setKey(Serial entity, String key) throws Exception
    {
        entity.setSerialName(key);
    }

    @Override
    public Serial getEntity(String key) throws Exception
    {
        return dao.getSerial(type, getDeptId(), year, key);
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        if (type == null)
            throw new NullPointerException("type cannot be null");

        super.beforeShowList();

        setDefaultDeptId();

        if (year == null)
            year = DateUtils.getYear(new Date());
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = (getAuthDeptIds() != null && getAuthDeptIds().size() == 1) ? new PageTableView() :
                new ComplexTableView(new AuthDeptDisplay(), "deptId");

        view.addComponent("流水号名称", "serialName");

        view.addButton(Buttons.query());
        view.addButton(Buttons.delete());
        view.addButton(new CButton("初始化", "initialize()"));

        view.addColumn("流水号名称", "serialName");
        view.addColumn("当前值", "serialValue");

        view.makeEditable();

        view.importJs("/platform/wordnumber/serial.js");

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("流水号值", "serialValue");
        view.addDefaultButtons();

        return view;
    }

    @Override
    public int deleteAll() throws Exception
    {
        return dao.deleteSerials(type, getDeptId(), getYear(), getKeys());
    }

    @Override
    public boolean delete(String key) throws Exception
    {
        return dao.deleteSerials(type, getDeptId(), getYear(), key) > 0;
    }

    @Service
    public void initialize() throws Exception
    {
        dao.initialize(type, getDeptId(), getYear(), getKeys());
    }
}
