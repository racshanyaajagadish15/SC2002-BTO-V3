package databases;

import models.HDBManager;
import enums.UserFileIndex;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;


public class HDBManagerDB {
    private static final String MANAGER_FILEPATH = "resources/data/ManagerList.xlsx";

    public static HDBManager getManagerByNRIC(String nric) throws IOException, NumberFormatException {
        try (FileInputStream fileStreamIn = new FileInputStream(MANAGER_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                // Skip header
                if (row.getRowNum() == 0) continue;

                // Read the user details from the row
                String fileNric = row.getCell(UserFileIndex.NRIC.getIndex()).getStringCellValue();

                // Check if NRIC match
                if (fileNric.equals(nric)) {
                    String name = row.getCell(UserFileIndex.NAME.getIndex()).getStringCellValue();
                    String maritalStatus = row.getCell(UserFileIndex.MARITAL_STATUS.getIndex()).getStringCellValue();
                    String filePassword = row.getCell(UserFileIndex.PASSWORD.getIndex()).getStringCellValue();
                    // !! Propogate any NumberFormatException to calling method
                    int age = (int) row.getCell(UserFileIndex.AGE.getIndex()).getNumericCellValue();

                    return new HDBManager(name, nric, age, maritalStatus, filePassword);
                }
            } 
        }
    // No results
    return null;
    }
    public static HDBManager getManagerbyName(String name) throws IOException, NumberFormatException {
        try (FileInputStream fileStreamIn = new FileInputStream(MANAGER_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                // Skip header
                if (row.getRowNum() == 0) continue;

                // Read the user details from the row
                String fileName = row.getCell(UserFileIndex.NAME.getIndex()).getStringCellValue();

                // Check if NRIC match
                if (fileName.equals(name)) {
                    String nric = row.getCell(UserFileIndex.NRIC.getIndex()).getStringCellValue();
                    String maritalStatus = row.getCell(UserFileIndex.MARITAL_STATUS.getIndex()).getStringCellValue();
                    String filePassword = row.getCell(UserFileIndex.PASSWORD.getIndex()).getStringCellValue();
                    // !! Propogate any NumberFormatException to calling method
                    int age = (int) row.getCell(UserFileIndex.AGE.getIndex()).getNumericCellValue();

                    return new HDBManager(name, nric, age, maritalStatus, filePassword);
                }
            } 
        }
    // No results
    return null;
    }

    public static boolean saveUser(HDBManager manager) throws IOException {
        try (FileInputStream fis = new FileInputStream(MANAGER_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean found = false;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String fileNric = row.getCell(UserFileIndex.NRIC.getIndex()).getStringCellValue();
                if (fileNric.equals(manager.getNric())) {
                    row.getCell(UserFileIndex.PASSWORD.getIndex()).setCellValue(manager.getPassword());
                    found = true;
                    break;
                }
            }

            if (found) {
                try (FileOutputStream fos = new FileOutputStream(MANAGER_FILEPATH)) {
                    workbook.write(fos);
                    return true;
                }
            }
            return false;
        }
    }

    public static void updateManagerPassword(String nric, String newPassword) throws IOException {
        try (FileInputStream fis = new FileInputStream(MANAGER_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                Cell nricCell = row.getCell(0); // Assuming NRIC is in the first column
                if (nricCell != null && nricCell.getStringCellValue().equals(nric)) {
                    Cell passwordCell = row.getCell(1); // Assuming password is in the second column
                    if (passwordCell == null) {
                        passwordCell = row.createCell(1);
                    }
                    passwordCell.setCellValue(newPassword);
                    break;
                }
            }

            // Write the updated workbook back to the file
            try (FileOutputStream fos = new FileOutputStream(MANAGER_FILEPATH)) {
                workbook.write(fos);
            }
        }
    }
    
}