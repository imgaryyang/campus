package com.gzzm.platform.devolve;

import java.util.Date;

/**
 * 工作移交器
 *
 * @author camel
 * @date 12-12-27
 */
public interface Devolver
{
    /**
     * 工作移交
     *
     * @param fromUserId 将此用户的工作移交给@toUserId
     * @param toUserId   将fromUserId的工作移交给此用户
     * @param startTime  开始时间，不为空表示只移交此时间之后的工作，为空表示没有限制
     * @param endTime    结束时间，不为空表示只移交此时间之前的工作，为空表示没有限制，
     *                   startTime和endTime可以同时不为空，表示只移交此时间段内的工作，都为空表示移交所有时间的工作
     * @throws Exception 允许实现类抛出异常
     */
    public void devolve(Integer fromUserId, Integer toUserId, Date startTime, Date endTime) throws Exception;
}