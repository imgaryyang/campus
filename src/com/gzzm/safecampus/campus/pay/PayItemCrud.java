
package com.gzzm.safecampus.campus.pay;

import com.gzzm.platform.commons.*;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.safecampus.campus.base.BaseCrud;
import com.gzzm.safecampus.campus.bus.BusStudentDao;
import com.gzzm.safecampus.campus.classes.ClassesDao;
import com.gzzm.safecampus.campus.classes.GradeDao;
import com.gzzm.safecampus.campus.classes.Student;
import com.gzzm.safecampus.campus.classes.StudentDao;
import com.gzzm.safecampus.campus.common.Node;
import com.gzzm.safecampus.campus.trusteeship.TrusteeshipDao;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.crud.annotation.*;
import net.cyan.crud.view.Align;
import net.cyan.crud.view.BaseSimpleCell;
import net.cyan.crud.view.components.CButton;
import net.cyan.crud.view.components.CHref;
import net.cyan.crud.view.components.ConditionComponent;
import net.cyan.nest.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

/**
 * 缴费项目增删及项目账单缴费状态数量显示
 * 生成缴费账单
 *
 * @author yuanfang
 * @date 18-02-23 14:38
 */
@Service(url = "/campus/pay/payitemcrud")
public class PayItemCrud extends BaseCrud<PayItem, Integer>
{

    @Inject
    private GradeDao gradeDao;

    @Inject
    private StudentDao studentDao;

    @Inject
    private ClassesDao classesDao;

    @Inject
    private PayItemDao payItemDao;

    @Inject
    private TrusteeshipDao trusteeshipDao;

    @Inject
    private BillDao billDao;

    @Inject
    private BusStudentDao busStudentDao;

    /**
     * 项目编号
     */
    @Like
    private String serialNo;

    /**
     * 项目名称
     */
    @Like
    private String payItemName;

    /**
     * 缴费项目状态
     */
    private PayItemStatus payItemStatus;

    /**
     * 缴费状态
     */
    private PaymentType paymentType;

    private PayItem payItem = new PayItem();

    public PayItemCrud()
    {
    }

    public PayItemStatus getPayItemStatus()
    {
        return payItemStatus;
    }

    public void setPayItemStatus(PayItemStatus payItemStatus)
    {
        this.payItemStatus = payItemStatus;
    }

    public PaymentType getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType)
    {
        this.paymentType = paymentType;
    }

    public PayItem getPayItem()
    {
        return payItem;
    }

    public void setPayItem(PayItem payItem)
    {
        this.payItem = payItem;
    }

    public String getSerialNo()
    {
        return serialNo;
    }

    public void setSerialNo(String serialNo)
    {
        this.serialNo = serialNo;
    }

    public String getPayItemName()
    {
        return payItemName;
    }

    public void setPayItemName(String payItemName)
    {
        this.payItemName = payItemName;
    }

    @Override
    protected Object createListView() throws Exception
    {
        PageTableView view = new PageTableView();

        view.addComponent("项目编号", "serialNo");
        view.addComponent("缴费类型", "paymentType");
        view.addComponent("项目名称", "payItemName");

        view.addColumn("项目名称", new CHref("${payItemName}")
                .setAction("editPayment(${payItemId},0)")).setAlign(Align.left);
        view.addColumn("项目编号", "serialNo");
        view.addColumn("缴费金额(元)", "money").setWidth("100");
        view.addColumn("缴费类型", "paymentType").setWidth("80");
        view.addColumn("缴费说明", "desc");
        view.addColumn("截止时间", "deadline");
        view.addColumn("未缴", new BaseSimpleCell()
        {
            @Override
            public Object getValue(Object entity) throws Exception
            {
                PayItem payItem = (PayItem) entity;
                int payItemId = payItem.getPayItemId();
                if (payItem.getPayItemStatus() == PayItemStatus.hold)
                {
                    return "<a class='noSend'>未发送</a>";
                }
                return "<a onclick='viewPayItemList(" + payItemId + ",0)' >"
                        + billDao.getPayItemCountWait(payItemId) + "</a>";
            }
        }).setWidth("60").setAlign(Align.center);
        view.addColumn("已缴", new BaseSimpleCell()
        {
            @Override
            public Object getValue(Object entity) throws Exception
            {
                PayItem payItem = (PayItem) entity;
                int payItemId = payItem.getPayItemId();
                if (payItem.getPayItemStatus() == PayItemStatus.hold)
                {
                    return "<a class='noSend'>未发送</a>";
                }
                return "<a  onclick='viewPayItemList(" + payItemId + ",1)'>"
                        + billDao.getPayItemCountSucceed(payItemId) + "</a>";
            }
        }).setWidth("60").setAlign(Align.center);


        view.addColumn("操作", new ConditionComponent()
                .add("payItemStatus.name()=='hold'",
                        new CButton("编辑", "editPayment(${payItemId},'${payItemStatus}')")
                                .setClass("editCol"))
                .add("payItemStatus.name()!='hold'",
                        new CButton("补发", "editPayment(${payItemId},'${payItemStatus}')")
                                .setClass("addSendCol"))).setWidth("60");

        view.addColumn("账单", new ConditionComponent()
                .add("payItemStatus.name()=='hold'",
                        new CButton("发送", "viewPayItemBills(${payItemId},'${payItemStatus}')")
                                .setClass("sendCol"))
                .add("payItemStatus.name()!='hold'",
                        new CButton("账单", "viewPayItemBills(${payItemId},'${payItemStatus}')")
                                .setClass("billCol"))).setWidth("60");

        view.addButton(Buttons.query());
        view.addButton(Buttons.add());
        view.addButton(Buttons.delete());
        view.importJs("/safecampus/campus/payment/cont.js");
        view.importCss("/safecampus/campus/payment/view.css");
        return view;
    }

    /**
     * 发送页面
     */
    @Service(url = "/campus/pay/payItem/sendPage/{$0}")
    public String sendPage(Integer payItemId) throws Exception
    {
        if (getEntity(payItemId) != null)
        {
            payItem = getEntity(payItemId);
        }
        return "/safecampus/campus/payment/send.ptl";
    }

    @Override
    @Forward(page = "/safecampus/campus/payment/show.ptl")
    public String show(Integer key, String forward) throws Exception
    {
        return super.show(key, forward);
    }

    @Override
    @Forward(page = "/safecampus/campus/payment/show.ptl")
    public String add(String forward) throws Exception
    {
        return super.add(forward);
    }

    @Override
    public boolean beforeSave() throws Exception
    {
        getEntity().setPayItemStatus(PayItemStatus.hold);
        getEntity().setCreatTime(new Date(System.currentTimeMillis()));
        return super.beforeSave();
    }

    /**
     * 发送账单学生 POST
     * 兼容火狐
     * @param payItemId 缴费项目ID
     * @return 发送账单数量
     * @throws Exception 数据库异常
     */
    @Service(url = "/campus/pay/payItem/sendPostTo/{$0}", method = HttpMethod.post)
    @Json
    public Integer sendBillsOfPost(Integer payItemId,String studentArray) throws Exception
    {
        Tools.debug("发送账单学生集合: " + studentArray);
        return sendBills(payItemId, studentArray);
    }

    /**
     * 发送账单学生 GET
     * @param payItemId 缴费项目ID
     * @return 发送账单数量
     * @throws Exception 数据库异常
     */
    @Service(url = "/campus/pay/payItem/sendTo/{$0}", method = HttpMethod.get)
    @Json
    public Integer sendBills(Integer payItemId, String studentArray) throws Exception
    {

        PayItem payItem = getEntity(payItemId);
        if (studentArray.length() > 0)
        {
            //前端以 - 连接学生ID
            String[] s = studentArray.split("-");
            List<Integer> stus = new ArrayList<>();
            if (s.length > 0)
            {
                for (String s1 : s)
                {
                    stus.add(Integer.parseInt(s1));
                }
                //发送
                List<Bill> list = new ArrayList<>();

                if (stus.size() > 0 && payItem != null)
                {
                    for (Integer id : stus)
                    {
                        //账单不重复，防止重复发送
                        Integer check = billDao.checkBill(payItem.getPayItemId(), id);
                        if (check != null)
                        {
                            continue;
                        }
                        Bill bill = new Bill();
                        bill.setPayItemId(payItem.getPayItemId());
                        bill.setMoney(payItem.getMoney());
                        bill.setCreateTime(new Timestamp(System.currentTimeMillis()));
                        bill.setDeadline(payItem.getDeadline());
                        bill.setDeptId(payItem.getDeptId());
                        bill.setBillStatus(BillStatus.Wait);
                        //以“项目编号 - 学生Id”作为账单编号
                        bill.setSerialNo(payItem.getSerialNo() + "-" + id);
                        Student student = studentDao.getStudent(id);
                        //微信账单查询及发送账单需关联班级和学生
                        bill.setStudentId(id);
                        bill.setStudent(student);
                        bill.setClassesId(student.getClassesId());
                        bill.setPayItem(payItem);
                        list.add(bill);
                        billDao.save(bill);
                    }
                   // billDao.saveEntities(list);
                    payItem.setPayItemStatus(PayItemStatus.send);
                    payItemDao.update(payItem);

                    //发送微信账单通知
                    PaymentService service = new PaymentService();
                    service.sendUnpaidBillMsg(list);
                    Tools.log("已生成并发送" + list.size() + "个账单，缴费项目：" + payItem.getPayItemId());
                }
            }
            return stus.size();
        }
        return 0;
    }

    /**
     * 缴费项目管理：发送账单数据
     * type: tuition: 获取 学费缴纳 年级、班级、学生
     * type: host: 获取 托管费缴纳： 托管室、学生
     * type: bus: 获取 校巴费缴纳： 线路、学生
     *
     * @return json
     */
    @Service(url = "/campus/pay/payItem/getStuMapList/{$0}/{$1}/{$2}")
    @Json
    public Map<String, List> stuMapList(Integer type, Integer payItemId, Integer status) throws Exception
    {
        //将dao获取数据封装到 Node 对象属性：id、name、parentId，减小不必要字段的开销

        Map<String, List> map = new HashMap<>();
        if (status == 1)
        {
            Integer deptId = payItemDao.getDeptId(payItemId);
            if (type == 0)
            //学费
            {
                map.put("grade", gradeDao.getGradeByDept(deptId));
                map.put("class", classesDao.getClassesByDept(deptId));
                List<Node> list = studentDao.getStudentByDept(deptId);
                //Collections.sort(list);
                map.put("student", list);
            } else if (type == 1)
            //托管室
            {
                map.put("room", trusteeshipDao.getRoomByDeptId(deptId));
                List<Node> list = trusteeshipDao.getRoomStudentByDeptId(deptId);
                //Collections.sort(list);
                map.put("student", list);
            } else if (type == 2)
            {
                //校巴
                map.put("bus", busStudentDao.getBusByDeptId(deptId));
                List<Node> list = busStudentDao.getBusStudentByDeptId(deptId);
                // Collections.sort(list);
                map.put("student", list);
            }
        } else if (status == 0)
        {
            map.put("student", billDao.getStudentByPayItemId(payItemId));
        }
        return map;
    }

    @Override
    public String getDeleteTagField()
    {
        return "deleteTag";
    }

}


