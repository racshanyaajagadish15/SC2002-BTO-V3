package views;

import utilities.ScannerUtility;

public class OfficerUpdateView implements IDisplayResult {

    public int showApplicationsToUpdate() {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("         MANAGE PROJECT BOOKINGS         ");
            System.out.println("=========================================");

            System.out.print("\nEnter option: ");
			try {
				System.out.print("Enter Application ID: ");
				int applicationId = ScannerUtility.SCANNER.nextInt();
				ScannerUtility.SCANNER.nextLine();
	
				boolean success = controller.generateReceipt(applicationId);
				if (success) {
					displaySuccess("Booking receipt generated successfully.");
				}
			} catch (Exception e) {
				displayError("Invalid Application ID format.");
				ScannerUtility.SCANNER.nextLine();
			}
            try {
                int option = ScannerUtility.SCANNER.nextInt();
                ScannerUtility.SCANNER.nextLine();
				if (option == 1 || option == 0){
					return option;
				}
				displayError("Invalid input. Please try again.");
            } catch (Exception e) {
                displayError("Invalid input. Please try again.");
                ScannerUtility.SCANNER.nextLine();
            }
        }
    }



    private void showUpdateApplicationMenu() {
        // TODO: Implement update application status menu
        System.out.println("Feature coming soon...");
    }
}