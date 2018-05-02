package com.gzzm.ods.exchange.inout;

import com.gzzm.ods.exchange.ReceiveBase;
import com.gzzm.ods.redhead.RedHead;
import com.gzzm.ods.sendnumber.SendNumber;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author LDP
 * @date 2017/4/27
 */
public abstract class DocumentDao extends GeneralDao
{
    public DocumentDao()
    {
    }

    /**
     * 根据公文ID获取收文记录
     */
    @OQL("select r from ReceiveBase r where r.documentId = ?1")
    public abstract List<ReceiveBase> getReceiveBaseList(Long documentId) throws Exception;

    /**
     * 根据发文字号ID获取发文字号
     */
    @LoadByKey
    public abstract SendNumber getSendNumber(Integer sendNumberId) throws Exception;

    /**
     * 根据部门及名称获取发文字号
     */
    @OQL("select s from SendNumber s where s.deptId = ?1 and s.sendNumberName = ?2 limit 1")
    public abstract SendNumber getSendNumber(Integer deptId, String sendNumberName) throws Exception;

    /**
     * 根据红头模板ID获取红头模板
     */
    @LoadByKey
    public abstract RedHead getRedHead(Integer redHeadId) throws Exception;

    @OQL("select r from RedHead r where r.deptId = ?2 and r.redHeadName = ?2 limit 1")
    public abstract RedHead getRedHead(Integer deptId, String redHeadName) throws Exception;
}
