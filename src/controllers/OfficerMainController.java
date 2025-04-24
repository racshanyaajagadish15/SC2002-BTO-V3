package controllers;

import models.HDBOfficer;
import views.OfficerMainView;

public class OfficerMainController {
	private final OfficerMainView mainView;
	private final OfficerApplicationController officerApplicationController;
	private final ApplicantEnquiryController applicantEnquiryController;
	private final OfficerEnquiryController officerEnquiryController;
	private final OfficerRegistrationController officerRegistrationController;
	private final OfficerBookingController officerBookingController;
	private final AuthenticatorController authenticatorController;


	public OfficerMainController() {
		this.mainView = new OfficerMainView();
		this.officerApplicationController = new OfficerApplicationController();
		this.applicantEnquiryController = new ApplicantEnquiryController();
		this.officerEnquiryController = new OfficerEnquiryController();
		this.officerRegistrationController = new OfficerRegistrationController();
		this.officerBookingController = new OfficerBookingController();
		this.authenticatorController = new AuthenticatorController();
	}
	public void officerSelectMenu(HDBOfficer officer) {
		int option=0;
	
		while (true){
			option = mainView.showOfficerMenu();
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
					officerRegistrationController.showRegistrations(officer);
					break;
				case 5:
					officerRegistrationController.joinProjectAction(officer);
					break;
				case 6:
					officerEnquiryController.enquiryActionMenu(officer);
					break;
				case 7:
					officerBookingController.selectApplicationToBook(officer);
					break;
				case 8:
					officerBookingController.viewGenerateReceipt(officer);
					break;
				case 9:
					authenticatorController.handlePasswordChange(officer);
					break;
				case 10:
					return;
				default:
					break;
			
			}
		}
	}

}