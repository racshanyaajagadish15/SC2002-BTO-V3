package controllers;

import java.util.ArrayList;
import models.Enquiry;

/**
 * This interface defines the methods for handling the enquiry process for managers.
 * It provides methods to manage enquiries, including replying to enquiries and retrieving all project enquiries.
 */
public interface IManagerEnquiryController {

	void replyToEnquiry(Enquiry enquiry, String message);
	ArrayList<Enquiry> getAllProjectEnquiries();
}