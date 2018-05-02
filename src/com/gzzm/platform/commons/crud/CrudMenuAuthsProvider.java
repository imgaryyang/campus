package com.gzzm.platform.commons.crud;

import com.gzzm.platform.commons.PageClassMenuAuthsProvider;
import net.cyan.arachne.PageClass;
import net.cyan.crud.*;

import java.util.*;

/**
 * 提供crud功能的默认权限
 *
 * @author camel
 * @date 2009-12-22
 */
public class CrudMenuAuthsProvider extends PageClassMenuAuthsProvider
{
    public CrudMenuAuthsProvider()
    {
    }

    protected List<AuthInfo> getAuthInfos(PageClass pageClass) throws Exception
    {
        Class c = pageClass.getType();
        if (Crud.class.isAssignableFrom(c))
        {
            List<AuthInfo> authInfos = new ArrayList<AuthInfo>();

            //默认权限可以调用的方法
            AuthInfo defaultAuth = new AuthInfo("default", "默认权限");


            authInfos.add(defaultAuth);

            if (ListCrud.class.isAssignableFrom(c))
            {
                defaultAuth.addMethod("showList", "查看列表");

                if (NormalCrud.class.isAssignableFrom(c))
                {
                    AuthInfo add = new AuthInfo("add", "添加").addMethod("add", "新增页面").addMethod("save", "保存数据")
                            .addMethod("saveEntity", "保存数据").addMethod("duplicate", "复制页面");
                    authInfos.add(add);

                    AuthInfo modify =
                            new AuthInfo("modify", "修改").addMethod("save", "保存数据").addMethod("saveEntity", "保存数据")
                                    .addMethod("showSortList", "排序页面", false).addMethod("sort", "排序");

                    authInfos.add(modify);

                    if (OwnedCrud.class.isAssignableFrom(c))
                    {
                        add.addMethod("copyTo", "复制记录").addMethod("copyAllTo", "复制记录");
                        add.addMethod("moveTo", "移动记录").addMethod("moveAllTo", "移动记录");
                    }

                    authInfos.add(new AuthInfo("imp", "导入").addMethod("showImp", "导入页面").addMethod("imp", "导入"));
                }

                if (DeletableCrud.class.isAssignableFrom(c))
                {
                    authInfos.add(new AuthInfo("delete", "删除").addMethod("remove", "删除").addMethod("removeAll", "删除"));
                }

                AuthInfo exportAuth = new AuthInfo("export", "导出").addMethod("exp", "导出列表");

                if (EntityDisplayCrud.class.isAssignableFrom(c))
                    exportAuth.addMethod("exportEntity", "导出记录");

                authInfos.add(exportAuth);
            }
            else if (TreeCrud.class.isAssignableFrom(c))
            {
                defaultAuth.addMethod("showTree", "显示树页面");
                defaultAuth.addMethod("loadChildren", "加载树节点");

                if (EntityTreeCrud.class.isAssignableFrom(c))
                {
                    authInfos.add(new AuthInfo("add", "添加").addMethod("add", "新增页面").addMethod("save", "保存数据")
                            .addMethod("saveEntity", "保存数据").addMethod("duplicate", "复制页面")
                            .addMethod("copy", "复制节点").addMethod("cloneForCopy", "复制节点"));
                    authInfos.add(new AuthInfo("modify", "修改").addMethod("save", "保存数据")
                            .addMethod("saveEntity", "保存数据").addMethod("move", "移动节点").addMethod("sort", "排序"));
                    authInfos.add(new AuthInfo("delete", "删除").addMethod("remove", "删除").addMethod("removeAll", "删除"));
                    authInfos.add(new AuthInfo("imp", "导入").addMethod("showImp", "导入页面").addMethod("imp", "导入"));
                }


                authInfos.add(new AuthInfo("export", "导出").addMethod("exp", "导出"));
            }

            if (EntityDisplayCrud.class.isAssignableFrom(c))
            {
                defaultAuth.addMethod("show", "查看记录", false);
            }

            return authInfos;
        }

        return null;
    }
}
