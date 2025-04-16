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

    // Helper function to create Enquiry object from excel row
    private static Enquiry createEnquiryFromRow(Row row) throws NumberFormatException {
        return new Enquiry(
            (int) row.getCell(EnquiryFileIndex.ID.getIndex()).getNumericCellValue(),
            row.getCell(EnquiryFileIndex.NRIC.getIndex()).getStringCellValue(),
            (int) row.getCell(EnquiryFileIndex.PROJECT_ID.getIndex()).getNumericCellValue(),
            row.getCell(EnquiryFileIndex.ENQUIRY.getIndex()).getStringCellValue(),
            row.getCell(EnquiryFileIndex.REPLY.getIndex()).getStringCellValue(),
            (Date) row.getCell(EnquiryFileIndex.ENQUIRY_DATE.getIndex()).getDateCellValue(),
            (Date) row.getCell(EnquiryFileIndex.REPLY_DATE.getIndex()).getDateCellValue());
    }

    // Helper function to create excel row from Enquiry object 
    private static void populateEnquiryRow(Row row, Enquiry enquiry) throws NumberFormatException {
        row.createCell(EnquiryFileIndex.ID.getIndex()).setCellValue(enquiry.getEnquiryID());
        row.createCell(EnquiryFileIndex.NRIC.getIndex()).setCellValue(enquiry.getNric());
        row.createCell(EnquiryFileIndex.PROJECT_ID.getIndex()).setCellValue(enquiry.getProjectID());
        row.createCell(EnquiryFileIndex.ENQUIRY.getIndex()).setCellValue(enquiry.getEnquiry());
        row.createCell(EnquiryFileIndex.REPLY.getIndex()).setCellValue(enquiry.getReply());
        row.createCell(EnquiryFileIndex.ENQUIRY_DATE.getIndex()).setCellValue(enquiry.getEnquiryDate());
        row.createCell(EnquiryFileIndex.REPLY_DATE.getIndex()).setCellValue(enquiry.getReplyDate());
    }

    // Create Enquiry
    public static boolean createEnquiry(Enquiry enquiry) throws IOException, NumberFormatException {
        try (FileInputStream fileStreamIn = new FileInputStream(ENQUIRY_FILEPATH);
        Workbook workbook = new XSSFWorkbook(fileStreamIn)) {
            Sheet sheet = workbook.getSheetAt(0);
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
                if (row.getRowNum() == 0) continue; // Skip header
                enquiries.add(createEnquiryFromRow(row));
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
                if (row.getCell(EnquiryFileIndex.NRIC.getIndex()).getStringCellValue() == nric){
                    enquiries.add(createEnquiryFromRow(row));
                }
            }
        }
        return enquiries;
    }

    // Update Enquiry using ID
    public static boolean updateEnquryByID(Enquiry enquiry) throws IOException, NumberFormatException {
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