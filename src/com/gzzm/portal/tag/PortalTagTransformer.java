package com.gzzm.portal.tag;

import net.cyan.arachne.*;
import net.cyan.commons.util.*;
import net.cyan.proteus.base.*;

import java.util.*;

/**
 * 为模版引擎增加扩展，通过在标签中插入portal属性，可以读取系统中的数据
 *
 * @author camel
 * @date 2011-6-11
 */
public class PortalTagTransformer extends AutoLogicTagTransformer
{
    private static final Object EMPTY = new Object();

    public PortalTagTransformer()
    {
    }

    protected Object getValue(AutoLogicTagProcessor tag) throws Exception
    {
        return null;
    }

    @Override
    public Object transform(Object value, String expression, final TagProcessor tag) throws Exception
    {
        String type = tag.getString(PortalTag.TYPE);

        if (StringUtils.isEmpty(type))
        {
            for (TagAttribute attribute : tag.getAttributes())
            {
                String name = attribute.getName();
                if (name.startsWith(PortalTag.PORTAL))
                {
                    type = name.substring(PortalTag.PREFIXLEN);
                    break;
                }
            }
        }
        else
        {
            tag.removeAttribute(PortalTag.TYPE);
        }

        if (!StringUtils.isEmpty(type))
        {
            Map<String, Object> map = new CollectionUtils.SimpleMap()
            {
                private Map<String, Object> temp;

                public boolean isEmpty()
                {
                    return false;
                }

                public boolean containsKey(Object key)
                {
                    return true;
                }

                private Object getFromTag(String name) throws Exception
                {
                    if (temp != null && temp.containsKey(name))
                        return temp.get(name);

                    //取过的标签属性从标签中删除，以防被生成到html中，然后放到临时map中，以防被第二次读取
                    Object value = tag.getAttribute(name);

                    if (temp == null)
                        temp = new HashMap<String, Object>();

                    temp.put(name, value);
                    tag.removeAttribute(name);

                    return value;
                }

                private boolean containsAttribute(String name) throws Exception
                {
                    return temp != null && temp.containsKey(name) || tag.containsAttribute(name);
                }

                public Object get(Object key)
                {
                    String name = (String) key;
                    try
                    {
                        //先从标签属性中取
                        if (containsAttribute(name))
                            return getFromTag(name);

                        if ("channelId".equals(name))
                        {
                            if (containsAttribute("channelCode"))
                            {
                                String channelCode = (String) getFromTag("channelCode");
                                return TagUtils.getChannelId(channelCode);
                            }
                        }

                        //标签属性中没有定义,则从模板上下文中取
                        if (!"self".equals(name) && !QueryTag.PAGENO.equals(name))
                        {
                            //pageNo是个特殊参数，不希望被其他标签的结果干扰，因此不从上下文取，只从标签属性和参数取

                            try
                            {
                                Object value = tag.getContext().getValue(name);
                                if (value != null)
                                    return value;
                            }
                            catch (Exception ex)
                            {
                                //从上下文去变量错误，跳过，当作从上下文取不到变量
                            }
                        }

                        //如果标签属性和上下文中没有定义变量，从参数中取
                        Object value = RequestContext.getContext().get(name);
                        if (value == null)
                        {
                            //处理几个特殊的变量
                            if ("stationId".equals(name))
                            {
                                //站点ID
                                value = TagUtils.getStationId();
                            }
                            else if ("channelId".equals(name))
                            {
                                //栏目ID
                                value = TagUtils.getChannelId0(this);
                            }
                            else if ("deptId".equals(name))
                            {
                                //部门ID
                                value = TagUtils.getDeptId();
                            }
                        }

                        return value;
                    }
                    catch (Exception ex)
                    {
                        ExceptionUtils.wrapException(ex);
                    }

                    return null;
                }

                public Object put(String key, Object value)
                {
                    ForwardContext context = RequestContext.getContext().getForwardContext();

                    try
                    {
                        context.setVariable(key, value);
                    }
                    catch (Exception ex)
                    {
                        ExceptionUtils.wrapException(ex);
                    }

                    return null;
                }

                @Override
                public Object remove(Object key)
                {
                    tag.removeAttribute((String) key);

                    return null;
                }
            };

            PortalTag portalTag = PortalTagContainer.getTag(type, map);

            if (portalTag != null)
            {
                Object result = portalTag.getValue(map);
                if (result != null)
                    return result;
            }
        }

        return EMPTY;
    }
}