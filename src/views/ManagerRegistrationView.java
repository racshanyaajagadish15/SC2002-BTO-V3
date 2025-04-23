package views;

import controllers.ManagerRegistrationController;
import databases.OfficerRegistrationDB;
import models.OfficerRegistration;

import java.util.ArrayList;
import java.util.Scanner;

public class ManagerRegistrationView {

    private Scanner scanner;
    private ManagerRegistrationController controller;

    public ManagerRegistrationView() {
        this.scanner = new Scanner(System.in);
        this.controller = new ManagerRegistrationController();
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
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    viewAllRegistrations();
                    break;
                case "2":
                    viewPendingRegistrations();
                    break;
                case "3":
                    updateRegistrationStatusMenu();
                    break;
                case "0":
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

    private void updateRegistrationStatusMenu() {
        try {
            ArrayList<OfficerRegistration> allRegistrations = OfficerRegistrationDB.getAllOfficerRegistrations();

            if (allRegistrations.isEmpty()) {
                System.out.println("No registrations available.");
                return;
            }

            System.out.println("\nSelect a registration to update:");
            displayRegistrations(allRegistrations);

            System.out.print("Enter the ID of the registration to update (or 0 to cancel): ");
            int regId = Integer.parseInt(scanner.nextLine());

            if (regId == 0) return;

            OfficerRegistration selected = null;
            for (OfficerRegistration reg : allRegistrations) {
                if (reg.getOfficerRegistrationID() == regId) {
                    selected = reg;
                    break;
                }
            }

            if (selected == null) {
                System.out.println("[ERROR] No registration with the entered ID.");
                return;
            }

            System.out.println("Selected: Officer NRIC: " + selected.getOfficer().getNric()
                    + ", Project ID: " + selected.getProjectID()
                    + ", Current Status: " + selected.getRegistrationStatus());

            System.out.println("Choose new status:");
            System.out.println("1. Successful");
            System.out.println("2. Unsuccessful");
            System.out.println("0. Cancel");

            String choice = scanner.nextLine();
            String newStatus = null;

            switch (choice) {
                case "1":
                    newStatus = "Successful";
                    break;
                case "2":
                    newStatus = "Unsuccessful";
                    break;
                case "0":
                    System.out.println("Cancelled.");
                    return;
                default:
                    System.out.println("Invalid option.");
                    return;
            }

            System.out.print("Are you sure you want to set status to '" + newStatus + "'? (y/n): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("y")) {
                controller.updateOfficerApplicationStatus(selected, newStatus);
            } else {
                System.out.println("Update cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid number input.");
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to update registration status: " + e.getMessage());
        }
    }

    private void displayRegistrations(ArrayList<OfficerRegistration> registrations) {
        System.out.println("\n==========================================================================");
        System.out.printf("| %-5s | %-15s | %-10s | %-15s |\n", "ID", "Officer NRIC", "Project ID", "Status");
        System.out.println("==========================================================================");
        int index = 1;
        for (OfficerRegistration registration : registrations) {
            System.out.printf("| %-5d | %-15s | %-10d | %-15s |\n",
                    index++,
                    registration.getOfficer().getNric(),
                    registration.getProjectID(),
                    registration.getRegistrationStatus());
        }
        System.out.println("==========================================================================");
    }
}

//commit
