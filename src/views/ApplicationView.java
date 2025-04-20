package views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import controllers.ApplicantApplicationController;
import enums.ApplicationStatus;
import enums.FilterIndex;
import enums.FlatTypeName;
import models.Applicant;
import models.Application;
import models.FlatType;
import models.Project;
import utilities.ScannerUtility;

public class ApplicationView {

	private boolean showApplicableProjects(ArrayList<Project> applicableProjects, Applicant applicant) {
		if (applicableProjects.size() == 0){
			System.out.println("You are uneligible to apply for any projects!");
			return false;
		}
		
		while (true){
			System.out.println("\n=========================================");
			System.out.println("            APPLY FOR PROJECT            ");
			System.out.println("=========================================");

			// Print Project details
			for (int i = 0; i < applicableProjects.size(); i++){
				System.out.println("No. " + (i+1));
				System.out.println("Name: " + applicableProjects.get(i).getProjectName());
				System.out.println("Neighborhood: " + applicableProjects.get(i).getNeighborhood());
				System.out.println("Applications Period: " + applicableProjects.get(i).getApplicationOpeningDate() + " -> " + applicableProjects.get(i).getApplicationClosingDate());
				System.out.println("Flat Types: ");
				for (FlatType flatType : applicableProjects.get(i).getFlatTypes()){
					System.out.println("~> " + flatType.getFlatType() + " : " + flatType.getNumFlats() + " units available ($" + flatType.getPricePerFlat() + ")");
				}
				System.out.println("-----------------------------------------");
			}

			System.out.print("\nSelect a project number to enquire or apply (0 to go back): ");
			try {
				int projectIndex = ScannerUtility.SCANNER.nextInt() - 1;
				ScannerUtility.SCANNER.nextLine(); 
				if (projectIndex == -1) {
					return false;
				}
				if (projectIndex < 0 || projectIndex >= applicableProjects.size()) {
					System.out.println("Invalid selection. Please try again.");
					continue;
				}
				int action;
				while (true){
					try {
						System.out.println("\nActions: ");
						System.out.println("1. Submit Enquiry");
						System.out.println("2. Submit Application");
						System.out.println("0. Back ");
						System.out.print("\nSelect action: ");

						action = ScannerUtility.SCANNER.nextInt();
						ScannerUtility.SCANNER.nextLine(); 
						if (action >= 0 && action <= 2){
							break;
						}
						System.out.println("Invalid selection. Please try again.");
					}
					catch (InputMismatchException e){
						ScannerUtility.SCANNER.nextLine(); 
						System.out.println("Invalid selection. Please try again.");
						continue;
					}
				}
				if (action == 0) {
					continue;
				}
				if (action == 1) {
					ApplicantEnquiryView applicantEnquiryView = new ApplicantEnquiryView();
					applicantEnquiryView.showCreateEnquiry(applicableProjects.get(projectIndex), applicant);
					continue;
				}
				FlatType selectedFlatType = null;
				int flatTypeIndex;
				while (true){
					int i = 0;
					System.out.println("\nFlat Types:");
					for (FlatType flatType : applicableProjects.get(projectIndex).getFlatTypes()){
						System.out.println((i+1) + ". "+ flatType.getFlatType() + " : " + flatType.getNumFlats() + " units available ($" + flatType.getPricePerFlat() +")");
						i++;
					}
					System.out.println("0. Back");
					System.out.print("\nSelect a flat type to apply: ");
					flatTypeIndex = ScannerUtility.SCANNER.nextInt() - 1;
					ScannerUtility.SCANNER.nextLine(); 
					if (flatTypeIndex >= 0  && flatTypeIndex < applicableProjects.get(projectIndex).getFlatTypes().size()) {
						selectedFlatType = applicableProjects.get(projectIndex).getFlatTypes().get(flatTypeIndex);
						break;
					}
					else if (flatTypeIndex == -1){
						break;
					}
					System.out.println("Invalid selection. Please try again.");
				}

				if (flatTypeIndex == -1){
					continue;
				}

				// Confirm application request
				int confirmation;
				while (true){
					System.out.println("\nNote: Only 1 application can be made.");
					System.out.println("Confirm the application?");
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
					ApplicantApplicationController applicantApplicationController = new ApplicantApplicationController();
					Project selectedProject = applicableProjects.get(projectIndex);
					if (applicantApplicationController.submitApplication(selectedProject, applicant, selectedFlatType)){
						System.out.println("Application Successful");
						return true;
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

	private boolean showApplicableFilteredProjects(ArrayList<Project> applicableProjects, Applicant applicant){
		int option;
		double price;
		List<String> filters = applicant.getFilter();
		while (true){
			try {
				System.out.println("\n=========================================");
				System.out.println("             FILTER PROJECTS             ");
				System.out.println("=========================================");
				System.out.println("Current filters");
				System.out.println("Project: " + (filters.get(FilterIndex.PROJECT_NAME.getIndex()) == "" ? " - " : filters.get(FilterIndex.PROJECT_NAME.getIndex())));
				System.out.println("Neighbourhood: " + (filters.get(FilterIndex.NEIGHBOURHOOD.getIndex()) == "" ? " - " : filters.get(FilterIndex.NEIGHBOURHOOD.getIndex())));
				System.out.println("Price start: " + (filters.get(FilterIndex.PRICE_START.getIndex()) == "" ? " - " : "$"+filters.get(FilterIndex.PRICE_START.getIndex())));
				System.out.println("Price end: " + (filters.get(FilterIndex.PRICE_END.getIndex()) == "" ? " - " : "$"+filters.get(FilterIndex.PRICE_END.getIndex())));
				System.out.println("Flat type: " + (filters.get(FilterIndex.FLAT_TYPE.getIndex()) == "" ? " - " : filters.get(FilterIndex.FLAT_TYPE.getIndex())));
				System.out.println("=========================================");
				System.out.println("1. Set project name search");
				System.out.println("2. Set neighbourhood name search");
				System.out.println("3. Set minimum price");
				System.out.println("4. Set maximum price");
				System.out.println("5. Set flat type");
				System.out.println("6. Clear filters");
				System.out.println("7. View projects sorted by project name");
				System.out.println("8. View projects sorted by neighbourhood");
				System.out.println("9. View projects sorted by price");
				System.out.println("0. Exit");
				System.out.println("=========================================");
				System.out.print("\nSelect an option: ");

				option = ScannerUtility.SCANNER.nextInt();
				ScannerUtility.SCANNER.nextLine(); 
				ArrayList<Project> projectsToFilter = new ArrayList<>();
				projectsToFilter.addAll(applicableProjects);
				// Option managment
				switch (option) {
					case 0:
						return false;
					case 1:
						System.out.print("\nEnter project name (Nothing to clear filter): ");
						filters.set(FilterIndex.PROJECT_NAME.getIndex(), ScannerUtility.SCANNER.nextLine());
						break;
					case 2:
						System.out.print("\nEnter neighbourhood name (Nothing to clear filter): ");
						filters.set(FilterIndex.NEIGHBOURHOOD.getIndex(), ScannerUtility.SCANNER.nextLine());
						break;
					case 3:
						System.out.print("\nEnter minimum price (0 to clear filter): ");
						try {
							price = ScannerUtility.SCANNER.nextDouble();
							ScannerUtility.SCANNER.nextLine();

						} catch (InputMismatchException e){
							ScannerUtility.SCANNER.nextLine(); 
							System.out.println("Invalid price. Setting failed.");
							break;
						}
						// Reset
						if (price == 0){
							filters.set(FilterIndex.PRICE_START.getIndex(), "");
							break;
						}
						else if (price < 0){
							System.out.println("Number cannot be negative. Setting failed.");
						}
						// check if start price is smaller the end price and if price is > 0
						String priceEnd = applicant.getFilter().get(FilterIndex.PRICE_END.getIndex());
						if (priceEnd != "" && (Double.parseDouble(priceEnd) < price)){
							System.out.println("Maximum price is smaller then minimum price. Setting failed.");
							break;
						}


						filters.set(FilterIndex.PRICE_START.getIndex(), String.format("%.2f", price));
						break;
					case 4:
						System.out.print("\nEnter maximum price (0 to clear filter): ");
						try {
							price = ScannerUtility.SCANNER.nextDouble();
							ScannerUtility.SCANNER.nextLine();
						} catch (InputMismatchException e){
							ScannerUtility.SCANNER.nextLine(); 
							System.out.println("Invalid price. Setting failed.");
							break;
						}
						// Reset
						if (price == 0){
							filters.set(FilterIndex.PRICE_END.getIndex(), "");
							break;
						}
						else if (price < 0){
							System.out.println("Number cannot be negative. Setting failed.");
							break;
						}
						// Check if end price is bigger the start price and if price is > 0
						String priceStart = applicant.getFilter().get(FilterIndex.PRICE_START.getIndex());
						if (priceStart != "" && Double.parseDouble(priceStart) > price){
							System.out.println("Minimum price is bigger then maximum price. Setting failed.");
							break;
						}
						filters.set(FilterIndex.PRICE_END.getIndex(), String.format("%.2f", price));
						break;
					case 5:

						int selectedFlat = 0;
						while (true){
							try {
								System.out.println("Flat Types:");
								System.out.println("1. 2~ROOM");
								System.out.println("2. 3~ROOM");
								System.out.println("3. Clear Filter");
								System.out.print("\nEnter Option: ");
								selectedFlat = ScannerUtility.SCANNER.nextInt();
								ScannerUtility.SCANNER.nextLine();
								if (selectedFlat >= 1 && selectedFlat <= 3){
									break;
								}
								System.out.println("Invalid option.");

							} catch (InputMismatchException e){
								ScannerUtility.SCANNER.nextLine(); 
								System.out.println("Invalid option.");
							}
						} 
			
						if (selectedFlat == 1){
							filters.set(FilterIndex.FLAT_TYPE.getIndex(), FlatTypeName.TWO_ROOM.getflatTypeName());
						}
						else if (selectedFlat == 2){
							filters.set(FilterIndex.FLAT_TYPE.getIndex(), FlatTypeName.THREE_ROOM.getflatTypeName());
						}
						else if (selectedFlat == 3){
							filters.set(FilterIndex.FLAT_TYPE.getIndex(), "");
						}
						break;					
					case 6:
						filters.clear();
						filters.addAll(List.of("","","","",""));
						break;
					case 7:
						Project.filterProject(projectsToFilter, filters);
						if (projectsToFilter.size() == 0){
							System.out.println("No result from search");
						}
						else {
							Project.sortProjectByName(projectsToFilter);
							if (showApplicableProjects(projectsToFilter, applicant)){
								return true;
							}
						}
						break;
					case 8:
						Project.filterProject(projectsToFilter, filters);
						if (projectsToFilter.size() == 0){
							System.out.println("No result from search");
						}
						else {
							Project.sortProjectByNeighbourhood(projectsToFilter);
							if (showApplicableProjects(projectsToFilter, applicant)){
								return true;
							}
						}
						break;
					case 9:
						Project.filterProject(projectsToFilter, filters);
						if (projectsToFilter.size() == 0){
							System.out.println("No result from search");
						}
						else {
							// Confirm application request
							int order;
							while (true){
								try{
									System.out.println("\nHow would you like it sorted?");
									System.out.println("1. Ascending");
									System.out.println("2. Descending");
									System.out.println("\nEnter Option: ");
									order = ScannerUtility.SCANNER.nextInt();
									ScannerUtility.SCANNER.nextLine(); 
									if (order == 1 || order == 2) {
										break;
									}
									System.out.println("Invalid selection. Please try again.");
								}
								catch (InputMismatchException e){
									System.out.println("Invalid selection. Please try again.");
								}
							}
							if (order == 1) {
								Project.sortProjectByPrice(projectsToFilter, true);
							}
							else{
								Project.sortProjectByPrice(projectsToFilter, false);
							}
							if (showApplicableProjects(projectsToFilter, applicant)){
								return true;
							}
						}
						break;
					default:
						System.out.println("Invalid selection. Please try again.");
						break;
				}
			}
			catch (InputMismatchException e) {
				ScannerUtility.SCANNER.nextLine(); 
				System.out.println("Invalid selection. Please try again.");
			}
		}
	}

	public void showApplicationMenu(ArrayList<Project> applicableProjects, Applicant applicant) {
		try{
			applicant.setApplication(Application.getApplicationByNricDB(applicant.getNric()));
		}
		catch (IOException e){
			System.out.println("Application system currently unavailable, please contact admin if error persist!");
			return;
		}
		if (applicant.getApplication() != null){
			System.out.println("You have an application. Unable to view projects.");
			return;
		}
		else if (applicableProjects.size() == 0){
			System.out.println("You are uneligible to apply for any projects!");
			return;
		}
		while (true) {
            System.out.println("\n=========================================");
            System.out.println("              VIEW PROJECTS              ");
            System.out.println("=========================================");
			System.out.println("1. View all applicable projects");
			System.out.println("2. Search and filter for projects");
			System.out.println("0. Exit");

			int option;
			try {
				System.out.print("\nSelect an option: ");
				option = ScannerUtility.SCANNER.nextInt();
				ScannerUtility.SCANNER.nextLine(); 
				
				switch (option) {
					case 0:
						return;
					case 1:
						if (showApplicableProjects(applicableProjects, applicant)){
							return;
						}
						break;
					case 2:
						if (showApplicableFilteredProjects(applicableProjects, applicant)){
							return;
						}
					default:
						System.out.println("Invalid selection. Please try again.");
						break;
				}
			}
			catch (InputMismatchException e) {
				ScannerUtility.SCANNER.nextLine(); 
				System.out.println("Invalid selection. Please try again.");
			}
		}
	}
	public void showApplicationDetails(Applicant applicant){
		try{
			applicant.setApplication(Application.getApplicationByNricDB(applicant.getNric()));
		}
		catch (IOException e){
			System.out.println("Application system currently unavailable, please contact admin if error persist!");
			return;
		}
		Application application = applicant.getApplication();
		if (application == null){
			System.out.println("You do not have an application.");
			return;
		}
		ApplicantApplicationController applicantApplicationController = new ApplicantApplicationController();
		System.out.println("\n=========================================");
		System.out.println("           APPLICATION DETAILS           ");
		System.out.println("=========================================");
		System.out.println("Project Name: " + application.getProject().getProjectName());
		System.out.println("Project Neighbourhood: " + application.getProject().getNeighborhood());
		String flatTypeName = application.getFlatType();
		double flatTypePrice = application.getProject().getFlatTypes().stream()
			.filter(flatType -> flatType.getFlatType().equals(flatTypeName))
			.map(flatType -> flatType.getPricePerFlat())
			.findFirst()
			.orElse(0.0);
		System.out.println("Application Flat Type: " + flatTypeName + " ($" + flatTypePrice + ")");
		System.out.println("Application Status: " + application.getApplicationStatus());
		System.out.println("=========================================");
		int option = -1;
		if (application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_PENDING.getStatus()) ||
			(application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_SUCCESSFUL.getStatus()) || 
			(application.getApplicationStatus().equals(ApplicationStatus.WITHDRAWAL_UNSUCCESSFUL.getStatus())))
			) {
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
		else{
			while (true){
				try {
					System.out.println("Actions:");
					System.out.println("1. Withdraw Application");
					System.out.println("0. Back");
					System.out.print("\nEnter Option: ");
					option = ScannerUtility.SCANNER.nextInt();
					ScannerUtility.SCANNER.nextLine();
					if (option == 1 || option == 0){
						break;
					}
					System.out.println("Invalid option.");

				} catch (InputMismatchException e){
					ScannerUtility.SCANNER.nextLine(); 
					System.out.println("Invalid option.");
				}
			} 
			if (option == 0){
				return;
			}
			else if (option == 1){
				if (applicantApplicationController.withdrawApplication(application)){
					System.out.println("Application withdrawal sent.");
				}
				else{
					System.out.println("Application withdrawal not sent due to error,. if error persist, contact admin!");
				}
			}
		}
	}
}