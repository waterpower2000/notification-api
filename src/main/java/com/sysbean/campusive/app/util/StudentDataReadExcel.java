package com.sysbean.campusive.app.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Component
public class StudentDataReadExcel {

    private static final Map<String, Integer> sheetRecord = new HashMap<>();

    private static final Map<String, Sheet> sheets = new HashMap<>();
    private final DataSource dataSource;

    public StudentDataReadExcel(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void writeStudentData(Workbook workbook) {
        String sql =
                        "SELECT B.group_id, A.student_id, B.group_name, A.STUDENT_ROLL, A.ADMISSION_NO, A.STUDENT_NAME, " +
                        "       A.PHONE_NUMBER, A.STUDENT_EMAIL, DATE_FORMAT(A.STUDENT_DOB, '%d-%m-%Y') AS STUDENT_DOB, A.STUDENT_BG, A.STUDENT_PARENT_NAME "+
                        "  FROM tb_students A " +
                        "  LEFT OUTER JOIN tb_student_group B on A.class = B.group_id " +
                        " ORDER BY B.group_name desc ";
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String className = resultSet.getString("group_name");
                Sheet sheet = getSheet(workbook, className);

                int integer = sheetRecord.get(className)+1;
                Row header = sheet.createRow(integer);
                header.createCell(0).setCellValue(resultSet.getString("group_id"));
                header.createCell(1).setCellValue(resultSet.getString("student_id"));
                header.createCell(2).setCellValue(resultSet.getString("STUDENT_ROLL"));
                header.createCell(3).setCellValue(resultSet.getString("ADMISSION_NO"));
                header.createCell(4).setCellValue(resultSet.getString("STUDENT_NAME"));
                header.createCell(5).setCellValue(resultSet.getString("PHONE_NUMBER"));
                header.createCell(6).setCellValue(resultSet.getString("STUDENT_EMAIL"));
                header.createCell(7).setCellValue(resultSet.getString("STUDENT_DOB"));
                header.createCell(8).setCellValue(resultSet.getString("STUDENT_BG"));
                header.createCell(9).setCellValue(resultSet.getString("STUDENT_PARENT_NAME"));

                sheetRecord.put(className, integer);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            CommonUtil.closeResource(connection);
        }
    }

    public static Sheet getSheet(Workbook workbook, String className) {
        if(sheets.containsKey(className)) {
            return sheets.get(className);
        }
        Sheet sheet = workbook.createSheet(className);
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(8);

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Class_ID");
        header.createCell(1).setCellValue("Student_ID");
        header.createCell(2).setCellValue("ROLL NO");
        header.createCell(3).setCellValue("ADMISSION NO");
        header.createCell(4).setCellValue("STUDENT NAME");
        header.createCell(5).setCellValue("STUDENT PHONE");
        header.createCell(6).setCellValue("STUDENT EMAIL");
        header.createCell(7).setCellValue("STUDENT DOB");
        header.createCell(8).setCellValue("STUDENT BG");
        header.createCell(9).setCellValue("PARENT NAME");




        sheets.put(className, sheet);
        sheetRecord.put(className, 0);
        return sheet;
    }
}
