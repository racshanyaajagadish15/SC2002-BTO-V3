package views;

import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;

import controllers.ManagerEnquiryController;
import databases.ProjectDB;
import models.Enquiry;
import models.Project;
import utilities.LoggerUtility;
import utilities.ScannerUtility;
import models.HDBManager;
//commit
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
            try {
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
                        LoggerUtility.logInfo("Manager exited the enquiry menu.");
                        System.out.println("Returning to Manager Dashboard...");
                        return; // Exit the method and return to the Manager Dashboard
                    default:
                        displayError("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine(); // Clear invalid input
                displayError("Invalid input. Please enter a number.");
            }
        }
    }

    private void replyToEnquiry() {
        try {
            // Fetch and display all enquiries
            ArrayList<Enquiry> allEnquiries = controller.getAllProjectEnquiries();
            if (allEnquiries.isEmpty()) {
                displayInfo("No enquiries available to reply to.");
                return;
            }
            displayEnquiries(allEnquiries);
    
            // Prompt user to select an enquiry by ID
            System.out.println("\n=========================================");
            System.out.println("           REPLY TO AN ENQUIRY           ");
            System.out.println("=========================================");
            System.out.print("Enter the Enquiry ID to reply to (or 0 to go back): ");
            int enquiryID = ScannerUtility.SCANNER.nextInt();
            ScannerUtility.SCANNER.nextLine(); // Consume newline
    
            if (enquiryID == 0) {
                displayInfo("Returning to the previous menu.");
                return; // Exit the method
            }
    
            // Find the selected enquiry
            Enquiry selectedEnquiry = null;
            for (Enquiry enquiry : allEnquiries) {
                if (enquiry.getEnquiryID() == enquiryID) {
                    selectedEnquiry = enquiry;
                    break;
                }
            }
    
            if (selectedEnquiry == null) {
                displayError("Invalid Enquiry ID. Please try again.");
                return;
            }
    
            // Prompt user to enter a reply
            System.out.print("Enter your reply: ");
            String reply = ScannerUtility.SCANNER.nextLine();
    
            // Send the reply
            controller.replyToEnquiry(selectedEnquiry, reply);
            showReplyResult(true);
            LoggerUtility.logInfo("Reply sent successfully for Enquiry ID: " + enquiryID);
    
        } catch (InputMismatchException e) {
            ScannerUtility.SCANNER.nextLine(); // Clear invalid input
            displayError("Invalid input. Please enter a valid Enquiry ID.");
        } catch (Exception e) {
            displayError("An error occurred: " + e.getMessage());
            LoggerUtility.logError("Error while replying to enquiry", e);
        }
    }

    private void viewAllEnquiries() {
        try {
            ArrayList<Enquiry> enquiries = controller.getAllProjectEnquiries();
            displayEnquiries(enquiries);
            LoggerUtility.logInfo("Viewed all enquiries.");
        } catch (Exception e) {
            displayError("An error occurred: " + e.getMessage());
            LoggerUtility.logError("Error while viewing all enquiries", e);
        }
    }

    private void viewProjectEnquiries() {
        try {
            // Retrieve all projects to display their names
            ArrayList<Project> allProjects = ProjectDB.getAllProjects(); // Assuming you have a method to get all projects
    
            if (allProjects.isEmpty()) {
                displayError("No projects available.");
                return;
            }
    
            // Print all project names
            System.out.println("Available Projects:");
            for (Project project : allProjects) {
                System.out.println("Project ID: " + project.getProjectID() + " - Name: " + project.getProjectName());
            }
    
            // Prompt for Project ID
            System.out.print("Enter Project ID: ");
            int projectID = ScannerUtility.SCANNER.nextInt();
            ScannerUtility.SCANNER.nextLine(); // Consume newline
    
            // Create a temporary project object to fetch enquiries
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
    
            // Fetch and display enquiries for the selected project
            ArrayList<Enquiry> enquiries = Enquiry.getProjectEnquiries(project);
            displayEnquiries(enquiries);
            LoggerUtility.logInfo("Viewed enquiries for Project ID: " + projectID);
        } catch (InputMismatchException e) {
            ScannerUtility.SCANNER.nextLine(); // Clear invalid input
            displayError("Invalid input. Please enter a valid Project ID.");
        } catch (Exception e) {
            displayError("An error occurred: " + e.getMessage());
            LoggerUtility.logError("Error while viewing project enquiries", e);
        }
    }

    private void displayEnquiries(ArrayList<Enquiry> enquiries) {
        if (enquiries.isEmpty()) {
            displayInfo("No enquiries found.");
            return;
        }
    
        // Determine the maximum width for each column
        int enquiryIdWidth = "Enquiry ID".length();
        int nricWidth = "NRIC".length();
        int projectIdWidth = "Project ID".length();
        int enquiryWidth = "Enquiry".length();
        int replyWidth = "Reply".length();
    
        for (Enquiry enquiry : enquiries) {
            enquiryIdWidth = Math.max(enquiryIdWidth, String.valueOf(enquiry.getEnquiryID()).length());
            nricWidth = Math.max(nricWidth, enquiry.getNric().length());
            projectIdWidth = Math.max(projectIdWidth, String.valueOf(enquiry.getProjectID()).length());
            enquiryWidth = Math.max(enquiryWidth, wrapText(enquiry.getEnquiry(), enquiryWidth).stream().map(String::length).max(Integer::compare).orElse(0));
            replyWidth = Math.max(replyWidth, wrapText(enquiry.getReply(), replyWidth).stream().map(String::length).max(Integer::compare).orElse(0));
        }
    
        // Calculate total width for the table
        int totalWidth = enquiryIdWidth + nricWidth + projectIdWidth + enquiryWidth + replyWidth + 8; // 8 for padding and borders
    
        // Print the header
        String headerFormat = "| %-"+enquiryIdWidth+"s | %-"+nricWidth+"s | %-"+projectIdWidth+"s | %-"+enquiryWidth+"s | %-"+replyWidth+"s |";
        String separator = "-".repeat(totalWidth);
    
        System.out.println("\n" + separator);
        System.out.printf(headerFormat, "Enquiry ID", "NRIC", "Project ID", "Enquiry", "Reply");
        System.out.println("\n" + separator);
    
        // Print the enquiry data
        for (Enquiry enquiry : enquiries) {
            List<String> enquiryLines = wrapText(enquiry.getEnquiry(), enquiryWidth);
            List<String> replyLines = wrapText(enquiry.getReply(), replyWidth);
            int maxLines = Math.max(enquiryLines.size(), replyLines.size());
    
            for (int i = 0; i < maxLines; i++) {
                System.out.printf(headerFormat,
                        (i == 0 ? enquiry.getEnquiryID() : ""), // Only show Enquiry ID on the first line
                        (i == 0 ? enquiry.getNric() : ""),     // Only show NRIC on the first line
                        (i == 0 ? enquiry.getProjectID() : ""), // Only show Project ID on the first line
                        (i < enquiryLines.size() ? enquiryLines.get(i) : ""), // Enquiry text
                        (i < replyLines.size() ? replyLines.get(i) : ""));    // Reply text
            }
            System.out.println(separator);
        }
    }
    
    /**
     * Wraps text into a list of strings, each with a maximum width.
     *
     * @param text  The text to wrap.
     * @param width The maximum width of each line.
     * @return A list of wrapped lines.
     */
    private List<String> wrapText(String text, int width) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }
    
        int currentIndex = 0;
        while (currentIndex < text.length()) {
            int endIndex = Math.min(currentIndex + width, text.length());
            lines.add(text.substring(currentIndex, endIndex));
            currentIndex = endIndex;
        }
    
        return lines;
    }

    public void showReplyResult(boolean success) {
        if (success) {
            displaySuccess("Reply sent successfully.");
        } else {
            displayError("Failed to send reply.");
        }
    }

    public void displayError(String message) {
        System.out.println("ERROR: " + message);
        LoggerUtility.logError(message, new Exception("Error logged without stack trace"));
    }

    public void displayInfo(String message) {
        System.out.println("INFO: " + message);
        LoggerUtility.logInfo(message);
    }

    public void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
        LoggerUtility.logInfo(message);
    }
}