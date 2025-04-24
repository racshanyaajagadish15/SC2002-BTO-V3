package views;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import models.Enquiry;
import models.Project;
import utilities.ScannerUtility;

/**
 * OfficerEnquiryView class handles displaying of registrations OfficerEnquiryView
 */
public class OfficerEnquiryView implements IDisplayResult {

    /**
     * Displays the menu for viewing and managing enquiries.
     * @param projectList
     * @return
     */
    public int promptProjectSelection(List<Project> projectList) {
        while (true) {
            try {
                System.out.println("\n=========================================");
                System.out.println("          VIEW & REPLY ENQUIRIES         ");
                System.out.println("=========================================");

                for (int i = 0; i < projectList.size(); i++) {
                    System.out.println((i + 1) + ". " + projectList.get(i).getProjectName());
                }
                System.out.println("0. Exit");
                System.out.print("\nSelect a project to view enquiries: ");

                int projectIndex = ScannerUtility.SCANNER.nextInt() - 1;
                ScannerUtility.SCANNER.nextLine(); 

                if (projectIndex == -1) {
                    return -1;
                }
                return projectIndex;
            }
            catch(InputMismatchException e){
                ScannerUtility.SCANNER.nextLine();
                return -2;
            }
        }
    }
    /**
     * Displays the list of enquiries for a selected project.
     * @param enquiryCount
     * @return
     */

    public int promptEnquirySelection(int enquiryCount) {
        try {
            System.out.print("\nEnter an enquiry number to modify (0 to go back): ");
            int enquiryIndex = ScannerUtility.SCANNER.nextInt() - 1;
            ScannerUtility.SCANNER.nextLine();
            if (enquiryIndex == -1) return -1;
            return enquiryIndex;
        }
        catch(InputMismatchException e){
            ScannerUtility.SCANNER.nextLine();
            return -2;
        }
        
    }

    /**
     * Prompts the user to enter a reply for an enquiry.
     * @return The reply text entered by the user.
     */
    public String promptReplyText() {
        System.out.print("Enter your reply enquiry (Blank to back): ");
        return ScannerUtility.SCANNER.nextLine();
    }

    /**
     * displayEnquiries displays the list of enquiries for a selected project.
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
     * Text wrapping function to break long lines into multiple lines.
     * @param text
     * @param width
     * @return
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