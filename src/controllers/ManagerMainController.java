package controllers;

//commit

import java.io.FileOutputStream;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import databases.ApplicationDB;
import models.HDBManager;
import views.ManagerMainView;
import models.Application;
import java.util.List;


public class ManagerMainController {

    public void managerSelectMenu(HDBManager manager) {
        System.out.println("Logged in as Manager: " + manager.getName());
        ManagerMainView view = new ManagerMainView(manager); // Pass the manager to the view
        view.showManagerMenu();
    }

    public void generateApplicantReport(String maritalStatusFilter, String flatTypeFilter, String projectNameFilter, String outputFilePath) {
        try {
            // Retrieve all applications
            List<Application> allApplications = ApplicationDB.getAllApplications();

            // Filter applications based on the provided filters
            List<Application> filteredApplications = allApplications.stream()
                .filter(app -> maritalStatusFilter == null || app.getApplicant().getMaritalStatus().equalsIgnoreCase(maritalStatusFilter))
                .filter(app -> flatTypeFilter == null || app.getFlatType().equalsIgnoreCase(flatTypeFilter))
                .filter(app -> projectNameFilter == null || app.getProject().getProjectName().equalsIgnoreCase(projectNameFilter))
                .collect(Collectors.toList());

            // Create a new Excel workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Applicant Report");

            // Create the header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Applicant Name");
            headerRow.createCell(1).setCellValue("Age");
            headerRow.createCell(2).setCellValue("Marital Status");
            headerRow.createCell(3).setCellValue("Project Name");
            headerRow.createCell(4).setCellValue("Flat Type");

            // Populate the sheet with filtered applications
            int rowIndex = 1;
            for (Application app : filteredApplications) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(app.getApplicant().getName());
                row.createCell(1).setCellValue(app.getApplicant().getAge());
                row.createCell(2).setCellValue(app.getApplicant().getMaritalStatus());
                row.createCell(3).setCellValue(app.getProject().getProjectName());
                row.createCell(4).setCellValue(app.getFlatType());
            }

            // Auto-size the columns for better readability
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the workbook to the specified file
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                workbook.write(fos);
            }

            // Close the workbook
            workbook.close();

            System.out.println("[SUCCESS] Report generated successfully: " + outputFilePath);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to generate report: " + e.getMessage());
        }
    }
    
}
