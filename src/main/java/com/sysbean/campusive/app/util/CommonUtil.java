package com.sysbean.campusive.app.util;


import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class CommonUtil {
    public static File convertMultiPartToFile(MultipartFile file) {
        try {
            if(file == null)
                return null;

            File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
            try(FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(file.getBytes());
            }
            return convFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert file object");
        }
    }

    public static String getDob(LocalDate dob) {
        if(dob == null) {
            return null;
        }
        return dob.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public static LocalDate getDob(String dob) {
        if(dob.equals(null)) {
            return null;

        }
        return LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public static void closeResource(Connection connection) {
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (Exception e) {}
    }

    public static String getDate(Date date) {
        if(date == null) {
            return null;
        }
        return date.toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
