package com.gzzm.ods.exchange.back;

import com.gzzm.ods.exchange.Back;
import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * @author camel
 * @date 2018/1/29
 */
public abstract class BackDao extends GeneralDao
{
    public BackDao()
    {
    }

    @LoadByKey
    public abstract Back getBack(Long backId) throws Exception;

    @GetByField("receiveId")
    public abstract Back getBackByReceiveId(Long receiveId) throws Exception;

    @LoadByKey
    public abstract BackRecord getBackRecord(Long recordId) throws Exception;

    @GetByField("backId")
    public abstract BackRecord getBackRecordByBackId(Long backId) throws Exception;

    @GetByField("back.receiveId")
    public abstract BackRecord getBackRecordByReceiveId(Long backId) throws Exception;

    @LoadByKey
    public abstract BackPaper getBackPaper(Integer paperId) throws Exception;

    @OQL("select p from BackPaper p where p.deptId=:1 order by orderId")
    public abstract List<BackPaper> getBackPagers(Integer deptId) throws Exception;

    @LoadByKey
    public abstract BackNumber getBackNumber(Integer backNumberId) throws Exception;

    @OQL("select n from BackNumber n where n.deptId=:1")
    public abstract List<BackNumber> getBackNumbers(Integer deptId) throws Exception;
}
