package databases;

import models.HDBOfficer;
import enums.UserFileIndex;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row; 
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * HDBOfficerDB.java
 * This class is responsible for managing the HDBOfficer database.
 */

public class HDBOfficerDB {
    private static final String MANAGER_FILEPATH = "resources/data/OfficerList.xlsx";

    /**
     * getOfficerByNRIC(String nric)
     * This method retrieves a HDBOfficer object from the database using the NRIC.
     * It reads the NRIC from the file and checks if it matches the provided NRIC.
     * @param nric The NRIC of the HDBOfficer to retrieve.
     * @return HDBOfficer object if found, null otherwise.
     * @throws IOException if there is an error reading the file.
     * @throws NumberFormatException if there is an error converting the age from the file.
     * @throws IllegalArgumentException if the NRIC is invalid.
     */

    public static HDBOfficer getOfficerByNRIC(String nric) throws IOException, NumberFormatException {
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

                    return new HDBOfficer(name, nric, age, maritalStatus, filePassword);
                }
            } 
        }
    // No results
    return null;
    }

    /**
     * saveUser(HDBOfficer officer)
     * This method saves the HDBOfficer object to the database.
     * It updates the password of the officer in the file.
     * @param officer The HDBOfficer object to save.
     * @return true if the officer was found and updated, false otherwise.
     * @throws IOException if there is an error reading or writing the file.
     */

    public static boolean saveUser(HDBOfficer officer) throws IOException {
        try (FileInputStream fis = new FileInputStream(MANAGER_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean found = false;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String fileNric = row.getCell(UserFileIndex.NRIC.getIndex()).getStringCellValue();
                if (fileNric.equals(officer.getNric())) {
                    row.getCell(UserFileIndex.PASSWORD.getIndex()).setCellValue(officer.getPassword());
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
}