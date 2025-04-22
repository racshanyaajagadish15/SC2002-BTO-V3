package views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;

import controllers.OfficerRegistrationController;
import models.HDBOfficer;
import models.OfficerRegistration;
import models.Project;
import utilities.ScannerUtility;

public class OfficerRegistrationView implements IDisplayResult {

	/**
	 * 
	 * @param registrations
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
                System.out.println("-----------------------------------------");
            }
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

	public void showRegistrableProjectMenu(ArrayList<Project> projects, HDBOfficer officer) {
		if (projects.size() == 0){
			displayInfo("No projects that you can register for are available!");
			return;
		}
		while (true){
			System.out.println("\n=========================================");
			System.out.println("       OFFICER PROJECT REGISTRATION      ");
			System.out.println("=========================================");

			// Print Project details
			for (int i = 0; i < projects.size(); i++){
				
				System.out.println("No. " + (i+1));
				System.out.println("Name: " + projects.get(i).getProjectName());
				System.out.println("Neighborhood: " + projects.get(i).getNeighborhood());
				System.out.println("Applications Period: " + projects.get(i).getApplicationOpeningDate() + " -> " + projects.get(i).getApplicationClosingDate());
				System.out.println("-----------------------------------------");
			}
			System.out.print("\nSelect a project number to register for (0 to go back): ");
			try {
				int projectIndex = ScannerUtility.SCANNER.nextInt() - 1;
				ScannerUtility.SCANNER.nextLine(); 
				if (projectIndex == -1) {
					return;
				}
				if (projectIndex < 0 || projectIndex >= projects.size()) {
					displayError("Invalid selection. Please try again.");
					continue;
				}
				// Confirm registration request
				int confirmation;
				while (true){
					System.out.println("Confirm the registration for " + projects.get(projectIndex).getProjectName() +"?");
					System.out.println("1. Yes");
					System.out.println("2. No");
					System.out.print("\nEnter option: ");
					confirmation = ScannerUtility.SCANNER.nextInt();
					ScannerUtility.SCANNER.nextLine(); 
					if (confirmation == 1 || confirmation == 2) {
						break;
					}
					displayError("Invalid selection. Please try again.");
				}

				// Submit application request
				if (confirmation == 1){
					OfficerRegistrationController officerJoinProjectController = new OfficerRegistrationController();
					Project selectedProject = projects.get(projectIndex);
					if (officerJoinProjectController.registerForProject(officer, selectedProject)){
						displaySuccess("Application Successful");
						return;
					}
					else{
						displayError("Application unsuccessful, contact admin if error persist.");
					}
				}
			}
			catch (InputMismatchException e){
				ScannerUtility.SCANNER.nextLine(); 
				displayError("Invalid selection. Please try again.");
				continue;
			}
		}
		
	}

}