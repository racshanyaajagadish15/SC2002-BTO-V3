package views;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

import models.Enquiry;
import models.Project;
import utilities.ScannerUtility;

public class ApplicantEnquiryView implements IDisplayResult {

    /**
     * Displays the menu for viewing and managing enquiries.
     * If no enquiries are found, it displays a message and returns -1.
     * @param projectEnquiriesMap
     * @param projectList
     * @return
     */
    public int showEnquiriesMenu(Map<Project, ArrayList<Enquiry>> projectEnquiriesMap, List<Project> projectList) {
        if (projectEnquiriesMap.size() == 0) {
            displayInfo("You have not made any enquiries!");
            return -1;
        }
        System.out.println("\n=========================================");
        System.out.println("         VIEW & MANAGE ENQUIRIES         ");
        System.out.println("=========================================");

        for (int i = 0; i < projectList.size(); i++) {
            System.out.println((i + 1) + ". " + projectList.get(i).getProjectName());
        }
        System.out.println("0. Exit");
        System.out.print("\nSelect a project to view enquiries: ");

        int projectIndex;
        try {
            projectIndex = ScannerUtility.SCANNER.nextInt() - 1;
            ScannerUtility.SCANNER.nextLine();
        } catch (InputMismatchException e) {
            ScannerUtility.SCANNER.nextLine();
            return -2;
        }
        return projectIndex;
    }

    /**
     * Displays the list of enquiries for a selected project.
     * If no enquiries are found, it displays a message and returns -1.
     * @param enquiries
     * @return
     */
    public int showEnquiriesList(ArrayList<Enquiry> enquiries) {
        displayEnquiries(enquiries);
        System.out.println("\nNote: Enquiries with replies can not be modified!");
        System.out.print("Enter an enquiry number to modify (0 to go back): ");
        int enquiryIndex;
        try {
            enquiryIndex = ScannerUtility.SCANNER.nextInt() - 1;
            ScannerUtility.SCANNER.nextLine();
        } catch (InputMismatchException e) {
            ScannerUtility.SCANNER.nextLine();
            return -2;
        }
        return enquiryIndex;
    }

    /**
     * Displays the options for modifying an enquiry.
     * If an invalid option is selected, it returns -1.
     * @return options
     */
    public int showEnquiryOptions() {
        System.out.println("\nEnquiry options:");
        System.out.println("1. Edit Enquiry");
        System.out.println("2. Delete Enquiry");
        System.out.println("0. Back");
        System.out.print("Select an option: ");
        int option;
        try {
            option = ScannerUtility.SCANNER.nextInt();
            ScannerUtility.SCANNER.nextLine();
        } catch (InputMismatchException e) {
            ScannerUtility.SCANNER.nextLine();
            return -1;
        }
        return option;
    }

    /**
     * Prompts for the new enquiry text.
     * If the input is blank, it returns an empty string.
     * @return enquiryText
     */
    public String promptNewEnquiryText() {
        System.out.print("Enter your new enquiry (Blank to return): ");
        return ScannerUtility.SCANNER.nextLine();
    }

    /**
     * Displays a message indicating that the enquiry has been modified successfully.
     * @param project
     * @return
     */
    public String showCreateEnquiry(Project project) {
        System.out.println("\n=========================================");
        System.out.println("            CREATE NEW ENQUIRY           ");
        System.out.println("=========================================");
        System.out.println("Project name: " + project.getProjectName());
        System.out.print("\nEnter your enquiry (Blank to return): ");
        String enquiryText = ScannerUtility.SCANNER.nextLine();
        return enquiryText;
    }
    
   /**
    * Displays all enquiries in a formatted table.
    * @param enquiries
    */
    public void displayEnquiries(ArrayList<Enquiry> enquiries) {
        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-3s | %-40s | %-30s | %-30s | %-40s |\n", 
            "No", "Enquiry Date", "Enquiry", "Reply", "Reply Date");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry e = enquiries.get(i);

            List<String> enquiryLines = wrapText(e.getEnquiry(), 30);
            List<String> replyLines = wrapText(e.getReply(), 30);

            int maxLines = Math.max(enquiryLines.size(), replyLines.size());

            for (int line = 0; line < maxLines; line++) {
                String no = (line == 0) ? String.valueOf(i + 1) : "-";
                String date = (line == 0) ? String.valueOf(e.getEnquiryDate()) : "-";
                String enquiry = line < enquiryLines.size() ? enquiryLines.get(line) : "-";
                String reply = line < replyLines.size() ? replyLines.get(line) : "-";
                String replyDate = (line == 0 && !e.getReply().isEmpty()) ? String.valueOf(e.getReplyDate()) : (line == 0 ? "-" : "");

                System.out.printf("| %-3s | %-40s | %-30s | %-30s | %-40s |\n",
                        no, date, enquiry, reply, replyDate);
            }
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }
    }

    /**
     * Helper to display information messages.
     * @param text
     * @param width
     * @return
     * 
     */
    private List<String> wrapText(String text, int width) {
        List<String> lines = new ArrayList<>();
        if (text == null) return lines;

        while (text.length() > width) {
            int breakIndex = text.lastIndexOf(' ', width);
            if (breakIndex == -1) breakIndex = width;

            lines.add(text.substring(0, breakIndex).trim());
            text = text.substring(breakIndex).trim();
        }
        if (!text.isEmpty()) lines.add(text);
        return lines;
    }
}
