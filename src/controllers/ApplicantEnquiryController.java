package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
		try {
			// Get all enquiries the user has made
			ArrayList<Enquiry> enquiries = Enquiry.getEnquiriesByNricDB(applicant.getNric());
			
			// Classify enquiries into projects
			ArrayList<Project> projects = new ArrayList<>();
			// Mapping of Project -> Enquiries ArrayList 
			Map<Project, ArrayList<Enquiry>> projectEnquiriesMap = new HashMap<Project, ArrayList<Enquiry>>(); 
			for (Enquiry enquiry : enquiries){
				projectEnquiriesMap
			}
			// Show and get enquiry to modify
			Enquiry enquiry = applicantEnquiryView.showEnquiries(enquiries);
			
		}
		catch (IOException e){

		}
		catch (NumberFormatException e){
			
		}		

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