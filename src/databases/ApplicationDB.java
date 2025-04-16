package databases;

import models.*;
import enums.ApplicationFileIndex;
import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ApplicationDB {
    private static final String APPLICATION_FILEPATH = "resources/data/ApplicationList.xlsx";

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

        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        populateApplicationRow(row, applicant, project, status);

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
                
                int currentProjectID = (int) row.getCell(ApplicationFileIndex.PROJECT_ID.getIndex()).getNumericCellValue();
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
                
                int currentProjectID = (int) row.getCell(ApplicationFileIndex.PROJECT_ID.getIndex()).getNumericCellValue();
                if (currentProjectID == projectID) {
                    applications.add(createApplicationFromRow(row));
                }
            }
        }
        return applications;
    }

    private static Application createApplicationFromRow(Row row) throws IOException {
        int applicationID = (int) row.getCell(ApplicationFileIndex.ID.getIndex()).getNumericCellValue();
        String nric = row.getCell(ApplicationFileIndex.NRIC.getIndex()).getStringCellValue();
        int projectID = (int) row.getCell(ApplicationFileIndex.PROJECT_ID.getIndex()).getNumericCellValue();
        String applicationStatus = row.getCell(ApplicationFileIndex.STATUS.getIndex()).getStringCellValue();        
        Applicant applicant;
        try {
            applicant = ApplicantDB.getApplicantByNRIC(nric);
        } catch (IOException e) {
            throw new IOException("Failed to fetch applicant by NRIC: " + nric, e);
        }
        Project project;
        try {
            project = ProjectDB.getProjectByID(projectID);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch project by ID: " + projectID, e);
        }
        
        return new Application(applicant, project, applicationStatus, applicationID);
    }

    private static void populateApplicationRow(Row row, Applicant applicant, Project project, String status) {
        row.createCell(ApplicationFileIndex.ID.getIndex()).setCellValue(row.getRowNum()); // Using row number as ID
        row.createCell(ApplicationFileIndex.NRIC.getIndex()).setCellValue(applicant.getNric());
        row.createCell(ApplicationFileIndex.PROJECT_ID.getIndex()).setCellValue(project.getProjectID());
        row.createCell(ApplicationFileIndex.STATUS.getIndex()).setCellValue(status);
        row.createCell(ApplicationFileIndex.DATE.getIndex()).setCellValue(new Date());
    }

    private static void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(ApplicationFileIndex.ID.getIndex()).setCellValue("Application ID");
        headerRow.createCell(ApplicationFileIndex.NRIC.getIndex()).setCellValue("Applicant NRIC");
        headerRow.createCell(ApplicationFileIndex.PROJECT_ID.getIndex()).setCellValue("Project ID");
        headerRow.createCell(ApplicationFileIndex.STATUS.getIndex()).setCellValue("Status");
        headerRow.createCell(ApplicationFileIndex.DATE.getIndex()).setCellValue("Application Date");
    }
}