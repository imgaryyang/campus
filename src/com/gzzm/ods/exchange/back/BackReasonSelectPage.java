package com.gzzm.ods.exchange.back;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.StringUtils;
import net.cyan.crud.TreeListCrud;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 退文理由选择页面
 *
 * @author ldp
 * @date 2018/1/10
 */
@Service(url = "/ods/exchange/backreasonselect")
public class BackReasonSelectPage extends BaseListCrud<BackReasonBean> implements TreeListCrud<BackReasonBean, String>
{
    @Inject
    private BackReasonDao dao;

    @Inject
    private DeptService deptService;

    private String key;

    /**
     * 选中的退文理由
     */
    private List<Integer> selected;

    private Integer deptId;

    public BackReasonSelectPage()
    {
        setPageSize(0);
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public List<Integer> getSelected()
    {
        return selected;
    }

    public void setSelected(List<Integer> selected)
    {
        this.selected = selected;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    @Override
    protected void loadList() throws Exception
    {
        List<BackReasonBean> list = loadList0();
        if (list == null)
            list = Collections.emptyList();

        setList(list);
    }

    private List<BackReasonBean> loadList0() throws Exception
    {
        if (StringUtils.isEmpty(key))
        {
            return dao.getTypesForBean();
        }
        else
        {
            Integer typeId = Integer.valueOf(key.substring(2));
            List<BackReasonBean> reasons = dao.getReasonsForBean(typeId, deptId);

            if (reasons.size() > 0)
                return reasons;

            if (deptId == 1)
                return null;

            DeptInfo dept = deptService.getDept(deptId);
            do
            {
                reasons = dao.getReasonsForBean(typeId, dept.getParentDeptId());
                if (reasons.size() > 0)
                    return reasons;
            }
            while ((dept = dept.parentDept()).getDeptId() != 1);

            return null;
        }
    }

    @Override
    public String getKey(BackReasonBean entity) throws Exception
    {
        return entity.getType() + ":" + entity.getId();
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.setTitle("选择退文理由");
        view.setCheckable("${type==1}");
        view.addColumn("退文理由", "name");
        view.addButton(Buttons.getButton("确定", "ok()", "ok"));
        view.addButton(Buttons.getButton("取消", "cancel()", "cancel"));

        view.importJs("/ods/exchange/back/backreasonselect.js");

        return view;
    }

    @Override
    public String getParentField()
    {
        return "key";
    }

    @Override
    public boolean hasChildren(BackReasonBean entity)
    {
        return entity.getType() == 0;
    }

    @Service
    @NotSerialized
    public List<String> getReasonByKey(String[] keys) throws Exception
    {
        if (keys == null || keys.length == 0)
            return null;

        List<String> reasons = new ArrayList<String>(keys.length);
        for (String key : keys)
        {
            BackReason reason = dao.getBackReason(Integer.valueOf(key.substring(2)));
            if (reason != null && StringUtils.isNotEmpty(reason.getReason()))
                reasons.add(reason.getReason());
        }

        return reasons;
    }

    @Override
    public Class<String> getKeyType()
    {
        return String.class;
    }
}
