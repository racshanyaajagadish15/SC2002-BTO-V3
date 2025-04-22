package databases;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import models.Application;
import models.BookingReceipt;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BookingReceiptDB {
    private static final String RECEIPT_FILEPATH = "resources/data/bookingReceipts.xlsx";

    public static void generateReceipt(Application application) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(RECEIPT_FILEPATH)) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Booking Receipt");

            // Create receipt content
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Booking Receipt");

            Row applicantRow = sheet.createRow(2);
            applicantRow.createCell(0).setCellValue("Applicant Name:");
            applicantRow.createCell(1).setCellValue(application.getApplicant().getName());

            Row nricRow = sheet.createRow(3);
            nricRow.createCell(0).setCellValue("NRIC:");
            nricRow.createCell(1).setCellValue(application.getApplicant().getNric());

            Row ageRow = sheet.createRow(4);
            ageRow.createCell(0).setCellValue("Age:");
            ageRow.createCell(1).setCellValue(application.getApplicant().getAge());

            Row maritalRow = sheet.createRow(5);
            maritalRow.createCell(0).setCellValue("Marital Status:");
            maritalRow.createCell(1).setCellValue(application.getApplicant().getMaritalStatus());

            Row flatTypeRow = sheet.createRow(7);
            flatTypeRow.createCell(0).setCellValue("Flat Type:");
            flatTypeRow.createCell(1).setCellValue(application.getFlatType());

            Row projectRow = sheet.createRow(8);
            projectRow.createCell(0).setCellValue("Project Name:");
            projectRow.createCell(1).setCellValue(application.getProject().getProjectName());

            Row neighborhoodRow = sheet.createRow(9);
            neighborhoodRow.createCell(0).setCellValue("Neighborhood:");
            neighborhoodRow.createCell(1).setCellValue(application.getProject().getNeighborhood());

            // Auto-size columns
            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fileOut);
        }
    }
}
