package com.gzzm.oa.superquery;

import com.gzzm.im.entitys.ImUserConfig;
import com.gzzm.oa.mail.Mail;
import com.gzzm.ods.flow.*;
import com.gzzm.platform.annotation.AuthDeptIds;
import com.gzzm.platform.commons.*;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.menu.Menu;
import com.gzzm.platform.message.comet.CometService;
import com.gzzm.platform.organ.User;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xzb
 * @date 2017-8-3
 */
@Service
public class SuperQuery
{
    private OdWorkSheetItemList odWorkSheetItemList;

    /**
     * 拥有权限的部门列表，通过setAuthDeptIds注入，以方便子类覆盖注入方式
     */
    @NotSerialized
    private Collection<Integer> authDeptIds;

    @Inject
    private CometService cometService;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    @Inject
    private SuperQueryDao dao;

    /**
     * 所有oa的菜单
     */
    private static List<String> oaMenuId;

    /**
     * 当前页数
     */
    private Integer pageNo = 1;

    /**
     * 查询类型
     * 1：公文
     * 2:邮件
     * 3:功能
     * 4:用户
     */
    private Integer searchType = 1;

    /**
     * 搜索条件
     */
    private String condition;

    /**
     * 每页大小
     */
    private Integer pageSize = 30;

    /**
     * 总页数
     */
    private Integer pageCount;

    private boolean wsCheck;

    private boolean emailCheck;

    private boolean menuCheck;

    private boolean userCheck;

    public SuperQuery() {
    }

    public Integer getPageNo()
    {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        if (pageNo == null || pageNo < 1)
        {
            this.pageNo = 1;
        }
        else
        {
            this.pageNo = pageNo;
        }
    }

    public Integer getSearchType()
    {
        return searchType;
    }

    public void setSearchType(Integer searchType)
    {
        this.searchType = searchType;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public boolean isWsCheck()
    {
        return wsCheck;
    }

    public void setWsCheck(boolean wsCheck)
    {
        this.wsCheck = wsCheck;
    }

    public boolean isEmailCheck()
    {
        return emailCheck;
    }

    public void setEmailCheck(boolean emailCheck)
    {
        this.emailCheck = emailCheck;
    }

    public boolean isMenuCheck()
    {
        return menuCheck;
    }

    public void setMenuCheck(boolean menuCheck)
    {
        this.menuCheck = menuCheck;
    }

    public boolean isUserCheck()
    {
        return userCheck;
    }

    public void setUserCheck(boolean userCheck)
    {
        this.userCheck = userCheck;
    }

    public Collection<Integer> getAuthDeptIds()
    {
        return authDeptIds;
    }

    @AuthDeptIds
    protected void setAuthDeptIds(Collection<Integer> authDeptIds)
    {
        this.authDeptIds = authDeptIds;
    }

    public Integer getPageCount() {
        if (pageCount != null && pageCount == 0)
        {
            pageCount++;
        }
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        if (pageCount < 1)
        {
            this.pageCount = 1;
        }
        else
        {
            this.pageCount = pageCount;
        }
    }

    /**
     * 个人主页跳转到搜索列表
     * @return
     * @throws Exception
     */
   @Service(url = "/oa/superquery/turnToSearch")
    public String turnToSearch() throws Exception {
        return "/oa/superquery/searchpage.ptl";
    }

    /**
     * 领导主页跳转到搜索列表
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/superquery/turntoleadersearch")
     public String turnToLeaderSearch() throws Exception {
         return "/oa/superquery/leadersearchpage.ptl";
     }

    /**
     * 个人搜索列表数据
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/superquery/search",method= HttpMethod.all)
    public Map<String,Object> search() throws Exception {
        return doSearch();
    }

    /**
     * 领导搜索列表数据
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/superquery/leadersearch",method= HttpMethod.all)
    public Map<String,Object> leaderSearch() throws Exception {
        return doLeaderSearch();
    }

    /**
     * 个人搜索列表数据(移动)
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/superquery/search/json",method = HttpMethod.all)
    @Json
    public Map<String,Object> searchJson() throws Exception {
        return doSearch();
    }

    /**
     * 企业搜索列表数据(移动)
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/superquery/leadersearch/json",method= HttpMethod.all)
    @Json
    public Map<String,Object> leaderSearchJson() throws Exception {
        return doLeaderSearch();
    }

    /**
     * 个人主页
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/superquery/homepage")
    public String homePage() throws Exception {
        return "/oa/superquery/homepage.ptl";
    }

    /**
     * 企业主页
     * @return
     * @throws Exception
     */
    @Service(url = "/oa/superquery/leaderhomepage")
    public String leaderHomePage() throws Exception {
        return "/oa/superquery/leaderhomepage.ptl";
    }

    @Service(url = "/oa/superquery/count")
    public Integer resultCount() throws Exception{
        if (StringUtils.isBlank(condition))
        {
            throw new NoErrorException("没有查询条件");
        }
        condition = condition.trim();
        String filter=null;
       if (StringUtils.isNotBlank(condition)){
            filter = "%" + condition + "%";
       }
        switch (searchType)
        {
            case 2:
                return getEmailCount(filter);
            case 3:
                return getMenuCount(filter);
            case 4:
                return getUserCount(filter);
        }
        return null;
    }

    private Map<String,Object> doSearch() throws Exception {
        if (StringUtils.isBlank(condition))
        {
            throw new NoErrorException("没有查询条件");
        }
        Map<String,Object> resultMap =new HashMap<String, Object>();
        condition = condition.trim();
        resultMap.put("condition",condition);
        switch (searchType) {
            case 1:
                doSearchWorkSheetItem(resultMap);
                break;
            case 2:
                doSearchEmail(resultMap);
                break;
            case 3:
                doSearchMenu(resultMap);
                break;
            case 4:
                doSearchUsers(resultMap);
                break;
        }
        resultMap.put("searchType",searchType);
        return resultMap;
    }

    private Map<String,Object> doLeaderSearch() throws Exception {
        if (StringUtils.isBlank(condition))
        {
            throw new NoErrorException("没有查询条件");
        }
            Map<String,Object> resultMap =new HashMap<String, Object>();
            condition = condition.trim();
            resultMap.put("condition",condition);
            switch (searchType) {
                case 1:
                    doSearchOdFlowInstance(resultMap);
                    break;
                case 3:
                    doSearchMenu(resultMap);
                    break;
                case 4:
                    doSearchUsers(resultMap);
                    break;
            }
            resultMap.put("searchType",searchType);
            return resultMap;
        }

    /**
     * 用户查询
     *
     * @throws Exception
     */
    private Map<String,Object> doSearchUsers(Map<String,Object> resultMap) throws Exception {
        Map<Integer, String> headMap;
        List<User> userList;
        userList = dao.getUsers(userOnlineInfo.getAuthDeptIdsByUrl("/UserSearch"), condition);
        headMap = new HashMap<Integer, String>();
        for (User user : userList)
        {
            String url = getheadUrl(user);
            if (StringUtils.isNotBlank(url))
            {
                headMap.put(user.getUserId(), url);
            }
        }
        resultMap.put("headMap",headMap);
        resultMap.put("userList",userListMap(userList));
        return resultMap;
    }

    /**
     * 公文查询
     *
     * @throws Exception
     */
    private Map<String,Object> doSearchWorkSheetItem(Map<String,Object> resultMap) throws Exception {
        odWorkSheetItemList = Tools.getBean(OdWorkSheetItemList.class);
        odWorkSheetItemList.setType(OdWorkSheetItemList.ALL);
        odWorkSheetItemList.setPageNo(pageNo);
        odWorkSheetItemList.setPageSize(pageSize);
        if (StringUtils.isNotBlank(condition)) {
            odWorkSheetItemList.setText(condition);
        }
        resultMap.put("workSheetItemList", WorkSheetItemMap(odWorkSheetItemList.getList()));
        resultMap.put("pageCount", odWorkSheetItemList.getPageCount());
        resultMap.put("pageNo",pageNo);
        resultMap.put("wsCount",odWorkSheetItemList.getTotalCount());
        return resultMap;
    }

    /**
     * email查询
     *
     * @throws Exception
     */
    private Map<String,Object> doSearchEmail(Map<String,Object> resultMap) throws Exception {
        List<Mail> emailList;
        String filter = "%" + condition + "%";
        Integer totalCount = dao.getMailsCount(condition, filter, userOnlineInfo.getUserId());
        if (totalCount != 0)
        {
            pageCount = totalCount / pageSize;
            if (totalCount % pageSize != 0) {
                pageCount++;
            }
            dao.setPageSize(pageSize);
            dao.setPageNo(pageNo);
            emailList = dao.getMails(condition, filter, userOnlineInfo.getUserId());
        }
        else
        {
            pageCount = 1;pageNo = 1;
            emailList = new ArrayList<Mail>();
        }
        resultMap.put("emailList",emailListMap(emailList));
        resultMap.put("pageCount",pageCount);
        resultMap.put("pageNo",pageNo);
        return resultMap;
    }

    /**
     * 菜单查询
     *
     * @throws Exception
     */
    private Map<String,Object> doSearchMenu(Map<String,Object> resultMap) throws Exception {
        List<Map<String, String>> menuList;
        if (CollectionUtils.isEmpty(oaMenuId))
        {
            loadOAMenu();
        }

        String filter = "%" + condition + "%";
        if (userOnlineInfo.isAdmin())
        {
            //管理员用户
            menuList = dao.getMenus(oaMenuId, filter);
        }
        else
        {
            //非管理员用户
            Collection<String> userOAId = new HashSet<String>(userOnlineInfo.getAppIds());
            userOAId.retainAll(oaMenuId);
            menuList = dao.getMenus(userOAId, filter);
        }
        resultMap.put("menuList",menuList);
        return resultMap;
    }

    /**
     * 公文查询
     *
     * @throws Exception
     */
    private Map<String,Object> doSearchOdFlowInstance(Map<String,Object> resultMap) throws Exception {
        OdFlowInstanceQuery odFlowInstance = Tools.getBean(OdFlowInstanceQuery.class);
        odFlowInstance.setAuthDeptIds(getAuthDeptIds());
        odFlowInstance.setPageNo(pageNo);
        odFlowInstance.setPageSize(pageSize);
        if (StringUtils.isNotBlank(condition)) {
            odFlowInstance.setText(condition);
        }
        resultMap.put("odFlowInstance", odFlowInstanceMap(odFlowInstance.getList()));
        resultMap.put("pageCount", odFlowInstance.getPageCount());
        resultMap.put("pageNo",pageNo);
        resultMap.put("odCount",odFlowInstance.getTotalCount());
        return resultMap;
    }

    private void getchildId(List<String> result, Menu menu) {
        result.add(menu.getMenuId());
        List<Menu> childrenList = menu.getChildMenus();
        if (CollectionUtils.isNotEmpty(childrenList))
        {
            for (Menu child : childrenList)
            {
                getchildId(result, child);
            }
        }
    }

    private String getheadUrl(User user) throws Exception {
        ImUserConfig userConfig = dao.load(ImUserConfig.class, user.getUserId());
        if (userConfig != null && userConfig.getHeadImg() != null) {
            return null;
        }
        if (cometService.isOnline(user.getUserId())) {
            return user.getSex() == Sex.female ? "/im/images/online_female.jpg" : "/im/images/online_male.jpg";
        }
        else {
            return user.getSex() == Sex.female ? "/im/images/offline_female.jpg" : "/im/images/offline_male.jpg";
        }
    }

    @Service(url = "/oa/superquery/{$0}/photo")
    public byte[] getPhoto(Integer userId) throws Exception {
        ImUserConfig userConfig = dao.load(ImUserConfig.class, userId);
        User user;
        if (userConfig == null)
        {
            user = dao.load(User.class, userId);
        }
        else
        {
            user = userConfig.getUser();
        }

        if (cometService.isOnline(userId))
        {
            if (userConfig != null && userConfig.getHeadImg() != null)
            {
                return userConfig.getHeadImg();
            }
            else
            {
                return getOnPhoto(user, "on");
            }
        }
        else
        {
            if (userConfig != null && userConfig.getOffHeadImg() != null)
            {
                return userConfig.getOffHeadImg();
            }
            else
            {
                return getOnPhoto(user, "off");
            }
        }
    }

    /**
     * 返回没默认头像
     *
     * @param user
     * @param state
     * @return
     * @throws Exception
     */
    byte[] getOnPhoto(User user, String state) throws Exception {
        Sex sex = user.getSex();
        return IOUtils.fileToBytes(Tools.getAppPath(sex == Sex.female ? "im/images/" + state + "line_female.jpg" : "im/images/" + state + "line_male.jpg"));
    }

    @Service(url = "/oa/superquery/loadoamenu")
    public void loadOAMenu() throws Exception {
        oaMenuId = new ArrayList<String>();
        Menu topMenu = dao.load(Menu.class, "oa");
        getchildId(oaMenuId, topMenu);
    }

    private List<Map<String ,Object>> WorkSheetItemMap(List<OdWorkSheetItem> odWorkSheetItems) throws Exception{
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        List<Map<String ,Object>> resultList=new ArrayList<Map<String ,Object>>();
        for(OdWorkSheetItem odWorkSheetItem:odWorkSheetItems){
            Map<String ,Object> resultMap= new HashMap<String, Object>();

            resultMap.put("titleText",odWorkSheetItem.getTitleText());
            resultMap.put("stepId",odWorkSheetItem.getStepId());
            resultMap.put("type",odWorkSheetItem.getOd().getType());
            if(StringUtils.isEmpty(odWorkSheetItem.getOd().getPriority())){
                resultMap.put("priority",odWorkSheetItem.getOd().getDocument().getPriority());
            }else{
                resultMap.put("priority",odWorkSheetItem.getOd().getPriority());
            }
            if(StringUtils.isEmpty(odWorkSheetItem.getOd().getTag())){
                resultMap.put("tag",odWorkSheetItem.getOd().getSimpleName());
            }else{
                resultMap.put("tag",odWorkSheetItem.getOd().getTag());
            }
            resultMap.put("textId",odWorkSheetItem.getOd().getDocument().getTextId());
            resultMap.put("documentId",odWorkSheetItem.getOd().getDocumentId());
            resultMap.put("attachment",odWorkSheetItem.isAttachment());
            resultMap.put("otherFileName",odWorkSheetItem.getOtherFileName());
            resultMap.put("typeName",odWorkSheetItem.getTypeName());
            resultMap.put("sendNumber",odWorkSheetItem.getOd().getDocument().getSendNumber());
            resultMap.put("source",odWorkSheetItem.getSource());
            resultMap.put("sourceName",removeNull(odWorkSheetItem.getSourceName()));

            resultMap.put("receiveTime",odWorkSheetItem.getReceiveTime()==null?"":simpleDateFormat.format(odWorkSheetItem.getReceiveTime()));
            resultMap.put("nodeName",odWorkSheetItem.getNodeName());
            resultMap.put("state", DataConvert.getEnumString(odWorkSheetItem.getOd().getState()));
            resultMap.put("workday",odWorkSheetItem.getWorkday());
            resultMap.put("flowTag",odWorkSheetItem.getFlowTag());
            resultList.add(resultMap);
        }
        return resultList;
    }

    private List<Map<String ,Object>> emailListMap(List<Mail> emailList) throws Exception{
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat ("yyyy-MM-dd HH:mm");
        List<Map<String ,Object>> resultList=new ArrayList<Map<String ,Object>>();
        for(Mail mail:emailList){
            Map<String ,Object> resultMap= new HashMap<String, Object>();
            resultMap.put("title",mail.getTitle());
            resultMap.put("replyed",mail.isReplyed());
            resultMap.put("color",mail.getMark()==null?null:mail.getMark().getColor());
            resultMap.put("mailId",mail.getMailId());
            resultMap.put("senderName",mail.getSenderName());
            resultMap.put("allDeptName",mail.getSenderUser().allDeptName());
            resultMap.put("acceptTime",simpleDateFormat.format(mail.getAcceptTime()));
            resultMap.put("mailSize",DataConvert.format(mail.getMailSize(),"bytesize"));
            resultList.add(resultMap);
        }
        return resultList;
    }

    private List<Map<String ,Object>> userListMap(List<User> userList) throws Exception{
        List<Map<String ,Object>> resultList=new ArrayList<Map<String ,Object>>();
        for(User user:userList){
            Map<String ,Object> resultMap= new HashMap<String, Object>();
            resultMap.put("userId",user.getUserId());
            resultMap.put("userName",DataConvert.format(user.getUserName(),"trunc(9)"));
            resultMap.put("allDeptName",DataConvert.format(user.allDeptName(),"trunc(9)"));
            resultMap.put("duty",DataConvert.format(user.getDuty(),"trunc(9)"));
            resultMap.put("officePhone",user.getOfficePhone());
            resultMap.put("phone",user.getPhone());
            resultList.add(resultMap);
        }
        return resultList;
    }

    private List<Map<String ,Object>> odFlowInstanceMap(List<OdFlowInstance> odFlowInstanceList) throws Exception{
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        List<Map<String ,Object>> resultList=new ArrayList<Map<String ,Object>>();
        for(OdFlowInstance odFlowInstance:odFlowInstanceList){
            Map<String ,Object> resultMap= new HashMap<String, Object>();

            if(odFlowInstance.getDocument()!=null){
                resultMap.put("titleText",odFlowInstance.getDocument().getTitleText());
                resultMap.put("sendNumber",odFlowInstance.getDocument().getSendNumber());
                resultMap.put("sourceDept",odFlowInstance.getDocument().getSourceDept());
            }
            resultMap.put("serial",odFlowInstance.getSerial());
            resultMap.put("typeName",odFlowInstance.getTypeName());
            resultMap.put("startTime",simpleDateFormat.format(odFlowInstance.getStartTime()));
            if(odFlowInstance.getCreateUser()!=null){
                resultMap.put("userName",odFlowInstance.getCreateUser().getUserName());
            }
            resultMap.put("instanceId",odFlowInstance.getInstanceId());

            resultList.add(resultMap);
        }
        return resultList;
    }

    private String removeNull(String arg){
        if(StringUtils.isEmpty(arg)){
            return "";
        }
        return arg;
    }

    private Integer getEmailCount(String filter) throws Exception{
        //邮件（数量）
        Integer totalCount = dao.getMailsCount(condition, filter, userOnlineInfo.getUserId());
       return totalCount;
    }

    private Integer getMenuCount(String filter) throws Exception{
        if (CollectionUtils.isEmpty(oaMenuId))
        {
            loadOAMenu();
        }
        Integer menuCount;
        if (userOnlineInfo.isAdmin())
        {
            //管理员用户
            menuCount=dao.getMenusCount(oaMenuId, filter);
        }
        else
        {
            //非管理员用户
            Collection<String> userOAId = new HashSet<String>(userOnlineInfo.getAppIds());
            userOAId.retainAll(oaMenuId);
            menuCount=dao.getMenusCount(userOAId, filter);
        }
        //菜单查询数量
        return menuCount;
    }

    private Integer getUserCount(String filter) throws Exception{
        //用户查询总数
        Integer userCount = dao.getUsersCount(userOnlineInfo.getAuthDeptIdsByUrl("/UserSearch"), condition);
        return userCount;
    }
}
