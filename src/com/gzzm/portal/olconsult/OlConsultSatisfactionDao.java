package com.gzzm.portal.olconsult;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wym
 * Date: 13-6-5
 * Time: 上午11:54
 * 满意度管理dao
 */
public abstract class OlConsultSatisfactionDao extends GeneralDao{

    public OlConsultSatisfactionDao()
    {

    }

    @OQL("select o from OlConsultSatisfaction o where typeId=1 order by orderId")
    public abstract List<OlConsultSatisfaction> getAllSatisfaction();

}
