package views;

import utilities.ScannerUtility;

public class AuthenticatorView {

	public void showLoginBanner() {
		System.out.println("================Login===============");
	}	

	public void showFailedLogin(String msg){
		System.out.println(msg);
	}

	public void showSuccessfulLogin(){
		System.out.println("Login Successful.");
	}
	
	public String getNric(){
		System.out.print("Enter your NRIC: ");
		return ScannerUtility.SCANNER.nextLine();
	}

	public String getPassword(){
		System.out.print("Enter your password: ");
		return ScannerUtility.SCANNER.nextLine();
	} 
}

