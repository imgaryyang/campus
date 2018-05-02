package com.gzzm.platform.commons.groovy

import com.gzzm.platform.commons.crud.*
import net.cyan.crud.Crud
import net.cyan.crud.view.components.Component
import net.cyan.groovy.crud.CrudViewHelper
import net.cyan.groovy.crud.TableColumns
import net.cyan.groovy.crud.ViewHelper

/**
 *
 * @author camel
 * @date 2016/2/17
 */
class PageViewHelper {
    static void registerAction(String name) {
        registerAction(name, name)
    }

    static void registerAction(String name, final String action) {
        CrudViewHelper.registerComponent0(name, { ->

            def helper = ViewHelper.getHelper()
            if (helper instanceof SimplePageListActions)
                Action."$action"()
            else
                Buttons."$action"()
        })

        ViewHelper.metaClass."~$name" { String title, owner, List args ->

            def helper = ViewHelper.getHelper()
            if (helper instanceof SimplePageListActions)
                Action."$action"(title)
            else
                Buttons."$action"(title)
        }
    }

    static void registerAction1(String name) {
        registerAction1(name, name)
    }

    static void registerAction1(String name, final String action) {
        CrudViewHelper.registerComponent1_0(name, { String s = null ->

            def helper = ViewHelper.getHelper()
            if (helper instanceof SimplePageListActions)
                Action."$action"(s)
            else
                Buttons."$action"(s)
        })
        ViewHelper.metaClass."~$name" { String title, owner, List args ->

            def s = args.find { it instanceof String }

            def helper = ViewHelper.getHelper()
            if (helper instanceof SimplePageListActions)
                Action."$action"(title, s)
            else
                Buttons."$action"(title, s)
        }
    }

    static void init() {
        PageView.metaClass.buttons { Closure closure ->
            PageButtons.initButtons(delegate, closure)
        }

        MainPageView.metaClass.components MainPageComponents.components
        MainPageView.metaClass.conditions MainPageComponents.components
        MainPageView.metaClass.moreComponents MainPageComponents.moreComponents
        MainPageView.metaClass.moreConditions MainPageComponents.moreComponents
        PageChartView.metaClass.components MainPageComponents.components
        PageChartView.metaClass.conditions MainPageComponents.components
        PageChartView.metaClass.moreComponents MainPageComponents.moreComponents
        PageChartView.metaClass.moreConditions MainPageComponents.moreComponents

        SimplePageListView.metaClass.actions { Closure closure ->
            SimplePageListActions.initActions(delegate, closure)
        }

        SimplePageListView.metaClass.display { Object[] args ->

            String field
            String time
            Object action = null
            Closure closure
            Map map

            for (def arg in args) {
                if (arg instanceof Closure) {
                    closure = arg
                } else if (arg instanceof Map) {
                    map = arg
                } else if (arg instanceof String) {
                    if (field == null)
                        field = arg
                    else if (action == null)
                        action = arg
                } else if (arg instanceof List) {
                    if (field == null && arg.size() > 0) {
                        field = arg[0]
                        if (arg.size() > 1)
                            time = arg[1]
                    }
                }
            }

            if (field) {
                if (closure)
                    action = closure()

                if (time)
                    delegate.setDisplay(field, time, action)
                else
                    delegate.setDisplay(field, action)
            } else if (closure) {
                Component component = delegate.makeComponent(closure)
                if (component) {
                    if (map)
                        CrudViewHelper.initComponent(component, map, null, delegate)
                    delegate.setDisplay(component)
                }
            }
        }

        SimplePageListView.metaClass.display { Object arg ->
            delegate.display([arg] as Object[])
        }

        PageView.metaClass.getAddDefaultButtons { ->
            delegate.addDefaultButtons()
        }

        MainPageView.metaClass.getDefaultInit { ->
            delegate.defaultInit()
        }

        MainPageView.metaClass.getMakeEditable { ->
            delegate.makeEditable()
        }

        MainPageView.metaClass.getEnableShow { ->
            delegate.enableShow()
        }

        MainPageView.metaClass.getEnableDD { ->
            delegate.enableDD()
        }

        PageTreeView.metaClass.getEnableCP { ->
            delegate.enableCP()
        }

        SimpleDialogView.metaClass.components { Closure closure ->
            SimpleDialogComponents.initComponents(delegate, closure)
        }

        SimpleDialogView.metaClass.hidden { String[] hiddens ->
            if (hiddens) {
                for (String hidden : hiddens)
                    delegate.addHidden(hidden)
            }
        }

        SimplePageListView.metaClass.getAddDefaultActions { ->
            delegate.addDefaultActions()
        }

        Crud.metaClass.pageTable { Closure closure ->
            def view = new PageTableView()
            view.with closure
            view
        }

        Crud.metaClass.pageTable { boolean checkable, Closure closure ->
            def view = new PageTableView(checkable)
            view.with closure
            view
        }

        Crud.metaClass.complexTable { Crud left, String field, boolean checkable, Closure closure ->
            def view = new ComplexTableView(left, field, checkable)
            view.with closure
            view
        }

        Crud.metaClass.complexTable { Crud left, String field, Closure closure ->
            def view = new ComplexTableView(left, field)
            view.with closure
            view
        }

        Crud.metaClass.complexTable { Class leftType, String field, boolean checkable, Closure closure ->
            def view = new ComplexTableView(leftType, field, checkable)
            view.with closure
            view
        }

        Crud.metaClass.complexTable { Class leftType, String field, Closure closure ->
            def view = new ComplexTableView(leftType, field)
            view.with closure
            view
        }

        Crud.metaClass.pageTree { Closure closure ->
            def view = new PageTreeView()
            view.with closure
            view
        }

        Crud.metaClass.pageTreeTable { Closure closure ->
            def view = new PageTreeTableView()
            view.with closure
            view
        }

        Crud.metaClass.simpleDialog { Closure closure ->
            def view = new SimpleDialogView()
            view.with closure
            view
        }

        Crud.metaClass.subList { String field, Closure closure ->
            def view = new SubListView(field)
            view.with closure
            view
        }

        Crud.metaClass.simpleList { Closure closure ->
            def view = new SimplePageListView()
            view.with closure
            view
        }

        Crud.metaClass.pageChart { Closure closure ->
            def view = new PageChartView()
            view.with closure
            view
        }

        CrudViewHelper.register("icon", { String name ->
            Buttons.getIcon(name)
        })

        CrudViewHelper.registerComponent1_0("edit", { String forward = null ->

            def helper = ViewHelper.getHelper()
            if (helper == null || helper instanceof TableColumns)
                Buttons.editImg(forward)
            else if (helper instanceof SimplePageListActions)
                Action.edit(forward)
            else
                Buttons.edit(forward)
        })

        CrudViewHelper.registerComponent1_0("duplicate", { String forward = null ->
            def helper = ViewHelper.getHelper()
            if (helper == null || helper instanceof TableColumns)
                Buttons.duplicateImg(forward)
            else
                Buttons.duplicate(forward)
        })

        ViewHelper.metaClass."~edit" { String title, owner, List args ->

            def helper = ViewHelper.getHelper()
            String forward = args.find({ it instanceof String })
            if (helper == null || helper instanceof TableColumns)
                owner.getEditColumn(forward, title)
            else if (helper instanceof SimplePageListActions)
                Action.edit(forward, title)
            else
                Buttons.edit(forward, title)
        }

        ViewHelper.metaClass."~show" { String title, owner, List args ->
            String forward = args.find({ it instanceof String })
            owner.getEditColumn(forward, title == null ? "curd.show" : title)
        }

        ViewHelper.metaClass."~duplicate" { String title, owner, List args ->
            def helper = ViewHelper.getHelper()
            String forward = args.find({ it instanceof String })
            if (helper == null || helper instanceof TableColumns)
                owner.getDuplicateColumn(forward, title)
            else
                Buttons.duplicate(forward, title)
        }

        registerAction("query")
        registerAction("stat")
        registerAction1("moreQuery")
        registerAction1("moreQuery2")
        registerAction1("add")
        registerAction("delete")
        registerAction("del", "delete")
        registerAction("save")
        registerAction("close")
        registerAction("ok")
        registerAction("cancel")
        registerAction("imp")
        registerAction("down")
        registerAction("up")
        registerAction("copy")
        registerAction("paste")
        registerAction1("sort")
        registerAction("chart", "showChart")
        registerAction("barChart", "showBarChart")
        registerAction("pieChart", "showPieChart")
        registerAction("lineChart", "showLineChart")
        registerAction("curveChart", "showCurveChart")
        registerAction1("export")
        registerAction1("print")
        registerAction1("exportEntitys")
        registerAction("more")
    }
}

