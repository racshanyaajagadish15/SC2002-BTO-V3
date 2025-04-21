package views;

import controllers.ManagerApplicationController;
import databases.ApplicationDB;
import databases.ProjectDB;
import enums.ApplicationStatus;
import models.Application;
import models.Project;
import models.HDBManager; // Ensure Manager is imported from the correct package

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ManagerApplicationView implements DisplayResult{

    private final ManagerApplicationController controller = new ManagerApplicationController();
    private final Scanner scanner = new Scanner(System.in);

    // Add a field for the logged-in manager
    private final HDBManager loggedInManager;

    // Constructor to initialize the logged-in manager
    public ManagerApplicationView(HDBManager loggedInManager) {
        this.loggedInManager = loggedInManager;
    }

    public void showApplicationMenu() {
        int choice = -1;
        do {
            System.out.println("\n=========================================");
            System.out.println("           MANAGE APPLICATIONS           ");
            System.out.println("=========================================");
            System.out.println("1. Update Application Status");
            System.out.println("2. Update Withdrawal Status");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                switch (choice) {
                    case 1:
                        updateApplicationStatus();
                        break;
                    case 2:
                        updateWithdrawalStatus();
                        break;
                    case 0:
                        System.out.println("Exiting Application Manager...");
                        break;
                    default:
                        System.out.println("[ERROR] Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e){
                scanner.nextLine(); // Consume newline
                System.out.println("[ERROR] Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    private void updateApplicationStatus() {
        try {
            displayAllApplications();
    
            int applicationID = -1;
            while (true) {
                try {
                    System.out.print("\nEnter Application ID: ");
                    applicationID = scanner.nextInt();
                    scanner.nextLine();
                    break; 
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    System.out.println("[ERROR] Invalid input. Please enter a valid numeric Application ID.");
                }
            }
    
            System.out.println("Statuses:");
            System.out.println("1. " + ApplicationStatus.SUCESSFUL.getStatus());
            System.out.println("2. " + ApplicationStatus.UNSUCCESSFUL.getStatus());
            int status = -1;
            while (true) {
                try {
                    System.out.print("\nEnter Status option: ");
                    applicationID = scanner.nextInt();
                    scanner.nextLine();
                    if (status == 1 || status == 2){
                        break; 
                    }
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    System.out.println("[ERROR] Invalid input. Please enter a valid numeric Application ID.");
                }
            }
            String statusStr = "";
            if (status == 1){
                statusStr = ApplicationStatus.SUCESSFUL.getStatus();
            } 
            else if (status == 2) {
                statusStr = ApplicationStatus.UNSUCCESSFUL.getStatus();
            }
            // Fetch application
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
    
            System.out.print("Enter Application ID: ");
            int applicationID = -1;
            while (true) {
                try {
                    System.out.print("\nEnter Application ID: ");
                    applicationID = scanner.nextInt();
                    scanner.nextLine();
                    break; 
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    System.out.println("[ERROR] Invalid input. Please enter a valid numeric Application ID.");
                }
            }

            System.out.print("Enter New Withdrawal Status: ");
            String status = scanner.nextLine();
    
            // Fetch application
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
    private void displayAllApplications() {
        try {
            // Fetch all projects managed by the logged-in manager
            List<Project> managedProjects = ProjectDB.getProjectsByManager(loggedInManager.getNric());
            if (managedProjects.isEmpty()) {
                System.out.println("No projects found for the logged-in manager.");
                return;
            }
    
            System.out.println("\n===== Applications List =====");
            boolean applicationsFound = false;
    
            // Iterate through each project and fetch its applications
            for (Project project : managedProjects) {
                List<Application> applications = ApplicationDB.getApplicationsForProject(project.getProjectID());
                for (Application application : applications) {
                    applicationsFound = true;
                    System.out.println("ID: " + application.getApplicationID() +
                                       ", Applicant: " + application.getApplicant().getNric() +
                                       ", Project ID: " + application.getProject().getProjectID() +
                                       ", Status: " + application.getApplicationStatus());
                }
            }
    
            if (!applicationsFound) {
                System.out.println("No applications found for the projects managed by the logged-in manager.");
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to retrieve applications: " + e.getMessage());
        }
    }
    private boolean isManagerAuthorized(Project project) {
        try {
            // Fetch the project using the Project ID
            Project managedProject = ProjectDB.getProjectByID(project.getProjectID());
            if (managedProject == null) {
                System.out.println("[DEBUG] Project not found for ID: " + project.getProjectID());
                return false;
            }
    
            // Check if the logged-in manager owns the project
            boolean isAuthorized = managedProject.getProjectManager().getNric().equals(loggedInManager.getNric());
            System.out.println("[DEBUG] Checking authorization: Manager NRIC = " + loggedInManager.getNric() +
                               ", Project Manager NRIC = " + managedProject.getProjectManager().getNric() +
                               ", Authorized = " + isAuthorized);
            return isAuthorized;
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to verify project ownership: " + e.getMessage());
            return false;
        }
    }

    // Fetch an application by ID (replace with actual logic)
    private Application fetchApplicationByID(int applicationID) {
        try {
            // Fetch all projects managed by the logged-in manager
            List<Project> managedProjects = ProjectDB.getProjectsByManager(loggedInManager.getNric());
            for (Project project : managedProjects) {
                // Fetch applications for each project
                List<Application> applications = ApplicationDB.getApplicationsForProject(project.getProjectID());
                for (Application application : applications) {
                    if (application.getApplicationID() == applicationID) {
                        return application; // Return the application if found
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to fetch application: " + e.getMessage());
        }
        return null; // Return null if not found
    }
}