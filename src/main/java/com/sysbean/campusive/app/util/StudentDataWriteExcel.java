package com.sysbean.campusive.app.util;

import com.sysbean.campusive.app.dto.InterimStudentDataDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class StudentDataWriteExcel {

    private static final DateTimeFormatter dobFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String[] sheetColumnOrder = new String[]{"Class_ID", "Student_ID", "ROLL NO", "ADMISSION NO", "STUDENT NAME", "STUDENT PHONE", "STUDENT EMAIL", "STUDENT DOB", "STUDENT BG", "PARENT NAME"};

    private final DataSource dataSource;

    public StudentDataWriteExcel(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void writeTempData(File convertedFile, String requestId, List<String> warning, List<String> error, List<Map<String, String>> newEntry, List<Map<String, String>> updatedEntry) {
        if(convertedFile == null || !convertedFile.canRead()) {
            error.add("Cannot read file");
            return;
        }

        Map<String, Sheet> sheets = new HashMap<>();

        Map<String, List<InterimStudentDataDTO>> data = new HashMap<>();

        try (FileInputStream file = new FileInputStream(convertedFile);) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            DataFormat fmt = workbook.createDataFormat();
            CellStyle stringStyle = workbook.createCellStyle();
            stringStyle.setDataFormat(fmt.getFormat("@"));

            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                String sheetName = sheet.getSheetName();
                if(sheets.containsKey(sheetName)) {
                    error.add(sheetName+" appeared multiple times in the file");
                    throw new RuntimeException(sheetName+" appeared multiple times in the file");
                }
                sheets.put(sheetName, sheet);
            }

            for(Sheet sheet : sheets.values()) {
                String sheetName = sheet.getSheetName();
                Row headerRow = sheet.getRow(0);


                for(int i = 0; i < sheetColumnOrder.length; i++) {
                    sheet.setDefaultColumnStyle(i, stringStyle);
                    String expectedRowName = sheetColumnOrder[i];
                    String actualRowName = getCellValue(headerRow, i);

                    if(!expectedRowName.equals(actualRowName)) {
                        error.add("Header is missing in the "+sheetName+" sheet : "+expectedRowName);
                    }
                }
                List<InterimStudentDataDTO> sheetData = processSheetData(sheet, warning, error);
                if(!sheetData.isEmpty()) {
                    data.put(sheetName, sheetData);
                }
            }

            if(error.isEmpty()) {
                storeData(requestId, data);
            }

            String duplicateStudentId = findDuplicateStudentId(requestId);
            if(duplicateStudentId.length() > 0) {
                error.add(duplicateStudentId);
            }

            String duplicatePhone = findDuplicatePhone(requestId);
            if(duplicatePhone.length() > 0) {
                error.add(duplicatePhone);
            }

            List<Map<String, String>> newRecord = getNewRecord(requestId);
            List<Map<String, String>> changeRecord = getChangeRecord(requestId);

            List<String> newPhoneNumbers = newRecord.stream()
                    .map(m -> m.get("PHONE_NUMBER"))
                    .collect(Collectors.toList());

            if(!newPhoneNumbers.isEmpty()) {
                List<String> existingPhones = checkExistingPhones(requestId, newPhoneNumbers);
                if(!existingPhones.isEmpty()) {
                    String join = String.join(",", existingPhones);
                    error.add("You are uploading old data as some phone numbers ["+join+"] are already available but in the uploaded file it is considered new. Download the latest file. ");
                }
            }

            newEntry.addAll(newRecord);
            updatedEntry.addAll(changeRecord);

        } catch (Exception e) {
            e.printStackTrace();
            error.add("Failed to perform the bulk upload operation. Contact administrator");
        }
    }

    private List<String> checkExistingPhones(String requestId, List<String> newPhoneNumbers) {
        String inClause = newPhoneNumbers.stream().map(s -> "?").collect(Collectors.joining(","));
        String sql =
                "SELECT distinct A.phone_number FROM tb_students_tmp A " +
                        "LEFT OUTER JOIN tb_students B on A.phone_number = B.phone_number " +
                        "WHERE A.phone_number in ("+inClause+") " +
                        "AND A.request_id = ? ";
        List<String> existingNumbers = new ArrayList<>();
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            int index = 1;
            for (String number : newPhoneNumbers) {
                preparedStatement.setString(index, number);
            }
            preparedStatement.setString(index+1, requestId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                existingNumbers.add(resultSet.getString("phone_number"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            CommonUtil.closeResource(connection);
        }
        return existingNumbers;
    }

    public List<Map<String, String>> getChangeRecord(String requestId) {
        List<Map<String, String>> data = new ArrayList<>();
        String sql =
                "SELECT A.student_name AS NEW_STUDENT_NAME, B.student_name OLD_STUDENT_NAME, " +
                        "       A.student_roll AS NEW_STUDENT_ROLL, B.student_roll OLD_STUDENT_ROLL, " +
                        "       A.admission_no AS NEW_STUDENT_ADMISSION, B.admission_no OLD_STUDENT_ADMISSION, " +
                        "       A.phone_number AS NEW_STUDENT_PHONE, B.phone_number OLD_STUDENT_PHONE, " +
                        "       A.student_email AS NEW_STUDENT_EMAIL, B.student_email OLD_STUDENT_EMAIL, " +
                        "       A.student_dob AS NEW_STUDENT_DOB, B.student_dob OLD_STUDENT_DOB, " +
                        "       A.student_bg AS NEW_STUDENT_BG, B.student_bg OLD_STUDENT_BG, " +
                        "       A.student_parent_name AS NEW_STUDENT_PARENT, B.student_parent_name OLD_STUDENT_PARENT, " +
                        "       A.class AS GROUP_ID, C.group_name AS GROUP_NAME, A.student_id AS STUDENT_ID " +
                        "  FROM tb_students_tmp A " +
                        "  LEFT OUTER JOIN tb_students B on A.student_id = B.student_id " +
                        "  LEFT OUTER JOIN tb_student_group C ON A.class = C.group_id " +
                        "  WHERE A.request_id = ? " +
                        "    AND B.student_id IS NOT NULL ";
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, requestId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String groupId = resultSet.getString("GROUP_ID");
                String groupName = resultSet.getString("GROUP_NAME");
                String studentId = resultSet.getString("STUDENT_ID");
                String newStudentName = resultSet.getString("NEW_STUDENT_NAME");
                String oldStudentName = resultSet.getString("OLD_STUDENT_NAME");
                long newStudentRoll = resultSet.getLong("NEW_STUDENT_ROLL");
                long oldStudentRoll = resultSet.getLong("OLD_STUDENT_ROLL");
                String newAdmissionNo = resultSet.getString("NEW_STUDENT_ADMISSION");
                String oldAdmissionNo = resultSet.getString("OLD_STUDENT_ADMISSION");
                String newPhoneNo = resultSet.getString("NEW_STUDENT_PHONE");
                String oldPhoneNo = resultSet.getString("OLD_STUDENT_PHONE");
                String newEmail = resultSet.getString("NEW_STUDENT_EMAIL");
                String oldEmail = resultSet.getString("OLD_STUDENT_EMAIL");
                String newDob = CommonUtil.getDate(resultSet.getDate("NEW_STUDENT_DOB"));
                String oldDob = CommonUtil.getDate(resultSet.getDate("OLD_STUDENT_DOB"));
                String newBg = resultSet.getString("NEW_STUDENT_BG");
                String oldBg = resultSet.getString("OLD_STUDENT_BG");
                String newParent = resultSet.getString("NEW_STUDENT_PARENT");
                String oldParent = resultSet.getString("OLD_STUDENT_PARENT");

                String change = "";

                change = change + checkChange("NAME", newStudentName, oldStudentName);

                if(newStudentRoll != oldStudentRoll) {
                    change = change + "ROLL ("+oldStudentRoll+"), ";
                }
                change = change + checkChange("ADMISSION NO", newAdmissionNo, oldAdmissionNo);
                change = change + checkChange("PHONE", newPhoneNo, oldPhoneNo);
                change = change + checkChange("EMAIL", newEmail, oldEmail);
                change = change + checkChange("DOB", newDob, oldDob);
                change = change + checkChange("BG", newBg, oldBg);
                change = change + checkChange("PARENT", newParent, oldParent);

                if(change.trim().length() > 0) {
                    Map<String, String> r = new HashMap<>();

                    r.put("CLASS", groupId);
                    r.put("CLASS_NAME", groupName);
                    r.put("STUDENT_ID", studentId);
                    r.put("STUDENT_ROLL", Long.toString(newStudentRoll));
                    r.put("ADMISSION_NO", newAdmissionNo);
                    r.put("STUDENT_NAME", newStudentName);
                    r.put("PHONE_NUMBER", newPhoneNo);
                    r.put("STUDENT_EMAIL", newEmail);
                    r.put("STUDENT_DOB", newDob);
                    r.put("STUDENT_BG", newBg);
                    r.put("STUDENT_PARENT_NAME", newParent);
                    r.put("CHANGE", change);

                    data.add(r);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            CommonUtil.closeResource(connection);
        }
        return data;
    }

    private String checkChange(String field, String firstValue, String secondValue) {
        String firstValueCheck = firstValue == null ? "" : firstValue;
        String secondValueCheck = secondValue == null ? "" : secondValue;

        if(!firstValueCheck.equals(secondValueCheck)) {
            return field+" ("+secondValueCheck+"), ";
        }
        return "";
    }

    public List<Map<String, String>> getNewRecord(String requestId) {
        List<Map<String, String>> data = new ArrayList<>();
        String sql = "select A.class, C.group_name, A.student_id, A.student_name, A.student_roll, A.admission_no, A.phone_number, A.student_email, A.student_dob, A.student_bg, A.student_parent_name " +
                "  from tb_students_tmp A " +
                "  left outer join tb_students B on A.student_id = B.student_id " +
                "  left outer join tb_student_group C on A.class = C.group_id "+
                "  where A.request_id = ? " +
                "  and B.student_id is null ";
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, requestId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Map<String, String> r = new HashMap<>();
                r.put("CLASS", resultSet.getString("class"));
                r.put("CLASS_NAME", resultSet.getString("group_name"));
                r.put("STUDENT_ID", resultSet.getString("student_id"));
                r.put("STUDENT_ROLL", resultSet.getString("student_roll"));
                r.put("ADMISSION_NO", resultSet.getString("admission_no"));
                r.put("STUDENT_NAME", resultSet.getString("student_name"));
                r.put("PHONE_NUMBER", resultSet.getString("phone_number"));
                r.put("STUDENT_EMAIL", resultSet.getString("student_email"));
                r.put("STUDENT_DOB", CommonUtil.getDate(resultSet.getDate("student_dob")));
                r.put("STUDENT_BG", resultSet.getString("student_bg"));
                r.put("STUDENT_PARENT_NAME", resultSet.getString("student_parent_name"));

                data.add(r);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            CommonUtil.closeResource(connection);
        }
        return data;
    }

    private List<InterimStudentDataDTO> processSheetData(Sheet sheet, List<String> warning, List<String> error) {
        List<InterimStudentDataDTO> sheetData = new ArrayList<>();
        String sheetName = sheet.getSheetName();
        int lastRowNum = sheet.getLastRowNum();

        if(lastRowNum < 1) {
            return new ArrayList<>();
        }

        String groupIdIfNew = UUID.randomUUID().toString();
        String groupId = sheet.getRow(1).getCell(0).getStringCellValue();
        groupId = groupId == null || groupId.trim().length() == 0 ? "" : groupId.trim();

        for(int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);

            String classId = getCellValue(row, 0);
            String studentId = getCellValue(row, 1);
            String rollNumber = getCellValue(row, 2);
            String admissionNo = getCellValue(row, 3);
            String studentName = getCellValue(row, 4);
            String studentPhone = getCellValue(row, 5);
            String studentEmail = getCellValue(row, 6);
            String studentDob = getCellValue(row, 7);
            String studentBg = getCellValue(row, 8);
            String parentName = getCellValue(row, 9);

            if(!groupId.equals(classId)) {
                error.add("All Class_ID should be same. In case its a new class, all Class_ID should be empty");
            }
            classId = groupId.equals("") ? groupIdIfNew : groupId;

            studentId = studentId == null || studentId.trim().length() == 0 ? UUID.randomUUID().toString() : studentId.trim();
            long studentRollNo = 0;
            try {
                studentRollNo = Long.parseLong(rollNumber);
            } catch (Exception e) {
                error.add("Failed to process roll number in sheet "+sheetName+" row "+(i+1));
            }

            if(studentName == null || studentName.trim().length() == 0) {
                warning.add("Invalid student name in sheet "+sheetName+" row "+(i+1));
            } else {
                studentName = studentName.trim();
            }

            if(studentPhone == null || studentPhone.trim().length() != 10) {
                warning.add("Invalid student phone number in sheet "+sheetName+" row "+(i+1));
            }

            if(studentEmail != null) {
                studentEmail = studentEmail.trim();
            }

            LocalDate studentDateOfBirth = null;
            if(studentDob != null && studentDob.trim().length() > 0) {
                try {
                    studentDateOfBirth = dobConverter(studentDob);
                } catch (Exception e) {
                    e.printStackTrace();
                    warning.add("Invalid student dob in sheet "+sheetName+" row "+(i+1));
                }
            }

            if(studentBg != null) {
                studentBg = studentBg.trim();
            }

            if(parentName != null) {
                parentName = parentName.trim();
            }

            sheetData.add(new InterimStudentDataDTO(classId, studentId, studentRollNo, admissionNo, studentName, studentPhone, studentEmail, studentDateOfBirth, studentBg, parentName));
        }

        return sheetData;
    }

    private LocalDate dobConverter(String dob) {
        return LocalDate.parse(dob, dobFormatter);
    }

    private String getCellValue(Row row, int index) {
        if(row == null) {
            return null;
        }
        Cell cell = row.getCell(index);
        if(cell == null) {
            return null;
        }
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }

    private void storeData(String requestId, Map<String, List<InterimStudentDataDTO>> data) {
        String sql =
                        "INSERT INTO tb_students_tmp(request_id, class, student_id, student_name, student_roll, admission_no, phone_number, student_email, student_dob, student_bg, student_parent_name) "+
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        Connection connection = getConnection();
        List<InterimStudentDataDTO> allStudents = data.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for(InterimStudentDataDTO allStudent : allStudents) {
                java.sql.Date dob = allStudent.getStudentDob() == null ? null : java.sql.Date.valueOf(allStudent.getStudentDob());
                preparedStatement.setString(1, requestId);
                preparedStatement.setString(2, allStudent.getClassId());
                preparedStatement.setString(3, allStudent.getStudentId());
                preparedStatement.setString(4, allStudent.getStudentName());
                preparedStatement.setLong(5, allStudent.getRollNumber());
                preparedStatement.setString(6, allStudent.getAdmissionNo());
                preparedStatement.setString(7, allStudent.getStudentPhone());
                preparedStatement.setString(8, allStudent.getStudentEmail());
                preparedStatement.setDate(9, dob);
                preparedStatement.setString(10, allStudent.getStudentBg());
                preparedStatement.setString(11, allStudent.getParentName());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            CommonUtil.closeResource(connection);
        }
    }

    private String findDuplicateStudentId(String requestId) {
        String sql = "select student_name, class from tb_students_tmp where student_id in ( " +
                "select student_id from tb_students_tmp " +
                "where request_id = ? " +
                "group by student_id " +
                "having count(student_id) > 1 " +
                ") " +
                "and request_id = ? ";
        String error = "";
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, requestId);
            preparedStatement.setString(2, requestId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                error = error + resultSet.getString("student_name") +"[Class ID: "+resultSet.getString("class")+"] \n";
            }
            if(error.length() > 0) {
                error = error + " have duplicate student id";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            CommonUtil.closeResource(connection);
        }
        return error;
    }

    private String findDuplicatePhone(String requestId) {
        String sql = "select student_name, class from tb_students_tmp where phone_number in ( " +
                "select phone_number from tb_students_tmp " +
                "where request_id = ? " +
                "group by phone_number " +
                "having count(phone_number) > 1 " +
                ") " +
                "and request_id = ? ";
        String error = "";
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, requestId);
            preparedStatement.setString(2, requestId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                error = error + resultSet.getString("student_name") +"[Class ID: "+resultSet.getString("class")+"],  \n";
            }
            if(error.length() > 0) {
                error = error + " have duplicate phone number";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            CommonUtil.closeResource(connection);
        }
        return error;
    }

    public void storeNewStudentData(String requestId) {
        List<Map<String, String>> newRecord = getNewRecord(requestId);
        String sql =
                        "INSERT INTO tb_students (class, student_id, student_name, student_roll, admission_no, phone_number, student_email, student_dob, student_bg," +
                        "       student_parent_name, created_on) "+
                        "VALUES (?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP())";
        if(newRecord.size() > 0) {
            Connection connection = getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                for(Map<String, String> data : newRecord) {

                    LocalDate dob = CommonUtil.getDob(data.get("STUDENT_DOB"));
                    java.sql.Date dobSql = dob == null ? null : java.sql.Date.valueOf(dob);

                    preparedStatement.setString(1, data.get("CLASS"));
                    preparedStatement.setString(2, data.get("STUDENT_ID"));
                    preparedStatement.setString(3, data.get("STUDENT_NAME"));
                    preparedStatement.setLong(4, Long.parseLong(data.get("STUDENT_ROLL")));
                    preparedStatement.setString(5, data.get("ADMISSION_NO"));
                    preparedStatement.setString(6, data.get("PHONE_NUMBER"));
                    preparedStatement.setString(7, data.get("STUDENT_EMAIL"));
                    preparedStatement.setDate(8, dobSql);
                    preparedStatement.setString(9, data.get("STUDENT_BG"));
                    preparedStatement.setString(10, data.get("STUDENT_PARENT_NAME"));
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                CommonUtil.closeResource(connection);
            }
        }
    }

    public void storeChangedStudentData(String requestId) {
        String sql =
                        "UPDATE tb_students "+
                        "   SET student_name = ?, student_roll = ?, admission_no = ?, phone_number = ?, student_email = ?, student_dob = ?, student_bg = ?," +
                        "       student_parent_name = ? "+
                        " WHERE student_id = ?";

        List<Map<String, String>> changeRecord = getChangeRecord(requestId);
        if(changeRecord.size() > 0) {
            Connection connection = getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                for(Map<String, String> data : changeRecord) {

                    LocalDate dob = CommonUtil.getDob(data.get("STUDENT_DOB"));
                    java.sql.Date dobSql = dob == null ? null : java.sql.Date.valueOf(dob);

                    preparedStatement.setString(1, data.get("STUDENT_NAME"));
                    preparedStatement.setLong(2, Long.parseLong(data.get("STUDENT_ROLL")));
                    preparedStatement.setString(3, data.get("ADMISSION_NO"));
                    preparedStatement.setString(4, data.get("PHONE_NUMBER"));
                    preparedStatement.setString(5, data.get("STUDENT_EMAIL"));
                    preparedStatement.setDate(6, dobSql);
                    preparedStatement.setString(7, data.get("STUDENT_BG"));
                    preparedStatement.setString(8, data.get("STUDENT_PARENT_NAME"));
                    preparedStatement.setString(9, data.get("STUDENT_ID"));
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                CommonUtil.closeResource(connection);
            }
        }
    }
}
