package controllers;

import models.Application;
import models.FlatType;
import models.HDBOfficer;
import models.OfficerRegistration;
import models.Project;
import utilities.LoggerUtility;
import views.OfficerBookingView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import enums.ApplicationStatus;
import enums.OfficerRegisterationStatus;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/**
 * OfficerBookingController.java
 * This class is responsible for handling the booking process of applications by HDB officers.
 */
public class OfficerBookingController implements IOfficerBookingController {

    private OfficerBookingView view;
    public OfficerBookingController() {
        this.view = new OfficerBookingView();
    }

    /**
     * Update the details of an application in the database.
     * 
     * @param application The application to update.
     */
    @Override
    public boolean updateApplications(Application application, String status) {
        try {
            // Update the application in the database
            application.setApplicationStatus(status);
            Application.updateApplicationDB(application);
            return true;
        } catch (Exception e) {
            LoggerUtility.logError("Failed to update application when booking", e);
            return false;
        }
    }

    /**
     * Update the project associated with an application in the database.
     * 
     * @param application The application whose project is to be updated.
     */
    @Override
    public boolean updateProject(Application application) {
        try {
            // Update the project details in the database
            for (FlatType flatType : application.getProject().getFlatTypes()){
                if (application.getFlatType() == flatType.getFlatType()){
                    flatType.setNumFlats(flatType.getNumFlats()-1);
                    Project.updateProjectDB(application.getProject());
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            LoggerUtility.logError("Failed to update project when booking", e);
            return false;
        }
    }

    /**
     * Generate a booking receipt for the specified application.
     * 
     * @param applicationId The ID of the application to generate a receipt for.
     * @return boolean indicating if receipt generation was successful
     */
    private boolean updateBooking(Application application) {
        if (updateApplications(application, ApplicationStatus.BOOKED.getStatus())){
            if (updateProject(application)){
                // Revert
                updateApplications(application, ApplicationStatus.BOOKED.getStatus());
                return false;
            }
        }
        
        return true;
    }

    /**
     * Select an application to book.
     * 
     * @param officer The HDB officer making the booking.
     */

    public void selectApplicationToBook(HDBOfficer officer){
        List<Application> allApplications;
        ArrayList<OfficerRegistration> registrations;
        try {
            allApplications = Application.getAllApplicationDB();
            registrations = OfficerRegistration.getOfficerRegistrationsByOfficerDB(officer);
            ArrayList<Application> officerProjectApplications = new ArrayList<Application>();
            for (Application application : allApplications){
                for (OfficerRegistration registration : registrations){
                    if ((registration.getProjectID() == application.getProject().getProjectID()) && 
                    application.getApplicationStatus().equals(ApplicationStatus.SUCESSFUL.getStatus()) && 
                    registration.getRegistrationStatus().equals(OfficerRegisterationStatus.SUCESSFUL.getStatus())){
                        officerProjectApplications.add(application);
                        break;
                    }
                }
            }
            if (officerProjectApplications.size() == 0){
                view.displayInfo("No applications need booking");
                return;
            }
            int option = view.promptApplicationSelection(officerProjectApplications, "Enter application to book (0 to back): ");
            if (option == -1){
                return;
            }
            Application selectedApplication = officerProjectApplications.get(option);
            if (updateBooking(selectedApplication)){
                view.displaySuccess("Successfully booked flat!");
            }
            else{
                view.displayError("Failed to book flat. Contact admin if error persist.");
            }
        }
        catch (IOException e){
            LoggerUtility.logError("Failed to get all projects when booking", e);
            view.displayError("Cannot display applications. Contact admin if error persist.");
        }

    }

    /**
     * View and generate a receipt for a booked application.
     * 
     * @param officer The HDB officer making the booking.
     */

    public void viewGenerateReceipt(HDBOfficer officer){
        List<Application> allApplications;
        ArrayList<OfficerRegistration> registrations;
        try {
            allApplications = Application.getAllApplicationDB();
            registrations = OfficerRegistration.getOfficerRegistrationsByOfficerDB(officer);
            ArrayList<Application> officerProjectApplications = new ArrayList<Application>();
            for (Application application : allApplications){
                for (OfficerRegistration registration : registrations){
                    if ((registration.getProjectID() == application.getProject().getProjectID()) && 
                    application.getApplicationStatus().equals(ApplicationStatus.BOOKED.getStatus()) && 
                    registration.getRegistrationStatus().equals(OfficerRegisterationStatus.SUCESSFUL.getStatus())){
                        officerProjectApplications.add(application);
                        break;
                    }
                }
            }
            if (officerProjectApplications.size() == 0){
                view.displayInfo("No booked applications");
                return;
            }
            int option = view.promptApplicationSelection(officerProjectApplications, "Enter application to generate receipt (0 to back): ");
            if (option == -1){
                return;
            }
            else{
                Application selectedApplication = officerProjectApplications.get(option);
                boolean success = generateReceiptPDF(selectedApplication);
                if (success) {
                    view.displaySuccess("Receipt generated successfully.");
                } else {
                    view.displayError("Failed to generate receipt PDF. Contact admin if error persist.");
                }
            }
        }
        catch (IOException e){
            LoggerUtility.logError("Failed to get all projects when booking", e);
            view.displayError("Cannot display applications. Contact admin if error persist.");
        }
    }

    /**
     * Generates a PDF receipt for the given application using PDFBox.
     * @param application The application to generate a receipt for.
     * @return true if successful, false otherwise.
     */
    private boolean generateReceiptPDF(Application application) {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float leading = 20;
            float yPosition = yStart;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                PDType1Font helveticaBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font helvetica = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                // Title
                contentStream.setFont(helveticaBold, 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Booking Receipt");
                contentStream.endText();

                yPosition -= leading * 2;

                // Applicant Details Header
                contentStream.setFont(helveticaBold, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Applicant Details");
                contentStream.endText();

                yPosition -= leading;

                // Applicant Details
                contentStream.setFont(helvetica, 12);

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Name: " + application.getApplicant().getName());
                contentStream.endText();

                yPosition -= leading;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("NRIC: " + application.getApplicant().getNric());
                contentStream.endText();

                yPosition -= leading;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Age: " + application.getApplicant().getAge());
                contentStream.endText();

                yPosition -= leading;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Marital Status: " + application.getApplicant().getMaritalStatus());
                contentStream.endText();

                yPosition -= leading * 2;

                // Project Details Header
                contentStream.setFont(helveticaBold, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Project Details");
                contentStream.endText();

                yPosition -= leading;

                // Project Details
                contentStream.setFont(helvetica, 12);

                double flatPrice = -1;
                for (FlatType flatType : application.getProject().getFlatTypes()){
                    if (flatType.getFlatType().equals(application.getFlatType())) {
                        flatPrice = flatType.getPricePerFlat();
                        break;
                    }
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(String.format("Flat Type Booked: %s ($%.2f)", application.getFlatType().toString(), flatPrice));
                contentStream.endText();

                yPosition -= leading;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Project Name: " + application.getProject().getProjectName());
                contentStream.endText();

                yPosition -= leading;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Neighbourhood: " + application.getProject().getNeighborhood());
                contentStream.endText();

                // Optionally add more project details here, each time decrementing yPosition by leading
            }

            // Save to file (filename: Receipt_<NRIC>_<ProjectID>.pdf)
            java.nio.file.Path directoryPath = java.nio.file.Paths.get("generated_files");
            if (!java.nio.file.Files.exists(directoryPath)) {
                java.nio.file.Files.createDirectories(directoryPath);
            }
            String fileName = "generated_files/Receipt_" + application.getApplicant().getNric() + "_" + application.getProject().getProjectID() + ".pdf";
            document.save(fileName);
            document.close();
            return true;
        } catch (Exception e) {
            LoggerUtility.logError("Failed to generate PDF receipt", e);
            try { document.close(); } catch (Exception ex) {}
            return false;
        }
    }
}