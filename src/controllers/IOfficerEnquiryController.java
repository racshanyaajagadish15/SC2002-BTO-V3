package controllers;

import java.util.ArrayList;

import models.Enquiry;
import models.Project;

public interface IOfficerEnquiryController {

	/**
	 * 
	 * @param enquiryID
	 * @param msg
	 */
	void replyEnquiry(Enquiry enquiry, String message);

	/**
	 * 
	 * @param project
	 * @param enquiry
	 */
	 ArrayList<Enquiry> getProjectEnquiries(Project project);

}