package controllers;
import java.util.ArrayList;
import models.Enquiry;
import models.Project;

public interface IManagerEnquiryController {

	/**
	 * 
	 * @param enquiry
	 * @param message
	 */
	void replyToEnquiry(Enquiry enquiry, String message);

	ArrayList<Enquiry> getAllProjectEnquiries();
}