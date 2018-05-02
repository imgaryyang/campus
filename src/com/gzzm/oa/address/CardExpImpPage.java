package com.gzzm.oa.address;

import com.gzzm.platform.annotation.*;
import com.gzzm.platform.commons.Sex;
import com.gzzm.platform.commons.crud.SystemCrudUtils;
import com.gzzm.platform.log.LogAction;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.arachne.*;
import net.cyan.arachne.annotation.*;
import net.cyan.commons.util.*;
import net.cyan.commons.validate.annotation.Require;
import net.cyan.crud.arachne.ProgressImportLisenter;
import net.cyan.crud.exporters.*;
import net.cyan.crud.importers.*;
import net.cyan.crud.view.*;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.util.*;

/**
 * 通讯录导入导出的Page类
 *
 * @author whf
 * @date 2010-3-19
 */
@Service
public class CardExpImpPage
{
    /**
     * 表示当前是对部门通讯录还是个人通讯录的维护
     */
    private AddressType type = AddressType.user;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    /**
     * 拥有权限维护的部门
     */
    @AuthDeptIds
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Collection<Integer> authDeptIds;

    @UserId
    private Integer userId;

    /**
     * 当前维护的部门ID
     */
    private Integer deptId;

    /**
     * 选择的组id
     */
    private Integer groupId;

    /**
     * 上传的导入文件
     */
    @Require
    private InputFile impFile;

    /**
     * 导出类型：xls、csv，用Store标签在cookie中保存用户的习惯
     */
    @Store(scope = Store.COOKIE)
    private String ext;

    /**
     * 导出时选中的列，用Store标签在cookie中保存用户的习惯
     */
    @Store(scope = Store.COOKIE)
    @NotSerialized
    private String[] exportCols;

    /**
     * 导入时的列映射，用Store标签在cookie中保存用户的习惯
     */
    @Store(scope = Store.COOKIE)
    @NotSerialized
    private List<CardItemMap> itemMaps;


    @Inject
    private AddressCardDao dao;

    public CardExpImpPage()
    {
    }

    public AddressType getType()
    {
        return type;
    }

    public void setType(AddressType type)
    {
        this.type = type;
    }

    public Integer getDeptId()
    {
        if (deptId == null)
        {
            if (authDeptIds != null)
            {
                if (authDeptIds.size() > 0)
                    deptId = authDeptIds.iterator().next();
            }
            else
            {
                deptId = userOnlineInfo.getBureauId();
            }
        }

        return deptId;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    @NotSerialized
    public Integer getOwner()
    {
        return type == AddressType.user ? userId : getDeptId();
    }

    public String[] getExportCols()
    {
        return exportCols;
    }

    public void setExportCols(String[] exportCols)
    {
        this.exportCols = exportCols;
    }

    public String getExt()
    {
        return ext;
    }

    public void setExt(String ext)
    {
        this.ext = ext;
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public InputFile getImpFile()
    {
        return impFile;
    }

    public void setImpFile(InputFile impFile)
    {
        this.impFile = impFile;
    }

    @NotSerialized
    public List<CardItem> getItems()
    {
        return CardItem.getItems();
    }

    public List<CardItemMap> getItemMaps()
    {
        return itemMaps;
    }

    public void setItemMaps(List<CardItemMap> itemMaps)
    {
        this.itemMaps = itemMaps;
    }

    /**
     * 可选择的组
     *
     * @return 可选择组列表
     * @throws Exception 数据库查询数据错误
     */
    @NotSerialized
    public List<AddressGroup> getGroups() throws Exception
    {
        return dao.getGroupsByOwner(type, getOwner());
    }

    /**
     * 进入导出界面
     *
     * @return 导出的页面
     * @throws Exception 数据库异常
     */
    @Service(url = "/oa/address/export")
    public String initExport() throws Exception
    {
        //默认导出方式为xls
        if (ext == null)
            ext = "xls";

        //设置默认导出的列
        if (exportCols == null)
            exportCols = CardItem.DEFAULTCOLS;

        return "export";
    }

    /**
     * 进入导入页面
     *
     * @return 导入的页面
     */
    @Service(url = "/oa/address/import")
    public String initImport()
    {
        return "import";
    }

    /**
     * 执行通讯录导出
     *
     * @return 最终导出的文件
     * @throws Exception 数据库查询错误或者IO异常
     */
    @Service(url = "/oa/address/export/down")
    public InputFile exportCards() throws Exception
    {
        List<AddressCard> dataList;
        if (groupId == null || Null.isNull(groupId) || groupId == 0)
            dataList = dao.getCardsByOwner(type, getOwner());
        else
            dataList = new ArrayList<AddressCard>(dao.getAddressGroup(groupId).getCards());

        if (exportCols != null && exportCols.length > 0)
        {
            List<Column> columns = new ArrayList<Column>(exportCols.length);
            for (String col : exportCols)
            {
                CardItem item = CardItem.getItem(col);

                if (item != null)
                {
                    if (!CardItem.isSimpleAttribute(col))
                        col = "attributes.get('" + col + "')";

                    columns.add(new ExpressionColumn(item.getName(), col));
                }
            }

            return ExportUtils.export(dataList, columns, new ExportParameters("通讯录"), ext);
        }

        return null;
    }

    /**
     * 解析要导入的文件
     *
     * @return 导入的文件有数据，返回true，没有数据返回false
     * @throws Exception 异常
     */
    @Service(method = HttpMethod.post)
    public boolean readImpFile() throws Exception
    {
        if (impFile != null)
        {
            String ext = IOUtils.getExtName(impFile.getName());
            File file = IOUtils.createTempFile(getImpFile().getInputable());
            String[] headers;

            EntityImportor importor = new EntityImportor<AddressCard>(AddressCard.class);
            try
            {
                importor.load(new FileInputStream(file), ext);
                headers = importor.getHeaders();
            }
            finally
            {
                try
                {
                    importor.close();
                }
                catch (Exception ex)
                {
                    //释放资源失败，忽略
                }
            }

            if (headers != null)
            {
                ImpFileCache cache = new ImpFileCache();
                cache.setExt(ext);
                cache.setPath(file.getAbsolutePath());
                cache.setHeaders(headers);


                RequestContext.getContext().getSession().setAttribute("imp_cache", cache);

                return true;
            }
        }

        return false;
    }

    @Service(url = "/oa/address/import/mapping")
    public String mapCols()
    {
        ImpFileCache cache = (ImpFileCache) RequestContext.getContext().getSession().getAttribute("imp_cache");

        if (cache != null)
        {
            List<CardItemMap> itemMaps = new ArrayList<CardItemMap>(cache.getHeaders().length);
            List<CardItem> items = getItems();
            for (String header : cache.getHeaders())
            {
                CardItemMap itemMap = new CardItemMap(header);

                String field = null;
                if (this.itemMaps != null)
                {
                    for (CardItemMap itemMap2 : this.itemMaps)
                    {
                        if (itemMap2.getCol().equals(itemMap.getCol()))
                        {
                            field = itemMap2.getField();
                            break;
                        }
                    }
                }

                if (field == null)
                    field = header;

                for (CardItem item : items)
                {
                    if (item.getField().equals(field) || item.getName().equals(field))
                    {
                        itemMap.setField(item.getField());
                        break;
                    }
                }

                itemMaps.add(itemMap);
            }

            this.itemMaps = itemMaps;

            return "cols_mapping";
        }

        return null;
    }

    /**
     * 导入联系人
     *
     * @return 导入的记录数
     * @throws Exception 导入数据异常，可能由io异常引起或者是数据库写入异常引起
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @ObjectResult
    @Service(method = HttpMethod.post)
    public int importCards() throws Exception
    {
        ImpFileCache cache = (ImpFileCache) RequestContext.getContext().getSession().getAttribute("imp_cache");

        if (cache != null)
        {
            final Integer owner = getOwner();

            EntityImportor importor = new EntityImportor<AddressCard>(AddressCard.class)
            {
                @Override
                protected void fill(AddressCard card, DataRecord record) throws Exception
                {
                    String[] headers = getHeaders();
                    int n = headers.length;
                    int columnCount = record.getColumnCount();
                    if (n > columnCount)
                        n = columnCount;

                    Map<String, String> attributes = null;
                    for (int i = 0; i < n; i++)
                    {
                        String header = headers[i];
                        for (CardItemMap itemMap : itemMaps)
                        {
                            if (itemMap.getCol().equals(header))
                            {
                                String value = record.get(String.class, i);

                                if (value != null)
                                {
                                    String field = itemMap.getField();
                                    if ("cardName".equals(field))
                                    {
                                        card.setCardName(value);
                                    }
                                    else if ("nick".equals(field))
                                    {
                                        card.setNick(value);
                                    }
                                    else if ("sex".equals(field))
                                    {
                                        card.setSex(DataConvert.convertValue(Sex.class, value));
                                    }
                                    else
                                    {
                                        if (attributes == null)
                                        {
                                            attributes = new HashMap<String, String>();
                                            card.setAttributes(attributes);
                                        }

                                        attributes.put(field, value);
                                    }
                                }

                                break;
                            }
                        }
                    }
                }

                @Override
                protected void save(AddressCard card, DataRecord record) throws Exception
                {
                    card.setType(type);
                    card.setOwner(owner);

                    if (groupId != null && !Null.isNull(groupId) && groupId != 0)
                        card.setGroups(Collections.singletonList(new AddressGroup(groupId)));
                    SystemCrudUtils.saveLog(card, LogAction.add, null, null);
                    dao.add(card);
                }
            };

            importor.addListiner(ProgressImportLisenter.getInstance());

            try
            {
                importor.load(new FileInputStream(new File(cache.getPath())), cache.getExt());

                importor.imp();

                return importor.getCount();
            }
            finally
            {
                try
                {
                    importor.close();
                }
                catch (Throwable ex)
                {
                    //释放资源失败，忽略
                }

                try
                {
                    new File(cache.getPath()).delete();
                }
                catch (Throwable ex)
                {
                    //删除临时文件错误，忽略
                }
            }
        }

        return -1;
    }
}