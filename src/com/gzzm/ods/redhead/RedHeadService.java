package com.gzzm.ods.redhead;

import com.gzzm.platform.commons.Tools;
import com.gzzm.platform.login.UserOnlineInfo;
import com.gzzm.platform.organ.*;
import net.cyan.commons.ext.CompressUtils;
import net.cyan.commons.util.*;
import net.cyan.commons.util.io.CacheData;
import net.cyan.nest.annotation.Inject;

import java.io.*;
import java.util.*;

/**
 * @author camel
 * @date 11-9-26
 */
public class RedHeadService
{
    public static final String REDHEAD_SELECT_APP = "redHead_select";

    @Inject
    private DeptService deptService;

    @Inject
    private RedHeadDao dao;

    public RedHeadService()
    {
    }

    public DeptService getDeptService()
    {
        return deptService;
    }

    public RedHeadDao getDao()
    {
        return dao;
    }

    public RedHead getRedHead(Integer redHeadId) throws Exception
    {
        return dao.getRedHead(redHeadId);
    }

    public RedHeadTitle getRedHeadTitle(Integer redHeadTitleId) throws Exception
    {
        return dao.getRedHeadTitle(redHeadTitleId);
    }

    public String getRedHeadPath(Integer redHeadId) throws Exception
    {
        return getRedHeadPath(getRedHead(redHeadId));
    }

    public static String getRedHeadPath(RedHead redhead) throws Exception
    {
        String path = "/temp/redhead/" + redhead.getRedHeadId();

        String result = Tools.getAppPath(path + ".doc");

        File file = new File(result);

        if (!file.exists() || file.lastModified() < redhead.getLastModified().getTime())
        {
            InputStream in = redhead.getRedHead();
            if (in != null)
                IOUtils.streamToFile(in, file);
        }

        return result;
    }

    public InputFile getRedHeadFile(Integer redHeadId) throws Exception
    {
        String path = getRedHeadPath(redHeadId);

        if (path == null)
            return null;
        else
            return new InputFile(new File(path));
    }

    public List<RedHead> getRedHeads(Integer deptId, final Integer typeId, UserOnlineInfo userOnlineInfo)
            throws Exception
    {
        Collection<Integer> authDeptIds = userOnlineInfo.getAuthDeptIds(REDHEAD_SELECT_APP);
        if (authDeptIds != null && authDeptIds.isEmpty())
        {
            DeptInfo dept = deptService.getDept(deptId);
            if (dept.getLevel() >= 1)
                authDeptIds = Arrays.asList(deptId, 1);
            else
                authDeptIds = Arrays.asList(deptId, dept.getParentDept(1).getDeptId(), 1);
        }

        return deptService.loadDatasFromParentDepts(deptId, new DeptOwnedDataProvider<RedHead>()
        {
            public List<RedHead> get(Integer deptId) throws Exception
            {
                return dao.getRedHeads(deptId, typeId);
            }
        }, authDeptIds, null);
    }

    /**
     * 生成红头文件压缩包，每个部门一个目录
     *
     * @param deptIds 红头文件的范围，即压缩哪些部门的红头文件，为null表示所有的部门
     * @return 红头文件的压缩包，zip文件
     * @throws Exception 数据库读取红头文件错误或者生成压缩包错误
     */
    public Inputable zip(Collection<Integer> deptIds) throws Exception
    {
        List<RedHead> redHeads = dao.getRedHeads(deptIds);

        CacheData cache = new CacheData();
        try
        {
            CompressUtils.Compress zip = CompressUtils.createCompress("zip", cache);
            for (RedHead redHead : redHeads)
            {
                zip.addFile(redHead.getDept().getDeptName() + "/" + redHead.getRedHeadName() + ".doc",
                        redHead.getRedHead());
            }

            zip.close();

            return cache;
        }
        catch (Exception ex)
        {
            //出错的时候清除缓存的文件
            try
            {
                cache.clear();
            }
            catch (Exception ex1)
            {
                //释放资源
            }

            throw ex;
        }
    }

    /**
     * 将红头文件压缩包解压并传到数据库中
     *
     * @param inputFile 压缩包
     * @param deptIds   允许上传的红头文件的范围，即允许上传哪些部门的红头文件，如果为null表示所有部门
     * @param listener  监听器，用于监听上传的进程
     * @throws Exception 解压错误或者数据库操作错误
     */
    public void up(InputFile inputFile, final Collection<Integer> deptIds, final RedHeadUpListener listener)
            throws Exception
    {
        CompressUtils.decompress(inputFile.getExtName().toLowerCase(), inputFile.getInputStream(),
                new CompressUtils.Decompresser()
                {
                    public void decompressDirectory(String name, long time, String comment) throws Exception
                    {
                    }

                    public void decompressFile(String fileName, InputStream in, long time, String comment)
                            throws IOException
                    {
                        int index = fileName.lastIndexOf('/');
                        if (index > 0)
                        {
                            //目录部分是部门名称，后面是红头名称
                            String deptName = fileName.substring(0, index);
                            String redHeadName = fileName.substring(index + 1);

                            index = deptName.lastIndexOf('/');
                            if (index >= 0)
                                deptName = deptName.substring(index + 1);

                            index = redHeadName.lastIndexOf('.');
                            if (index > 0)
                            {
                                String ext = redHeadName.substring(index + 1);
                                //红头必须是doc格式
                                if (ext.equalsIgnoreCase("doc"))
                                {
                                    redHeadName = redHeadName.substring(0, index);

                                    try
                                    {
                                        if (listener != null)
                                            listener.upFile(fileName);

                                        up(deptName, redHeadName, in, deptIds);
                                    }
                                    catch (Exception ex)
                                    {
                                        Tools.wrapException(ex);
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }

    public void up(String deptName, String redHeadName, InputStream in, Collection<Integer> deptIds)
            throws Exception
    {
        List<Dept> depts = dao.getDeptsByName(deptName);

        if (depts.size() == 0)
            return;

        Dept dept = null;

        if (deptIds == null)
        {
            if (depts.size() > 1)
                return;
            else
                dept = depts.get(0);
        }
        else
        {
            for (Dept dept1 : depts)
            {
                if (deptIds.contains(dept1.getDeptId()))
                {
                    if (dept != null)
                        return;

                    dept = dept1;
                }
            }

            if (dept == null)
                return;
        }

        List<RedHead> redHeads = dao.getRedHeadsByName(dept.getDeptId(), redHeadName);

        if (redHeads.size() > 1)
            return;

        RedHead redHead;
        if (redHeads.size() == 0)
        {
            redHead = new RedHead();
            redHead.setDeptId(dept.getDeptId());
            redHead.setRedHeadName(redHeadName);
        }
        else
        {
            redHead = redHeads.get(0);
        }

        redHead.setLastModified(new Date());
        redHead.setRedHead(in);

        dao.save(redHead);
    }
}
