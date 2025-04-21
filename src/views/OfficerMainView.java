package views;

import utilities.ScannerUtility;

public class OfficerMainView implements DisplayResult {

    public int showOfficerMenu() {
        System.out.println("\n=========================================");
        System.out.println("            OFFICER DASHBOARD            ");
        System.out.println("=========================================");
        System.out.println("1. View Application Status");
        System.out.println("2. View Available Projects");
        System.out.println("3. Manage Personal Enquiries");
        System.out.println("4. View Project Registrations");
        System.out.println("5. Register for Project");
        System.out.println("6. Handle Project Enquiries");
        System.out.println("7. Change Password");
        System.out.println("8. Logout");
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