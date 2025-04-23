package views;

import utilities.ScannerUtility;
import controllers.ManagerMainController;
import controllers.ManagerProjectController;
import models.HDBManager;

//commit


public class ManagerMainView {
    private ManagerApplicationView applicationView;
    private ManagerRegistrationView registrationView;
    private ManagerProjectView projectView;
    private ManagerEnquiryView enquiryView;
    private HDBManager loggedInManager;

    public ManagerMainView(HDBManager manager) {
        this.loggedInManager = manager;
        this.applicationView = new ManagerApplicationView(loggedInManager);
        ManagerProjectController controller = new ManagerProjectController(); // Create the controller
        this.projectView = new ManagerProjectView(controller); // Pass the controller to the view
        this.enquiryView = new ManagerEnquiryView(loggedInManager);
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
                case 5:
                    handleReportGeneration();
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
        System.out.println("5. Generate Applicant Report");
        System.out.println("0. Logout");
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
        applicationView.showApplicationMenu();
    }

    private void handleProjects() {
        ManagerProjectController projectController = new ManagerProjectController(); // Create a controller instance
        projectController.setLoggedInManager(loggedInManager); // Pass the logged-in manager
        projectController.handleProjectMenu(); // Delegate to the project menu
    }

    private void handleEnquiries() {
        enquiryView.showEnquiryMenu();
    }

    private void handleRegistrations() {
        registrationView.showRegistrationMenu(loggedInManager);
    }

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

        System.out.print("Enter output file path (e.g., ApplicantReport.xlsx): ");
        String outputFilePath = ScannerUtility.SCANNER.nextLine().trim();

        // Call the controller to generate the report
        ManagerMainController controller = new ManagerMainController();
        controller.generateApplicantReport(maritalStatusFilter, flatTypeFilter, projectNameFilter, outputFilePath);
    }
}