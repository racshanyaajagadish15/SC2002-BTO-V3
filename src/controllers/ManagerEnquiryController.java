package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;

import models.Enquiry;
import models.Project;
import utilities.ScannerUtility;
import views.ManagerEnquiryView;

public class ManagerEnquiryController implements IManagerEnquiryController {

    private final ManagerEnquiryView view;

    public ManagerEnquiryController() {
        this.view = new ManagerEnquiryView(this);
    }

    /**
     * Reply to an enquiry by updating its reply and reply date.
     * 
     * @param enquiry The enquiry to reply to.
     * @param message The reply message.
     */
    @Override
    public void replyToEnquiry(Enquiry enquiry, String message) {
        try {
            enquiry.setReply(message);
            enquiry.setReplyDate(new Date()); // Set the current date as the reply date
            boolean success = Enquiry.updateEnquiryDB(enquiry);
            if (success) {
                view.displaySuccess("Reply sent successfully.");
            } else {
                view.displayError("Failed to update the enquiry in the database.");
            }
        } catch (IOException e) {
            view.displayError("An error occurred while replying to the enquiry: " + e.getMessage());
        }
    }

    /**
     * Retrieve all project enquiries from the database.
     * 
     * @return A list of all enquiries.
     */
    public ArrayList<Enquiry> getAllProjectEnquiries() {
        try {
            return Enquiry.getAllEnquiriesDB();
        } catch (IOException e) {
            System.out.println("An error occurred while retrieving all project enquiries: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void viewProjectEnquiries() {
        try {
            System.out.print("Enter Project ID: ");
            int projectID = ScannerUtility.SCANNER.nextInt();
            ScannerUtility.SCANNER.nextLine();

            Project project = Project.getProjectByIdDB(projectID);
            if (project == null) {
                System.out.println("Project not found.");
                return;
            }

            ArrayList<Enquiry> enquiries = Enquiry.getProjectEnquiries(project);
            if (enquiries.isEmpty()) {
                System.out.println("No enquiries found for the specified project.");
                return;
            }
            displayEnquiries(enquiries);
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void displayEnquiries(ArrayList<Enquiry> enquiries) {
        for (Enquiry enquiry : enquiries) {
            System.out.println(enquiry);
        }
    }
}