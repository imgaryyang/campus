package com.gzzm.platform.receiver;

import com.gzzm.platform.organ.User;
import net.cyan.arachne.annotation.NotSerialized;
import net.cyan.commons.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 接收者信息
 *
 * @author camel
 * @date 2010-4-28
 */
public class Receiver implements Serializable
{
    private static final long serialVersionUID = 4083991556585895073L;

    /**
     * 接收者的姓名
     */
    private String name;

    /**
     * 接收者的值
     */
    private String value;

    /**
     * 接收者说明
     */
    private String remark;

    /**
     * 是否显示值
     */
    private boolean editable;

    /**
     * 当前用户是否有效
     */
    private boolean valid = true;

    private String id;

    public Receiver()
    {
    }

    public Receiver(String text)
    {
        parse(text);
    }

    private void parse(String text)
    {
        value = text;

        int index = text.indexOf("<");
        if (index >= 0 && text.endsWith(">"))
        {
            value = text.substring(index + 1, text.length() - 1);
            text = text.substring(0, index);
        }

        if (value.endsWith("@local"))
            id = value.substring(0, value.length() - 6);

        if (text.startsWith("\"") || text.startsWith("\'"))
            text = text.substring(1);
        if (text.endsWith("\"") || text.endsWith("\'"))
            text = text.substring(0, text.length() - 1);

        this.name = text;
    }

    public static List<Receiver> parseReceiverList(String text)
    {
        if (StringUtils.isEmpty(text))
            return null;

        String[] ss = text.split("[;,]");
        List<Receiver> receiverList = new ArrayList<Receiver>(ss.length);

        for (String s : ss)
        {
            Receiver receiver = new Receiver();
            receiver.parse(s);
            receiverList.add(receiver);
        }

        return receiverList;
    }

    private static Integer parseUserId(String text)
    {
        int index = text.indexOf("<");
        String value = text;
        if (index >= 0 && text.endsWith(">"))
            value = text.substring(index + 1, text.length() - 1);

        if (value.endsWith("@local"))
            return Integer.valueOf(value.substring(0, value.length() - 6));

        return null;
    }

    public static List<Integer> toUserIds(String text)
    {
        if (StringUtils.isEmpty(text))
            return Collections.emptyList();

        String[] ss = text.split("[;|,]");
        List<Integer> userIds = new ArrayList<Integer>(ss.length);

        for (String s : ss)
        {
            Integer userId = parseUserId(s);
            if (userId != null)
                userIds.add(userId);
        }

        return userIds;
    }

    public static String usersToReceivers(Collection<User> users)
    {
        StringBuilder buffer = new StringBuilder();
        for (User user : users)
        {
            if (buffer.length() > 0)
                buffer.append(";");
            buffer.append("\"").append(user.getUserName()).append("\"<").append(user.getUserId()).append("@local>");
        }

        return buffer.toString();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public boolean isValid()
    {
        return valid;
    }

    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    @Override
    public String toString()
    {
        return name;
    }

    @NotSerialized
    public String getReceiver()
    {
        StringBuilder buffer = new StringBuilder();
        if (!StringUtils.isEmpty(name))
        {
            buffer.append("\"").append(name).append("\"");
        }

        buffer.append("<").append(value).append(">");

        return buffer.toString();
    }
}
