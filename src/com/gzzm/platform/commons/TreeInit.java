package com.gzzm.platform.commons;

import com.gzzm.platform.organ.DeptCrud;
import net.cyan.commons.test.*;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2011-3-7
 */
public class TreeInit
{
    @Inject
    private DeptCrud crud;

    public TreeInit()
    {
    }

    @TestCase
    public void test() throws Exception
    {
        crud.initTree(0);
    }

    public static void main(String[] args)
    {
        TestRunner.run();
    }
}
