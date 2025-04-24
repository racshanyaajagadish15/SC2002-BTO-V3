package controllers;

import models.Applicant;
import views.ApplicantMainView;



/**
 * This class is responsible for handling the main menu actions for applicants.
 * It provides methods to manage the applicant's application, project, and enquiry actions.
 */
public class ApplicantMainController {
    /**
     * The view used for displaying information to the user.
     * The ApplicantApplicationController is responsible for handling the application process.
     * The ApplicantEnquiryController is responsible for handling the enquiry process.
     * The AuthenticatorController is responsible for handling authentication-related actions.
     */
    private final ApplicantMainView mainView;
    private final ApplicantApplicationController applicationController;
    private final ApplicantEnquiryController applicantEnquiryController;
	private final AuthenticatorController authenticatorController;

    /**
     * Constructor for the ApplicantMainController class.
     * Initializes the view and controllers used for managing the applicant's actions.
     */
    public ApplicantMainController() {
        this.mainView = new ApplicantMainView();
        this.applicationController = new ApplicantApplicationController();
        this.applicantEnquiryController = new ApplicantEnquiryController();
		this.authenticatorController = new AuthenticatorController();
    }

    /**
     * This method displays the main menu for the applicant and handles their selections.
     * It provides options for managing applications, projects, enquiries, and password changes.
     * 
     * @param applicant The applicant for whom to display the main menu.
     */
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
                    applicantEnquiryController.enquiryActionMenu(applicant);
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