package controllers;

import java.io.IOException;
import java.util.ArrayList;

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
	 * @param nric
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	ArrayList<Enquiry> getEnquiriesByNric(String nric) throws NumberFormatException, IOException;

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