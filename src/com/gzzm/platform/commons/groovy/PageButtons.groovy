package com.gzzm.platform.commons.groovy

import net.cyan.crud.view.components.CButton
import net.cyan.crud.view.components.Component
import net.cyan.groovy.crud.CrudViewHelper
import net.cyan.groovy.crud.ViewHelper

/**
 * 在groovy里对添加页面按钮的支持
 * @author camel
 * @date 2016/2/19
 */
@SuppressWarnings(["GroovyAssignabilityCheck", "GrReassignedInClosureLocalVar"])
class PageButtons extends ViewHelper {
    PageButtons(Object view) {
        super(view)
    }

    static initButtons(view, Closure closure) {
        def buttons = new PageButtons(view)
        buttons.init(view, closure)
    }

    @Override
    void addResult(Object owner, String title, List parameters, Object functionResult, Map map, Closure closure) {
        String action = parameters.find { it instanceof CharSequence }

        Component component = null
        if (functionResult instanceof Component) {
            component = functionResult
        } else if (action) {
            component = new CButton(title, action)
        } else if (closure) {
            component = getView().makeComponents(closure)
            closure = null
        }

        if (component) {
            CrudViewHelper.initComponent(component, map, closure, getView())
            owner.addButton component
        }
    }
}
