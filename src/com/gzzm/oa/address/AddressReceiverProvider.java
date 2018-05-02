package com.gzzm.oa.address;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.receiver.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 从通讯录里获取接收者
 *
 * @author camel
 * @date 2010-4-28
 */
public class AddressReceiverProvider implements ReceiverProvider
{
    @Inject
    private static Provider<AddressCardDao> daoProvider;

    public AddressReceiverProvider()
    {
    }

    private static Collection<Integer> getAuthDeptIds(ReceiversLoadContext context)
    {
        return context.getUser().getAuthDeptIdsByUrl("/oa/address/card?type=dept&readOnly=true",
                "/oa/address/card?readOnly=true&type=dept");
    }

    public List<ReceiverGroup> getGroups(ReceiversLoadContext context, String parentGroupId) throws Exception
    {
        if (parentGroupId == null)
        {
            AddressCardDao dao = daoProvider.get();

            List<AddressGroup> userGroups = dao.getGroupsByOwner(AddressType.user, context.getUserId());
            List<AddressGroup> deptGroups = null;

            Collection<Integer> authDeptIds = getAuthDeptIds(context);

            if (authDeptIds != null)
            {
                deptGroups = dao.getGroupsByDeptIds(authDeptIds);
            }

            List<ReceiverGroup> receiverGroups = new ArrayList<ReceiverGroup>(userGroups.size() +
                    (deptGroups == null ? 0 : deptGroups.size()));

            for (AddressGroup addressGroup : userGroups)
            {
                receiverGroups.add(new ReceiverGroup(addressGroup.getGroupId().toString(), addressGroup.getGroupName(),
                        false));
            }

            if (deptGroups != null)
            {
                for (AddressGroup addressGroup : deptGroups)
                {
                    receiverGroups.add(new ReceiverGroup(addressGroup.getGroupId().toString(),
                            addressGroup.getGroupName(), false));
                }
            }

            return receiverGroups;
        }
        else
        {
            return null;
        }
    }

    public List<Receiver> getReceivers(ReceiversLoadContext context, String groupId) throws Exception
    {
        String type = chageType(context.getType());

        if (groupId == null || "0".equals(groupId))
        {
            Collection<Integer> authDeptIds = getAuthDeptIds(context);

            if (authDeptIds == null)
                return toReceivers(daoProvider.get().getCardsByOwner(AddressType.user, context.getUserId()), type);
            else
                return toReceivers(daoProvider.get().getCardsByUserAndDepts(context.getUserId(), authDeptIds), type);
        }
        else
        {
            return toReceivers(daoProvider.get().getCardsInGroup(Integer.valueOf(groupId)), type);
        }
    }

    public List<Receiver> queryReceivers(ReceiversLoadContext context, String s) throws Exception
    {
        String type = chageType(context.getType());

        return toReceivers(daoProvider.get().queryCardsByNameOrField(AddressType.user, context.getUserId(),
                type, s, context.getMaxCount()), type);
    }

    private String chageType(String type)
    {
        if (ReceiversLoadContext.EMAIL.equals(type))
            return CardItem.EMAIL;
        else if (ReceiversLoadContext.PHONE.equals(type))
            return CardItem.MOBILEPHONE;

        return type;
    }

    private static List<Receiver> toReceivers(List<AddressCard> cards, String type)
    {
        List<Receiver> result = new ArrayList<Receiver>(cards.size());

        for (AddressCard card : cards)
        {
            Receiver receiver = toReceiver(card, type);
            if (receiver != null)
                result.add(receiver);
        }

        return result;
    }

    private static Receiver toReceiver(AddressCard card, String type)
    {
        String name = card.getCardName();

        Receiver receiver = new Receiver();
        receiver.setEditable(true);
        receiver.setId(card.getCardId().toString());
        receiver.setName(name);

        String value = card.getAttributes().get(type);

        if (StringUtils.isEmpty(value))
        {
            //用户没有定义此联系方式，设置无效
            receiver.setValid(false);
            receiver.setValue("address_" + card.getCardId());
        }
        else
        {
            receiver.setValue("\"" + name + "\"<" + value + ">");
            receiver.setRemark(value);
        }

        return receiver;
    }

    public List<String> queryGroup(ReceiversLoadContext context, String s) throws Exception
    {
        List<AddressGroup> groups =
                daoProvider.get().getGroupsByOwner(AddressType.user, context.getUserId());
        List<String> result = new ArrayList<String>(groups.size());
        for (AddressGroup group : groups)
        {
            if (Tools.matchText(group.getGroupName(), s))
                result.add(group.getGroupId().toString());
        }

        return result;
    }

    public String getParentGroupId(ReceiversLoadContext context, String groupId) throws Exception
    {
        return null;
    }

    public String getName()
    {
        return "通讯录";
    }

    public String getId()
    {
        return "address";
    }

    public boolean isGroup()
    {
        return true;
    }

    public boolean accept(ReceiversLoadContext context) throws Exception
    {
        return !ReceiversLoadContext.USERID.equals(context.getType()) &&
                !ReceiversLoadContext.STATIONID.equals(context.getType());
    }
}
