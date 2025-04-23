package controllers;

import models.Applicant;
import views.ApplicantMainView;

public class ApplicantMainController {
    private final ApplicantMainView mainView;
    private final ApplicantApplicationController applicationController;
    private final ApplicantEnquiryController enquiryController;
	private final AuthenticatorController authenticatorController;

    public ApplicantMainController() {
        this.mainView = new ApplicantMainView();
        this.applicationController = new ApplicantApplicationController();
        this.enquiryController = new ApplicantEnquiryController();
		this.authenticatorController = new AuthenticatorController();
    }

    public void applicantSelectMenu(Applicant applicant) {
        int option = 0;
        while (true) {
            option = mainView.showApplicantMenu();
            switch (option) {
                case 1:
                    applicationController.applicationAction(applicant);
                    break;
                case 2:
                    applicationController.projectAction(applicant);
                    break;
                case 3:
                    enquiryController.enquiryActionMenu(applicant);
                    break;
                case 4:
                    authenticatorController.handlePasswordChange(applicant);
                    break;
                case 5:
                    return;
                default:
                    mainView.displayError("Invalid selection. Please try again.");
                    break;
            }
        }
    }
}