package com.gzzm.platform.commons.groovy

import com.gzzm.platform.commons.crud.CBetween
import net.cyan.crud.view.components.CCombox
import net.cyan.crud.view.components.CData
import net.cyan.crud.view.components.Component
import net.cyan.groovy.crud.CrudViewHelper
import net.cyan.groovy.crud.ViewHelper

/**
 * 在groovy里对添加页面查询条件的支持
 * @author camel
 * @date 2016/2/19
 */
@SuppressWarnings(["GroovyAssignabilityCheck", "GrReassignedInClosureLocalVar", "UnnecessaryQualifiedReference"])
class MainPageComponents extends ViewHelper {
    static components = { Closure closure ->
        MainPageComponents.initComponents(delegate, false, closure)
    }

    static moreComponents = { Closure closure ->
        MainPageComponents.initComponents(delegate, true, closure)
    }

    boolean more

    MainPageComponents(Object view, boolean more) {
        super(view)
        this.more = more
    }

    static initComponents(view, boolean more, Closure closure) {
        def components = new MainPageComponents(view, more)
        components.init(view, closure)
    }

    void addResult(Object owner, String title, List parameters, Object functionResult, Map map, Closure closure) {

        String field
        String field2
        Integer width
        Object selectable

        for (def arg in parameters) {
            if (arg instanceof String) {
                if (field == null)
                    field = arg
                else if (field2 == null)
                    field2 = arg
            } else if (arg instanceof Integer) {
                if (width == null)
                    width = arg
            } else if (arg instanceof Collection || arg instanceof Object[]) {
                if (selectable == null)
                    selectable = arg
            }
        }

        Component component = null

        if (functionResult instanceof Component) {
            component = functionResult
        } else if (field) {
            if (field2)
                component = new CBetween(field, field2)
            else if (selectable != null)
                component = new CCombox(field, selectable)
            else
                component = new CData(field)
        } else if (closure) {
            component = getView().makeComponent(closure)
            closure = null
        }

        CrudViewHelper.initComponent(component, map, closure, getView())

        if (more)
            owner.addMoreComponent(title, component)
        else
            owner.addComponent(title, component)
    }
}
