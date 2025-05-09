package databases;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import enums.EnquiryFileIndex;
import models.Enquiry;
import models.Project;
import utilities.LoggerUtility;

/**
 * EnquiryDB class handles the database operations for the Enquiry entity.
 * It provides methods to create, read, update, and delete enquiries in an Excel file.
 * It also includes helper methods to convert between Enquiry objects and Excel rows.
 */

public class EnquiryDB {
    private static final String ENQUIRY_FILEPATH = "resources/data/ProjectEnquiry.xlsx";

    /**
     * Helper function to get numeric cell value from a cell.
     * Handles both numeric and string types.
     * @param cell The cell to retrieve the value from.
     * @return The numeric value of the cell.
     * @throws IllegalArgumentException if the cell is null or not numeric/string.
     */
    private static double getNumericCellValue(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("Cell is null");
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Expected numeric value but found string: " + cell.getStringCellValue());
            }
        } else {
            throw new IllegalArgumentException("Unexpected cell type: " + cell.getCellType());
        }
    }

    /**
     * Helper function to get string cell value from a cell.
     * Handles null and non-string types.
     * @param cell The cell to retrieve the value from.
     * @return The string value of the cell.
     * @throws IllegalArgumentException if the cell is null or not string.
     */

    private static String getStringCellValue(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.STRING) {
            return ""; // Return an empty string if the cell is null or not a string
        }
        return cell.getStringCellValue();
    }
    
    /**
     * Helper function to get date cell value from a cell.
     * Handles null and non-date types.
     * @param cell The cell to retrieve the value from.
     * @return The date value of the cell.
     * @throws IllegalArgumentException if the cell is null or not a date.
     */

    private static Date getDateCellValue(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(cell)) {
            return null; 
        }
        return cell.getDateCellValue();
    }

    /**
     * Helper function to create an Enquiry object from a row in the Excel sheet.
     * @param row The row to create the Enquiry object from.
     * @return The Enquiry object created from the row.
     * @throws IOException if there is an error reading the cell values.
     */
    private static Enquiry createEnquiryFromRow(Row row) throws IOException {
        if (row == null) {
            throw new IllegalArgumentException("Row is null");
        }
    
        int enquiryID = (int) getNumericCellValue(row.getCell(EnquiryFileIndex.ID.getIndex()));
        String nric = getStringCellValue(row.getCell(EnquiryFileIndex.NRIC.getIndex()));
        int projectID = (int) getNumericCellValue(row.getCell(EnquiryFileIndex.PROJECT_ID.getIndex()));
        Project project = ProjectDB.getProjectByIdDB(projectID); // Use ProjectDB to fetch the project
    
        if (project == null) {
            LoggerUtility.logInfo("Project with ID " + projectID + " not found for enquiry ID: " + enquiryID);
            return null; 
        }
    
        String enquiry = getStringCellValue(row.getCell(EnquiryFileIndex.ENQUIRY.getIndex()));
        String reply = getStringCellValue(row.getCell(EnquiryFileIndex.REPLY.getIndex()));
        Date enquiryDate = getDateCellValue(row.getCell(EnquiryFileIndex.ENQUIRY_DATE.getIndex()));
        Date replyDate = getDateCellValue(row.getCell(EnquiryFileIndex.REPLY_DATE.getIndex()));
    
        return new Enquiry(enquiryID, nric, project, enquiry, reply, enquiryDate, replyDate);
    }

    /**
     * Helper function to populate a row in the Excel sheet with Enquiry data.
     * @param row The row to populate with Enquiry data.
     * @param enquiry The Enquiry object containing the data to populate the row.
     * @throws NumberFormatException if there is an error converting cell values.
     */
    private static void populateEnquiryRow(Row row, Enquiry enquiry) throws NumberFormatException {
        if (enquiry.getEnquiryID() != 0) {
            row.createCell(EnquiryFileIndex.ID.getIndex()).setCellValue(enquiry.getEnquiryID());
        }
        if (enquiry.getNric() != null && !enquiry.getNric().isEmpty()) {
            row.createCell(EnquiryFileIndex.NRIC.getIndex()).setCellValue(enquiry.getNric());
        }
        if (enquiry.getProject() != null) {
            row.createCell(EnquiryFileIndex.PROJECT_ID.getIndex()).setCellValue(enquiry.getProject().getProjectID());
        }
        if (enquiry.getEnquiry() != null && !enquiry.getEnquiry().isEmpty()) {
            row.createCell(EnquiryFileIndex.ENQUIRY.getIndex()).setCellValue(enquiry.getEnquiry());
        }
        if (enquiry.getReply() != null && !enquiry.getReply().isEmpty()) {
            row.createCell(EnquiryFileIndex.REPLY.getIndex()).setCellValue(enquiry.getReply());
        }
        Workbook workbook = row.getSheet().getWorkbook();
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss")); //Date formatter

        if (enquiry.getEnquiryDate() != null) {
            Cell enquiryDateCell = row.createCell(EnquiryFileIndex.ENQUIRY_DATE.getIndex());
            enquiryDateCell.setCellValue(enquiry.getEnquiryDate());
            enquiryDateCell.setCellStyle(dateCellStyle);
        }
        if (enquiry.getReplyDate() != null) {
            Cell replyDateCell = row.createCell(EnquiryFileIndex.REPLY_DATE.getIndex());
            replyDateCell.setCellValue(enquiry.getReplyDate());
            replyDateCell.setCellStyle(dateCellStyle);
        }
    }

    /**
     * Creates a new enquiry in the Excel file.
     * @param enquiry The Enquiry object to create.
     * @return true if the enquiry was created successfully, false otherwise.
     */
    public static boolean createEnquiry(Enquiry enquiry) throws IOException, NumberFormatException {
        try (FileInputStream fileStreamIn = new FileInputStream(ENQUIRY_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row lastRow = sheet.getRow(sheet.getLastRowNum());
            if (lastRow == null || lastRow.getCell(EnquiryFileIndex.ID.getIndex()) == null) {
                enquiry.setEnquiryID(1); 
            } else {
                enquiry.setEnquiryID((int) lastRow.getCell(EnquiryFileIndex.ID.getIndex()).getNumericCellValue() + 1);
            }
            int newRowNum = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(newRowNum);
            populateEnquiryRow(row, enquiry);

            try (FileOutputStream fileOut = new FileOutputStream(ENQUIRY_FILEPATH)) {
                workbook.write(fileOut);
            }
            LoggerUtility.logInfo("Created new enquiry ID: " + enquiry.getEnquiryID());
            return true;
        } catch (IOException e) {
            LoggerUtility.logError("Failed to create enquiry for user: " + enquiry.getNric(), e);
            throw e;
        }
    }

    /**
     * Retrieves all enquiries from the Excel file.
     * @return An ArrayList of Enquiry objects.
     * @throws IOException if there is an error reading the file.
     */
    public static ArrayList<Enquiry> getAllEnquiries() throws IOException {
        ArrayList<Enquiry> enquiries = new ArrayList<>();
    
        try (FileInputStream fileStreamIn = new FileInputStream(ENQUIRY_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
    
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                if (row.getCell(EnquiryFileIndex.ID.getIndex()) == null) continue; // Skip empty rows
                try {
                    Enquiry enquiry = createEnquiryFromRow(row);
                    if (enquiry != null) { // Only add valid enquiries
                        enquiries.add(enquiry);
                    }
                } catch (IllegalArgumentException e) {
                    LoggerUtility.logInfo("Skipping invalid row: " + row.getRowNum() + " - " + e.getMessage());
                }
            }
            return enquiries;
        } catch (IOException e) {
            LoggerUtility.logError("Failed to retrieve enquiries from database", e);
            throw e;
        }
    }

    /**
     * Retrieves all enquiries for a specific NRIC from the Excel file.
     * @param nric The NRIC to filter enquiries by.
     * @return An ArrayList of Enquiry objects for the specified NRIC.
     * @throws IOException if there is an error reading the file.
     */
    public static ArrayList<Enquiry> getEnquiriesByNricDB(String nric) throws IOException, NumberFormatException {
        ArrayList<Enquiry> enquiries = new ArrayList<>();
        
        try (FileInputStream fileStreamIn = new FileInputStream(ENQUIRY_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                if (row.getCell(EnquiryFileIndex.NRIC.getIndex()).getStringCellValue().equals(nric)){
                    enquiries.add(createEnquiryFromRow(row));
                }
            }
        }
        return enquiries;
    }

    /**
     * Retrieves all enquiries for a specific project ID from the Excel file.
     * @param projectID The project ID to filter enquiries by.
     * @return An ArrayList of Enquiry objects for the specified project ID.
     * @throws IOException if there is an error reading the file.
     */
    public static boolean updateEnquiry(Enquiry enquiry) throws IOException, NumberFormatException {
        try (FileInputStream fileStreamIn = new FileInputStream(ENQUIRY_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowToUpdate = -1;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (row.getCell(EnquiryFileIndex.ID.getIndex()) == null || row.getCell(EnquiryFileIndex.ID.getIndex()).getCellType() != CellType.NUMERIC) continue;
                if ((int) row.getCell(EnquiryFileIndex.ID.getIndex()).getNumericCellValue() == enquiry.getEnquiryID()){
                    rowToUpdate = row.getRowNum();
                    break;
                }
            }

            if (rowToUpdate != -1) {
                Row row = sheet.getRow(rowToUpdate);
                populateEnquiryRow(row, enquiry);

                try (FileOutputStream fileOut = new FileOutputStream(ENQUIRY_FILEPATH)) {
                    workbook.write(fileOut);
                }
                return true;
            }
            return false;
        }
    
    }

    /**
     * Deletes an enquiry by its ID from the Excel file.
     * @param ID The ID of the enquiry to delete.
     * @return true if the enquiry was deleted successfully, false otherwise.
     * @throws IOException if there is an error reading or writing the file.
     */
    public static boolean deleteEnquiryByID(int ID) throws IOException, NumberFormatException {
        try (FileInputStream fileStreamIn = new FileInputStream(ENQUIRY_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowToDelete = -1;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                if (row.getCell(EnquiryFileIndex.ID.getIndex()) == null || row.getCell(EnquiryFileIndex.ID.getIndex()).getCellType() != CellType.NUMERIC) continue;
                if ((int) row.getCell(EnquiryFileIndex.ID.getIndex()).getNumericCellValue() == ID){
                    rowToDelete = row.getRowNum();
                    break;
                }
            }

            if (rowToDelete != -1) {
                sheet.removeRow(sheet.getRow(rowToDelete));
                // Shift rows up to fill the gap
                if (rowToDelete < sheet.getLastRowNum()) {
                    sheet.shiftRows(rowToDelete + 1, sheet.getLastRowNum(), -1);
                }

                try (FileOutputStream fileOut = new FileOutputStream(ENQUIRY_FILEPATH)) {
                    workbook.write(fileOut);
                }
                LoggerUtility.logInfo("Deleted enquiry ID: " + ID);
                return true;
            }
            return false;
        } catch (IOException e) {
            LoggerUtility.logError("Failed to delete enquiry ID: " + ID, e);
            throw e;
        }
    }
    
}