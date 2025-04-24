package views;
import java.util.ArrayList;
import java.util.Date;

import models.Project;
import utilities.LoggerUtility;

public class ManagerProjectView implements IDisplayResult {

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
        System.out.println("8. View Filtered Projects");
        System.out.println("0. Exit");
        System.out.println("=========================================");
        System.out.print("Enter your choice: ");
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

    public void displayProjects(ArrayList<Project> projects) {
        if (projects.isEmpty()) {
            displayInfo("No projects found.");
            return;
        }

        System.out.println("\n=========================================");
        System.out.println("           BTO PROJECTS                ");
        System.out.println("=========================================");
    
        // Print each project's details in lines
        for (Project project : projects) {
    
            // Print project details
            System.out.println("ID: " + project.getProjectID());
            System.out.println("Project Name: " + project.getProjectName());
            System.out.println("Neighborhood: " + project.getNeighborhood());
            System.out.println("Flat Types: " + project.getFlatTypes().size() + " types");
            System.out.println("Status: " + (project.getProjectVisibility() ? "Visible" : "Hidden"));
            System.out.println("Opening Date: " + project.getApplicationOpeningDate());
            System.out.println("Closing Date: " + project.getApplicationClosingDate());
            System.out.println("Applications: " + (project.getApplicationClosingDate().after(new Date()) ? "Open" : "Closed"));
            System.out.println("Officer Slots: " + project.getOfficerSlots());
    
            // Print a separator line between projects
            System.out.println("-----------------------------------------");
        }
    }
}