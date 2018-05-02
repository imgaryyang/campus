package com.gzzm.platform.commons.crud;

import net.cyan.arachne.*;
import net.cyan.commons.util.BeanUtils;
import net.cyan.crud.*;
import net.cyan.crud.arachne.ArachneCrudInit;

import java.lang.reflect.Method;

/**
 * @author camel
 * @date 2009-11-3
 */
public class SystemCrudInit extends ArachneCrudInit
{
    public SystemCrudInit()
    {
    }

    @Override
    public void run()
    {
        super.run();
        CrudConfig.setConfig("pageSize", PageSizeProvider.getInstance());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initServices(Class<? extends Crud> crudType, PageClass info, ServiceParser parser) throws Exception
    {
        super.initServices(crudType, info, parser);

        if (OwnedCrud.class.isAssignableFrom(crudType))
        {
            String path = info.getUrl();

            Class keyType = CrudUtils.getKeyType((Class<? extends KeyBaseCrud>) crudType);
            Class ownerKeyType = OwnedCrudUtils.getOwnerKeyType((Class<? extends OwnedCrud>) crudType);

            //moveto
            if (info.getMethod("moveTo") == null)
            {
                Method m;
                try
                {
                    m = crudType.getMethod("moveTo", keyType, ownerKeyType, ownerKeyType);
                }
                catch (NoSuchMethodException ex)
                {
                    try
                    {
                        m = crudType.getMethod("moveTo", Object.class, ownerKeyType, ownerKeyType);
                    }
                    catch (NoSuchMethodException ex1)
                    {
                        m = crudType.getMethod("moveTo", Object.class, Object.class, Object.class);
                    }
                }

                PageMethod method = new PageMethod(m, HttpMethod.put, ResultType.object);
                method.addUrl(path + "/{$0}/move/{$1}");

                parser.init(method);

                info.addMethod(method);
            }

            //moveAllTo
            if (info.getMethod("moveAllTo") == null)
            {
                Method m;
                try
                {
                    m = crudType.getMethod("moveAllTo", BeanUtils.getArrayType(keyType), ownerKeyType, ownerKeyType);
                }
                catch (NoSuchMethodException ex)
                {
                    try
                    {
                        m = crudType.getMethod("moveAllTo", Object[].class, ownerKeyType, ownerKeyType);
                    }
                    catch (NoSuchMethodException ex1)
                    {
                        m = crudType.getMethod("moveAllTo", Object[].class, Object.class, Object.class);
                    }
                }

                PageMethod method = new PageMethod(m, HttpMethod.post, ResultType.object);
                method.addUrl(path + "/moveto/{$1}");

                parser.init(method);

                info.addMethod(method);
            }

            //copyto
            if (info.getMethod("copyTo") == null)
            {
                Method m;
                try
                {
                    m = crudType.getMethod("copyTo", keyType, ownerKeyType, ownerKeyType);
                }
                catch (NoSuchMethodException ex)
                {
                    try
                    {
                        m = crudType.getMethod("copyTo", Object.class, ownerKeyType, ownerKeyType);
                    }
                    catch (NoSuchMethodException ex1)
                    {
                        m = crudType.getMethod("copyTo", Object.class, Object.class, Object.class);
                    }
                }

                PageMethod method = new PageMethod(m, HttpMethod.put, ResultType.object);
                method.addUrl(path + "/{$0}/copy/{$1}");

                parser.init(method);

                info.addMethod(method);
            }

            //copyAllTo
            if (info.getMethod("copyAllTo") == null)
            {
                Method m;
                try
                {
                    m = crudType.getMethod("copyAllTo", BeanUtils.getArrayType(keyType), ownerKeyType, ownerKeyType);
                }
                catch (NoSuchMethodException ex)
                {
                    try
                    {
                        m = crudType.getMethod("copyAllTo", Object[].class, ownerKeyType, ownerKeyType);
                    }
                    catch (NoSuchMethodException ex1)
                    {
                        m = crudType.getMethod("copyAllTo", Object[].class, Object.class, Object.class);
                    }
                }

                PageMethod method = new PageMethod(m, HttpMethod.post, ResultType.object);
                method.addUrl(path + "/copyto/{$1}");

                parser.init(method);

                info.addMethod(method);
            }
        }
    }
}
