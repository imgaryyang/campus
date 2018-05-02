package com.gzzm.oa.version;

import net.cyan.thunwind.annotation.OQL;
import net.cyan.thunwind.dao.GeneralDao;

import java.util.List;

/**
 * 版本更新发布信息持久层
 *
 * @author zy
 * @date 2017/1/10 10:14
 */
public abstract class VersionDao extends GeneralDao
{
    public VersionDao()
    {
    }

    /**
     * 根据项目编号和版本号查询是否存在该版本信息
     *
     * @param projectCode 项目编号
     * @param verNo       版本号
     * @return 版本信息
     * @throws Exception
     */
    @OQL("select v from com.gzzm.oa.version.VersionInfo v where v.projectCode=:1 and v.verNo=:2 and v.deleteTag=0 order by v.createTime desc")
    public abstract VersionInfo getVersionInfo(String projectCode, String verNo) throws Exception;

    /**
     * 验证版本信息是否重复
     *
     * @param project   项目编号
     * @param verNo     版本号
     * @param versionId 版本主键
     * @return 版本信息
     * @throws Exception
     */
    @OQL("select v from com.gzzm.oa.version.VersionInfo v where v.project=:1 and v.verNo=:2 and v.deleteTag=0 and v.versionId<>?3")
    public abstract VersionInfo checkVersionInfo(ProjectDir project, String verNo, Integer versionId) throws Exception;

    /**
     * 根据项目编号查询该项目的版本信息
     *
     * @param project 项目编号
     * @return 版本信息
     * @throws Exception
     */
    @OQL("select v from com.gzzm.oa.version.VersionInfo v where v.project=:1 and v.deleteTag=0 order by v.createTime desc")
    public abstract List<VersionInfo> getVersionInfoList(ProjectDir project) throws Exception;

    /**
     * 根据当前版本需要推送的用户
     *
     * @throws Exception
     */
    @OQL("select u.userId from com.gzzm.platform.organ.User u where u.state=0 and u.type=0")
    public abstract List<Integer> getPushUser() throws Exception;

    /**
     * 根据用户ID和项目编号查找是否需要推送
     *
     * @throws Exception
     */
    @OQL("select pu from com.gzzm.oa.version.VersionPushUser pu where pu.userId=?1 and pu.project=?2")
    public abstract VersionPushUser getPushUserByUserIdAndCode(Integer userId,ProjectDir projectCode) throws Exception;
}
