package views;

import models.Project;
import models.FlatType;
import utilities.ScannerUtility;
import utilities.LoggerUtility;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;

import databases.ProjectDB;
import controllers.ManagerProjectController;

//commit
import enums.FlatTypeName;

public class ManagerProjectView implements IDisplayResult {
    ManagerProjectController controller = new ManagerProjectController();

    public int showProjectMenuHeader() {
        while (true){
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
            System.out.print("\nEnter your choice: ");
            int option;
            try {
                option = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); 
                if (option >= 0 && option <=7){
                    return option;
                }
                displayError("Invalid selection. Please try again.");
            } catch (InputMismatchException e){
                displayError("Invalid number. Please try again.");
                ScannerUtility.SCANNER.nextLine(); // Consume newline
            }
        }
    }

    public Project createNewProjectMenu() {
        System.out.println("\n=========================================");
        System.out.println("           CREATE NEW PROJECT            ");
        System.out.println("=========================================");
    
        // Step 1: Enter Project Name
        System.out.print("\nEnter Project Name (Blank to cancel): ");
        String projectName = ScannerUtility.SCANNER.nextLine();
        if (projectName.isBlank()){
            return null;
        }
        // Step 2: Enter Neighborhood
        System.out.print("\nEnter Neighborhood (Blank to cancel): ");
        String neighborhood = ScannerUtility.SCANNER.nextLine();
        if (neighborhood.isBlank()){
            return null;
        }
        // Step 3: Enter Opening Date
        Date openingDate;
        while (true){
            System.out.print("\nEnter Application Opening Date (dd-MM-yyyy): ");
            openingDate = getDateInput();
            if (openingDate == null) {
                displayError("Invalid date format. Please try again.");
            }
            else{
                break;
            }
        }
        // Step 4: Enter Application Closing Date
        Date closingDate;
        while (true){
            System.out.print("\nEnter Application Closing Date (dd-MM-yyyy): ");
            closingDate = getDateInput();
            if (closingDate == null || closingDate.before(openingDate)) {
                displayError("Invalid date format or closing date is before opening date. Please try again.");
            }
            else{
                break;
            }
        }
    
        // Step 5: Enter Officer Slots
        int officerSlots;
        while (true){
            System.out.print("\nEnter Officer Slots: ");
            try {
                officerSlots = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); 
                if (officerSlots >= 0){
                    break;
                }
                displayError("Number must be positive. Please try again.");
            } catch (InputMismatchException e){
                displayError("Invalid number. Please try again.");
                ScannerUtility.SCANNER.nextLine(); // Consume newline
            }
        }
    
        // Step 6: Add Flat Types
        ArrayList<FlatType> flatTypes = new ArrayList<>();
        System.out.println("2-Room Number of flats:");
        int numUnits2Room = inputNumUnit();
        double pricePerFlat2Room = inputPricePerFlat();
        flatTypes.add(new FlatType(FlatTypeName.TWO_ROOM.getflatTypeName(), numUnits2Room, pricePerFlat2Room));
        System.out.println("3-Room Number of flats:");
        int numUnits3Room = inputNumUnit();
        double pricePerFlat3Room = inputPricePerFlat();
        flatTypes.add(new FlatType(FlatTypeName.TWO_ROOM.getflatTypeName(), numUnits3Room, pricePerFlat3Room));
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        // Step 7: Confirm Project Creation
        System.out.println("\n=========================================");
        System.out.println("           PROJECT SUMMARY               ");
        System.out.println("=========================================");
        System.out.println("Project Name: " + projectName);
        System.out.println("Neighborhood: " + neighborhood);
        try {
            System.out.println("Application Opening Date: " + formatter.parse(formatter.format(openingDate)));
        } catch (ParseException e) {
            System.out.println("Application Opening Date: " + openingDate);
        }
        try {
            System.out.println("Application Opening Date: " + formatter.parse(formatter.format(closingDate)));
        } catch (ParseException e) {
            System.out.println("Application Opening Date: " + openingDate);
        }
        System.out.println("Officer Slots: " + officerSlots);
        System.out.println("Flat Types:");
        for (FlatType flatType : flatTypes) {
            System.out.println("- " + flatType.getFlatType() + ": " + flatType.getNumFlats() + " units at $" + flatType.getPricePerFlat());
        }
        int confirm;
        while (true) {
            System.out.println("\nConfirm project creation: ");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.print("Confirm project creation: ");
            try {
                confirm = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
                if (confirm == 1){
                    break;
                }
                else if (confirm == 2) {
                    return null;
                }
            }
            catch (InputMismatchException e){
                ScannerUtility.SCANNER.nextLine();
                displayError("Invalid number. Please try again.");
            }
        }
     
        // Step 8: Create and Return Project
        System.out.println("=========================================");
        return new Project(0, projectName, null, neighborhood, flatTypes, openingDate, closingDate, officerSlots, true);
    }


    public Project editProjectMenu(ArrayList<Project> projects) {
        if (projects.isEmpty()) {
            displayError("No projects available to edit.");
            return null;
        }
    
        // Display the list of projects
        int choice;
        while (true){
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
            try {
                // Prompt the user to select a project
                System.out.print("\nEnter the number of the project to edit: ");
                choice = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); // Consume newline
            
                if (choice == 0) {
                    displayInfo("Edit canceled.");
                    return null;
                }
            
                if (choice < 1 || choice > projects.size()) {
                    displayError("Invalid selection. Please try again.");
                    continue;
                }
                break;
            }
            catch (InputMismatchException e){
                ScannerUtility.SCANNER.nextLine(); // Consume newline
                displayError("Invalid selection. Please try again.");
            }
        }
    
        // Get the selected project
        Project selectedProject = projects.get(choice - 1);
    
        // Edit the selected project
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("           EDIT PROJECT DETAILS          ");
            System.out.println("=========================================");
            System.out.println("1. Edit Project Name");
            System.out.println("2. Edit Neighborhood");
            System.out.println("3. Edit Application Opening Date");
            System.out.println("4. Edit Application Closing Date");
            System.out.println("5. Edit Number of Officer Slots");
            System.out.println("6. Edit Flat Type Details");
            System.out.println("0. Back");
            System.out.println("=========================================");
            System.out.print("\nEnter your choice: ");
            int option;
            try {
            option = ScannerUtility.SCANNER.nextInt();
            ScannerUtility.SCANNER.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                displayError("Invalid number. Please try again.");
            ScannerUtility.SCANNER.nextLine(); // Consume newline
            continue;
            }

            switch (option) {
                case 1:
                    System.out.print("Enter new Project Name (current: " + selectedProject.getProjectName() + "): ");
                    String newName = ScannerUtility.SCANNER.nextLine();
                        if (!newName.isBlank()) {
                            selectedProject.setProjectName(newName);
                        }
                    break;

                case 2:
                    System.out.print("Enter new Neighborhood (current: " + selectedProject.getNeighborhood() + "): ");
                    String newNeighborhood = ScannerUtility.SCANNER.nextLine();
                        if (!newNeighborhood.isBlank()) {
                            selectedProject.setNeighborhood(newNeighborhood);
                        }
                    break;

                case 3:
                    System.out.print("Enter new Opening Date (dd-MM-yyyy) (current: " + selectedProject.getApplicationOpeningDate() + "): ");
                    Date newOpeningDate = getDateInput();
                        if (newOpeningDate != null) {
                            selectedProject.setApplicationOpeningDate(newOpeningDate);
                        }
                    break;

                case 4:
                    System.out.print("Enter new Application Closing Date (dd-MM-yyyy) (current: " + selectedProject.getApplicationClosingDate() + "): ");
                    Date newClosingDate = getDateInput();
                    if (newClosingDate != null && newClosingDate.after(selectedProject.getApplicationOpeningDate())) {
                        selectedProject.setApplicationClosingDate(newClosingDate);
                    } else if (newClosingDate != null) {
                        displayError("Closing date must be after the opening date. Update failed.");
                    }
                    break;

                case 5:
                    System.out.print("Enter new Officer Slots (current: " + selectedProject.getOfficerSlots() + "): ");
                    String officerSlotsInput = ScannerUtility.SCANNER.nextLine();
                    if (!officerSlotsInput.isBlank()) {
                        try {
                            int newOfficerSlots = Integer.parseInt(officerSlotsInput);
                            selectedProject.setOfficerSlots(newOfficerSlots);
                        } catch (NumberFormatException e) {
                            displayError("Invalid number. Update failed.");
                        }
                    }
                    break;
                case 6:
                    editFlatTypes(selectedProject.getFlatTypes());
                    break;
                case 0:
                    return selectedProject;

                default:
                    displayError("Invalid selection. Please try again.");
            }
        }    
    }

    /**
     * Allows the user to edit the flat types of a project.
     *
     * @param flatTypes The list of flat types to edit.
     */
    private void editFlatTypes(ArrayList<FlatType> flatTypes) {
        // Assumes flatTypes contains exactly two types: 2-Room and 3-Room
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("           EDIT FLAT TYPES               ");
            System.out.println("=========================================");
            System.out.println("1. Edit 2-Room Flat Type");
            System.out.println("2. Edit 3-Room Flat Type");
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false); // Ensure strict date parsing

        try {
            String dateInput = ScannerUtility.SCANNER.nextLine();
            return dateFormat.parse(dateInput);
        } catch (ParseException e) {
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
    private Double inputPricePerFlat(){
        while (true){
            try {
                System.out.print("Enter Price per Flat: ");
                double pricePerFlat = ScannerUtility.SCANNER.nextDouble();
                if (pricePerFlat >= 0){
                    return pricePerFlat;
                }
                displayError("Number must be positive. Please try again.");
            }
            catch (InputMismatchException e){
                displayError("Invalid number. Please try again.");
                ScannerUtility.SCANNER.nextLine(); // Consume newline
            }
        }
    }
    private int inputNumUnit(){
        while (true){
            try {
                System.out.print("Enter Number of Units: ");
                int numUnits = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine(); // Consume newline
                if (numUnits >= 0){
                    return numUnits;
                }
                displayError("Number must be positive. Please try again.");
            }
            catch (InputMismatchException e){
                displayError("Invalid number. Please try again.");
                ScannerUtility.SCANNER.nextLine(); // Consume newline
            }
        }
    }
}