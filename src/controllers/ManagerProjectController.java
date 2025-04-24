package controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Set;
import databases.ProjectDB;
import enums.FilterIndex;
import enums.FlatTypeName;
import views.ManagerProjectView;
import models.Project;
import utilities.LoggerUtility;
import utilities.ScannerUtility;
import models.FlatType;
import models.HDBManager;

/**
 * The ManagerProjectController class handles the project management functionalities for HDB managers.
 * It allows managers to create, edit, delete, and filter projects, as well as manage flat types within those projects.
 */

public class ManagerProjectController implements IManagerProjectController {

    /**
     * Filters the projects based on the provided filters.
     *
     * @param filters The list of filters to apply.
     * @return A list of filtered projects.
     */
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


    /**
     * Constructor for ManagerProjectController.
     * Initializes the view for project management.
     */
    public ManagerProjectController() {
        this.view = new ManagerProjectView();
    }

    /**
     * Displays the project management menu and handles user input for various project-related actions.
     */
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
                        Project selectedProject = editProjectMenu(allProjectsToEdit); // Returns the edited project
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
                    toggleProjectVisibilityMenu(allProjectsForVisibility);
                    break;
                case 7:
                    ArrayList<Project> allProjectsToDelete = getAllProjects();
                    if (allProjectsToDelete.isEmpty()) {
                        view.displayError("No projects available to delete.");
                    } else {
                        // Pass 'this' as the controller parameter
                        deleteProjectView(allProjectsToDelete, this);

                    }
                    break;
                case 8: // New case for filtering projects
                    ArrayList<Project> allProjectsToFilter = getAllProjects();
                    if (allProjectsToFilter.isEmpty()) {
                        view.displayError("No projects available to filter.");
                    } else {
                        boolean filterApplied = filterProjectsMenu(allProjectsToFilter);
                        if (filterApplied) {
                            view.displaySuccess("Filters applied successfully.");
                        }
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

    /**
     * Prompts the user for a valid choice within the specified range.
     *
     * @param min The minimum valid choice.
     * @param max The maximum valid choice.
     * @return The user's choice as an integer.
     */
    private int getValidChoice(int min, int max) {
        while (!ScannerUtility.SCANNER.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            ScannerUtility.SCANNER.next();
        }
        int choice = ScannerUtility.SCANNER.nextInt();
        ScannerUtility.SCANNER.nextLine(); // Clear newline
        return choice;
    }

    /**
     * Creates a new project instance and passes to database storage method.
     * If the project creation is successful, a success message is displayed.
     */
    @Override
    public void createProject() {
        try {
            Project project = createNewProjectMenu();
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
                view.displaySuccess("Saving project to storage...");
            } else {
                view.displayError("Failed to create project.");
            }

        } catch (IOException e) {
            view.displayError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Edits an existing project and passes to database storage method.
     *
     * @param project The project to edit.
     */

    @Override
    public void editProject(Project project) {
        try {
            ArrayList<Project> projectList = new ArrayList<>();
            projectList.add(project);
            editProjectMenu(projectList);

            if (ProjectDB.updateProject(project)) {
                view.displaySuccess("Project updated successfully.");
            } else {
                view.displayError("Failed to update project.");
            }
        } catch (IOException e) {
            view.displayError("Error: " + e.getMessage());
        }
    }

    /**
     * Toggles the visibility of a project and passes it to the database storage method.
     *
     * @param project The project whose visibility to toggle.
     */
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

    /**
     * Retrieves all projects from the database without duplicates.
     *
     * @return A list of unique projects.
     */

    @Override
    public ArrayList<Project> getAllProjects() {
        try {
            ArrayList<Project> allProjects = ProjectDB.getAllProjects();
            // Remove duplicates by converting to a Set and back to a List
            ArrayList<Project> uniqueProjects = new ArrayList<>(new HashSet<>(allProjects));
            // Sort projects by ID
            uniqueProjects.sort((p1, p2) -> Integer.compare(p1.getProjectID(), p2.getProjectID()));
            return uniqueProjects;
        } catch (IOException e) {
            view.displayError("Error loading projects: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    /**
     * Retrieves all projects owned by the logged-in manager.
     *
     * @return A list of projects owned by the logged-in manager.
     */
    @Override
    public ArrayList<Project> getOwnedProjects() {
        try {
            if (loggedInManager == null) {
                return new ArrayList<>();
            }
            return ProjectDB.getProjectsByManager(loggedInManager.getName());
        } catch (IOException e) {
            view.displayError("Error loading projects: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves a specific project by its name.
     *
     * @param projectName The name of the project to retrieve.
     * @return A list of projects matching the specified name.
     */

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

    /**
     * Deletes a project from the database.
     *
     * @param project The project to delete.
     */

    @Override
    public void deleteProject(Project project) {
        try {
            ProjectDB.deleteProject(project);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a menu for deleting a project and handles user input for deletion confirmation.
     *
     * @param projects The list of projects to choose from for deletion.
     */
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
                    project.getNeighborhood());
        }
        System.out.println("0. Back");
        System.out.println("=========================================");
    
        int choice;
        while (true) {
            // Prompt the user to select a project
            try {
                System.out.print("\nEnter the project number to delete: ");
                choice = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                if (choice == 0) {
                    view.displayInfo("Delete canceled.");
                    return;
                }
    
                if (choice >= 1 && choice <= projects.size()) {
                    break;
                } else {
                    view.displayError("Invalid selection. Please try again.");
                }
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                view.displayError("Invalid input. Please enter a valid number.");
                continue;
            }
        }
    
        while (true) {
            // Prompt the user to confirm
            int confirm;
            try {
                System.out.println("\nConfirm the deletion of " + projects.get(choice - 1).getProjectName() + "?");
                System.out.println("1. Yes");
                System.out.println("2. No");
                System.out.print("Enter your choice: ");
                confirm = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                if (confirm == 1) {
                    deleteProject(projects.get(choice - 1));
                    view.displaySuccess("Project deleted successfully.");
                    break;
                } else if (confirm == 2) {
                    view.displayInfo("Delete canceled.");
                    return;
                } else {
                    view.displayError("Invalid selection. Please try again.");
                }
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                view.displayError("Invalid input. Please enter 1 or 2.");
            }
        }
    }

    /**
     * Searches for projects based on user input and displays the results.
     */
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

    /**
     * Sets the logged-in manager for the controller.
     *
     * @param manager The logged-in HDBManager instance.
     */
    public void setLoggedInManager(HDBManager manager) {
        this.loggedInManager = manager;
    }

    /**
     * Retrieves the logged-in manager.
     *
     * @return The logged-in HDBManager instance.
     */
    public static void filterProject(ArrayList<Project> projects, List<String> filters) {
        // Filter by Project Name
        if (!filters.get(FilterIndex.PROJECT_NAME.getIndex()).isEmpty()) {
            String projectNameFilter = filters.get(FilterIndex.PROJECT_NAME.getIndex()).toLowerCase();
            projects.removeIf(project -> !project.getProjectName().toLowerCase().contains(projectNameFilter));
        }
    
        // Filter by Neighborhood
        if (!filters.get(FilterIndex.NEIGHBOURHOOD.getIndex()).isEmpty()) {
            String neighborhoodFilter = filters.get(FilterIndex.NEIGHBOURHOOD.getIndex()).toLowerCase();
            projects.removeIf(project -> !project.getNeighborhood().toLowerCase().contains(neighborhoodFilter));
        }
    
        // Filter by Minimum Price
        if (!filters.get(FilterIndex.PRICE_START.getIndex()).isEmpty()) {
            double minPrice = Double.parseDouble(filters.get(FilterIndex.PRICE_START.getIndex()));
            projects.removeIf(project -> project.getFlatTypes().stream()
                    .noneMatch(flatType -> flatType.getPricePerFlat() >= minPrice));
        }
    
        // Filter by Maximum Price
        if (!filters.get(FilterIndex.PRICE_END.getIndex()).isEmpty()) {
            double maxPrice = Double.parseDouble(filters.get(FilterIndex.PRICE_END.getIndex()));
            projects.removeIf(project -> project.getFlatTypes().stream()
                    .noneMatch(flatType -> flatType.getPricePerFlat() <= maxPrice));
        }
    
        // Filter by Flat Type
        if (!filters.get(FilterIndex.FLAT_TYPE.getIndex()).isEmpty()) {
            String flatTypeFilter = filters.get(FilterIndex.FLAT_TYPE.getIndex()).toLowerCase();
            projects.removeIf(project -> project.getFlatTypes().stream()
                    .noneMatch(flatType -> flatType.getFlatType().toLowerCase().contains(flatTypeFilter)));
        }
    }

    /**
     * Prompts the user to create a new project and returns the created project.
     *
     * @return The created Project object or null if the creation was canceled.
     */

    public Project createNewProjectMenu() {
        System.out.println("\n=========================================");
        System.out.println("           CREATE NEW PROJECT            ");
        System.out.println("=========================================");
    
        System.out.print("Enter Project Name: ");
        String projectName = ScannerUtility.SCANNER.nextLine();
    
        System.out.print("Enter Neighborhood: ");
        String neighborhood = ScannerUtility.SCANNER.nextLine();
    
        System.out.print("Enter Opening Date (yyyy-MM-dd): ");
        Date openingDate = getDateInput();
        if (openingDate == null) {
            view.displayError("Invalid date format. Please try again.");
            return null;
        }
    
        System.out.print("Enter Application Closing Date (yyyy-MM-dd): ");
        Date closingDate = getDateInput();
        if (closingDate == null || closingDate.before(openingDate)) {
            view.displayError("Invalid date format or closing date is before opening date. Please try again.");
            return null;
        }
    
        System.out.print("Enter Officer Slots: ");
        int officerSlots = ScannerUtility.SCANNER.nextInt();
        ScannerUtility.SCANNER.nextLine(); 
        

        ArrayList<FlatType> flatTypes = new ArrayList<>();
        editFlatTypes(flatTypes);
    
        System.out.println("\n=========================================");
        System.out.println("           PROJECT SUMMARY               ");
        System.out.println("=========================================");
        System.out.println("Project Name: " + projectName);
        System.out.println("Neighborhood: " + neighborhood);
        System.out.println("Opening Date: " + openingDate);
        System.out.println("Application Closing Date: " + closingDate);
        System.out.println("Officer Slots: " + officerSlots);
        System.out.println("Flat Types:");
        for (FlatType flatType : flatTypes) {
            System.out.println("- " + flatType.getFlatType() + ": " + flatType.getNumFlats() + " units at $" + flatType.getPricePerFlat());
        }
        System.out.print("Confirm project creation? (yes/no): ");
        String confirm = ScannerUtility.SCANNER.nextLine().trim().toLowerCase();
    
        if (!confirm.equals("yes")) {
            view.displayInfo("Project creation aborted.");
            return null;
        }
    
        System.out.println("=========================================");
        System.out.println("Project created successfully!");
        LoggerUtility.logInfo("New project created: " + projectName);
    
        return new Project(0, projectName, null, neighborhood, flatTypes, openingDate, closingDate, officerSlots, true);
    }

    /**
     * Displays a menu for editing an existing project and handles user input for project details.
     *
     * @param projects The list of projects to choose from for editing.
     * @return The edited Project object or null if the editing was canceled.
     */

    public Project editProjectMenu(ArrayList<Project> projects) {
        if (projects.isEmpty()) {
            view.displayError("No projects available to edit.");
            return null;
        }
    
        System.out.println("\n=========================================");
        System.out.println("           AVAILABLE PROJECTS            ");
        System.out.println("=========================================");
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            System.out.printf("%d. %s (Neighborhood: %s)\n",
                    i + 1,
                    project.getProjectName(),
                    project.getNeighborhood());
        }
        System.out.println("0. Cancel");
        System.out.println("=========================================");
    
        // Prompt the user to select a project
        System.out.print("Enter the number of the project to edit: ");
        int choice = ScannerUtility.SCANNER.nextInt();
        ScannerUtility.SCANNER.nextLine(); // Consume newline
    
        if (choice == 0) {
            view.displayInfo("Edit operation canceled.");
            return null;
        }
    
        if (choice < 1 || choice > projects.size()) {
            view.displayError("Invalid selection. Please try again.");
            return null;
        }
    
        // Get the selected project
        Project selectedProject = projects.get(choice - 1);
    
        // Edit the selected project
        System.out.println("\n=========================================");
        System.out.println("           EDIT PROJECT DETAILS          ");
        System.out.println("=========================================");
    
        System.out.print("Enter new Project Name (current: " + selectedProject.getProjectName() + "): ");
        String newName = ScannerUtility.SCANNER.nextLine();
        if (!newName.isBlank()) {
            selectedProject.setProjectName(newName);
            LoggerUtility.logInfo("Project name updated to: " + newName);
        }
    
        System.out.print("Enter new Neighborhood (current: " + selectedProject.getNeighborhood() + "): ");
        String newNeighborhood = ScannerUtility.SCANNER.nextLine();
        if (!newNeighborhood.isBlank()) {
            selectedProject.setNeighborhood(newNeighborhood);
            LoggerUtility.logInfo("Neighborhood updated to: " + newNeighborhood);
        }
    
        System.out.print("Enter new Opening Date (yyyy-MM-dd) (current: " + selectedProject.getApplicationOpeningDate() + "): ");
        Date newOpeningDate = getDateInput();
        if (newOpeningDate != null) {
            selectedProject.setApplicationOpeningDate(newOpeningDate);
            LoggerUtility.logInfo("Opening date updated to: " + newOpeningDate);
        }
    
        System.out.print("Enter new Application Closing Date (yyyy-MM-dd) (current: " + selectedProject.getApplicationClosingDate() + "): ");
        Date newClosingDate = getDateInput();
        if (newClosingDate != null && newClosingDate.after(selectedProject.getApplicationOpeningDate())) {
            selectedProject.setApplicationClosingDate(newClosingDate);
            LoggerUtility.logInfo("Closing date updated to: " + newClosingDate);
        } else if (newClosingDate != null) {
            view.displayError("Closing date must be after the opening date. Update failed.");
        }
    
        System.out.print("Enter new Officer Slots (current: " + selectedProject.getOfficerSlots() + "): ");
        String officerSlotsInput = ScannerUtility.SCANNER.nextLine();
        if (!officerSlotsInput.isBlank()) {
            try {
                int newOfficerSlots = Integer.parseInt(officerSlotsInput);
                selectedProject.setOfficerSlots(newOfficerSlots);
                LoggerUtility.logInfo("Officer slots updated to: " + newOfficerSlots);
            } catch (NumberFormatException e) {
                view.displayError("Invalid number format. Update failed.");
            }
        }
    
        // Keep the manager name static
        selectedProject.setProjectManager(loggedInManager);
        System.out.println("\nEditing Flat Types...");
        editFlatTypes(selectedProject.getFlatTypes());
    
        // Confirm changes
        System.out.println("\n=========================================");
        System.out.println("           UPDATED PROJECT DETAILS       ");
        System.out.println("=========================================");
        System.out.println("Project Name: " + selectedProject.getProjectName());
        System.out.println("Neighborhood: " + selectedProject.getNeighborhood());
        System.out.println("Opening Date: " + selectedProject.getApplicationOpeningDate());
        System.out.println("Application Closing Date: " + selectedProject.getApplicationClosingDate());
        System.out.println("Officer Slots: " + selectedProject.getOfficerSlots());
        System.out.println("Flat Types:");
        for (FlatType flatType : selectedProject.getFlatTypes()) {
            System.out.println("- " + flatType.getFlatType() + ": " + flatType.getNumFlats() + " units at $" + flatType.getPricePerFlat());
        }
        System.out.println("Manager Name: " + selectedProject.getProjectManager().getName());
        System.out.print("Confirm changes? (yes/no): ");
        String confirm = ScannerUtility.SCANNER.nextLine().trim().toLowerCase();
    
        if (!confirm.equals("yes")) {
            view.displayInfo("Changes discarded.");
            return null;
        } else {
            try {
                if (ProjectDB.updateProject(selectedProject)) {
                    view.displaySuccess("Project updated successfully!");
                    return selectedProject;
                } else {
                    view.displayError("Failed to update project. Please try again.");
                    return null;
                }
            } catch (IOException e) {
                view.displayError("An error occurred while updating the project: " + e.getMessage());
                LoggerUtility.logError("Failed to update project: " + selectedProject.getProjectName(), e);
                return null;
            }
        }
    }

    /**
     * Displays a menu for editing flat types and handles user input for adding, editing, or removing flat types.
     *
     * @param flatTypes The list of flat types to edit.
     */

    private void editFlatTypes(ArrayList<FlatType> flatTypes) {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("           EDIT FLAT TYPES               ");
            System.out.println("=========================================");
            System.out.println("1. Add Flat Type");
            System.out.println("2. Edit Existing Flat Type");
            System.out.println("3. Remove Flat Type");
            System.out.println("0. Exit");
            System.out.println("=========================================");
            System.out.print("Enter your choice: ");
    
            int choice;
            try {
                choice = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine(); // Clear invalid input
                System.out.println("[ERROR] Invalid input. Please enter a valid number.");
                continue;
            }
    
            switch (choice) {
                case 1: // Add Flat Type
                    FlatType FT1 = new FlatType();
                    FT1.addFlatType(flatTypes);
                    break;
    
                case 2: // Edit Existing Flat Type
                    FlatType FT2 = new FlatType();
                    FT2.editExistingFlatType(flatTypes);
                    break;
    
                case 3: // Remove Flat Type
                    FlatType FT3 = new FlatType();
                    FT3.removeFlatType(flatTypes);
                    break;
    
                case 0: // Exit
                    System.out.println("Exiting Flat Types Editor...");
                    return;
    
                default:
                    System.out.println("[ERROR] Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Toggles the visibility of a project and updates it in the database.
     *
     * @param projects The list of projects to choose from for toggling visibility.
     */
    public void toggleProjectVisibilityMenu(ArrayList<Project> projects) {
        if (projects.isEmpty()) {
            view.displayError("No projects available to toggle visibility.");
            return;
        }

        // Display the list of projects
        view.displayProjects(projects);

        // Prompt the user to select a project
        System.out.print("Enter the number of the project to toggle visibility: ");
        int choice = ScannerUtility.SCANNER.nextInt();
        ScannerUtility.SCANNER.nextLine(); // Consume newline

        if (choice == 0) {
            view.displayInfo("Toggle visibility operation canceled.");
            return;
        }

        if (choice < 1 || choice > projects.size()) {
            view.displayError("Invalid selection. Please try again.");
            return;
        }

        // Get the selected project
        Project selectedProject = projects.get(choice - 1);

        // Toggle visibility
        boolean currentVisibility = selectedProject.getProjectVisibility();
        selectedProject.setProjectVisibility(!currentVisibility);

        // Persist the change to the database
        try {
            if (ProjectDB.updateProject(selectedProject)) {
                view.displaySuccess("Project visibility toggled to " + (selectedProject.getProjectVisibility() ? "Visible" : "Hidden") + ".");
            } else {
                view.displayError("Failed to update project visibility in the database.");
            }
        } catch (IOException e) {
            view.displayError("Error updating project visibility: " + e.getMessage());
        }
    }
    
    /**
     * Deletes a project from the database and updates the view accordingly.
     *
     * @param projects The list of projects to choose from for deletion.
     * @param controller The controller instance to handle deletion logic.
     */
    public void deleteProjectView(ArrayList<Project> projects, ManagerProjectController controller) {
        if (projects.isEmpty()) {
            view.displayError("No projects found to delete.");
            return;
        }
    
        // Display the details of the projects found
        System.out.println("\nProjects found:");
        view.displayProjects(projects); // Assuming you have a method to display project details
    
        // Prompt for confirmation
        System.out.print("Enter the index of the project to delete (1-based): ");
        int indexToDelete = -1;
    
        while (true) {
            if (ScannerUtility.SCANNER.hasNextInt()) {
                indexToDelete = ScannerUtility.SCANNER.nextInt() - 1; // Convert to 0-based index
                ScannerUtility.SCANNER.nextLine(); // Consume newline
    
                if (indexToDelete >= 0 && indexToDelete < projects.size()) {
                    break; // Valid index
                } else {
                    System.out.println("[ERROR] Invalid index. Please try again.");
                    continue;
                }
            } else {
                System.out.println("[ERROR] Please enter a valid number.");
                ScannerUtility.SCANNER.nextLine(); // Clear invalid input
            }
        }
    
        Project projectToDelete = projects.get(indexToDelete);
        System.out.print("Are you sure you want to delete the project '" + projectToDelete.getProjectName() + "'? (yes/no): ");
        String confirmation = ScannerUtility.SCANNER.nextLine();
    
        if (confirmation.equalsIgnoreCase("yes")) {
            controller.deleteProject(projectToDelete); // Call the controller method to delete the project
            view.displaySuccess("Project '" + projectToDelete.getProjectName() + "' deleted successfully.");
        } else {
            view.displaySuccess("Project deletion canceled.");
        }
    }


    /**
     * Prompts the user to enter a date in the format yyyy-MM-dd and parses it.
     *
     * @return A valid Date object or null if the input is invalid.
     */
    private Date getDateInput() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false); // Ensure strict date parsing

        try {
            String dateInput = ScannerUtility.SCANNER.nextLine();
            return dateFormat.parse(dateInput);
        } catch (ParseException e) {
            LoggerUtility.logError("Invalid date format entered: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Displays a menu for filtering projects and allows users to define and store filtering preferences.
     *
     * @param allProjects The list of all projects to filter.
     * @return true if the user successfully filters and views projects, false otherwise.
     */
    public boolean filterProjectsMenu(ArrayList<Project> allProjects) {
        List<String> filters = new ArrayList<>(List.of("", "", "", "", "")); // Initialize empty filters
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("              FILTER PROJECTS            ");
            System.out.println("=========================================");
            System.out.println("1. Filter by Project Name");
            System.out.println("2. Filter by Neighborhood");
            System.out.println("3. Filter by Minimum Price");
            System.out.println("4. Filter by Maximum Price");
            System.out.println("5. Filter by Flat Type");
            System.out.println("6. View Current Filters");
            System.out.println("7. Clear All Filters");
            System.out.println("8. Apply Filters and Sort by Name");
            System.out.println("9. Apply Filters and Sort by Neighborhood");
            System.out.println("10. Apply Filters and Sort by Price");
            System.out.println("0. Exit");
            System.out.println("=========================================");
            System.out.print("\nSelect an option: ");
    
            int option = -1;
            try {
                option = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                view.displayError("Invalid selection. Please try again.");
                continue;
            }
    
            ArrayList<Project> projectsToFilter = new ArrayList<>(allProjects);
            switch (option) {
                case 0:
                    return false;
                case 1:
                    filters.set(FilterIndex.PROJECT_NAME.getIndex(), promptProjectNameFilter(allProjects));
                    break;
                    case 2:
                    filters.set(FilterIndex.NEIGHBOURHOOD.getIndex(), promptNeighbourhoodFilter(allProjects));
                    break;
                case 3: {
                    Double price = promptMinPriceFilter();
                    if (price == 0) {
                        filters.set(FilterIndex.PRICE_START.getIndex(), "");
                    } else if (price > 0) {
                        filters.set(FilterIndex.PRICE_START.getIndex(), String.format("%.2f", price));
                    }
                    break;
                }
                case 4: {
                    Double price = promptMaxPriceFilter();
                    if (price == 0) {
                        filters.set(FilterIndex.PRICE_END.getIndex(), "");
                    } else if (price > 0) {
                        filters.set(FilterIndex.PRICE_END.getIndex(), String.format("%.2f", price));
                    }
                    break;
                }
                case 5: {
                    int selectedFlat = promptFlatTypeFilter();
                    if (selectedFlat == 1) {
                        filters.set(FilterIndex.FLAT_TYPE.getIndex(), FlatTypeName.TWO_ROOM.getflatTypeName());
                    } else if (selectedFlat == 2) {
                        filters.set(FilterIndex.FLAT_TYPE.getIndex(), FlatTypeName.THREE_ROOM.getflatTypeName());
                    } else if (selectedFlat == 3) {
                        filters.set(FilterIndex.FLAT_TYPE.getIndex(), "");
                    }
                    break;
                }
                case 6: // View Current Filters
                    displayCurrentFilters(filters);
                    break;
                case 7:
                    filters.clear();
                    filters.addAll(List.of("", "", "", "", ""));
                    view.displayInfo("Filters cleared.");
                    break;
                case 8:
                    Project.filterProject(projectsToFilter, filters);
                    if (projectsToFilter.isEmpty()) {
                        view.displayInfo("No results found.");
                    } else {
                        Project.sortProjectByName(projectsToFilter);
                        view.displayProjects(projectsToFilter);
                    }
                    break;
                case 9:
                    Project.filterProject(projectsToFilter, filters);
                    if (projectsToFilter.isEmpty()) {
                        view.displayInfo("No results found.");
                    } else {
                        Project.sortProjectByNeighbourhood(projectsToFilter);
                        view.displayProjects(projectsToFilter);
                    }
                    break;
                case 10:
                    Project.filterProject(projectsToFilter, filters);
                    if (projectsToFilter.isEmpty()) {
                        view.displayInfo("No results found.");
                    } else {
                        int order = promptSortOrder();
                        if (order == 1) {
                            Project.sortProjectByPrice(projectsToFilter, true);
                        } else if (order == 2) {
                            Project.sortProjectByPrice(projectsToFilter, false);
                        } else {
                            view.displayError("Invalid selection. Please try again.");
                            break;
                        }
                        view.displayProjects(projectsToFilter);
                    }
                    break;
                default:
                    view.displayError("Invalid selection. Please try again.");
                    break;
            }
        }
    }

    /**
     * Displays the current filters applied to the project list.
     *
     * @param filters The list of filters currently applied.
     */
    
    private void displayCurrentFilters(List<String> filters) {
        System.out.println("\n=========================================");
        System.out.println("           CURRENT FILTERS               ");
        System.out.println("=========================================");
        System.out.println("1. Project Name: " + (filters.get(FilterIndex.PROJECT_NAME.getIndex()).isEmpty() ? "Not Set" : filters.get(FilterIndex.PROJECT_NAME.getIndex())));
        System.out.println("2. Neighborhood: " + (filters.get(FilterIndex.NEIGHBOURHOOD.getIndex()).isEmpty() ? "Not Set" : filters.get(FilterIndex.NEIGHBOURHOOD.getIndex())));
        System.out.println("3. Minimum Price: " + (filters.get(FilterIndex.PRICE_START.getIndex()).isEmpty() ? "Not Set" : filters.get(FilterIndex.PRICE_START.getIndex())));
        System.out.println("4. Maximum Price: " + (filters.get(FilterIndex.PRICE_END.getIndex()).isEmpty() ? "Not Set" : filters.get(FilterIndex.PRICE_END.getIndex())));
        System.out.println("5. Flat Type: " + (filters.get(FilterIndex.FLAT_TYPE.getIndex()).isEmpty() ? "Not Set" : filters.get(FilterIndex.FLAT_TYPE.getIndex())));
        System.out.println("=========================================");
    }

    private String promptProjectNameFilter() {
        System.out.print("\nEnter project name (Nothing to clear filter): ");
        return ScannerUtility.SCANNER.nextLine();
    }
    private String promptNeighbourhoodFilter() {
        System.out.print("\nEnter neighbourhood name (Nothing to clear filter): ");
        return ScannerUtility.SCANNER.nextLine();
    }
    private Double promptMinPriceFilter() {
        while (true){
        System.out.print("\nEnter minimum price (0 to clear filter): ");
            try {
                double price = ScannerUtility.SCANNER.nextDouble();
                ScannerUtility.SCANNER.nextLine();
                return price;
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                view.displayError("Invalid Price. Please try again.");
            }
        }
    }

    
    /**
     * Prompts the user to select a project name from a list of available projects.
     *
     * @param allProjects The list of all projects to choose from.
     * @return The selected project name or an empty string if the filter is cleared.
     */
    public String promptProjectNameFilter(ArrayList<Project> allProjects) {
        System.out.println("\n=========================================");
        System.out.println("           AVAILABLE PROJECT NAMES       ");
        System.out.println("=========================================");
        for (int i = 0; i < allProjects.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, allProjects.get(i).getProjectName());
        }
        System.out.println("0. Clear Filter");
        System.out.println("=========================================");
    
        while (true) {
            System.out.print("\nEnter the number corresponding to the project name (0 to clear filter): ");
            try {
                int choice = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); // Consume newline
    
                if (choice == 0) {
                    return ""; // Clear filter
                }
    
                if (choice >= 1 && choice <= allProjects.size()) {
                    return allProjects.get(choice - 1).getProjectName();
                } else {
                    view.displayError("Invalid selection. Please try again.");
                }
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine(); // Consume invalid input
                view.displayError("Invalid input. Please enter a valid number.");
            }
        }
    }

    
    /**
     * Prompts the user to select a neighborhood from a list of available neighborhoods.
     *
     * @param allProjects The list of all projects to choose from.
     * @return The selected neighborhood or an empty string if the filter is cleared.
     */

    public String promptNeighbourhoodFilter(ArrayList<Project> allProjects) {
        // Extract unique neighborhoods from the list of projects
        Set<String> neighborhoods = new HashSet<>();
        for (Project project : allProjects) {
            neighborhoods.add(project.getNeighborhood());
        }

        // Display the list of neighborhoods
        System.out.println("\n=========================================");
        System.out.println("           AVAILABLE NEIGHBORHOODS       ");
        System.out.println("=========================================");
        List<String> neighborhoodList = new ArrayList<>(neighborhoods);
        for (int i = 0; i < neighborhoodList.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, neighborhoodList.get(i));
        }
        System.out.println("0. Clear Filter");
        System.out.println("=========================================");

        // Prompt the user to select a neighborhood
        while (true) {
            System.out.print("\nEnter the number corresponding to the neighborhood (0 to clear filter): ");
            try {
                int choice = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); // Consume newline

                if (choice == 0) {
                    return ""; // Clear filter
                }

                if (choice >= 1 && choice <= neighborhoodList.size()) {
                    return neighborhoodList.get(choice - 1);
                } else {
                    view.displayError("Invalid selection. Please try again.");
                }
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine(); // Consume invalid input
                view.displayError("Invalid input. Please enter a valid number.");
            }
        }
    }
  
    /**
     * Prompts the user to enter a maximum price for filtering projects.
     *
     * @return The entered maximum price or 0 if the filter is cleared.
     */

    public Double promptMaxPriceFilter() {
        while (true){
            System.out.print("\nEnter maximum price (0 to clear filter): ");
            try {
                double price = ScannerUtility.SCANNER.nextDouble();
                ScannerUtility.SCANNER.nextLine();
                return price;
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                view.displayError("Invalid Price. Please try again.");
            }
        }
    }

    /**
     * Prompts the user to select a flat type for filtering projects.
     *
     * @return The selected flat type or 0 if the filter is cleared.
     */

    public int promptFlatTypeFilter() {
        while (true) {
            try {
                System.out.println("\nApplicable Flat Types:");
                System.out.println("1. 2~ROOM");
                System.out.println("2. 3~ROOM");
                System.out.println("3. Clear Filter");
                System.out.print("\nEnter Option: ");
                int selectedFlat = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                if (selectedFlat >= 1 && selectedFlat <= 3) {
                    return selectedFlat;
                }
                view.displayInfo("Invalid option.");
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                view.displayInfo("Invalid option.");
            }
        }
    }

    /**
     * Prompts the user to select a sort order for filtering projects.
     *
     * @return The selected sort order (1 for ascending, 2 for descending).
     */
    
    public int promptSortOrder() {
        while (true) {
            try {
                System.out.println("\nHow would you like it sorted?");
                System.out.println("1. Ascending");
                System.out.println("2. Descending");
                System.out.print("\nEnter Option: ");
                int order = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                if (order == 1 || order == 2) {
                    return order;
                }
                view.displayError("Invalid selection. Please try again.");
            } catch (InputMismatchException e) {
                ScannerUtility.SCANNER.nextLine();
                view.displayError("Invalid . Please try again.");
            }
        }
    }

}