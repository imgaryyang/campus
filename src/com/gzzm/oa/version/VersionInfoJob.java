package com.gzzm.oa.version;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.*;
import net.cyan.commons.util.xml.XmlUtils;
import net.cyan.nest.annotation.Inject;
import org.w3c.dom.*;

import java.io.File;
import java.util.*;

/**
 * 项目版本更新发布信息任务
 *
 * @author zy
 * @date 2017/1/10 10:42
 */
public class VersionInfoJob implements Runnable
{
    @Inject
    private static Provider<VersionDao> daoProvider;

    @Inject
    private static Provider<VersionConfig> versionConfigProvider;

    public VersionInfoJob()
    {
    }

    @Override
    public void run()
    {
        versionInfoJob();
    }

    /**
     * 获取/WEB-INF/目录
     *
     * @return 目录
     */
    public static String getRootDir()
    {
        String path = Tools.getConfigPath("/");
        if (path.length() - path.lastIndexOf("\\") == 2)
        {
            return path.substring(0, path.length() - 1);
        }
        else
        {
            return path;
        }
    }

    /**
     * 扫描版本更新发布内容xml，解析并更新到数据库
     */
    private void versionInfoJob()
    {
        File directory = new File(getRootDir() + versionConfigProvider.get().getFilePath());
        if (directory.isDirectory())
        {
            List<File> files = getFiles(directory);
            if (files != null && files.size() > 0)
            {
                for (File file : files)
                {
                    try
                    {
                        Document document = XmlUtils.createDocument(file);
                        Element root = document.getDocumentElement();
                        NodeList nodeList = root.getChildNodes();
                        if (nodeList != null)
                        {
                            for (int i = 0; i < nodeList.getLength(); i++)
                            {
                                Node node = nodeList.item(i);
                                if (node.getNodeName().equals("version"))
                                {
                                    NodeList nodeList1 = node.getChildNodes();
                                    for (int j = 0; j < nodeList1.getLength(); j++)
                                    {
                                        Node child = nodeList1.item(j);
                                        VersionInfo versionInfo = new VersionInfo();
                                        for (Node node2 = child.getFirstChild(); node2 != null; node2 = node2.getNextSibling())
                                        {
                                            ProjectDir project = new ProjectDir();
                                            if (node2.getNodeName().equals("projectCode"))
                                            {
                                                String projectCode = node2.getFirstChild() != null ? node2.getFirstChild().getNodeValue() : null;
                                                if (projectCode != null)
                                                {
                                                    project.setProjectCode(projectCode != null ? projectCode.trim() : null);
                                                }
                                            }
                                            if (node2.getNodeName().equals("projectName"))
                                            {
                                                String projectName = node2.getFirstChild() != null ? node2.getFirstChild().getNodeValue() : null;
                                                project.setProjectName(projectName != null ? projectName.trim() : null);
                                            }
                                            versionInfo.setProject(project);
                                            if (node2.getNodeName().equals("verName"))
                                            {
                                                String verName = node2.getFirstChild() != null ? node2.getFirstChild().getNodeValue() : null;
                                                versionInfo.setVerName(verName != null ? verName.trim() : null);
                                            }
                                            if (node2.getNodeName().equals("verNo"))
                                            {
                                                String verNo = node2.getFirstChild() != null ? node2.getFirstChild().getNodeValue() : null;
                                                versionInfo.setVerNo(verNo != null ? verNo.trim() : null);
                                            }
                                            if (node2.getNodeName().equals("publishContent"))
                                            {
                                                String content = node2.getFirstChild() != null ? node2.getFirstChild().getNodeValue() : null;
                                                if (content != null)
                                                {
                                                    String[] contents = content.split("\n");
                                                    String result = "";
                                                    for (String c : contents)
                                                    {
                                                        result += c.trim() + "\n";
                                                    }
                                                    versionInfo.setPublishContent(result.trim());
                                                }
                                            }
                                        }
                                        saveVersionInfo(versionInfo);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Tools.log(e);
                    }
                }
            }
        }
    }

    /**
     * 扫描该文件夹下面需要版本更新发布内容文件
     *
     * @param directory 文件夹
     * @return 所有版本更新发布内容的xml文件
     */
    private List<File> getFiles(File directory)
    {
        List<File> files = new ArrayList<File>();
        for (String filePath : directory.list())
        {
            File file = new File(directory.getAbsolutePath() + "\\" + filePath);
            if (file.isDirectory())
            {
                List<File> fileList = getFiles(file);
                if (fileList != null && fileList.size() > 0)
                {
                    files.addAll(fileList);
                }
            }
            else
            {
                if (file.getName().endsWith(versionConfigProvider.get().getFileLastName()))
                {
                    files.add(file);
                }
            }
        }
        return files;
    }

    /**
     * 保存版本更新发布信息
     *
     * @param versionInfo 版本更新发布信息
     * @throws Exception
     */
    private void saveVersionInfo(VersionInfo versionInfo) throws Exception
    {
        if (versionInfo != null && StringUtils.isNotBlank(versionInfo.getProject().getProjectCode()) && StringUtils.isNotBlank(versionInfo.getVerNo()))
        {
            VersionInfo versionInfo1 = daoProvider.get().getVersionInfo(versionInfo.getProject().getProjectCode(), versionInfo.getVerNo());
            if (versionInfo1 == null)
            {
                versionInfo1 = versionInfo;
                versionInfo1.setCreateTime(new Date());
            }
            else
            {
                versionInfo1.setPublishContent(versionInfo.getPublishContent());
                versionInfo1.setVerName(versionInfo.getVerName());
                versionInfo1.getProject().setProjectName(versionInfo.getProject().getProjectName());
            }
            versionInfo1.setUpdateTime(new Date());
            versionInfo1.setReleaseMode(ReleaseMode.SYSTEM);
            daoProvider.get().save(versionInfo1);
        }
    }
}
