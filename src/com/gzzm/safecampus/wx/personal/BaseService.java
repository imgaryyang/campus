package com.gzzm.safecampus.wx.personal;

import com.gzzm.platform.message.Message;
import com.gzzm.platform.message.sms.SmsMessageSender;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.face.FaceService;
import com.gzzm.safecampus.campus.tutorship.TutorTeacher;
import com.gzzm.safecampus.campus.wx.tag.TagService;
import com.gzzm.safecampus.wx.user.WxLoginService;
import com.gzzm.safecampus.wx.user.WxUserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.RequestContext;
import net.cyan.arachne.annotation.ObjectResult;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;
import net.cyan.thunwind.annotation.DAO;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Date;
import java.util.Random;

/**
 * 认证与注册基本service
 *
 * @author yiuman
 * @date 2018/3/28
 */
@Service
public class BaseService {
    @Inject
    protected WxUserOnlineInfo wxUserOnlineInfo;

    @Inject
    protected WxLoginService wxLoginService;

    @Inject
    protected WxAuthDao wxAuthDao;

    @Inject
    protected TagService tagService;

    @Inject
    private FaceService faceService;

    public WxUserOnlineInfo getWxUserOnlineInfo() {
        return wxUserOnlineInfo;
    }


    public BaseService() {
    }


    @Service
    public void sendVerifCode(String phone) throws Exception {
        if (StringUtils.isNotBlank(phone)) {
            String captcha = StringUtils.leftPad(Integer.toString(new Random().nextInt(9999)), 4, '0');
            HttpSession session = RequestContext.getContext().getSession();
            session.setAttribute("MOCAPTCHA:" + phone, captcha);
            //5分钟过期
            session.setMaxInactiveInterval(30 * 60);
            Message message = new Message();
            message.setPhone(phone);
            message.setForce(true);
            message.setMethods(SmsMessageSender.SMS);
            message.setMessage("尊敬的用户，您正在进行招in校园认证的操作，短信验证码为:" + captcha + "(仅对本次操作及5分钟内有效)");
            message.send();
        }
    }

    /**
     * 获取学生头像
     *
     * @param studetnId
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/centerpage/image/{$0}")
    public byte[] getStudentImage(Integer studetnId) throws Exception {
        if (studetnId == null)
            return new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/wx/image/tx_06_2.png"))).getBytes();
        Student studetn = wxAuthDao.get(Student.class, studetnId);
        return studetn.getPicture() == null ? new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/wx/image/tx_06_2.png"))).getBytes() : studetn.getPicture().getBytes();
    }

    /**
     * 获取老师头像
     *
     * @param teacherId
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/teahcer/image/{$0}")
    public byte[] getTeacherImage(Integer teacherId) throws Exception {
        if (teacherId == null)
            return new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/campus/icons/teacher.jpg"))).getBytes();
        TutorTeacher teacher = wxAuthDao.get(TutorTeacher.class, teacherId);
        return teacher.getPicture() == null ? new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/campus/icons/teacher.jpg"))).getBytes() : teacher.getPicture().getBytes();
    }

    /**
     * 获取学生人脸照片
     *
     * @param faceType
     * @param studentId
     * @return
     * @throws Exception
     */
    @Service(url = "/wx/face/image/{$0}/{$1}")
    public byte[] getFaceImage(FaceType faceType, Integer studentId) throws Exception {
        if (studentId == null || studentId == 0)
            return new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/wx/image/face_" + faceType.ordinal() + ".png"))).getBytes();
        StudentFaces faces = wxAuthDao.getFaceByStudentIdAndType(studentId, faceType);
        return (faces == null || faces.getPhoto() == null) ? new InputFile(new File(RequestContext.getContext().getRealPath("/safecampus/wx/image/face_" + faceType.ordinal() + ".png"))).getBytes() : faces.getPhoto().getBytes();
    }

    /**
     * 保存人面照片
     *
     * @param faceList  人面照片信息传输类
     * @param studentId 学生Id
     * @throws Exception
     */
    @Service(method = HttpMethod.post)
    @ObjectResult
    public String saveFaces(FaceDTO[] faceList, Integer studentId) throws Exception {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(faceList)) {
            for (FaceDTO dto : faceList) {
                StudentFaces faces = wxAuthDao.getFaceByStudentIdAndType(studentId, FaceType.values()[dto.getFaceType()]);
                if (faces == null) {
                    faces = new StudentFaces();
                }
                faces.setStudentId(studentId);
                faces.setFaceType(FaceType.values()[dto.getFaceType()]);
                String data = dto.getBase64();
                String type = "png";
                String base64;
                //苹果与安卓获取到的 base64数据不一样   安卓为纯base64,苹果前面带类型 用,隔开
                if (data.contains(",")) {
                    String[] base64data = data.split(",");
                    type = base64data[0].substring(base64data[0].indexOf("/") + 1, base64data[0].indexOf(";"));
                    base64 = base64data[1];
                } else {
                    base64 = data;
                }
                if (StringUtils.isBlank(type) || StringUtils.isBlank(base64)) continue;
                byte[] base64Byte = CommonUtils.base64ToByteArray(base64);
                String imageName = "Face" + dto.getFaceType() + studentId + "." + type;
                String imgId = null;
                if (faces.getFaceId() == null || StringUtils.isNotBlank(faces.getImageId())) {
                    imgId = faceService.addFace(studentId, base64, imageName, new Date(), null);
                } else {
                    imgId = faceService.updateFace(faces.getImageId(), base64Byte, imageName);
                }
                if (imgId == null) {
                    builder.append(DataConvert.getEnumString(faces.getFaceType()) + ",");
                    continue;
                }
                faces.setImageId(imgId);
                Inputable photo = new InputFile(base64Byte, imageName).getInputable();
                faces.setPhoto(photo);
                wxAuthDao.save(faces);
                //如果是正面照 保存到学生照片里
                if(FaceType.ANDY ==faces.getFaceType()){
                    Student student = wxAuthDao.get(Student.class,studentId);
                    student.setPicture(photo);
                    wxAuthDao.save(student);
                }
            }
        }
        String result = null;
        if (builder.length() > 0) {
            result = "[" + builder.substring(0, builder.length() - 1) + "]  识别失败,请重新上传相应照片";
        }
        return result;
    }

}
