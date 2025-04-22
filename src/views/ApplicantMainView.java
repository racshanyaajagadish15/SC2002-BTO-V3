package views;

import utilities.ScannerUtility;

public class ApplicantMainView implements IDisplayResult {

    public int showApplicantMenu() {
        System.out.println("\n=========================================");
        System.out.println("           APPLICANT DASHBOARD           ");
        System.out.println("=========================================");
        System.out.println("1. View Application Status");
        System.out.println("2. View Available Projects");
        System.out.println("3. Manage Enquiries");
        System.out.println("4. Change Password");
        System.out.println("5. Logout");
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