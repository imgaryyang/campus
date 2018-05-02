package com.gzzm.platform.wordnumber;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import com.gzzm.platform.timeout.TimeoutConfig;
import net.cyan.arachne.annotation.*;

/**
 * @author camel
 * @date 12-8-17
 */
@Service(url = "/wordnumber/config")
public class WordNumberConfigCrud extends DeptOwnedNormalCrud<WordNumberConfig, Integer>
{
    public WordNumberConfigCrud()
    {
        addOrderBy("configId");
    }

    @NotSerialized
    public String getText() throws Exception
    {
        WordNumberConfig config = getEntity();
        if (config != null)
        {
            return config.getText();
        }

        return null;
    }

    @Override
    @Select(field = {"entity.wordNumberDeptId"})
    public AuthDeptTreeModel getDeptTree()
    {
        return super.getDeptTree();
    }

    @Override
    public String getOrderField()
    {
        return null;
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();

        setDefaultDeptId();
    }

    @Override
    public void initEntity(WordNumberConfig entity) throws Exception
    {
        super.initEntity(entity);
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

        view.addColumn("编号类型", "type.name").setWidth("200");
        view.addColumn("编号", "text").setOrderFiled("wordNumber");

        view.defaultInit(false);

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("编号类型", "type");
        view.addComponent("编号", "this.text");
        view.addComponent("编号部门", "wordNumberDeptId").setProperty("text",
                getEntity().getWordNumberDept() != null ? getEntity().getWordNumberDept().getDeptName() : "");

        view.addHidden("entity.wordNumber");

        view.importJs("/platform/wordnumber/config.js");
        view.importCss("/platform/wordnumber/config.css");

        view.addDefaultButtons();

        return view;
    }
}