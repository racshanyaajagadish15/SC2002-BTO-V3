package controllers;

import java.util.ArrayList;

import models.Enquiry;
import models.Project;

public interface IApplicantEnquiryController {

	/**
	 * 
	 * @param project
	 * @param enquiry
	 */
	void submitEnquiry(Project project, String enquiry);

	/**
	 * 
	 * @param nric
	 */
	ArrayList<Enquiry> getEnquiries(String nric);

	/**
	 * 
	 * @param id
	 * @param newEnquiry
	 */
	void editEnquiry(int id, String newEnquiry);

	/**
	 * 
	 * @param id
	 */
	void deleteEnquiry(int id);

}