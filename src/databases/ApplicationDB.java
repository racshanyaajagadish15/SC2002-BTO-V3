package databases;

import models.*;
import enums.ProjectApplicationFileIndex;
import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ApplicationDB {
    private static final String APPLICATION_FILEPATH = "resources/data/ProjectApplication.xlsx";

    // Create a new application
    public static void createApplication(Applicant applicant, Project project, String status, FlatType flatType) throws IOException {
        File file = new File(APPLICATION_FILEPATH);
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            Row lastRow = sheet.getRow(sheet.getLastRowNum());
            int applicationID = (lastRow != null && lastRow.getCell(ProjectApplicationFileIndex.ID.getIndex()) != null) 
                ? (int) lastRow.getCell(ProjectApplicationFileIndex.ID.getIndex()).getNumericCellValue() 
                : 1; // Default to 1 if no rows exist
            populateApplicationRow(row, applicationID, applicant, project, status, flatType.getFlatType());

            // Save the changes to the file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    // Create a new application
    public static void updateApplication(Application application) throws IOException {
        File file = new File(APPLICATION_FILEPATH);
        Workbook workbook;
        Sheet sheet;
        try (FileInputStream fis = new FileInputStream(file)) {
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheetAt(0);
        }
            
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row
            
            int applicationID = (int) row.getCell(ProjectApplicationFileIndex.ID.getIndex()).getNumericCellValue();
            if (applicationID == application.getApplicationID()) {
                populateApplicationRow(
                    row, 
                    applicationID, 
                    application.getApplicant(),
                    application.getProject(), 
                    application.getApplicationStatus(), 
                    application.getFlatType());
                break;
            }
        }
        // Save the changes to the file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    // Check if a project has applications
    public static boolean hasApplicationsForProject(int projectID) throws IOException {
        try (FileInputStream fis = new FileInputStream(APPLICATION_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                
                Cell projectIDCell = row.getCell(ProjectApplicationFileIndex.PROJECT_ID.getIndex());
                if (projectIDCell != null && projectIDCell.getCellType() == CellType.NUMERIC) {
                    int currentProjectID = (int) projectIDCell.getNumericCellValue();
                    if (currentProjectID == projectID) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Get all applications for a project
    public static List<Application> getApplicationsForProject(int projectID) throws IOException {
        List<Application> applications = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(APPLICATION_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fis)) {
    
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
    
                Cell projectIDCell = row.getCell(ProjectApplicationFileIndex.PROJECT_ID.getIndex());
                int currentProjectID;
    
                // Check the cell type and retrieve the value accordingly
                if (projectIDCell.getCellType() == CellType.NUMERIC) {
                    currentProjectID = (int) projectIDCell.getNumericCellValue();
                } else if (projectIDCell.getCellType() == CellType.STRING) {
                    currentProjectID = Integer.parseInt(projectIDCell.getStringCellValue());
                } else {
                    throw new IllegalStateException("Unexpected cell type for Project ID: " + projectIDCell.getCellType());
                }
    
                if (currentProjectID == projectID) {
                    applications.add(createApplicationFromRow(row));
                }
            }
        }
        return applications;
    }

    public static Application getApplicationByNric(String nric) throws IOException {
        try (FileInputStream fis = new FileInputStream(APPLICATION_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Cell nricCell = row.getCell(ProjectApplicationFileIndex.NRIC.getIndex());
                if (nricCell == null) continue;
                if (nricCell.getCellType() == CellType.STRING && nricCell.getStringCellValue().equals(nric)) {
                    return createApplicationFromRow(row);
                }
            }
        }
        return null;
    }

    private static Application createApplicationFromRow(Row row) throws IOException {
        int applicationID = (int) row.getCell(ProjectApplicationFileIndex.ID.getIndex()).getNumericCellValue();
        String nric = row.getCell(ProjectApplicationFileIndex.NRIC.getIndex()).getStringCellValue();
        int projectID = (int) row.getCell(ProjectApplicationFileIndex.PROJECT_ID.getIndex()).getNumericCellValue();
        String applicationStatus = row.getCell(ProjectApplicationFileIndex.STATUS.getIndex()).getStringCellValue();
        String flatType = row.getCell(ProjectApplicationFileIndex.FLAT_TYPE.getIndex()).getStringCellValue();

        // Fetch the Applicant object
        Applicant applicant;
        try {
            applicant = ApplicantDB.getApplicantByNRIC(nric);
            if (applicant == null) {
                applicant = HDBOfficerDB.getOfficerByNRIC(nric);
                if (applicant == null) {
                    throw new IOException("Applicant with NRIC " + nric + " not found.");
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed to fetch applicant by NRIC: " + nric, e);
        }
    
        // Fetch the Project object
        Project project;
        try {
            project = ProjectDB.getProjectByID(projectID);
            if (project == null) {
                throw new IOException("Project with ID " + projectID + " not found.");
            }
        } catch (IOException e) {
            throw new IOException("Failed to fetch project by ID: " + projectID, e);
        }
    
        // Create and return the Application object
        return new Application(applicant, project, applicationStatus, applicationID, flatType);
    }

    private static void populateApplicationRow(Row row, int applicationID,Applicant applicant, Project project, String status, String flatType) {
        row.createCell(ProjectApplicationFileIndex.ID.getIndex()).setCellValue(applicationID); // Using row number as ID
        row.createCell(ProjectApplicationFileIndex.NRIC.getIndex()).setCellValue(applicant.getNric());
        row.createCell(ProjectApplicationFileIndex.PROJECT_ID.getIndex()).setCellValue(project.getProjectID());
        row.createCell(ProjectApplicationFileIndex.STATUS.getIndex()).setCellValue(status);
        row.createCell(ProjectApplicationFileIndex.FLAT_TYPE.getIndex()).setCellValue(flatType);
        row.createCell(ProjectApplicationFileIndex.DATE.getIndex()).setCellValue(new Date());
    }
}