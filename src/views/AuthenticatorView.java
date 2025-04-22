package views;

import utilities.ScannerUtility;

public class AuthenticatorView implements DisplayResult {

    public void showLoginBanner() {
        System.out.println("\n=========================================");
        System.out.println("                Welcome to               ");
        System.out.println("          BTO Management System          ");
        System.out.println("=========================================");
        System.out.println("                  LOGIN                  ");
        System.out.println("=========================================");
    }

    public String getNric() {
        System.out.print("Enter NRIC: ");
        return ScannerUtility.SCANNER.nextLine().toUpperCase();
    }

    public String getPassword() {
        System.out.print("Enter Password: ");
        return ScannerUtility.SCANNER.nextLine();
    }

    public String getNewPassword() {
        System.out.print("Enter New Password: ");
        return ScannerUtility.SCANNER.nextLine();
    }

    public String getConfirmPassword() {
        System.out.print("Confirm New Password: ");
        return ScannerUtility.SCANNER.nextLine();
    }

    public void showPasswordChangePrompt() {
        System.out.println("\n=========================================");
        System.out.println("            CHANGE PASSWORD              ");
        System.out.println("=========================================");
    }

    public String getCurrentPassword() {
        System.out.print("Enter Current Password: ");
        return ScannerUtility.SCANNER.nextLine();
    }
}