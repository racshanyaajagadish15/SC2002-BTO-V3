package databases;

import models.*;
import enums.ProjectListFileIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ProjectDB {
    private static final String PROJECT_FILEPATH = "resources/data/ProjectList.xlsx";

    // Get a project by ID
    public static Project getProjectByName(String projectName) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Cell nameCell = row.getCell(ProjectListFileIndex.NAME.getIndex());
                if (nameCell != null && nameCell.getStringCellValue().equalsIgnoreCase(projectName)) {
                    return createProjectFromRow(row);
                }
            }
        }
        return null;
    }

    public static ArrayList<Project> getAllProjects() throws IOException {
        ArrayList<Project> projects = new ArrayList<>();
        
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; 
                Project project = createProjectFromRow(row);
                if (project != null) {
                    projects.add(project);
                }
            }
        }
        return projects;
    }

    public static ArrayList<Project> getProjectsByManager(String managerName) throws IOException {
        ArrayList<Project> projects = new ArrayList<>();
    
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
    
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
    
                Cell managerCell = row.getCell(ProjectListFileIndex.MANAGER.getIndex());
                if (managerCell != null && managerCell.getStringCellValue().equalsIgnoreCase(managerName)) {
                    Project project = createProjectFromRow(row);
                    if (project != null) {
                        projects.add(project);
                    }
                }
            }
        }
        return projects;
    }

    public static boolean createProject(Project project) throws IOException {
        File file = new File(PROJECT_FILEPATH);
        Workbook workbook;
        Sheet sheet;
    
        if (file.exists()) {
            try (FileInputStream fileStreamIn = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fileStreamIn);
                sheet = workbook.getSheetAt(0);
            }
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Projects");
            createHeaderRow(sheet);
        }
    
        // Check if project already exists
        if (getProjectByName(project.getProjectName()) != null) {
            return false;
        }
    
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        populateProjectRow(row, project);
    
        try (FileOutputStream fileOut = new FileOutputStream(PROJECT_FILEPATH)) {
            workbook.write(fileOut);
        }
        workbook.close();
        return true;
    }

    public static boolean updateProject(Project project) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
    
            Sheet sheet = workbook.getSheetAt(0);
            boolean projectFound = false;
    
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
    
                Cell nameCell = row.getCell(ProjectListFileIndex.NAME.getIndex());
                if (nameCell != null && nameCell.getStringCellValue().equalsIgnoreCase(project.getProjectName())) {
                    populateProjectRow(row, project);
                    projectFound = true;
                    break;
                }
            }
    
            if (!projectFound) {
                return false;
            }
    
            try (FileOutputStream fileOut = new FileOutputStream(PROJECT_FILEPATH)) {
                workbook.write(fileOut);
            }
            return true;
        }
    }

    public static boolean deleteProject(String projectName) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            int rowToDelete = -1;
    
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
    
                Cell nameCell = row.getCell(ProjectListFileIndex.NAME.getIndex());
                if (nameCell != null && nameCell.getStringCellValue().equalsIgnoreCase(projectName)) {
                    rowToDelete = row.getRowNum();
                    break;
                }
            }
    
            if (rowToDelete != -1) {
                sheet.removeRow(sheet.getRow(rowToDelete));
                if (rowToDelete < sheet.getLastRowNum()) {
                    sheet.shiftRows(rowToDelete + 1, sheet.getLastRowNum(), -1);
                }
    
                try (FileOutputStream fileOut = new FileOutputStream(PROJECT_FILEPATH)) {
                    workbook.write(fileOut);
                }
                return true;
            }
            return false;
        }
    }

    private static Project createProjectFromRow(Row row) throws IOException {
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
            HDBManager manager = new HDBManager(managerName, "", ""); // Create minimal manager object
    
            int officerSlots = (int) row.getCell(ProjectListFileIndex.OFFICER_SLOT.getIndex()).getNumericCellValue();
    
            // Handle visibility
            Cell visibilityCell = row.getCell(ProjectListFileIndex.VISIBILITY.getIndex());
            boolean visibility = true; // default
            if (visibilityCell != null) {
                String visibilityStr = visibilityCell.getStringCellValue();
                visibility = visibilityStr.equalsIgnoreCase("Visible");
            }
    
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

    private static void populateProjectRow(Row row, Project project) {
        // Clear existing cells
        for (int i = 0; i < ProjectListFileIndex.values().length; i++) {
            row.createCell(i);
        }

        row.getCell(ProjectListFileIndex.NAME.getIndex()).setCellValue(project.getProjectName());
        row.getCell(ProjectListFileIndex.NEIGHBORHOOD.getIndex()).setCellValue(project.getNeighborhood());
    
        // Flat Type 1
        FlatType type1 = project.getFlatTypes().get(0);
        row.getCell(ProjectListFileIndex.TYPE_1.getIndex()).setCellValue(type1.getFlatType());
        row.getCell(ProjectListFileIndex.TYPE_1_UNITS.getIndex()).setCellValue(type1.getNumFlats());
        row.getCell(ProjectListFileIndex.TYPE_1_PRICE.getIndex()).setCellValue(type1.getPricePerFlat());
    
        // Flat Type 2 if exists
        if (project.getFlatTypes().size() > 1) {
            FlatType type2 = project.getFlatTypes().get(1);
            row.getCell(ProjectListFileIndex.TYPE_2.getIndex()).setCellValue(type2.getFlatType());
            row.getCell(ProjectListFileIndex.TYPE_2_UNITS.getIndex()).setCellValue(type2.getNumFlats());
            row.getCell(ProjectListFileIndex.TYPE_2_PRICE.getIndex()).setCellValue(type2.getPricePerFlat());
        }
    
        // Dates
        row.getCell(ProjectListFileIndex.OPENING_DATE.getIndex()).setCellValue(project.getApplicationOpeningDate());
        row.getCell(ProjectListFileIndex.CLOSING_DATE.getIndex()).setCellValue(project.getApplicationClosingDate());
    
        // Manager (store name instead of NRIC)
        row.getCell(ProjectListFileIndex.MANAGER.getIndex()).setCellValue(
            project.getProjectManager() != null ? project.getProjectManager().getName() : "N/A");
    
        // Other fields
        row.getCell(ProjectListFileIndex.OFFICER_SLOT.getIndex()).setCellValue(project.getOfficerSlots());
        row.getCell(ProjectListFileIndex.VISIBILITY.getIndex()).setCellValue(
            project.getProjectVisibility() ? "Visible" : "Hidden");
    }
    // Helper method to create header row
    private static void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(ProjectListFileIndex.NAME.getIndex()).setCellValue("Project Name");
        headerRow.createCell(ProjectListFileIndex.NEIGHBORHOOD.getIndex()).setCellValue("Neighborhood");
        headerRow.createCell(ProjectListFileIndex.TYPE_1.getIndex()).setCellValue("Type 1");
        headerRow.createCell(ProjectListFileIndex.TYPE_1_UNITS.getIndex()).setCellValue("Number of units for Type 1");
        headerRow.createCell(ProjectListFileIndex.TYPE_1_PRICE.getIndex()).setCellValue("Selling price for Type 1");
        headerRow.createCell(ProjectListFileIndex.TYPE_2.getIndex()).setCellValue("Type 2");
        headerRow.createCell(ProjectListFileIndex.TYPE_2_UNITS.getIndex()).setCellValue("Number of units for Type 2");
        headerRow.createCell(ProjectListFileIndex.TYPE_2_PRICE.getIndex()).setCellValue("Selling price for Type 2");
        headerRow.createCell(ProjectListFileIndex.OPENING_DATE.getIndex()).setCellValue("Application opening date");
        headerRow.createCell(ProjectListFileIndex.CLOSING_DATE.getIndex()).setCellValue("Application closing date");
        headerRow.createCell(ProjectListFileIndex.MANAGER.getIndex()).setCellValue("Manager");
        headerRow.createCell(ProjectListFileIndex.OFFICER_SLOT.getIndex()).setCellValue("Officer Slot");
        headerRow.createCell(ProjectListFileIndex.OFFICER.getIndex()).setCellValue("Officer");
        headerRow.createCell(ProjectListFileIndex.VISIBILITY.getIndex()).setCellValue("Visibility"); // Add visibility header
    }
}