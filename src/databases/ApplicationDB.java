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
    public static void createApplication(Applicant applicant, Project project, String status) throws IOException {
        File file = new File(APPLICATION_FILEPATH);
        Workbook workbook;
        Sheet sheet;
    
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
            }
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Applications");
            createHeaderRow(sheet);
        }
    
        boolean recordUpdated = false;
    
        // Check if a record with the same NRIC exists and update it
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row
    
            String existingNRIC = row.getCell(ProjectApplicationFileIndex.NRIC.getIndex()).getStringCellValue();
            if (existingNRIC.equals(applicant.getNric())) {
                // Update the existing record
                row.getCell(ProjectApplicationFileIndex.PROJECT_ID.getIndex()).setCellValue(project.getProjectID());
                row.getCell(ProjectApplicationFileIndex.STATUS.getIndex()).setCellValue(status);
                row.getCell(ProjectApplicationFileIndex.DATE.getIndex()).setCellValue(new Date().toString());
                recordUpdated = true;
                break;
            }
        }
    
        // If no existing record was found, add a new row
        if (!recordUpdated) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            populateApplicationRow(row, applicant, project, status);
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
                
                int currentProjectID = (int) row.getCell(ProjectApplicationFileIndex.PROJECT_ID.getIndex()).getNumericCellValue();
                if (currentProjectID == projectID) {
                    return true;
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

    private static Application createApplicationFromRow(Row row) throws IOException {
        int applicationID = (int) row.getCell(ProjectApplicationFileIndex.ID.getIndex()).getNumericCellValue();
        String nric = row.getCell(ProjectApplicationFileIndex.NRIC.getIndex()).getStringCellValue();
        int projectID = (int) row.getCell(ProjectApplicationFileIndex.PROJECT_ID.getIndex()).getNumericCellValue();
        String applicationStatus = row.getCell(ProjectApplicationFileIndex.STATUS.getIndex()).getStringCellValue();
    
        // Fetch the Applicant object
        Applicant applicant;
        try {
            applicant = ApplicantDB.getApplicantByNRIC(nric);
            if (applicant == null) {
                throw new IOException("Applicant with NRIC " + nric + " not found.");
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
        return new Application(applicant, project, applicationStatus, applicationID);
    }

    private static void populateApplicationRow(Row row, Applicant applicant, Project project, String status) {
        row.createCell(ProjectApplicationFileIndex.ID.getIndex()).setCellValue(row.getRowNum()); // Using row number as ID
        row.createCell(ProjectApplicationFileIndex.NRIC.getIndex()).setCellValue(applicant.getNric());
        row.createCell(ProjectApplicationFileIndex.PROJECT_ID.getIndex()).setCellValue(project.getProjectID());
        row.createCell(ProjectApplicationFileIndex.STATUS.getIndex()).setCellValue(status);
        row.createCell(ProjectApplicationFileIndex.DATE.getIndex()).setCellValue(new Date());
    }

    private static void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(ProjectApplicationFileIndex.ID.getIndex()).setCellValue("Application ID");
        headerRow.createCell(ProjectApplicationFileIndex.NRIC.getIndex()).setCellValue("Applicant NRIC");
        headerRow.createCell(ProjectApplicationFileIndex.PROJECT_ID.getIndex()).setCellValue("Project ID");
        headerRow.createCell(ProjectApplicationFileIndex.STATUS.getIndex()).setCellValue("Status");
        headerRow.createCell(ProjectApplicationFileIndex.DATE.getIndex()).setCellValue("Application Date");
    }
}