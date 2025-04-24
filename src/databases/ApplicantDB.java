package databases;

import models.Applicant;
import enums.UserFileIndex;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row; 
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * ApplicantDB handles the database operations for the Applicant class.
 * It provides methods to retrieve and save applicant information from an Excel file.
 */

public class ApplicantDB {
    private static final String APPLICANT_FILEPATH = "resources/data/ApplicantList.xlsx";

    /**
     * Retrieves an applicant's information based on their NRIC.
     * 
     * @param nric The NRIC of the applicant to retrieve.
     * @return An Applicant object containing the applicant's information, or null if not found.
     * @throws IOException If there is an error reading the file.
     * @throws NumberFormatException If there is an error converting the age to an integer.
     */
    
    public static Applicant getApplicantByNRIC(String nric) throws IOException, NumberFormatException {
		try (FileInputStream fileStreamIn = new FileInputStream(APPLICANT_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                // Skip header
                if (row.getRowNum() == 0) continue;

                // Read the user details from the row
                if (row.getCell(UserFileIndex.NRIC.getIndex()) == null || 
                    row.getCell(UserFileIndex.NRIC.getIndex()).getCellType() != org.apache.poi.ss.usermodel.CellType.STRING) {
                    continue;
                }
                String fileNric = row.getCell(UserFileIndex.NRIC.getIndex()).getStringCellValue();

                // Check if NRIC match
                if (fileNric.equals(nric)) {
                    String name = row.getCell(UserFileIndex.NAME.getIndex()).getStringCellValue();
                    String maritalStatus = row.getCell(UserFileIndex.MARITAL_STATUS.getIndex()).getStringCellValue();
                    String filePassword = row.getCell(UserFileIndex.PASSWORD.getIndex()).getStringCellValue();
                    // !! Propogate any NumberFormatException to calling method
                    int age = (int) row.getCell(UserFileIndex.AGE.getIndex()).getNumericCellValue();

                    return new Applicant(name, nric, age, maritalStatus, filePassword);
                }
            } 
        }
		return null;
    }

    /**
     * Saves the updated password for an applicant in the database.
     * 
     * @param applicant The Applicant object containing the updated information.
     * @return true if the password was successfully saved, false otherwise.
     * @throws IOException If there is an error writing to the file.
     */
    public static boolean saveUser(Applicant applicant) throws IOException {
        try (FileInputStream fis = new FileInputStream(APPLICANT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean found = false;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String fileNric = row.getCell(UserFileIndex.NRIC.getIndex()).getStringCellValue();
                if (fileNric.equals(applicant.getNric())) {
                    row.getCell(UserFileIndex.PASSWORD.getIndex()).setCellValue(applicant.getPassword());
                    found = true;
                    break;
                }
            }

            if (found) {
                try (FileOutputStream fos = new FileOutputStream(APPLICANT_FILEPATH)) {
                    workbook.write(fos);
                    return true;
                }
            }
            return false;
        }
    }
}
