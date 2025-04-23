package views;

import controllers.ManagerApplicationController;
import databases.ApplicationDB;
import databases.ProjectDB;
import enums.ApplicationStatus;
import models.Application;
import models.Project;
import utilities.ScannerUtility;
import models.HDBManager;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;

public class ManagerApplicationView implements IDisplayResult {

    private final ManagerApplicationController controller = new ManagerApplicationController();
    private final HDBManager loggedInManager;

    public ManagerApplicationView(HDBManager loggedInManager) {
        this.loggedInManager = loggedInManager;
    }

    public void showApplicationMenu() {
        int choice = -1;
        do {
            System.out.println("\n========================================");
            System.out.println("           MANAGE APPLICATIONS            ");
            System.out.println("\n========================================");
            System.out.println("  1. Update Application Status            ");
            System.out.println("  2. Update Withdrawal Status             ");
            System.out.println("  0. Exit                                 ");
            System.out.println("\n========================================");
            System.out.print("Enter your choice: ");
            try {
                choice = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); // Consume newline
                switch (choice) {
                    case 1 -> updateApplicationStatus();
                    case 2 -> updateWithdrawalStatus();
                    case 0 -> {}
                    default -> System.out.println("[ERROR] Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                System.out.println("[ERROR] Invalid input. Please enter a number.");
            }
        } while (choice != 0);
    }

    private void updateApplicationStatus() {
        try {
            displayAllApplications();

            int applicationID = promptForApplicationID();

            System.out.println("\n========================================");
            System.out.println("                 STATUSES          ");
            System.out.println("\n========================================");
            System.out.printf("| %-2s | %-23s |\n", "1", ApplicationStatus.SUCESSFUL.getStatus());
            System.out.printf("| %-2s | %-23s |\n", "2", ApplicationStatus.UNSUCCESSFUL.getStatus());
            System.out.println("\n========================================");

            int status = -1;
            while (true) {
                try {
                    System.out.print("Enter Status option: ");
                    status = ScannerUtility.SCANNER.nextInt();
                    ScannerUtility.SCANNER.nextLine();
                    if (status == 1 || status == 2) break;
                    else System.out.println("[ERROR] Invalid choice. Enter 1 or 2.");
                } catch (InputMismatchException e) {
                    ScannerUtility.SCANNER.nextLine();
                    System.out.println("[ERROR] Please enter a valid number.");
                }
            }

            String statusStr = (status == 1) ?
                    ApplicationStatus.SUCESSFUL.getStatus() :
                    ApplicationStatus.UNSUCCESSFUL.getStatus();

            Application application = fetchApplicationByID(applicationID);
            if (application != null) {
                controller.updateBTOApplicationStatus(application, statusStr);
                System.out.println("[SUCCESS] Application status updated successfully.");
            } else {
                System.out.println("[ERROR] Application not found.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to update application status: " + e.getMessage());
        }
    }

    private void updateWithdrawalStatus() {
        try {
            displayAllApplications();
            int applicationID = promptForApplicationID();
    
            String status = "";
            boolean validChoice = false;
    
            // Loop until a valid choice is made
            while (!validChoice) {
                // Display menu options for withdrawal status
                System.out.println("Select Withdrawal Status:");
                System.out.println("1. Withdrawal Pending");
                System.out.println("2. Withdrawal Processed");
                System.out.println("3. Withdrawal Declined");
                System.out.print("Enter your choice (1-3): ");
                
                int choice = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); // Consume the newline character
    
                switch (choice) {
                    case 1:
                        status = "Withdrawal Pending";
                        validChoice = true; // Valid choice made
                        break;
                    case 2:
                        status = "Withdrawal Processed";
                        validChoice = true; // Valid choice made
                        break;
                    case 3:
                        status = "Withdrawal Declined";
                        validChoice = true; // Valid choice made
                        break;
                    default:
                        System.out.println("[ERROR] Invalid choice. Please select a valid option.");
                        // No need to change validChoice, loop will continue
                }
            }
    
            Application application = fetchApplicationByID(applicationID);
            if (application != null) {
                controller.updateBTOApplicationWithdrawalStatus(application, status);
                System.out.println("[SUCCESS] Withdrawal status updated successfully.");
            } else {
                System.out.println("[ERROR] Application not found.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to update withdrawal status: " + e.getMessage());
        }
    }

    private int promptForApplicationID() {
        int id = -1;
        while (true) {
            try {
                System.out.print("Enter Application ID: ");
                id = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                break;
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                System.out.println("[ERROR] Please enter a valid numeric Application ID.");
            }
        }
        return id;
    }

    private void displayAllApplications() {
        try {
            List<Project> managedProjects = ProjectDB.getProjectsByManager(loggedInManager.getNric());
            if (managedProjects.isEmpty()) {
                System.out.println("No projects found for the logged-in manager.");
                return;
            }

            System.out.println("\n================================================================================");
            System.out.printf("| %-5s | %-15s | %-10s | %-25s |\n", "ID", "Applicant NRIC", "Project ID", "Status");
            System.out.println("--------------------------------------------------------------------------------");

            boolean applicationsFound = false;
            for (Project project : managedProjects) {
                List<Application> applications = ApplicationDB.getApplicationsForProject(project.getProjectID());
                for (Application application : applications) {
                    applicationsFound = true;
                    System.out.printf("| %-5d | %-15s | %-10s | %-25s |\n",
                            application.getApplicationID(),
                            application.getApplicant().getNric(),
                            application.getProject().getProjectID(),
                            application.getApplicationStatus());
                }
            }

            if (!applicationsFound) {
                System.out.println("| No applications found for your managed projects.                              |");
            }

            System.out.println("================================================================================");

        } catch (IOException e) {
            System.out.println("[ERROR] Failed to retrieve applications: " + e.getMessage());
        }
    }

    private Application fetchApplicationByID(int applicationID) {
        try {
            List<Project> managedProjects = ProjectDB.getProjectsByManager(loggedInManager.getNric());
            for (Project project : managedProjects) {
                List<Application> applications = ApplicationDB.getApplicationsForProject(project.getProjectID());
                for (Application application : applications) {
                    if (application.getApplicationID() == applicationID) {
                        return application;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to fetch application: " + e.getMessage());
        }
        return null;
    }
}
