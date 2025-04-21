package views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;

import controllers.OfficerJoinProjectController;
import models.HDBOfficer;
import models.OfficerRegistration;
import models.Project;
import utilities.ScannerUtility;

public class OfficerJoinProjectView {

	/**
	 * 
	 * @param project
	 */
	public void registerForProject(int project) {
		
	}

	/**
	 * 
	 * @param registrations
	 */
	public void showRegistrationStatus(ArrayList<OfficerRegistration> registrations) {
		try {
			System.out.println("\n=========================================");
			System.out.println("           REGISTRATION STATUS           ");
			System.out.println("=========================================");
			if (registrations.isEmpty()) {
				System.out.println("No registrations found.");
				return;
			} else {
				for (OfficerRegistration registration : registrations) {
					Project project = Project.getProjectByIdDB(registration.getProjectID());
					if (project == null){
						continue;
					}
					System.out.println("Project Name: " + Project.getProjectByIdDB(registration.getProjectID()).getProjectName());
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
		} catch (IOException e){
			System.out.println("Cannot show project registrations due to error, contact admin if error persist.");
		}
	}

	public void showRegistrableProjectMenu(ArrayList<Project> projects, HDBOfficer officer) {
		if (projects.size() == 0){
			System.out.println("No projects that you can register for are available!");
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
					System.out.println("Invalid selection. Please try again.");
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
					System.out.println("Invalid selection. Please try again.");
				}

				// Submit application request
				if (confirmation == 1){
					OfficerJoinProjectController officerJoinProjectController = new OfficerJoinProjectController();
					Project selectedProject = projects.get(projectIndex);
					if (officerJoinProjectController.registerForProject(officer, selectedProject)){
						System.out.println("Application Successful");
						return;
					}
					else{
						System.out.println("Application unsuccessful, contact admin if error persist.");
					}
				}

				
			}
			catch (InputMismatchException e){
				ScannerUtility.SCANNER.nextLine(); 
				System.out.println("Invalid selection. Please try again.");
				continue;
			}
		}
		
	}

}