package databases;

import models.HDBOfficer;
import enums.UserFileIndex;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row; 
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class HDBOfficerDB {
    private static final String MANAGER_FILEPATH = "resources/data/OfficerList.xlsx";

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
                    String name = row.getCell(UserFileIndex.NRIC.getIndex()).getStringCellValue();
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
}