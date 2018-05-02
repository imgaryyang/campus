package com.gzzm.portal.webdeptgroup;

import com.gzzm.portal.annotation.Tag;
import com.gzzm.portal.tag.OQLTag;

import java.util.Map;

/**
 * purposeId规范：
 * 信息公开目录:xxgkml
 * 部门频道(机构管理):bmpd
 * 咨询投诉部门:zxtsbm
 * 依申请公开:ysqgk
 *
 * @author Xrd
 * @date 2018/3/21 13:37
 */
@Tag(name = "webDeptGroupTag", singleton = true)
public class WebDeptGroupTag extends OQLTag<WebDeptGroup>
{
    public WebDeptGroupTag()
    {
    }

    @Override
    protected int getPageSize(Map<String, Object> context) throws Exception
    {
        //不支持分页
        return -1;
    }

    @Override
    protected String getQueryString(Map<String, Object> parameters) throws Exception
    {
        return "select w from WebDeptGroup w where purposeId=:purposeId and groupName=?groupName order by orderId";
    }

    @Override
    protected Object transform(WebDeptGroup webDeptGroup) throws Exception
    {
        return new WebDeptGroupInfo(webDeptGroup);
    }
}
