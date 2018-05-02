package com.gzzm.platform.commons.groovy

import com.gzzm.platform.commons.crud.Action
import net.cyan.groovy.crud.ViewHelper

/**
 * 在groovy里对简单页面的actions添加支持
 * @author camel
 * @date 2016/2/25
 */
class SimplePageListActions extends ViewHelper {
    SimplePageListActions(Object view) {
        super(view)
    }

    static void initActions(view, Closure closure) {
        def actions = new SimplePageListActions(view)
        actions.init(view, closure)
    }

    @Override
    void addResult(Object owner, String title, List parameters, Object functionResult, Map map, Closure closure) {
        Action result
        if (functionResult instanceof Action) {
            result = functionResult
        }

        Object action
        Object icon = null

        for (def arg in parameters) {
            if ((action == null || icon == null) && !(arg instanceof Map)) {
                if (arg instanceof Closure) {
                    Closure c = arg
                    arg = c()
                }

                if (action == null) {
                    action = arg
                    if (action == null)
                        return
                } else if (icon == null) {
                    icon = arg
                }
            }

            if (action) {
                result = new Action(title, action, icon)
            }
        }

        if (result)
            owner.addAction result
    }
}
