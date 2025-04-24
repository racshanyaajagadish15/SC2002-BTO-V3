package views;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import models.Enquiry;
import models.Project;
import utilities.LoggerUtility;
import utilities.ScannerUtility;

public class ManagerEnquiryView implements IDisplayResult {

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
                        displayInfo("Reply to an Enquiry selected.");
                        break;
                    case 2:
                        displayInfo("View All Enquiries selected.");
                        break;
                    case 3:
                        displayInfo("View Enquiries for a Specific Project selected.");
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

    public int getEnquiryIDInput() {
        System.out.print("Enter the Enquiry ID to reply to (or 0 to go back): ");
        return ScannerUtility.SCANNER.nextInt();
    }

    public String getReplyInput() {
        System.out.print("Enter your reply: ");
        return ScannerUtility.SCANNER.nextLine();
    }

    public int getProjectIDInput() {
        System.out.print("Enter Project ID: ");
        return ScannerUtility.SCANNER.nextInt();
    }

    public void displayEnquiries(ArrayList<Enquiry> enquiries) {
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

    public void displayProjects(ArrayList<Project> projects) {
        if (projects.isEmpty()) {
            displayError("No projects available.");
            return;
        }

        System.out.println("Available Projects:");
        for (Project project : projects) {
            System.out.println("Project ID: " + project.getProjectID() + " - Name: " + project.getProjectName());
        }
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