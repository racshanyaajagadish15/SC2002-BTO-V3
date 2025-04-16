public interface IManagerEnquiryController {

	/**
	 * 
	 * @param enquiry
	 * @param message
	 */
	void replyToEnquiry(Enquiry enquiry, String message);

	ArrayList<Enquiry> getAllProjectEnquiries();

	/**
	 * 
	 * @param project
	 */
	ArrayList<Enquiry> getProjectEnquiries(Project project);

}