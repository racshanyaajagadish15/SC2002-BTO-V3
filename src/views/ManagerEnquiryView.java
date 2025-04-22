package views;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;

import controllers.ManagerEnquiryController;
import models.Enquiry;
import models.Project;
import utilities.ScannerUtility;
import models.HDBManager;

public class ManagerEnquiryView implements IDisplayResult {

    private ManagerEnquiryController controller;

    public ManagerEnquiryView(ManagerEnquiryController controller) {
        this.controller = controller;
    }

    public ManagerEnquiryView(HDBManager HDBManager) {
        this.controller = new ManagerEnquiryController();
    }

    public void showEnquiryMenu() {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("             MANAGE ENQUIRIES           ");
            System.out.println("=========================================");
            System.out.println("1. Reply to an Enquiry");
            System.out.println("2. View All Enquiries");
            System.out.println("3. View Enquiries for a Specific Project");
            System.out.println("0. Exit");
            System.out.println("=========================================");
            System.out.print("Enter your choice: ");
            int choice = ScannerUtility.SCANNER.nextInt();
            ScannerUtility.SCANNER.nextLine(); // Consume newline
    
            switch (choice) {
                case 1:
                    replyToEnquiry();
                    break;
                case 2:
                    viewAllEnquiries();
                    break;
                case 3:
                    viewProjectEnquiries();
                    break;
                case 0:
                    System.out.println("Returning to Manager Dashboard...");
                    return; // Exit the method and return to the Manager Dashboard
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void replyToEnquiry() {
        System.out.print("Enter Enquiry ID: ");
        int enquiryID = ScannerUtility.SCANNER.nextInt();
        ScannerUtility.SCANNER.nextLine(); // Consume newline

        System.out.print("Enter your reply: ");
        String reply = ScannerUtility.SCANNER.nextLine();

        try {
            ArrayList<Enquiry> allEnquiries = controller.getAllProjectEnquiries();
            for (Enquiry enquiry : allEnquiries) {
                if (enquiry.getEnquiryID() == enquiryID) {
                    controller.replyToEnquiry(enquiry, reply);
                    showReplyResult(true);
                    return;
                }
            }
            displayError("Enquiry ID not found.");
        } catch (Exception e) {
            displayError("An error occurred: " + e.getMessage());
        }
    }

    private void viewAllEnquiries() {
        try {
            ArrayList<Enquiry> enquiries = controller.getAllProjectEnquiries();
            displayEnquiries(enquiries);
        } catch (Exception e) {
            displayError("An error occurred: " + e.getMessage());
        }
    }

    private void viewProjectEnquiries() {
        System.out.print("Enter Project ID: ");
        int projectID = ScannerUtility.SCANNER.nextInt();
        ScannerUtility.SCANNER.nextLine(); // Consume newline

        try {
            // Create a Project object with default values for other parameters
            Project project = new Project(
                projectID,                // Project ID
                "Default Project Name",   // Project Name
                null,                     // HDB Manager (null for now)
                "Default Neighborhood",   // Neighborhood
                new ArrayList<>(),        // Flat Types (empty list)
                new Date(),               // Opening Date (current date)
                new Date(),               // Closing Date (current date)
                0,                        // Officer Slots
                true                      // Visibility
            );
            ArrayList<Enquiry> enquiries = Enquiry.getProjectEnquiries(project);
            displayEnquiries(enquiries);
        } catch (Exception e) {
            displayError("An error occurred: " + e.getMessage());
        }
    }

    private void displayEnquiries(ArrayList<Enquiry> enquiries) {
        if (enquiries.isEmpty()) {
            displayInfo("No enquiries found.");
            return;
        }
        System.out.println("\n---------------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-15s | %-10s | %-20s | %-20s |\n", "Enquiry ID", "NRIC", "Project ID", "Enquiry", "Reply");
        System.out.println("---------------------------------------------------------------------------------------------");
        for (Enquiry enquiry : enquiries) {
            System.out.printf("| %-10d | %-15s | %-10d | %-20s | %-20s |\n",
                    enquiry.getEnquiryID(),
                    enquiry.getNric(),
                    enquiry.getProjectID(),
                    enquiry.getEnquiry(),
                    enquiry.getReply());
        }
        System.out.println("---------------------------------------------------------------------------------------------");
    }

    public void showReplyResult(boolean success) {
        if (success) {
            displaySuccess("Reply sent successfully.");
        } else {
            displayError("Failed to send reply.");
        }
    }
}