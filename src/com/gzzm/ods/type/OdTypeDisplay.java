package com.gzzm.ods.type;

import com.gzzm.ods.business.*;
import com.gzzm.ods.receivetype.*;
import com.gzzm.ods.sendnumber.*;
import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.commons.crud.*;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.annotation.Service;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 公文分类树，第一级为公文分类，如发文，收文，等等
 * 第二级为发文字号类型或收文分类
 *
 * @author camel
 * @date 13-11-11
 */
@Service
public class OdTypeDisplay extends BaseTreeShow<OdType, String>
{
    @AuthDeptIds
    protected Collection<Integer> authDeptIds;

    @Inject
    protected UserOnlineInfo userOnlineInfo;

    @Inject
    protected BusinessDao businessDao;

    @Inject
    protected SendNumberDao sendNumberDao;

    @Inject
    protected ReceiveTypeService receiveTypeService;

    /**
     * 业务类型，只过滤某种业务的数据
     */
    private String[] businessType;

    public OdTypeDisplay()
    {
    }

    public String[] getBusinessType()
    {
        return businessType;
    }

    public void setBusinessType(String[] businessType)
    {
        this.businessType = businessType;
    }

    @Override
    public OdType getRoot() throws Exception
    {
        return OdType.ROOT;
    }

    @Override
    public List<OdType> getChildren(String parent) throws Exception
    {
        Collection<Integer> authDeptIds = this.authDeptIds;
        if (authDeptIds == null || authDeptIds.size() == 0)
        {
            authDeptIds = Collections.singleton(userOnlineInfo.getBureauId());
        }

        if ("root".equals(parent))
        {
            List<OdType> odTypes = new ArrayList<OdType>();

            //添加一个时间分类
            odTypes.add(new OdType("time", "按时间分类", false));

            //根节点，列出所有公文类型，在BusinessModel中定义了typeName的为单独一个类型
            List<BusinessModel> business = businessDao.getAllBusiness(authDeptIds, businessType);

            for (BusinessModel businessModel : business)
            {
                String typeName;
                String type = businessModel.getTypeName();

                if (StringUtils.isEmpty(type))
                {
                    type = businessModel.getType().getType();
                    typeName = businessModel.getType().getName();
                }
                else
                {
                    typeName = type;
                }

                boolean exists = false;
                for (OdType type1 : odTypes)
                {
                    if (type1.getId().equals(type))
                    {
                        exists = true;
                        break;
                    }
                }

                if (!exists)
                    odTypes.add(new OdType(type, typeName, !"send".equals(type) && !"receive".equals(type)));
            }

            return odTypes;
        }
        else if ("send".equals(parent))
        {
            //发文节点下列出发文字号分类

            List<SendNumber> sendNumbers = sendNumberDao.getSendNumbers(authDeptIds);
            List<OdType> odTypes = new ArrayList<OdType>(sendNumbers.size());

            for (SendNumber sendNumber : sendNumbers)
            {
                odTypes.add(new OdType("s:" + sendNumber.getSendNumberId(), sendNumber.getSendNumberName(), true));
            }

            return odTypes;
        }
        else if ("receive".equals(parent))
        {
            //收文节点下列出所有一级收文类型

            List<OdType> odTypes = new ArrayList<OdType>();

            for (Integer deptId : authDeptIds)
            {
                List<ReceiveType> receiveTypes = receiveTypeService.getTopReceiveTypes(deptId, 0, userOnlineInfo);

                for (ReceiveType receiveType : receiveTypes)
                {
                    boolean exists = false;
                    String id = "r:" + receiveType.getReceiveTypeId();
                    for (OdType odType : odTypes)
                    {
                        if (odType.getId().equals(id))
                        {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists)
                    {
                        odTypes.add(new OdType(id, receiveType.getReceiveTypeName(),
                                receiveType.getSubReceiveTypes().size() == 0));
                    }
                }
            }

            return odTypes;
        }
        else if (parent.startsWith("r:"))
        {
            //收文类型下列出下级收文类型

            Integer receiveTypeId = Integer.valueOf(parent.substring(2));

            List<ReceiveType> receiveTypes =
                    receiveTypeService.getDao().getReceiveType(receiveTypeId).getSubReceiveTypes();

            List<OdType> odTypes = new ArrayList<OdType>(receiveTypes.size());
            for (ReceiveType receiveType : receiveTypes)
            {
                odTypes.add(new OdType("s:" + receiveType.getReceiveTypeId(), receiveType.getReceiveTypeName()));
            }

            return odTypes;
        }
        else if ("time".equals(parent))
        {
            //时间分类，下面列出年份

            int year = DateUtils.getYear();
            int startYear = Tools.getStartYear();
            List<OdType> odTypes = new ArrayList<OdType>(year - startYear + 1);

            odTypes.add(new OdType("time_all", "全部公文", true));
            odTypes.add(new OdType("lm:1", "最近一个月", true));
            odTypes.add(new OdType("lm:3", "最近三个月", true));
            odTypes.add(new OdType("lm:6", "最近半年", true));
            odTypes.add(new OdType("lm:12", "最近一年", true));

            for (; year >= startYear; year--)
            {
                odTypes.add(new OdType("y:" + year, year + "年", false));
            }

            return odTypes;
        }
        else if (parent.startsWith("y:"))
        {
            //年份，下面是月份
            int year = Integer.parseInt(parent.substring(2));
            int maxMonth = 12;

            int year0 = DateUtils.getYear();
            if (year == year0)
            {
                //如果是当前年份，最大的月份为当前月
                maxMonth = DateUtils.getMonth();
            }

            int startYear = Tools.getStartYear();
            List<OdType> odTypes = new ArrayList<OdType>(year - startYear + 1);
            for (int m = maxMonth; m >= 1; m--)
            {
                odTypes.add(new OdType("m:" + year + "_" + m, year + "年" + m + "月", true));
            }

            return odTypes;
        }

        return Collections.emptyList();
    }

    @Override
    public boolean hasChildren(OdType node) throws Exception
    {
        return !node.isLeaf();
    }

    @Override
    public OdType getNode(String key) throws Exception
    {
        if ("root".equals(key))
            return OdType.ROOT;

        return new OdType(key, null);
    }

    public String getKey(OdType node) throws Exception
    {
        return node.getId();
    }

    @Override
    protected Object createTreeView() throws Exception
    {
        return new SelectableTreeView();
    }
}
