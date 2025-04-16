package databases;

import models.*;
import enums.ProjectListFileIndex;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ProjectDB {
    private static final String PROJECT_FILEPATH = "resources/data/ProjectList.xlsx";

    // Helper function to create Project object from excel row
    private static Project createProjectFromRow(Row row) {
        try {
            String projectName = row.getCell(ProjectListFileIndex.NAME.getIndex()).getStringCellValue();
            String neighborhood = row.getCell(ProjectListFileIndex.NEIGHBORHOOD.getIndex()).getStringCellValue();
    
            // Flat Type 1
            String type1Name = row.getCell(ProjectListFileIndex.TYPE_1.getIndex()).getStringCellValue().trim();
            int type1Units = (int) row.getCell(ProjectListFileIndex.TYPE_1_UNITS.getIndex()).getNumericCellValue();
            double type1Price = row.getCell(ProjectListFileIndex.TYPE_1_PRICE.getIndex()).getNumericCellValue();
            FlatType type1 = new FlatType(type1Name, type1Units, type1Price);
    
            ArrayList<FlatType> flatTypes = new ArrayList<>();
            flatTypes.add(type1);
    
            // Flat Type 2 if exists
            Cell type2Cell = row.getCell(ProjectListFileIndex.TYPE_2.getIndex());
            if (type2Cell != null && !type2Cell.getStringCellValue().isBlank()) {
                String type2Name = type2Cell.getStringCellValue().trim();
                int type2Units = (int) row.getCell(ProjectListFileIndex.TYPE_2_UNITS.getIndex()).getNumericCellValue();
                double type2Price = row.getCell(ProjectListFileIndex.TYPE_2_PRICE.getIndex()).getNumericCellValue();
                flatTypes.add(new FlatType(type2Name, type2Units, type2Price));
            }
    
            Date openingDate = row.getCell(ProjectListFileIndex.OPENING_DATE.getIndex()).getDateCellValue();
            Date closingDate = row.getCell(ProjectListFileIndex.CLOSING_DATE.getIndex()).getDateCellValue();
    
            String managerName = row.getCell(ProjectListFileIndex.MANAGER.getIndex()).getStringCellValue();
    
            // Adapt to the existing HDBManager constructor
            HDBManager manager = new HDBManager(
                managerName,          // Name
                "N/A",                // NRIC (placeholder)
                0,                    // Age (default value)
                "Unknown",            // Marital Status (placeholder)
                "defaultPassword"     // Password (placeholder)
            );
    
            int officerSlots = (int) row.getCell(ProjectListFileIndex.OFFICER_SLOT.getIndex()).getNumericCellValue();
    
            // Handle visibility
            Cell visibilityCell = row.getCell(ProjectListFileIndex.VISIBILITY.getIndex());
            boolean visibility = visibilityCell != null && visibilityCell.getStringCellValue().equalsIgnoreCase("Visible");
    
            return new Project(
                row.getRowNum(), // Use row number as temporary ID
                projectName,
                manager,
                neighborhood,
                flatTypes,
                openingDate,
                closingDate,
                officerSlots,
                visibility
            );
        } catch (Exception e) {
            System.err.println("Error creating project from row: " + e.getMessage());
            return null;
        }
    }

    // Helper function to populate excel row from Project object
    private static void populateProjectRow(Row row, Project project) {
        row.createCell(ProjectListFileIndex.NAME.getIndex()).setCellValue(project.getProjectName());
        row.createCell(ProjectListFileIndex.NEIGHBORHOOD.getIndex()).setCellValue(project.getNeighborhood());

        // Flat Type 1
        FlatType type1 = project.getFlatTypes().get(0);
        row.createCell(ProjectListFileIndex.TYPE_1.getIndex()).setCellValue(type1.getFlatType());
        row.createCell(ProjectListFileIndex.TYPE_1_UNITS.getIndex()).setCellValue(type1.getNumFlats());
        row.createCell(ProjectListFileIndex.TYPE_1_PRICE.getIndex()).setCellValue(type1.getPricePerFlat());

        // Flat Type 2 if exists
        if (project.getFlatTypes().size() > 1) {
            FlatType type2 = project.getFlatTypes().get(1);
            row.createCell(ProjectListFileIndex.TYPE_2.getIndex()).setCellValue(type2.getFlatType());
            row.createCell(ProjectListFileIndex.TYPE_2_UNITS.getIndex()).setCellValue(type2.getNumFlats());
            row.createCell(ProjectListFileIndex.TYPE_2_PRICE.getIndex()).setCellValue(type2.getPricePerFlat());
        }

        // Dates
        row.createCell(ProjectListFileIndex.OPENING_DATE.getIndex()).setCellValue(project.getApplicationOpeningDate());
        row.createCell(ProjectListFileIndex.CLOSING_DATE.getIndex()).setCellValue(project.getApplicationClosingDate());

        // Manager (store name instead of NRIC)
        row.createCell(ProjectListFileIndex.MANAGER.getIndex()).setCellValue(
            project.getProjectManager() != null ? project.getProjectManager().getName() : "N/A");

        // Other fields
        row.createCell(ProjectListFileIndex.OFFICER_SLOT.getIndex()).setCellValue(project.getOfficerSlots());
        row.createCell(ProjectListFileIndex.VISIBILITY.getIndex()).setCellValue(
            project.getProjectVisibility() ? "Visible" : "Hidden");
    }

    // Create Project
    public static boolean createProject(Project project) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Check if project already exists
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (row.getCell(ProjectListFileIndex.NAME.getIndex()).getStringCellValue().equalsIgnoreCase(project.getProjectName())) {
                    return false; // Project already exists
                }
            }

            Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
            populateProjectRow(newRow, project);

            try (FileOutputStream fileOut = new FileOutputStream(PROJECT_FILEPATH)) {
                workbook.write(fileOut);
            }
            return true;
        }
    }

    // Get all Projects
    public static ArrayList<Project> getAllProjects() throws IOException {
        ArrayList<Project> projects = new ArrayList<>();
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                Project project = createProjectFromRow(row);
                if (project != null) {
                    projects.add(project);
                }
            }
        }
        return projects;
    }

    // Get Project by Name
    public static Project getProjectByName(String projectName) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (row.getCell(ProjectListFileIndex.NAME.getIndex()).getStringCellValue().equalsIgnoreCase(projectName)) {
                    return createProjectFromRow(row);
                }
            }
        }
        return null;
    }

    // Update Project
    public static boolean updateProject(Project project) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (row.getCell(ProjectListFileIndex.NAME.getIndex()).getStringCellValue().equalsIgnoreCase(project.getProjectName())) {
                    populateProjectRow(row, project);

                    try (FileOutputStream fileOut = new FileOutputStream(PROJECT_FILEPATH)) {
                        workbook.write(fileOut);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // Delete Project
    public static boolean deleteProject(String projectName) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (row.getCell(ProjectListFileIndex.NAME.getIndex()).getStringCellValue().equalsIgnoreCase(projectName)) {
                    int rowToDelete = row.getRowNum();
                    sheet.removeRow(row);
                    if (rowToDelete < sheet.getLastRowNum()) {
                        sheet.shiftRows(rowToDelete + 1, sheet.getLastRowNum(), -1);
                    }

                    try (FileOutputStream fileOut = new FileOutputStream(PROJECT_FILEPATH)) {
                        workbook.write(fileOut);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static ArrayList<Project> getProjectsByManager(String hdbManagerID) throws IOException {
        ArrayList<Project> allProjects = getAllProjects();
        ArrayList<Project> filteredProjects = new ArrayList<>();
        for (Project project : allProjects) {
            if (project.getProjectManager().getNric() == hdbManagerID) {
                filteredProjects.add(project);
            }
        }
        return filteredProjects;
    }
}