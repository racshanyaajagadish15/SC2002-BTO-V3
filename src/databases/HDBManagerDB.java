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

/**
 * HDBManagerDB.java
 * This class is responsible for managing the HDBManager database.
 */

public class HDBManagerDB {
    private static final String MANAGER_FILEPATH = "resources/data/ManagerList.xlsx";

    /**
     * getManagerByNRIC(String nric)
     * This method retrieves a HDBManager object from the database using the NRIC.
     * It reads the NRIC from the file and checks if it matches the provided NRIC.
     * @param nric The NRIC of the HDBManager to retrieve.
     * @return HDBManager object if found, null otherwise.
     * @throws IOException if there is an error reading the file.
     * @throws NumberFormatException if there is an error converting the age from the file.
     * @throws IllegalArgumentException if the NRIC is invalid.
     */

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
    return null;
    }
    
    /**
     * getManagerByName(String name)
     * This method retrieves a HDBManager object from the database using the name.
     * It reads the name from the file and checks if it matches the provided name.
     * @param name The name of the HDBManager to retrieve.
     * @return HDBManager object if found, null otherwise.
     * @throws IOException if there is an error reading the file.
     * @throws NumberFormatException if there is an error converting the age from the file.
     */
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

    /** 
     * saveUser(HDBManager manager)
     * This method saves the HDBManager object to the database.
     * It checks if the NRIC already exists in the file and updates the password if it does.
     * @param manager The HDBManager object to save.
     * @return true if the user was saved successfully, false otherwise.
     * @throws IOException if there is an error reading or writing the file.
     */
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

    /** 
     * updateManagerPassword(String nric, String newPassword)
     * This method updates the password of the HDBManager in the database.
     * It reads the NRIC from the file and checks if it matches the provided NRIC.
     * @param nric The NRIC of the HDBManager to update.
     * @param newPassword The new password to set.
     * @throws IOException if there is an error reading or writing the file.
     */

    public static void updateManagerPassword(String nric, String newPassword) throws IOException {
        try (FileInputStream fis = new FileInputStream(MANAGER_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; 

                Cell nricCell = row.getCell(0); 
                if (nricCell != null && nricCell.getStringCellValue().equals(nric)) {
                    Cell passwordCell = row.getCell(1); 
                    if (passwordCell == null) {
                        passwordCell = row.createCell(1);
                    }
                    passwordCell.setCellValue(newPassword);
                    break;
                }
            }
            try (FileOutputStream fos = new FileOutputStream(MANAGER_FILEPATH)) {
                workbook.write(fos);
            }
        }
    }
    
}