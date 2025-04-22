package views;

import controllers.ManagerRegistrationController; // Import the controller
import databases.OfficerRegistrationDB;
import models.OfficerRegistration;

import java.util.ArrayList;
import java.util.Scanner;

public class ManagerRegistrationView {

    private Scanner scanner;
    private ManagerRegistrationController controller; // Add a controller instance

    public ManagerRegistrationView() {
        this.scanner = new Scanner(System.in);
        this.controller = new ManagerRegistrationController(); // Initialize the controller
    }

    public void showRegistrationMenu() {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("         MANAGE OFFICER REGISTRATIONS    ");
            System.out.println("=========================================");
            System.out.println("1. View All Registrations");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Update Registration Status");
            System.out.println("0. Exit");
            System.out.println("=========================================");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAllRegistrations();
                    break;
                case 2:
                    viewPendingRegistrations();
                    break;
                case 3:
                    updateRegistrationStatus();
                    break;
                case 0:
                    System.out.println("Exiting Registration Manager...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewAllRegistrations() {
        try {
            ArrayList<OfficerRegistration> registrations = OfficerRegistrationDB.getAllOfficerRegistrations();
            if (registrations.isEmpty()) {
                System.out.println("No registrations found.");
                return;
            }
            displayRegistrations(registrations);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to retrieve registrations: " + e.getMessage());
        }
    }

    private void viewPendingRegistrations() {
        try {
            ArrayList<OfficerRegistration> pendingRegistrations = OfficerRegistration.getPendingRegistrationsDB();
            if (pendingRegistrations.isEmpty()) {
                System.out.println("No pending registrations found.");
                return;
            }
            displayRegistrations(pendingRegistrations);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to retrieve pending registrations: " + e.getMessage());
        }
    }

    private void updateRegistrationStatus() {
        try {
            System.out.print("Enter Officer NRIC: ");
            String nric = scanner.nextLine();
            System.out.print("Enter Project ID: ");
            int projectId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            System.out.print("Enter New Status (Approved/Rejected): ");
            String status = scanner.nextLine();

            OfficerRegistration registration = findRegistrationByNricAndProject(nric, projectId);
            if (registration == null) {
                System.out.println("[ERROR] No matching registration found.");
                return;
            }

            // Use the controller to update the registration status
            controller.updateOfficerApplicationStatus(registration, status);
            System.out.println("[SUCCESS] Registration status updated successfully.");
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to update registration status: " + e.getMessage());
        }
    }

    private OfficerRegistration findRegistrationByNricAndProject(String nric, int projectId) {
        try {
            ArrayList<OfficerRegistration> registrations = OfficerRegistrationDB.getAllOfficerRegistrations();
            for (OfficerRegistration registration : registrations) {
                if (registration.getOfficer().getNric().equalsIgnoreCase(nric) &&
                    registration.getProjectID() == projectId) {
                    return registration;
                }
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to find registration: " + e.getMessage());
        }
        return null;
    }

    private void displayRegistrations(ArrayList<OfficerRegistration> registrations) {
        System.out.println("\n==========================================================================");
        System.out.printf("| %-5s | %-15s | %-10s | %-15s |\n", "ID", "Officer NRIC", "Project ID", "Status");
        System.out.println("==========================================================================");
        for (OfficerRegistration registration : registrations) {
            System.out.printf("| %-5d | %-15s | %-10d | %-15s |\n",
                    registration.getOfficerRegistrationID(),
                    registration.getOfficer().getNric(),
                    registration.getProjectID(),
                    registration.getRegistrationStatus());
        }
        System.out.println("==========================================================================");
    }
}