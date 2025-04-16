package controllers;

public interface IEnquiryAdminController {

	/**
	 * 
	 * @param enquiry
	 * @param message
	 */
	void replyToEnquiry(Enquiry enquiry, String message);

	List<Enquiry> getAllProjectEnquiries();

	/**
	 * 
	 * @param project
	 */
	List<Enquiry> getProjectEnquiries(Project project);

}