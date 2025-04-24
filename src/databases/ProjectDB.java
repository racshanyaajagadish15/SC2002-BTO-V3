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
/**
 * ProjectDB class handles the database operations for the Project entity.
 * It provides methods to create, read, update, and delete Project in an Excel file.
 * It also includes helper methods to convert between Project objects and Excel rows.
 */
public class ProjectDB {
    private static final String PROJECT_FILEPATH = "resources/data/ProjectList.xlsx";

    /**
     * createProjectFromRow(Row row)
     * This method creates a Project object from a given row in the Excel sheet.
     * It extracts the necessary fields from the row and returns a Project object.
     * @param row The row from which to create the Project object.
     * @return A Project object created from the row data.
     * @throws IllegalArgumentException if the row does not contain valid data.
     */
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
    
    /**
     * getStringCellValue(Row row, int cellIndex)
     * This method retrieves the string value from a cell in the given row.
     * @param row The row from which to retrieve the cell value.
     * @param cellIndex The index of the cell to retrieve.
     * @return The string value of the cell, or an empty string if the cell is null or not a string.
     * @throws IllegalArgumentException if the cell index is invalid.
     */
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

    private static void populateProjectRow(Row row, Project project) {
        row.createCell(ProjectListFileIndex.PROJECT_ID.getIndex()).setCellValue(project.getProjectID());

        row.createCell(ProjectListFileIndex.NAME.getIndex()).setCellValue(project.getProjectName());
        row.createCell(ProjectListFileIndex.NEIGHBORHOOD.getIndex()).setCellValue(project.getNeighborhood());

        FlatType type1 = project.getFlatTypes().get(0);
        row.createCell(ProjectListFileIndex.TYPE_1.getIndex()).setCellValue(type1.getFlatType());
        row.createCell(ProjectListFileIndex.TYPE_1_UNITS.getIndex()).setCellValue(type1.getNumFlats());
        row.createCell(ProjectListFileIndex.TYPE_1_PRICE.getIndex()).setCellValue(type1.getPricePerFlat());

        if (project.getFlatTypes().size() > 1) {
            FlatType type2 = project.getFlatTypes().get(1);
            row.createCell(ProjectListFileIndex.TYPE_2.getIndex()).setCellValue(type2.getFlatType());
            row.createCell(ProjectListFileIndex.TYPE_2_UNITS.getIndex()).setCellValue(type2.getNumFlats());
            row.createCell(ProjectListFileIndex.TYPE_2_PRICE.getIndex()).setCellValue(type2.getPricePerFlat());
        }

        row.createCell(ProjectListFileIndex.OPENING_DATE.getIndex()).setCellValue(project.getApplicationOpeningDate());
        row.createCell(ProjectListFileIndex.CLOSING_DATE.getIndex()).setCellValue(project.getApplicationClosingDate());


        row.createCell(ProjectListFileIndex.MANAGER.getIndex()).setCellValue(
            project.getProjectManager() != null ? project.getProjectManager().getName() : "N/A");

        row.createCell(ProjectListFileIndex.OFFICER_SLOT.getIndex()).setCellValue(project.getOfficerSlots());
        row.createCell(ProjectListFileIndex.VISIBILITY.getIndex()).setCellValue(
            project.getProjectVisibility() ? "Visible" : "Hidden");
    }

    /**
     * createProject(Project project)
     * This method creates a new project in the Excel file.
     * It checks if the project already exists and if not, adds it to the file.
     * @param project The Project object to create.
     * @return true if the project was created successfully, false if it already exists.
     */
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
    

            int newRowNum = sheet.getLastRowNum() + 1;
            while (newRowNum >= 0 && isRowEmpty(sheet.getRow(newRowNum))) {
                newRowNum--;
            }
            newRowNum++; 
    

            int projectID = 1;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; 
                Cell idCell = row.getCell(ProjectListFileIndex.PROJECT_ID.getIndex());
                if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                    projectID = Math.max(projectID, (int) idCell.getNumericCellValue() + 1);
                }
            }
    
            project.setProjectID(projectID);
    

            Row row = sheet.createRow(newRowNum);
            populateProjectRow(row, project);
    

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

    /**
     * getAllProjects()
     * This method retrieves all projects from the Excel file.
     * It reads the file and creates Project objects for each row, returning a list of projects.
     * @return An ArrayList of Project objects.
     * @throws IOException if there is an error reading the file.
     */
    public static ArrayList<Project> getAllProjects() throws IOException {
        ArrayList<Project> projects = new ArrayList<>();
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; 
    
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
    
    /**
     * isRowEmpty(Row row)
     * This method checks if a given row is empty.
     * It iterates through the cells in the row and checks if they are all blank or null.
     * @param row The row to check.
     * @return true if the row is empty, false otherwise.
     */
    private static boolean isRowEmpty(Row row) {
        if (row == null) return true; 
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false; 
            }
        }
        return true; 
    }

    /**
     * getProjectsById(int id)
     * This method retrieves a project by its ID from the Excel file.
     * It reads the file and checks each row for the matching ID.
     *  @param id The ID of the project to retrieve.
     *  @return A Project object if found, null otherwise.
     * @throws IOException if there is an error reading the file.
     */
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

    /**
     * getProjectByName(String projectName)
     * This method retrieves a project by its name from the Excel file.
     * It reads the file and checks each row for the matching name.
     * @param projectName The name of the project to retrieve.
     * @return A Project object if found, null otherwise.
     * @throws IOException if there is an error reading the file.
     * @throws IllegalArgumentException if the project name is invalid.
     */
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

    /**
     * updateProject(Project project)
     * This method updates an existing project in the Excel file.
     * It finds the project by its ID and updates the corresponding row with new data.
     * @param project The Project object with updated data.
     * @return true if the project was updated successfully, false if not found.
     * @throws IOException if there is an error reading or writing the file.
     */
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
        return false; 
    }

    /**
     * deleteProject(Project project)
     * This method deletes a project from the Excel file.
     * It finds the project by its ID and removes the corresponding row from the file.
     * @param project The Project object to delete.
     * @return true if the project was deleted successfully, false if not found.
     * @throws IOException if there is an error reading or writing the file.
     */
    public static boolean deleteProject(Project project) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
    
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (row.getCell(ProjectListFileIndex.NAME.getIndex()).getStringCellValue().equalsIgnoreCase(project.getProjectName())) {
                    int rowToDelete = row.getRowNum();
                    int projectID = (int) row.getCell(ProjectListFileIndex.PROJECT_ID.getIndex()).getNumericCellValue();
    
                    
                    EnquiryDB.getAllEnquiries().stream()
                        .filter(enquiry -> enquiry.getProject().getProjectID() == projectID)
                        .forEach(enquiry -> {
                            try {
                                EnquiryDB.deleteEnquiryByID(enquiry.getEnquiryID());
                            } catch (IOException e) {
                                LoggerUtility.logError("Failed to delete enquiry linked to project: " + project.getProjectName(), e);
                            }
                        });
    
                    
                    try {
                        ApplicationDB.deleteApplicationbyProj(project);
                    } catch (IOException e) {
                        LoggerUtility.logError("Failed to delete applications linked to project: " + project.getProjectName(), e);
                    }
    
                    
                    try {
                        OfficerRegistrationDB.deleteOfficerRegistrationByProjID((project));
                    } catch (IOException e) {
                        LoggerUtility.logError("Failed to delete officer registrations linked to project: " + project.getProjectName(), e);
                    }
    
                    sheet.removeRow(row);
                    if (rowToDelete < sheet.getLastRowNum()) {
                        sheet.shiftRows(rowToDelete + 1, sheet.getLastRowNum(), -1);
                    }
    
                    try (FileOutputStream fileOut = new FileOutputStream(PROJECT_FILEPATH)) {
                        workbook.write(fileOut);
                    }
                    LoggerUtility.logInfo("Deleted project: " + project.getProjectName());
                    return true;
                }
            }
        } catch (IOException e) {
            LoggerUtility.logError("Failed to delete project: " + project.getProjectName(), e);
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
                if (row.getRowNum() == 0) continue; 
    
                String projectManagerNric = row.getCell(ProjectListFileIndex.MANAGER.getIndex()).getStringCellValue().trim();
    
                if (projectManagerNric.equals(managerNric)) {
                    projects.add(createProjectFromRow(row));
                }
            }
        }
        return projects;
    }

    /**
     * getProjectByIdDB(int projectID)
     * This method retrieves a project by its ID from the Excel file.
     * It reads the file and checks each row for the matching ID.
     * @param projectID The ID of the project to retrieve.
     * @return A Project object if found, null otherwise.
     * @throws IOException if there is an error reading the file.
     */
    public static Project getProjectByIdDB(int projectID) throws IOException {
        try (FileInputStream fileStreamIn = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; 
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

    /**
     * addOfficerNRICToExcel(int projectID, String officerNRIC)
     * This method adds an officer's NRIC to the "Officer" column of the specified project in the Excel file.
     * It appends the NRIC to the existing value in the cell.
     * @param projectID The ID of the project to update.
     * @param officerNRIC The NRIC of the officer to add.
     * @throws IOException if there is an error reading or writing the file.
     */
    public static void addOfficerNRICToExcel(int projectID, String officerNRIC) throws IOException {
        try (FileInputStream fis = new FileInputStream(PROJECT_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fis)) {
    
            Sheet sheet = workbook.getSheetAt(0);
            int columnIdx = -1;
            int projectRowIdx = -1;
    
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                if (cell.getStringCellValue().equalsIgnoreCase("Officer")) {
                    columnIdx = cell.getColumnIndex();
                    break;
                }
            }
    
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
    
            Row projectRow = sheet.getRow(projectRowIdx);
            Cell cell = projectRow.getCell(columnIdx);
            if (cell == null) {
                cell = projectRow.createCell(columnIdx);
            }
            String existingValue = cell.getStringCellValue();
            String newValue = existingValue.isEmpty() ? officerNRIC : existingValue + ", " + officerNRIC;
            cell.setCellValue(newValue);
    
            try (FileOutputStream fos = new FileOutputStream(PROJECT_FILEPATH)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            throw e;
        }
    }
}