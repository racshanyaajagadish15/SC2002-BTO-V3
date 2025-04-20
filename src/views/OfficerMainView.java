package views;

import java.util.InputMismatchException;

import utilities.ScannerUtility;

public class OfficerMainView {

	public int showOfficerMenu() {
		int option;
		while (true){
			System.out.println("\n=========================================");
			System.out.println("            OFFICER DASHBOARD            ");
			System.out.println("=========================================");
			System.out.println("1. View Application Details");
			System.out.println("2. View projects");
			System.out.println("3. Manage Your Enquiries");
			System.out.println("4. Manage Project Enquiries");
			System.out.println("5. Register For Project As Officer");
			System.out.println("5. Change Password");
			System.out.println("5. Logout");
			try {
				System.out.print("Please enter your choice: ");
				option = ScannerUtility.SCANNER.nextInt();
				ScannerUtility.SCANNER.nextLine();
				return option;
			} catch (InputMismatchException e){
				System.out.println("Invalid selection. Please try again.");
				ScannerUtility.SCANNER.nextLine();
				continue;
			}
		}
		
	}

}