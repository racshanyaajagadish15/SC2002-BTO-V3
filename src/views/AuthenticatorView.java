package views;

import utilities.ScannerUtility;

public class AuthenticatorView {

    public void showLoginBanner() {
        System.out.println("=========================================");
        System.out.println("               Welcome to           ");
        System.out.println("   	   BTO Management System          ");
        System.out.println("=========================================");
        System.out.println("============== Login ==============");
    }	

    public void showFailedLogin(String msg) {
        System.out.println("===================================");
        System.out.println("Login Failed: " + msg);
        System.out.println("===================================");
    }

    public void showSuccessfulLogin() {
        System.out.println("===================================");
        System.out.println("Login Successful. Welcome!");
        System.out.println("===================================");
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