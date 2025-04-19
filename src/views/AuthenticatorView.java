package views;

import utilities.ScannerUtility;

public class AuthenticatorView {

    public void showLoginBanner() {
        System.out.println("\n=========================================");
        System.out.println("                Welcome to               ");
        System.out.println("          BTO Management System          ");
        System.out.println("=========================================");
        System.out.println("                  Login                  ");
		System.out.println("=========================================");

    }	

    public void showFailedLogin(String msg) {
        System.out.println("Login Failed: " + msg);
    }

    public void showSuccessfulLogin() {
        System.out.println("Login Successful. Welcome!");
    }
    
    public String getNric() {
        System.out.print("Enter your NRIC: ");
        return ScannerUtility.SCANNER.nextLine();
    }

    public String getPassword() {
        System.out.print("Enter your password: ");
        return ScannerUtility.SCANNER.nextLine();
    } 
}