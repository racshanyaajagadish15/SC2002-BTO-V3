package views;

import java.util.InputMismatchException;
import java.util.List;
import java.util.ArrayList;

import models.Application;
import utilities.ScannerUtility;

public class OfficerBookingView implements IDisplayResult {

    // Helper method to wrap text to a specified width
    private List<String> wrapText(String text, int width) {
        List<String> lines = new ArrayList<>();
        if (text == null) {
            lines.add("-");
            return lines;
        }
        int idx = 0;
        while (idx < text.length()) {
            int end = Math.min(idx + width, text.length());
            lines.add(text.substring(idx, end));
            idx = end;
        }
        return lines;
    }

    /**
     * Displays a list of applications and prompts the user to select one.
     * @param applications List of applications to display.
     * @param prompt Prompt message for user input.
     * @return Selected application index (0-based), or -1 if user chooses to go back.
     */
    public int promptApplicationSelection(List<Application> applications, String prompt) {
        if (applications.size() == 0){
            return -1;
        }
        while (true) {
            displayApplicationsTable(applications);
            int option;
            System.out.print("\n" + prompt);
            try {
                option = ScannerUtility.SCANNER.nextInt() - 1;
                ScannerUtility.SCANNER.nextLine();
                if (option == -1) {
                    return option;
                } else if (option < 0 || option >= applications.size()) {
                    displayError("Invalid selection. Please try again.");
                    continue;
                }
                return option;
            } catch (InputMismatchException e) {
                displayError("Invalid selection. Please try again.");
                ScannerUtility.SCANNER.nextLine();
            }
        }
    }

    /**
     * Displays a formatted table of applications.
     * @param applications List of applications to display.
     */
    public void displayApplicationsTable(List<Application> applications) {
        System.out.println("\n-----------------------------------------------------------------------------------------");
        System.out.printf("| %-3s | %-20s | %-20s | %-15s | %-15s |\n",
            "No", "Applicant", "Project", "Flat Type", "Status");
        System.out.println("----------------------------------------------------------------------------------------");
        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);

            List<String> applicantLines = wrapText(app.getApplicant().getName(), 20);
            List<String> projectLines = wrapText(app.getProject().getProjectName(), 20);
            List<String> flatTypeLines = wrapText(app.getFlatType().toString(), 15);
            List<String> statusLines = wrapText(app.getApplicationStatus(), 15);

            int maxLines = Math.max(
                Math.max(applicantLines.size(), projectLines.size()),
                Math.max(flatTypeLines.size(), statusLines.size())
            );

            for (int line = 0; line < maxLines; line++) {
                String no = (line == 0) ? String.valueOf(i + 1) : "-";
                String applicant = line < applicantLines.size() ? applicantLines.get(line) : "";
                String project = line < projectLines.size() ? projectLines.get(line) : "";
                String flatType = line < flatTypeLines.size() ? flatTypeLines.get(line) : "";
                String status = line < statusLines.size() ? statusLines.get(line) : "";

                System.out.printf("| %-3s | %-20s | %-20s | %-15s | %-15s |\n",
                    no, applicant, project, flatType, status);
            }
            System.out.println("-----------------------------------------------------------------------------------------");
        }
    }
}