package com.gzzm.portal.datacollection;

import com.gzzm.platform.commons.NoErrorException;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.AbstractPageTableView;
import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.ComplexTableView;
import com.gzzm.platform.commons.crud.PageTableView;
import net.cyan.arachne.annotation.Forward;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.CollectionUtils;
import net.cyan.commons.util.Null;
import net.cyan.commons.util.StringUtils;
import net.cyan.commons.util.json.JsonParser;
import net.cyan.commons.util.json.JsonSerializer;
import net.cyan.crud.OrderType;
import net.cyan.nest.annotation.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据列表CRUD
 *
 * @author ldp
 * @date 2018/4/25
 */
@Service(url = "/portal/dc/datalist")
public class DataListCrud extends BaseNormalCrud<DataList, String> {

    @Inject
    private DataCollectionDao dao;

    private Integer typeId;

    private Map<String, Object> timeVal;

    public DataListCrud() {
        addOrderBy("collectTime", OrderType.desc);
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Map<String, Object> getTimeVal() {
        return timeVal;
    }

    public void setTimeVal(Map<String, Object> timeVal) {
        this.timeVal = timeVal;
    }

    @Override
    protected Object createListView() throws Exception {
        AbstractPageTableView view;
        if(typeId != null) {
            view = new PageTableView();
        } else {
            view = new ComplexTableView(Tools.getBean(DataTypeSelect.class), "typeId");
        }

        view.addColumn("采集时间", "collectTime");
        view.addColumn("数值", "dataVal");
        view.addColumn("单位", "type.unit");

        view.defaultInit(false);

        view.importJs("/portal/datacollection/datalist.js");

        return view;
    }

    @Override
    protected String getComplexCondition() throws Exception {
        // 没有选择目录时不查询数据
        if(Null.isNull(typeId)) return "1 <> 1";

        return super.getComplexCondition();
    }

    @Override
    public void initEntity(DataList entity) throws Exception {
        super.initEntity(entity);

        if (Null.isNull(typeId)) throw new NoErrorException("typeId cannot be null");

        entity.setTypeId(typeId);
        entity.setType(dao.get(DataType.class, typeId));
    }

    @Override
    @Forward(page = "/portal/datacollection/datalist.ptl")
    public String add(String forward) throws Exception {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/portal/datacollection/datalist.ptl")
    public String show(String key, String forward) throws Exception {
        return super.show(key, forward);
    }

    @Override
    public void afterLoad() throws Exception {
        super.afterLoad();

        if(StringUtils.isNotBlank(getEntity().getTimeVal())){
            timeVal = new HashMap<String, Object>();
            new JsonParser(getEntity().getTimeVal()).parse(timeVal);
        }
    }

    @Override
    public boolean beforeSave() throws Exception {
        if (CollectionUtils.isEmpty(timeVal)) throw new NoErrorException("请求参数有误");

        DataType type = dao.get(DataType.class, getEntity().getTypeId());

        if(type.getTimeType().getDataEnhancer() != null) {
            Tools.getBean(type.getTimeType().getDataEnhancer()).enhance(timeVal);
        }

        Pattern p = Pattern.compile("\\{(\\w+)\\}");
        Matcher m = p.matcher(type.getTimeType().getTemplate());

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, timeVal.get(m.group(1)).toString());
        }
        m.appendTail(sb);

        String collectTime = sb.toString();

        DataList dl = dao.getByTypeAndCollectTime(getEntity().getTypeId(), collectTime);
        if(dl != null) {
            if(isNew$()) {
                throw new NoErrorException("记录【" + collectTime + "】已存在，不能重复添加");
            } else if(!dl.getListId().equals(getEntity().getListId())) {
                throw new NoErrorException("记录【" + collectTime + "】已存在，请修改原来的记录");
            }
        }

        getEntity().setCollectTime(collectTime);

        getEntity().setTimeVal(new JsonSerializer().serialize(timeVal).toString());


        return super.beforeSave();
    }
}
