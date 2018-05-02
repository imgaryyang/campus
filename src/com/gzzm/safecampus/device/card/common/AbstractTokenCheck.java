package com.gzzm.safecampus.device.card.common;

/**
 * @author liyabin
 * @date 2018/3/21
 */
public abstract class AbstractTokenCheck implements TokenInteface
{
    public AbstractTokenCheck()
    {
    }

    @Override
    public boolean checkToken() throws Exception
    {
        return true;
    }

    @Override
    public void initToken() throws Exception
    {
    }


}
