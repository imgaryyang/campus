package com.gzzm.platform.commons;

import net.cyan.thunwind.*;

/**
 * id生成器
 *
 * @author camel
 * @date 2009-10-7
 */
public class SystemIdGenerator extends BaseIdGenerator
{
    public SystemIdGenerator()
    {
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getId(String catalog, String schema, String name, int length, Class<T> c, Session session)
            throws PersistenceException
    {
        boolean prefix = length > 5 && length <= 18 && (c == String.class || c == int.class || c == Integer.class ||
                c == long.class || c == Long.class) && !UUID.equals(name) && !TIMESTAMP.equals(name) &&
                !name.endsWith("_order");

        if (prefix)
            length -= 2;

        T id = super.getId(catalog, schema, name, length, c, session);

        if (prefix)
        {
            long systemCode;

            try
            {
                systemCode = Tools.getSystemCode();
            }
            catch (PersistenceException ex)
            {
                throw ex;
            }
            catch (Exception ex)
            {
                throw new PersistenceException(ex);
            }

            if (c == String.class)
            {
                String s = Long.toString(systemCode);
                if (s.length() < 2)
                    s = "0" + s;

                id = (T) (s + id);
            }
            else
            {
                for (int i = 0; i < length; i++)
                    systemCode *= 10;

                if (c == int.class || c == Integer.class)
                    return (T) new Integer((int) systemCode + (Integer) id);
                else
                    return (T) new Long(systemCode + (Long) id);
            }
        }

        return id;
    }
}
