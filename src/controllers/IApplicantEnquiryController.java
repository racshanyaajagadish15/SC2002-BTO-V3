package controllers;

import models.Applicant;
import models.Enquiry;
import models.Project;

/**
 * This interface defines the methods for handling the enquiry process for applicants.
 * It provides methods to manage enquiries, including creating, editing, and deleting enquiries.
 */
public interface IApplicantEnquiryController {

	public void enquiryActionMenu(Applicant applicant);
	boolean submitEnquiry(Enquiry enquiry) ;
	void editEnquiry(Enquiry enquiry, String newText);
	boolean deleteEnquiry(int id);
	void createEnquiryFlow(Project project, Applicant applicant);

}