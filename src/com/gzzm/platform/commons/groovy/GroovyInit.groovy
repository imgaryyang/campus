package com.gzzm.platform.commons.groovy

import net.cyan.commons.util.SystemConfig
import net.cyan.groovy.crud.CrudInit
import net.cyan.groovy.thunwind.PersistenceInit

/**
 *
 * @author camel
 * @date 2016/2/19
 */
class GroovyInit implements Runnable {
    @Override
    void run() {
        net.cyan.groovy.commons.GroovyInit.init()

        SystemConfig.getInstance().addAfterLoad(new PersistenceInit())

        CrudInit.init()
        PageViewHelper.init()
    }
}
