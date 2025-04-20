package views;

import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

import controllers.ApplicantEnquiryController;
import models.Applicant;
import models.Enquiry;
import models.Project;
import utilities.ScannerUtility;

public class ApplicantEnquiryView {

    public void showEnquiriesMenu(Map<Project, ArrayList<Enquiry>> projectEnquiriesMap) {
        if (projectEnquiriesMap.size() == 0){
			System.out.println("You are have not made any enquiries!");
			return;
		}
        List<Project> projectList = new ArrayList<>(projectEnquiriesMap.keySet());
        Project.sortProjectByName(projectList);
        ArrayList<Enquiry> enquiries;
        while (true) {
            try {
                System.out.println("\n=========================================");
                System.out.println("         VIEW & MANAGE ENQUIRIES         ");
                System.out.println("=========================================");

                for (int i = 0; i < projectList.size(); i++) {
                    System.out.println((i + 1) + ". " + projectList.get(i).getProjectName());
                }
                System.out.println("0. Exit");
                System.out.print("\nSelect a project to view enquiries: ");

                int projectIndex = ScannerUtility.SCANNER.nextInt() - 1;
                ScannerUtility.SCANNER.nextLine(); 

                if (projectIndex == -1) {
                    System.out.println("Returning to Applicant Dashboard...");
                    return;
                }

                if (projectIndex < 0 || projectIndex >= projectList.size()) {
                    System.out.println("Invalid selection. Please try again.");
                    continue;
                }

                Project selectedProject = projectList.get(projectIndex);
                enquiries = projectEnquiriesMap.get(selectedProject);

                if (enquiries.isEmpty()) {
                    System.out.println("No enquiries found for the selected project.");
                    continue;
                }
            }
            catch(InputMismatchException e){
                System.out.println("Invalid selection. Please try again.");
                ScannerUtility.SCANNER.nextLine();
                continue;
            }
            Enquiry selectedEnquiry;
            int enquiryIndex = -1;
            while (true) {
                try{
                    displayEnquiries(enquiries);
                    System.out.println("\nNote: Enquiries wiith replies can not be modified!");
                    System.out.print("Enter an enquiry number to modify (0 to go back): ");
                    enquiryIndex = ScannerUtility.SCANNER.nextInt() - 1;
                    ScannerUtility.SCANNER.nextLine();

                    if (enquiryIndex == -1) break;

                    if (enquiryIndex < 0 || enquiryIndex >= enquiries.size()) {
                        System.out.println("Invalid selection. Please try again.");
                        continue;
                    }

                    selectedEnquiry = enquiries.get(enquiryIndex);

                    if (!selectedEnquiry.getReply().isEmpty()) {
                        System.out.println("This enquiry has already been replied to and cannot be modified.");
                        continue;
                    }
                }
                catch(InputMismatchException e){
                    System.out.println("Invalid selection. Please try again.");
                    ScannerUtility.SCANNER.nextLine();
                    continue;
                }

                while (true) {
                    try{
                        System.out.println("\nEnquiry options:");
                        System.out.println("1. Edit Enquiry");
                        System.out.println("2. Delete Enquiry");
                        System.out.println("0. Back");
                        System.out.print("Select an option: ");
                        int option = ScannerUtility.SCANNER.nextInt();
                        ScannerUtility.SCANNER.nextLine(); 

                        ApplicantEnquiryController controller = new ApplicantEnquiryController();

                        if (option == 1) {
                            System.out.print("Enter your new enquiry: ");
                            String newText = ScannerUtility.SCANNER.nextLine();
                            controller.editEnquiry(selectedEnquiry, newText);
                            break;
                        } else if (option == 2) {
                            controller.deleteEnquiry(selectedEnquiry.getEnquiryID());
                            enquiries.remove(enquiryIndex);
                            System.out.println("Enquiry deleted successfully.");
                            break;
                        } else if (option == 0) {
                            break;
                        } else {
                            System.out.println("Invalid option. Please try again.");
                        }
                    }
                    catch(InputMismatchException e){
                        System.out.println("Invalid selection. Please try again.");
                        ScannerUtility.SCANNER.nextLine();
                        continue;
                    }
                }
            }
        }
    }

    public void showCreateEnquiry(Project project, Applicant applicant) {
        ApplicantEnquiryController applicantEnquiryController = new ApplicantEnquiryController();
        System.out.println("\n=========================================");
        System.out.println("            CREATE NEW ENQUIRY           ");
        System.out.println("=========================================");

        System.out.print("Enter your enquiry: ");
        String enquiryText = ScannerUtility.SCANNER.nextLine();

        if (enquiryText.trim().isEmpty()) {
            System.out.println("Enquiry cannot be empty. Please try again.");
            return;
        }

        Enquiry newEnquiry = new Enquiry(enquiryText, applicant.getNric(), project.getProjectID());
        applicantEnquiryController.submitEnquiry(newEnquiry);
        System.out.println("Enquiry created successfully.");
    }

    private void displayEnquiries(ArrayList<Enquiry> enquiries) {
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
                String no = (line == 0) ? String.valueOf(i + 1) : "";
                String date = (line == 0) ? String.valueOf(e.getEnquiryDate()) : "";
                String enquiry = line < enquiryLines.size() ? enquiryLines.get(line) : "";
                String reply = line < replyLines.size() ? replyLines.get(line) : "";
                String replyDate = (line == 0 && !e.getReply().isEmpty()) ? String.valueOf(e.getReplyDate()) : (line == 0 ? "-" : "");

                System.out.printf("| %-3s | %-40s | %-30s | %-30s | %-40s |\n",
                        no, date, enquiry, reply, replyDate);
            }
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }
    }
    
    
    // Table helper method
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
