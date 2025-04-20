package databases;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import models.HDBOfficer;
import models.OfficerRegistration;
import models.Project;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import enums.OfficerRegistrationFileIndex;

public class OfficerRegistrationDB {
    private static final String REGISTRATION_FILEPATH = "resources/data/OfficerRegistration.xlsx";

    public static ArrayList<OfficerRegistration> getOfficerRegistrationsByOfficer(HDBOfficer officer) throws IOException {
        ArrayList<OfficerRegistration> officerRegistrations = new ArrayList<>();

        try (FileInputStream fileStreamIn = new FileInputStream(REGISTRATION_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                // Skip header
                if (row.getRowNum() == 0) continue;
                String officerNric = row.getCell(OfficerRegistrationFileIndex.NRIC.getIndex()).getStringCellValue();

                if (officerNric.equals(officer.getNric())) {
                    int registrationId = (int) row.getCell(OfficerRegistrationFileIndex.ID.getIndex()).getNumericCellValue();
                    int projectId = (int) row.getCell(OfficerRegistrationFileIndex.PROJECT.getIndex()).getNumericCellValue();
                    String status = row.getCell(OfficerRegistrationFileIndex.STATUS.getIndex()).getStringCellValue();
                    
                    OfficerRegistration registration = new OfficerRegistration(registrationId, officer, projectId, status);
                    officerRegistrations.add(registration);
                }
            }
        }
        return officerRegistrations;
    }

    public static OfficerRegistration createOfficerRegistration(HDBOfficer officer, Project project, String registrationStatus) throws IOException{
        try (FileInputStream fileStreamIn = new FileInputStream(REGISTRATION_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            Row lastRow = sheet.getRow(sheet.getLastRowNum());
            int registrationID = (lastRow != null && lastRow.getCell(OfficerRegistrationFileIndex.ID.getIndex()) != null) 
                ? (int) lastRow.getCell(OfficerRegistrationFileIndex.ID.getIndex()).getNumericCellValue() 
                : 1; // Default to 1 if no rows exist
            populateRegistrationRow(row, registrationID, officer, project, registrationStatus);
            // Save the changes to the file
            try (FileOutputStream fos = new FileOutputStream(REGISTRATION_FILEPATH)) {
                workbook.write(fos);
            }
            workbook.close();
            return new OfficerRegistration(registrationID, officer, project.getProjectID(), registrationStatus);
        }
    }

    private static void populateRegistrationRow(Row row, int registrationID, HDBOfficer officer, Project project, String status) {
        row.createCell(OfficerRegistrationFileIndex.ID.getIndex()).setCellValue(registrationID); // Using row number as ID
        row.createCell(OfficerRegistrationFileIndex.NRIC.getIndex()).setCellValue(officer.getNric());
        row.createCell(OfficerRegistrationFileIndex.PROJECT.getIndex()).setCellValue(project.getProjectID());
        row.createCell(OfficerRegistrationFileIndex.STATUS.getIndex()).setCellValue(status);
        row.createCell(OfficerRegistrationFileIndex.DATE.getIndex()).setCellValue(new Date());
    }
}