package com.gzzm.portal.cms.in;

import com.gzzm.in.*;
import com.gzzm.platform.appauth.*;
import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.organ.*;
import com.gzzm.portal.cms.channel.ChannelAuthCrud;
import com.gzzm.portal.cms.information.*;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.transaction.Transactional;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author Xrd
 * @date 2017/7/4 16:41
 */
@Service
public class InformationInterfacePage implements InterfacePage {

    @InterfaceDeptIds
    private Collection<Integer> deptIds;

    @InterfaceDeptId
    private Integer deptId;

    @Inject
    private AppAuthDao dao;

    @Inject
    private DeptService deptService;

    @Inject
    private InformationInterDao informationInterDao;

    public InformationInterfacePage() {
    }

    @Service(url = "/interface/portal/new/info", method = HttpMethod.post)
    @Transactional
    @Json
    public InfoResult receiveInformation(@RequestBody ReceiveInformation receiveInfo) throws Exception {

        InfoResult infoResult = new InfoResult();
        if (StringUtils.isEmpty(receiveInfo.getOrgCode())) {
            infoResult.setError("没有组织机构编码");
            return infoResult;
        }
        Integer infoDeptId = informationInterDao.getDeptIdByDeptCode(receiveInfo.getOrgCode());
        if (infoDeptId == null) {
            infoResult.setError("没有组织机构编码");
            return infoResult;
        }
        //判断channelId
        if (!checkChannelAuth(infoDeptId, receiveInfo.getChannelId())) {
            infoResult.setError("没有栏目权限");
            return infoResult;
        }

        //判断日志有无informationId和deptId组成的记录
        if (receiveInfo.getInformationId() != null
                && informationInterDao.getInfoInByInformationIdAndDeptId(receiveInfo.getInformationId(), this.deptId) == null) {
            infoResult.setError("无操作权限");
            return infoResult;
        }

        //数据录入
        InformationEdit informationEdit = new InformationEdit();

        informationEdit.setPhoto(receiveInfo.getPhoto());
        informationEdit.setExtName(receiveInfo.getExtName());
        informationEdit.setTitle(receiveInfo.getTitle());
        informationEdit.setChannelId(receiveInfo.getChannelId());
        informationEdit.setType(receiveInfo.getType());
        if (receiveInfo.getType() == InformationType.url) {
            informationEdit.setLinkUrl(receiveInfo.getLinkUrl());
        }
        informationEdit.setKeywords(receiveInfo.getKeywords());
        informationEdit.setSource(receiveInfo.getSource());
        informationEdit.setFileCode(receiveInfo.getFileCode());
        informationEdit.setSubject(receiveInfo.getSubject());
        informationEdit.setSummary(receiveInfo.getSummary());
        informationEdit.setOrgCode(receiveInfo.getOrgCode());
        informationEdit.setOrgName(receiveInfo.getOrgName());
        informationEdit.setPublished(true);
        informationEdit.setValid(true);
        informationEdit.setCatalogId(receiveInfo.getCatalogId());
        informationEdit.setPublishTime(receiveInfo.getPublishTime());
        informationEdit.setState(InformationState.published);
        if (receiveInfo.getInformationId() == null) {
            informationEdit.setCreateTime(new Date());
            informationEdit.setUpdateTime(new Date());
        } else {
            informationEdit.setUpdateTime(new Date());
        }
        informationEdit.setDeptId(deptId);
        informationEdit.setValidTime(receiveInfo.getValidTime());
        informationEdit.setLang(receiveInfo.getLang());

        if (receiveInfo.getInformationId() != null) {
            //修改的时候有id
            informationEdit.setInformationId(receiveInfo.getInformationId());
        } else {
            informationEdit.setOrderId(informationInterDao.getId("portal_information_order", 6, Integer.class));
        }

        //保存
        informationInterDao.save(informationEdit);

        Information information = new Information();
        if (!informationEdit.getContents().isEmpty()) {
            informationEdit.setContents(null);
        }
        BeanUtils.copyProperties(informationEdit, information);
        informationInterDao.save(information);

        //type=信息采编时，才set content
        if (receiveInfo.getType() == InformationType.information) {
            InformationContentEdit informationContentEdit = new InformationContentEdit();
            informationContentEdit.setContent(receiveInfo.getContent().toCharArray());
            informationContentEdit.setPageNo(0);

            //新增
            informationContentEdit.setInformationId(informationEdit.getInformationId());

            informationInterDao.save(informationContentEdit);
            InformationContent informationContent = new InformationContent();
            BeanUtils.copyProperties(informationContentEdit, informationContent);
            informationInterDao.save(informationContent);
        }

        //记录日志
        InformationInterface info_in = new InformationInterface();
        info_in.setInformationId(informationEdit.getInformationId());
        if (receiveInfo.getInformationId() != null) {
            //更新
            info_in.setUpdateTime(new Date());
        } else {
            //新增
            info_in.setAddTime(new Date());
            info_in.setUpdateTime(new Date());
            info_in.setDeptId(this.deptId);
        }
        informationInterDao.save(info_in);

        infoResult.setInfoId(informationEdit.getInformationId());
        infoResult.setSuccess(true);
        return infoResult;
    }

    public boolean checkChannelAuth(Integer infoDeptId, Integer channelId) throws Exception {
        AppAuth auth = dao.getAuth(ChannelAuthCrud.PORTAL_CHANNEL_PUBLISH, channelId);
        if(auth == null){
            Tools.log("auth is null！");
            return false;
        }
        for (Integer deptId1 : deptIds) {
            AppAuthItem deptAuthItem = dao.getAuthItem(auth.getAuthId(), AuthType.dept, deptId1);
            if (deptAuthItem != null) {
                //包含空的范围id，表示没有范围限制，对所有部门拥有权限
                if (deptAuthItem.getScopeId() == null || deptAuthItem.getScopeId() == -1)
                    return true;

                Scopes scopes = new Scopes(deptAuthItem.getScopeId(), deptId1, deptService);

                Collection<Integer> deptIds1 = scopes.getDeptIds();
                if (deptId1 == null)
                    return true;

                if (deptIds1.contains(infoDeptId)) {
                    return true;
                }
            }
        }
        return false;
    }

}