package com.gzzm.safecampus.campus.pay;

import com.gzzm.safecampus.campus.common.Node;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author yuanfang
 * @date 18-03-01 18:23
 */

public abstract class BillDao extends GeneralDao
{
    public BillDao()
    {
    }

    /**
     * 已缴费数量：由缴费项目生成的账单
     *
     * @param payItemId 缴费项目id
     * @return 账单数量
     * @throws Exception oql异常
     */
    @OQL("select count(*) from Bill  where payItemId =:1 and billStatus = 1 and deleteTag =0 and cancelFlag =0 ")
    public abstract Integer getPayItemCountSucceed(Integer payItemId) throws Exception;

    /**
     * 未缴费数量：由缴费项目生成的账单
     *
     * @param payItemId 缴费项目id
     * @return 账单数量
     * @throws Exception oql异常
     */
    @OQL("select count(*) from Bill  where payItemId =:1 and billStatus = 0 and deleteTag =0 and cancelFlag =0 ")
    public abstract Integer getPayItemCountWait(Integer payItemId) throws Exception;

    /**
     * 缴费项目生成的账单发送给的所有学生集合
     *
     * @param payItemId 缴费项目id
     * @return 学生集合
     * @throws Exception oql异常
     */
    @OQL("select studentId as nodeId ,student.studentName as nodeName from Bill  " +
            "where payItemId =:1 and (deleteTag=0 or deleteTag is null) and cancelFlag =0 ")
    public abstract List<Node> getStudentByPayItemId(Integer payItemId) throws Exception;

    /**
     * 由账单id获取账单
     *
     * @param billId 账单id
     * @return 账单
     * @throws Exception oql异常
     */
    @OQL("select b from Bill b where billId =:1")
    public abstract Bill getBillById(Integer billId) throws Exception;

    /**
     * 由家长关联的学生，获取学生所有账单
     *
     * @param guardianId 家长id
     * @return 学生所有账单集合
     * @throws Exception oql
     */
    @OQL("select b from Bill b where studentId in( " +
            "select studentId from Guardian where guardianId =:1) " +
            "and b.deleteTag =0 and b.cancelFlag =0" +
            " order by  b.billStatus,b.payItem.paymentType ")
    public abstract List<Bill> getBillsByGuardianId(Integer guardianId) throws Exception;

    /**
     * 根据家属Id获取他的学生的type类型的账单，按缴费状态和缴费类型排序
     *
     * @param guardianId 家属Id
     * @param type       缴费类型
     * @return 账单集合
     * @throws Exception oql异常
     */
    @OQL("select b from Bill b where studentId in( " +
            "select studentId from Guardian where guardianId =:1)" +
            " and billStatus =0 and payItem.paymentType =?2 and deleteTag =0 and cancelFlag =0 " +
            "order by  b.billStatus , b.createTime, b.payItem.paymentType ")
    public abstract List<Bill> getBillsByGuardianIdAndType(Integer guardianId, Integer type) throws Exception;

    /**
     *  根据学生ID获取的type类型的账单，按缴费状态和缴费类型升序，账单时间降序排序
     * @param studentIds
     * @param type
     * @return
     * @throws Exception
     */
    @OQL("select b from Bill b where studentId in :1 " +
            "and billStatus =0 and payItem.paymentType =?2 and deleteTag =0 and cancelFlag =0 " +
            "order by  b.billStatus asc, b.createTime desc, b.payItem.paymentType ")
    public abstract List<Bill> getBillsByStudentsAndType(Integer[] studentIds, Integer type) throws Exception;

    /**
     * 验证账单号的唯一性，不生成重复账单
     * （不含已删除的无效账单编号）
     *
     * @param payItemId 项目id
     * @param studentId 学生id
     * @return 账单id
     * @throws Exception oql异常
     */
    @OQL("select b.billId from Bill b where b.payItemId =:1 and b.studentId=:2 and cancelFlag =0 ")
    public abstract Integer checkBill(Integer payItemId, Integer studentId) throws Exception;

    /**
     * 将账单登记为已缴
     *
     * @param paymentMethod 支付方式
     * @param billStatus    账单支付状态
     * @param date          时间
     * @param userId        系统操作用户id
     * @param billIds       账单id数组
     * @throws Exception oql异常
     */
    @OQLUpdate("update Bill set paymentMethod =:1 ,billStatus =:2  , payTime =:3 ,operatorId =:4 where billId in :5")
    public abstract void setPaid(PaymentMethod paymentMethod, BillStatus billStatus, Timestamp date, Integer userId, Integer[] billIds) throws Exception;

    public abstract List<Bill> getBillsByStudentGuardiansId(Integer uid);

    /**
     *  根据学生ID获取的type类型的账单，按缴费状态和缴费类型升序，账单时间降序排序
     * @param studentIds
     * @return
     * @throws Exception
     **
     */
    @OQL("select b from Bill b where studentId in :1 " +
            "and b.deleteTag =0 and b.cancelFlag =0 " +
            "order by  b.billStatus asc, b.createTime desc, b.payItem.paymentType")
    public abstract List<Bill> getBillsByStudentIds(Integer[] studentIds) throws Exception;

    /**
     * 账单超时未支付设过期状态
     * @param expired
     * @param date
     */
    @OQLUpdate("update Bill set billStatus =:1 where deadline <:2 and billStatus=0")
    public abstract Integer checkBillsTimeout(BillStatus expired, Date date);
}

