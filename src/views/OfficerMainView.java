package views;

import utilities.ScannerUtility;

/**
 * OfficerMainView class implements IDisplayResult interface to display the officer dashboard menu.
 * It provides methods to show the officer menu and handle user input.
 */

public class OfficerMainView implements IDisplayResult {

    /**
     * Displays the main menu for the officer dashboard.
     * @return The selected option as an integer.
     */

    public int showOfficerMenu() {
        System.out.println("\n=========================================");
        System.out.println("            OFFICER DASHBOARD            ");
        System.out.println("=========================================");
        System.out.println("1. View Application Status");
        System.out.println("2. Apply/Enquire Project As Applicant");
        System.out.println("3. Manage Personal Enquiries");
        System.out.println("4. View Officer Project Registrations");
        System.out.println("5. Apply As Project Officer Registrations");
        System.out.println("6. Reply Managed Project Enquiries");
        System.out.println("7. Manage Project Bookings");
        System.out.println("8. Generate Application Bookings Receipt");
        System.out.println("9. Change Password");
        System.out.println("10. Logout");
        System.out.print("\nEnter option: ");

        try {
            int option = ScannerUtility.SCANNER.nextInt();
            ScannerUtility.SCANNER.nextLine();
            return option;
        } catch (Exception e) {
            displayError("Invalid input. Please enter a number.");
            ScannerUtility.SCANNER.nextLine();
            return -1;
        }
    }
}