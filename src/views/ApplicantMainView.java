package views;

import java.util.InputMismatchException;

import utilities.ScannerUtility;

public class ApplicantMainView {

	public int showApplicantMenu() {

		int option;
		while (true){
			System.out.println("\n=========================================");
			System.out.println("           APPLICANT DASHBOARD           ");
			System.out.println("=========================================");
			System.out.println("1. View Application Details");
			System.out.println("2. View projects");
			System.out.println("3. Manage Enquiries");
			System.out.println("4. Change Password");
			System.out.println("4. Logout");
			try {
				System.out.print("Please enter your choice: ");
				option = ScannerUtility.SCANNER.nextInt();
				ScannerUtility.SCANNER.nextLine();
				return option;
			} catch (InputMismatchException e){
				showError("Invalid selection. Please try again.");
				ScannerUtility.SCANNER.nextLine();
				continue;
			}
		}
	}

	public void showError(String error) {
		System.out.println(error);
	}

}