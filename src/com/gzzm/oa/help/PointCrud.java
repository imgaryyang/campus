package com.gzzm.oa.help;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.commons.crud.Action;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.ObjectResult;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.transaction.Transactional;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.BaseSimpleCell;
import net.cyan.crud.view.components.CButton;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.crud.view.components.ConditionComponent;
import net.cyan.nest.annotation.Inject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 提示信息维护
 *
 * @author ljb
 * @date 2017/3/31 0031
 */
@Service(url = "/oa/help/pointcrud")
public class PointCrud extends BaseNormalCrud<Pointion, Integer> {

    @Like
    private String title;

    @Inject
    private PointDao dao;

    public PointCrud() {
        addOrderBy(" validTime desc");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView();
        view.addComponent("标题", "title");
        view.addColumn("标题", "title").setWidth("100");
        view.addColumn("内容", "pointContent");
        view.addColumn("有效时间", "validTime");
        view.addColumn("发布状态", new ConditionComponent().add("publicTag==0",
                new CButton("发布", "toPublish(${pointId})")).add("publicTag==1",
                new CButton("取消发布", "toPublish(${pointId})"))
        );
        view.addColumn("修改", new ConditionComponent().add("publicTag==0",
                Buttons.getButton("修改",Actions.show(null),Tools.getCommonIcon("edit")).setIcon(Tools.getCommonIcon("edit")))
        );
        view.setCheckable("${publicTag==0}");
        view.addDefaultButtons();
        view.addComponent(Buttons.getButton("发布信息", "toAllPublic()"));
        view.importJs("/oa/help/js/point.js");
        return view;
    }

    @Override
    protected Object createShowView() throws Exception {
        SimpleDialogView view = new SimpleDialogView();
        view.setPage("double");
        view.addComponent("标题", "title");
        view.addComponent("有效时间", "validTime", true);
        view.addDoubleComponent("提示内容", new CTextArea("pointContent"), true).setHeight(300);
        view.setTitle("提示信息维护");

        view.addDefaultButtons();
        return view;
    }

    /**
     * 单个发布
     *
     * @param pointId
     * @return
     * @throws Exception
     */
    @Service(method = HttpMethod.get)
    @ObjectResult
    public String toPublicTag(Integer pointId) throws Exception {
        Pointion pointion = getEntity(pointId);
        if (pointion != null) {
            if (pointion.isPublicTag() == true)
                pointion.setPublicTag(false);
            else pointion.setPublicTag(true);
            dao.save(pointion);
            return "操作成功";
        }
        return "操作失败";
    }

    /**
     * 多个发布
     *
     * @return
     * @throws Exception
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    @Transactional
    public String toAllPublicTag() throws Exception {

        for (Integer key : getKeys()) {
            Pointion pointion = getEntity(key);
            pointion.setPublicTag(true);
            dao.save(pointion);
        }
        return "操作成功";
    }

}
