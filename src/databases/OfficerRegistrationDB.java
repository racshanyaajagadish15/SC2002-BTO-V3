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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import enums.OfficerRegistrationFileIndex;


/**
 * OfficerRegistrationDB.java
 * This class is responsible for managing the Officer Registration database.
 */

public class OfficerRegistrationDB {
    private static final String REGISTRATION_FILEPATH = "resources/data/OfficerRegistration.xlsx";

    /**
     * createOfficerRegistration(HDBOfficer officer, Project project, String registrationStatus)
     * This method creates a new officer registration entry in the database.
     * It generates a new registration ID, populates the row with officer details, and saves the changes to the file.
     * @param officer The HDBOfficer object to register.
     * @param project The Project object associated with the registration.
     * @param registrationStatus The status of the registration.
     * @return OfficerRegistration object containing the registration details.
     * @throws IOException if there is an error reading or writing the file.
     */

    public static OfficerRegistration createOfficerRegistration(HDBOfficer officer, Project project, String registrationStatus) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(REGISTRATION_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row lastRow = sheet.getRow(sheet.getLastRowNum());
            int registrationID;
            if (lastRow == null || lastRow.getCell(OfficerRegistrationFileIndex.ID.getIndex()) == null) {
                registrationID = 1; // Start with ID 1 if no valid last row exists
            } else {
                registrationID  = (int) lastRow.getCell(OfficerRegistrationFileIndex.ID.getIndex()).getNumericCellValue() + 1;
            }
            int newRowNum = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(newRowNum);
            populateRegistrationRow(row, registrationID, officer, project, registrationStatus);
            // Save the changes to the file
            try (FileOutputStream fos = new FileOutputStream(REGISTRATION_FILEPATH)) {
                workbook.write(fos);
            }
            workbook.close();
            return new OfficerRegistration(registrationID, officer, project, registrationStatus);
        }
    }

    /**
     * getAllOfficerRegistrations()
     * This method retrieves all officer registrations from the database.
     * It reads the file, iterates through the rows, and creates OfficerRegistration objects for each entry.
     * @return ArrayList of OfficerRegistration objects.
     * * @throws IOException if there is an error reading the file.
     */

    public static ArrayList<OfficerRegistration> getAllOfficerRegistrations() throws IOException {
        ArrayList<OfficerRegistration> officerRegistrations = new ArrayList<>();
    
        try (FileInputStream fileStreamIn = new FileInputStream(REGISTRATION_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
    
            for (Row row : sheet) {
                // Skip header row
                if (row.getRowNum() == 0) continue;
    
                int registrationId = (int) row.getCell(OfficerRegistrationFileIndex.ID.getIndex()).getNumericCellValue();
                String officerNric = row.getCell(OfficerRegistrationFileIndex.NRIC.getIndex()).getStringCellValue();
                int projectId = (int) row.getCell(OfficerRegistrationFileIndex.PROJECT.getIndex()).getNumericCellValue();
                String status = row.getCell(OfficerRegistrationFileIndex.STATUS.getIndex()).getStringCellValue();
    
                HDBOfficer officer = HDBOfficerDB.getOfficerByNRIC(officerNric);
                Project project = Project.getProjectByIdDB(projectId);
                
                OfficerRegistration registration = new OfficerRegistration(registrationId, officer, project, status);
                officerRegistrations.add(registration);                    
                }
            }
            return officerRegistrations;
        }
    

    /**
     * getOfficerRegistrationByID(int registrationID)
     * This method retrieves an officer registration entry by its ID.
     * It reads the file, iterates through the rows, and returns the matching OfficerRegistration object.
     * @param registrationID The ID of the officer registration to retrieve.
     * @return OfficerRegistration object if found, null otherwise.
     * @throws IOException if there is an error reading the file.
     */
    public static void updateOfficerRegistration(int registrationID, String newStatus) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(REGISTRATION_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
    
            Sheet sheet = workbook.getSheetAt(0);
    
            // Iterate through the rows to find the matching registration ID
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
    
                // Retrieve the registration ID from the row
                int rowRegistrationID = (int) row.getCell(OfficerRegistrationFileIndex.ID.getIndex()).getNumericCellValue();
    
                // Check if the registration ID matches
                if (rowRegistrationID == registrationID) {
                    // Update the status in the row
                    Cell statusCell = row.getCell(OfficerRegistrationFileIndex.STATUS.getIndex());
                    if (statusCell == null) {
                        statusCell = row.createCell(OfficerRegistrationFileIndex.STATUS.getIndex());
                    }
                    statusCell.setCellValue(newStatus);
    
                    // Save the changes to the file
                    try (FileOutputStream fileOut = new FileOutputStream(REGISTRATION_FILEPATH)) {
                        workbook.write(fileOut);
                    }
                    return;
                }
            }
    
            // If no matching registration ID is found, throw an exception
            throw new IllegalArgumentException("No matching registration found for ID: " + registrationID);
        }
    }

    /**
     * deleteOfficerRegistrationByProjID(Project project)
     * This method deletes officer registrations associated with a specific project ID.
     * It reads the file, identifies the rows to delete, and removes them from the sheet.
     * @param project The Project object whose registrations are to be deleted.
     * @return void
     * @throws IOException if there is an error reading or writing the file.
     */

    public static void deleteOfficerRegistrationByProjID(Project project) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(REGISTRATION_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {

            Sheet sheet = workbook.getSheetAt(0);
            int projectIdToDelete = project.getProjectID();

            // Collect row indices to delete (skip header)
            ArrayList<Integer> rowsToDelete = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                int rowProjectId = (int) row.getCell(OfficerRegistrationFileIndex.PROJECT.getIndex()).getNumericCellValue();
                if (rowProjectId == projectIdToDelete) {
                    rowsToDelete.add(row.getRowNum());
                }
            }

            // Delete rows in reverse order to avoid shifting issues
            for (int i = rowsToDelete.size() - 1; i >= 0; i--) {
                int rowIndex = rowsToDelete.get(i);
                int lastRowNum = sheet.getLastRowNum();
                if (rowIndex >= 0 && rowIndex <= lastRowNum) {
                    sheet.removeRow(sheet.getRow(rowIndex));
                    // Shift rows up if not the last row
                    if (rowIndex < lastRowNum) {
                        sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
                    }
                }
            }

            // Save changes
            try (FileOutputStream fos = new FileOutputStream(REGISTRATION_FILEPATH)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }

    /**
     * populateRegistrationRow(Row row, int registrationID, HDBOfficer officer, Project project, String status)
     * This method populates a row with officer registration details.
     * It sets the values for ID, NRIC, project ID, status, and date.
     * @param row The Row object to populate.
     * @param registrationID The ID of the registration.
     * @param officer The HDBOfficer object associated with the registration.
     * @param project The Project object associated with the registration.
     * @param status The status of the registration.
     */
    private static void populateRegistrationRow(Row row, int registrationID, HDBOfficer officer, Project project, String status) {
        row.createCell(OfficerRegistrationFileIndex.ID.getIndex()).setCellValue(registrationID); // Using row number as ID
        row.createCell(OfficerRegistrationFileIndex.NRIC.getIndex()).setCellValue(officer.getNric());
        row.createCell(OfficerRegistrationFileIndex.PROJECT.getIndex()).setCellValue(project.getProjectID());
        row.createCell(OfficerRegistrationFileIndex.STATUS.getIndex()).setCellValue(status);
        row.createCell(OfficerRegistrationFileIndex.DATE.getIndex()).setCellValue(new Date());
    }
}