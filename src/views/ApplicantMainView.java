package views;

import utilities.ScannerUtility;

public class ApplicantMainView {

	public void showApplicantMenu() {
		System.out.println("==============Applicant=============");
		System.out.println("1. View Application Details");
		System.out.println("2. View projects");
		System.out.println("3. Manage Enquiries");
		System.out.println("4. Logout");
	}
	public int getApplicantAction() {
		System.out.print("Option (1-4): ");
		return ScannerUtility.SCANNER.nextInt();
	}
	

}