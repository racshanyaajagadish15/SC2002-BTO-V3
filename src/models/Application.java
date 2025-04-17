package models;

public class Application {

	private Applicant applicant;
	private Project project;
	private String applicationStatus;
	private int applicationID;

	/**
	 * 
	 * @param applicant
	 * @param project
	 * @param applicationStatus
	 * @param applicationID
	 */
	public Application(Applicant applicant, Project project, String applicationStatus, int applicationID) {
		this.applicant = applicant;
		this.project = project;
		this.applicationStatus = applicationStatus;
		this.applicationID = applicationID;
	}

	public static void createEnquiryDB() {
		// TODO - implement Application.createEnquiryDB
		
	}

	/**
	 * 
	 * @param applicant
	 * @param project
	 * @param applicationStatus
	 * @param applicationID
	 */
	public static void updateApplicationDB(Applicant applicant, Project project, String applicationStatus, int applicationID) {
		// TODO - implement Application.updateApplicationDB
		
	}

	/**
	 * 
	 * @param applicationID
	 */
	public static void deleteApplicationDB(int applicationID) {
		// TODO - implement Application.deleteApplicationDB
		
	}

	public static void createApplication(Applicant applicant, Project project, String applicationStatus) {
		// TODO - implement Application.createApplicationDB
		
	}

	public Applicant getApplicant() {
		return this.applicant;
	}

	public Project getProject() {
		return this.project;
	}

	public String getApplicationStatus() {
		return this.applicationStatus;
	}

	public int getApplicationID() {
		return this.applicationID;
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
	 * @param applicationStatus
	 */
	public void setApplicationStatus(String applicationStatus) {
		this.applicationStatus = applicationStatus;
	}

	/**
	 * 
	 * @param applicationID
	 */
	public void setApplicationID(int applicationID) {
		this.applicationID = applicationID;
	}

}