package views;

import models.Project;
import models.FlatType;
import utilities.ScannerUtility;
import utilities.LoggerUtility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import databases.ProjectDB;
import controllers.ManagerProjectController;

//commit

public class ManagerProjectView implements IDisplayResult {
    ManagerProjectController controller = new ManagerProjectController();

    public void showProjectMenuHeader() {
        System.out.println("\n=========================================");
        System.out.println("           MANAGE BTO PROJECTS           ");
        System.out.println("=========================================");
        System.out.println("1. Create New Project");
        System.out.println("2. View All Projects");
        System.out.println("3. View My Projects");
        System.out.println("4. Search Projects");
        System.out.println("5. Edit Project");
        System.out.println("6. Toggle Project Visibility");
        System.out.println("7. Delete Project");
        System.out.println("0. Exit");
        System.out.println("=========================================");
        System.out.print("Enter your choice: ");
    }

    public Project createNewProjectMenu() {
        System.out.println("\n=========================================");
        System.out.println("           CREATE NEW PROJECT            ");
        System.out.println("=========================================");
    
        // Step 1: Enter Project Name
        System.out.print("Enter Project Name: ");
        String projectName = ScannerUtility.SCANNER.nextLine();
    
        // Step 2: Enter Neighborhood
        System.out.print("Enter Neighborhood: ");
        String neighborhood = ScannerUtility.SCANNER.nextLine();
    
        // Step 3: Enter Opening Date
        System.out.print("Enter Opening Date (yyyy-MM-dd): ");
        Date openingDate = getDateInput();
        if (openingDate == null) {
            displayError("Invalid date format. Please try again.");
            return null;
        }
    
        // Step 4: Enter Application Closing Date
        System.out.print("Enter Application Closing Date (yyyy-MM-dd): ");
        Date closingDate = getDateInput();
        if (closingDate == null || closingDate.before(openingDate)) {
            displayError("Invalid date format or closing date is before opening date. Please try again.");
            return null;
        }
    
        // Step 5: Enter Officer Slots
        System.out.print("Enter Officer Slots: ");
        int officerSlots = ScannerUtility.SCANNER.nextInt();
        ScannerUtility.SCANNER.nextLine(); // Consume newline
    
        // Step 6: Add Flat Types
        ArrayList<FlatType> flatTypes = new ArrayList<>();
        editFlatTypes(flatTypes);
    
        // Step 7: Confirm Project Creation
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
            displayInfo("Project creation aborted.");
            return null;
        }
    
        // Step 8: Create and Return Project
        System.out.println("=========================================");
        System.out.println("Project created successfully!");
        LoggerUtility.logInfo("New project created: " + projectName);
    
        return new Project(0, projectName, null, neighborhood, flatTypes, openingDate, closingDate, officerSlots, true);
    }


    public Project editProjectMenu(ArrayList<Project> projects) {
        if (projects.isEmpty()) {
            displayError("No projects available to edit.");
            return null;
        }
    
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
        System.out.println("0. Cancel");
        System.out.println("=========================================");
    
        // Prompt the user to select a project
        System.out.print("Enter the number of the project to edit: ");
        int choice = ScannerUtility.SCANNER.nextInt();
        ScannerUtility.SCANNER.nextLine(); // Consume newline
    
        if (choice == 0) {
            displayInfo("Edit operation canceled.");
            return null;
        }
    
        if (choice < 1 || choice > projects.size()) {
            displayError("Invalid selection. Please try again.");
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
            displayError("Closing date must be after the opening date. Update failed.");
        }
    
        System.out.print("Enter new Officer Slots (current: " + selectedProject.getOfficerSlots() + "): ");
        String officerSlotsInput = ScannerUtility.SCANNER.nextLine();
        if (!officerSlotsInput.isBlank()) {
            try {
                int newOfficerSlots = Integer.parseInt(officerSlotsInput);
                selectedProject.setOfficerSlots(newOfficerSlots);
                LoggerUtility.logInfo("Officer slots updated to: " + newOfficerSlots);
            } catch (NumberFormatException e) {
                displayError("Invalid number format. Update failed.");
            }
        }
    
        // Edit flat types
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
        System.out.print("Confirm changes? (yes/no): ");
        String confirm = ScannerUtility.SCANNER.nextLine().trim().toLowerCase();
    
        if (!confirm.equals("yes")) {
            displayInfo("Changes discarded.");
            return null;
        } else {
            displaySuccess("Project updated successfully!");
            return selectedProject;
        }
    }
    
    /**
     * Allows the user to edit the flat types of a project.
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
    
            int choice = ScannerUtility.SCANNER.nextInt();
            ScannerUtility.SCANNER.nextLine(); // Consume newline
    
            switch (choice) {
                case 1:
                    // Adding Flat Type
                    String flatTypeName;
                    while (true) {
                        System.out.println("Select Flat Type:");
                        System.out.println("1. 2-Room");
                        System.out.println("2. 3-Room");
                        System.out.print("Enter your choice: ");
                        int flatTypeChoice = ScannerUtility.SCANNER.nextInt();
                        ScannerUtility.SCANNER.nextLine(); // Consume newline
    
                        if (flatTypeChoice == 1) {
                            flatTypeName = "2-Room";
                            break; // Valid choice made
                        } else if (flatTypeChoice == 2) {
                            flatTypeName = "3-Room";
                            break; // Valid choice made
                        } else {
                            System.out.println("[ERROR] Invalid flat type choice. Please try again.");
                        }
                    }
    
                    int numUnits;
                    while (true) {
                        System.out.print("Enter Number of Units: ");
                        if (ScannerUtility.SCANNER.hasNextInt()) {
                            numUnits = ScannerUtility.SCANNER.nextInt();
                            ScannerUtility.SCANNER.nextLine(); // Consume newline
                            break; // Valid input
                        } else {
                            System.out.println("[ERROR] Please enter a valid number.");
                            ScannerUtility.SCANNER.nextLine(); // Clear invalid input
                        }
                    }
    
                    double pricePerFlat;
                    while (true) {
                        System.out.print("Enter Price per Flat: ");
                        if (ScannerUtility.SCANNER.hasNextDouble()) {
                            pricePerFlat = ScannerUtility.SCANNER.nextDouble();
                            ScannerUtility.SCANNER.nextLine(); // Consume newline
                            break; // Valid input
                        } else {
                            System.out.println("[ERROR] Please enter a valid price.");
                            ScannerUtility.SCANNER.nextLine(); // Clear invalid input
                        }
                    }
    
                    flatTypes.add(new FlatType(flatTypeName, numUnits, pricePerFlat));
                    LoggerUtility.logInfo("Added Flat Type: " + flatTypeName + " with " + numUnits + " units at $" + pricePerFlat);
                    break;
    
                case 2:
                    // Editing Existing Flat Type
                    if (flatTypes.isEmpty()) {
                        System.out.println("[ERROR] No flat types available to edit.");
                        break;
                    }
    
                    // Print existing flat types
                    System.out.println("Existing Flat Types:");
                    for (int i = 0; i < flatTypes.size(); i++) {
                        FlatType ft = flatTypes.get(i);
                        System.out.println((i + 1) + ". " + ft.getFlatType() + " - Units: " + ft.getNumFlats() + ", Price: $" + ft.getPricePerFlat());
                    }
    
                    int indexToEdit;
                    while (true) {
                        System.out.print("Enter the index of the Flat Type to edit (1-based): ");
                        if (ScannerUtility.SCANNER.hasNextInt()) {
                            indexToEdit = ScannerUtility.SCANNER.nextInt() - 1;
                            ScannerUtility.SCANNER.nextLine(); // Consume newline
                            if (indexToEdit >= 0 && indexToEdit < flatTypes.size()) {
                                break; // Valid index
                            } else {
                                System.out.println("[ERROR] Invalid index. Please try again.");
                            }
                        } else {
                            System.out.println("[ERROR] Please enter a valid index.");
                            ScannerUtility.SCANNER.nextLine(); // Clear invalid input
                        }
                    }
    
                    FlatType flatType = flatTypes.get(indexToEdit);
                    System.out.print("Enter new Flat Type Name (current: " + flatType.getFlatType() + "): ");
                    flatType.setFlatType(ScannerUtility.SCANNER.nextLine());
    
                    while (true) {
                        System.out.print("Enter new Number of Units (current: " + flatType.getNumFlats() + "): ");
                        if (ScannerUtility.SCANNER.hasNextInt()) {
                            flatType.setNumFlats(ScannerUtility.SCANNER.nextInt());
                            ScannerUtility.SCANNER.nextLine(); // Consume newline
                            break; // Valid input
                        } else {
                            System.out.println("[ERROR] Please enter a valid number.");
                            ScannerUtility.SCANNER.nextLine(); // Clear invalid input
                        }
                    }
    
                    while (true) {
                        System.out.print("Enter new Price per Flat (current: $" + flatType.getPricePerFlat() + "): ");
                        if (ScannerUtility.SCANNER.hasNextDouble()) {
                            flatType.setPricePerFlat(ScannerUtility.SCANNER.nextDouble());
                            ScannerUtility.SCANNER.nextLine(); // Consume newline
                            break; // Valid input
                        } else {
                            System.out.println("[ERROR] Please enter a valid price.");
                            ScannerUtility.SCANNER.nextLine(); // Clear invalid input
                        }
                    }
    
                    LoggerUtility.logInfo("Updated Flat Type: " + flatType.getFlatType());
                    break;
    
                case 3:
                    // Removing Flat Type
                    if (flatTypes.isEmpty()) {
                        System.out.println("[ERROR] No flat types available to remove.");
                        break;
                    }
    
                    // Print existing flat types
                    System.out.println("Existing Flat Types:");
                    for (int i = 0; i < flatTypes.size(); i++) {
                        FlatType ft = flatTypes.get(i);
                        System.out.println((i + 1) + ". " + ft.getFlatType() + " - Units: " + ft.getNumFlats() + ", Price: $" + ft.getPricePerFlat());
                    }
    
                    int indexToRemove;
                    while (true) {
                        System.out.print("Enter the index of the Flat Type to remove (1-based): ");
                        if (ScannerUtility.SCANNER.hasNextInt()) {
                            indexToRemove = ScannerUtility.SCANNER.nextInt() - 1;
                            ScannerUtility.SCANNER.nextLine(); // Consume newline
                            if (indexToRemove >= 0 && indexToRemove < flatTypes.size()) {
                                FlatType removedFlatType = flatTypes.remove(indexToRemove);
                                LoggerUtility.logInfo("Removed Flat Type: " + removedFlatType.getFlatType());
                                break; // Valid index
                            } else {
                                System.out.println("[ERROR] Invalid index. Please try again.");
                            }
                        } else {
                            System.out.println("[ERROR] Please enter a valid index.");
                            ScannerUtility.SCANNER.nextLine(); // Clear invalid input
                        }
                    }
                    break;
    
                case 0:
                    System.out.println("Exiting flat type edit menu...");
                    return;
    
                default:
                    System.out.println("[ERROR] Invalid choice. Please try again.");
            }
        }
    }

    public void toggleProjectVisibilityMenu(ArrayList<Project> projects) {
        if (projects.isEmpty()) {
            displayError("No projects available to toggle visibility.");
            return;
        }

        // Display the list of projects
        displayProjects(projects);

        // Prompt the user to select a project
        System.out.print("Enter the number of the project to toggle visibility: ");
        int choice = ScannerUtility.SCANNER.nextInt();
        ScannerUtility.SCANNER.nextLine(); // Consume newline

        if (choice == 0) {
            displayInfo("Toggle visibility operation canceled.");
            return;
        }

        if (choice < 1 || choice > projects.size()) {
            displayError("Invalid selection. Please try again.");
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
                displaySuccess("Project visibility toggled to " + (selectedProject.getProjectVisibility() ? "Visible" : "Hidden") + ".");
            } else {
                displayError("Failed to update project visibility in the database.");
            }
        } catch (IOException e) {
            displayError("Error updating project visibility: " + e.getMessage());
        }
    }

    public void deleteProjectView(ArrayList<Project> projects) {
        if (projects.isEmpty()) {
            displayError("No projects found to delete.");
            return;
        }
    
        // Display the details of the projects found
        System.out.println("\nProjects found:");
        displayProjects(projects); // Assuming you have a method to display project details
    
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
            displaySuccess("Project '" + projectToDelete.getProjectName() + "' deleted successfully.");
        } else {
            displaySuccess("Project deletion canceled.");
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

    public void displayProjects(ArrayList<Project> projects) {
        if (projects.isEmpty()) {
            displayInfo("No projects found.");
            return;
        }
    
        // Determine the maximum width for each column
        int idWidth = "ID".length();
        int nameWidth = "Name".length();
        int neighborhoodWidth = "Neighborhood".length();
        int statusWidth = "Status".length();
        int openingDateWidth = "Opening Date".length();
        int closingDateWidth = "Closing Date".length();
        int applicationsWidth = "Applications".length();
        int officerSlotsWidth = "Officer Slots".length();
    
        for (Project project : projects) {
            idWidth = Math.max(idWidth, String.valueOf(project.getProjectID()).length());
            nameWidth = Math.max(nameWidth, wrapText(project.getProjectName(), nameWidth).stream().map(String::length).max(Integer::compare).orElse(0));
            neighborhoodWidth = Math.max(neighborhoodWidth, wrapText(project.getNeighborhood(), neighborhoodWidth).stream().map(String::length).max(Integer::compare).orElse(0));
            statusWidth = Math.max(statusWidth, (project.getProjectVisibility() ? "Visible" : "Hidden").length());
            openingDateWidth = Math.max(openingDateWidth, project.getApplicationOpeningDate().toString().length());
            closingDateWidth = Math.max(closingDateWidth, project.getApplicationClosingDate().toString().length());
            applicationsWidth = Math.max(applicationsWidth, (project.getApplicationClosingDate().after(new Date()) ? "Open" : "Closed").length());
            officerSlotsWidth = Math.max(officerSlotsWidth, String.valueOf(project.getOfficerSlots()).length());
        }
    
        // Calculate total width for the table
        int totalWidth = idWidth + nameWidth + neighborhoodWidth + statusWidth + openingDateWidth + closingDateWidth + applicationsWidth + officerSlotsWidth + 8; // 8 for padding and borders
    
        // Print the header
        String headerFormat = "| %-"+idWidth+"s | %-"+nameWidth+"s | %-"+neighborhoodWidth+"s | %-"+statusWidth+"s | %-"+openingDateWidth+"s | %-"+closingDateWidth+"s | %-"+applicationsWidth+"s | %-"+officerSlotsWidth+"s |";
        String separator = "-".repeat(totalWidth);
        
        System.out.println("\n" + separator);
        System.out.printf(headerFormat, "ID", "Name", "Neighborhood", "Status", "Opening Date", "Closing Date", "Applications", "Officer Slots");
        System.out.println("\n" + separator);
    
        // Print the project data
        for (Project project : projects) {
            // Wrap text for project name and neighborhood
            List<String> nameLines = wrapText(project.getProjectName(), nameWidth);
            List<String> neighborhoodLines = wrapText(project.getNeighborhood(), neighborhoodWidth);
            int maxLines = Math.max(nameLines.size(), neighborhoodLines.size());
    
            for (int i = 0; i < maxLines; i++) {
                System.out.printf(headerFormat,
                        (i == 0 ? project.getProjectID() : ""), // Only show Project ID on the first line
                        (i < nameLines.size() ? nameLines.get(i) : ""), // Project Name
                        (i < neighborhoodLines.size() ? neighborhoodLines.get(i) : ""), // Neighborhood
                        (i == 0 ? (project.getProjectVisibility() ? "Visible" : "Hidden") : ""), // Status
                        (i == 0 ? project.getApplicationOpeningDate() : ""), // Opening Date
                        (i == 0 ? project.getApplicationClosingDate() : ""), // Closing Date
                        (i == 0 ? (project.getApplicationClosingDate().after(new Date()) ? "Open" : "Closed") : ""), // Applications
                        (i == 0 ? project.getOfficerSlots() : "")); // Officer Slots
            }
            System.out.println(separator);
        }
    }

    public void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
        LoggerUtility.logInfo(message);
    }

    public void displayError(String message) {
        System.out.println("ERROR: " + message);
        LoggerUtility.logError(message, new Exception("Error logged without stack trace"));
    }

    public void displayInfo(String message) {
        System.out.println("INFO: " + message);
        LoggerUtility.logInfo(message);
    }

    /**
     * Wraps text into a list of strings, each with a maximum width.
     *
     * @param text  The text to wrap.
     * @param width The maximum width of each line.
     * @return A list of wrapped lines.
     */
    private List<String> wrapText(String text, int width) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }

        int currentIndex = 0;
        while (currentIndex < text.length()) {
            int endIndex = Math.min(currentIndex + width, text.length());
            lines.add(text.substring(currentIndex, endIndex));
            currentIndex = endIndex;
        }

        return lines;
    }
}