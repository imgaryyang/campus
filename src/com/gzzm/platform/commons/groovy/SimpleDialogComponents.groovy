package com.gzzm.platform.commons.groovy

import net.cyan.crud.view.components.CCombox
import net.cyan.crud.view.components.CData
import net.cyan.crud.view.components.Component
import net.cyan.groovy.crud.CrudViewHelper
import net.cyan.groovy.crud.ViewHelper

/**
 * 在groovy里对添加新增和修改页面中的控件的支持
 * @author camel
 * @date 2016/2/19
 */
@SuppressWarnings(["GroovyUnusedCatchParameter", "GroovyAssignabilityCheck", "GrReassignedInClosureLocalVar"])
class SimpleDialogComponents extends ViewHelper {
    SimpleDialogComponents(Object view) {
        super(view)
    }

    static void initComponents(view, Closure closure) {
        def components = new SimpleDialogComponents(view)
        components.init(view, closure)
    }

    @Override
    List getDefaultPropertyValue(String name) {
        if (name == "duple" || name == "require")
            return [name, true]
        else
            null
    }

    @Override
    void addResult(Object owner, String title, List parameters, Object functionResult, Map map, Closure closure) {

        String field = parameters.find { it instanceof String }
        Object selectable = parameters.
                find { it instanceof Collection || it instanceof Object[] }

        Component component = null
        if (functionResult instanceof Component) {
            component = functionResult
        } else if (field) {
            if (selectable != null)
                component = new CCombox(field, selectable)
            else
                component = new CData(field)
        } else if (closure) {
            component = getView().makeComponent(closure)
            closure = null
        }

        if (closure)
            CrudViewHelper.initComponent(component, null, closure, getView())

        def item = owner.item(title, component)

        if (map) {
            map.each { String name, value ->
                try {
                    item[name] = value
                }
                catch (MissingPropertyException ignored) {
                    CrudViewHelper.initComponentProperty(component, name, value, getView())
                }
            }
        }

        owner.addComponent item
    }
}
