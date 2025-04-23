package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import databases.ProjectDB;
import views.ManagerProjectView;
import models.Project;
import utilities.ScannerUtility;
import models.HDBManager;

public class ManagerProjectController implements IManagerProjectController {

    @Override
    public ArrayList<Project> getFilteredProjects(List<String> filters) {
        ArrayList<Project> allProjects = getAllProjects();
        ArrayList<Project> filteredProjects = new ArrayList<>();

        for (Project project : allProjects) {
            boolean matches = filters.stream().allMatch(filter -> 
                project.getProjectName().toLowerCase().contains(filter.toLowerCase()) ||
                project.getNeighborhood().toLowerCase().contains(filter.toLowerCase()) ||
                project.getFlatTypes().stream().anyMatch(ft -> ft.getFlatType().toLowerCase().contains(filter.toLowerCase()))
            );
            if (matches) {
                filteredProjects.add(project);
            }
        }

        return filteredProjects;
    }

    private ManagerProjectView view;
    private HDBManager loggedInManager;

    public ManagerProjectController() {
        this.view = new ManagerProjectView();
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
                    // Display the list of projects and allow the user to select and edit one
                    ArrayList<Project> allProjectsToEdit = getAllProjects();
                    if (allProjectsToEdit.isEmpty()) {
                        view.displayError("No projects available to edit.");
                    } else {
                        // Let the view handle the selection and editing process
                        Project selectedProject = view.editProjectMenu(allProjectsToEdit); // Returns the edited project
                        if (selectedProject != null) {
                            try {
                                // Update the project in the database
                                if (ProjectDB.updateProject(selectedProject)) {
                                    view.displaySuccess("Project updated successfully and saved to storage.");
                                } else {
                                    view.displayError("Failed to update project in storage.");
                                }
                            } catch (IOException e) {
                                view.displayError("Error saving project: " + e.getMessage());
                            }
                        }
                    }
                    break;
                case 6:
                    ArrayList<Project> allProjectsForVisibility = getAllProjects();
                    view.toggleProjectVisibilityMenu(allProjectsForVisibility);
                    break;
                case 7:
                    ArrayList<Project> allProjectsToDelete = getAllProjects();
                    if (allProjectsToDelete.isEmpty()) {
                        view.displayError("No projects available to delete.");
                    } else {
                        // Call the view method to handle the deletion process
                       deleteProjectView(allProjectsToDelete);
                    }
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
        while (!ScannerUtility.SCANNER.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            ScannerUtility.SCANNER.next();
        }
        int choice = ScannerUtility.SCANNER.nextInt();
        ScannerUtility.SCANNER.nextLine(); // Clear newline
        return choice;
    }

    @Override
    public void createProject() {
        try {
            Project project = view.createNewProjectMenu();
            if (project == null) {
                view.displayError("Project creation canceled.");
                return;
            }

            if (loggedInManager == null) {
                throw new IllegalStateException("No manager is logged in. Cannot create project.");
            }

            // Assign the logged-in manager to the project
            project.setProjectManager(loggedInManager);

            // Generate ID based on existing projects
            int projectID = ProjectDB.getAllProjects().size() + 1;
            project.setProjectID(projectID);

            if (ProjectDB.createProject(project)) {
                view.displaySuccess("Project created successfully!");
            } else {
                view.displayError("Failed to create project.");
            }

        } catch (IOException e) {
            view.displayError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void editProject(Project project) {
        try {
            ArrayList<Project> projectList = new ArrayList<>();
            projectList.add(project);
            view.editProjectMenu(projectList);

            if (ProjectDB.updateProject(project)) {
                view.displaySuccess("Project updated successfully.");
            } else {
                view.displayError("Failed to update project.");
            }
        } catch (IOException e) {
            view.displayError("Error: " + e.getMessage());
        }
    }


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

    @Override
    public ArrayList<Project> getAllProjects() {
        try {
            return ProjectDB.getAllProjects();
        } catch (IOException e) {
            view.displayError("Error loading projects: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public ArrayList<Project> getOwnedProjects() {
        try {
            if (loggedInManager == null) {
                return new ArrayList<>();
            }
            return ProjectDB.getProjectsByManager(loggedInManager.getNric());
        } catch (IOException e) {
            view.displayError("Error loading projects: " + e.getMessage());
            return new ArrayList<>();
        }
    }

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

    @Override
    public void deleteProject(Project project) {
        try {
            ProjectDB.deleteProject(project.getProjectName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteProjectView(ArrayList<Project> projects) {
        // Display the list of projects
        System.out.println("\n=========================================");
        System.out.println("           AVAILABLE PROJECTS            ");
        System.out.println("=========================================");
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            System.out.printf("%d. %s (Neighborhood: %s)\n",
                    i + 1,
                    project.getProjectName(),
                    project.getNeighborhood(),
                    project.getApplicationOpeningDate());
        }
        System.out.println("0. Back");
        System.out.println("=========================================");
        int choice;
        while (true){
            // Prompt the user to select a project
            try{
                System.out.print("\nEnter the project number to delete: ");
                choice = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); 
                if (choice == 0) {
                    view.displayInfo("Delete canceled.");
                    return;
                }
        
                if (choice < 1 || choice > projects.size()) {
                    view.displayError("Invalid selection. Please try again.");
                    continue;
                }
                break;
            }
            catch (InputMismatchException e){
                ScannerUtility.SCANNER.nextLine(); 
                view.displayError("Invalid selection. Please try again.");
            }
        }
        while (true){
            // Prompt the user to confirm
            int confirm;
            try{
                System.out.println("\nConfirm the deletion of " + projects.get(choice - 1).getProjectName() + "?");
                System.out.println("1. Yes");
                System.out.println("2. No");
                confirm = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); 
                if (confirm == 1) {
                    deleteProject(projects.get(choice - 1));
                    view.displaySuccess("Project deleted successfully.");
                    break;
                }
        
                if (confirm == 2) {
                    view.displayInfo("Delete canceled.");
                    return;
                }
                view.displayError("Invalid selection. Please try again.");
            }
            catch (InputMismatchException e){
                ScannerUtility.SCANNER.nextLine(); // Consume newline
                view.displayError("Invalid selection. Please try again.");
            }
        }
    }

    private void searchProjects() {
        System.out.print("Enter search keyword (e.g., project name or neighborhood): ");
        String keyword = ScannerUtility.SCANNER.nextLine().trim().toLowerCase();

        ArrayList<Project> allProjects = getAllProjects();
        ArrayList<Project> filteredProjects = new ArrayList<>();

        for (Project project : allProjects) {
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

    public void setLoggedInManager(HDBManager manager) {
        this.loggedInManager = manager;
    }

}