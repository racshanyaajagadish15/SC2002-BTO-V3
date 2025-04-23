package controllers;

import models.Applicant;
import models.Enquiry;

public interface IApplicantEnquiryController {

	/**
	 *
	 * @param applicant
	 */
	public void enquiryActionMenu(Applicant applicant);

	/**
	 * 
	 * @param enquiry
	 */
	boolean submitEnquiry(Enquiry enquiry) ;

	/**
	 * 
	 * @param enquiry
	 * @param newText
	 */
	void editEnquiry(Enquiry enquiry, String newText);

	/**
	 * 
	 * @param id
	 * @return 
	 */
	boolean deleteEnquiry(int id);

}