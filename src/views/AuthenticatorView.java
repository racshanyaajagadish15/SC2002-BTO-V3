package views;

import utilities.ScannerUtility;

public class AuthenticatorView implements IDisplayResult {

    /**
     * Displays the login banner and prompts the user for NRIC and password.
     * @return The NRIC entered by the user.
     */

    public void showLoginBanner() {
        System.out.println("\n=========================================");
        System.out.println("                Welcome to               ");
        System.out.println("          BTO Management System          ");
        System.out.println("=========================================");
        System.out.println("                  LOGIN                  ");
        System.out.println("=========================================");
    }

    /**
     * Prompts the user for NRIC
     * @return The NRIC entered by the user.
     */

    public String getNric() {
        System.out.print("Enter NRIC: ");
        return ScannerUtility.SCANNER.nextLine().toUpperCase();
    }
    /**
     * Prompt the user for password
     * @return The NRIC entered by the user.
     */

    public String getPassword() {
        System.out.print("Enter Password: ");
        return ScannerUtility.SCANNER.nextLine();
    }

    /**
     * Prompt the user for new password
     * @return The new password
     */

    public String getNewPassword() {
        System.out.print("Enter New Password: ");
        return ScannerUtility.SCANNER.nextLine();
    }

    /**
     * Prompt the user for confirm password
     * @return The confirm password
     */

    public String getConfirmPassword() {
        System.out.print("Confirm New Password: ");
        return ScannerUtility.SCANNER.nextLine();
    }

    /**
     * Displays a message indicating that the password has been changed successfully.
     */

    public void showPasswordChangePrompt() {
        System.out.println("\n=========================================");
        System.out.println("            CHANGE PASSWORD              ");
        System.out.println("=========================================");
    }

    /**
     * Displays a message indicating that the password has been changed successfully.
     */
    public String getCurrentPassword() {
        System.out.print("Enter Current Password (Blank to back): ");
        return ScannerUtility.SCANNER.nextLine();
    }
}