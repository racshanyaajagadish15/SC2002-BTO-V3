package controllers;

//commit

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import databases.ApplicationDB;
import databases.HDBManagerDB;
import models.HDBManager;
import views.ManagerMainView;
import models.Application;
import java.util.List;


/**
 * Controller class for managing the main menu and functionalities of the HDB Manager.
 * This class handles the interactions between the view and the model for the manager's main menu.
 */
public class ManagerMainController {

    public void managerSelectMenu(HDBManager manager) {
        System.out.println("Logged in as Manager: " + manager.getName());
        ManagerMainView view = new ManagerMainView(manager); // Pass the manager to the view
        view.showManagerMenu();
    }

    /**
     * Generates a report of applicants based on the provided filters and saves it to an Excel file.
     * 
     * @param maritalStatusFilter The marital status filter (e.g., "Single", "Married").
     * @param flatTypeFilter The flat type filter (e.g., "2 Room", "3 Room").
     * @param projectNameFilter The project name filter (e.g., "Project A").
     * @param outputFilePath The path where the Excel file will be saved.
     */
    public void generateApplicantReport(String maritalStatusFilter, String flatTypeFilter, String projectNameFilter) {
        try {
            List<Application> allApplications = ApplicationDB.getAllApplications();
    
            List<Application> filteredApplications = allApplications.stream()
                .filter(app -> maritalStatusFilter == null || app.getApplicant().getMaritalStatus().equalsIgnoreCase(maritalStatusFilter))
                .filter(app -> flatTypeFilter == null || app.getFlatType().equalsIgnoreCase(flatTypeFilter))
                .filter(app -> projectNameFilter == null || app.getProject().getProjectName().equalsIgnoreCase(projectNameFilter))
                .collect(Collectors.toList());
    
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Applicant Report");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Applicant Name");
            headerRow.createCell(1).setCellValue("Age");
            headerRow.createCell(2).setCellValue("Marital Status");
            headerRow.createCell(3).setCellValue("Project Name");
            headerRow.createCell(4).setCellValue("Flat Type");
    
            int rowIndex = 1;
            for (Application app : filteredApplications) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(app.getApplicant().getName());
                row.createCell(1).setCellValue(app.getApplicant().getAge());
                row.createCell(2).setCellValue(app.getApplicant().getMaritalStatus());
                row.createCell(3).setCellValue(app.getProject().getProjectName());
                row.createCell(4).setCellValue(app.getFlatType());
            }
    
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
    
            // Create the folder if it doesn't exist
            String folderPath = "generated_files";
            java.io.File folder = new java.io.File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
    
            // Generate the file name with a timestamp
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String outputFilePath = folderPath + "/ApplicantReport_" + timestamp + ".xlsx";
    
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                workbook.write(fos);
            }
    
            workbook.close();
    
            System.out.println("[SUCCESS] Report generated successfully: " + outputFilePath);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to generate report: " + e.getMessage());
        }
    }

    /**
     * Change the password of the HDB Manager.
     * 
     * @param manager The HDB Manager whose password is to be changed.
     * @param newPassword The new password to set.
     */
    
    public void changePassword(HDBManager manager, String newPassword) {
    try {
        // Update the password in memory
        manager.setPassword(newPassword);

        // Update the password in the Excel file
        HDBManagerDB.updateManagerPassword(manager.getNric(), newPassword);

        System.out.println("[SUCCESS] Password changed successfully.");
    } catch (IOException e) {
        System.out.println("[ERROR] Failed to update password in the database: " + e.getMessage());
    } catch (Exception e) {
        System.out.println("[ERROR] Failed to change password: " + e.getMessage());
    }
}
}
