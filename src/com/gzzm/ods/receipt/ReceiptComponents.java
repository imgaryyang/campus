package com.gzzm.ods.receipt;

import com.gzzm.ods.receipt.attachment.AttachmentReceiptComponent;
import com.gzzm.ods.receipt.meeting.MeetingReceiptComponent;
import com.gzzm.ods.receipt.normal.NormalReceiptComponent;
import com.gzzm.ods.receipt.vote.VoteReceiptComponent;
import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;

import java.util.*;

/**
 * @author camel
 * @date 12-4-8
 */
public class ReceiptComponents
{
    private static List<ReceiptComponent> components = new ArrayList<ReceiptComponent>();

    static
    {
        addComponent(new AttachmentReceiptComponent());
        addComponent(new NormalReceiptComponent());
        addComponent(new MeetingReceiptComponent());

        try
        {
            addComponent(new VoteReceiptComponent());
        }
        catch (Throwable ex)
        {
            //添加回执失败
        }
    }

    private ReceiptComponents()
    {
    }

    public static synchronized void addComponent(ReceiptComponent component)
    {
        components.add(component);
    }

    public static synchronized ReceiptComponent getComponent(String type)
    {
        for (ReceiptComponent component : components)
        {
            if (component.getType().equals(type))
                return component;
        }

        return null;
    }

    public static synchronized List<KeyValue<String>> getComponents()
    {
        List<KeyValue<String>> result = new ArrayList<KeyValue<String>>(components.size());
        for (ReceiptComponent component : components)
        {
            String type = component.getType();
            String name = getName(type);

            if (!StringUtils.isEmpty(name))
                result.add(new KeyValue<String>(type, name));
        }

        return result;
    }

    public static String getName(String type)
    {
        String s = ReceiptComponent.class.getName() + "." + type;
        String name = Tools.getMessage(s);
        if (name != null && name.equals(s))
            name = null;
        return name;
    }
}
