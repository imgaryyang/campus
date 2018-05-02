package com.gzzm.safecampus.campus.face;

import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.s3.S3Service;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.*;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 人脸库crud
 *
 * @author zy
 * @date 2018/3/30 14:34
 */
@Service(url = "/campus/face/facelibrary")
public class FaceLibraryCrud extends DeptOwnedNormalCrud<FaceLibrary, String>
{
    private PersonType personType;

    @Like
    private String faceId;

    private InputFile file;
    private InputFile file2;
    private InputFile file3;
    private InputFile file4;
    private InputFile file5;

    private String campusSerialNo;

    @Inject
    private FaceService faceService;

    @Inject
    private FaceDao faceDao;

    @Inject
    private S3Service s3Service;

    private List<FaceImageLib> faceImageLibs;

    public FaceLibraryCrud()
    {
        addOrderBy("createTime", OrderType.desc);
    }

    public PersonType getPersonType()
    {
        return personType;
    }

    public void setPersonType(PersonType personType)
    {
        this.personType = personType;
    }

    public String getFaceId()
    {
        return faceId;
    }

    public void setFaceId(String faceId)
    {
        this.faceId = faceId;
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    public String getCampusSerialNo()
    {
        return campusSerialNo;
    }

    public void setCampusSerialNo(String campusSerialNo)
    {
        this.campusSerialNo = campusSerialNo;
    }

    public List<FaceImageLib> getFaceImageLibs()
    {
        return faceImageLibs;
    }

    public void setFaceImageLibs(List<FaceImageLib> faceImageLibs)
    {
        this.faceImageLibs = faceImageLibs;
    }

    public InputFile getFile2()
    {
        return file2;
    }

    public void setFile2(InputFile file2)
    {
        this.file2 = file2;
    }

    public InputFile getFile3()
    {
        return file3;
    }

    public void setFile3(InputFile file3)
    {
        this.file3 = file3;
    }

    public InputFile getFile4()
    {
        return file4;
    }

    public void setFile4(InputFile file4)
    {
        this.file4 = file4;
    }

    public InputFile getFile5()
    {
        return file5;
    }

    public void setFile5(InputFile file5)
    {
        this.file5 = file5;
    }

    @NotCondition
    @Override
    public Integer getDeptId()
    {
        return super.getDeptId();
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        return "(select 1 from Dept l,Dept r where l.leftValue>=r.leftValue and l.rightValue<=r.rightValue and l.deptId=d.deptId and r.deptId=?deptId) is not empty";
    }

    @Override
    public String getAlias()
    {
        return "d";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new ComplexTableView(new AuthDeptDisplay(), "deptId");
        view.addComponent("用户类型", "personType");
        view.addComponent("人脸库主键", "faceId");
        view.addColumn("用户主键", "personId").setWidth("120");
        view.addColumn("用户类型", "personType").setWidth("120");
        view.addColumn("所属学校", "school.schoolName");
        view.addColumn("人脸库主键", "faceId").setWidth("300px");
        view.addColumn("学生人脸编号", "personType==personType.student?student.campusSerialNo:''").setWidth("120px");
        view.addColumn("学生姓名", "personType==personType.student?student.studentName:''").setWidth("80");
        view.addColumn("创建时间", "createTime").setWidth("160px");
        view.addButton(Buttons.query());
        view.addButton(Buttons.add());
        view.addButton(new CButton("人脸库培训", "retrainFace0()"));
        view.addButton(Buttons.delete());
        view.makeEditable();
        view.importJs("/safecampus/campus/face/faceimagelib.js");
//        view.makeEditable();
        return view;
    }

    @Service
    @ObjectResult
    public String retrainFace()
    {
        return faceService.retrainFace() ? "培训成功" : "培训失败";
    }

    @Override
    @Forward(page = "/safecampus/campus/face/facelibrary.ptl")
    public String show(String key, String forward) throws Exception
    {
        setFaceImageLibs(faceDao.getFaceImageLibs(key));
        return super.show(key, forward);
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        if(isNew$())
        {
            view.addComponent("学生编号", "this.campusSerialNo");
            view.addComponent("上传人脸", "this.file");
            view.addComponent("上传人脸", "this.file2");
            view.addComponent("上传人脸", "this.file3");
            view.addComponent("上传人脸", "this.file4");
            view.addComponent("上传人脸", "this.file5");
            view.addDefaultButtons();
        }
        else
        {
            view.addComponent("人脸图片", new CImage(
                    "/campus/faceLib/image/" + getEntity().getFaceId()).setWidth(100).setHeight(100));
        }
        return view;
    }

    private void addFace(InputFile file0) throws Exception
    {
        if (file0 != null)
        {
            byte[] bytes = file0.getBytes();
            School school = faceDao.getSchoolByDeptId(getDeptId());
            String result =
                    faceService.addFace(school.getSchoolCode(), campusSerialNo, bytes, file0.getName(), new Date());
            if (StringUtils.isBlank(result))
                throw new MessageException("人脸入库失败");
        }
    }

    /**
     * 获取图片
     * @param faceId 人脸主键
     * @return 图片
     * @throws Exception 异常信息
     */
    @Service(url = "/campus/faceLib/image/{$0}")
    public byte[] getImage(String faceId) throws Exception
    {
        FaceImageLib faceImageLib=faceDao.getFaceImageByFaceId(faceId,ImageType.FaceLib);
        if(faceImageLib!=null&&StringUtils.isNotBlank(faceImageLib.getImagePath()))
            return s3Service.readFile(faceImageLib.getImagePath());
        return null;
    }

    @Override
    @Transactional(mode = TransactionMode.not_supported)
    public String save() throws Exception
    {
        addFace(file);
        addFace(file2);
        addFace(file3);
        addFace(file4);
        addFace(file5);
        return "ok";
    }
}
