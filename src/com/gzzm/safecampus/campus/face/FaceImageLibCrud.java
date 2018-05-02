package com.gzzm.safecampus.campus.face;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.organ.AuthDeptDisplay;
import com.gzzm.safecampus.campus.account.School;
import com.gzzm.safecampus.identification.*;
import com.gzzm.safecampus.s3.S3Service;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.OrderType;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 人脸图片库crud
 * @author zy
 * @date 2018/3/30 14:26
 */
@Service(url = "/campus/face/faceimagelib")
public class FaceImageLibCrud extends DeptOwnedNormalCrud<FaceImageLib, String>
{
    @Inject
    private FaceService faceService;

    @Inject
    private FaceDao faceDao;

    @Inject
    private S3Service s3Service;

    @Like
    private String imageName;

    private InputFile file;

    private ImageType imageType;

    private FaceResult faceResult;

    private String ideType;

    @Like
    private String faceId;

    public FaceImageLibCrud()
    {
        addOrderBy("createTime", OrderType.desc);
    }

    public String getImageName()
    {
        return imageName;
    }

    public void setImageName(String imageName)
    {
        this.imageName = imageName;
    }

    public InputFile getFile()
    {
        return file;
    }

    public void setFile(InputFile file)
    {
        this.file = file;
    }

    public ImageType getImageType()
    {
        return imageType;
    }

    public void setImageType(ImageType imageType)
    {
        this.imageType = imageType;
    }

    public String getFaceId()
    {
        return faceId;
    }

    public void setFaceId(String faceId)
    {
        this.faceId = faceId;
    }

    public FaceResult getFaceResult()
    {
        return faceResult;
    }

    public void setFaceResult(FaceResult faceResult)
    {
        this.faceResult = faceResult;
    }

    public String getIdeType()
    {
        return ideType;
    }

    public void setIdeType(String ideType)
    {
        this.ideType = ideType;
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
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

    @Override
    public String getAlias()
    {
        return "d";
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new ComplexTableView(new AuthDeptDisplay(),"deptId");
        view.addComponent("图片名称","imageName");
        view.addComponent("识别结果","faceResult");
        view.addComponent("图片类型","imageType");
        view.addComponent("所属人脸","faceId");
        view.addColumn("图片名称", "imageName").setWidth("250");
        view.addColumn("所属学校", "school.schoolName");
        view.addColumn("所属人脸", "faceId").setWidth("300");
        view.addColumn("姓名", "faceLibrary.personType==faceLibrary.personType.student?faceLibrary.student.studentName:''").setWidth("60");
        view.addColumn("识别结果", "faceResult").setWidth("80");
        view.addColumn("返回消息", "resultMsg").setWidth("400").setWrap(true);
        view.addColumn("创建时间", "createTime").setWidth("160");
        view.addColumn("摄像头", "camera.cameraName").setWidth("150");
        view.addButton(Buttons.query());
        view.addButton(Buttons.add("face","人脸识别"));
        view.addButton(Buttons.add("action","行为识别"));
        view.addButton(Buttons.delete());
        view.makeEditable();
        return view;
    }

    @Override
    public String add(String forward) throws Exception
    {
        setIdeType(forward);
        return super.add("");
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view=new SimpleDialogView();
        if(isNew$())
        {
            view.addComponent("比对图片","this.file");
            view.addButton(new CButton("比对","predictFace0()"));
        }
        else
        {
            view.addComponent("识别匹配人", new CImage(getEntity().getFaceId() == null ? Tools.getCommonIcon("blank") :
                    "/campus/faceLib/image/" + getEntity().getFaceId()).setWidth(100).setHeight(100));
            view.addComponent("当前图片", new CImage(getEntity().getImagePath() == null ? Tools.getCommonIcon("blank") :
                    "/campus/face/image/" + getEntity().getImageId()).setProperty("id", "icon").setWidth(100).setHeight(100));
            StringBuilder ret=new StringBuilder("错误消息："+getEntity().getResultMsg());
            if((FaceResult.Face==getEntity().getFaceResult()||FaceResult.All==getEntity().getFaceResult())&&getEntity().getFacePersonResultList()!=null)
            {
                ret.append("\n人脸结果：\n");
                for(FacePersonResult facePersonResult:getEntity().getFacePersonResultList())
                {
                    ret.append("坐标x:").append(facePersonResult.getImageX()).append(",").append("坐标y:").append(facePersonResult.getImageY()).append(",\n")
                            .append("脸宽:").append(facePersonResult.getFaceWidth()).append(",").append("脸高:").append(facePersonResult.getFaceHeight()).append(",\n")
                            .append("相识人脸id:").append(facePersonResult.getFaceId()).append(",").append("\n置信度:").append(facePersonResult.getConfidence()).append("\n");
                }
            }
            if((FaceResult.Action==getEntity().getFaceResult()||FaceResult.All==getEntity().getFaceResult())&&getEntity().getActionPersonResultList()!=null)
            {
                ret.append("\n行为结果：\n");
                for(ActionPersonResult actionPersonResult:getEntity().getActionPersonResultList())
                {
                    StringBuilder actionMsg= new StringBuilder();
                    if(StringUtils.isNotBlank(actionPersonResult.getActionId()))
                    {
                        String[] actionIds=actionPersonResult.getActionId().split(",");
                        for(String actionId:actionIds)
                        {
                            try
                            {
                                actionMsg.append(ActionType.values()[Integer.valueOf(actionId)]).append(",");
                            }
                            catch (Exception e)
                            {
                                actionMsg.append(actionId).append(",");
                            }
                        }
                        actionMsg = actionMsg.deleteCharAt(actionMsg.length()-1);
                    }
                    ret.append("坐标x:").append(actionPersonResult.getImageX()).append(",").append("坐标y:").append(actionPersonResult.getImageY()).append(",\n")
                            .append("人宽:").append(actionPersonResult.getActionWidth()).append(",").append("人高:").append(actionPersonResult.getActionHeight()).append(",\n")
                            .append("所属行为:").append(actionMsg).append(",").append("\n置信度:").append(actionPersonResult.getConfidence()).append("\n");
                }
            }
            getEntity().setResultMsg(ret.toString());
            view.addComponent("识别结果",new CTextArea("resultMsg"));
        }
        view.addButton(Buttons.close());
        view.importJs("/safecampus/campus/face/faceimagelib.js");
        return view;
    }

    @Service(method = HttpMethod.post)
    @ObjectResult
    public String predictFace() throws Exception
    {
        if(file!=null)
        {
            byte[] bytes=file.getBytes();
            School school=faceDao.getSchoolByDeptId(getDeptId());
            StringBuffer buffer=new StringBuffer();
            if(getIdeType()!=null)
            {
                if(getIdeType().equals("face"))
                {
                    List<ResultIde> ret= faceService.predictFaceFromImage(school.getSchoolCode(),bytes,buffer);
                    if(ret!=null)
                    {
                        for(ResultIde resultIde:ret)
                        {
                            for(ResultItem resultItem:resultIde.getResultItems())
                            {
                                buffer.append("识别人脸：").append(resultItem.getPersonId()).append(",可信度：")
                                        .append(resultItem.getConfidence()).append("\r\n");
                            }
                        }
                    }
                }
                else if(getIdeType().equals("action"))
                {
                    List<ResultIde> actionRet= faceService.predictActionFromImage(school.getSchoolCode(),bytes,buffer);
                    if(actionRet!=null)
                    {
                        for(ResultIde resultIde:actionRet)
                        {
                            if(StringUtils.isNotBlank(resultIde.getActionPersonId()))
                            {
                                buffer.append("识别用户：").append(resultIde.getActionPersonId());
                            }
                            for(ResultItem resultItem:resultIde.getResultItems())
                            {
                                buffer.append("识别行为：").append(resultItem.getActionId()).append(",可信度：")
                                        .append(resultItem.getConfidence()).append("\r\n");
                            }
                        }
                    }
                }
            }
            return buffer.toString();
        }
        return "未识别到人脸";
    }

    @Override
    public int removeAll(String[] keys) throws Exception
    {
        for(String key:keys)
        {
            faceService.delFace(key);
        }
        return keys.length;
    }

    /**
     * 获取图片
     * @param imageId 图片主键
     * @return 图片
     * @throws Exception 异常信息
     */
    @Service(url = "/campus/face/image/{$0}")
    public byte[] getImage(String imageId) throws Exception
    {
        return s3Service.readFile(getEntity(imageId).getImagePath());
    }
}
