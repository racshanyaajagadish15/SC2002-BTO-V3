package controllers;

import java.util.ArrayList;

import models.Applicant;
import models.Enquiry;
import models.Project;
import views.ApplicantEnquiryView;

public class ApplicantEnquiryController implements IApplicantEnquiryController {


	/**
	 * 
	 * @param project
	 * @param enquiry
	 */
	public void enquiryActionMenu(Applicant applicant) {
		ApplicantEnquiryView applicantEnquiryView = new ApplicantEnquiryView();
		Enquiry.getEnquiryByApplicantDB(applicant);
		applicantEnquiryView.showEnquiries();
		
	}

	/**
	 * 
	 * @param project
	 * @param enquiry
	 */
	public void submitEnquiry(Project project, String enquiry) {
		// TODO - implement ApplicantEnquiryController.submitEnquiry

	}

	/**
	 * 
	 * @param nric
	 */
	public ArrayList<Enquiry> getEnquiries(String nric) {
		// TODO - implement ApplicantEnquiryController.getEnquiries
		return null;
	}

	/**
	 * 
	 * @param id
	 * @param newEnquiry
	 */
	public void editEnquiry(int id, String newEnquiry) {
		// TODO - implement ApplicantEnquiryController.editEnquiry

	}

	/**
	 * 
	 * @param id
	 */
	public void deleteEnquiry(int id) {
		// TODO - implement ApplicantEnquiryController.deleteEnquiry

	}

}