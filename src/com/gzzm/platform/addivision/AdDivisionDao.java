package com.gzzm.platform.addivision;

import net.cyan.thunwind.annotation.*;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 和行政区划相关的数据操作
 *
 * @author camel
 * @date 2011-4-29
 */
public abstract class AdDivisionDao extends GeneralDao
{
    public AdDivisionDao()
    {
    }

    public AdDivision getRoot() throws Exception
    {
        AdDivision division = getAdDivision(0);
        if (division == null)
        {
            division = new AdDivision();
            division.setDivisionId(0);
            division.setDivisionName("根节点");
            division.setDivisionCode("");
            division.setLeftValue(0);
            division.setRightValue(1);
            save(division);
        }

        return division;
    }

    public AdDivision getAdDivision(Integer divisionId) throws Exception
    {
        return load(AdDivision.class, divisionId);
    }

    @GetByField("divisionCode")
    public abstract AdDivision getAdDivisionByCode(String divisionCode) throws Exception;

    /**
     * 获得某个行政区划的下属行政区划列表，一般用户多级行政区划联动
     *
     * @param parentDivisionId 上级行政区划ID
     * @return 行政区划列表
     * @throws Exception 数据库查询错误
     */
    @OQL("select d from AdDivision d where d.parentDivisionId=:1 order by leftValue")
    public abstract List<AdDivision> getAdDivisions(Integer parentDivisionId) throws Exception;

    /**
     * 获得首级行政区划
     *
     * @return 首级行政区划列表，可能是国家列表也可能是省列表，看系统业务需求
     * @throws Exception 数据库查询错误
     */
    public List<AdDivision> getFirstLevelDivisions() throws Exception
    {
        return getAdDivisions(0);
    }
}
