package views;

import utilities.ScannerUtility;
import controllers.ManagerProjectController;
import models.HDBManager;

public class ManagerMainView {
    private ManagerApplicationView applicationView;
    private ManagerProjectView projectView;
    private ManagerEnquiryView enquiryView;
    private ManagerRegistrationView registrationView;
    private HDBManager loggedInManager;

    public ManagerMainView() {
        this.applicationView = new ManagerApplicationView(loggedInManager);
        this.projectView = new ManagerProjectView();
        this.enquiryView = new ManagerEnquiryView();
        this.registrationView = new ManagerRegistrationView();
    }

    public ManagerMainView(HDBManager manager) {
        this.loggedInManager = manager;
        this.applicationView = new ManagerApplicationView(loggedInManager);
        this.projectView = new ManagerProjectView();
        this.enquiryView = new ManagerEnquiryView();
        this.registrationView = new ManagerRegistrationView();
    }

    public void showManagerMenu() {
        int choice;

        do {
            printMenuHeader();
            choice = getValidChoice(0, 4);

            switch (choice) {
                case 1:
                    handleApplications();
                    break;
                case 2:
                    handleProjects();
                    break;
                case 3:
                    handleEnquiries();
                    break;
                case 4:
                    handleRegistrations();
                    break;
                case 0:
                    System.out.println("Exiting... Thank you!");
                    break;
                default:
                    System.out.println("Invalid option. Please choose from 0 to 4.");
            }

        } while (choice != 0);
    }

    private void printMenuHeader() {
        System.out.println("\n=========================================");
        System.out.println("            MANAGER DASHBOARD           ");
        System.out.println("=========================================");
        System.out.println("1. Manage Applications");
        System.out.println("2. Manage Projects");
        System.out.println("3. Manage Enquiries");
        System.out.println("4. Manage Officer Registrations");
        System.out.println("0. Exit");
        System.out.println("=========================================");
        System.out.print("Please enter your choice: ");
    }

    private int getValidChoice(int min, int max) {
        while (!ScannerUtility.SCANNER.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            ScannerUtility.SCANNER.next(); // Clear invalid input
        }
        int choice = ScannerUtility.SCANNER.nextInt();
        ScannerUtility.SCANNER.nextLine(); // Consume newline
        return choice;
    }

    private void handleApplications() {
        System.out.println(">> You selected: Manage Applications");
        applicationView.showApplicationMenu();
    }

    private void handleProjects() {
        System.out.println(">> You selected: Manage Projects");
        ManagerProjectController projectController = new ManagerProjectController(); // Create a controller instance
        projectController.setLoggedInManager(loggedInManager); // Pass the logged-in manager
        projectController.handleProjectMenu(); // Delegate to the project menu
    }

    private void handleEnquiries() {
        System.out.println(">> You selected: Manage Enquiries");
        enquiryView.showEnquiryMenu();
    }

    private void handleRegistrations() {
        System.out.println(">> You selected: Manage Officer Registrations");
        registrationView.showRegistrationMenu();
    }
}