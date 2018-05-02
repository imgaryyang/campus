package com.gzzm.oa.help;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.PageTableView;
import com.gzzm.platform.commons.crud.SimpleDialogView;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.KeyValue;
import net.cyan.commons.util.Null;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.BaseSimpleCell;
import net.cyan.crud.view.components.CCombox;
import net.cyan.crud.view.components.CTextArea;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;

/**
 * 系统使用小窍门维护
 *
 * @author gyw
 * @date 2017/7/20 0020
 */
@Service(url = "/oa/help/sysuserskillcrud")
public class SysUseSkillCrud extends BaseNormalCrud<SysUseSkill, Integer> {

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Like
    private String content;

    private Integer state;

    public SysUseSkillCrud() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    protected String getComplexCondition() throws Exception {
        String condition = null;
        if (state != null && state != 0) {
            switch (state) {
                case 1:
                    condition = " publishTag = 1";
                    break;
                case 2:
                    condition = " publishTag = 0";
                    break;
            }
        }
        return super.getComplexCondition() == null ? condition : super.getComplexCondition() + " and " + condition;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView();
        view.addComponent("系统使用小窍门", "content");
        view.addComponent("发布状态", new CCombox("state", new Object[]{
                new KeyValue<String>("1", "已发布"), new KeyValue<String>("2", "未发布")
        }));
        view.addColumn("系统使用小窍门", "content");
        view.addColumn("创建人", "creator.userName").setAlign(Align.center);
        view.addColumn("创建时间", "createDate");
        view.addColumn("发布状态", new BaseSimpleCell() {
            @Override
            public Object getValue(Object o) throws Exception {
                SysUseSkill sus = (SysUseSkill) o;
                if (sus.getPublishTag() != null && sus.getPublishTag()) {
                    return "已发布";
                } else {
                    return "未发布";
                }
            }
        }).setAlign(Align.center);
        view.addColumn("更改状态", new BaseSimpleCell() {
            @Override
            public Object getValue(Object o) throws Exception {
                SysUseSkill sus = (SysUseSkill) o;
                if (sus.getPublishTag() != null && sus.getPublishTag()) {
                    return "<button onclick='noPublish(" + sus.getSkillId() + ")'>取消发布</button>";
                }
                return "<button onclick='publish(" + sus.getSkillId() + ")'>发布</button>";
            }
        }).setAlign(Align.center);
        view.addColumn("修改", new BaseSimpleCell() {
            @Override
            public Object getValue(Object o) throws Exception {
                SysUseSkill sus = (SysUseSkill) o;
                if (sus.getPublishTag() == null || !sus.getPublishTag()) {
                    return "<div title='修改' class='crud-button-edit' onclick=show('" + sus.getSkillId() + "','') ></div>";
                } else {
                    return "";
                }
            }
        }).setAlign(Align.center).setWidth("50");
        view.addDefaultButtons();
        view.setCheckable("${!publishTag}");
        view.importJs("/oa/help/sysuseskill/sysuseskill.js");
        return view;
    }

    @Override
    public boolean beforeInsert() throws Exception {
        getEntity().setCreatorId(userOnlineInfo.getUserId());
        getEntity().setCreateDate(new Date(System.currentTimeMillis()));
        return super.beforeInsert();
    }

    @Override
    protected Object createShowView() throws Exception {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("内容", new CTextArea("content").setHeight("300px").setWidth("500px").setProperty("style", "padding:0 10px"));
        view.setTitle("系统使用小技巧");
        view.setPage("double");
        view.addDefaultButtons();
        return view;
    }

    @Service(method = HttpMethod.all)
    public Integer changeTag(Integer id, Integer tag) throws Exception {
        SysUseSkill sus = getEntity(id);
        sus.setPublishTag(tag == 1);
        if(tag==1){
            sus.setPublishDate(new Date(System.currentTimeMillis()));
        }else{
            sus.setPublishDate(null);
        }
        return saveEntity(sus, Null.isNull(id));
    }


}
