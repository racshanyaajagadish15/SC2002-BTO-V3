package controllers;

public interface IOfficerEnquiryController {

	/**
	 * 
	 * @param enquiryID
	 * @param msg
	 */
	void replyEnquiry(int enquiryID, String msg);

	/**
	 * 
	 * @param project
	 * @param enquiry
	 */
	void getProjectEnquiries(Project project, int enquiry);

}