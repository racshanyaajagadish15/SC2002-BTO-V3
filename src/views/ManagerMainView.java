package views;

import utilities.ScannerUtility;
import controllers.ManagerMainController;
import controllers.ManagerProjectController;
import models.HDBManager;
import controllers.AuthenticatorController;


public class ManagerMainView {
    private ManagerApplicationView applicationView;
    private ManagerRegistrationView registrationView;
    private ManagerEnquiryView enquiryView;
    private HDBManager loggedInManager;

    /**
     * Constructor for ManagerMainView.
     * Initializes the views and sets the logged-in manager.
     * @param manager The HDBManager instance representing the logged-in manager.
     */
    public ManagerMainView(HDBManager manager) {
        this.loggedInManager = manager;
        this.applicationView = new ManagerApplicationView(loggedInManager);
        this.enquiryView = new ManagerEnquiryView(loggedInManager); // Pass the required argument(s)
        this.registrationView = new ManagerRegistrationView();
    }

    /**
     * Displays the manager menu and handles user input.
     * This method is the main entry point for the manager dashboard.
     * It directs the user to different functionalities based on their choice.
     */
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
                case 5:
                    handleReportGeneration();

                case 6:
                   AuthenticatorController authenticatorController = new AuthenticatorController();
                   authenticatorController.handlePasswordChange(loggedInManager);
                    break;
                case 0:
                    System.out.println("Exiting... Thank you!");
                    break;
                default:
                    System.out.println("Invalid option. Please choose from 0 to 4.");
            }

        } while (choice != 0);
    }

    /**
     * Prints the menu header for the manager dashboard.
     * This method displays the available options for the manager.
     */

    private void printMenuHeader() {
        System.out.println("\n=========================================");
        System.out.println("            MANAGER DASHBOARD           ");
        System.out.println("=========================================");
        System.out.println("1. Manage Applications");
        System.out.println("2. Manage Projects");
        System.out.println("3. Manage Enquiries");
        System.out.println("4. Manage Officer Registrations");
        System.out.println("5. Generate Applicant Report");
        System.out.println("6. Change Password");
        System.out.println("0. Logout");
        System.out.println("=========================================");
        System.out.print("Please enter your choice: ");
    }

    /**
     * Gets a valid choice from the user within the specified range.
     * This method ensures that the user input is an integer and falls within the specified range.
     * @param min The minimum valid choice.
     * @param max The maximum valid choice.
     * @return The valid choice as an integer.
     */

    private int getValidChoice(int min, int max) {
        while (!ScannerUtility.SCANNER.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            ScannerUtility.SCANNER.next(); // Clear invalid input
        }
        int choice = ScannerUtility.SCANNER.nextInt();
        ScannerUtility.SCANNER.nextLine(); // Consume newline
        return choice;
    }

    /**
     * Handles the user's choice for managing applications.
     * This method delegates the application management to the application view.
     */

    private void handleApplications() {
        applicationView.showApplicationMenu();
    }

    /**
     * Handles the user's choice for managing projects.
     * This method delegates the project management to the project controller.
     */

    private void handleProjects() {
        ManagerProjectController projectController = new ManagerProjectController(); // Create a controller instance
        projectController.setLoggedInManager(loggedInManager); // Pass the logged-in manager
        projectController.handleProjectMenu(); // Delegate to the project menu
    }

    /**
     * Handles the user's choice for managing enquiries.
     * This method delegates the enquiry management to the enquiry view.
     */

    private void handleEnquiries() {
        enquiryView.showEnquiryMenu();
    }

    /**
     * Handles the user's choice for managing officer registrations.
     * This method delegates the registration management to the registration view.
     */

    private void handleRegistrations() {
        registrationView.showRegistrationMenu(loggedInManager);
    }

    /**
     * Handles the user's choice for generating applicant reports.
     * This method prompts the user for filters and calls the controller to generate the report.
     */

    private void handleReportGeneration() {
        // Prompt the user for filters
        System.out.print("Enter marital status filter (or leave blank for no filter): ");
        String maritalStatusFilter = ScannerUtility.SCANNER.nextLine().trim();
        maritalStatusFilter = maritalStatusFilter.isEmpty() ? null : maritalStatusFilter;

        System.out.print("Enter flat type filter (or leave blank for no filter): ");
        String flatTypeFilter = ScannerUtility.SCANNER.nextLine().trim();
        flatTypeFilter = flatTypeFilter.isEmpty() ? null : flatTypeFilter;

        System.out.print("Enter project name filter (or leave blank for no filter): ");
        String projectNameFilter = ScannerUtility.SCANNER.nextLine().trim();
        projectNameFilter = projectNameFilter.isEmpty() ? null : projectNameFilter;

        // Call the controller to generate the report
        ManagerMainController controller = new ManagerMainController();
        controller.generateApplicantReport(maritalStatusFilter, flatTypeFilter, projectNameFilter);
    }
}