package databases;

import models.*;
import utilities.LoggerUtility;
import enums.EnquiryFileIndex;
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
            Row lastRow = sheet.getRow(sheet.getLastRowNum());
            int applicationID;
            if (lastRow == null || lastRow.getCell(ProjectApplicationFileIndex.ID.getIndex()) == null) {
                applicationID = 1; // Start with ID 1 if no valid last row exists
            } else {
                applicationID  = (int) lastRow.getCell(ProjectApplicationFileIndex.ID.getIndex()).getNumericCellValue() + 1;
            }
            int newRowNum = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(newRowNum);
            populateApplicationRow(row, applicationID, applicant, project, status, flatType.getFlatType());

            // Save the changes to the file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            LoggerUtility.logInfo(String.format("Created application for NRIC: %s, Project: %s", 
                applicant.getNric(), project.getProjectName()));
        } catch (IOException e) {
            LoggerUtility.logError("Failed to create application for NRIC: " + applicant.getNric(), e);
            throw e;
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
            if (row.getCell(ProjectApplicationFileIndex.ID.getIndex()) == null || row.getCell(ProjectApplicationFileIndex.ID.getIndex()).getCellType() == CellType.BLANK) continue;
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
                if (projectIDCell == null || projectIDCell.getCellType() == CellType.BLANK) continue;

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
                if (projectIDCell == null || projectIDCell.getCellType() == CellType.BLANK) continue;
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
                if (nricCell == null || nricCell.getCellType() == CellType.BLANK) continue;

                if (nricCell == null) continue;
                if (nricCell.getCellType() == CellType.STRING && nricCell.getStringCellValue().equals(nric)) {
                    return createApplicationFromRow(row);
                }
            }
        }
        return null;
    }

    private static Application createApplicationFromRow(Row row) throws IOException {
        try {
            Cell idCell = row.getCell(ProjectApplicationFileIndex.ID.getIndex());
            int applicationID = (idCell != null && idCell.getCellType() == CellType.NUMERIC) 
                ? (int) idCell.getNumericCellValue() 
                : 0;

            Cell nricCell = row.getCell(ProjectApplicationFileIndex.NRIC.getIndex());
            String nric = (nricCell != null && nricCell.getCellType() == CellType.STRING) 
                ? nricCell.getStringCellValue() 
                : "";

            Cell projectIDCell = row.getCell(ProjectApplicationFileIndex.PROJECT_ID.getIndex());
            int projectID = (projectIDCell != null && projectIDCell.getCellType() == CellType.NUMERIC) 
                ? (int) projectIDCell.getNumericCellValue() 
                : 0;

            Cell statusCell = row.getCell(ProjectApplicationFileIndex.STATUS.getIndex());
            String applicationStatus = (statusCell != null && statusCell.getCellType() == CellType.STRING) 
                ? statusCell.getStringCellValue() 
                : "";

            Cell flatTypeCell = row.getCell(ProjectApplicationFileIndex.FLAT_TYPE.getIndex());
            String flatType = (flatTypeCell != null && flatTypeCell.getCellType() == CellType.STRING) 
                ? flatTypeCell.getStringCellValue() 
                : "";

            // Fetch the Applicant object
            Applicant applicant;
            try {
                // Check for null or empty NRIC
                if (nric == null || nric.trim().isEmpty()) {
                    throw new IOException("Applicant NRIC is missing in application row.");
                }
                applicant = ApplicantDB.getApplicantByNRIC(nric);
                if (applicant == null) {
                    Object officer = HDBOfficerDB.getOfficerByNRIC(nric);
                    if (officer instanceof Applicant) {
                        applicant = (Applicant) officer;
                    } else {
                        throw new IOException("Applicant with NRIC " + nric + " not found.");
                    }
                }
            } catch (IOException e) {
                LoggerUtility.logError("Failed to fetch applicant by NRIC: " + nric, e);
                throw e;
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
        } catch (IOException e) {
            LoggerUtility.logError("Failed to create application from row: " + row.getRowNum(), e);
            throw e;
        }
    }

    public static List<Application> getAllApplications() throws IOException {
        List<Application> applications = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(APPLICATION_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fis)) {
    
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
    
                try {
                    // Create an Application object from the row and add it to the list
                    applications.add(createApplicationFromRow(row));
                } catch (Exception e) {
                    LoggerUtility.logError("Failed to process row: " + row.getRowNum(), e);
                }
            }
        }
        return applications;
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