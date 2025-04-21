package controllers;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.Date;
import java.text.ParseException;

import databases.ProjectDB;
import views.ManagerProjectView;
import models.Project;
import models.FlatType;
import models.HDBManager;

public class ManagerProjectController implements IManagerProjectController {

	private ManagerProjectView view;
	private Scanner scanner;
	private HDBManager loggedInManager;

	public ManagerProjectController() {
		this.view = new ManagerProjectView();
		this.scanner = new Scanner(System.in);
	}
	

	public void handleProjectMenu() {
		int choice;
	
		do {
			view.showProjectMenuHeader();
			choice = getValidChoice(0, 7);
	
			switch (choice) {
				case 1:
					createProject();
					break;
				case 2:
					ArrayList<Project> allProjects = getAllProjects();
					view.displayProjects(allProjects);
					break;
				case 3:
					ArrayList<Project> myProjects = getOwnedProjects();
					view.displayProjects(myProjects);
					break;
				case 4:
					searchProjects();
					break;
				case 5:
					editProjectMenu();
					break;
				case 6:
					toggleProjectVisibilityMenu();
					break;
				case 7:
					deleteProjectMenu();
					break;
				case 0:
					view.displaySuccess("Returning to main menu.");
					break;
				default:
					view.displayError("Invalid choice. Try again.");
			}
		} while (choice != 0);
	}
	

    private int getValidChoice(int min, int max) {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // Clear newline
        return choice;
    }

	@Override
	public void createProject() {
        try {
            System.out.print("Enter Project Name: ");
            String projectName = scanner.nextLine();

            System.out.print("Enter Neighborhood: ");
            String neighborhood = scanner.nextLine();

            System.out.print("Enter Application Opening Date (yyyy-MM-dd): ");
            String openingDateInput = scanner.nextLine();
            Date openingDate = new SimpleDateFormat("yyyy-MM-dd").parse(openingDateInput);

            System.out.print("Enter Application Closing Date (yyyy-MM-dd): ");
            String closingDateInput = scanner.nextLine();
            Date closingDate = new SimpleDateFormat("yyyy-MM-dd").parse(closingDateInput);

            System.out.print("Enter Number of Officer Slots: ");
            int officerSlots = scanner.nextInt();
            scanner.nextLine();

            ArrayList<FlatType> flatTypes = new ArrayList<>();
            System.out.println("Enter Flat Types:");
            while (true) {
                System.out.println("Select Flat Type:");
                System.out.println("1. 2-Room");
                System.out.println("2. 3-Room");
                System.out.println("0. Done");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 0) {
                    break;
                }

                String flatType;
                if (choice == 1) {
                    flatType = "2-Room";
                } else if (choice == 2) {
                    flatType = "3-Room";
                } else {
                    System.out.println("Invalid choice. Please try again.");
                    continue;
                }

                System.out.print("Enter Number of Units: ");
                int numUnits = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Enter Price per Flat: ");
                double pricePerFlat = scanner.nextDouble();
                scanner.nextLine();

                flatTypes.add(new FlatType(flatType, numUnits, pricePerFlat));
            }

            System.out.print("Enter Project Visibility (true/false): ");
            boolean visibility = scanner.nextBoolean();
            scanner.nextLine();

            if (loggedInManager == null) {
                throw new IllegalStateException("No manager is logged in. Cannot create project.");
            }

            // Generate ID based on existing projects
            int projectID = ProjectDB.getAllProjects().size() + 1;

            Project project = new Project(
                projectID,
                projectName,
                loggedInManager,
                neighborhood,
                flatTypes,
                openingDate,
                closingDate,
                officerSlots,
                visibility
            );

            if (ProjectDB.createProject(project)) {
                view.displaySuccess("Project created successfully!");
            } else {
                view.displayError("Failed to create project.");
            }

        } catch (IOException | ParseException e) {
            view.displayError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Edit an existing project
    @Override
    public void editProject(Project project) {
        try {
            ProjectDB.updateProject(project);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private void editProjectMenu() {
        try {
            System.out.print("Enter the name of the project to edit: ");
            String projectName = scanner.nextLine().trim();
        
            Project project = ProjectDB.getProjectByName(projectName);
            if (project == null) {
                view.displayError("No project found with the given name.");
                return;
            }
        
            // Verify the project belongs to the logged-in manager
            if (!project.getProjectManager().getNric().equals(loggedInManager.getNric())) {
                view.displayError("You can only edit projects you manage.");
                return;
            }
        
            System.out.println("Editing project: " + project.getProjectName());
            System.out.println("Leave field blank to keep current value.");
        
            System.out.print("New Project Name [" + project.getProjectName() + "]: ");
            String newName = scanner.nextLine();
            if (!newName.isBlank()) {
                project.setProjectName(newName);
            }
        
            System.out.print("New Neighborhood [" + project.getNeighborhood() + "]: ");
            String newNeighborhood = scanner.nextLine();
            if (!newNeighborhood.isBlank()) {
                project.setNeighborhood(newNeighborhood);
            }
        
            System.out.print("New Officer Slots [" + project.getOfficerSlots() + "]: ");
            String officerSlotsInput = scanner.nextLine();
            if (!officerSlotsInput.isBlank()) {
                try {
                    project.setOfficerSlots(Integer.parseInt(officerSlotsInput));
                } catch (NumberFormatException e) {
                    view.displayError("Invalid number format.");
                    return;
                }
            }
        
            if (ProjectDB.updateProject(project)) {
                view.displaySuccess("Project updated successfully.");
            } else {
                view.displayError("Failed to update project.");
            }
        } catch (IOException e) {
            view.displayError("Error: " + e.getMessage());
        }
    }

    // Toggle the visibility of a project
    @Override

	public void toggleProjectVisibility(Project project) {
		try {
			// Toggle the visibility
			boolean currentVisibility = project.getProjectVisibility();
			project.setProjectVisibility(!currentVisibility);

			// Save the updated project to the database
			ProjectDB.updateProject(project);

			// Display success message
			view.displaySuccess("Project visibility toggled to " + (project.getProjectVisibility() ? "Visible" : "Hidden") + ".");
		} catch (IOException e) {
			view.displayError("Failed to toggle project visibility: " + e.getMessage());
		}
	}

	private void toggleProjectVisibilityMenu() {
		System.out.print("Enter the name of the project to toggle visibility: ");
		String projectName = scanner.nextLine().trim();
	
		ArrayList<Project> projects = getSpecificProject(projectName);
	
		if (projects.isEmpty()) {
			view.displayError("No project found with the given name.");
			return;
		}
	
		Project project = projects.get(0); // Assuming only one project matches the name
		boolean currentVisibility = project.getProjectVisibility();
		project.setProjectVisibility(!currentVisibility);
	
		try {
			ProjectDB.updateProject(project); // Save the updated project to the database
			view.displaySuccess("Project visibility toggled to " + (project.getProjectVisibility() ? "Visible" : "Hidden") + ".");
		} catch (IOException e) {
			view.displayError("Failed to toggle project visibility: " + e.getMessage());
		}
	}

    // Get all projects
    @Override
    public ArrayList<Project> getAllProjects() {
        try {
            return ProjectDB.getAllProjects();
        } catch (IOException e) {
            view.displayError("Error loading projects: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get all projects owned by a manager
    @Override
    public ArrayList<Project> getOwnedProjects() {
        try {
            if (loggedInManager == null) {
                return new ArrayList<>();
            }
            return ProjectDB.getProjectsByManager(loggedInManager.getNric()); // Changed to use name
        } catch (IOException e) {
            view.displayError("Error loading projects: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get filtered projects based on a filter list
    @Override
    public ArrayList<Project> getFilteredProjects(List<String> filter) {
        try {
            ArrayList<Project> allProjects = ProjectDB.getAllProjects();
            ArrayList<Project> filteredProjects = new ArrayList<>();
            for (Project project : allProjects) {
                // Implement filtering logic based on the filter list
                // For example, filtering based on neighborhood or project name
                if (filter.contains(project.getNeighborhood())) {
                    filteredProjects.add(project);
                }
            }
            return filteredProjects;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Get a specific project by name
    @Override
    public ArrayList<Project> getSpecificProject(String projectName) {
        try {
            ArrayList<Project> allProjects = ProjectDB.getAllProjects();
            ArrayList<Project> specificProjects = new ArrayList<>();
            for (Project project : allProjects) {
                if (project.getProjectName().equalsIgnoreCase(projectName)) {
                    specificProjects.add(project);
                }
            }
            return specificProjects;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Delete a project
    @Override
    public void deleteProject(Project project) {
        try {
            ProjectDB.deleteProject(project.getProjectName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private void deleteProjectMenu() {
		System.out.print("Enter the name of the project to delete: ");
		String projectName = scanner.nextLine();
		ArrayList<Project> projects = getSpecificProject(projectName);
	
		if (projects.isEmpty()) {
			view.displayError("No project found with the given name.");
			return;
		}
	
		Project project = projects.get(0); // Assuming only one project matches the name
		System.out.print("Are you sure you want to delete this project? (yes/no): ");
		String confirmation = scanner.nextLine();
	
		if (confirmation.equalsIgnoreCase("yes")) {
			deleteProject(project);
			view.displaySuccess("Project deleted successfully.");
		} else {
			view.displaySuccess("Project deletion canceled.");
		}
	}

	public void setLoggedInManager(HDBManager manager) {
		this.loggedInManager = manager;
	}

	private void searchProjects() {
		System.out.print("Enter search keyword (e.g., project name or neighborhood): ");
		String keyword = scanner.nextLine().trim().toLowerCase(); // Normalize input for case-insensitive search
	
		ArrayList<Project> allProjects = getAllProjects();
		ArrayList<Project> filteredProjects = new ArrayList<>();
	
		for (Project project : allProjects) {
			// Check if the keyword matches the project name, neighborhood, or flat types
			if (project.getProjectName().toLowerCase().contains(keyword) ||
				project.getNeighborhood().toLowerCase().contains(keyword) ||
				project.getFlatTypes().stream().anyMatch(ft -> ft.getFlatType().toLowerCase().contains(keyword))) {
				filteredProjects.add(project);
			}
		}
	
		if (filteredProjects.isEmpty()) {
			view.displayError("No projects found matching the keyword.");
		} else {
			view.displayProjects(filteredProjects);
		}
	}
}
