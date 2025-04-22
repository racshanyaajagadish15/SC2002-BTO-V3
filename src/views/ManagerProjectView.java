package views;

import models.Project;
import java.util.ArrayList;
import java.util.Date;

public class ManagerProjectView implements IDisplayResult{

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
        System.out.print("Enter your choice: ");
    }

    public void displayProjects(ArrayList<Project> projects) {
        if (projects.isEmpty()) {
            System.out.println("No projects found");
            return;
        }

        System.out.println("\n===== Project List =====");
        for (Project project : projects) {
            System.out.println("ID: " + project.getProjectID());
            System.out.println("Name: " + project.getProjectName());
            System.out.println("Neighborhood: " + project.getNeighborhood());
            System.out.println("Status: " + (project.getProjectVisibility() ? "Visible" : "Hidden"));
            System.out.println("Applications: " + (project.getApplicationClosingDate().after(new Date()) ? "Open" : "Closed"));
            System.out.println("----------------------------------");
        }
    }
}
