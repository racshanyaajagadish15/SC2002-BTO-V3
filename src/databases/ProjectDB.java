package databases;

import models.*;
import utilities.LoggerUtility;
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
            // Extract PROJECT_ID
            Cell projectIDCell = row.getCell(ProjectListFileIndex.PROJECT_ID.getIndex());
            if (projectIDCell == null || projectIDCell.getCellType() != CellType.NUMERIC) {
                throw new IllegalArgumentException("Missing or invalid PROJECT_ID in row " + row.getRowNum());
            }
            int projectID = (int) projectIDCell.getNumericCellValue();
    
            String projectName = getStringCellValue(row, ProjectListFileIndex.NAME.getIndex());
            String neighborhood = getStringCellValue(row, ProjectListFileIndex.NEIGHBORHOOD.getIndex());
    
            // Flat Type 1
            String type1Name = getStringCellValue(row, ProjectListFileIndex.TYPE_1.getIndex());
            int type1Units = getNumericCellValue(row, ProjectListFileIndex.TYPE_1_UNITS.getIndex());
            double type1Price = getNumericCellValue(row, ProjectListFileIndex.TYPE_1_PRICE.getIndex());
            FlatType type1 = new FlatType(type1Name, type1Units, type1Price);
    
            ArrayList<FlatType> flatTypes = new ArrayList<>();
            flatTypes.add(type1);
    
            // Flat Type 2 if exists
            Cell type2Cell = row.getCell(ProjectListFileIndex.TYPE_2.getIndex());
            if (type2Cell != null && !type2Cell.getStringCellValue().isBlank()) {
                String type2Name = type2Cell.getStringCellValue().trim();
                int type2Units = getNumericCellValue(row, ProjectListFileIndex.TYPE_2_UNITS.getIndex());
                double type2Price = getNumericCellValue(row, ProjectListFileIndex.TYPE_2_PRICE.getIndex());
                flatTypes.add(new FlatType(type2Name, type2Units, type2Price));
            }
    
            Date openingDate = getDateCellValue(row, ProjectListFileIndex.OPENING_DATE.getIndex());
            Date closingDate = getDateCellValue(row, ProjectListFileIndex.CLOSING_DATE.getIndex());
    
            String managerNRIC = getStringCellValue(row, ProjectListFileIndex.MANAGER.getIndex());
            HDBManager manager = (HDBManager) HDBManager.findUserByNricDB(managerNRIC);
    
            int officerSlots = getNumericCellValue(row, ProjectListFileIndex.OFFICER_SLOT.getIndex());
    
            // Handle visibility
            boolean visibility = "Visible".equalsIgnoreCase(getStringCellValue(row, ProjectListFileIndex.VISIBILITY.getIndex()));
    
            return new Project(
                projectID,
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
    
    // Helper methods to handle null or missing cells
    private static String getStringCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        return (cell != null && cell.getCellType() == CellType.STRING) ? cell.getStringCellValue().trim() : "";
    }
    
    private static int getNumericCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        return (cell != null && cell.getCellType() == CellType.NUMERIC) ? (int) cell.getNumericCellValue() : 0;
    }
    
    private static Date getDateCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        return (cell != null && cell.getCellType() == CellType.NUMERIC) ? cell.getDateCellValue() : null;
    }

    // Helper function to populate excel row from Project object
    private static void populateProjectRow(Row row, Project project) {
        // Populate PROJECT_ID
        row.createCell(ProjectListFileIndex.PROJECT_ID.getIndex()).setCellValue(project.getProjectID());

        // Populate other fields
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
                if (row.getRowNum() == 0) continue; // Skip header row
                Cell nameCell = row.getCell(ProjectListFileIndex.NAME.getIndex());
                if (nameCell != null && nameCell.getCellType() == CellType.STRING &&
                    nameCell.getStringCellValue().equalsIgnoreCase(project.getProjectName())) {
                    return false; // Project already exists
                }
            }
    
            // Find the next empty row
            int newRowNum = sheet.getLastRowNum() + 1;
            while (newRowNum >= 0 && isRowEmpty(sheet.getRow(newRowNum))) {
                newRowNum--;
            }
            newRowNum++; // Move to the next row after the last non-empty row
    
            // Increment the PROJECT_ID
            int projectID = 1; // Default to 1 if no rows exist
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                Cell idCell = row.getCell(ProjectListFileIndex.PROJECT_ID.getIndex());
                if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                    projectID = Math.max(projectID, (int) idCell.getNumericCellValue() + 1);
                }
            }
    
            // Assign the new PROJECT_ID to the project
            project.setProjectID(projectID);
    
            // Create a new row and populate it
            Row row = sheet.createRow(newRowNum);
            populateProjectRow(row, project);
    
            // Save the changes to the file
            try (FileOutputStream fileOut = new FileOutputStream(PROJECT_FILEPATH)) {
                workbook.write(fileOut);
            }
            LoggerUtility.logInfo("Created new project: " + project.getProjectName());
            return true;
        } catch (IOException e) {
            LoggerUtility.logError("Failed to create project: " + project.getProjectName(), e);
            throw e;
        }
    }

    // Get all Projects
    public static ArrayList<Project> getAllProjects() throws IOException {
        ArrayList<Project> projects = new ArrayList<>();
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
    
                // Check if the row is empty
                if (isRowEmpty(row)) break;
    
                try {
                    Project project = createProjectFromRow(row);
                    if (project != null) {
                        projects.add(project);
                    }
                } catch (Exception e) {
                    LoggerUtility.logError("Failed to create project from row " + row.getRowNum(), e);
                }
            }
        } catch (IOException e) {
            LoggerUtility.logError("Failed to read projects from file", e);
            throw e;
        }
        return projects;
    }
    
    // Helper method to check if a row is empty
    private static boolean isRowEmpty(Row row) {
        if (row == null) return true; // If the row is null, consider it empty
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false; // Row is not empty if any cell is not blank
            }
        }
        return true; // Row is empty if all cells are blank or null
    }
    public static Project getProjectsById(int id) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                if ((int) row.getCell(ProjectListFileIndex.PROJECT_ID.getIndex()).getNumericCellValue() == id) {
                    return createProjectFromRow(row);
                }
            }
        }
        return null;
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
                if (row.getRowNum() == 0) continue; // Skip header
    
                // Use PROJECT_ID to find the correct row
                int rowProjectID = (int) row.getCell(ProjectListFileIndex.PROJECT_ID.getIndex()).getNumericCellValue();
                if (rowProjectID == project.getProjectID()) {
                    // Update the row
                    populateProjectRow(row, project);
    
                    try (FileOutputStream fileOut = new FileOutputStream(PROJECT_FILEPATH)) {
                        workbook.write(fileOut);
                    }
                    return true;
                }
            }
        }
        return false; // Return false if no matching project is found
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
                    int projectID = (int) row.getCell(ProjectListFileIndex.PROJECT_ID.getIndex()).getNumericCellValue();
    
                    // Remove related enquiries
                    EnquiryDB.getAllEnquiries().stream()
                        .filter(enquiry -> enquiry.getProject().getProjectID() == projectID)
                        .forEach(enquiry -> {
                            try {
                                EnquiryDB.deleteEnquiryByID(enquiry.getEnquiryID());
                            } catch (IOException e) {
                                LoggerUtility.logError("Failed to delete enquiry linked to project: " + projectName, e);
                            }
                        });
    
                    // Remove related applications
                    ApplicationDB.getApplicationsForProject(projectID).forEach(application -> {
                        try {
                            ApplicationDB.updateApplication(new Application(
                                application.getApplicant(),
                                application.getProject(),
                                "Deleted", 
                                application.getApplicationID(),
                                application.getFlatType()
                            ));
                        } catch (IOException e) {
                            LoggerUtility.logError("Failed to update application linked to project: " + projectName, e);
                        }
                    });
    
                    // Remove related officer registrations
                    OfficerRegistrationDB.getAllOfficerRegistrations().stream()
                        .filter(registration -> registration.getProject().getProjectID() == projectID)
                        .forEach(registration -> {
                            try {
                                OfficerRegistrationDB.updateOfficerRegistration(registration.getOfficerRegistrationID(), "Deleted");
                            } catch (IOException e) {
                                LoggerUtility.logError("Failed to update officer registration linked to project: " + projectName, e);
                            }
                        });
    
                    // Remove the project row
                    sheet.removeRow(row);
                    if (rowToDelete < sheet.getLastRowNum()) {
                        sheet.shiftRows(rowToDelete + 1, sheet.getLastRowNum(), -1);
                    }
    
                    try (FileOutputStream fileOut = new FileOutputStream(PROJECT_FILEPATH)) {
                        workbook.write(fileOut);
                    }
                    LoggerUtility.logInfo("Deleted project: " + projectName);
                    return true;
                }
            }
        } catch (IOException e) {
            LoggerUtility.logError("Failed to delete project: " + projectName, e);
            throw e;
        }
        return false;
    }

    public static ArrayList<Project> getProjectsByManager(String managerNric) throws IOException {
        ArrayList<Project> projects = new ArrayList<>();
    
        try (FileInputStream fis = new FileInputStream("resources/data/ProjectList.xlsx");
             Workbook workbook = new XSSFWorkbook(fis)) {
    
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
    
                String projectManagerNric = row.getCell(ProjectListFileIndex.MANAGER.getIndex()).getStringCellValue().trim();
    
                if (projectManagerNric.equals(managerNric)) {
                    projects.add(createProjectFromRow(row));
                }
            }
        }
        return projects;
    }

    // Get Project by ID
    public static Project getProjectByIdDB(int projectID) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                Cell idCell = row.getCell(ProjectListFileIndex.PROJECT_ID.getIndex());
                if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                    int id = (int) idCell.getNumericCellValue();
                    if (id == projectID) {
                        return createProjectFromRow(row);
                    }
                }
            }
        } catch (IOException e) {
            LoggerUtility.logError("Error retrieving project with ID: " + projectID, e);
            throw e;
        }
        return null; // Return null if no project matches the given ID
    }

    public static void addOfficerNRICToExcel(int projectID, String officerNRIC) throws IOException {
        try (FileInputStream fis = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fis)) {
    
            Sheet sheet = workbook.getSheetAt(0);
            int columnIdx = -1;
            int projectRowIdx = -1;
    
            // Find the column index for "Officer"
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                if (cell.getStringCellValue().equalsIgnoreCase("Officer")) {
                    columnIdx = cell.getColumnIndex();
                    break;
                }
            }
    
            // Find the row index for the specified project ID
            for (Row row : sheet) {
                Cell cell = row.getCell(ProjectListFileIndex.PROJECT_ID.getIndex());
                if (cell != null) {
                    int cellProjectID = -1;
                    if (cell.getCellType() == CellType.NUMERIC) {
                        cellProjectID = (int) cell.getNumericCellValue();
                    } else if (cell.getCellType() == CellType.STRING) {
                        try {
                            cellProjectID = Integer.parseInt(cell.getStringCellValue());
                        } catch (NumberFormatException e) {
                            continue;
                        }
                    }
    
                    if (cellProjectID == projectID) {
                        projectRowIdx = row.getRowNum();
                        break;
                    }
                }
            }
    
            if (columnIdx == -1 || projectRowIdx == -1) {
                throw new IOException("Column or Project ID not found in the Excel sheet.");
            }
    
            // Append the NRIC to the "Officer" column
            Row projectRow = sheet.getRow(projectRowIdx);
            Cell cell = projectRow.getCell(columnIdx);
            if (cell == null) {
                cell = projectRow.createCell(columnIdx);
            }
            String existingValue = cell.getStringCellValue();
            String newValue = existingValue.isEmpty() ? officerNRIC : existingValue + ", " + officerNRIC;
            cell.setCellValue(newValue);
    
            // Save the changes
            try (FileOutputStream fos = new FileOutputStream(PROJECT_FILEPATH)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            throw e;
        }
    }
}