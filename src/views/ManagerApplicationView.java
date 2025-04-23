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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

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
            System.out.println("========================================");
            System.out.println("1. View All Application Status            ");
            System.out.println("2. Update Application Status            ");
            System.out.println("3. Update Withdrawal Status             ");
            System.out.println("0. Exit                                 ");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");
            try {
                choice = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); // Consume newline
                switch (choice) {
                    case 1 -> showApplications(getAllOwnedApplications());
                    case 2 -> updatePendingApplicationStatus();
                    case 3 -> updateWithdrawalApplicationStatus();
                    case 0 -> {return;}
                    default -> System.out.println("[ERROR] Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                System.out.println("[ERROR] Invalid input. Please enter a number.");
            }
        } while (choice != 0);
    }
    private void showApplications(Map<Project, List<Application>> projectApplicationsMap){
        List<Application> selectedProjectApplications = new ArrayList<Application>();
        for (Project project : projectApplicationsMap.keySet()){
            for (Application application : projectApplicationsMap.get(project)){
                selectedProjectApplications.add(application);
            }
        }
        System.out.println("\n=============================================================================================");
        System.out.printf("| %-5s | %-15s | %-35s | %-25s |\n", "No.", "Applicant NRIC", "Project Name", "Status");
        System.out.println("=============================================================================================");
        for (int i = 0; i < selectedProjectApplications.size(); i++){
            Application application = selectedProjectApplications.get(i);
            System.out.printf("| %-5d | %-15s | %-35s | %-25s |\n",
            i+1,
            application.getApplicant().getNric(),
            application.getProject().getProjectName(),
            application.getApplicationStatus());
            System.out.println("---------------------------------------------------------------------------------------------");
        }
    }

    private void updatePendingApplicationStatus() {
        try {
            Map<Project, List<Application>> projectApplicationMap = getPendingOwnedApplications();
            if (projectApplicationMap.size() == 0){
                System.out.println("No projects have pending application status.");
                return;
            }
            Application application = promptForApplication(projectApplicationMap);
            if (application == null){
                return;
            }
            int status = -1;

            while (true) {
                System.out.println("\n========================================");
                System.out.println("                 STATUSES          ");
                System.out.println("========================================");
                System.out.println("1. " + ApplicationStatus.SUCESSFUL.getStatus());
                System.out.println("2. " + ApplicationStatus.UNSUCCESSFUL.getStatus());
                System.out.println("========================================");
                try {
                System.out.print("\nEnter option (0 to back): ");
                status = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                if (status == 0){
                    return;
                }
                if (status == 1 || status == 2) break;
                    else displayError("Invalid choice");
                } catch (InputMismatchException e) {
                    ScannerUtility.SCANNER.nextLine();
                    displayError("Please enter a valid number.");
                }
            }

            String statusStr = (status == 1) ?
                ApplicationStatus.SUCESSFUL.getStatus() :
                ApplicationStatus.UNSUCCESSFUL.getStatus();

            controller.updateBTOApplicationStatus(application, statusStr);
            displaySuccess("Application status updated successfully.");
  
        } catch (Exception e) {
            displayError("Failed to update application status. If error persist, contact admin." );
        }
    }

    private void updateWithdrawalApplicationStatus() {
        try {
            
            Map<Project, List<Application>> projectApplicationMap = getAllWithdrawalApplications();
            if (projectApplicationMap.size() == 0){
                System.out.println("No projects have pending withdrawal application status.");
                return;
            }
            Application application = promptForApplication(projectApplicationMap);
            if (application == null){
                return;
            }
            String status = "";

            // Display menu options for withdrawal status

            while (true){
                try{
                    System.out.println("\n========================================");
                    System.out.println("                 STATUSES          ");
                    System.out.println("========================================");
                    System.out.println("1. " + ApplicationStatus.WITHDRAWAL_SUCCESSFUL.getStatus());
                    System.out.println("2. " + ApplicationStatus.WITHDRAWAL_UNSUCCESSFUL.getStatus());
                    System.out.println("========================================");

                    System.out.print("\nEnter option (0 to back): ");
                    int choice;
                    choice = ScannerUtility.SCANNER.nextInt();
                    ScannerUtility.SCANNER.nextLine(); // Consume the newline character
                    if (choice == 0){
                        return;
                    }
                    if (choice == 1){
                        status = ApplicationStatus.WITHDRAWAL_SUCCESSFUL.getStatus();
                        break;
                    } 
                    else if (choice == 2){
                        status = ApplicationStatus.WITHDRAWAL_UNSUCCESSFUL.getStatus();
                        break;
                    }
                    displayError("Invalid choice. Please select a valid option.");
                        // No need to change validChoice, loop will continue
                    }
                catch (InputMismatchException e){
                    ScannerUtility.SCANNER.nextLine(); // Consume the newline character
                    displayError("Invalid choice. Please select a valid option.");
                }
            }
                          
            controller.updateBTOApplicationWithdrawalStatus(application, status);
            displaySuccess("Withdrawal status updated successfully.");
            return;
        } catch (Exception e) {
            displayError("Failed to update withdrawal status");
        }
    }

    private Application promptForApplication(Map<Project, List<Application>> projectApplicationsMap) {
        int id = -1;
        while (true) {
            List<Project> projects = new ArrayList<Project>();
            for (Project project : projectApplicationsMap.keySet()){
                projects.add(project);
            }
            Project.sortProjectByName(projects);
            System.out.println("\nProjects:");
            for (int i = 0; i < projects.size(); i++){
                System.out.println((i+1) + ". " + projects.get(i).getProjectName());
            }
            System.out.print("0. Back");

            int projOption;
            while (true){
                try {
                    System.out.print("\nSelect project to view: ");
                    projOption = ScannerUtility.SCANNER.nextInt()-1;
                    ScannerUtility.SCANNER.nextLine();
                    if (projOption == -1){
                        return null;
                    }
                    else if (0 > projOption || projOption >= projects.size()){
                        displayError("Invalid option. Please try again.");
                    }
                    else{
                        break;
                    }
                }catch (InputMismatchException e){
                    ScannerUtility.SCANNER.nextLine();
                    displayError("Invalid option. Please try again.");
                }
            }
            Project selectedProject = projects.get(projOption);
            List<Application> selectedProjectApplications = projectApplicationsMap.get(selectedProject);
            while (true){
                try{
                    System.out.println("\n=============================================================================================");
                    System.out.printf("| %-5s | %-15s | %-35s | %-25s |\n", "No.", "Applicant NRIC", "Project Name", "Status");
                    System.out.println("=============================================================================================");
                    for (int i = 0; i < selectedProjectApplications.size(); i++){
                        Application application = selectedProjectApplications.get(i);
                        System.out.printf("| %-5d | %-15s | %-35s | %-25s |\n",
                        i+1,
                        application.getApplicant().getNric(),
                        application.getProject().getProjectName(),
                        application.getApplicationStatus());
                        System.out.println("---------------------------------------------------------------------------------------------");

                    }

                    System.out.print("\nEnter Application Number (0 to Back): ");
                    
                    id = ScannerUtility.SCANNER.nextInt() - 1;
                    ScannerUtility.SCANNER.nextLine();
                    if (id == -1){
                        return null;
                    }
                    else if (0 > id || id >= selectedProjectApplications.size()){
                        displayError("Invalid Selection. Please try again");
                    }
                    else{
                        return selectedProjectApplications.get(id);
                    }
                } catch (InputMismatchException e) {
                    ScannerUtility.SCANNER.nextLine();
                    displayError("Invalid Selection. Please try again");
                }
            }
        }
    }

    private Map<Project, List<Application>> getAllWithdrawalApplications() {
        try {
            List<Project> managedProjects = ProjectDB.getProjectsByManager(loggedInManager.getNric());
            if (managedProjects.isEmpty()) {
                return new HashMap<>();
            }

            Map<Project, List<Application>> pendingProjectApplicationsMap = new HashMap<>();
            for (Project project : managedProjects) {
                List<Application> applications = ApplicationDB.getApplicationsForProject(project.getProjectID());
                for (Application application : applications) {
                    if (application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_PENDING.getStatus())){
                        if (pendingProjectApplicationsMap.containsKey(project)){
                            pendingProjectApplicationsMap.get(project).add(application);
                        }
                        else{
                            pendingProjectApplicationsMap.put(project, new ArrayList<Application>());
                            pendingProjectApplicationsMap.get(project).add(application);
                        }
                    }
                }
            }

            return pendingProjectApplicationsMap;
        } catch (IOException e) {
            displayError("Failed to retrieve applications. If error persist, contact admin.");
            return new HashMap<>();
        }
    }

    private Map<Project, List<Application>> getAllOwnedApplications() {
        try {
            List<Project> managedProjects = ProjectDB.getProjectsByManager(loggedInManager.getNric());
            if (managedProjects.isEmpty()) {
                return new HashMap<>();
            }

            Map<Project, List<Application>> pendingProjectApplicationsMap = new HashMap<>();
            for (Project project : managedProjects) {
                List<Application> applications = ApplicationDB.getApplicationsForProject(project.getProjectID());
                for (Application application : applications) {
                    if (pendingProjectApplicationsMap.containsKey(project)){
                        pendingProjectApplicationsMap.get(project).add(application);
                    }
                    else{
                        pendingProjectApplicationsMap.put(project, new ArrayList<Application>());
                        pendingProjectApplicationsMap.get(project).add(application);
                    }
                }
            }

            return pendingProjectApplicationsMap;
        } catch (IOException e) {
            displayError("Failed to retrieve applications. If error persist, contact admin.");
            return new HashMap<>();
        }
    }

    private Map<Project, List<Application>> getPendingOwnedApplications() {
        try {
            List<Project> managedProjects = ProjectDB.getProjectsByManager(loggedInManager.getNric());
            if (managedProjects.isEmpty()) {
                return new HashMap<>();
            }
            Map<Project, List<Application>> pendingProjectApplicationsMap = new HashMap<>();
            for (Project project : managedProjects) {
                List<Application> applications = ApplicationDB.getApplicationsForProject(project.getProjectID());
                for (Application application : applications) {
                    if (application.getApplicationStatus().equals(ApplicationStatus.PENDING.getStatus())){
                        if (pendingProjectApplicationsMap.containsKey(project)){
                            pendingProjectApplicationsMap.get(project).add(application);
                        }
                        else{
                            pendingProjectApplicationsMap.put(project, new ArrayList<Application>());
                            pendingProjectApplicationsMap.get(project).add(application);
                        }
                    }
                }
            }
            return pendingProjectApplicationsMap;

        } catch (IOException e) {
            displayError("Failed to retrieve applications. If error persist, contact admin.");
            return new HashMap<>();
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
            displayError("Failed to fetch application. If error persist, contact admin.");
        }
        return null;
    }
    
}
