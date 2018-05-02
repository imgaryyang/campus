package com.gzzm.safecampus.campus.device;

import com.gzzm.platform.annotation.UserId;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.campus.base.BaseCrud;
import com.gzzm.safecampus.campus.classes.ClassesTreeDisplay;
import com.gzzm.safecampus.device.card.service.DeviceService;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.crud.annotation.Like;
import net.cyan.crud.view.components.*;
import net.cyan.nest.annotation.Inject;

import java.sql.Date;
import java.util.*;

/**
 * 招行小白卡Crud
 *
 * @author yiuman
 * @date 2018/3/14
 */

@Service(url = "/campus/tutorship/devicecardcrud")
public class DeviceCardCrud extends BaseCrud<DeviceCard, Integer>
{

    @Inject
    private Provider<DeviceDao> daoProvider;

    @Inject
    DeviceService deviceService;

    @Like("student.studentName")
    private String studentName;

    @Like("student.studentNo")
    private String studentNo;

    @Like("teacher.teacherName")
    private String teacherName;

    @Like("teacher.teacherNo")
    private String teacherNo;

    @Like("cardNo")
    private String cardNo;

    @UserId
    private Integer userId;

    private Integer classesId;

    private Integer studentId;

    private CardType type;

    private CardStatus status;

    public Integer getStudentId()
    {
        return studentId;
    }

    public Integer getClassesId()
    {
        return classesId;
    }

    public void setClassesId(Integer classesId)
    {
        this.classesId = classesId;
    }

    public void setStudentId(Integer studentId)
    {
        this.studentId = studentId;
    }

    public CardType getType()
    {
        return type;
    }

    public void setType(CardType type)
    {
        this.type = type;
    }

    public CardStatus getStatus()
    {
        return status;
    }

    public void setStatus(CardStatus status)
    {
        this.status = status;
    }

    public String getCardNo()
    {
        return cardNo;
    }

    public void setCardNo(String cardNo)
    {
        this.cardNo = cardNo.trim();
    }

    public String getStudentNo()
    {
        return studentNo;
    }

    public void setStudentNo(String studentNo)
    {
        this.studentNo = studentNo.trim();
    }

    public String getStudentName()
    {
        return studentName;
    }

    public void setStudentName(String studentName)
    {
        this.studentName = studentName.trim();
    }

    public String getTeacherName()
    {
        return teacherName;
    }

    public void setTeacherName(String teacherName)
    {
        this.teacherName = teacherName;
    }

    public String getTeacherNo()
    {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo)
    {
        this.teacherNo = teacherNo;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    @Override
    protected String getComplexCondition() throws Exception
    {
        StringBuilder conditions = new StringBuilder();
        if (classesId != null && classesId != 0)
        {
            if (classesId < 0)
            {
                //选中年级节点，查询年级下的所有班级的数据
                conditions.append("student.classes.gradeId=" + (-classesId));
            }
            else
            {
                //选中班级节点,查询该班级的数据
                conditions.append("student.classesId="+classesId);
            }
        }else return null;
        return conditions.toString();
    }

    @Override
    protected void beforeShowList() throws Exception
    {
        super.beforeShowList();
        setDeptId(getDefaultDeptId());
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView(false);
        if (type == CardType.STUDENT)
        {
            view = new ComplexTableView(new ClassesTreeDisplay(), "classesId");
            view.addComponent("学生姓名", "studentName");
            view.addComponent("学号", "studentNo");
            view.addColumn("学生姓名", "student.studentName");
            view.addColumn("学号", "student.studentNo");

        }
        else
        {
            view.addComponent("教师姓名", "teacherName");
            view.addComponent("教师编码", "teacherNo");
            view.addColumn("教师姓名", "teacher.teacherName");
            view.addColumn("教师编码", "teacher.teacherNo");
        }
        view.addComponent("校园卡卡号", "cardNo");
        view.addComponent("校园卡状态", "status");
        view.addColumn("校园卡卡号", "cardNo");
        view.addColumn("校园卡类型", "type");
        view.addColumn("校园卡状态", "status");
        view.addColumn("发卡时间", "releaseTime");
        view.addColumn("过期时间", "expireTime");
        view.addColumn("操作",
                new ConditionComponent().add("crud$.check(status)", new CButton("退卡", "returnCard(${cardId})")));
        view.importJs("/safecampus/campus/device/deviceCard.js");
        view.addButton(Buttons.query());
        return view;
    }

    /**
     * 判断是出现审核按钮
     *
     * @param status
     * @return
     */
    public boolean check(CardStatus status)
    {
        return CardStatus.ACTIVE.equals(status);
    }

    @Service
    @ObjectResult
    public Map<String, String> returnCardByCardId(Integer cardId) throws Exception
    {
        Map<String, String> map = new HashMap<>();
        if (cardId == null)
        {

            map.put("success", "1");
            return map;
        }
        DeviceCard load = daoProvider.get().load(DeviceCard.class, cardId);
        load.setStatus(CardStatus.RETIRED);
        load.setReturnOperation(userId);
        daoProvider.get().save(load);
        map.put("success", "0");
        return map;
    }

    @Override
    protected Object createShowView() throws Exception
    {
        SimpleDialogView view = new SimpleDialogView();
        view.addComponent("学生", "targetId").setProperty("text", "${student.studentName}");
        if (isNew$())
            view.addComponent("校园卡卡号", "cardNo").setProperty("readOnly", "true")
                    .setProperty("text", "${deviceService.studentName}");
        else
            view.addComponent("校园卡卡号", "cardNo").setProperty("readOnly", "true");

        view.addComponent("校园卡序列号", "cardSn");
//        view.addComponent("校园卡类型", "type");
        view.addComponent("校园卡状态", "status");
        view.addComponent("发卡时间", "releaseTime");
        view.addComponent("过期时间", "expireTime");
        view.addDefaultButtons();
        return view;
    }

    @Override
    public void initEntity(DeviceCard entity) throws Exception
    {
        entity.setReleaseTime(new Date(System.currentTimeMillis()));
        super.initEntity(entity);
    }

    @Service
    public void reportTheLoss(Integer state)
    {
        Integer[] keys = getKeys();
        if (CollectionUtils.isNotEmpty(keys))
            daoProvider.get().changeStatus(keys, state);
    }

}
