package com.gzzm.ods.sensitive;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import net.cyan.arachne.annotation.Service;
import net.cyan.crud.annotation.Like;

/**
 * 敏感词的Crud
 *
 * @author camel
 * @date 11-12-31
 */
@Service(url = "/ods/sensitive/word")
public class SensitiveWordCrud extends DeptOwnedNormalCrud<SensitiveWord, Integer>
{
    @Like
    private String word;

    public SensitiveWordCrud()
    {
        addOrderBy("word");
    }

    public String getWord()
    {
        return word;
    }

    public void setWord(String word)
    {
        this.word = word;
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
    protected Object createListView() throws Exception
    {
        PageTableView view = (getAuthDeptIds() != null && getAuthDeptIds().size() == 1) ? new PageTableView() :
                new ComplexTableView(new AuthDeptDisplay(), "deptId");

        view.addComponent("敏感词", "word");

        view.addColumn("敏感词", "word");

        view.defaultInit();

        return view;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();

        view.addComponent("敏感词", "word");

        view.addDefaultButtons();

        return view;
    }
}
