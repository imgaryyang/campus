package com.gzzm.safecampus.campus.tutorship;

import com.gzzm.platform.commons.crud.BaseNormalCrud;
import com.gzzm.platform.commons.crud.PageTableView;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.Forward;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.InputFile;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.Like;

import java.io.File;
import java.util.Date;

/**
 * 机构信息CRUD
 *
 * @author yiuman
 * @date 2018/3/12
 */
@Service(url = "/campus/tutorship/tutorinsitutioncrud")
public class TutorInstitutionCrud extends BaseNormalCrud<TutorInstitution, Integer> {


    @Like
    private String institutionName;

    @Like
    private String phone;

    public TutorInstitutionCrud()
    {
        addOrderBy("createTime", OrderType.desc);
    }

    private InputFile file;

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public InputFile getFile() {
        return file;
    }

    public void setFile(InputFile file) {
        this.file = file;
    }

    @Override
    protected Object createListView() throws Exception {
        PageTableView view = new PageTableView();
        view.addComponent("机构名称", "institutionName");
        view.addComponent("联系电话", "phone");
        view.addColumn("机构名称", "institutionName");
        view.addColumn("联系电话", "phone");
        view.addColumn("客服QQ", "qq");
        view.addColumn("机构地址", "address");
        view.defaultInit(false);
        return view;
    }

    @Override
    public boolean beforeInsert() throws Exception
    {
        getEntity().setCreateTime(new Date());
        return super.beforeInsert();
    }

    @Override
    @Forward(page = "/safecampus/campus/turtor/insitution.ptl")
    public String add(String forward) throws Exception {
        return super.add(forward);
    }

    @Override
    @Forward(page = "/safecampus/campus/turtor/insitution.ptl")
    public String show(Integer key, String forward) throws Exception {
        return super.show(key, forward);
    }

    @Service(url = "/campus/tutorship/institution/{$0}")
    public byte[] getTeacherImage(Integer intstitutionId) throws Exception {
        if (intstitutionId == null)
            return new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/campus/icons/organ.jpg"))).getBytes();
        TutorInstitution intstitution = getEntity(intstitutionId);
        return intstitution.getPicture() == null ? new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/campus/icons/organ.jpg"))).getBytes() : intstitution.getPicture().getBytes();
    }
}
