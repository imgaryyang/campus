package com.gzzm.oa.cloudstorage;

import com.gzzm.oa.cloudstorage.dao.FileDao;
import com.gzzm.oa.deptfile.*;
import com.gzzm.oa.userfile.*;
import com.gzzm.platform.commons.FileUploadService;
import com.gzzm.platform.commons.crud.SystemCrudUtils;
import com.gzzm.platform.log.LogAction;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.HttpMethod;
import net.cyan.arachne.annotation.Service;
import net.cyan.arachne.exts.ParameterCheck;
import net.cyan.commons.util.*;
import net.cyan.nest.annotation.Inject;

import java.util.*;

/**
 * @author gyw
 * @date 2017/8/23 0023
 */
@Service
public class CommonCloudCrud {

    protected static String PICSTRING = "'jpg','jpeg','gif','bmp','png'";

    protected static String DOCSTRING = "'txt','doc','docx','xls','xlsx','pdf','ppt','pptx'";

    protected static String MISSTRING = "'wav','mp3','ram','avi'";

    protected static String VIESTRING = "'avi','mov','mpeg','mpg','ram','mp4'";

    static {
        ParameterCheck.addNoCheckURL("/oa/cloudstorage/usercloud*");
    }

    @Inject
    protected FileDao dao;

    @Inject
    protected UserOnlineInfo userOnlineInfo;

    protected Integer nowfolderId;

    @Inject
    protected static Provider<FileUploadService> uploadServiceProvider;

    protected String eleStr = "";

    /**
     * 文件夹ID字符串，用“，”隔开
     */
    protected String folderIds;

    /**
     * 文件ID字符串
     */
    protected String fileIds;

    /**
     * 分享文件ID字符串
     */
    protected String shareIds;

    /**
     * 文件数量
     */
    protected Integer fileCount;


    protected InputFile[] files;
    /**
     * 上传的文件
     */
    protected InputFile file;

    /**
     * 风格标志
     */
    protected Integer tag;

    /**
     * 文件类型
     */
    protected Integer type;

    protected String searchName;

    public CommonCloudCrud() {
    }

    public Integer getNowfolderId() {
        return nowfolderId;
    }

    public void setNowfolderId(Integer nowfolderId) {
        this.nowfolderId = nowfolderId;
    }

    public String getFolderIds() {
        return folderIds;
    }

    public void setFolderIds(String folderIds) {
        this.folderIds = folderIds;
    }

    public String getFileIds() {
        return fileIds;
    }

    public void setFileIds(String fileIds) {
        this.fileIds = fileIds;
    }

    public String getShareIds() {
        return shareIds;
    }

    public void setShareIds(String shareIds) {
        this.shareIds = shareIds;
    }

    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    public InputFile[] getFiles() {
        return files;
    }

    public void setFiles(InputFile[] files) {
        this.files = files;
    }

    public InputFile getFile() {
        return file;
    }

    public void setFile(InputFile file) {
        this.file = file;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getEleStr() {
        return eleStr;
    }

    public void setEleStr(String eleStr) {
        this.eleStr = eleStr;
    }

    /**
     * 随机生成密码
     *
     * @return
     */
    protected String generateWord() {
        String[] beforeShuffle = new String[]{"0", "1", "2", "3", "4", "5", "6", "7",
                "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};
        List list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = afterShuffle.substring(5, 9);
        return result;
    }

    /**
     * 重命名（1、个人文件  2、个人文件夹  3、部门文件  4、部门文件夹）
     *
     * @param id
     * @param renameStr
     * @param type
     * @return
     * @throws Exception
     */
    @Service(method = HttpMethod.all)
    public Boolean fileRename(Integer id, String renameStr, Integer type) throws Exception {
        if (type == 1) {
            Integer count = dao.checkNameRepeat(UserFile.class, renameStr, userOnlineInfo.getUserId(),nowfolderId,id);
            if (count > 0) {
                return false;
            }
            UserFile file = dao.get(UserFile.class, id);
            file.setFileName(renameStr);
            SystemCrudUtils.saveLog(file, LogAction.modify,null,null);
            dao.save(file);
            return true;
        } else if (type == 2) {
            Integer count = dao.checkNameRepeat(UserFileFolder.class, renameStr, userOnlineInfo.getUserId(),nowfolderId,id);
            if (count > 0) {
                return false;
            }
            UserFileFolder userFileFolder = dao.get(UserFileFolder.class, id);
            userFileFolder.setFolderName(renameStr);
            SystemCrudUtils.saveLog(userFileFolder, LogAction.modify,null,null);
            dao.save(userFileFolder);
            return true;
        } else if (type == 3) {
            Integer count = dao.checkNameRepeat(DeptFile.class, renameStr, userOnlineInfo.getDeptId(),nowfolderId,id);
            if (count > 0) {
                return false;
            }
            DeptFile file = dao.get(DeptFile.class, id);
            file.setFileName(renameStr);
            SystemCrudUtils.saveLog(file, LogAction.modify,null,null);
            dao.save(file);
            return true;
        } else if (type == 4) {
            Integer count = dao.checkNameRepeat(DeptFileFolder.class, renameStr, userOnlineInfo.getDeptId(),nowfolderId,id);
            if (count > 0) {
                return false;
            }
            DeptFileFolder folder = dao.get(DeptFileFolder.class, id);
            folder.setFolderName(renameStr);
            SystemCrudUtils.saveLog(folder, LogAction.modify,null,null);
            dao.save(folder);
            return true;
        }
        return false;
    }

    protected String getFileFolderCondition(String condition) {
        if (searchName != null && !"".equals(searchName)) {
            condition += " and f.folderName like " + "'%" + searchName + "%' ";
            if (nowfolderId != 0) {
                if (type == null || type == 0) {
                    condition += " and f.parentFolderId =" + nowfolderId;
                }
            }
        } else {
            if (type == null || type == 0) {
                condition += " and f.parentFolderId =" + nowfolderId;
            }
        }
        condition += " order by f.createTime desc";
        return condition;
    }

    protected String getFileCondition(String condition) {
        if (type != null) {
            switch (type) {
                case 1:
                    condition += " and f.fileType in (" + PICSTRING + ")";
                    break;
                case 2:
                    condition += " and f.fileType in (" + DOCSTRING + ")";
                    break;
                case 3:
                    condition += " and f.fileType in (" + VIESTRING + ")";
                    break;
                case 4:
                    condition += " and f.fileType in (" +  MISSTRING+ ")";
                    break;
                case 5:
                    condition += " and f.fileType not in (" + PICSTRING + "," + DOCSTRING + "," + VIESTRING + "," + MISSTRING + ")";
                    break;
            }
        }
        if (searchName != null && !"".equals(searchName)) {
            if (nowfolderId != 0) {
                if (type == null || type == 0) {
                    condition += " and f.folderId =" + nowfolderId;
                }
            }
            condition += " and f.fileName like " + "'%" + searchName + "%'";

        } else {
            if (type == null || type == 0) {
                condition += " and f.folderId = " + nowfolderId;
            }
        }
        condition += " order by f.updateTime desc";
        return condition;
    }


    /**
     * 将字符串转化成ID集合
     *
     * @return
     */
    protected List<Integer> formatIdsByStr(String idsStr) {
        if (idsStr != null && !"".equals(idsStr)) {
            List<Integer> idsList = new ArrayList<Integer>();
            String[] ids = idsStr.split(",");
            for (String id : ids) {
                idsList.add(Integer.parseInt(id));
            }
            return idsList;
        }
        return null;
    }

    /**
     * 实体拷贝类型过滤器
     */
    public class BaseTypeFilter implements Filter<PropertyInfo> {
        @Override
        public boolean accept(PropertyInfo property) throws Exception {
            return DataConvert.isBaseType(property.getType()) || property.getType() == byte[].class ||
                    (property.getType().isArray() && property.getType().getComponentType().isEnum());
        }
    }


}
