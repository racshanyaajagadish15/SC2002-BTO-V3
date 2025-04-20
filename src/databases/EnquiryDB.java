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

public class EnquiryDB {
    private static final String ENQUIRY_FILEPATH = "resources/data/ProjectEnquiry.xlsx";

    // Helper function to get numeric cell value safely
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

    private static String getStringCellValue(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.STRING) {
            return ""; // Return an empty string if the cell is null or not a string
        }
        return cell.getStringCellValue();
    }
    
    private static Date getDateCellValue(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(cell)) {
            return null; // Return null if the cell is not a valid date
        }
        return cell.getDateCellValue();
    }

    // Helper function to create Enquiry object from excel row
    private static Enquiry createEnquiryFromRow(Row row) throws NumberFormatException {
        if (row == null) {
            throw new IllegalArgumentException("Row is null");
        }
    
        // Skip rows with invalid or missing data
        Cell idCell = row.getCell(EnquiryFileIndex.ID.getIndex());
        if (idCell == null) {
            throw new IllegalArgumentException("Missing Enquiry ID in row: " + row.getRowNum());
        }
    
        int enquiryID = (int) getNumericCellValue(idCell);
        String nric = getStringCellValue(row.getCell(EnquiryFileIndex.NRIC.getIndex()));
        int projectID = (int) getNumericCellValue(row.getCell(EnquiryFileIndex.PROJECT_ID.getIndex()));
        String enquiry = getStringCellValue(row.getCell(EnquiryFileIndex.ENQUIRY.getIndex()));
        String reply = getStringCellValue(row.getCell(EnquiryFileIndex.REPLY.getIndex()));
        Date enquiryDate = getDateCellValue(row.getCell(EnquiryFileIndex.ENQUIRY_DATE.getIndex()));
        Date replyDate = getDateCellValue(row.getCell(EnquiryFileIndex.REPLY_DATE.getIndex()));
    
        return new Enquiry(enquiryID, nric, projectID, enquiry, reply, enquiryDate, replyDate);
    }
    // Helper function to create excel row from Enquiry object 
    private static void populateEnquiryRow(Row row, Enquiry enquiry) throws NumberFormatException {
        if (enquiry.getEnquiryID() != 0) {
            row.createCell(EnquiryFileIndex.ID.getIndex()).setCellValue(enquiry.getEnquiryID());
        }
        if (enquiry.getNric() != null && !enquiry.getNric().isEmpty()) {
            row.createCell(EnquiryFileIndex.NRIC.getIndex()).setCellValue(enquiry.getNric());
        }
        if (enquiry.getProjectID() != 0) {
            row.createCell(EnquiryFileIndex.PROJECT_ID.getIndex()).setCellValue(enquiry.getProjectID());
        }
        if (enquiry.getEnquiry() != null && !enquiry.getEnquiry().isEmpty()) {
            row.createCell(EnquiryFileIndex.ENQUIRY.getIndex()).setCellValue(enquiry.getEnquiry());
        }
        if (enquiry.getReply() != null && !enquiry.getReply().isEmpty()) {
            row.createCell(EnquiryFileIndex.REPLY.getIndex()).setCellValue(enquiry.getReply());
        }
        if (enquiry.getEnquiryDate() != null) {
            row.createCell(EnquiryFileIndex.ENQUIRY_DATE.getIndex()).setCellValue(enquiry.getEnquiryDate());
        }
        if (enquiry.getReplyDate() != null) {
            row.createCell(EnquiryFileIndex.REPLY_DATE.getIndex()).setCellValue(enquiry.getReplyDate());
        }
    }

    // Create Enquiry
    public static boolean createEnquiry(Enquiry enquiry) throws IOException, NumberFormatException {
        try (FileInputStream fileStreamIn = new FileInputStream(ENQUIRY_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row lastRow = sheet.getRow(sheet.getLastRowNum());
            if (lastRow == null || lastRow.getCell(EnquiryFileIndex.ID.getIndex()) == null) {
                enquiry.setEnquiryID(1); // Start with ID 1 if no valid last row exists
            } else {
                enquiry.setEnquiryID((int) lastRow.getCell(EnquiryFileIndex.ID.getIndex()).getNumericCellValue() + 1);
            }
            int newRowNum = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(newRowNum);
            populateEnquiryRow(row, enquiry);

            try (FileOutputStream fileOut = new FileOutputStream(ENQUIRY_FILEPATH)) {
                workbook.write(fileOut);
            }
            return true;
        }
        // False will only occur when error is thrown to controller
    }

    // Get all enquiries
    public static ArrayList<Enquiry> getAllEnquiries() throws IOException, NumberFormatException {
        ArrayList<Enquiry> enquiries = new ArrayList<>();
    
        try (FileInputStream fileStreamIn = new FileInputStream(ENQUIRY_FILEPATH);
             Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
    
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                if (row.getCell(EnquiryFileIndex.ID.getIndex()) == null) continue; // Skip empty rows
                try {
                    enquiries.add(createEnquiryFromRow(row));
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping invalid row: " + row.getRowNum() + " - " + e.getMessage());
                }
            }
        }
        return enquiries;
    }

    // Get all enquiries by user nric
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

    // Update Enquiry using ID
    public static boolean updateEnquiry(Enquiry enquiry) throws IOException, NumberFormatException {
        try (FileInputStream fileStreamIn = new FileInputStream(ENQUIRY_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowToUpdate = -1;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
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

    // Delete Enquiry using ID
    public static boolean deleteEnquiryByID(int ID) throws IOException, NumberFormatException {
        try (FileInputStream fileStreamIn = new FileInputStream(ENQUIRY_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowToDelete = -1;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
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
                return true;
            }
            return false;
        }
    }
    
}