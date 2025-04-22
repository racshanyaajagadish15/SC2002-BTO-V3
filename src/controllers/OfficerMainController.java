package controllers;

import models.HDBOfficer;
import views.OfficerMainView;

public class OfficerMainController {

	public void officerSelectMenu(HDBOfficer officer) {
		int option=0;
		OfficerMainView officerMainView = new OfficerMainView();
		OfficerApplicationController officerApplicationController = new OfficerApplicationController();
		ApplicantEnquiryController applicantEnquiryController = new ApplicantEnquiryController();
		OfficerEnquiryController officerEnquiryController = new OfficerEnquiryController();
		OfficerRegistrationController officerJoinProjectController = new OfficerRegistrationController();
		// Load enquiries and projects that the applicant have TODO

		while (true){
			option = officerMainView.showOfficerMenu();
			switch (option) {
				case 1:
					officerApplicationController.applicationAction(officer);
					break;
				case 2:
					officerApplicationController.projectAction(officer);
					break;
				case 3:
					applicantEnquiryController.enquiryActionMenu(officer);
					break;
				case 4:
					officerJoinProjectController.showRegistrations(officer);
					break;
				case 5:
					officerJoinProjectController.joinProjectAction(officer);
					break;
				case 6:
					officerEnquiryController.enquiryActionMenu(officer);
					break;
				case 7:
					//Change password
					break;
				case 8:
					// return back to login
					return;
				default:
					System.out.println("Invalid selection. Please try again.");
					break;
			
			}
		}
	}

}