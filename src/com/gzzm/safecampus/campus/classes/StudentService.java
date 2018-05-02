package com.gzzm.safecampus.campus.classes;

import com.gzzm.platform.addivision.AdDivision;
import com.gzzm.platform.addivision.AdDivisionDao;
import com.gzzm.platform.commons.Sex;
import com.gzzm.platform.login.UserOnlineInfo;
import net.cyan.commons.util.InputFile;
import net.cyan.commons.util.Inputable;
import net.cyan.nest.annotation.Inject;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Neo
 * @date 2018/4/10 19:49
 */
public class StudentService
{
    @Inject
    private GradeDao gradeDao;

    @Inject
    private ClassesDao classesDao;

    @Inject
    private AdDivisionDao dao;

    @Inject
    private UserOnlineInfo userOnlineInfo;

    public StudentService()
    {
    }

    /**
     * 学生信息导出
     *
     * @param classesId 班级Id
     * @return 导出的文件
     * @throws Exception 操作异常
     */
    public InputFile exportStudent(Integer classesId) throws Exception
    {
        Map<String, List<Student>> classesStudentMap = new HashMap<>();

        String name = "";
        if (classesId == null || classesId == 0)
        {
            List<Integer> schoolList = gradeDao.getGradeIdByDept(userOnlineInfo.getDeptId());
            for (Integer gradeId : schoolList)
            {
                List<Classes> classesList = classesDao.getGradeClasses(gradeId);
                if (classesList.size() > 0)
                    for (Classes classes : classesList)
                    {
                        classesStudentMap.put(classes.getAllName(), classes.getStudents());
                    }
            }
        }
        //选择年级
        else if (classesId < 0)
        {
            name = gradeDao.getGradeName(-classesId);
            List<Classes> classesList = classesDao.getGradeClasses(-classesId);
            if (classesList.size() > 0)
                for (Classes classes : classesList)
                {
                    classesStudentMap.put(name + classes.getClassesName(), classes.getStudents());
                }
        }
        //选择班级
        else
        {
            name = classesDao.getClassesName(classesId);
            Classes classes = classesDao.load(Classes.class, classesId);

            classesStudentMap.put(classes.getAllName(), classes.getStudents());
        }

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

        HSSFWorkbook wb = new HSSFWorkbook();

        if (classesStudentMap.size() > 0)
        {
            //每个部门建立一个sheet
            HSSFSheet book;
            Set<Map.Entry<String, List<Student>>> set = classesStudentMap.entrySet();
            for (Map.Entry<String, List<Student>> s : set)
            {
                book = wb.createSheet(s.getKey());
                book.setDefaultColumnWidth(15);
                book.setDefaultRowHeight((short) 400);

                CellRangeAddress broderick00 = new CellRangeAddress(0, 0, 0, 10);
                book.addMergedRegion(broderick00);

                List<Student> list = s.getValue();
                HSSFRow[] row = new HSSFRow[list.size() + 2];

                row[0] = book.createRow(0);
                row[0].createCell(0).setCellValue(s.getKey() + "-学生表");

                row[1] = book.createRow(1);
                row[1].createCell(0).setCellValue("学号");
                row[1].createCell(1).setCellValue("姓名");
                row[1].createCell(2).setCellValue("性别");
                row[1].createCell(3).setCellValue("出生日期");
                row[1].createCell(4).setCellValue("省份");
                row[1].createCell(5).setCellValue("城市");
                row[1].createCell(6).setCellValue("电话");
                row[1].createCell(7).setCellValue("住址");
                row[1].createCell(8).setCellValue("入学时间");
                row[1].createCell(9).setCellValue("身份证号");
                row[1].createCell(10).setCellValue("备注");
                row[1].createCell(11).setCellValue("家属姓名");
                row[1].createCell(12).setCellValue("家属关系");
                row[1].createCell(13).setCellValue("家属联系方式");

                //部门sheet添加部门人员信息
                for (int j = 2; j < list.size() + 2; j++)
                {
                    Student stu = list.get(j - 2);
                    row[j] = book.createRow(j);
                    row[j].createCell(0).setCellValue(stu.getStudentNo());
                    row[j].createCell(1).setCellValue(stu.getStudentName());
                    row[j].createCell(2).setCellValue(stu.getSex().equals(Sex.male) ? "男" : "女");
                    Date date = stu.getBirthday();
                    row[j].createCell(3).setCellValue(date == null ? "" : sdf2.format(date));
                    String city = stu.getCity().getDivisionName();
                    String province = stu.getProvince().getDivisionName();
                    if (city != null)
                        if (Character.isDigit(city.charAt(0)))
                        {
                            AdDivision c = dao.getAdDivision(Integer.parseInt(city));
                            if (c != null)
                                city = c.toString();
                        }
                    if (province != null)
                        if (Character.isDigit(province.charAt(0)))
                        {
                            AdDivision p = dao.getAdDivision(Integer.parseInt(province));
                            if (p != null)
                                province = p.toString();
                        }

                    row[j].createCell(4).setCellValue(province);
                    row[j].createCell(5).setCellValue(city);

                    row[j].createCell(6).setCellValue(stu.getPhone());
                    row[j].createCell(7).setCellValue(stu.getAddress());
                    Date time = stu.getEntryTime();
                    row[j].createCell(8).setCellValue(time == null ? "" : sdf2.format(time));
                    row[j].createCell(9).setCellValue(stu.getIdCard());
                    row[j].createCell(10).setCellValue(stu.getDesc());
                    //家属信息
                    StringBuffer names = new StringBuffer();
                    StringBuffer phones = new StringBuffer();
                    StringBuffer relations = new StringBuffer();
                    List<Guardian> guardians = stu.getGuardians();
                    if (guardians != null && guardians.size() > 0)
                    {
                        for (Guardian guardian : guardians)
                        {
                            names = names.append(";").append(guardian.getName());
                            phones = phones.append(";").append(guardian.getPhone());
                            relations = relations.append(";").append(guardian.getRelationInfo());
                        }
                        names = names.deleteCharAt(0);
                        phones = phones.deleteCharAt(0);
                        relations = relations.deleteCharAt(0);
                    }
                    row[j].createCell(11).setCellValue(names.toString());
                    row[j].createCell(12).setCellValue(relations.toString());
                    row[j].createCell(13).setCellValue(phones.toString());
                }
            }
        }
        //转换为输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        return new InputFile(new Inputable.ByteInput(out.toByteArray()), name + "学生信息表" + sdf2.format(new Date()) + ".xls");
    }
}
