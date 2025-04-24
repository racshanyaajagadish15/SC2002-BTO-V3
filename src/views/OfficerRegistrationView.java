package views;

import java.util.ArrayList;
import java.util.InputMismatchException;

import models.OfficerRegistration;
import models.Project;
import utilities.ScannerUtility;

public class OfficerRegistrationView implements IDisplayResult {

    /**
     * Displays the registration status for the officer.
     */
    public void showRegistrationStatus(ArrayList<OfficerRegistration> registrations) {
        System.out.println("\n=========================================");
        System.out.println("           REGISTRATION STATUS           ");
        System.out.println("=========================================");
        if (registrations.isEmpty()) {
            displayInfo("No registrations found.");
            return;
        } else {
            for (OfficerRegistration registration : registrations) {
                Project project = registration.getProject();
                if (project == null){
                    continue;
                }
                System.out.println("Project Name: " + project.getProjectName());
                System.out.println("Registration Status: " + registration.getRegistrationStatus());
                System.out.println("Neighborhood: " + project.getNeighborhood());
                System.out.println("Applications Period: " + registration.getProject().getApplicationOpeningDate() + " -> " + registration.getProject().getApplicationClosingDate());
                System.out.println("-----------------------------------------");
            }
        }
    }

    /**
     * Displays the list of registrable projects.
     */
    public void showRegistrableProjects(ArrayList<Project> projects) {
        if (projects.size() == 0){
            displayInfo("No projects that you can register for are available!");
            return;
        }
        System.out.println("\n=========================================");
        System.out.println("       OFFICER PROJECT REGISTRATION      ");
        System.out.println("=========================================");
        for (int i = 0; i < projects.size(); i++){
            System.out.println("No. " + (i+1));
            System.out.println("Name: " + projects.get(i).getProjectName());
            System.out.println("Neighborhood: " + projects.get(i).getNeighborhood());
            System.out.println("Applications Period: " + projects.get(i).getApplicationOpeningDate() + " -> " + projects.get(i).getApplicationClosingDate());
            System.out.println("-----------------------------------------");
        }
    }

    /**
     * Gets the user's project selection.
     * Returns -1 if user selects 0 (back), otherwise returns the index (0-based).
     */
    public int getProjectSelection(int numProjects) {
        while (true) {
            System.out.print("\nSelect a project number to register for (0 to go back): ");
            try {
                int projectIndex = ScannerUtility.SCANNER.nextInt() - 1;
                ScannerUtility.SCANNER.nextLine();
                if (projectIndex == -1) {
                    return -1;
                }
                if (projectIndex < 0 || projectIndex >= numProjects) {
                    displayError("Invalid selection. Please try again.");
                    continue;
                }
                return projectIndex;
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                displayError("Invalid selection. Please try again.");
            }
        }
    }

    /**
     * Gets confirmation from the user for registration.
     * Returns true if confirmed, false otherwise.
     */
    public boolean getRegistrationConfirmation(String projectName) {
        int confirmation;
        while (true){
            System.out.println("\nConfirm the registration for " + projectName +"?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.print("\nEnter option: ");
            try {
                confirmation = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                if (confirmation == 1) {
                    return true;
                }
                if (confirmation == 2) {
                    return false;
                }
                displayError("Invalid selection. Please try again.");
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                displayError("Invalid selection. Please try again.");
            }
        }
    }

    /**
     * Waits for the user to select "Back" (0) to continue.
     */
    public void waitForBack() {
        int option = -1;
        while (true){
            try {
                System.out.println("Actions:");
                System.out.println("0. Back");
                System.out.print("\nEnter Option: ");
                option = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                if (option == 0){
                    return;
                }
                System.out.println("Invalid option.");
            } catch (InputMismatchException e){
                ScannerUtility.SCANNER.nextLine();
                System.out.println("Invalid option.");
            }
        }
    }
}