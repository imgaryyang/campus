package com.gzzm.platform.message.sms;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.receiver.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author camel
 * @date 12-5-1
 */
public class PhoneListReceiverProvider implements ReceiverProvider
{
    @Inject
    private static Provider<SmsDao> daoProvider;

    @Inject
    private static Provider<UserOnlineInfo> userOnlineInfoProvider;

    public PhoneListReceiverProvider()
    {
    }

    public List<ReceiverGroup> getGroups(ReceiversLoadContext context, String parentGroupId) throws Exception
    {
        return null;
    }

    private List<Receiver> toReceivers(List<PhoneList> phoneLists, String s, int max)
    {
        int n = phoneLists.size();
        if (max >= 0 & n > max)
            n = max;
        List<Receiver> receivers = new ArrayList<Receiver>(n);

        for (PhoneList phoneList : phoneLists)
        {
            if (max >= 0 && receivers.size() >= max)
                break;

            boolean b;
            if (s == null)
            {
                b = true;
            }
            else
            {
                String listName = phoneList.getListName();
                b = listName.contains(s) || Chinese.getFirstLetters(listName).startsWith(s) ||
                        Chinese.getLetters(s).contains(s);
            }

            if (b)
            {
                Receiver receiver = new Receiver();
                receiver.setId(phoneList.getListId().toString());
                receiver.setName(phoneList.getListName());
                receiver.setEditable(false);
                receiver.setValid(true);
                receiver.setValue("#" + receiver.getId());

                receivers.add(receiver);
            }
        }

        return receivers;
    }

    public List<Receiver> getReceivers(ReceiversLoadContext context, String groupId) throws Exception
    {
        return queryReceivers(context, null, 50);
    }

    public List<Receiver> queryReceivers(ReceiversLoadContext context, String s) throws Exception
    {
        return queryReceivers(context, s, context.getMaxCount());
    }

    public List<Receiver> queryReceivers(ReceiversLoadContext context, String s, int maxCount) throws Exception
    {
        Collection<Integer> authDeptIds = context.getAuthDeptIds();
        if (authDeptIds == null)
        {
            UserOnlineInfo userOnlineInfo = userOnlineInfoProvider.get();
            authDeptIds = Arrays.asList(context.getDeptId(), userOnlineInfo.getUserId(), userOnlineInfo.getBureauId());
        }

        return toReceivers(daoProvider.get().getPhoneLists(authDeptIds), s, maxCount);
    }

    public List<String> queryGroup(ReceiversLoadContext context, String s) throws Exception
    {
        return null;
    }

    public String getParentGroupId(ReceiversLoadContext context, String groupId) throws Exception
    {
        return null;
    }

    public String getName()
    {
        return "手机列表";
    }

    public String getId()
    {
        return "phonelist";
    }

    public boolean isGroup()
    {
        return false;
    }

    public boolean accept(ReceiversLoadContext context) throws Exception
    {
        return ReceiversLoadContext.PHONE.equals(context.getType());
    }
}
