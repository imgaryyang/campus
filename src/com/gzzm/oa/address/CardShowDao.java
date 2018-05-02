package com.gzzm.oa.address;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

/**
 * @author ljb
 * @date 2017/2/17 0017
 */
public abstract class CardShowDao extends GeneralDao {
    public CardShowDao() {
    }

    @OQL("select a from AddressCard a where attributes[:1]=:2 order by a.cardId desc limit 1 ")
    public abstract AddressCard getAddressCardByEmpNo(String field,String empNo)throws Exception;
}
