package com.gzzm.safecampus.campus.siesta;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * 午休dao
 *
 * @author hmc
 * @date 2018/4/11
 */
public abstract class SiestaDao extends GeneralDao {

    /**
     * 根据午休室id获取午休室
     *
     * @param sroomId 午休室id
     * @return
     */
    @OQL("select s from SiestaRoom s where s.sroomId=:1 ")
    public abstract SiestaRoom getByRoomId(Integer sroomId);

    /**
     * 根据名称和部门id获取午休室
     *
     * @param name   午休室名称
     * @param deptId 部门id
     * @return
     */
    @OQL("select s from SiestaRoom s where s.name=:1 and s.deptId=:2")
    public abstract SiestaRoom getSiestaRoomByName(String name, Integer deptId);

    /**
     * 根据名称和部门id获取午休室
     *
     * @param name   午休室名称
     * @param deptId 部门id
     * @return
     */
    @OQL("select s from SiestaRoom s where s.name=:1 and s.deptId=:2")
    public abstract SiestaRoom getRoomByDeptId(String name, Integer deptId);

    /**
     * 根据学生id获取午休学生记录id
     *
     * @param studentId 学生id
     * @return
     */
    @OQL("select s.ssId from SiestaStudent s where s.studentId=:1")
    public abstract Integer selectByRoomIdAndStuId(Integer studentId);

    /**
     * 根据午休室id和学生id获取午休学生记录
     *
     * @param sroomId   午休室id
     * @param studentId 学生id
     * @return
     */
    @OQL("select ss from SiestaStudent ss where ss.sroomId = :1 and ss.studentId = :2")
    public abstract SiestaStudent getsietaByStu(Integer sroomId, Integer studentId);
}
