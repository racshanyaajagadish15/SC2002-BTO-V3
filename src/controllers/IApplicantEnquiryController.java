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
	 */
	boolean editEnquiry(Enquiry enquiry);

	/**
	 * 
	 * @param id
	 * @return 
	 */
	boolean deleteEnquiry(int id);

}