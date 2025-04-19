package controllers;

import models.Applicant;
import views.ApplicantMainView;


public class ApplicantMainController {

	public void applicantSelectMenu(Applicant applicant) {
		int option=0;
		ApplicantMainView applicantMainView = new ApplicantMainView();
		ApplicantApplicationController applicantApplicationController = new ApplicantApplicationController();
		ApplicantEnquiryController applicantEnquiryController = new ApplicantEnquiryController();
		// Load enquiries and projects that the applicant have TODO

		while (true){
			option = applicantMainView.showApplicantMenu();
			switch (option) {
				case 1:
					applicantApplicationController.applicationAction(applicant);
					break;
				case 2:
					applicantApplicationController.projectAction(applicant);
					break;
				case 3:
					applicantEnquiryController.enquiryActionMenu(applicant);
					break;
				case 4:
					// return back to login
					return;
				default:
					applicantMainView.showError("Invalid selection. Please try again.");
					break;
			
			}
		}
	}

}