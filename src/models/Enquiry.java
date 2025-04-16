package models;

public class Enquiry {

	private int enquiryID;
	private Applicant applicant;
	private Project project;
	private String enquiry;
	private String reply;

	/**
	 * 
	 * @param applicant
	 * @param project
	 * @param enquiryID
	 * @param enquiry
	 * @param reply
	 */
	public Enquiry(Applicant applicant, Project project, int enquiryID, String enquiry, String reply) {
		// TODO - implement Enquiry.Enquiry
		
	}

	public static void createEnquiryDB() {
		// TODO - implement Enquiry.createEnquiryDB
		
	}

	public static void updateEnquiryDB() {
		// TODO - implement Enquiry.updateEnquiryDB
		
	}

	public static void deleteEnquiryDB() {
		// TODO - implement Enquiry.deleteEnquiryDB
		
	}

	public static void getEnquiryDB() {
		// TODO - implement Enquiry.getEnquiryDB
		
	}

	public int getEnquiryID() {
		return this.enquiryID;
	}

	public Applicant getApplicant() {
		return this.applicant;
	}

	public Project getProject() {
		return this.project;
	}

	public String getEnquiry() {
		return this.enquiry;
	}

	public String getReply() {
		return this.reply;
	}

	/**
	 * 
	 * @param enquiryID
	 */
	public void setEnquiryID(int enquiryID) {
		this.enquiryID = enquiryID;
	}

	/**
	 * 
	 * @param applicant
	 */
	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	/**
	 * 
	 * @param project
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * 
	 * @param enquiry
	 */
	public void setEnquiry(String enquiry) {
		this.enquiry = enquiry;
	}

	/**
	 * 
	 * @param reply
	 */
	public void setReply(String reply) {
		this.reply = reply;
	}

}