package com.gzzm.ods.redhead;

import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.DeptInfo;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * 选择红头模版和红头标题的页面
 *
 * @author camel
 * @date 11-9-25
 */
@Service
public class RedHeadPage
{
    @Inject
    private RedHeadService service;

    /**
     * 当前用户信息
     */
    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 当前部门
     */
    private Integer deptId;

    /**
     * 红头标题部门
     */
    private Integer[] titleDeptIds;

    /**
     * 选择的红头类型ID
     */
    private Integer typeId;

    /**
     * 选择的红头模版ID
     */
    private Integer redHeadId;

    /**
     * 选择的红头标题ID
     */
    private Integer[] titleIds;

    /**
     * 缓存类型列表，避免重复查询
     */
    private List<RedHeadType> types;

    private List<RedHeadTitle> selectableRedHeadTitles;

    public RedHeadPage()
    {
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer[] getTitleDeptIds()
    {
        return titleDeptIds;
    }

    public void setTitleDeptIds(Integer[] titleDeptIds)
    {
        this.titleDeptIds = titleDeptIds;
    }

    public Integer getTypeId() throws Exception
    {
        if (typeId == null)
        {
            if (redHeadId == null)
            {
                List<RedHeadType> types = getTypes();
                if (types.size() > 1)
                    typeId = types.get(0).getTypeId();
            }
            else
            {
                RedHead redHead = service.getRedHead(redHeadId);
                if (redHead != null)
                    typeId = redHead.getTypeId();
            }
        }
        return typeId;
    }

    public void setTypeId(Integer typeId)
    {
        this.typeId = typeId;
    }

    public Integer getRedHeadId()
    {
        return redHeadId;
    }

    public void setRedHeadId(Integer redHeadId)
    {
        this.redHeadId = redHeadId;
    }

    public Integer[] getTitleIds()
    {
        return titleIds;
    }

    public void setTitleIds(Integer[] titleIds)
    {
        this.titleIds = titleIds;
    }

    @NotSerialized
    @Select(field = "typeId")
    public List<RedHeadType> getTypes() throws Exception
    {
        if (types == null)
            types = service.getDao().getRedHeadTypes();
        return types;
    }

    @NotSerialized
    @Select(field = "redHeadId")
    public List<RedHead> getRedHeads() throws Exception
    {
        return service.getRedHeads(deptId, typeId, userOnlineInfo);
    }

    @NotSerialized
    @Select(field = "titleIds")
    public List<RedHeadTitle> getSelectableRedHeadTitles() throws Exception
    {
        if (selectableRedHeadTitles == null)
        {
            List<Integer> deptIds = new ArrayList<Integer>();
            deptIds.add(deptId);

            DeptInfo dept = service.getDeptService().getDept(deptId);
            if (dept.getLevel() < 1)
                deptIds.add(dept.getParentDept(1).getDeptId());

            if (titleDeptIds != null)
            {
                for (Integer titleDeptId : titleDeptIds)
                {
                    deptIds.add(titleDeptId);

                    dept = service.getDeptService().getDept(titleDeptId);
                    if (dept.getLevel() < 1)
                        deptIds.add(dept.getParentDept(1).getDeptId());
                }
            }

            selectableRedHeadTitles = service.getDao().getRedHeadTitles(deptIds);
        }

        return selectableRedHeadTitles;
    }

    @NotSerialized
    public List<RedHeadTitle> getSelectedRedHeadTitles() throws Exception
    {
        if (titleIds == null)
            return null;

        List<RedHeadTitle> redHeadTitles = new ArrayList<RedHeadTitle>(titleIds.length);
        for (Integer titleId : titleIds)
        {
            redHeadTitles.add(service.getRedHeadTitle(titleId));
        }

        return redHeadTitles;
    }

    /**
     * 下载红头模板
     *
     * @return 转化后的红头模板
     * @throws Exception 模板错误或io错误，或者数据库加载数据错误
     */
    @Service(url = "/ods/redhead/{$0}/get")
    public InputFile getRedHead(Integer redHeadId) throws Exception
    {
        if (redHeadId != null)
            return service.getRedHeadFile(redHeadId);

        return null;
    }

    @Service(url = "/ods/redhead/{$0}/qr")
    public Position qrPosition(Integer redHeadId) throws Exception
    {
        if (redHeadId != null)
        {
            RedHead redHead = service.getRedHead(redHeadId);

            int left = 0;
            if (redHead.getQrLeft() != null)
                left = redHead.getQrLeft();

            int top = 0;
            if (redHead.getQrTop() != null)
                top = redHead.getQrTop();

            return new Position(left, top);
        }

        return null;
    }

    @Service(url = "/ods/redhead/select")
    public String show()
    {
        return "select";
    }
}
